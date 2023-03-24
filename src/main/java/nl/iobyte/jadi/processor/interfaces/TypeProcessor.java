package nl.iobyte.jadi.processor.interfaces;

import java.lang.annotation.Annotation;
import nl.iobyte.jadi.processor.objects.ProcessContext;
import nl.iobyte.jadi.reflections.Type;

public interface TypeProcessor<T extends Annotation> extends BaseProcessor<T> {

    /**
     * Process annotation for type
     *
     * @param ctx        process context
     * @param type       type to process
     * @param annotation annotation instance
     */
    void process(ProcessContext ctx, Type<?> type, T annotation);

}
