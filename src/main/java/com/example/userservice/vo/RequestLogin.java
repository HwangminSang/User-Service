package com.example.userservice.vo;

import lombok.Data;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Data
public class RequestLogin {

    @NotNull(message = "Eamil cannot be null")
    @Size(min=2 , message="Email not be less than 2 characters")
    private  String email;
    @NotNull(message = "passWord cannot be null")
    @Size(min=8 , message="Email muse be  equals grater than 8 characters")
    private  String passWord;


}
