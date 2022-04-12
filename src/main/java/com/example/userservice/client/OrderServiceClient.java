package com.example.userservice.client;


import com.example.userservice.vo.ResponseOrder;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@FeignClient(name="order-service")  //호출하고자하는 서비스 이름 과 해당어노테이션 필수
public interface OrderServiceClient {

    //order-service의 해당 컨트롤러의 메서드 반환값을 지정
    @GetMapping("/order-service/{userId}/orders")
    List<ResponseOrder> getOrders(@PathVariable String userId);
}
