package com.example.userservice.service;

import com.example.userservice.dto.UserDto;
import com.example.userservice.entity.UserEntity;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.util.List;


//UserDetailsService를 상속받아야 WebSecurtiy 설정에서 커리뽑아서 사용가능
public interface UserService  extends UserDetailsService {

    UserDto createUser(UserDto userDto);

     UserDto getUserByUserId(String userId);

    List<UserDto> getUserByAll();

    UserDto getUserDeatilsByEmail(String userName);
}
