package com.notesapp.backend.service;

import com.notesapp.backend.model.User;
import com.notesapp.backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.Service;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {
  @Autowired
  UserRepository userRepository;

  @Override
  public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
    User user = userRepository.findByUsername(username)
        .orElseThrow(() -> new UsernameNotFoundException("User not found"));
    return org.springframework.security.core.userdetails.User.withUsername(user.getUsername())
      .password(user.getPasswordHash())
      .roles("USER").build();
  }
}

