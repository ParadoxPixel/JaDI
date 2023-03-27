package nl.iobyte.jadi;

import com.spotify.futures.CompletableFutures;
import java.time.Duration;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import lombok.AccessLevel;
import lombok.Getter;
import nl.iobyte.jadi.objects.HierarchyMap;
import nl.iobyte.jadi.processor.AnnotationProcessor;
import nl.iobyte.jadi.processor.objects.ProcessContext;
import nl.iobyte.jadi.reflections.Type;
import nl.iobyte.jadi.reflections.TypeFactory;

@SuppressWarnings({"unused", "unchecked"})
public class JaDI extends AnnotationProcessor {

    @Getter(AccessLevel.PACKAGE) // Used for Testing
    private final HierarchyMap<CompletableFuture<?>> hierarchyMap;
    private final Set<Type<?>> instantiated;

    /**
     * Java Dependency Injector
     */
    public JaDI() {
        this.hierarchyMap = new HierarchyMap<>();
        this.instantiated = new HashSet<>();
        this.bind(Type.of(JaDI.class), this);
    }

    /**
     * Bind value to type
     *
     * @param type  type
     * @param value value
     * @param <T>   type
     */
    public synchronized <T> void bind(Type<T> type, T value) {
        assert type != null;
        assert value != null;

        if(!instantiated.add(type))
            return;

        // Make sure a future of this specific type exists
        hierarchyMap.computeIfAbsent(
            type,
            key -> new CompletableFuture<>()
        );

        ForkJoinPool.commonPool().execute(() -> complete(type, value));
    }

    /**
     * Resolve type value
     *
     * @param type   type
     * @param values assisted values
     * @param <T>    type
     * @return value
     */
    public synchronized <T> CompletableFuture<T> resolve(Type<T> type, Object... values) {
        assert type != null;

        if(hierarchyMap.containsKey(type))
            return (CompletableFuture<T>) hierarchyMap.get(type);

        return instantiate(type, values);
    }

    /**
     * Attempt to resolve type within duration else return null
     *
     * @param duration timeout
     * @param type     type
     * @param values   assisted values
     * @param <T>      type
     * @return type instance or null
     */
    public <T> T resolve(Duration duration, Type<T> type, Object... values) {
        try {
            return resolve(type, values).get(
                duration.toMillis(),
                TimeUnit.MILLISECONDS
            );
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * Get instance supplier for type and values
     *
     * @param type   type
     * @param values assisted values
     * @param <T>    type
     * @return future supplier
     */
    public synchronized <T> CompletableFuture<Supplier<T>> getSupplier(Type<T> type, Object... values) {
        TypeFactory<T> factory = TypeFactory.of(type);
        boolean b = factory.getAssistedDependencies().stream().allMatch(t -> {
            for(Object value : values)
                if(!t.noInstance(value))
                    return true;

            return false;
        });
        if(!b)
            throw new IllegalArgumentException("missing assisted values for factory of type " + type.getOriginalType());

        Map<Type<?>, CompletableFuture<?>> map = factory.getDependencies()
            .stream()
            .collect(Collectors.toMap(
                t -> t,
                t -> hierarchyMap.computeIfAbsent(
                    t,
                    key -> new CompletableFuture<>()
                )
            ));

        return CompletableFutures.allAsMap(map)
            .thenApply(m -> () -> {
                T obj = factory.create(m::get, values);
                process(new ProcessContext(
                    obj,
                    type,
                    this
                ));
                return obj;
            });
    }

    /**
     * Instantiate type
     *
     * @param type   type to instantiate
     * @param values assisted values
     * @param <T>    type
     * @return type instance
     */
    private <T> CompletableFuture<T> instantiate(Type<T> type, Object... values) {
        CompletableFuture<T> future = (CompletableFuture<T>) hierarchyMap.computeIfAbsent(
            type,
            key -> new CompletableFuture<>()
        );

        if(!type.isInstantiable() || !instantiated.add(type))
            return future;

        getSupplier(type, values).thenAccept(s -> complete(type, s.get()));
        return future;
    }

    /**
     * Complete instance for type
     *
     * @param type type
     * @param obj  instance of type
     * @param <T>  type
     */
    private <T> void complete(Type<T> type, T obj) {
        ForkJoinPool.commonPool().execute(
            () -> hierarchyMap.getAssignableValues(type).forEach(f -> {
                if(f.isDone())
                    return;

                ((CompletableFuture<T>) f).complete(obj);
            })
        );
    }

}
