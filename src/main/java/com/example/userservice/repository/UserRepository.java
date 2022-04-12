package com.example.userservice.repository;

import com.example.userservice.entity.UserEntity;
import org.apache.catalina.User;
import org.springframework.data.repository.CrudRepository;


//자동으로 repository에 등록됨
public interface UserRepository extends CrudRepository<UserEntity,Long> {

    UserEntity findByUserId(String userId);
    UserEntity findByEmail(String username);
    boolean existsByEmail(String email);
}
