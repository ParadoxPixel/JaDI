package nl.iobyte.jadi.processor.objects;

import lombok.AllArgsConstructor;
import nl.iobyte.jadi.processor.interfaces.TypeProcessor;
import nl.iobyte.jadi.reflections.Type;

@AllArgsConstructor
public class BrokenProcessor implements TypeProcessor<DummyAnnotation> {

    private final String str;

    @Override
    public Class<DummyAnnotation> getType() {
        return DummyAnnotation.class;
    }

    @Override
    public void process(ProcessContext ctx, Type<?> type, DummyAnnotation annotation) {
        System.out.println(str);
    }

}
