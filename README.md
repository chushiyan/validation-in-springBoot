# Spring Boot 2 中的参数校验 spring-boot-starter-validation/Hibernate Validator

Validation in Spring Boot

## 一、添加依赖

```
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-validation</artifactId>
</dependency>
```

这个starter依赖的是Hibernate Validator。

## 二、实体类参数校验



### （一）实体类上加上注解

```java
import lombok.Data;

import javax.validation.constraints.*;
import java.io.Serializable;

/**
 * @author chushiyan
 * @email  Y2h1c2hpeWFuMDQxNUAxNjMuY29t(base64)
 * @description
 */
@Data
public class User implements Serializable {

    private String id;

    @NotNull(message = "姓名不能为空")
    @Size(min = 1, max = 20, message = "姓名长度必须在1-20之间")
    private String name;

    @Min(value = 10, message = "年龄必须大于10")
    @Max(value = 150, message = "年龄必须小于150")
    private Integer age;

    @Email(message = "邮箱格式不正确")
    private String email;
}
```

### （二）Controller中加上注解

在controller中使用@Valid 或者@Validated 注解校验

```java
import com.chushiyan.validation_tutorial.entity.Result;
import com.chushiyan.validation_tutorial.pojo.User;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

/**
 * @author chushiyan
 * @email  Y2h1c2hpeWFuMDQxNUAxNjMuY29t(base64)
 * @description
 */
@RestController
@RequestMapping("/user")
public class UserController  {

    @PostMapping
    public Result test(@Valid  @RequestBody User user){
        System.out.println(user);
        return new Result(true,200,"");
    }
}
```

### （三）测试

使用postman发送POST请求：http://localhost:10000/user

```json
{
	"age":120,
	"email":"chushiyan"
}
```

控制台打印：

```
WARN 12476 --- [io-10000-exec-1] .w.s.m.s.DefaultHandlerExceptionResolver : Resolved [org.springframework.web.bind.MethodArgumentNotValidException: Validation failed for argument [0] in public com.chushiyan.validation_tutorial.entity.Result com.chushiyan.validation_tutorial.controller.UserController.test(com.chushiyan.validation_tutorial.pojo.User) with 2 errors: [Field error in object 'user' on field 'name': rejected value [null]; codes [NotNull.user.name,NotNull.name,NotNull.java.lang.String,NotNull]; arguments [org.springframework.context.support.DefaultMessageSourceResolvable: codes [user.name,name]; arguments []; default message [name]]; default message [姓名不能为空]] [Field error in object 'user' on field 'email': rejected value [chushiyan]; codes [Email.user.email,Email.email,Email.java.lang.String,Email]; arguments [org.springframework.context.support.DefaultMessageSourceResolvable: codes [user.email,email]; arguments []; default message [email],[Ljavax.validation.constraints.Pattern$Flag;@20195c7a,.*]; default message [邮箱格式不正确]] ]

```

响应的数据：

