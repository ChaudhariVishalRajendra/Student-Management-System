package com.springboot.spring_intro.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Map;

@Slf4j
@Component
public class RestAuthenticationEntryPoint implements AuthenticationEntryPoint {
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void commence(
            HttpServletRequest request,
            HttpServletResponse response,
            AuthenticationException authException) throws IOException,
            ServletException {

        log.error("==> [RestAuthenticationEntryPoint] 401 Unauthorized for URI: {} {}. Reason: {}", 
                request.getMethod(), request.getRequestURI(), authException.getMessage());

        response.setStatus(
                HttpServletResponse.SC_UNAUTHORIZED
        );
        response.setContentType(
                MediaType.APPLICATION_JSON_VALUE
        );
        objectMapper.writeValue(
                response.getOutputStream(),
                Map.of(
                        "status", 401,
                        "message", "Authentication Required",
                        "error", authException.getMessage() != null ? authException.getMessage() : "Unauthorized",
                        "path", request.getRequestURI()
                )
        );
    }
}
