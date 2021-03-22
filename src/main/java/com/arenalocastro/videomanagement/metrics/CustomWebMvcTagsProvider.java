package com.arenalocastro.videomanagement.metrics;

import io.micrometer.core.instrument.Tag;
import io.micrometer.core.instrument.Tags;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.actuate.metrics.web.servlet.DefaultWebMvcTagsProvider;
import org.springframework.boot.actuate.metrics.web.servlet.WebMvcMetricsFilter;
import org.springframework.boot.actuate.metrics.web.servlet.WebMvcTags;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.swing.text.html.Option;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Enumeration;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Stream;

public class CustomWebMvcTagsProvider extends DefaultWebMvcTagsProvider {

    @Value(value = "${ananke.service-name}")
    String serviceName;

    // TODO make a cleaner code
    final String notAvailable = "Not-Available";

    @Override
    public Iterable<Tag> getTags(HttpServletRequest request, HttpServletResponse response, Object handler, Throwable exception) {
        return Tags.of(super.getTags(request, response, handler, exception)).and(
                getAnankeTags(request, response, handler, exception));
    }

    private Tags getAnankeTags(HttpServletRequest request, HttpServletResponse response, Object handler, Throwable exception) {
        Tags tags = Tags.of(Tag.of("caller", Optional.ofNullable(request.getHeader("X-Caller")).orElse(notAvailable)),
                //Tag.of("request-id", Optional.ofNullable(request.getHeader("X-Request-Id")).orElse(notAvailable)),
                Tag.of("root-id", Optional.ofNullable(request.getHeader("X-Root-Id")).orElse(serviceName)),
                Tag.of("service-name", serviceName),
                Tag.of("request-name", WebMvcTags.uri(request, response).getValue()),
                Tag.of("action", WebMvcTags.method(request).getValue()),
                Tag.of("response-status", WebMvcTags.status(response).getValue()));
                Tag.of("request-id", "test");

        /*Tag executor;
        try {
            executor = Tag.of("executor-ip", new String(InetAddress.getLocalHost().getAddress()));
        } catch (UnknownHostException e) {
            executor = Tag.of("executor-ip", notAvailable);
            e.printStackTrace();
        }*/
        return tags;//.and(executor);
    }

    @Override
    public Iterable<Tag> getLongRequestTags(HttpServletRequest request, Object handler) {
        return super.getLongRequestTags(request, handler);
    }
}
