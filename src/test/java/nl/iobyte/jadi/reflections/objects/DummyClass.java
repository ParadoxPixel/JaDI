package nl.iobyte.jadi.reflections.objects;

import lombok.Getter;
import lombok.ToString;
import nl.iobyte.jadi.annotations.Assisted;
import nl.iobyte.jadi.annotations.AssistedInject;
import nl.iobyte.jadi.annotations.Inject;
import nl.iobyte.jadi.annotations.PostConstruct;

@Getter
@ToString
public class DummyClass {

    @Inject
    private String str;

    @AssistedInject
    private boolean debug;

    private final int i;

    @Assisted
    public DummyClass(int i, @AssistedInject int j) {
        this.i = i + 3 * j;
    }

    public DummyClass(int i) {
        this.i = i;
    }

    @PostConstruct
    public int start() {
        return i;
    }

}
