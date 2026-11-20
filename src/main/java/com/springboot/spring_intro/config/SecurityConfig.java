package com.springboot.spring_intro.config;

import com.springboot.spring_intro.security.JwtAuthenticationFilter;
import com.springboot.spring_intro.security.RestAccessDeniedHandler;
import com.springboot.spring_intro.security.RestAuthenticationEntryPoint;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final UserDetailsService userDetailsService;
    private final PasswordEncoder passwordEncoder;
    private final RestAuthenticationEntryPoint restAuthenticationEntryPoint;
    private final RestAccessDeniedHandler restAccessDeniedHandler;


//    @Bean
//    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception{
//        http
//                .authorizeHttpRequests(auth->auth
//                        .requestMatchers("/api/students").permitAll()
//                        .anyRequest().authenticated()
//                );
//        return http.build();
//    }

    // Security Configuration
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception{
        http
                .csrf(csrf-> csrf.disable())
                .authorizeHttpRequests(auth->auth

                        // Render Health Check, Error & Swagger/OpenAPI
                        .requestMatchers(
                                "/",
                                "/error",
                                "/favicon.ico",
                                "/swagger-ui/**",
                                "/swagger-ui.html",
                                "/swagger-ui.html/**",
                                "/v3/api-docs/**",
                                "/v3/api-docs"
                        ).permitAll()

                        // Allow all preflight OPTIONS requests for CORS
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()

                        // Authentication API
                                .requestMatchers("/auth/login",
                                        "/auth/refresh",
                                        "/auth/logout",
                                        "/auth/otp/**")
                                .permitAll()

                                .requestMatchers(HttpMethod.GET,"/api/students",
                                        "/api/students/**")
                                .hasAnyRole("USER", "ADMIN")

                                .requestMatchers(HttpMethod.POST,
                                        "/api/students",
                                        "/api/students/**")
                                .hasRole("ADMIN")

                                .requestMatchers(HttpMethod.PUT,
                                        "/api/students/**")
                                .hasRole("ADMIN")

                                .requestMatchers(HttpMethod.PATCH,
                                        "/api/students/**")
                                .hasRole("ADMIN")

                                .requestMatchers(HttpMethod.DELETE,
                                        "/api/students/**")
                                .hasRole("ADMIN")

                        // Any Other Request Requires Authentication
                                .anyRequest()
                                .authenticated()

                )
                .exceptionHandling(exception ->
                        exception.authenticationEntryPoint(
                                restAuthenticationEntryPoint
                        )
                                .accessDeniedHandler(
                                        restAccessDeniedHandler
                                )
                )
                .sessionManagement(session ->
                        session.sessionCreationPolicy(
                                SessionCreationPolicy.STATELESS
                        )
                )
                .authenticationProvider(
                        authenticationProvider()
                )
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        // Enable Http Basic Authentication
//                .httpBasic(Customizer.withDefaults());
        return http.build();
    }

    @Bean
    protected AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider(userDetailsService);
//        provider.setUserDetailsService(
//                userDetailsService
//        );
        provider.setPasswordEncoder(
                passwordEncoder
        );
        return provider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config)
        throws Exception {
        return config.getAuthenticationManager();
    }

//    @Bean
//    public SecurityFilterChain securityFilterChain(HttpSecurity http)
//            throws Exception {
//
//        http
//                .csrf(csrf -> csrf.disable())
//                .authorizeHttpRequests(auth -> auth
//                        .requestMatchers("/auth/**").permitAll()
//                        .anyRequest().authenticated()
//                );
//
//        return http.build();
//    }

//    // In-Memory Users
//    @Bean
//    public UserDetailsService userDetailsService() {
//        // Normal User
//        UserDetails user = User.builder()
//                .username("user")
//                .password(passwordEncoder.encode("password"))
//                .roles("USER")
//                .build();
//
//        // Admin
//        UserDetails admin = User.builder()
//                .username("admin")
//                .password(passwordEncoder.encode("admin"))
//                .roles("ADMIN")
//                .build();
//
//        return new InMemoryUserDetailsManager(user, admin);
//    }

}
