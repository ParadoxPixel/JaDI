package nl.iobyte.jadi.processor.objects;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import nl.iobyte.jadi.processor.annotations.Processors;
import nl.iobyte.jadi.processor.groups.FieldProcessorGroupTest.DummyFieldProcessor;
import nl.iobyte.jadi.processor.groups.MethodProcessorGroupTest.DummyMethodProcessor;
import nl.iobyte.jadi.processor.groups.TypeProcessorGroupTest.DummyTypeProcessor;

@Target({ElementType.TYPE, ElementType.FIELD, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Processors({
    DummyTypeProcessor.class,
    DummyFieldProcessor.class,
    DummyMethodProcessor.class
})
public @interface DummyAnnotation {

}
