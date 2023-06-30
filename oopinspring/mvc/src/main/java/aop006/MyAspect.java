package aop006;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;

@Component
@Aspect
public class MyAspect {

    @Before("execution(* runSomething())")
    public void before(JoinPoint joinPoint) {
        System.out.println("얼굴 인식 확인: 문을 개방하라");
    }

    @After("execution(* runSomething())")
    public void lockDoor(JoinPoint joinPoint) {
        System.out.println("주인님 나갔다: 어이 문 잠가!!");
    }
}