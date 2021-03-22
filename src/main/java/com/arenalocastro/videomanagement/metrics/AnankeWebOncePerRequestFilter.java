package com.arenalocastro.videomanagement.metrics;

import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.boot.actuate.metrics.AutoTimer;
import org.springframework.boot.actuate.metrics.web.servlet.WebMvcMetricsFilter;
import org.springframework.boot.actuate.metrics.web.servlet.WebMvcTagsProvider;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
/*
public class AnankeWebOncePerRequestFilter {

    AtomicInteger bytesTx;
    AtomicInteger bytesRx;
    AtomicInteger bwTx;
    AtomicInteger bwRx;
    AtomicInteger startTime;
    AtomicInteger endTime;

    public AnankeWebOncePerRequestFilter(MeterRegistry registry, WebMvcTagsProvider tagsProvider) {
        AtomicInteger myGauge = registry.gauge("numberGauge", new AtomicInteger(0));
        Gauge.builder("http.server.requests.txbytes", bytesTx, AtomicInteger::get)
                .description("Transmitted bytes")
                .tags(tagsProvider.)
                .register(registry);

        bytesTx = registry.gauge("http.server.requests.txbytes", new AtomicInteger(0));
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        super.doFilterInternal(request, response, filterChain);
        bytesTx.set(response.);
    }
}
*/