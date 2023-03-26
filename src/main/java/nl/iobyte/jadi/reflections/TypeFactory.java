package nl.iobyte.jadi.reflections;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import nl.iobyte.jadi.annotations.Assisted;
import nl.iobyte.jadi.annotations.AssistedInject;
import nl.iobyte.jadi.annotations.Inject;
import nl.iobyte.jadi.annotations.PostConstruct;
import nl.iobyte.jadi.interfaces.TypeResolver;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class TypeFactory<T> {

    private final Type<T> type;
    private final TypeConstructor<T> constructor;
    private final List<TypeField> fields;
    private final List<TypeMethod> methods;

    /**
     * Get type factory's dependencies
     *
     * @return list of types
     */
    public List<Type<?>> getDependencies() {
        return Stream.of(
                constructor.getParameters()
                    .stream()
                    .filter(p -> !p.hasAnnotation(AssistedInject.class))
                    .map(TypeParameter::getType),
                fields.stream()
                    .filter(f -> !f.hasAnnotation(AssistedInject.class))
                    .map(TypeField::getValueType),
                methods.stream()
                    .flatMap(
                        method -> method.getParameterTypes()
                            .stream()
                            .filter(p -> !p.hasAnnotation(AssistedInject.class))
                    )
            ).flatMap(s -> s)
            .collect(Collectors.toList());
    }

    /**
     * Get all types of assisted dependencies.
     *
     * @return list of types
     */
    public List<Type<?>> getAssistedDependencies() {
        return Stream.of(
                constructor.getParameters()
                    .stream()
                    .filter(p -> p.hasAnnotation(AssistedInject.class))
                    .map(TypeParameter::getType),
                fields.stream()
                    .filter(f -> f.hasAnnotation(AssistedInject.class))
                    .map(TypeField::getValueType),
                methods.stream()
                    .flatMap(
                        method -> method.getParameterTypes()
                            .stream()
                            .filter(p -> p.hasAnnotation(AssistedInject.class))
                    )
            ).flatMap(s -> s)
            .collect(Collectors.toList());
    }

    /**
     * Create a new instance of type with type resolves
     *
     * @param typeResolver type -> instance resolver
     * @param values       array of assisted values
     * @return new type instance
     */
    public T create(TypeResolver typeResolver, Object... values) {
        Object[] finalValues = Optional.ofNullable(values).orElse(new Object[0]);
        TypeResolver assistedInject = type -> {
            for(Object value : finalValues)
                if(!type.noInstance(value))
                    return value;

            throw new IllegalStateException("no value found for assisted inject");
        };

        Object[] constructorValues = constructor.getParameters()
            .stream()
            .map(p -> {
                if(!p.hasAnnotation(AssistedInject.class))
                    return typeResolver.apply(p.getType());

                return assistedInject.apply(p.getType());
            })
            .toArray(Object[]::new);

        T instance = constructor.newInstance(constructorValues);
        if(instance == null)
            return null;

        // Inject fields
        fields.forEach(field -> field.set(
            instance,
            (field.hasAnnotation(AssistedInject.class) ? assistedInject : typeResolver).apply(field.getValueType())
        ));

        // Run methods
        methods.forEach(method -> {
            Object[] methodValues = method.getParameterTypes()
                .stream()
                .map(type -> {
                    if(!type.hasAnnotation(AssistedInject.class))
                        return typeResolver.apply(type);

                    return assistedInject.apply(type);
                })
                .toArray(Object[]::new);

            method.invoke(instance, methodValues);
        });

        return instance;
    }

    /**
     * Get type factory of type
     *
     * @param type type
     * @param <T>  type
     * @return type factory
     */
    public static <T> TypeFactory<T> of(Type<T> type) {
        TypeConstructor<T> constructor = getConstructor(type);
        List<TypeField> fields = type.getFields(field -> field.hasAnnotation(Inject.class) || field.hasAnnotation(AssistedInject.class));
        List<TypeMethod> methods = type.getMethods(method -> method.hasAnnotation(PostConstruct.class));

        return new TypeFactory<>(type, constructor, fields, methods);
    }

    /**
     * Get type constructor from type
     *
     * @param type type
     * @param <T>  type
     * @return type constructor
     */
    private static <T> TypeConstructor<T> getConstructor(Type<T> type) {
        List<TypeConstructor<T>> constructors = type.getConstructors(c -> true);
        if(constructors.size() == 1)
            return constructors.get(0);

        if(constructors.size() == 2) {
            if(constructors.stream().anyMatch(c -> c.getParameterCount() == 0))
                return constructors.stream()
                    .filter(c -> c.getParameterCount() != 0)
                    .findAny()
                    .orElseThrow();
        }

        return constructors.stream()
            .filter(c -> c.hasAnnotation(Assisted.class))
            .max(Comparator.comparingInt(o -> o.getConstructor().getParameterCount()))
            .orElseThrow(() -> new IllegalArgumentException(type.getOriginalType() + " needs assisted constructor"));
    }

}
