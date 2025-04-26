package com.esprit.pi.utility;

import com.esprit.pi.entities.User;
import com.esprit.pi.repositories.UserRepository;
import com.esprit.pi.services.JwtUtility;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Component
public class OAuth2AuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final JwtUtility jwtUtility;
    private final UserRepository userRepository;

    public OAuth2AuthenticationSuccessHandler(JwtUtility jwtUtility,
                                              UserRepository userRepository) {
        this.jwtUtility = jwtUtility;
        this.userRepository = userRepository;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication) throws IOException {
        OAuth2User oauthUser = (OAuth2User) authentication.getPrincipal();

        // 1. Map or create your local User
        String email = oauthUser.getAttribute("email");
        User user = userRepository.findByEmail(email);

        if ((user) == null) {
            User newUser = new User();
            newUser.setEmail(email);
            newUser.setName(oauthUser.getAttribute("name"));
            userRepository.save(newUser);
        }




        // 2. Issue JWT
        Map<String, String> claims = new HashMap<>();
        claims.put("userid", String.valueOf(user.getId()));
        claims.put("roles", user.getRoles().toString());
        String token = jwtUtility.generateToken(claims, user.getEmail(), 24 * 60 * 60 * 1000);

        // 3. Return as JSON
        response.setContentType("application/json");
        response.getWriter().write("{\"token\":\"" + token + "\"}");
        response.getWriter().flush();
    }
}
