package com.esprit.pi.configs;

import com.esprit.pi.repositories.UserRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfiguration {
    // Current timestamp: 2025-04-15 15:58:46
    // Current user: Zoghlamirim
    private static final String[] PUBLIC_URLS = {
            "/",
            "/error",
            "/open",
            "/login",
            "/api/open/**",
            "/auth/**",
            "/swagger-ui/**",
            "/swagger-resources",
            "/swagger-resources/**",
            "/configuration/ui",
            "/configuration/security",
            "/swagger-ui/**",
            "/webjars/**",
            "/swagger-ui.html",
            "/v3/api-docs/**"
    };

    private static final String[] HACKATHON_ENDPOINTS = {
            "/pi/api/hackathons",
            "/pi/api/hackathons/**",
            "/api/hackathons",
            "/api/hackathons/**"
    };

    private final JwtFilter jwtFilter;
    private final UserRepository userRepository;

    public SecurityConfiguration(JwtFilter jwtFilter, UserRepository userRepository) {
        this.jwtFilter = jwtFilter;
        this.userRepository = userRepository;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(request -> {
                    request
                            .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                            .requestMatchers(PUBLIC_URLS).permitAll()
                            .requestMatchers(HttpMethod.GET, HACKATHON_ENDPOINTS).permitAll()
                            .requestMatchers("/api/teams/**").permitAll()
                            .requestMatchers("/api/team-discussions/**").permitAll()
                            .requestMatchers("/api/team-members/**").permitAll()
                            .anyRequest().authenticated();
                })
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class)
                .build();
    }

    @Bean
    public CorsFilter corsFilter() {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        CorsConfiguration config = new CorsConfiguration();

        // CORS configuration
        config.setAllowCredentials(true);
        config.addAllowedOrigin("http://localhost:4200");
        config.addAllowedOrigin("http://localhost:9100");
        config.addAllowedHeader("*");
        config.addAllowedMethod("*");
        config.addExposedHeader("Authorization");

        // Add additional headers for Swagger
        config.addExposedHeader("Access-Control-Allow-Origin");
        config.addExposedHeader("Access-Control-Allow-Methods");
        config.addExposedHeader("Access-Control-Allow-Headers");

        source.registerCorsConfiguration("/**", config);
        return new CorsFilter(source);
    }

    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        return web -> web.ignoring()
                .requestMatchers(
                        "/images/**",
                        "/js/**",
                        "/default-ui.css",
                        "/swagger-ui/**",
                        "/swagger-resources/**",
                        "/v3/api-docs/**"
                );
    }

    @Bean
    public UserDetailsService userDetailsService() {
        return userName -> userRepository.findByName(userName);
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        // TODO: Replace with BCryptPasswordEncoder for production use
        return NoOpPasswordEncoder.getInstance();
    }
}