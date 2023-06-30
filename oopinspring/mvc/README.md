### Pointcut

- 타겟 클래스의 타겟 메서드 지정자를 말한다. (p.303)
- Pointcut 은 JoinPoint 의 부분집합이다. (p.304)

### JoinPoint

- Aspect 적용이 가능한 모든 지점을 말한다. (p.304)
- 스프링 AOP 에서 JoinPoint 란 스프링이 관리하는 빈의 모든 메서드에 해당한다. (광의의 JoinPoint) (p.304)
- 호출된 객체의 메서드는 "협의의 JoinPoint" 라고 말한다. (p.305)

### Advice

- Pointcut 에 적용할 로직, 즉 메서드를 의미한다. (p.305)
- Pointcut 에 언제, 무엇을 적용할지 정의한 메서드를 말한다. (p.305)

### <aop:aspectj-autoproxy />

- 스프링이 AOP 프록시를 사용하라고 알려주는 지시자를 말한다. (p.301)
- autoproxy의 proxy는 프록시 패턴을 이용해 횡단 관심사를 핵심 관심사에 주입하는 것을 말한다. (p.300)


