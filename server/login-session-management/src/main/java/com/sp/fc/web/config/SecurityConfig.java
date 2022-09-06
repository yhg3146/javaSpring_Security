package com.sp.fc.web.config;

import com.sp.fc.user.service.SpUserService;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.boot.web.servlet.ServletListenerRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.security.access.hierarchicalroles.RoleHierarchy;
import org.springframework.security.access.hierarchicalroles.RoleHierarchyImpl;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
<<<<<<< HEAD
import org.springframework.security.core.Authentication;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.core.session.SessionRegistryImpl;
import org.springframework.security.core.userdetails.UserDetails;
=======
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.core.session.SessionRegistryImpl;
>>>>>>> 78788ae22eccf611607554ca685cebd2e2e28fd5
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.access.AccessDeniedHandlerImpl;
import org.springframework.security.web.access.ExceptionTranslationFilter;
import org.springframework.security.web.access.intercept.FilterSecurityInterceptor;
import org.springframework.security.web.authentication.rememberme.JdbcTokenRepositoryImpl;
import org.springframework.security.web.authentication.rememberme.PersistentTokenBasedRememberMeServices;
import org.springframework.security.web.authentication.rememberme.PersistentTokenRepository;
import org.springframework.security.web.authentication.session.ConcurrentSessionControlAuthenticationStrategy;
import org.springframework.security.web.authentication.session.SessionAuthenticationStrategy;
import org.springframework.security.web.session.ConcurrentSessionFilter;
import org.springframework.security.web.session.HttpSessionEventPublisher;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSessionEvent;
import javax.sql.DataSource;
import java.time.LocalDateTime;

@EnableWebSecurity(debug = true)
@EnableGlobalMethodSecurity(prePostEnabled = true)
@ComponentScan(basePackages = {
        "com.sp.fc.user"
})
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    ExceptionTranslationFilter exceptionTranslationFilter;
    FilterSecurityInterceptor filterSecurityInterceptor;
    AccessDeniedHandlerImpl accessDeniedHandler;

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
<<<<<<< HEAD
        return registry;
    }

=======
        return  registry;
    }
>>>>>>> 78788ae22eccf611607554ca685cebd2e2e28fd5
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
<<<<<<< HEAD
                        ){
                    @Override
                    protected Authentication createSuccessfulAuthentication(HttpServletRequest request, UserDetails user) {
                        return new UsernamePasswordAuthenticationToken(user.getUsername(), user.getPassword(), null);
//                        return super.createSuccessfulAuthentication(request, user);
                    }
                };
=======
                        );
>>>>>>> 78788ae22eccf611607554ca685cebd2e2e28fd5
        service.setAlwaysRemember(true);
        return service;
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .authorizeRequests(request->
                    request
<<<<<<< HEAD
                            .antMatchers("/", "/error").permitAll()
                            .antMatchers("/admin/**").hasRole("ADMIN")
=======
                            .antMatchers("/admin/**").hasRole("ADMIN") //FilterSecurityInterceptor
                            .antMatchers("/").permitAll()
>>>>>>> 78788ae22eccf611607554ca685cebd2e2e28fd5
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
<<<<<<< HEAD
//                                .accessDeniedPage("/access-denied")
                                .accessDeniedHandler(new CustomDeniedHandler())
                                .authenticationEntryPoint(new CustomEntryPoint())

=======
                                .accessDeniedHandler(new CustomDeniedHandler())
                                .authenticationEntryPoint( new CustomEntryPoint()) //스타트 페이지로 감
//                                .accessDeniedPage("/access-denied")
>>>>>>> 78788ae22eccf611607554ca685cebd2e2e28fd5
                )
                .rememberMe(r->r
                        .rememberMeServices(rememberMeServices())
                )
                .sessionManagement(
                        s->s
<<<<<<< HEAD
//                                .sessionCreationPolicy(p-> SessionCreationPolicy.S)
                                .sessionFixation(sessionFixationConfigurer -> sessionFixationConfigurer.changeSessionId())
                        .maximumSessions(2)
                        .maxSessionsPreventsLogin(true)
                        .expiredUrl("/session-expired")
                )
                ;
=======
                                .sessionCreationPolicy(SessionCreationPolicy.ALWAYS)
//                                .sessionFixation(sessionFixationConfigurer -> sessionFixationConfigurer.none())
                                .sessionFixation(sessionFixationConfigurer -> sessionFixationConfigurer.changeSessionId())
                                .maximumSessions(1)
                                .maxSessionsPreventsLogin(false)
                                .expiredUrl("/session-expired")
                );
>>>>>>> 78788ae22eccf611607554ca685cebd2e2e28fd5
    }

    @Override
    public void configure(WebSecurity web) throws Exception {
        web.ignoring()
<<<<<<< HEAD
                .antMatchers("/sessions", "/session/expire", "/session-expired")
=======
                .antMatchers("/sessions","/session/expire","/session-expired")
>>>>>>> 78788ae22eccf611607554ca685cebd2e2e28fd5
                .requestMatchers(
                        PathRequest.toStaticResources().atCommonLocations(),
                        PathRequest.toH2Console()
                )
        ;
    }

}
