package com.example.userservice.exception;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.Date;


@RestControllerAdvice
public class CustomizedResponseEntityExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(Exception.class)
    public final ResponseEntity<Object> handleAllExceptions(Exception ex, WebRequest requset){//WebRequest어디서 발생했는지
        //우리가 등록한 POJO 객체                                                                   //uri=/users/100"
        ExceptionResponse exceptionResponse
                =new ExceptionResponse(new Date(),"ID 중복" , ex.getMessage());//언제 , 메세지 , 상세정보내용 X

        return new ResponseEntity(exceptionResponse, HttpStatus.CONFLICT); //409아이디 중복
    }

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {

        ExceptionResponse exceptionResponse=new ExceptionResponse(new Date(),"Validation Failed", ex.getBindingResult().getFieldError().getDefaultMessage());

            return new ResponseEntity(exceptionResponse,HttpStatus.BAD_REQUEST);


    }

}
