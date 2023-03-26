package nl.iobyte.jadi.reflections;

import java.lang.reflect.Constructor;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
@EqualsAndHashCode(callSuper = true)
public class TypeConstructor<T> extends TypeElement<Constructor<T>> {

    private final Constructor<T> constructor;
    private final Type<T> type;

    /**
     * Type Constructor
     *
     * @param constructor reflection constructor
     * @param type        instance type
     */
    public TypeConstructor(Constructor<T> constructor, Type<T> type) {
        super(constructor);
        constructor.setAccessible(true);

        this.constructor = constructor;
        this.type = type;
    }

    /**
     * Get parameter count
     *
     * @return amount
     */
    public int getParameterCount() {
        return constructor.getParameterCount();
    }

    /**
     * Get parameters of constructor
     *
     * @return list of type parameters
     */
    public List<TypeParameter> getParameters() {
        return Arrays.stream(constructor.getParameters())
            .map(TypeParameter::new)
            .toList();
    }

    /**
     * Get a new instance of the type
     *
     * @param values array of values to pass to constructor
     * @return new instance
     */
    public T newInstance(Object... values) {
        values = Optional.ofNullable(values).orElse(new Object[0]);
        if(constructor.getParameterCount() != values.length)
            throw new IllegalArgumentException("expected " + constructor.getParameterCount() + " parameters got " + values.length);

        for(int i = 0; i < constructor.getParameterCount(); i++)
            if(Type.of(constructor.getParameterTypes()[i]).noInstance(values[i]))
                throw new IllegalArgumentException("value #" + i + " is of invalid type");

        try {
            return constructor.newInstance(values);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

}
