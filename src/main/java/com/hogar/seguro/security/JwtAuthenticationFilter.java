package com.hogar.seguro.security;


import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.constraints.NotNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;

    public JwtAuthenticationFilter(JwtService jwtService, UserDetailsService userDetailsService) {
        this.jwtService = jwtService;
        this.userDetailsService = userDetailsService;
    }


    @Override
    protected void doFilterInternal(
            @NotNull HttpServletRequest request,
            @NotNull HttpServletResponse response,
            @NotNull FilterChain filterChain
       ) throws ServletException, IOException {

        String jwt = null;
        String username = null;


        //retrieve cookies:
        if (request.getCookies() != null) {
            for (Cookie cookie : request.getCookies()) {
                if ("jwt".equals(cookie.getName())) {
                    jwt = cookie.getValue();
                    break;
                }
            }
        }


        //if token does not exist:
        if (jwt == null) {
            filterChain.doFilter(request, response);
            return;
        }


        //if token exists:
        try {
            username = jwtService.extractUsername(jwt);
        } catch (Exception e) {
                    //Delete corrupted cookie if token expired:
            Cookie cookie = new Cookie("jwt", null);
            cookie.setMaxAge(0);
            cookie.setPath("/");
            response.addCookie(cookie);
                        //& allow the request to proceed as anonymous (without authentication or role):
            filterChain.doFilter(request, response);
            return;
        }


        //Verify if authentication exists in SecurityContextHolder:
        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {

            //load real user (userDetails):
            UserDetails userDetails = this.userDetailsService.loadUserByUsername(username);

            //Validate token against user:
            if (jwtService.isTokenValid(jwt, userDetails)) {
                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                        userDetails,
                        null,
                        userDetails.getAuthorities()
                );
                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));//additional info request

                //Register user in spring sec. (SecurityContextHolder - authenticated):
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }

        }


        //continue filter flow:
        filterChain.doFilter(request, response);

    }

}




