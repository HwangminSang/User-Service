package com.example.userservice.security;

import com.example.userservice.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import javax.servlet.Filter;

@RequiredArgsConstructor
@Configuration //빈등록 우선됨
@EnableWebSecurity //웹보안
public class WebSecurity extends WebSecurityConfigurerAdapter {

     private  final Environment env; //토큰 설정한 yml파일에서 값을 가져오기 위해사용용
     private  final UserService userService;
     private  final BCryptPasswordEncoder bCryptPasswordEncoder; //패스워드 인코딩
    //권한관련

    @Override//HttpSecurity http 권한관련
    protected void configure(HttpSecurity http) throws Exception {
        http.csrf().disable();  //csrf 사용안함

        //해당 url은 인증없이 사용가능
        //http.authorizeRequests().antMatchers("/users/**").permitAll();
        http.authorizeRequests().antMatchers("/actuator/**").permitAll();  //어플리케이션 상태값 볼수있다.  권한없이
        
        http.authorizeRequests().antMatchers("/**")
//                .hasIpAddress("127.0.0.1")//통과시켜주고싶은 ip 권한 부야 // Forbidden 403은 접근 제한이기 때문에 IP가 차단된 경우(또는 지정된 IP가 아닌경우)에 대한 오류, 401은 인증 오류로 인해 해당 리소스에 접근할 수 없는 오류입니다.
                .permitAll()
                .and() //추가작업
                .addFilter(getAuthenticationFilter());//필터통과시만 권한 부여
        //프레임을 보이게 해준다
        http.headers().frameOptions().disable();
    }

    private AuthenticationFilter getAuthenticationFilter() throws Exception { //인증처리  401관련
        AuthenticationFilter authenticationFilter=new AuthenticationFilter(userService,env);
        authenticationFilter.setAuthenticationManager(authenticationManager());

        return authenticationFilter;
    }

    //select pwd from users where eamil=?  -->userDetailsService을 상속받은 userService에서 처리
    // db_pwd(encrypted) ==input_pwd(encrypted)<우리가입력한 비번을 변환(bCryptPasswordEncoder) 하여 비교>
    @Override //AuthenticationManagerBuilder 인증관련
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        //패스워드 인코딩
        auth.userDetailsService(userService).passwordEncoder(bCryptPasswordEncoder);//사용자전달한것을 이용 로그인처리 .
    }
}
