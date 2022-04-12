package com.example.userservice.security;

import com.example.userservice.dto.UserDto;
import com.example.userservice.service.UserService;
import com.example.userservice.vo.RequestLogin;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;

import org.springframework.core.env.Environment;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;

@RequiredArgsConstructor
@Slf4j
public class AuthenticationFilter extends UsernamePasswordAuthenticationFilter {
    /**
     * @param request
     * @param response
     * @return
     * @throws AuthenticationException
     *  *POST방식으로 들어오기때문에 inputSteam을 이용하여 읽어온다.
     */
     private final UserService userService;
     private final Environment env;  // yml파일 설정 가져옴

    @Override //1번째로 처리
    public Authentication attemptAuthentication(HttpServletRequest request
                                                ,HttpServletResponse response) throws AuthenticationException {
        try {

            RequestLogin creds=new ObjectMapper().readValue(request.getInputStream(),RequestLogin.class);
            //스프링 시큐얼리티에서 사용할수 있는 값으로 변경하기위해서 아래 클래스(UsernamePasswordAuthenticationToken))이용
            // new ArrayList<>() 권한부여
            return getAuthenticationManager().authenticate( //인증 처리 매니저에게 넘김
                    new UsernamePasswordAuthenticationToken(  // 토큰으로 변경후   //2번째처리
                            creds.getEmail()
                            ,creds.getPassWord()
                            ,new ArrayList<>())
            );


        } catch (IOException e) {
          throw  new RuntimeException(e);
        }

    }

    //로그인 성공시 정확히 어떤 처리를 해줄것인지 ( 토큰 설정 , 사용자 로그인시 반환값등 처리 )
    //4번쟤
    @Override
    protected void successfulAuthentication(HttpServletRequest request
                                          , HttpServletResponse response
                                        , FilterChain chain
                                      , Authentication authResult) throws IOException, ServletException {

        //User는 서비스의 loadUserByUsername 메서드에서 처리한 시큐얼리티 객체로 유저정보알수있다
        //getPrincipal() authResult에 포함되어있는 객체를 가져온다
        //유저 id를 이용하여 token은 만들자
        String userName=(((User)authResult.getPrincipal()).getUsername());
        UserDto userDetails =userService.getUserDeatilsByEmail(userName);

        //toke 생성 compact로 토큰생성
        String token = Jwts.builder().
                setSubject(userDetails.getUserId())  //토큰만들기
                .setExpiration(new Date(System.currentTimeMillis() +Long.parseLong(env.getProperty("token.expiration_time")))) //유효기간. Long.parseLong 이용하여 String->Long 형변환
                .signWith(SignatureAlgorithm.HS512,env.getProperty("token.secret")) // 알고리즘 이용하여 암호화 // 키 조합으로 yml에서 가져옴
                .compact();

        //헤더에 토큰과 유저id를 넣어서 보냄
        response.addHeader("token",token);
        response.addHeader("userId",userDetails.getUserId());
    }
}
