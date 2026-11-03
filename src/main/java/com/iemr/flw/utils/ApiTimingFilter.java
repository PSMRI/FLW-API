package com.iemr.flw.utils;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.io.IOException;

// One [API-TIMING] line per request (method, path, status, total ms incl. auth filters) so camp-laptop
// logs show every API's time: journalctl -u amrit-flw-api | grep API-TIMING
@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class ApiTimingFilter implements Filter {

    private final Logger logger = LoggerFactory.getLogger(this.getClass().getName());

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;
        long start = System.currentTimeMillis();
        try {
            chain.doFilter(servletRequest, servletResponse);
        } finally {
            logger.info("[API-TIMING] {} {} status={} ms={}", request.getMethod(), request.getRequestURI(),
                    response.getStatus(), System.currentTimeMillis() - start);
        }
    }
}
