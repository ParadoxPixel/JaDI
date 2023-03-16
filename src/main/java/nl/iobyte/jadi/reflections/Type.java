package nl.iobyte.jadi.reflections;

import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import net.jodah.expiringmap.ExpiringMap;
import org.apache.commons.lang3.ClassUtils;

@Getter
@ToString
@EqualsAndHashCode(callSuper = true)
public class Type<T> extends TypeElement<Class<T>> {

    // Cache
    private static final Map<Class<?>, Type<?>> typeMap = ExpiringMap.builder()
        .expiration(5, TimeUnit.MINUTES)
        .build();

    // Local
    private final Class<T> type;
    private final Class<?> originalType;

    private Type(Class<T> type, Class<?> originalType) {
        super(type);
        this.type = type;
        this.originalType = originalType;
    }

    /**
     * Get optional type of super class
     *
     * @return optional type
     */
    public Optional<Type<?>> getSuperType() {
        return Optional.ofNullable(type.getSuperclass()).map(Type::of);
    }

    /**
     * Get list of all directly implemented interface types
     *
     * @return list of interface types
     */
    public List<Type<?>> getInterfaceTypes() {
        return Arrays.stream(type.getInterfaces())
            .map(Type::of)
            .collect(Collectors.toList());
    }

    /**
     * Get all super types
     *
     * @return list of super types
     */
    public List<Type<?>> getAssignableTypes() {
        List<Type<?>> list = getInterfaceTypes();
        getSuperType().ifPresent(list::add);
        return list;
    }

    /**
     * Check if type can be instantiated
     *
     * @return whether the type can be instantiated
     */
    public boolean isInstantiable() {
        if(type.isInterface() || Modifier.isAbstract(type.getModifiers()))
            return false;

        if(ClassUtils.isPrimitiveOrWrapper(type))
            return false;

        return !type.getPackageName().startsWith("java.lang");
    }

    /**
     * Check if type is assignable to this type
     *
     * @param type type to check
     * @return whether the provided type is assignable to this type
     */
    public boolean isAssignable(Type<?> type) {
        assert type != null;

        return ClassUtils.isAssignable(type.type, this.type);
    }

    /**
     * Check whether an object is assignable to this type
     *
     * @param obj object to check for
     * @return whether it can be assigned to this type
     */
    public boolean noInstance(Object obj) {
        return !ClassUtils.isAssignable(ClassUtils.toClass(obj), originalType);
    }

    /**
     * Get hierarchy depth until type
     *
     * @param type parent type
     * @return depth
     */
    public int getHierarchyDepth(Type<?> type) {
        if(!type.isAssignable(this))
            return -1;

        if(this.type.equals(type.type))
            return 0;

        return getAssignableTypes().stream()
            .map(t -> t.getHierarchyDepth(type) + 1)
            .filter(i -> i > 0)
            .min(Integer::compareTo)
            .orElse(-1);
    }

    /**
     * Get type fields that satisfy filters
     *
     * @param filters array of predicates to filter fields
     * @return list of type fields
     */
    @SafeVarargs
    public final List<TypeField> getFields(Predicate<TypeField>... filters) {
        Stream<TypeField> stream = Arrays.stream(type.getDeclaredFields())
            .map(field -> new TypeField(
                field,
                this
            ));

        if(filters == null)
            return stream.collect(Collectors.toList());

        for(Predicate<TypeField> filter : filters)
            stream = stream.filter(filter);

        return stream.collect(Collectors.toList());
    }

    /**
     * Get type constructors that satisfy filters
     *
     * @param filters array of predicates to filter constructors
     * @return list of type constructors
     */
    @SafeVarargs
    public final List<TypeConstructor<T>> getConstructors(Predicate<TypeConstructor<T>>... filters) {
        //noinspection unchecked
        Stream<TypeConstructor<T>> stream = Arrays.stream(type.getDeclaredConstructors())
            .map(constructor -> (Constructor<T>) constructor)
            .map(constructor -> new TypeConstructor<>(
                constructor,
                this
            ));

        if(filters == null)
            return stream.collect(Collectors.toList());

        for(Predicate<TypeConstructor<T>> filter : filters)
            stream = stream.filter(filter);

        return stream.collect(Collectors.toList());
    }

    /**
     * Get type methods that satisfy filters
     *
     * @param filters array of predicates to filter methods
     * @return list of type methods
     */
    @SafeVarargs
    public final List<TypeMethod> getMethods(Predicate<TypeMethod>... filters) {
        Stream<TypeMethod> stream = Arrays.stream(type.getDeclaredMethods())
            .map(field -> new TypeMethod(
                field,
                this
            ));

        if(filters == null)
            return stream.collect(Collectors.toList());

        for(Predicate<TypeMethod> filter : filters)
            stream = stream.filter(filter);

        return stream.collect(Collectors.toList());
    }

    /**
     * Get wrapped type
     *
     * @param originalType type to wrap
     * @param <T>          type
     * @return wrapped type
     */
    public static <T> Type<T> of(Class<T> originalType) {
        assert originalType != null;

        //noinspection unchecked
        return (Type<T>) typeMap.computeIfAbsent(
            originalType,
            key -> new Type<>(
                (Class<T>) ClassUtils.primitiveToWrapper(key),
                key
            )
        );
    }

}
