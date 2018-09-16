package com.pp.toptal.soccermanager.config;

import java.util.TimeZone;

import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer;
import org.springframework.boot.autoconfigure.jackson.JacksonAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;

@Configuration
@Import(JacksonAutoConfiguration.class)
public class JsonConfiguration {

    @Bean
    public Jackson2ObjectMapperBuilderCustomizer getObjectMapperBuilderCustomizer() {
        return new Jackson2ObjectMapperBuilderCustomizer() {
            @Override
            public void customize(Jackson2ObjectMapperBuilder jacksonObjectMapperBuilder) {
                jacksonObjectMapperBuilder.failOnEmptyBeans(false);
                jacksonObjectMapperBuilder.timeZone(TimeZone.getDefault());
            }
        };
    }

}
