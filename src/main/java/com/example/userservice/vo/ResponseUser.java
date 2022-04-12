package com.example.userservice.vo;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.util.List;


//반환시 쓰는 vo
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)  //NULL일경우 무시해버린다.
public class ResponseUser {

   private  String email;
   private  String name;
   private  String userId;

   private List<ResponseOrder> orders;
}
