package nl.iobyte.jadi.reflections;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
@EqualsAndHashCode(callSuper = true)
public class TypeMethod extends TypeElement<Method> {

    private final Method method;
    private final Type<?> type;

    /**
     * Type Method.
     *
     * @param method reflection method
     * @param type   instance type
     */
    public TypeMethod(Method method, Type<?> type) {
        super(method);
        method.setAccessible(true);

        this.method = method;
        this.type = type;
    }

    /**
     * Get parameter types of constructor.
     *
     * @return list of parameter types
     */
    public List<Type<?>> getParameterTypes() {
        return Arrays.stream(method.getParameterTypes())
            .map(Type::of)
            .collect(Collectors.toList());
    }

    /**
     * Set the value of the field for a specific instance.
     *
     * @param instance type instance
     * @param values   array of values to pass to constructor
     */
    public void invoke(Object instance, Object... values) {
        assert instance != null;

        if(type.noInstance(instance))
            throw new IllegalArgumentException("provided instance is of invalid type");

        if(values == null)
            values = new Object[0];

        if(method.getParameterCount() != values.length)
            throw new IllegalArgumentException("expected " + method.getParameterCount() + " parameters got " + values.length);

        for(int i = 0; i < method.getParameterCount(); i++)
            if(Type.of(method.getParameterTypes()[i]).noInstance(values[i]))
                throw new IllegalArgumentException("value #" + i + " is of invalid type");

        try {
            method.invoke(instance, values);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
