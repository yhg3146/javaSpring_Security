### Permision Root 생성
#### public class MethodSecurityConfiguration extends GlobalMethodSecurityConfiguration {}
#### -> protected MethodSecurityExpressionHandler createExpressionHandler()<br/><br/><br/>

### 허락평가 Custom 하기
#### public class CustomMethodSecurityExpressionRoot extends SecurityExpressionRoot /
####    implements MethodSecurityExpressionOperations{}<br/><br/><br/>

### Custom된 권한 사용
#### @PreAuthorize("isStudent()")<br/><br/><br/>


### 예제

```java

@EnableGlobalMethodSecurity(prePostEnabled = true)
public class MethodSecurityConfiguration extends GlobalMethodSecurityConfiguration {

    @Autowired
    private CustomPermissionEvaluator permissionEvaluator; //허락 평가 Custom

 @Override
    protected MethodSecurityExpressionHandler createExpressionHandler() {
        MethodSecurityExpressionHandler handler = new DefaultMethodSecurityExpressionHandler(){
            @Override
            protected MethodSecurityExpressionOperations createSecurityExpressionRoot(Authentication authentication, MethodInvocation invocation) {
                return  new CustomMethodSecurityExpressionRoot(authentication,invocation);
            };
        };
        return handler;
    }


}
```

```java

@Getter
@Setter
public class CustomMethodSecurityExpressionRoot extends SecurityExpressionRoot
    implements MethodSecurityExpressionOperations {

    MethodInvocation invocation;

    public CustomMethodSecurityExpressionRoot(Authentication authentication, MethodInvocation invocation) {
        super(authentication);
        this.invocation = invocation;
    }

    private Object filterObject;
    private Object returnObject;

    public boolean isStudent(){
        return getAuthentication().getAuthorities().stream()
                .filter(a->a.getAuthority().equals("ROLE_STUDENT"))
                .findAny().isPresent();
    }

    public boolean isTutor(){
        return getAuthentication().getAuthorities().stream()
                .filter(a->a.getAuthority().equals("ROLE_TUTOR"))
                .findAny().isPresent();
    }

    @Override
    public Object getThis() {
        return this;
    }
}
```
```java
    @PreAuthorize("isStudent()")
    @GetMapping("/mypapers")
    public List<Paper> myPapers(@AuthenticationPrincipal User user){
        return paperService.getMyPapers(user.getUsername());
    }
    ```

