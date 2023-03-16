package nl.iobyte.jadi.processor.groups;

import java.util.concurrent.atomic.AtomicInteger;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import nl.iobyte.jadi.processor.interfaces.TypeProcessor;
import nl.iobyte.jadi.processor.objects.DummyAnnotation;
import nl.iobyte.jadi.processor.objects.DummyContext;
import nl.iobyte.jadi.processor.objects.ProcessContext;
import nl.iobyte.jadi.reflections.Type;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class TypeProcessorGroupTest {

    private TypeProcessorGroup<DummyAnnotation> group;

    @BeforeEach
    void setUp() {
        group = new TypeProcessorGroup<>(DummyAnnotation.class);
        group.add(new DummyTypeProcessor(null));
    }

    @Test
    void testProcessors() {
        Assertions.assertEquals(1, group.processors(new DummyContext().getType()).size());
    }

    @AllArgsConstructor
    @NoArgsConstructor
    public static class DummyTypeProcessor implements TypeProcessor<DummyAnnotation> {

        private AtomicInteger i;

        @Override
        public Class<DummyAnnotation> getType() {
            return DummyAnnotation.class;
        }

        @Override
        public void process(ProcessContext ctx, Type<?> type, DummyAnnotation annotation) {
            if(i.addAndGet(1) == 2) {
                throw new IllegalStateException("counter cannot be 2");
            }
        }

    }

}