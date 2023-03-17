package nl.iobyte.jadi.reflections;

import java.lang.reflect.Field;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
@EqualsAndHashCode(callSuper = true)
public class TypeField extends TypeElement<Field> {

    private final Field field;
    private final Type<?> type;
    private final Type<?> valueType;

    /**
     * Type Field.
     *
     * @param field reflection field
     * @param type  instance type
     */
    public TypeField(Field field, Type<?> type) {
        super(field);
        field.setAccessible(true);

        this.field = field;
        this.type = type;
        this.valueType = Type.of(field.getType());
    }

    /**
     * Set the value of the field for a specific instance.
     *
     * @param instance type instance
     * @param value    new value
     */
    public void set(Object instance, Object value) {
        assert instance != null;

        if(type.noInstance(instance))
            throw new IllegalArgumentException("provided instance is of invalid type");

        if(valueType.noInstance(value))
            throw new IllegalArgumentException("value \"" + value + "\" is not assignable to type " + valueType.getOriginalType());

        try {
            field.set(instance, value);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
