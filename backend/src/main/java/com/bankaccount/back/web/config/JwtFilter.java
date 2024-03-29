package com.bankaccount.back.web.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Config class in charge of work with the AUTHORIZATION header.
 * <p>Extends {@link OncePerRequestFilter}
 */
@Component
public class JwtFilter extends OncePerRequestFilter {

   private final JwtUtil jwtUtil;
   private final UserDetailsService userDetailsService;

   /**
    * Constructor for {@link JwtFilter}.
    * @param jwtUtil class to work with the jwt logic
    * @param userDetailsService class to work the user security
    */
   @Autowired
   public JwtFilter(JwtUtil jwtUtil, UserDetailsService userDetailsService) {
      this.jwtUtil = jwtUtil;
      this.userDetailsService = userDetailsService;
   }

   /**
    * Make a validation of the jwt token to verify if is valid, to subsequently verify
    * if the user making the request has the right authorities
    * @param request the value to get information about the request
    * @param response the value to get information about the response
    * @param filterChain the value to verify the security about the endpoint
    * @throws ServletException by the {@code filterChain}
    * @throws IOException by the {@code filterChain}
    */
   @Override
   protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
      String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);

      if (authHeader == null || authHeader.isEmpty()) {
         filterChain.doFilter(request, response);
         return;
      }

      String jwt = authHeader.split(" ")[1].trim();

      if (!this.jwtUtil.isValid(jwt)) {
         filterChain.doFilter(request, response);
         return;
      }

      String username = jwtUtil.getUsername(jwt);
      User user = (User) this.userDetailsService.loadUserByUsername(username);

      UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
              user.getUsername(),
              user.getPassword(),
              user.getAuthorities()
      );

      authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
      SecurityContextHolder.getContext().setAuthentication(authenticationToken);
      filterChain.doFilter(request, response);
   }
}
