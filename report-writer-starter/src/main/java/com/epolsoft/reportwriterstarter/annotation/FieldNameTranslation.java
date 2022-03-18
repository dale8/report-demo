package com.epolsoft.reportwriterstarter.annotation;

import java.lang.annotation.*;

@Inherited
@Retention(value = RetentionPolicy.RUNTIME)
@Target(value = {ElementType.FIELD, ElementType.METHOD})
public @interface FieldNameTranslation {
    String value() default "";
    int order() default Integer.MAX_VALUE;
    boolean ignore() default false;
}
