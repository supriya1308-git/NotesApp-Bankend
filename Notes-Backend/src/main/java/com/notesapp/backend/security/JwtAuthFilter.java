package com.notesapp.backend.security;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.notesapp.backend.service.UserDetailsServiceImpl;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class JwtAuthFilter extends OncePerRequestFilter {

  @Autowired
  JwtUtils jwtUtils;

  @Autowired
  UserDetailsServiceImpl userDetailsService;

//  @Override
//  protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
//      throws ServletException, IOException {
//    String header = request.getHeader("Authorization");
//    if (header != null && header.startsWith("Bearer ")) {
//      String token = header.substring(7);
//      if (jwtUtils.validateToken(token)) {
//        String username = jwtUtils.getUsernameFromToken(token);
//        var userDetails = userDetailsService.loadUserByUsername(username);
//        var auth = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
//        SecurityContextHolder.getContext().setAuthentication(auth);
//      }
//    }
//    filterChain.doFilter(request, response);
//  }
  @Override
  protected void doFilterInternal(HttpServletRequest request,
                                  HttpServletResponse response,
                                  FilterChain filterChain) throws ServletException, IOException {
      String header = request.getHeader("Authorization");

      if (header != null && header.startsWith("Bearer ")) {
          String token = header.substring(7);

          if (jwtUtils.validateToken(token)) {
              String username = jwtUtils.getUsernameFromToken(token);
              UserDetails userDetails = userDetailsService.loadUserByUsername(username);

              UsernamePasswordAuthenticationToken auth =
                      new UsernamePasswordAuthenticationToken(
                              userDetails,
                              null,
                              userDetails.getAuthorities()
                      );
              auth.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

           
              SecurityContextHolder.getContext().setAuthentication(auth);
          }
      }

      filterChain.doFilter(request, response);
  }

  
  
}
