package nl.iobyte.jadi.processor.interfaces;

import java.lang.annotation.Annotation;

public interface BaseProcessor<T extends Annotation> {

    /**
     * Get type of annotation
     *
     * @return type
     */
    Class<T> getType();

}
