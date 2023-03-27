package nl.iobyte.jadi;

import java.time.Duration;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.function.Supplier;
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
        int initial = jadi.getHierarchyMap().size();
        jadi.bind(Type.of(boolean.class), false);
        Assertions.assertEquals(initial + 1, jadi.getHierarchyMap().size());
    }

    @Test
    void resolveSelf() throws ExecutionException, InterruptedException {
        JaDI obj = jadi.resolve(Type.of(JaDI.class)).get();
        Assertions.assertNotNull(obj);
        Assertions.assertEquals(jadi, obj);
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
    void resolveComplex() throws ExecutionException, InterruptedException {
        CompletableFuture<DummyBuildClass> future = jadi.resolve(Type.of(DummyBuildClass.class));
        Assertions.assertNotNull(future);
        Assertions.assertFalse(future.isDone());

        DummyExtendedBuildClass obj = new DummyExtendedBuildClass("nope", false);
        jadi.bind(Type.of(DummyExtendedBuildClass.class), obj);
        Assertions.assertEquals(obj, future.get());
    }

    @Test
    void supplier() throws ExecutionException, InterruptedException {
        CompletableFuture<Supplier<DummyClass>> future = jadi.getSupplier(Type.of(DummyClass.class));
        Assertions.assertNotNull(future);
        Assertions.assertTrue(future.isDone());

        Supplier<DummyClass> supplier = future.get();
        Assertions.assertNotNull(supplier);
        Assertions.assertNotNull(supplier.get());
    }

    @AllArgsConstructor
    public static class DummyBuildClass {

        private String str;

        private boolean b;

    }

    public static class DummyExtendedBuildClass extends DummyBuildClass {

        public DummyExtendedBuildClass(String str, boolean b) {
            super(str, b);
        }

    }

}