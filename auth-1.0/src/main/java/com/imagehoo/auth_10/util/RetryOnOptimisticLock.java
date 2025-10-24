package com.imagehoo.auth_10.util;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(value = RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
public @interface RetryOnOptimisticLock {
    int maxRetries() default 3;
    long delayInMillis() default 50L;
}
