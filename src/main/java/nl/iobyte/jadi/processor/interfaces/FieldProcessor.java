package nl.iobyte.jadi.processor.interfaces;

import java.lang.annotation.Annotation;
import nl.iobyte.jadi.processor.objects.ProcessContext;
import nl.iobyte.jadi.reflections.TypeField;

public interface FieldProcessor<T extends Annotation> extends BaseProcessor<T> {

    /**
     * Process annotation for field.
     *
     * @param ctx        process context
     * @param field      field to process
     * @param annotation annotation instance
     */
    void process(ProcessContext ctx, TypeField field, T annotation);

}
