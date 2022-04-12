package com.example.userservice.entity;


import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@NoArgsConstructor
@Entity
@Data
@Table(name="USERS")
public class UserEntity {

     @Id
     @GeneratedValue(strategy = GenerationType.AUTO)
     private  Long id;

     @Column(nullable = false,length = 50)
     private  String email;
    @Column(nullable = false,length = 50)
     private String name;
    @Column(nullable = false) //유니크 조건
     private  String userId;
    @Column(nullable = false) //유니크 조건
     private  String encryptedPwd;

}
