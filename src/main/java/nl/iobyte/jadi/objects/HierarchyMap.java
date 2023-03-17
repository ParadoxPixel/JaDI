package nl.iobyte.jadi.objects;

import java.util.HashMap;
import java.util.List;
import nl.iobyte.jadi.reflections.Type;

public class HierarchyMap extends HashMap<Type<?>, Object> {

    @Override
    public synchronized Object get(Object key) {
        return getInternal((Type<?>) key);
    }

    @Override
    public synchronized Object put(Type<?> key, Object value) {
        return super.put(key, value);
    }

    /**
     * Get the closest hierarchical type's value.
     *
     * @param type type
     * @return value
     */
    private Object getInternal(Type<?> type) {
        Object value = super.get(type);
        if(value != null)
            return value;

        // Get assignable keys
        List<Type<?>> assignableKeys = keySet().stream()
            .filter(type::isAssignable)
            .toList();

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

        return closestType == null ? null : super.get(closestType);
    }

    /**
     * Check if type can be mapped.
     *
     * @param type type
     * @return whether it can be mapped or not
     */
    public boolean containsKey(Type<?> type) {
        if(super.containsKey(type))
            return true;

        return keySet().stream().anyMatch(type::isAssignable);
    }

}
