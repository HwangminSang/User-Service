package com.example.userservice;

import com.example.userservice.error.FeignErrorDecoder;
import feign.Logger;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.client.RestTemplate;

@SpringBootApplication
@EnableDiscoveryClient  //유레카 서버에 등록할 준비
@EnableFeignClients //feginClient 사용 RestTemplate 대체
public class UserServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(UserServiceApplication.class, args);
    }

    //제일먼저 시작되는 클래스
    @Bean
    public BCryptPasswordEncoder passwordEncoder(){
        return  new BCryptPasswordEncoder();
    }

    //서비스간 통신하기위해 사용
    //LoadBalnced 사용시
    // 기존     http://127.0.0.1:8000/order-service~
    // 변경 --> http://order-service/order-service~
    @Bean
    @LoadBalanced
    public RestTemplate getRestTemplate(){
        return new RestTemplate();
    }

    //feign 전용 로그남기는 빈 등록
    @Bean
    public Logger.Level feignLoggerLevel(){
        return Logger.Level.FULL;
    }

    //feignClient 사용시 오류가 나면 해당 빈이 처리.
    //@componet로 등록했기때문에 생략가능!
//    @Bean
//    public FeignErrorDecoder feignErrorDecoder(){
//        return new FeignErrorDecoder();
//    }
}
