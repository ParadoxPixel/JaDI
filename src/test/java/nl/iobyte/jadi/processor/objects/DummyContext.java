package nl.iobyte.jadi.processor.objects;

import nl.iobyte.jadi.reflections.Type;

public class DummyContext extends ProcessContext {

    public DummyContext() {
        super(new DummyClass(), Type.of(DummyClass.class), null);
    }

}
