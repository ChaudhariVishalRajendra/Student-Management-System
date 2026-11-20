package com.springboot.spring_intro.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;

    protected void doFilterInternal(HttpServletRequest req, HttpServletResponse res,
                                    FilterChain filterChain) throws ServletException, IOException    {

        final String authorizationHeader = req.getHeader("Authorization");

        log.info("==> [JwtFilter] Request: {} {}, AuthHeader present: {}", 
                req.getMethod(), req.getRequestURI(), (authorizationHeader != null));

        if(authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")){
            log.warn("==> [JwtFilter] Authorization header is missing or does not start with 'Bearer '. Header value: '{}'", authorizationHeader);
            filterChain.doFilter(req, res);
            return;
        }

        final String jwtToken = authorizationHeader.substring(7).trim();
        final String username;

        try {
            username = jwtService.extractUsername(jwtToken);
            log.info("==> [JwtFilter] Successfully extracted username: '{}'", username);
        } catch (Exception e) {
            log.error("==> [JwtFilter] JWT extraction failed! Token snippet: '{}...', Error: {}", 
                    jwtToken.substring(0, Math.min(20, jwtToken.length())), e.getMessage(), e);
            res.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            res.setContentType("application/json");
            res.getWriter().write(String.format("{\"status\": 401, \"error\": \"JWT Extraction Failed\", \"details\": \"%s\"}", e.getMessage()));
            return;
        }

        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null){
            try {
                UserDetails userDetails = userDetailsService.loadUserByUsername(username);
                log.info("==> [JwtFilter] Found user '{}' in DB with authorities: {}", username, userDetails.getAuthorities());

                if (jwtService.isTokenValid(jwtToken, userDetails)){
                    UsernamePasswordAuthenticationToken authenticationToken =
                            new UsernamePasswordAuthenticationToken(
                                    userDetails, null,
                                    userDetails.getAuthorities()
                            );
                    authenticationToken.setDetails(
                            new WebAuthenticationDetailsSource().buildDetails(req)
                    );
                    SecurityContextHolder.getContext().setAuthentication(authenticationToken);
                    log.info("==> [JwtFilter] Successfully authenticated '{}' into SecurityContext", username);
                } else {
                    log.warn("==> [JwtFilter] isTokenValid returned FALSE for user '{}' (token expired or mismatch)", username);
                    res.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                    res.setContentType("application/json");
                    res.getWriter().write("{\"status\": 401, \"error\": \"Token Validation Failed (Expired or Mismatched)\"}");
                    return;
                }
            } catch (Exception e) {
                log.error("==> [JwtFilter] Error loading user details for '{}': {}", username, e.getMessage(), e);
                res.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                res.setContentType("application/json");
                res.getWriter().write(String.format("{\"status\": 401, \"error\": \"User Details Lookup Failed\", \"details\": \"%s\"}", e.getMessage()));
                return;
            }
        }
        filterChain.doFilter(req, res);
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {

        String path = request.getServletPath();

        return path.startsWith("/auth/otp")
                || path.equals("/auth/login")
                || path.equals("/auth/refresh");
    }

}
