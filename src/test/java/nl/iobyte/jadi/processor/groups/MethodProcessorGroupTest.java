package nl.iobyte.jadi.processor.groups;

import nl.iobyte.jadi.processor.interfaces.MethodProcessor;
import nl.iobyte.jadi.processor.objects.DummyAnnotation;
import nl.iobyte.jadi.processor.objects.DummyContext;
import nl.iobyte.jadi.processor.objects.ProcessContext;
import nl.iobyte.jadi.reflections.TypeMethod;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class MethodProcessorGroupTest {

    private MethodProcessorGroup<DummyAnnotation> group;

    @BeforeEach
    void setUp() {
        group = new MethodProcessorGroup<>(DummyAnnotation.class);
        group.add(new DummyMethodProcessor());
    }

    @Test
    void testProcessors() {
        Assertions.assertEquals(1, group.processors(new DummyContext().getType()).size());
    }

    public static class DummyMethodProcessor implements MethodProcessor<DummyAnnotation> {

        @Override
        public Class<DummyAnnotation> getType() {
            return DummyAnnotation.class;
        }

        @Override
        public void process(ProcessContext ctx, TypeMethod method, DummyAnnotation annotation) {
        }

    }

}