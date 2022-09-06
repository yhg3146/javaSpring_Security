### SecurityExpressionRoot에 Method 추가
#### public class CustomMethodSecurityExpressionRoot extends SecurityExpressionRoot /
#### implements MethodSecurityExpressionOperations{
####  public boolean notPrePareState(Paper paper)}<br/><br/><br/>

### Controller에 구현
#### @PostFilter("notPrePareState(filterObject) && filterObject.studentIds.contains(#user.username)")<br/><br/><br/>

### Service에도 구현(웬만하면 Controller에 구현)<br/><br/><br/>

###코드 예시
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

    public boolean isStudent() {
        return getAuthentication().getAuthorities().stream()
                .filter(a -> a.getAuthority().equals("ROLE_STUDENT"))
                .findAny().isPresent();
    }

    public boolean isTutor() {
        return getAuthentication().getAuthorities().stream()
                .filter(a -> a.getAuthority().equals("ROLE_TUTOR"))
                .findAny().isPresent();
    }
    //post 사용
    public boolean notPrePareState(Paper paper) {
        return paper.getState() != com.sp.fc.web.service.Paper.State.PREPARE;
    }

    @Override
    public Object getThis() {
        return this;
    }
}
```
```java
@RequestMapping("/paper")
@RestController
public class PaperController {

    @Autowired
    private PaperService paperService;

//    @PreAuthorize("isStudent()")
@PostFilter("notPrePareState(filterObject) && filterObject.studentIds.contains(#user.username)")
    @GetMapping("/mypapers")
    public List<Paper> myPapers(@AuthenticationPrincipal User user){
        return paperService.getMyPapers(user.getUsername());
    }

//    @PreAuthorize("hasPermission(#paperId, 'paper', 'read')")//postAuthorize로 구현하는게 더 자연스러움
    @PostAuthorize("returnObject.studentIds.contains(#user.username)")
    @GetMapping("/get/{paperId}")
    public Paper getPaper(@AuthenticationPrincipal User user, @PathVariable Long paperId){
        return paperService.getPaper(paperId);
    }

}
```
```java
@Service
public class PaperService implements InitializingBean {

    private HashMap<Long, Paper> paperDB = new HashMap<>();

    @Override
    public void afterPropertiesSet() throws Exception {

    }

    public void setPaper(Paper paper){
        paperDB.put(paper.getPaperId(), paper);
    }

    @PostFilter("notPrePareState(filterObject)") //그냥 하면 paperService 라이프 사이클 때문에 오류남. CustomPermissionEvaluator에서 @autowired에서 @Lazy로 변경
    public List<Paper> getMyPapers(String username) {
//       return paperDB.values().stream().collect(Collectors.toList());

         return paperDB.values().stream().filter(
                paper -> paper.getStudentIds().contains(username)
        ).collect(Collectors.toList());
    }

//    @PostAuthorize("returnObject.studentIds.contains(#user.username)")//이걸로 하면 안됨
//user는 Authentication.principal에 있는 user를 가져오기 때문에 username대신 principal
@PostAuthorize("returnObject.studentIds.contains(#principal.username)")

public Paper getPaper(Long paperId) {
        return paperDB.get(paperId);
    }//List가 아닌경우는 PostAuthorize 검사. List인 경우는 PostFilter사용
}
```
