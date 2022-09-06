### 사용자 추가 
#### protected void configure(AuthenticationManagerBuilder auth)<br/><br/><br/>

### 권한 상속
#### RoleHierarchy roleHierarchy()<br/><br/><br/>

### [사용자 인증  customAuthDetails](./src/main/java/com/sp/fc/web/config)<br/><br/><br/>

### css 인증 제거
#### public void configure(WebSecurity web)<br/><br/><br/>

```java
public class SecurityConfig extends WebSecurityConfigurerAdapter {
   
   
    private final CustomAuthDetails customAuthDetails;

    public SecurityConfig(CustomAuthDetails customAuthDetails) {
        this.customAuthDetails = customAuthDetails;
    }

    
    
    
    //사용자 추가
    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth
                .inMemoryAuthentication()
                .withUser(
                        User.withDefaultPasswordEncoder()
                                .username("user1")
                                .password("1111")
                                .roles("USER")
                ).withUser(
                User.withDefaultPasswordEncoder()
                        .username("admin")
                        .password("2222")
                        .roles("ADMIN")
        );
    }




    //권한 상속 ADMIN이 USER권한도 포함되게 함
    @Bean
    RoleHierarchy roleHierarchy(){
        RoleHierarchyImpl roleHierarchy = new RoleHierarchyImpl();
        roleHierarchy.setHierarchy("ROLE_ADMIN > ROLE_USER");
        return roleHierarchy;
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
                        //인증요소 자세히 보기(Ip와 세션아이디 등등)
                        .authenticationDetailsSource(customAuthDetails)
                        .failureUrl("/login-error")
                )
                .logout(logout->
                        logout.logoutSuccessUrl("/"))
                .exceptionHandling(error->
                        error.accessDeniedPage("/access-denied")
                )
                ;
    }
    
    
    
    
    //css 웹 인증 제거
    @Override
    public void configure(WebSecurity web) throws Exception {
        web.ignoring()
                .requestMatchers(
                        PathRequest.toStaticResources().atCommonLocations()
                )
        ;
    }

}
```
