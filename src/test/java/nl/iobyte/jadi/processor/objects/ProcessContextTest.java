package nl.iobyte.jadi.processor.objects;

import nl.iobyte.jadi.reflections.Type;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ProcessContextTest {

    private ProcessContext ctx;

    @BeforeEach
    void setUp() {
        ctx = new ProcessContext("some string", Type.of(String.class), null);
    }

    @Test
    void getInstance() {
        Assertions.assertEquals("some string", ctx.getInstance());
    }

    @Test
    void getType() {
        Assertions.assertEquals(Type.of(String.class), ctx.getType());
    }

}