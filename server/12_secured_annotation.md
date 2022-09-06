## 1.일반적인 Secured 사용

### @Secured 사용
#### @Secured({"SCHOOL_PRIMARY"})<br/><br/><br/>

### Secured를 처리해 줄 DataSource와 Voter 메서드 생성
#### public class MethodSecurityConfiguration extends GlobalMethodSecurityConfiguration
#### Datasource -> protected MethodSecurityMetadataSource customMethodSecurityMetadataSource
#### Voter -> protected AccessDecisionManager accessDecisionManager()<br/><br/><br/>

### CustomMetaDatasource 생성
#### CustomMetadataSource implements MethodSecurityMetadataSource<br/><br/><br/>

### CustomVoter 생성
#### public class CustomVoter implements AccessDecisionVoter<MethodInvocation><br/><br/><br/>

## 2.@secured 대신 Custom한 Innotation 사용
### CustomSecyrityTag 생성
#### public @interface CustomSecurityTag<br/><br/><br/>

### CustomMetadata에 Custom한 annotation 적용
#### public Collection<ConfigAttribute> getAttributes(Method method, Class<?> targetClass)
#### private   <A extends Annotation> A findAnnotation(Method method, Class<?> targetClass, Class<A> annotationClass)<br/><br/><br/>


### Paper Controller에 적용
#### @CustomSecurityTag("SCHOOL_PRIMARY")<br/><br/><br/>


### 코드예제<br/><br/><br/>

```java


//securedEnable만 추가할 시 에러가 남.securedEnable을 처리해줄 곳이 없기 때문.처리해줄 보터 생성->customVoter
@EnableGlobalMethodSecurity(prePostEnabled = true,securedEnabled = true)
public class MethodSecurityConfiguration extends GlobalMethodSecurityConfiguration {

    @Autowired
    private CustomPermissionEvaluator permissionEvaluator;

    @Override
    protected MethodSecurityMetadataSource customMethodSecurityMetadataSource() {
        return new CustomMetadataSource(); //내가 만든 CustomMetadaSource로 보냄
    }

    @Override
    protected MethodSecurityExpressionHandler createExpressionHandler() {
        DefaultMethodSecurityExpressionHandler handler = new DefaultMethodSecurityExpressionHandler(){
            @Override
            protected MethodSecurityExpressionOperations createSecurityExpressionRoot(Authentication authentication, MethodInvocation invocation) {
                CustomMethodSecurityExpressionRoot root = new CustomMethodSecurityExpressionRoot(authentication, invocation);
                root.setPermissionEvaluator(getPermissionEvaluator());
                return root;
            }
        };
        handler.setPermissionEvaluator(permissionEvaluator);
        return handler;
    }

    @Override
    protected AccessDecisionManager accessDecisionManager() {
        List<AccessDecisionVoter<?>> decisionVoters = new ArrayList<>();
        ExpressionBasedPreInvocationAdvice expressionAdvice = new ExpressionBasedPreInvocationAdvice();
        expressionAdvice.setExpressionHandler(getExpressionHandler());

        decisionVoters.add(new PreInvocationAuthorizationAdviceVoter(expressionAdvice));
        decisionVoters.add(new RoleVoter());
        decisionVoters.add(new AuthenticatedVoter());
        decisionVoters.add(new CustomVoter());

        return new AffirmativeBased(decisionVoters);
//        ConsensusBased committee = new ConsensusBased(decisionVoters);
//        committee.setAllowIfEqualGrantedDeniedDecisions(false);
//        return committee;
    }

}
```
```java
public class CustomMetadataSource implements MethodSecurityMetadataSource {

    SecuredAnnotationSecurityMetadataSource securedAnnotationSecurityMetadataSource;
    PrePostAnnotationSecurityMetadataSource prePostAnnotationSecurityMetadataSource;


    @Override
    public Collection<ConfigAttribute> getAttributes(Method method, Class<?> targetClass) {
       //Custom한 annotation 적용
         CustomSecurityTag annotation = findAnnotation(method,targetClass,CustomSecurityTag.class);
       if(annotation != null){
           return List.of(new SecurityConfig(annotation.value()));
       }
        /*if(method.getName().equals("getPapersByPrimary") && targetClass == PaperController.class){
            return List.of(new SecurityConfig("SCHOOL_PRIMARY")); //PaperController에 @Secured가 있지 않더라도 CustomVoter에서 보터를 찾아옴.
        }*/
        return null;
    }

    @Override
    public Collection<ConfigAttribute> getAttributes(Object object) throws IllegalArgumentException {
        return null;
    }

    @Override
    public Collection<ConfigAttribute> getAllConfigAttributes() {
        return null;
    }

    @Override
    public boolean supports(Class<?> clazz) {
        return MethodInvocation.class.isAssignableFrom(clazz);
    }

    //@Secured가 아닌 우리가 지정한 annotation으로 만들기
    private   <A extends Annotation> A findAnnotation(Method method, Class<?> targetClass, Class<A> annotationClass) {

        Method specificMethod = ClassUtils.getMostSpecificMethod(method, targetClass);
        A annotation = AnnotationUtils.findAnnotation(specificMethod, annotationClass);
        return annotation;
    }
}
```
```java
public class CustomVoter implements AccessDecisionVoter<MethodInvocation> {
    private  final String PREFIX ="SCHOOL_";

    @Override
    public boolean supports(ConfigAttribute attribute)
    {
        return attribute.getAttribute().startsWith(PREFIX);
    }

    @Override
    public boolean supports(Class<?> clazz) {
        return MethodInvocation.class.isAssignableFrom(clazz);
    }

    @Override
    public int vote(Authentication authentication, MethodInvocation object, Collection<ConfigAttribute> attributes) {

        String role = attributes.stream().filter(attr->attr.getAttribute().startsWith(PREFIX)).map(
                attr->attr.getAttribute().substring(PREFIX.length())).findFirst().get();

        if(authentication.getAuthorities().stream().filter(auth->auth.getAuthority().equals("ROLE_"+role.toUpperCase()))
                .findAny().isPresent()){
            return ACCESS_GRANTED;
        }
        return ACCESS_DENIED;
    }
}
```
```java
@Target({ ElementType.METHOD, ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
public @interface CustomSecurityTag {

    String value();
}
```
```java
//    @Secured({"SCHOOL_PRIMARY"})
    @CustomSecurityTag("SCHOOL_PRIMARY")
    @GetMapping("/getPapersByPrimary")
    public List<Paper> getPapersByPrimary(@AuthenticationPrincipal User user){
        return paperService.getAllPapers();
    }
```

