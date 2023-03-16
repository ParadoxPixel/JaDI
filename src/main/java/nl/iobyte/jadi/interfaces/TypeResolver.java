package nl.iobyte.jadi.interfaces;

import java.util.function.Function;
import nl.iobyte.jadi.reflections.Type;

public interface TypeResolver extends Function<Type<?>, Object> {

}
