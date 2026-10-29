package com.example.timeoff.security;

import com.example.timeoff.domain.User;
import com.example.timeoff.domain.UserRepository;
import java.util.Optional;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final TokenService tokenService;

    AuthService(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            TokenService tokenService
    ) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.tokenService = tokenService;
    }

    // Unknown user and wrong password give the same error, so a caller cannot tell which usernames exist.
    @Transactional(readOnly = true)
    public LoginResult login(String username, String password) {
        Optional<User> found = userRepository.findByUsername(username);
        boolean passwordMatches = found
                .map(user -> passwordEncoder.matches(password, user.getPasswordHash()))
                .orElse(false);

        if (!passwordMatches) {
            throw new InvalidLoginException();
        }

        User user = found.get();
        String token = tokenService.issue(user);

        return new LoginResult(token, UserView.of(user));
    }

    @Transactional(readOnly = true)
    public UserView me(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow();

        return UserView.of(user);
    }
}
