package com.imagehoo.auth_10.aspect;

import com.imagehoo.auth_10.util.RetryOnOptimisticLock;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.stereotype.Component;

@Slf4j
@Aspect
@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class LockingRetryAspect {

    @Around("@annotation(retryOnOptimisticLock)")
    public Object retry(ProceedingJoinPoint joinPoint, RetryOnOptimisticLock optimisticLock) throws Throwable {
        int maxRetries = optimisticLock.maxRetries();
        long delayInMs = optimisticLock.delayInMillis();
        int attempts = 0;
        for (attempts = 0; attempts < maxRetries; attempts++) {
            try {
                joinPoint.proceed();
            }catch (ObjectOptimisticLockingFailureException ex){
                if (attempts + 1 == maxRetries) {
                    // If this was the last attempt, re-throw the exception
                    log.error("Optimistic lock failure: Max retries exhausted for method: {}", joinPoint.getSignature().toShortString(), ex);
                    throw ex;
                }

                // Log and wait before retrying
                System.out.println("Optimistic lock failure detected. Retrying attempt " + (attempts + 1) + " of " + maxRetries + " after " + delayInMs + "ms...");

                // Add a small wait time to let the database cool down slightly
                Thread.sleep(delayInMs);
            }
        }
        throw new IllegalStateException("Failed to execute method after all retries.");
    }

}
