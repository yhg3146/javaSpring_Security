### httpSecurity에 Access 추가
#### .mvcMatchers("/greeting/{name}").access("@nameCheck.check(#name)")<br/><br/><br/>
### 예제

```java
@Component
public class NameCheck {


    public boolean check(String name){
        return name.equals("jongwon");
    }

}
```

```java

@Service
public class SecurityMessageService {


//    @PreAuthorize("hasRole('USER')")
    @PreAuthorize("@nameCheck.check(#name)") // httpSecurity뿐만 아니라 Controller에서도 사용 가능
    public String message(String name){
        return name;
    }

}

```
```java

@RestController
public class HomeController {

    MethodSecurityInterceptor methodSecurityInterceptor;

    private final SecurityMessageService securityMessageService;

    public HomeController(SecurityMessageService securityMessageService) {
        this.securityMessageService = securityMessageService;
    }

    @GetMapping("/greeting/{name}")
    public String greeting(@PathVariable String name){
        return "hello "+securityMessageService.message(name);
    }

}
```
```java

@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {


    @Autowired
    private NameCheck nameCheck;

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .csrf().disable()
                .httpBasic().and()
                .authorizeRequests(
                        authority->authority
                                .mvcMatchers("/greeting/{name}")
                                    .access("@nameCheck.check(#name)")
                                .anyRequest().authenticated()
//                        .accessDecisionManager(filterAccessDecisionManager())
                )
                ;
    }
}
```
