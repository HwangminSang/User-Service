package com.example.userservice.error;


import feign.Response;
import feign.codec.ErrorDecoder;
import lombok.RequiredArgsConstructor;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

//feingClient 예외처리
//각 메소드마다 try catch 할필요없이 이 클래스가 전체적으로 오류를 잡아 처리해준다!.
@RequiredArgsConstructor
@Component // 생성자로 주입하기 위해서는 componet로 등록해둬야한다.
public class FeignErrorDecoder implements ErrorDecoder {
    //에러문을 하드코딩 x 환경설정해둔 파일에서 읽어오기 위해 해당객체 사용
    private  final Environment env;

    @Override
    public Exception decode(String methodName, Response response) {
        switch (response.status()){
            case 400 :
                        break;
            case 404 :
                if(methodName.contains("getOrders")){
            return new ResponseStatusException(HttpStatus.valueOf(response.status())  //404 Not Found 로 기존 500번에서 변경되어 반환
                     ,env.getProperty("order_service.exception.orders_is_empty"));
        }
            break;
        default:
        return new Exception(response.reason()); //예외발생원인 리턴
    }

        return null;
    }
}
