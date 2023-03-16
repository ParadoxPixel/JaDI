package nl.iobyte.jadi.processor.groups;

import java.lang.annotation.Annotation;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import nl.iobyte.jadi.processor.interfaces.BaseProcessor;
import nl.iobyte.jadi.processor.objects.ProcessContext;
import nl.iobyte.jadi.reflections.Type;

@RequiredArgsConstructor
public abstract sealed class AbstractProcessorGroup<T extends Annotation, P extends BaseProcessor<T>> permits
    FieldProcessorGroup, MethodProcessorGroup, TypeProcessorGroup {

    protected final Set<P> set = new HashSet<>();
    @Getter
    private final Class<T> type;

    /**
     * Add processor to group.
     *
     * @param processor processor instance
     */
    public void add(P processor) {
        synchronized(set) {
            set.add(processor);
        }
    }

    /**
     * Process context with processors in group.
     *
     * @param ctx process context
     */
    public void process(ProcessContext ctx) {
        processors(ctx.getType()).forEach(c -> {
            try {
                c.accept(ctx);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    /**
     * Get list of consumers to process annotation from type.
     *
     * @param type type to get processors for
     * @return list of annotation consumers
     */
    public abstract List<Consumer<ProcessContext>> processors(Type<?> type);

}
