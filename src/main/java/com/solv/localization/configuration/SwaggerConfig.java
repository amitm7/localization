package com.solv.localization.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;
import redis.clients.jedis.Jedis;


@Configuration

public class SwaggerConfig {
    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

    @Bean
    public Jedis jedis(){ return  new Jedis(); }

}
