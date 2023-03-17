package nl.iobyte.jadi;

import java.time.Duration;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import nl.iobyte.jadi.processor.objects.DummyClass;
import nl.iobyte.jadi.reflections.Type;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class JaDITest {

    private JaDI jadi;

    @BeforeEach
    void setUp() {
        jadi = new JaDI();
    }

    @Test
    void bind() {
        Assertions.assertEquals(0, jadi.getHierarchyMap().size());
        jadi.bind(Type.of(boolean.class), false);
        Assertions.assertEquals(1, jadi.getHierarchyMap().size());
    }

    @Test
    void resolve() throws ExecutionException, InterruptedException {
        CompletableFuture<String> future = jadi.resolve(Type.of(String.class));
        Assertions.assertNotNull(future);
        Assertions.assertEquals(future, jadi.resolve(Type.of(String.class)));

        jadi.bind(Type.of(String.class), "something");
        Assertions.assertEquals("something", future.get());
    }

    @Test
    void resolveFactory() {
        Assertions.assertDoesNotThrow(() -> jadi.resolve(Type.of(DummyClass.class), Duration.ofSeconds(1)));
        Assertions.assertNotNull(jadi.resolve(Type.of(DummyClass.class), Duration.ofSeconds(1)));
    }

}