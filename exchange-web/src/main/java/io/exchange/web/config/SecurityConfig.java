package io.exchange.web.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.authentication.RememberMeServices;
import org.springframework.security.web.header.writers.StaticHeadersWriter;

import io.exchange.core.config.CoreConfig;
import io.exchange.core.service.ActionLogService;
import io.exchange.domain.enums.Phase;
import io.exchange.domain.util.EnumUtils;
import io.exchange.web.config.security.CustomLoginFailureHandler;
import io.exchange.web.config.security.CustomLoginSuccessHandler;
import io.exchange.web.config.security.CustomLogoutHandler;
import io.exchange.web.config.security.CustomLogoutSuccessHandler;
import io.exchange.web.config.security.CustomRememberMeSuccessHandler;
import io.exchange.web.service.CustomRememberMeServices;
import io.exchange.web.service.CustomSessionRegistryService;
import lombok.RequiredArgsConstructor;

@Configuration
@ComponentScan(basePackages = { "io.exchange.web.security", "io.exchange.core.service", "io.exchange.core.config"})
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
@DependsOn("coreConfig")
@RequiredArgsConstructor
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    private final UserDetailsService userDetailsService;

    private final CustomLoginSuccessHandler customLoginSuccessHandler;
    private final CustomLoginFailureHandler customLoginFailureHandler;
    private final CustomLogoutHandler customLogoutHandler;
    private final CustomLogoutSuccessHandler customLogoutSuccessHandler;

    private final CustomRememberMeSuccessHandler customRememberMeSuccessHandler;
    private final CustomSessionRegistryService customSessionRegistryService;

    private final AuthenticationEntryPoint customAuthenticationEntryPoint;
    private final AccessDeniedHandler customAccessDeniedHandler;
    private final ActionLogService actionLogService;

    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    @Override
    protected void configure(final AuthenticationManagerBuilder auth) throws Exception {
        auth.authenticationProvider(authProvider())
            .userDetailsService(userDetailsService)
            .passwordEncoder(bCryptPasswordEncoder());
    }

    @Override
    public void configure(final WebSecurity web) throws Exception {
        web.ignoring().antMatchers("/resources/**", "/favicon.ico");
    }

    @Override
    protected void configure(final HttpSecurity http) throws Exception {
        http.csrf().disable()
            .authorizeRequests()

                // view allowd before login. 
                .antMatchers(HttpMethod.GET,
                        "/", "/login", "/signup", "/active", "/reset", "/notice/**").permitAll();

        http.authorizeRequests().antMatchers(HttpMethod.GET, "/api/v1.0/posts/**").permitAll();

        // local
        if (EnumUtils.hasContain(CoreConfig.currentPhase, Phase.LOCAL)) {
                http.authorizeRequests()
                .antMatchers("/h2/**").permitAll();
        }

        // dev
        if (EnumUtils.hasContain(CoreConfig.currentPhase, Phase.DEV)) {
            if(CoreConfig.isH2File) {
                http.authorizeRequests()
                .antMatchers("/h2/**").permitAll();
            }
        }

        if(EnumUtils.hasContain(CoreConfig.currentPhase, Phase.PRD)) {
            http.authorizeRequests()
            // don't "ROLE_ADMIN".
            // remove ROLE_
            .antMatchers("/actuator/**")
            .hasRole("ADMIN")
            .antMatchers("/application/**")
            .hasRole("ADMIN");
        } else {
            http.authorizeRequests()
            .antMatchers("/actuator/**")
            .permitAll()
            .antMatchers("/application/**")
            .permitAll();
        }

        http.authorizeRequests()
                /*
                 *  user api before login
                 *  
                 * .anyRequest().fullyAuthenticated() after not available.
                 * contoller layer @PreAuthorize("permitAll()")
                 */
                .antMatchers(HttpMethod.GET, "/api/**/preloads/**").permitAll()
                .antMatchers(HttpMethod.POST, "/api/**/users").permitAll()
                .antMatchers(HttpMethod.PUT, "/api/**/users/**/active").permitAll()
                .antMatchers(HttpMethod.PUT, "/api/**/users/**/resend").permitAll()
                .antMatchers(HttpMethod.PUT, "/api/**/users/**/reset").permitAll()
                .antMatchers(HttpMethod.GET, "/api/v1.0/kakaos/verify").permitAll()

                .anyRequest().fullyAuthenticated()
            .and()
                .httpBasic()
                .authenticationEntryPoint(customAuthenticationEntryPoint)
            .and()
            .formLogin()
                .loginPage("/login")
                .loginProcessingUrl("/login-check")
                .usernameParameter("email")
                .passwordParameter("pwd")
                .successHandler(customLoginSuccessHandler)
                .failureHandler(customLoginFailureHandler)
                .permitAll()
            .and()
            .logout()
                .logoutUrl("/logout")
                .deleteCookies("JSESSIONID")
                .addLogoutHandler(customLogoutHandler)
                .logoutSuccessHandler(customLogoutSuccessHandler)
            .and()
                .sessionManagement()
                .maximumSessions(1).sessionRegistry(customSessionRegistryService)
            .and()
                .sessionFixation().none()
            .and()
                .rememberMe()
                    .rememberMeParameter("remember-me-new")
                    .rememberMeServices(rememberMeServices())
                    .authenticationSuccessHandler(customRememberMeSuccessHandler)
                    .key(CoreConfig.REMEMBER_ME_KEY)
            .and()
                .exceptionHandling().authenticationEntryPoint(customAuthenticationEntryPoint)
            .and()
                .exceptionHandling()
                .accessDeniedHandler(customAccessDeniedHandler);
//             .and()
//                .exceptionHandling().accessDeniedPage("/error")

              /* h2 console  X-Frame-Options disable
               * https://java.ihoney.pe.kr/403
               */
        // local
        if (EnumUtils.hasContain(CoreConfig.currentPhase, Phase.LOCAL)) {
            http.headers().addHeaderWriter(new StaticHeadersWriter("X-Content-Security-Policy", "script-src 'self'"))
                    .frameOptions().disable();
        }
        // dev
        if (EnumUtils.hasContain(CoreConfig.currentPhase, Phase.DEV)) {
            if (CoreConfig.isH2File) {
                http.headers()
                        .addHeaderWriter(new StaticHeadersWriter("X-Content-Security-Policy", "script-src 'self'"))
                        .frameOptions().disable();
            }
        }
    }

    @Bean
    public DaoAuthenticationProvider authProvider() {
        final DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(bCryptPasswordEncoder());
        return authProvider;
    }

    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public RememberMeServices rememberMeServices() {
        CustomRememberMeServices rememberMeServices = new CustomRememberMeServices(userDetailsService, actionLogService, CoreConfig.REMEMBER_ME_KEY);
        return rememberMeServices;
    }
}