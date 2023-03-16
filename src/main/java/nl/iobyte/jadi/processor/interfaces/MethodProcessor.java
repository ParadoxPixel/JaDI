package nl.iobyte.jadi.processor.interfaces;

import java.lang.annotation.Annotation;
import nl.iobyte.jadi.processor.objects.ProcessContext;
import nl.iobyte.jadi.reflections.TypeMethod;

public interface MethodProcessor<T extends Annotation> extends BaseProcessor<T> {

    /**
     * Process annotation for field.
     *
     * @param ctx        process context
     * @param method     method to process
     * @param annotation annotation instance
     */
    void process(ProcessContext ctx, TypeMethod method, T annotation);

}
