package nl.iobyte.jadi.processor.groups;

import java.lang.annotation.Annotation;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;
import nl.iobyte.jadi.processor.interfaces.TypeProcessor;
import nl.iobyte.jadi.processor.objects.ProcessContext;
import nl.iobyte.jadi.reflections.Type;

public final class TypeProcessorGroup<T extends Annotation> extends AbstractProcessorGroup<T, TypeProcessor<T>> {

    /**
     * Type Processor Group.
     *
     * @param type annotation type
     */
    public TypeProcessorGroup(Class<T> type) {
        super(type);
    }

    /**
     * {@inheritDoc}
     *
     * @param type type to get processors for
     */
    @Override
    public List<Consumer<ProcessContext>> processors(Type<?> type) {
        if(!type.hasAnnotation(getType()))
            return Collections.emptyList();
        
        return set.stream()
            .map(processor -> (Consumer<ProcessContext>) ctx -> processor.process(
                ctx,
                type,
                type.getAnnotation(getType())
            )).toList();
    }

}
