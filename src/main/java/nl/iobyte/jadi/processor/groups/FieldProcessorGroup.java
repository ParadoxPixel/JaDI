package nl.iobyte.jadi.processor.groups;

import java.lang.annotation.Annotation;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import nl.iobyte.jadi.processor.interfaces.FieldProcessor;
import nl.iobyte.jadi.processor.objects.ProcessContext;
import nl.iobyte.jadi.reflections.Type;

public final class FieldProcessorGroup<T extends Annotation> extends AbstractProcessorGroup<T, FieldProcessor<T>> {

    /**
     * Field Processor Group
     *
     * @param type annotation type
     */
    public FieldProcessorGroup(Class<T> type) {
        super(type);
    }

    /**
     * {@inheritDoc}
     *
     * @param type type to get fields from
     */
    @Override
    public List<Consumer<ProcessContext>> processors(Type<?> type) {
        return type.getFields(field -> field.hasAnnotation(getType()))
            .stream()
            .flatMap(field -> set.stream().map(processor -> (Consumer<ProcessContext>) ctx -> processor.process(
                ctx,
                field,
                field.getAnnotation(getType())
            ))).collect(Collectors.toList());
    }

}
