package nl.iobyte.jadi.processor.objects;

import lombok.AllArgsConstructor;
import lombok.Getter;
import nl.iobyte.jadi.reflections.Type;

@AllArgsConstructor
@Getter
public abstract class ProcessContext {

    private final Object instance;
    private final Type<?> type;

}
