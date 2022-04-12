package com.example.userservice.service;


import com.example.userservice.client.OrderServiceClient;
import com.example.userservice.exception.UserNotFoundException;
import com.example.userservice.dto.UserDto;
import com.example.userservice.entity.UserEntity;
import com.example.userservice.repository.UserRepository;

import com.example.userservice.vo.ResponseOrder;
import feign.FeignException;
import lombok.RequiredArgsConstructor;

import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.modelmapper.convention.MatchingStrategies;


import org.springframework.cloud.client.circuitbreaker.CircuitBreaker;
import org.springframework.cloud.client.circuitbreaker.CircuitBreakerFactory;
import org.springframework.core.env.Environment;

import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;


@Slf4j
@RequiredArgsConstructor
@Service
public class UserServiceImpl implements UserService{
    //application간 통신하기위해 사용
    private final RestTemplate restTemplate;
    //feginClient 사용 RestTemplate대체용
    private  final OrderServiceClient orderServiceClient;
    //설정정보를 읽어오는 객체
    private  final Environment env;
    //jpa
    private  final UserRepository userRepository;
   //비밀번호 인코딩용 <가장먼저 기동되는 bean에서 해당 클래스를 만들어주면 자동으로 찾아서 주입이 가능!>
   private  final BCryptPasswordEncoder bCryptPasswordEncoder;
   // 라이브러리르 설치했기떄문에 자동으로 di받는다<페인클라이언트 에러발생시 시작)
   private  final CircuitBreakerFactory circuitBreakerFactory;

    @Override
    public UserDto createUser(UserDto userDto) {
        //중복체크
         if(userRepository.existsByEmail(userDto.getEmail())){
             throw new IllegalArgumentException("중복되는 이메일입니다");
         }


        userDto.setUserId(UUID.randomUUID().toString());

        ModelMapper mapper=new ModelMapper();
        //setMatchingStrategy 필드끼리 정확하게 일치 안하면 오류생기게 설정
        mapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
        UserEntity userEntity= mapper.map(userDto, UserEntity.class);


        //비밀번호 암호화
        userEntity.setEncryptedPwd(bCryptPasswordEncoder.encode(userDto.getPwd()));

        userRepository.save(userEntity);

        UserDto returnUserDto= mapper.map(userEntity, UserDto.class);

        return returnUserDto;
    }


    /**
     *   restTemplete 사용  ,   Feign Client 사용 ,  ErrorDecoder 사용 예외처리  / 서킷브레이크 사용 에러별처리
     * 
     * @param userId
     * @return
     */
    @Override
    public UserDto getUserByUserId(String userId) {
        UserEntity userEntity = userRepository.findByUserId(userId);
        if(userEntity==null){
            throw  new UserNotFoundException("User not found");
        }
        ModelMapper mapper=new ModelMapper();
        mapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);

        UserDto userDto = mapper.map(userEntity, UserDto.class);


        /**
         * Using a RestTemplete
         * 다른 서비스와 통신하기위해 url값을 가져온다
         * env.getProperty를 이용하여 환경정보를 읽어온후 사용
         * 그리고 apigateway에게 요청후 order-service의 해당 url로 맵핑되는 메소드 호출
         *  매게변수 1. 주소 , 2. get 방식 ,3 보내는 파라미터 , 4. 받는형식
         *    String orderUrl=String.format(env.getProperty("order_service.url"),userId);
         *         ResponseEntity<List<ResponseOrder>> orderListResponse = restTemplate.exchange(
         *                 orderUrl
         *                 , HttpMethod.GET
         *                 , null
         *                 , new ParameterizedTypeReference<List<ResponseOrder>>() {
         *                 });
         *         //body에서 꺼냄
         *         List<ResponseOrder> orderList = orderListResponse.getBody();
         */

        /**
         *  Using a Feign Client
         *  Feign exception handleling
         */
//        List<ResponseOrder> orderList =null;
//        try {
//          orderList = orderServiceClient.getOrders(userId);
//            userDto.setOrders(orderList);
//
//        }catch (FeignException ex){
//          log.error(ex.getMessage());   }

        /**
         *  ErrorDecoder 사용 예외처리  < try catch 없이 자동으로 잡아준다>
         *   List<ResponseOrder> orderList=orderServiceClient.getOrders(userId);
         */

        /**
         * 서킷브레이크 사용    시작(run) and 1.반환값처리 and 2.문제처리
         */
        //생성
        log.info("orders microservice 호출전");
        CircuitBreaker circuitbreaker = circuitBreakerFactory.create("circuitbreaker");
        List<ResponseOrder> orderList = circuitbreaker.run(
                () -> orderServiceClient.getOrders(userId),  //성공시
                throwable -> new ArrayList<>()); //실패시시 빈배열반환
        log.info("orders microservice 호출후");
        userDto.setOrders(orderList);
        return userDto;
    }

    @Override
    public   List<UserDto> getUserByAll() {
        List<UserEntity> userlist = (List<UserEntity>) userRepository.findAll();

        ModelMapper mapper=new ModelMapper();
        mapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
        List<UserDto> userDtoList = mapper.map(userlist, new TypeToken<List<UserDto>>(){}.getType());

        return userDtoList;
    }

    //인증처리하기위해 UserDetailsService의 오버라이딩
    //username==eamil
    //3번째처리
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
       UserEntity userEntity=userRepository.findByEmail(username);
       if(userEntity==null){
           throw  new UsernameNotFoundException(username); //sercurity제공
       }

       //new ArrayList<>() 권한을 추가하는 마지막작업 처리
        return new User(userEntity.getEmail(),userEntity.getEncryptedPwd(),
                true,true,true,true
                ,new ArrayList<>());//sercurity제공
    }

    @Override
    public UserDto getUserDeatilsByEmail(String email) {
        UserEntity userEntity = userRepository.findByEmail(email);

        if(userEntity==null){
            throw new UsernameNotFoundException(email);//시큐얼리티 입섹션
        }

        return new ModelMapper().map(userEntity,UserDto.class);
    }

}
