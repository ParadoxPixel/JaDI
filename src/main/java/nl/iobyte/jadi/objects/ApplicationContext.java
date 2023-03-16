package nl.iobyte.jadi.objects;

import lombok.Getter;
import nl.iobyte.jadi.interfaces.TypeResolver;
import nl.iobyte.jadi.processor.objects.ProcessContext;
import nl.iobyte.jadi.reflections.Type;

public class ApplicationContext extends ProcessContext {

    @Getter
    private final TypeResolver typeResolver;

    public ApplicationContext(Object instance, Type<?> type, TypeResolver typeResolver) {
        super(instance, type);
        this.typeResolver = typeResolver;
    }

}
