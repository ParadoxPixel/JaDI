package nl.iobyte.jadi;

import java.time.Duration;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import lombok.AllArgsConstructor;
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

    @Test
    void buildNone() {
        jadi.bind(Type.of(boolean.class), true);
        jadi.bind(Type.of(String.class), "something");

        DummyBuildClass instance = jadi.build(Type.of(DummyBuildClass.class));
        Assertions.assertNotNull(instance);
        Assertions.assertTrue(instance.b);
        Assertions.assertEquals("something", instance.str);
    }

    @Test
    void buildAll() {
        DummyBuildClass instance = jadi.build(Type.of(DummyBuildClass.class), true, "something");
        Assertions.assertNotNull(instance);
        Assertions.assertTrue(instance.b);
        Assertions.assertEquals("something", instance.str);
    }

    @Test
    void buildPartial() {
        jadi.bind(Type.of(boolean.class), true);
        jadi.bind(Type.of(String.class), "none");
        
        DummyBuildClass instance = jadi.build(Type.of(DummyBuildClass.class), "something");
        Assertions.assertNotNull(instance);
        Assertions.assertTrue(instance.b);
        Assertions.assertEquals("something", instance.str);
    }

    @Test
    void buildFail() {
        DummyBuildClass instance = jadi.build(Type.of(DummyBuildClass.class), "something");
        Assertions.assertNull(instance);
    }

    @AllArgsConstructor
    public static class DummyBuildClass {

        private String str;
        private boolean b;

    }

}