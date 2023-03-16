package nl.iobyte.jadi.processor;

import nl.iobyte.jadi.processor.groups.FieldProcessorGroupTest.DummyFieldProcessor;
import nl.iobyte.jadi.processor.groups.MethodProcessorGroupTest.DummyMethodProcessor;
import nl.iobyte.jadi.processor.groups.TypeProcessorGroupTest.DummyTypeProcessor;
import nl.iobyte.jadi.processor.objects.BrokenProcessor;
import nl.iobyte.jadi.processor.objects.DummyAnnotation;
import nl.iobyte.jadi.processor.objects.DummyContext;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class AnnotationProcessorTest {

    private AnnotationProcessor processor;

    @BeforeEach
    void setUp() {
        processor = new AnnotationProcessor();
    }

    @Test
    void registerAnnotation() {
        processor.registerAnnotation(DummyAnnotation.class);

        Assertions.assertEquals(3, processor.getProcessorMap().size());
        Assertions.assertTrue(processor.getProcessorMap().containsKey(DummyTypeProcessor.class));
        Assertions.assertTrue(processor.getProcessorMap().containsKey(DummyFieldProcessor.class));
        Assertions.assertTrue(processor.getProcessorMap().containsKey(DummyMethodProcessor.class));
    }

    @Test
    void registerGeneric() {
        processor.registerGeneric(DummyTypeProcessor.class);

        Assertions.assertEquals(1, processor.getProcessorMap().size());
        Assertions.assertTrue(processor.getProcessorMap().containsKey(DummyTypeProcessor.class));
    }

    @Test
    void registerType() {
        processor.registerType(DummyTypeProcessor.class);

        Assertions.assertEquals(1, processor.getProcessorMap().size());
        Assertions.assertTrue(processor.getProcessorMap().containsKey(DummyTypeProcessor.class));
    }

    @Test
    void registerField() {
        processor.registerField(DummyFieldProcessor.class);

        Assertions.assertEquals(1, processor.getProcessorMap().size());
        Assertions.assertTrue(processor.getProcessorMap().containsKey(DummyFieldProcessor.class));
    }

    @Test
    void registerMethod() {
        processor.registerMethod(DummyMethodProcessor.class);

        Assertions.assertEquals(1, processor.getProcessorMap().size());
        Assertions.assertTrue(processor.getProcessorMap().containsKey(DummyMethodProcessor.class));
    }

    @Test
    void getProcessor() {
        Assertions.assertNotNull(processor.getProcessor(DummyTypeProcessor.class));
        Assertions.assertEquals(DummyTypeProcessor.class, processor.getProcessor(DummyTypeProcessor.class).getClass());
    }

    @Test
    void getProcessorException() {
        processor = new AnnotationProcessor();
        Assertions.assertNull(processor.getProcessor(BrokenProcessor.class));
    }

    @Test
    void process() {
        processor.process(new DummyContext());
    }

}