package nl.iobyte.jadi.processor.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import nl.iobyte.jadi.processor.interfaces.BaseProcessor;

@Target(ElementType.ANNOTATION_TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface Processors {

    /**
     * Processors for annotation
     *
     * @return array of processor types
     */
    Class<? extends BaseProcessor<?>>[] value() default {};

}
