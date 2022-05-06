package com.spring.boot.logger.filters;

import com.spring.boot.logger.application.ApplicationLogger;
import lombok.RequiredArgsConstructor;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
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
