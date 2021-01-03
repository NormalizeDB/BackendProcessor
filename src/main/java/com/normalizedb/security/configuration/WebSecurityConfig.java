package com.normalizedb.security.configuration;

import com.normalizedb.security.handlers.AuthenticationFailureHandlerImpl;
import com.normalizedb.security.handlers.AuthorizationFailureHandlerImpl;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.security.access.expression.SecurityExpressionHandler;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.core.GrantedAuthorityDefaults;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.FilterInvocation;
import org.springframework.security.web.access.expression.DefaultWebSecurityExpressionHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import com.normalizedb.security.jwt.JWTGenerator;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import com.normalizedb.security.jwt.JWTValidator;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.CorsUtils;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import java.util.Arrays;

@EnableWebSecurity
@Configuration
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    @Value("${com.normalizedb.valid-domains}")
    private String[] validDomains;

    private static final String LOCALIP = "127.0.0.1/32";
    private final UserDetailsService userDetailsService;
    private final AuthenticationFailureHandlerImpl authenticationFailureHandler;
    private final AuthorizationFailureHandlerImpl authorizationFailureHandler;
    private final JWTGenerator generator;
    private final JWTValidator authorizationFilter;

    //TODO: Configure a custom expression handler
    private final SecurityExpressionHandler<FilterInvocation> expressionHandler = new DefaultWebSecurityExpressionHandler();

    public WebSecurityConfig(UserDetailsService userDetailsService,
                             AuthenticationFailureHandlerImpl authenticationFailureHandler,
                             AuthorizationFailureHandlerImpl authorizationFailureHandler,
                             JWTGenerator generator,
                             JWTValidator authorizationFilter) {
        this.userDetailsService = userDetailsService;
        this.authenticationFailureHandler = authenticationFailureHandler;
        this.authorizationFailureHandler = authorizationFailureHandler;
        this.generator = generator;
        this.authorizationFilter = authorizationFilter;
    }

    @Override
    public void configure(HttpSecurity httpSecurity) throws Exception {
        //BUILDS JWT TOKEN
        UsernamePasswordAuthenticationFilter authenticationFilter = new UsernamePasswordAuthenticationFilter();
        authenticationFilter.setAuthenticationManager(authenticationManager());
        //On failure, replace default text/html content
        authenticationFilter.setAuthenticationFailureHandler(authenticationFailureHandler);
        //On success, generate a JWT token
        authenticationFilter.setAuthenticationSuccessHandler(generator);

        httpSecurity
                .cors()
                    .configurationSource(corsConfigurationSource())
                .and()
                .csrf()
                    .disable()
                .authorizeRequests()
                    .expressionHandler(expressionHandler)
                    .antMatchers("/admin/signup").hasIpAddress(LOCALIP)
                    .antMatchers("/normalize/**").access("hasRole('ROLE_USER')")
                // A Request Matcher matches a request based on request meta-data (headers)
                    .requestMatchers(CorsUtils::isPreFlightRequest)
                        .permitAll()
                // authenticated() means that each request must have an authenticated SecurityContext
                .and()
                    .sessionManagement()
                    .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                    .exceptionHandling()
                        .authenticationEntryPoint(authenticationFailureHandler)
                        .accessDeniedHandler(authorizationFailureHandler)
                .and()
                    .addFilter(authenticationFilter)
                //DECODES JWT Token
                    .addFilterAfter(authorizationFilter,
                            UsernamePasswordAuthenticationFilter.class);
    }

    @Override
    public void configure(AuthenticationManagerBuilder authManager) throws Exception {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(userDetailsService);
        provider.setPasswordEncoder(passwordEncoder());
        provider.setForcePrincipalAsString(true);

        authManager.authenticationProvider(provider);
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        UrlBasedCorsConfigurationSource config = new UrlBasedCorsConfigurationSource();
        //Query params are not a relevant use case for special CORS configurations
        config.setRemoveSemicolonContent(true);

        CorsConfiguration globalCorsConfig = new CorsConfiguration();
        globalCorsConfig.applyPermitDefaultValues();
        globalCorsConfig.setAllowedOrigins(Arrays.asList(validDomains));
        globalCorsConfig.addAllowedMethod(CorsConfiguration.ALL);

        config.registerCorsConfiguration("/normalize/**", globalCorsConfig);
        config.registerCorsConfiguration("/login", globalCorsConfig);
        return config;
    }

}
