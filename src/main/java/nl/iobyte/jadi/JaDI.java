package nl.iobyte.jadi;

import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import lombok.Getter;
import nl.iobyte.jadi.objects.ApplicationContext;
import nl.iobyte.jadi.objects.HierarchyMap;
import nl.iobyte.jadi.processor.AnnotationProcessor;
import nl.iobyte.jadi.reflections.Type;
import nl.iobyte.jadi.reflections.TypeFactory;

@Getter
@SuppressWarnings({"unused", "unchecked"})
public class JaDI {

    private final HierarchyMap hierarchyMap = new HierarchyMap();
    private final AnnotationProcessor annotationProcessor = new AnnotationProcessor();
    private final Map<Type<?>, CompletableFuture<?>> futureMap = new ConcurrentHashMap<>();

    /**
     * Bind value to type
     *
     * @param type  type
     * @param value value
     * @param <T>   type
     */
    public <T> void bind(Type<T> type, T value) {
        assert type != null;
        assert value != null;

        hierarchyMap.put(type, value);
        CompletableFuture<T> future = (CompletableFuture<T>) futureMap.remove(type);
        if(future != null)
            future.complete(value);

        processQueue();
    }

    /**
     * Resolve type value
     *
     * @param type type
     * @param <T>  type
     * @return value
     */
    public <T> CompletableFuture<T> resolve(Type<T> type) {
        assert type != null;

        if(hierarchyMap.containsKey(type))
            return CompletableFuture.completedFuture((T) hierarchyMap.get(type));

        T instance = instantiate(type);
        if(instance == null)
            return (CompletableFuture<T>) futureMap.computeIfAbsent(type, key -> new CompletableFuture<>());

        CompletableFuture<T> future = (CompletableFuture<T>) futureMap.remove(type);
        if(future == null)
            future = new CompletableFuture<>();

        processQueue();
        future.complete(instance);
        return future;
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
        annotationProcessor.process(new ApplicationContext(
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
