package org.example.merchant.handler;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import lombok.extern.slf4j.Slf4j;
import org.example.merchant.bean.SingleResponse;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BusinessException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public SingleResponse<String> handleBusinessException(BusinessException e) {
        log.error("业务异常：", e);
        return SingleResponse.buildFailure(e.getMessage());
    }

    @ExceptionHandler(ExpiredJwtException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public SingleResponse<String> handleExpiredJwtException(ExpiredJwtException e) {
        log.error("Token已过期：", e);
        return SingleResponse.buildFailure("登录已过期，请重新登录");
    }

    @ExceptionHandler(JwtException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public SingleResponse<String> handleJwtException(JwtException e) {
        log.error("Token验证失败：", e);
        return SingleResponse.buildFailure("token无效");
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public SingleResponse<String> handleException(Exception e) {
        log.error("系统异常：", e);
        return SingleResponse.buildFailure("系统错误，请稍后重试");
    }
}