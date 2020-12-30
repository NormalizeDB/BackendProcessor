package com.normalizedb.security.configuration;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import com.normalizedb.security.jwt.JWTGenerator;
import com.normalizedb.handlers.GlobalExceptionHandler;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
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

    private final UserDetailsService userDetailsService;

    public WebSecurityConfig(UserDetailsService userDetailsService) {
        this.userDetailsService = userDetailsService;
    }

    @Override
    public void configure(HttpSecurity httpSecurity) throws Exception {
        //BUILDS JWT TOKEN
        UsernamePasswordAuthenticationFilter authenticationFilter = new UsernamePasswordAuthenticationFilter();
        authenticationFilter.setAuthenticationManager(authenticationManager());
        authenticationFilter.setAuthenticationSuccessHandler(new JWTGenerator());

        //DECODES JWT TOKEN
        BasicAuthenticationFilter authorizationFilter = new JWTValidator(authenticationManager());

        httpSecurity
                .cors()
                    .configurationSource(corsConfigurationSource())
                .and()
                .csrf()
                    .disable()
                .authorizeRequests()
                .requestMatchers(CorsUtils::isPreFlightRequest).permitAll()
                .anyRequest().authenticated()
                .and()
                    .sessionManagement()
                    .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                    .exceptionHandling()
                        .authenticationEntryPoint(new GlobalExceptionHandler())
                        .accessDeniedHandler(new GlobalExceptionHandler())
                .and()
                    .addFilter(authenticationFilter)
                    .addFilterAfter(authorizationFilter, UsernamePasswordAuthenticationFilter.class);
    }

    @Override
    public void configure(AuthenticationManagerBuilder authManager) throws Exception {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setSaltSource(new PasswordEncoderSalt());
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

        config.registerCorsConfiguration("/**", globalCorsConfig);
        return config;
    }

}
