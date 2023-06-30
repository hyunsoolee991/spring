## 애노테이션

- @RequestParam
  - ```java
    @ResponseBody
    @RequestMapping("/request-param-default")
    public String requestParamDefault(
          @RequestParam(required = true, defaultValue = "guest") String username,
          @RequestParam(required = false, defaultValue = "-1") int age
    ) {
    
          log.info("username={}, age={}", username, age);
          return "ok";
    }
    ```
  - defaultValue 옵션을 추가하면 required 옵션은 없어도 된다.
  - 파라미터의 값이 빈 문자열이라면 defaultValue가 적용된다.

## HttpEntity

- 메세지 바디 정보를 직접 조회해준다.
- 요청 파라미터를 조회하는 기능과 관계가 없다. '@RequestParam', '@ModelAttribute'
- 응답에도 사용 가능하다.
- 메세지 바디 정보 직접 반환
- 헤더 저보 포함 가능
- view를 조회하지 않는다.

## 요청 파라미터

- GET 요청으로 URL에 쿼리 스트링으로 요청하는 방식
- HTML Form에서 Post으로 요청하는 방식