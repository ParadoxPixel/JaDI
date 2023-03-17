package nl.iobyte.jadi.reflections;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
@EqualsAndHashCode
@AllArgsConstructor
public class TypeElement<T extends AnnotatedElement> {

    private final T elem;

    /**
     * Check if element has annotation.
     *
     * @param type annotation type
     * @return whether the element has the annotation or not
     */
    public boolean hasAnnotation(Class<? extends Annotation> type) {
        return elem.isAnnotationPresent(type);
    }

    /**
     * Get annotation instance by type.
     *
     * @param type annotation type
     * @param <R>  type
     * @return annotation instance
     */
    public <R extends Annotation> R getAnnotation(Class<R> type) {
        return elem.getAnnotation(type);
    }

}
