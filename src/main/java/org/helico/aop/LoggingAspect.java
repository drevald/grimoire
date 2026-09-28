package org.helico.aop;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class LoggingAspect {
    @Around("@annotation(org.helico.aop.Logged) || @within(org.helico.aop.Logged)")
    public Object logEntryExit(ProceedingJoinPoint pjp) throws Throwable {
        Logger log = LoggerFactory.getLogger(pjp.getSignature().getDeclaringType());
        if (!log.isDebugEnabled()) {
            return pjp.proceed();
        }
        String method = pjp.getSignature().getName();
        long start = System.nanoTime();
        log.debug(">>> {}", method);
        try {
            Object result = pjp.proceed();
            log.debug("<<< {} ({} ms)", method, (System.nanoTime() - start) / 1_000_000);
            return result;
        } catch (Throwable e) {
            log.debug("<<< {} failed: {}", method, e.toString());
            throw e;
        }
    }
}
