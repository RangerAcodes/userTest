package com.example.userservice.services;

import com.example.userservice.Models.Session;
import com.example.userservice.Models.SessionStatus;
import com.example.userservice.Models.User;
import com.example.userservice.dtos.UserDto;
import com.example.userservice.repositories.SessionRepository;
import com.example.userservice.repositories.UserRepository;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.MacAlgorithm;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.MultiValueMapAdapter;

import javax.crypto.SecretKey;
import javax.swing.text.html.Option;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
public class AuthService {
    //create repo objects first

    private UserRepository userRepository;
    private SessionRepository sessionRepository;
    //private BCryptPasswordEncoder bCryptPasswordEncoder;


    //Constructor of AuthService class

    public AuthService(UserRepository userRepository, SessionRepository sessionRepository ){
    //, BCryptPasswordEncoder bCryptPasswordEncoder) {
        this.userRepository = userRepository;
        this.sessionRepository = sessionRepository;
        //this.bCryptPasswordEncoder = bCryptPasswordEncoder;
    }
    //created constructor of bCryptPasswordEncoder in Security Class

    public ResponseEntity<UserDto> login(String email, String password) {
        Optional<User> userOptional = userRepository.findByEmail(email);

        //if user does not exist
        if (userOptional.isEmpty()) {
            return null;
        }

        User user = userOptional.get();

        //validation
//        if (!bCryptPasswordEncoder.matches(password, user.getPassword())) {
//            return null;
//        }

        //return null;


        Map<String, Object> jsonForJwt = new HashMap<>();
        jsonForJwt.put("email", user.getEmail());
        jsonForJwt.put("roles", user.getRoles());
        jsonForJwt.put("roles", user.getRoles());
        jsonForJwt.put("expirationDate",new Date());
        jsonForJwt.put("createdAt", new Date());

        MacAlgorithm alg = Jwts.SIG.HS256;
        SecretKey key = alg.key().build();

        String token = Jwts.builder().claims(jsonForJwt).signWith(key, alg).compact();

        Session session = new Session();
        session.setSessionStatus(SessionStatus.ACTIVE);
        session.setToken(token);
        session.setUser(user);
        sessionRepository.save(session);

        UserDto userDto = User.from(user);

//        Map<String, String> headers = new HashMap<>();
//        headers.put(HttpHeaders.SET_COOKIE, token);

        MultiValueMapAdapter<String, String> headers = new MultiValueMapAdapter<>(new HashMap<>());
        headers.add(HttpHeaders.SET_COOKIE, "auth-token:" + token);

        ResponseEntity<UserDto> response = new ResponseEntity<>(userDto, headers, HttpStatus.OK);
//        response.getHeaders().add(HttpHeaders.SET_COOKIE, token);

        return response;

    }

    public ResponseEntity<Void> logout(String token, Long userId){
        Optional<Session> sessionOptional = sessionRepository.findByTokenAndUser_id(token, userId);

        if (sessionOptional.isEmpty()){
            return null;
        }

        Session session = sessionOptional.get();

        session.setSessionStatus(SessionStatus.ENDED);

        sessionRepository.save(session);

        return ResponseEntity.ok().build();

    }

    public UserDto signUp(String email, String password){
        User user = new User();
        user.setEmail(email);
        //user.setPassword(bCryptPasswordEncoder.encode(password));
        User savedUser = userRepository.save(user);
        return UserDto.from(savedUser);
    }

    public SessionStatus validate(String token, Long userId){
        Optional<Session> sessionOptional = sessionRepository.findByTokenAndUser_id(token,userId);

        if (sessionOptional.isEmpty()){
            return null;
        }

        MacAlgorithm alg = Jwts.SIG.HS256;
        SecretKey key = alg.key().build();

        Claims claims = Jwts.parser().verifyWith(key).build().parseSignedClaims(token).getPayload();

        return SessionStatus.ACTIVE;
    }

}
