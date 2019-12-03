package com.chushiyan.validation_tutorial.controller;

import com.chushiyan.validation_tutorial.entity.Result;
import com.chushiyan.validation_tutorial.pojo.User;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

/**
 * @author chushiyan
 * @email chushiyan0415@163.com
 * @description
 */
@RestController
@RequestMapping("/user")
@Validated
public class UserController  {

    @PostMapping
    public Result test(@Valid  @RequestBody User user){
        System.out.println(user);
        return new Result(true,200,"");
    }

    @GetMapping
    public Result test2(@NotNull(message = "name不能为空")  String name){
        System.out.println(name);
        return new Result(true,200,"");
    }
}
