package com.spring.boot.logger.filters;

import com.spring.boot.logger.application.ApplicationLogger;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@RequiredArgsConstructor
public class FilterExceptionHandler extends OncePerRequestFilter {

    private final ApplicationLogger logger;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        try {
            filterChain.doFilter(request, response);
        } catch (Exception e) {
            logger.error(e.getMessage());
            throw e;
            // TODO : you can use HandlerExceptionResolver
        }
    }
}
