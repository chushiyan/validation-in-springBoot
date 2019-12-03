package com.chushiyan.validation_tutorial.handler;


import com.chushiyan.validation_tutorial.entity.Result;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindException;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.ValidationException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author chushiyan
 * @email chushiyan0415@163.com
 * @description
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * 处理所有参数校验时抛出的异常
     *
     * @param ex
     * @return
     */
    @ExceptionHandler(value = ValidationException.class)
    public Result handleBindException(ValidationException ex) {

        Map<String, Object> body = new LinkedHashMap<String, Object>();
        body.put("timestamp", new Date());

        // 获取所有异常
        List<String> errors = new LinkedList<String>();
        if (ex instanceof ConstraintViolationException) {
            ConstraintViolationException exs = (ConstraintViolationException) ex;
            Set<ConstraintViolation<?>> violations = exs.getConstraintViolations();
            for (ConstraintViolation<?> item : violations) {
                errors.add(item.getMessage());
            }
        }
        body.put("errors", errors);
        return new Result(true, 20001, "提交的参数校验失败", body);
    }

    /**
     * 处理所有校验失败的异常（MethodArgumentNotValidException异常）
     *
     * @param ex
     * @return
     */
    @ExceptionHandler(value = MethodArgumentNotValidException.class)
    // 设置响应状态码为400
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Result handleBindGetException(MethodArgumentNotValidException ex) {
        Map<String, Object> body = new LinkedHashMap<String, Object>();
        body.put("timestamp", new Date());

        // 获取所有异常
        List<String> errors = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(x -> x.getDefaultMessage())
                .collect(Collectors.toList());
        body.put("errors", errors);
        return new Result(false, 20001, "提交的数据校验失败", body);
//        return new Result(false, 20001, StringUtils.join(errors.toArray(), " "), body);

    }

    /**
     * @param ex
     * @return
     */
    @ExceptionHandler(value = Exception.class)
    public Result handleException(Exception ex) {
        return new Result(false, 20001, ex.getMessage(), null);
    }
}
