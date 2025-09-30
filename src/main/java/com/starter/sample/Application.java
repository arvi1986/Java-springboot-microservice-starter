package com.starter.sample;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.concurrent.Executor;

@SpringBootApplication
@EnableAsync
@ConfigurationPropertiesScan
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                // Pattern options:
                // "http://localhost:[*]" - allows any localhost port
                // "https://*.example.com" - allows all subdomains of example.com
                // "http://localhost:3000" - specific origin
                // "http://192.168.1.*" - allows IP range
                // Multiple patterns can be specified
                registry.addMapping("/api/**")
                        .allowedOriginPatterns(
                            "http://localhost:[*]",          // Any localhost port
                            "https://*.yourdomain.com",      // All subdomains
                            "http://localhost:3000",         // Specific development port
                            "https://app.yourdomain.com",    // Production application
                            "http://127.0.0.1:[*]"          // Local IP with any port
                        )
                        .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH")
                        .allowedHeaders("*")
                        .exposedHeaders("X-Correlation-ID")
                        .allowCredentials(true)
                        .maxAge(3600);
            }
        };
    }

    @Bean(name = "taskExecutor")
    public Executor taskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(5);
        executor.setMaxPoolSize(10);
        executor.setQueueCapacity(25);
        executor.setThreadNamePrefix("AsyncThread-");
        executor.initialize();
        return executor;
    }
}
