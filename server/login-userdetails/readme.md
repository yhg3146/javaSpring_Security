### [SpUser와 SpAuthortiy를 클래스 생성](../../comp/user-admin/src/main/java/com/sp/fc/user/domain/)<br/><br/>

### [SpUser DB 생성](../../comp/user-admin/src/main/java/com/sp/fc/user/repository/)<br/><br/>

### [SpUser를 이용한 Service 생성](../../comp/user-admin/src/main/java/com/sp/fc/user/service/)<br/><br/>

### application.yml 설정<br/><br/>

### Spuser를 SecurityConfig에서 사용<br></br>


## application.yml
```java
server:
  port: 9055

spring:
  devtools:
    livereload:
      enabled: true
    restart:
      enabled: true

  thymeleaf:
    prefix: classpath:/templates/
    cache: false
    check-template-location: true

  h2:
    console:
      enabled: true
      path: /h2-console

  datasource:
    url: jdbc:h2:mem:userdetails-test;
    driverClassName: org.h2.Driver
    username: sa
    password:

  jpa:
    database-platform: org.hibernate.dialect.H2Dialect

```

## SecurityConfig
```java
@EnableWebSecurity(debug = true)
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    private final SpUserService spUserService;

    public SecurityConfig(SpUserService spUserService) {
        this.spUserService = spUserService;
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(spUserService);
    }

    @Bean
    PasswordEncoder passwordEncoder(){
        return NoOpPasswordEncoder.getInstance();
    }


    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .authorizeRequests(request->
                    request.antMatchers("/").permitAll()
                            .anyRequest().authenticated()
                )
                .formLogin(login->
                        login.loginPage("/login")
                        .loginProcessingUrl("/loginprocess")
                        .permitAll()
                        .defaultSuccessUrl("/", false)
                        .failureUrl("/login-error")
                )
                .logout(logout->
                        logout.logoutSuccessUrl("/"))
                .exceptionHandling(error->
                        error.accessDeniedPage("/access-denied")
                )
                ;
    }

    @Override
    public void configure(WebSecurity web) throws Exception {
        web.ignoring()
                .requestMatchers(
                        PathRequest.toStaticResources().atCommonLocations(),
                        PathRequest.toH2Console()
                )
        ;
    }

}

```
