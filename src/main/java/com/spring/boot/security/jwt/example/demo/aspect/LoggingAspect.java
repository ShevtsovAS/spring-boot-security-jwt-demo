package com.spring.boot.security.jwt.example.demo.aspect;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Slf4j
@Aspect
@Component
public class LoggingAspect {

    @Pointcut("execution(public * com.spring.boot.security.jwt.example.demo.controller.*.*(..))")
    private void controllerPackage() {}

    @Pointcut("execution(public * com.spring.boot.security.jwt.example.demo.service.*.*(..))")
    private void servicePackage() {}

    @Pointcut("execution(public * com.spring.boot.security.jwt.example.demo.repository.*.*(..))")
    private void repositoryPackage() {}

    @Pointcut("controllerPackage() || servicePackage() || repositoryPackage()")
    private void appFlow() {}

    @Before("appFlow()")
    public void before(JoinPoint joinPoint) {
        String user = getCurrentUser();
        String method = joinPoint.getSignature().toShortString();
        Object[] args = joinPoint.getArgs();
        log.info("User {} calling method {} with arguments {}", user, method, args);
    }

    @AfterReturning(pointcut = "appFlow()", returning = "result")
    public void afterReturning(JoinPoint joinPoint, Object result) {
        String user = getCurrentUser();
        String method = joinPoint.getSignature().toShortString();
        log.info("For user {} method {} returns {}", user, method, result);
    }

    @Around("@annotation(LogExecutionTime)")
    public Object logExecutionTime(ProceedingJoinPoint joinPoint) throws Throwable {
        long start = System.currentTimeMillis();
        Object proceed = joinPoint.proceed();
        long executionTime = System.currentTimeMillis() - start;

        String method = joinPoint.getSignature().toShortString();
        log.info("Method {} was executed in {} second", method, executionTime / 1000D);
        return proceed;
    }

    private String getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication == null ? "unauthorized" : authentication.getName();
    }

}
