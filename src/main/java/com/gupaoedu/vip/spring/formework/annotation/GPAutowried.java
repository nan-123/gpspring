package com.gupaoedu.vip.spring.formework.annotation;

import java.lang.annotation.*;

@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface GPAutowried {
    String value()default "";
}
