package nl.iobyte.jadi.objects;

import java.util.HashMap;
import java.util.List;
import java.util.function.Function;
import nl.iobyte.jadi.reflections.Type;

public class HierarchyMap<T> extends HashMap<Type<?>, T> {

    /**
     * Get the closest instance of type
     *
     * @param key type
     * @return type instance
     */
    @Override
    public synchronized T get(Object key) {
        return getInternal((Type<?>) key);
    }

    @Override
    public synchronized T put(Type<?> key, T value) {
        return super.put(key, value);
    }

    @Override
    public synchronized T computeIfAbsent(Type<?> key, Function<? super Type<?>, ? extends T> mappingFunction) {
        T obj = super.get(key);
        if(obj != null)
            return obj;

        obj = mappingFunction.apply(key);
        if(obj == null)
            return null;

        super.put(key, obj);
        return obj;
    }

    /**
     * Get the closest hierarchical type's value
     *
     * @param type type
     * @return value
     */
    private T getInternal(Type<?> type) {
        type = getClosestType(type);
        if(type == null)
            return null;

        return super.get(type);
    }

    /**
     * Get all values that can be assigned to type
     *
     * @param type type
     * @return list of values
     */
    public List<T> getAssignableValues(Type<?> type) {
        return keySet().stream()
            .filter(t -> t.isAssignable(type))
            .map(super::get)
            .toList();
    }

    /**
     * Check if type can be mapped
     *
     * @param type type
     * @return whether it can be mapped or not
     */
    public boolean containsKey(Type<?> type) {
        if(super.containsKey(type))
            return true;

        return keySet().stream().anyMatch(type::isAssignable);
    }

    /**
     * Get the closest type found in map
     *
     * @param type type
     * @return type
     */
    private Type<?> getClosestType(Type<?> type) {
        if(super.containsKey(type))
            return type;

        // Get assignable keys
        List<Type<?>> assignableKeys = keySet().stream()
            .filter(type::isAssignable)
            .toList();

        // Get the closest type
        int distance = Integer.MAX_VALUE;
        Type<?> closestType = null;
        for(Type<?> assignableKey : assignableKeys) {
            int i = assignableKey.getHierarchyDepth(type);
            if(i == -1)
                continue;

            if(i < distance) {
                distance = i;
                closestType = assignableKey;
            }
        }

        return closestType;
    }

}
