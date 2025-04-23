package org.example.merchant.handler;



import lombok.extern.slf4j.Slf4j;
import org.example.merchant.bean.Response;
import org.example.merchant.bean.SingleResponse;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

@Slf4j
@ControllerAdvice
public class WebExceptionHandler {

    @ExceptionHandler(Exception.class)
    @ResponseBody
    public Response customerException(Exception exception) {

        log.error("System Error", exception);

        SingleResponse response = new SingleResponse();
        response.setCode(500);
        response.setErrMessage("System Error, Please Try Again Later");
        return response;
    }
}
