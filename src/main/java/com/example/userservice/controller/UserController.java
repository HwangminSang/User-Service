package com.example.userservice.controller;


import com.example.userservice.dto.UserDto;
import com.example.userservice.service.UserService;
import com.example.userservice.vo.Greeting;
import com.example.userservice.vo.RequsetUser;
import com.example.userservice.vo.ResponseUser;
import io.micrometer.core.annotation.Timed;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;

import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/") //APIGATEWAY에서 설정을 해줬기떄문에 user-service를 path에 넣을 필요가없다
@RequiredArgsConstructor
public class UserController {
        //yml 파일 가져옴~
       private  final Environment env;

       private  final Greeting greeting;

       private  final UserService userService;


       //서버 정상작동 유무 확인
    @GetMapping("/health_check")
    @Timed(value="users.status",longTask = true) //각종 지표수집
         public  String status(){

        return String.format("it is Working in User Service Port"
                +",port(lacal.server.port)="+env.getProperty("local.server.port")
                +",port(server.port)="+env.getProperty("server.port")
                +",token secret="+env.getProperty("token.secret")  //외부 yml 파일에서 가져옴
                +",token expiration time="+env.getProperty("token.expiration_time")); //외부 yml 파일에서 가져옴

        }

    @GetMapping("/welcome")
    @Timed(value="users.welcome",longTask = true) //각종 지표수집
    public  String message(){

//        return env.getProperty("greeting.message");
        return greeting.getMessage();
    }

    /**
     *
     * ResponseEntity을 이용하여 status code 200 --> 201바꿔주기
     * 정확하게 알려주는게 중요하다.
     * @param user
     * @return
     */

    @PostMapping("/users")
    public ResponseEntity<ResponseUser> createUser(@Valid @RequestBody RequsetUser user){

        ModelMapper mapper=new ModelMapper();
        mapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
        UserDto userDto=mapper.map(user,UserDto.class);
        userService.createUser(userDto);

        ResponseUser responseUser= mapper.map(userDto,ResponseUser.class);

        return ResponseEntity.status(HttpStatus.CREATED).body(responseUser);
    }
    //조회
    @GetMapping("/users")
    public ResponseEntity<List<ResponseUser>> getAllUsers(){
        List<UserDto> userList = userService.getUserByAll();
       List<ResponseUser> result=new ArrayList<>();
        userList.forEach(v->{
            result.add(new ModelMapper().map(v,ResponseUser.class));
        });
        return  ResponseEntity.status(HttpStatus.OK).body(result);
    }

    @GetMapping("/users/{userId}")
    public ResponseEntity<ResponseUser> getUser(@PathVariable String userId){
        UserDto user = userService.getUserByUserId(userId);

        ResponseUser responseUser = new ModelMapper().map(user, ResponseUser.class);

        return  ResponseEntity.status(HttpStatus.OK).body(responseUser);
    }

}
