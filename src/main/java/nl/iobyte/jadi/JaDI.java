package nl.iobyte.jadi;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import lombok.Getter;
import nl.iobyte.jadi.objects.ApplicationContext;
import nl.iobyte.jadi.objects.HierarchyMap;
import nl.iobyte.jadi.processor.AnnotationProcessor;
import nl.iobyte.jadi.reflections.Type;
import nl.iobyte.jadi.reflections.TypeFactory;

@Getter
@SuppressWarnings("unused")
public class JaDI {

    private final HierarchyMap hierarchyMap = new HierarchyMap();
    private final AnnotationProcessor annotationProcessor = new AnnotationProcessor();
    private final List<Type<?>> queue = new ArrayList<>();

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
    }

    /**
     * Resolve type value
     *
     * @param type type
     * @param <T>  type
     * @return value
     */
    public <T> T resolve(Type<T> type) {
        assert type != null;

        if(hierarchyMap.containsKey(type))
            //noinspection unchecked
            return (T) hierarchyMap.get(type);

        T instance;
        synchronized(queue) {
            instance = instantiate(type);
            if(instance == null) {
                if(!queue.contains(type))
                    queue.add(type);

                return null;
            }

            queue.remove(type);
        }

        processQueue();
        return instance;
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
        synchronized(queue) {
            Iterator<Type<?>> it = queue.iterator();
            while(it.hasNext()) {
                // Try to resolve type
                Object instance = instantiate(it.next());
                if(instance == null)
                    continue;

                // Remove resolved type and restart iterator
                it.remove();
                it = queue.iterator();
            }
        }
    }

}
