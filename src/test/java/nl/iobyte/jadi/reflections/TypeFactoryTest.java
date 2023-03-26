package nl.iobyte.jadi.reflections;

import java.util.List;
import nl.iobyte.jadi.objects.HierarchyMap;
import nl.iobyte.jadi.reflections.objects.DummyClass;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class TypeFactoryTest {

    private TypeFactory<DummyClass> factory;

    @BeforeEach
    void setUp() {
        factory = TypeFactory.of(Type.of(DummyClass.class));
    }

    @Test
    void getDependencies() {
        List<Type<?>> dependencies = factory.getDependencies();
        Assertions.assertEquals(2, dependencies.size());
        Assertions.assertTrue(dependencies.contains(Type.of(String.class)));
        Assertions.assertTrue(dependencies.contains(Type.of(int.class)));
    }

    @Test
    void getAssistedDependencies() {
        List<Type<?>> dependencies = factory.getAssistedDependencies();
        Assertions.assertEquals(2, dependencies.size());
        Assertions.assertTrue(dependencies.contains(Type.of(boolean.class)));
        Assertions.assertTrue(dependencies.contains(Type.of(int.class)));
    }

    @Test
    void createMissingAssisted() {
        HierarchyMap map = new HierarchyMap();
        map.put(Type.of(boolean.class), true);
        map.put(Type.of(String.class), "some thing");
        map.put(Type.of(int.class), 42);

        Assertions.assertThrowsExactly(IllegalStateException.class, () -> factory.create(map));
    }

    @Test
    void createSuccess() {
        HierarchyMap map = new HierarchyMap();
        map.put(Type.of(boolean.class), true);
        map.put(Type.of(String.class), "some thing");
        map.put(Type.of(int.class), 42);

        DummyClass obj = factory.create(map, false, 9);
        Assertions.assertNotNull(obj);
        Assertions.assertEquals("some thing", obj.getStr());
        Assertions.assertFalse(obj.isDebug());
        Assertions.assertEquals(69, obj.getI());
    }

    @Test
    void getType() {
        Assertions.assertEquals(Type.of(DummyClass.class), factory.getType());
    }

    @Test
    void getConstructor() {
        Assertions.assertNotNull(factory.getConstructor());
        Assertions.assertEquals(2, factory.getConstructor().getParameterCount());
    }

    @Test
    void getFields() {
        Assertions.assertEquals(2, factory.getFields().size());
    }

    @Test
    void getMethods() {
        Assertions.assertEquals(1, factory.getMethods().size());
    }

}