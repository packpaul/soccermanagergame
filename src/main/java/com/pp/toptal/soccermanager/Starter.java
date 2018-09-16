package com.pp.toptal.soccermanager;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.support.SpringBootServletInitializer;
import org.springframework.context.annotation.Bean;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@SpringBootApplication
public class Starter extends SpringBootServletInitializer {
    
    private static final Logger LOG = LoggerFactory.getLogger(Starter.class);
    
    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder builder) {
        return builder.sources(Starter.class);
    }
    
    public static void main(String[] args) {
        SpringApplication.run(Starter.class, args);
    }
    
    @Bean
    CommandLineRunner run() {
       return (args) -> {
           LOG.info("Soccer Manager Game application has started.");
       };
    }
    
}
