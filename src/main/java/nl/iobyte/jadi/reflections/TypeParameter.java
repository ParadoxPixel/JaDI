package nl.iobyte.jadi.reflections;

import java.lang.reflect.Parameter;
import lombok.Getter;

public class TypeParameter extends TypeElement<Parameter> {

    @Getter
    private final Type<?> type;

    /**
     * Type Parameter
     *
     * @param elem Parameter
     */
    public TypeParameter(Parameter elem) {
        super(elem);
        this.type = Type.of(elem.getType());
    }

}
