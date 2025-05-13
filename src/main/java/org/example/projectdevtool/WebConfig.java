package org.example.projectdevtool;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins("/**", "http://localhost:8080", "https://172.18.80.1:8080",
                "http://172.18.80.1:8080", "https://172.18.45.1:8080",
                "http://172.18.45.1:8080",
                "http://172.24.103.196:8080", "http://16.16.201.28:8080",
                 "http://localhost:5173")
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                // .allowedHeaders("*")
                .allowCredentials(true);
    }
}
