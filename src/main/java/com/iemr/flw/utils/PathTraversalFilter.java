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
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;

@Component
@Order(1)
public class PathTraversalFilter implements Filter {

    private final Logger logger = LoggerFactory.getLogger(this.getClass().getName());

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;

        String uri = request.getRequestURI();

        // Decode once to catch %2e%2e and similar encoded variants
        String decodedUri = URLDecoder.decode(uri, StandardCharsets.UTF_8);

        if (containsTraversalPattern(uri) || containsTraversalPattern(decodedUri)) {
            logger.warn("Path traversal attempt blocked: {}", uri);
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid request path");
            return;
        }

        chain.doFilter(servletRequest, servletResponse);
    }

    private boolean containsTraversalPattern(String path) {
        if (path == null) return false;
        String normalized = path.toLowerCase();
        return normalized.contains("../")
                || normalized.contains("..\\")
                || normalized.contains("..;")
                || normalized.contains("%2e%2e")
                || normalized.contains("%252e")  // double-encoded
                || normalized.endsWith("..");
    }
}
