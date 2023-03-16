package nl.iobyte.jadi.processor.groups;

import nl.iobyte.jadi.processor.interfaces.FieldProcessor;
import nl.iobyte.jadi.processor.objects.DummyAnnotation;
import nl.iobyte.jadi.processor.objects.DummyContext;
import nl.iobyte.jadi.processor.objects.ProcessContext;
import nl.iobyte.jadi.reflections.TypeField;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class FieldProcessorGroupTest {

    private FieldProcessorGroup<DummyAnnotation> group;

    @BeforeEach
    void setUp() {
        group = new FieldProcessorGroup<>(DummyAnnotation.class);
        group.add(new DummyFieldProcessor());
    }

    @Test
    void testProcessors() {
        Assertions.assertEquals(1, group.processors(new DummyContext().getType()).size());
    }

    public static class DummyFieldProcessor implements FieldProcessor<DummyAnnotation> {

        @Override
        public Class<DummyAnnotation> getType() {
            return DummyAnnotation.class;
        }

        @Override
        public void process(ProcessContext ctx, TypeField field, DummyAnnotation annotation) {
        }

    }

}