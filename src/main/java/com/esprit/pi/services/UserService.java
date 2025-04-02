package com.esprit.pi.services;

import com.esprit.pi.entities.PasswordResetToken;
import com.esprit.pi.entities.User;
import com.esprit.pi.repositories.PasswordResetTokenRepository;
import com.esprit.pi.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserService implements IUserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordResetTokenRepository passwordTokenRepository;

    private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    @Autowired
    private PasswordResetTokenRepository passwordResetTokenRepository;

    @Override
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @Override
    public Optional<User> getUserById(Long id) {
        return userRepository.findById(id);
    }

    @Override
    public User createUser(User user) {
        return userRepository.save(user);
    }


    @Override
    public User updateUser(Long id, User user) {
        return userRepository.findById(id).map(existingUser -> {
            existingUser.setUsername(user.getUsername());
            existingUser.setEmail(user.getEmail());
            existingUser.setRoles(user.getRoles());
            existingUser.setPassword(user.getPassword());
            return userRepository.save(existingUser);
        }).orElse(null);
    }

    @Override
    public void deleteUser(Long id) {

    }

    public void changeUserPassword(User user, String password) {
        user.setPassword(passwordEncoder.encode(password));
        userRepository.save(user);
    }


    public User findUserByEmail(String email) {
        String cleanedEmail = email.trim().replaceAll("[\\r\\n]+", "");
        System.out.println("Looking for email: " + cleanedEmail);

        User user = userRepository.findByEmail(cleanedEmail.toLowerCase());

        if (user == null) {
            System.out.println("User with email " + cleanedEmail + " not found!");
        } else {
            System.out.println("User found: " + user.getEmail());
        }
        return user;
    }



    public void createPasswordResetTokenForUser(User user, String token) {
        PasswordResetToken myToken = new PasswordResetToken(token, user);
        passwordTokenRepository.save(myToken);
    }

    public User getUserByPasswordResetToken(String token) {
        PasswordResetToken tokenObj = passwordResetTokenRepository.findByToken(token);
        return userRepository.findById(tokenObj.getUser().getId()).orElse(null);
    }
}