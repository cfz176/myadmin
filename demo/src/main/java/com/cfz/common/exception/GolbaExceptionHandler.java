package com.cfz.common.exception;

import com.cfz.common.lang.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GolbaExceptionHandler {


    /**
     * 实体校验异常捕获
     * @param e
     * @return
     */
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(value = MethodArgumentNotValidException.class)
    public Result handler(MethodArgumentNotValidException e) {
        BindingResult result = e.getBindingResult();
        ObjectError error = result.getAllErrors().stream().findFirst().get();
        log.error("实体校验异常：-------------{}",error.getDefaultMessage());
        return Result.fail(error.getDefaultMessage());
    }

    /**
     * 非法数据异常捕获
     * @param e
     * @return
     */
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(value = IllegalArgumentException.class)
    public Result handler(IllegalArgumentException e) {
        log.error("Assert异常：-------------{}",e.getMessage());
        return Result.fail(e.getMessage());
    }

    /**
     * 运行时异常捕获
     * @param e
     * @return
     */
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(value = RuntimeException.class)
    public Result handler(RuntimeException e) {
        log.error("运行时异常：-------------{}",e.getMessage());
        return Result.fail(e.getMessage());
    }

}
