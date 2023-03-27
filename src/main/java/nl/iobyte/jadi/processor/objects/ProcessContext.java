package nl.iobyte.jadi.processor.objects;

import lombok.AllArgsConstructor;
import lombok.Getter;
import nl.iobyte.jadi.JaDI;
import nl.iobyte.jadi.reflections.Type;

@Getter
@AllArgsConstructor
public class ProcessContext {

    private final Object instance;
    private final Type<?> type;
    private final JaDI jadi;

}
