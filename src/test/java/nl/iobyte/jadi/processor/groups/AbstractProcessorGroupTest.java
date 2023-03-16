package nl.iobyte.jadi.processor.groups;

import java.util.concurrent.atomic.AtomicInteger;
import nl.iobyte.jadi.processor.groups.TypeProcessorGroupTest.DummyTypeProcessor;
import nl.iobyte.jadi.processor.interfaces.TypeProcessor;
import nl.iobyte.jadi.processor.objects.DummyAnnotation;
import nl.iobyte.jadi.processor.objects.DummyContext;
import nl.iobyte.jadi.processor.objects.ProcessContext;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class AbstractProcessorGroupTest {

    private AbstractProcessorGroup<DummyAnnotation, TypeProcessor<DummyAnnotation>> group;

    @BeforeEach
    void setUp() {
        group = new TypeProcessorGroup<>(DummyAnnotation.class);
    }

    @Test
    void add() {
        ProcessContext ctx = new DummyContext();

        // Without processor
        Assertions.assertEquals(0, group.processors(ctx.getType()).size());

        // With processor
        group.add(new DummyTypeProcessor(null));
        Assertions.assertEquals(1, group.processors(ctx.getType()).size());
    }

    @Test
    void process() {
        AtomicInteger i = new AtomicInteger(0);
        group.add(new DummyTypeProcessor(i));

        // Process
        group.process(new DummyContext());
        Assertions.assertEquals(1, i.get());
    }

    @Test
    void processException() {
        AtomicInteger i = new AtomicInteger(1);
        group.add(new DummyTypeProcessor(i));

        // Process
        group.process(new DummyContext());
        Assertions.assertEquals(2, i.get());
    }

    @Test
    void processExceptionDebug() {
        group = new TypeProcessorGroup<>(DummyAnnotation.class);

        AtomicInteger i = new AtomicInteger(1);
        group.add(new DummyTypeProcessor(i));

        // Process
        group.process(new DummyContext());
        Assertions.assertEquals(2, i.get());
    }

}