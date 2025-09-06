package com.notesapp.backend.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.notesapp.backend.model.User;
import com.notesapp.backend.repository.UserRepository;
import com.notesapp.backend.security.JwtUtils;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

  @Autowired UserRepository userRepo;
  @Autowired JwtUtils jwtUtils;
  BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

  @PostMapping("/register")
  public Map<String, String> register(@RequestBody Map<String,String> body) {
    String username = body.get("username");
    String email = body.get("email");
    String password = body.get("password");
    if (userRepo.existsByUsername(username)) throw new RuntimeException("username exists");
    if (userRepo.existsByEmail(email)) throw new RuntimeException("email exists");
    User u = new User();
    u.setUsername(username);
    u.setEmail(email);
    u.setPasswordHash(encoder.encode(password));
    userRepo.save(u);
    return Map.of("message","ok");
  }

  @PostMapping("/login")
  public Map<String,String> login(@RequestBody Map<String,String> body) {
   System.err.println("AuthController.login()");
	  
	  String username = body.get("username");
    String password = body.get("password");
    User u = userRepo.findByUsername(username).orElseThrow(() -> new RuntimeException("invalid"));
    if (!encoder.matches(password, u.getPasswordHash())) throw new RuntimeException("invalid");
    String token = jwtUtils.generateToken(username);
    return Map.of("accessToken", token, "username", username);
  }
  
}
