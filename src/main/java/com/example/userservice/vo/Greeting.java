package com.example.userservice.vo;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;


@AllArgsConstructor
@NoArgsConstructor //디폴트
@Data
@Component
public class Greeting {
    //yml 파일에서들고옴
    @Value("${greeting.message}")
     private  String message;


}
