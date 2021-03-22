package com.arenalocastro.videomanagement.metrics;

import org.springframework.boot.actuate.metrics.web.servlet.WebMvcTagsProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class TagsConfiguration {
    @Bean
    public WebMvcTagsProvider webMvcTagsProvider() {
        return new CustomWebMvcTagsProvider();
    }
}