```json
{
    "timestamp": "2019-12-03T09:14:00.759+0000",
    "status": 400,
    "error": "Bad Request",
    "errors": [
        {
            "codes": [
                "NotNull.user.name",
                "NotNull.name",
                "NotNull.java.lang.String",
                "NotNull"
            ],
            "arguments": [
                {
                    "codes": [
                        "user.name",
                        "name"
                    ],
                    "arguments": null,
                    "defaultMessage": "name",
                    "code": "name"
                }
            ],
            "defaultMessage": "姓名不能为空",
            "objectName": "user",
            "field": "name",
            "rejectedValue": null,
            "bindingFailure": false,
            "code": "NotNull"
        },
        {
            "codes": [
                "Email.user.email",
                "Email.email",
                "Email.java.lang.String",
                "Email"
            ],
            "arguments": [
                {
                    "codes": [
                        "user.email",
                        "email"
                    ],
                    "arguments": null,
                    "defaultMessage": "email",
                    "code": "email"
                },
                [],
                {
                    "arguments": null,
                    "defaultMessage": ".*",
                    "codes": [
                        ".*"
                    ]
                }
            ],
            "defaultMessage": "邮箱格式不正确",
            "objectName": "user",
            "field": "email",
            "rejectedValue": "chushiyan",
            "bindingFailure": false,
            "code": "Email"
        }
    ],
    "message": "Validation failed for object='user'. Error count: 2",
    "trace": "org.springframework.web.bind.MethodArgumentNotValidException: Validation failed for argument [0] in public com.chushiyan.validation_tutorial.entity.Result com.chushiyan.validation_tutorial.controller.UserController.test(com.chushiyan.validation_tutorial.pojo.User) with 2 errors: [Field error in object 'user' on field 'name': rejected value [null]; codes [NotNull.user.name,NotNull.name,NotNull.java.lang.String,NotNull]; arguments [org.springframework.context.support.DefaultMessageSourceResolvable: codes [user.name,name]; arguments []; default message [name]]; default message [姓名不能为空]] [Field error in object 'user' on field 'email': rejected value [chushiyan]; codes [Email.user.email,Email.email,Email.java.lang.String,Email]; arguments [org.springframework.context.support.DefaultMessageSourceResolvable: codes [user.email,email]; arguments []; default message [email],[Ljavax.validation.constraints.Pattern$Flag;@20195c7a,.*]; default message [邮箱格式不正确]] \r\n\tat  （博主进行了省略......）",
    "path": "/user"
}
```

### （四）全局处理异常

上面响应的错误肯定是不够友好的，所以需要进行异常处理。这里定义一个全局处理函数

```java
import com.chushiyan.validation_tutorial.entity.Result;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindException;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author chushiyan
 * @email  Y2h1c2hpeWFuMDQxNUAxNjMuY29t(base64)
 * @description
 */
@RestControllerAdvice
public class GlobalExceptionHandler {
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
    }
}
```



### （五）再次测试

使用postman发送POST请求：http://localhost:10000/user

```json
{
	"age":120,
	"email":"chushiyan"
}
```

响应的json数据：

```json
{
    "flag": false,
    "code": 20001,
    "message": "提交的数据校验失败",
    "data": {
        "timestamp": "2019-12-03T09:35:02.815+0000",
        "errors": [
            "邮箱格式不正确",
            "姓名不能为空"
        ]
    }
}
```



## 三、单个参数校验

### (一）直接在参数前加上校验注解：

```java
package com.chushiyan.validation_tutorial.controller;

import com.chushiyan.validation_tutorial.entity.Result;
import com.chushiyan.validation_tutorial.pojo.User;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

/**
 * @author chushiyan
 * @email  Y2h1c2hpeWFuMDQxNUAxNjMuY29t(base64)
 * @description
 */
@RestController
@RequestMapping("/user")
@Validated
public class UserController  {

    @GetMapping
    public Result test2(@NotNull(message = "name不能为空")  String name){
        System.out.println(name);
        return new Result(true,200,"");
    }
}

```

注意：需要在类上添加@Validated注解，否则不会校验。

### （二）全局处理函数

```java
import com.chushiyan.validation_tutorial.entity.Result;
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
 * @email  Y2h1c2hpeWFuMDQxNUAxNjMuY29t(base64)
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
        if(ex instanceof ConstraintViolationException){
            ConstraintViolationException exs = (ConstraintViolationException) ex;
            Set<ConstraintViolation<?>> violations = exs.getConstraintViolations();
            for (ConstraintViolation<?> item : violations) {
                errors.add(item.getMessage());
            }
        }
        body.put("errors", errors);
        return new Result(true, 20001, "提交的参数校验失败", body);
    }
}
```



### （二）测试

postman 测试http://localhost:10000/user GET

```java
{
    "flag": true,
    "code": 20001,
    "message": "提交的参数校验失败",
    "data": {
        "timestamp": "2019-12-03T09:58:45.212+0000",
        "errors": [
            "name不能为空"
        ]
    }
}
```

