package com.audition.configuration;

import io.micrometer.tracing.Span;
import io.micrometer.tracing.Tracer;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
public class ResponseHeaderInjector extends OncePerRequestFilter {

    private final Tracer tracer;
    private final AppProperties appProperties;

    public ResponseHeaderInjector(final Tracer tracer, final AppProperties appProperties) {
        super();
        this.tracer = tracer;
        this.appProperties = appProperties;
    }

    @Override
    protected void doFilterInternal(final HttpServletRequest request,
                                    final HttpServletResponse response, final FilterChain filterChain)
            throws ServletException, IOException {
        final Span currentSpan = tracer.currentSpan();
        if (currentSpan != null) {
            response.setHeader(appProperties.traceIdHeader(), currentSpan.context().traceId());
            response.setHeader(appProperties.spanIdHeader(), currentSpan.context().spanId());
        }
        filterChain.doFilter(request, response);
    }
}
