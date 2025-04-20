package com.esprit.pi.configs;

import com.esprit.pi.repositories.UserRepository;
import com.esprit.pi.services.JwtUtility;
import com.esprit.pi.utility.OAuth2AuthenticationSuccessHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.crypto.SecretKey;

@Configuration
@EnableWebSecurity
public class SecurityConfiguration {

    JwtFilter jwtFilter;
    UserRepository userRepository;

    @Autowired
    private final OAuth2AuthenticationSuccessHandler successHandler;

    public SecurityConfiguration(JwtFilter jwtFilter, UserRepository userRepository, OAuth2AuthenticationSuccessHandler successHandler) {
        // Auto inject dependent beans
        this.jwtFilter = jwtFilter;
        this.userRepository = userRepository;
        this.successHandler = successHandler;
    }


    @Bean
    @Order(1)
    public SecurityFilterChain basicAuthSecurityFilterChain(HttpSecurity http) throws Exception {
        return http
                .cors()
                .and()
                .securityMatcher("/api/**", "/auth/**","/**","/login/**")
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(request -> {
                    request.requestMatchers("/api/open/**", "/auth/**","/**","/login/**").permitAll();
                    request.anyRequest().authenticated();
                })
                .oauth2ResourceServer(rs -> rs.jwt())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class)
                .build();
    }



    // Security Filter Chain for Web Pages
    @Bean
    @Order(2)
    public SecurityFilterChain formSecurityFilterChain(HttpSecurity http) throws Exception {
        // Form Login for Web Pages
        return http.authorizeHttpRequests(request -> {
                    request.requestMatchers("/").permitAll();
                    request.requestMatchers("/error").permitAll();
                    request.requestMatchers("/open").permitAll();
                    request.anyRequest().authenticated();
                })
                .formLogin((formLoginConfig) -> formLoginConfig.defaultSuccessUrl("/protected", true))
                .logout(logoutConfig -> logoutConfig.logoutSuccessUrl("/"))
                .build();
    }

    // Ignore selected URIs from security checks
    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        // Ignore static directories from Security Filter Chain
        return web -> web.ignoring().requestMatchers("/images/**", "/js/**");
    }

    // Custom User Details Service to manage login
    @Bean
    public UserDetailsService userDetailsService() {
        UserDetailsService userDetailsService = (userName) -> {
            return userRepository.findByName(userName);
        };
        return userDetailsService;
    }

    // Password encoder
    @Bean
    public PasswordEncoder getPasswordEncoder() {
        return NoOpPasswordEncoder.getInstance();
    }

    @Bean
    public JwtDecoder jwtDecoder(JwtUtility jwtUtility) {
        SecretKey key = jwtUtility.getSignInKey(); // you'll need to make getSignInKey() public
        return NimbusJwtDecoder.withSecretKey(key).build();
    }
}
