package com.example.wedding.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;

@Configuration
public class JacksonConfig {

    @Bean
    public MappingJackson2HttpMessageConverter mappingJackson2HttpMessageConverter() {
        ObjectMapper mapper = new ObjectMapper();
        
        // Prevent serialization failures on circular references
        mapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
        
        MappingJackson2HttpMessageConverter converter = 
            new MappingJackson2HttpMessageConverter(mapper);
        return converter;
    }
} 