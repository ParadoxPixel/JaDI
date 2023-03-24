package nl.iobyte.jadi.processor.groups;

import java.lang.annotation.Annotation;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import nl.iobyte.jadi.processor.interfaces.MethodProcessor;
import nl.iobyte.jadi.processor.objects.ProcessContext;
import nl.iobyte.jadi.reflections.Type;

public final class MethodProcessorGroup<T extends Annotation> extends AbstractProcessorGroup<T, MethodProcessor<T>> {

    /**
     * Method Processor Group
     *
     * @param type annotation type
     */
    public MethodProcessorGroup(Class<T> type) {
        super(type);
    }

    /**
     * {@inheritDoc}
     *
     * @param type type to get methods from
     */
    @Override
    public List<Consumer<ProcessContext>> processors(Type<?> type) {
        return type.getMethods(method -> method.hasAnnotation(getType()))
            .stream()
            .flatMap(method -> set.stream().map(processor -> (Consumer<ProcessContext>) ctx -> processor.process(
                ctx,
                method,
                method.getAnnotation(getType())
            ))).collect(Collectors.toList());
    }

}
