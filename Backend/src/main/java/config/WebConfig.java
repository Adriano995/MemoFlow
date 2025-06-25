/*package config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.*;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**") // Tutti gli endpoint
                .allowedOrigins("http://localhost:4200") // Dove gira Angular
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS") // Metodi ammessi
                .allowedHeaders("*")
                .allowCredentials(true);
    }
}

*/