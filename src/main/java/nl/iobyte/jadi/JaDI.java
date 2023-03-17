package nl.iobyte.jadi;

import java.time.Duration;
import java.util.Iterator;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.TimeUnit;
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
    private final HierarchyMap hierarchyMap = new HierarchyMap();
    private final Map<Type<?>, CompletableFuture<?>> futureMap = new ConcurrentHashMap<>();

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

        hierarchyMap.put(type, value);
        CompletableFuture<T> future = (CompletableFuture<T>) futureMap.remove(type);

        processQueue();
        if(future != null)
            ForkJoinPool.commonPool().execute(() -> future.complete(value));
    }

    /**
     * Resolve type value
     *
     * @param type type
     * @param <T>  type
     * @return value
     */
    public synchronized <T> CompletableFuture<T> resolve(Type<T> type) {
        assert type != null;

        if(hierarchyMap.containsKey(type))
            return CompletableFuture.completedFuture((T) hierarchyMap.get(type));

        T instance = instantiate(type);
        if(instance == null)
            return (CompletableFuture<T>) futureMap.computeIfAbsent(type, key -> new CompletableFuture<>());

        CompletableFuture<T> future = Optional.ofNullable(
            (CompletableFuture<T>) futureMap.remove(type)
        ).map(f -> {
            ForkJoinPool.commonPool().execute(() -> f.complete(instance));
            return f;
        }).orElseGet(() -> CompletableFuture.completedFuture(instance));

        processQueue();

        // Complete future
        return future;
    }

    /**
     * Attempt to resolve type within duration else return null
     *
     * @param type     type
     * @param duration timeout
     * @param <T>      type
     * @return type instance or null
     */
    public <T> T resolve(Type<T> type, Duration duration) {
        try {
            return resolve(type).get(
                duration.toMillis(),
                TimeUnit.MILLISECONDS
            );
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * Instantiate type
     *
     * @param type type to instantiate
     * @param <T>  type
     * @return type instance
     */
    private <T> T instantiate(Type<T> type) {
        if(!type.isInstantiable())
            return null;

        if(hierarchyMap.containsKey(type))
            return (T) hierarchyMap.get(type);

        TypeFactory<T> factory = TypeFactory.of(type);
        boolean b = factory.getDependencies().stream().allMatch(hierarchyMap::containsKey);
        if(!b)
            return null;

        // Get instance
        T instance = factory.create(hierarchyMap::get);
        if(instance != null) {
            hierarchyMap.put(type, instance);
        } else {
            return null;
        }

        // Process annotations
        process(new ProcessContext(
            instance,
            type,
            hierarchyMap::get
        ));
        return instance;
    }

    /**
     * Attempt to instantiate types in queue
     */
    private void processQueue() {
        Iterator<Type<?>> it = futureMap.keySet().iterator();
        while(it.hasNext()) {
            // Try to resolve type
            Type<?> type = it.next();
            Object instance = instantiate(type);
            if(instance == null)
                continue;

            CompletableFuture<Object> future = (CompletableFuture<Object>) futureMap.get(type);
            it.remove();

            if(future != null)
                future.complete(instance);

            // Restart iterator
            it = futureMap.keySet().iterator();
        }
    }

}
