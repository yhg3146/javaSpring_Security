### [커스텀 인증 세부사항 클래스 생성](CustomAuthDetails.java)
#### public class CustomAuthDetails implements AuthenticationDetailsSource<HttpServletRequest, RequestInfo><br/><br/><br/>

```java

@Component
public class CustomAuthDetails implements AuthenticationDetailsSource<HttpServletRequest, RequestInfo> {
    @Override
    public RequestInfo buildDetails(HttpServletRequest request) {
        return RequestInfo.builder()
                .remoteIp(request.getRemoteAddr())
                .sessionId(request.getSession().getId())
                .loginTime(LocalDateTime.now())
                .build();
    }
}

```
