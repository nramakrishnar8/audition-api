package com.audition.configuration;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.audition.support.TestProperties;
import java.io.IOException;

import io.micrometer.tracing.Span;
import io.micrometer.tracing.TraceContext;
import io.micrometer.tracing.Tracer;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.Test;

class ResponseHeaderInjectorTest {

    @Test
    void testDoFilterInternalAddsTraceAndSpanIds()
            throws ServletException, IOException {
        final Tracer tracer = mock(Tracer.class);
        final Span span = mock(Span.class);
        final TraceContext context = mock(TraceContext.class);
        final HttpServletRequest request = mock(HttpServletRequest.class);
        final HttpServletResponse response = mock(HttpServletResponse.class);
        final FilterChain filterChain = mock(FilterChain.class);

        when(tracer.currentSpan()).thenReturn(span);
        when(span.context()).thenReturn(context);
        when(context.traceId()).thenReturn("trace-123");
        when(context.spanId()).thenReturn("span-456");

        final ResponseHeaderInjector injector =
                new ResponseHeaderInjector(tracer, TestProperties.appProperties());

        injector.doFilterInternal(request, response, filterChain);

        verify(response).setHeader(TestProperties.TRACE_ID_HEADER, "trace-123");
        verify(response).setHeader(TestProperties.SPAN_ID_HEADER, "span-456");
        verify(filterChain).doFilter(request, response);
    }

    @Test
    void testDoFilterInternalContinuesWhenNoSpanExists()
            throws ServletException, IOException {
        final Tracer tracer = mock(Tracer.class);
        final HttpServletRequest request = mock(HttpServletRequest.class);
        final HttpServletResponse response = mock(HttpServletResponse.class);
        final FilterChain filterChain = mock(FilterChain.class);

        when(tracer.currentSpan()).thenReturn(null);

        final ResponseHeaderInjector injector =
                new ResponseHeaderInjector(tracer, TestProperties.appProperties());

        injector.doFilterInternal(request, response, filterChain);

        verify(filterChain).doFilter(request, response);
        verify(response, never()).setHeader(TestProperties.TRACE_ID_HEADER, "trace-123");
        verify(response, never()).setHeader(TestProperties.SPAN_ID_HEADER, "span-456");
    }
}
