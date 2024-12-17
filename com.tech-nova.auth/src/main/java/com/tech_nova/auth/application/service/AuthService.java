package com.tech_nova.auth.application.service;

import com.tech_nova.auth.application.dto.UserDto;
import com.tech_nova.auth.domain.model.User;
import com.tech_nova.auth.domain.model.UserRole;
import com.tech_nova.auth.domain.repository.UserRepository;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class AuthService {

    @Value("${spring.application.name}")
    private String issuer;

    @Value("${service.jwt.access-expiration}")
    private Long accessExpiration;

    private final SecretKey secretKey;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthService(@Value("${service.jwt.secret-key}") String secretKey,
                       UserRepository userRepository,
                       PasswordEncoder passwordEncoder) {
        this.secretKey = Keys.hmacShaKeyFor(Decoders.BASE64URL.decode(secretKey));
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public String createAccessToken(UUID userId, String role) {
        System.out.println("userId: " + userId);
        System.out.println("role: " + role);
        return Jwts.builder()
                .claim("user_id", userId)
                .claim("role", role)
                .issuer(issuer)
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + accessExpiration))
                .signWith(secretKey, io.jsonwebtoken.SignatureAlgorithm.HS512)
                .compact();
    }

    public void signUp(UserDto userDto) {
        String username = userDto.getUsername();
        if (!isValidUsername(username)) {
            throw new IllegalArgumentException("Username은 최소 4자 이상, 10자 이하이며 알파벳 소문자(a~z)와 숫자(0~9)만 포함해야 합니다.");
        }

        String password = userDto.getPassword();
        if (!isValidPassword(password)) {
            throw new IllegalArgumentException("Password는 최소 8자 이상, 15자 이하이며 알파벳 대소문자, 숫자, 특수문자가 포함되어야 합니다.");
        }

        String encryptedPassword = passwordEncoder.encode(userDto.getPassword());

        User user = User.create(
                username,
                encryptedPassword,
                UserRole.valueOf(userDto.getRole()),
                userDto.getSlackId()
        );

        userRepository.save(user);
    }

    public String signIn(String username, String password) {
        User user = userRepository.findByUsernameAndIsDeletedFalse(username)
                .orElseThrow(() -> new IllegalArgumentException("Invalid user ID or password"));

        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new IllegalArgumentException("Invalid user ID or password");
        }

        return createAccessToken(user.getId(), String.valueOf(user.getRole()));
    }

    private boolean isValidUsername(String username) {
        String regex = "^[a-z0-9]{4,10}$";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(username);
        return matcher.matches();
    }

    private boolean isValidPassword(String password) {
        String regex = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[\\W_]).{8,15}$";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(password);
        return matcher.matches();
    }
}