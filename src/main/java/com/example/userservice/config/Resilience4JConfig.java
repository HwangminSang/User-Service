package com.example.userservice.config;


import io.github.resilience4j.circuitbreaker.CircuitBreakerConfig;
import io.github.resilience4j.timelimiter.TimeLimiterConfig;
import org.springframework.cloud.circuitbreaker.resilience4j.Resilience4JCircuitBreakerFactory;
import org.springframework.cloud.circuitbreaker.resilience4j.Resilience4JConfigBuilder;
import org.springframework.cloud.client.circuitbreaker.Customizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;


//Resilience4JCircuitBreaker 커스터마이징하기
@Configuration //설정
public class Resilience4JConfig {

   @Bean
    public Customizer<Resilience4JCircuitBreakerFactory> globalCustomConfiguration(){
       //팩토리 설정
       CircuitBreakerConfig circuitBreakerConfig= CircuitBreakerConfig.custom()
               .failureRateThreshold(4)
               .waitDurationInOpenState(Duration.ofMillis(1000))
               .slidingWindowType(CircuitBreakerConfig.SlidingWindowType.COUNT_BASED)
               .slidingWindowSize(2)
               .build();

       TimeLimiterConfig timeLimiterConfig=TimeLimiterConfig.custom()
               .timeoutDuration(Duration.ofSeconds(4)) //4초
               .build();


       return factory -> factory.configureDefault(id ->new Resilience4JConfigBuilder(id)
               .timeLimiterConfig(timeLimiterConfig)
               .circuitBreakerConfig(circuitBreakerConfig)
               .build()
       );

   }

}
