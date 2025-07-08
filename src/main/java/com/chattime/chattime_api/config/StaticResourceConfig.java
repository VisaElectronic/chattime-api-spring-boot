package com.chattime.chattime_api.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class StaticResourceConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry
                // map all /uploads/** URLs...
                .addResourceHandler("/uploads/**")
                // ...to files under the 'uploads' directory at your project root
                .addResourceLocations("file:uploads/");
    }
}

