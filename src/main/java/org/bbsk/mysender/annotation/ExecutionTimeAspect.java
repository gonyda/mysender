package org.bbsk.mysender.annotation;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.Instant;

@Aspect
@Component
@Slf4j
public class ExecutionTimeAspect {

    @Around("@annotation(org.bbsk.mysender.annotation.MeasureExecutionTime)")
    public Object measureExecutionTime(ProceedingJoinPoint joinPoint) throws Throwable {
        Instant startTime = Instant.now(); // 시작시간
        Object proceed = joinPoint.proceed();       // 메서드 실행
        Instant endTime = Instant.now(); // 종료시간
        log.info("## 소요시간 : {}초", Duration.between(startTime, endTime).getSeconds());
        return proceed; // 결과 반환
    }
}