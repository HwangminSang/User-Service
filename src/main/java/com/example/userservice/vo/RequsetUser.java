package com.example.userservice.vo;

import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Data
public class RequsetUser {

    @NotBlank(message = "Email을 입력해주세요")
    @Size(min=2, message = "이메일은 최소 2글자 이상을 입력하셔야합니다.")
    @Email
     private  String email;

    @NotBlank(message = "name 입력해주세요")
    @Size(min=2 , message = "이름은 최소 2글자 이상을 입력하셔야합니다.")
     private  String name;

    @NotBlank(message = "비밀번호를 입력하셔야 합니다")
    @Size(min = 8 , message = "비밀번호는 최소 8글자 이상입니다.")
    private  String pwd;

}
