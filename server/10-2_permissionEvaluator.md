### HasPermission Custom 하기
#### public class CustomPermissionEvaluator implements PermissionEvaluator </br></br></br>

### HasPermission 사용 예제
#### @PreAuthorize("hasPermission(#paperId, 'paper', 'read')")
####    @GetMapping("/get/{paperId}")</br></br></br>

### 예제
```java
@EnableGlobalMethodSecurity(prePostEnabled = true,securedEnabled = true)
public class MethodSecurityConfiguration extends GlobalMethodSecurityConfiguration {

    @Autowired
    private CustomPermissionEvaluator permissionEvaluator; //허락 평가 Custom

    @Override
    protected MethodSecurityExpressionHandler createExpressionHandler() {
     //       MethodSecurityExpressionHandler handler = new DefaultMethodSecurityExpressionHandler();//PermissionEvaluator 사용할 시에 사용 못DefaultMethod...로 변 경
        DefaultMethodSecurityExpressionHandler handler = new DefaultMethodSecurityExpressionHandler(){
            @Override
            protected MethodSecurityExpressionOperations createSecurityExpressionRoot(Authentication authentication, MethodInvocation invocation) {
            //                return new CustomMethodSecurityExpressionRoot(authentication,invocation);
                // root 설정
                CustomMethodSecurityExpressionRoot root = new CustomMethodSecurityExpressionRoot(authentication, invocation);
                root.setPermissionEvaluator(getPermissionEvaluator());
                return root;
            }
        };
        handler.setPermissionEvaluator(permissionEvaluator);
        return handler;
    }
}
```
```java
@Component
public class CustomPermissionEvaluator implements PermissionEvaluator {

    @Autowired
    private PaperService paperService;

    @Override
    public boolean hasPermission(Authentication authentication, Object targetDomainObject, Object permission) {
        return false;
    }

    @Override
    public boolean hasPermission(Authentication authentication,
                                 Serializable targetId,
                                 String targetType,
                                 Object permission) {
        Paper paper = paperService.getPaper((long)targetId);
        if(paper == null) throw new AccessDeniedException("시험지가 존재하지 않음.");


        if(paper.getState() == Paper.State.PREPARE) return false;

        boolean canUse = paper.getStudentIds().stream().filter(userId -> userId.equals(authentication.getName()))
                .findAny().isPresent();

        return canUse;
    }
}
```


```java
 @PreAuthorize("hasPermission(#paperId, 'paper', 'read')")
    @GetMapping("/get/{paperId}")
    public Paper getPaper(@AuthenticationPrincipal User user, @PathVariable Long paperId){
        return paperService.getPaper(paperId);
    }
```
```java
    @DisplayName("3. user2 라도 출제중인 시험지에는 접근할 수 없다.")
    @Test
    void test_3() {
        paperService.setPaper(paper2);
        client = new TestRestTemplate("user2", "1111");
        ResponseEntity<Paper> response = client.exchange(uri("/paper/get/2"),
                HttpMethod.GET, null, new ParameterizedTypeReference<Paper>() {
                });

        assertEquals(403, response.getStatusCodeValue());
    }
```
