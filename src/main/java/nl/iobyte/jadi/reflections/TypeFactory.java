package nl.iobyte.jadi.reflections;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
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
                constructor.getParameterTypes().stream(),
                fields.stream().map(TypeField::getValueType),
                methods.stream().flatMap(method -> method.getParameterTypes().stream())
            ).flatMap(s -> s)
            .collect(Collectors.toList());
    }

    /**
     * Create a new instance of type with type resolves
     *
     * @param typeResolver type -> instance resolver
     * @return new type instance
     */
    public T create(TypeResolver typeResolver) {
        Object[] constructorValues = constructor.getParameterTypes()
            .stream()
            .map(typeResolver)
            .toArray(Object[]::new);

        T instance = constructor.newInstance(constructorValues);
        if(instance == null)
            return null;

        // Inject fields
        fields.forEach(field -> field.set(
            instance,
            typeResolver.apply(field.getValueType())
        ));

        // Run methods
        methods.forEach(method -> {
            Object[] methodValues = method.getParameterTypes()
                .stream()
                .map(typeResolver)
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
        TypeConstructor<T> constructor = type.getConstructors(c -> true)
            .stream()
            .max(Comparator.comparingInt(o -> o.getConstructor().getParameterCount()))
            .orElseThrow();

        List<TypeField> fields = type.getFields(field -> field.hasAnnotation(Inject.class));
        List<TypeMethod> methods = type.getMethods(method -> method.hasAnnotation(PostConstruct.class));

        return new TypeFactory<>(type, constructor, fields, methods);
    }

}
