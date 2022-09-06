### [Session 만들기](./src/main/java/com/sp/fc/web/controller)<br/><br/>

### customDenied 생성
#### public class CustomDeniedHandler implements AccessDeniedHandler
#### YouCannotAccessUserPage 클래스 생성<br/><br/><br/>

### customEntryPoint 생성
#### public class CustomEntryPoint implements AuthenticationEntryPoint
#### request.getRequestDispathcer 사용<br/><br/><br/>

### Configure 설정 추가
#### .exceptionHandling(error-> error
####                             .accessDeniedHandler(new CustomDeniedHandler())
####                              .authenticationEntryPoint( new CustomEntryPoint())<br/><br/><br/>
                                

```java
@EnableWebSecurity(debug = true)
@EnableGlobalMethodSecurity(prePostEnabled = true)
@ComponentScan(basePackages = {
        "com.sp.fc.user"
})
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    private final SpUserService spUserService;
    private final DataSource dataSource;

    public SecurityConfig(SpUserService spUserService, DataSource dataSource) {
        this.spUserService = spUserService;
        this.dataSource = dataSource;
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(spUserService);
    }

    @Bean
    PasswordEncoder passwordEncoder(){
        return NoOpPasswordEncoder.getInstance();
    }

    @Bean
    RoleHierarchy roleHierarchy(){
        RoleHierarchyImpl roleHierarchy = new RoleHierarchyImpl();
        roleHierarchy.setHierarchy("ROLE_ADMIN > ROLE_USER");
        return roleHierarchy;
    }

    @Bean
    public ServletListenerRegistrationBean<HttpSessionEventPublisher> httpSessionEventPublisher() {
        return new ServletListenerRegistrationBean<HttpSessionEventPublisher>(new HttpSessionEventPublisher(){
            @Override
            public void sessionCreated(HttpSessionEvent event) {
                super.sessionCreated(event);
                System.out.printf("===>> [%s] 세션 생성됨 %s \n", LocalDateTime.now(), event.getSession().getId());
            }

            @Override
            public void sessionDestroyed(HttpSessionEvent event) {
                super.sessionDestroyed(event);
                System.out.printf("===>> [%s] 세션 만료됨 %s \n", LocalDateTime.now(), event.getSession().getId());
            }

            @Override
            public void sessionIdChanged(HttpSessionEvent event, String oldSessionId) {
                super.sessionIdChanged(event, oldSessionId);
                System.out.printf("===>> [%s] 세션 아이디 변경  %s:%s \n",  LocalDateTime.now(), oldSessionId, event.getSession().getId());
            }
        });
    }

    @Bean
    SessionRegistry sessionRegistry(){
        SessionRegistryImpl registry = new SessionRegistryImpl();
        return  registry;
    }
    @Bean
    PersistentTokenRepository tokenRepository(){
        JdbcTokenRepositoryImpl repository = new JdbcTokenRepositoryImpl();
        repository.setDataSource(dataSource);
        try{
            repository.removeUserTokens("1");
        }catch(Exception ex){
            repository.setCreateTableOnStartup(true);
        }
        return repository;
    }

    @Bean
    PersistentTokenBasedRememberMeServices rememberMeServices(){
        PersistentTokenBasedRememberMeServices service =
                new PersistentTokenBasedRememberMeServices("hello",
                        spUserService,
                        tokenRepository()
                        );
        service.setAlwaysRemember(true);
        return service;
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .authorizeRequests(request->
                    request
                            .antMatchers("/admin/**").hasRole("ADMIN") //FilterSecurityInterceptor
                            .antMatchers("/").permitAll()
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
                        error
                                .accessDeniedHandler(new CustomDeniedHandler())
                                .authenticationEntryPoint( new CustomEntryPoint()) //스타트 페이지로 감
//                                .accessDeniedPage("/access-denied")
                )
                .rememberMe(r->r
                        .rememberMeServices(rememberMeServices())
                )
                .sessionManagement(
                        s->s
                                .sessionCreationPolicy(SessionCreationPolicy.ALWAYS)
//                                .sessionFixation(sessionFixationConfigurer -> sessionFixationConfigurer.none())
                                .sessionFixation(sessionFixationConfigurer -> sessionFixationConfigurer.changeSessionId())
                                .maximumSessions(1)
                                .maxSessionsPreventsLogin(false)
                                .expiredUrl("/session-expired")
                );
    }

    @Override
    public void configure(WebSecurity web) throws Exception {
        web.ignoring()
                .antMatchers("/sessions","/session/expire","/session-expired")
                .requestMatchers(
                        PathRequest.toStaticResources().atCommonLocations(),
                        PathRequest.toH2Console()
                )
        ;
    }

}
```
