package nl.iobyte.jadi.processor;

import java.lang.annotation.Annotation;
import java.util.Arrays;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import nl.iobyte.jadi.processor.annotations.Processors;
import nl.iobyte.jadi.processor.groups.FieldProcessorGroup;
import nl.iobyte.jadi.processor.groups.MethodProcessorGroup;
import nl.iobyte.jadi.processor.groups.TypeProcessorGroup;
import nl.iobyte.jadi.processor.interfaces.BaseProcessor;
import nl.iobyte.jadi.processor.interfaces.FieldProcessor;
import nl.iobyte.jadi.processor.interfaces.MethodProcessor;
import nl.iobyte.jadi.processor.interfaces.TypeProcessor;
import nl.iobyte.jadi.processor.objects.ProcessContext;

@NoArgsConstructor
@SuppressWarnings({"unchecked", "unused"})
public class AnnotationProcessor {

    @Getter(AccessLevel.PACKAGE) // Used for Testing
    private final Map<Class<? extends BaseProcessor<?>>, BaseProcessor<?>> processorMap = new ConcurrentHashMap<>();
    private final Map<Class<? extends Annotation>, TypeProcessorGroup<?>> typeProcessorMap = new ConcurrentHashMap<>();
    private final Map<Class<? extends Annotation>, FieldProcessorGroup<?>> fieldProcessorMap = new ConcurrentHashMap<>();
    private final Map<Class<? extends Annotation>, MethodProcessorGroup<?>> methodProcessorMap = new ConcurrentHashMap<>();

    /**
     * Register processors for annotation by the {@link Processors} annotation
     *
     * @param type annotation type
     * @param <T>  type
     */
    public <T extends Annotation> void registerAnnotation(Class<T> type) {
        Optional.ofNullable(type.getAnnotation(Processors.class))
            .ifPresent(processors -> Arrays.stream(processors.value()).forEach(
                processorType -> registerGeneric((Class<? extends BaseProcessor<T>>) processorType)
            ));
    }

    /**
     * Register base processor
     *
     * @param type processor type
     * @param <T>  annotation type
     */
    public <T extends Annotation> void registerGeneric(Class<? extends BaseProcessor<T>> type) {
        if(TypeProcessor.class.isAssignableFrom(type)) {
            registerType((Class<? extends TypeProcessor<T>>) type);
            return;
        }

        if(FieldProcessor.class.isAssignableFrom(type)) {
            registerField((Class<? extends FieldProcessor<T>>) type);
            return;
        }

        if(!(MethodProcessor.class.isAssignableFrom(type)))
            return;

        registerMethod((Class<? extends MethodProcessor<T>>) type);
    }

    /**
     * Register type processor
     *
     * @param type type processor type
     * @param <T>  annotation type
     */
    public <T extends Annotation> void registerType(Class<? extends TypeProcessor<T>> type) {
        TypeProcessor<T> processor = getProcessor(type);
        if(processor == null)
            return;

        TypeProcessorGroup<?> processorGroup = typeProcessorMap.computeIfAbsent(
            processor.getType(),
            TypeProcessorGroup::new
        );
        ((TypeProcessorGroup<T>) processorGroup).add(processor);
    }

    /**
     * Register field processor
     *
     * @param type field processor type
     * @param <T>  annotation type
     */
    public <T extends Annotation> void registerField(Class<? extends FieldProcessor<T>> type) {
        FieldProcessor<T> processor = getProcessor(type);
        if(processor == null)
            return;

        FieldProcessorGroup<?> processorGroup = fieldProcessorMap.computeIfAbsent(
            processor.getType(),
            FieldProcessorGroup::new
        );
        ((FieldProcessorGroup<T>) processorGroup).add(processor);
    }

    /**
     * Register method processor
     *
     * @param type method processor type
     * @param <T>  annotation type
     */
    public <T extends Annotation> void registerMethod(Class<? extends MethodProcessor<T>> type) {
        MethodProcessor<T> processor = getProcessor(type);
        if(processor == null)
            return;

        MethodProcessorGroup<?> processorGroup = methodProcessorMap.computeIfAbsent(
            processor.getType(),
            MethodProcessorGroup::new
        );
        ((MethodProcessorGroup<T>) processorGroup).add(processor);
    }

    /**
     * Get (and register if not exists) processor by type
     *
     * @param type processor type
     * @param <T>  annotation type
     * @param <P>  processor type
     * @return processor instance
     */
    public <T extends Annotation, P extends BaseProcessor<T>> P getProcessor(Class<P> type) {
        return (P) processorMap.computeIfAbsent(type, key -> {
            try {
                return key.getConstructor().newInstance();
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        });
    }

    /**
     * Process context with provided process types
     *
     * @param ctx process context
     */
    public void process(ProcessContext ctx) {
        typeProcessorMap.values().forEach(group -> group.process(ctx));
        fieldProcessorMap.values().forEach(group -> group.process(ctx));
        methodProcessorMap.values().forEach(group -> group.process(ctx));
    }

}
