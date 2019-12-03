package com.chushiyan.validation_tutorial.controller;

import com.chushiyan.validation_tutorial.entity.Result;
import com.chushiyan.validation_tutorial.pojo.User;
import com.chushiyan.validation_tutorial.validate.GroupA;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.groups.Default;

/**
 * @author chushiyan
 * @email chushiyan0415@163.com
 * @description
 */
@RestController
@RequestMapping("/user")
@Validated
public class UserController {

    @PostMapping("/test")
    public Result test(@Valid @RequestBody User user) {
        System.out.println(user);
        return new Result(true, 200, "");
    }

    @GetMapping
    public Result test2(@NotNull(message = "name不能为空") String name) {
        System.out.println(name);
        return new Result(true, 200, "");
    }


    @PostMapping
    public Result add(@Validated @RequestBody User user) {
        return new Result(true, 200, "增加用户成功");
    }

    @PutMapping("/update")
    // 指定GroupA，这样就会校验id属性是否为空
    // 注意：还得必须添加Default.class，否则不会执行其他的校验（如我们案例中的@Email）
    public Result update(@Validated({GroupA.class, Default.class}) @RequestBody User user) {
        return new Result(true, 200, "修改用户成功");
    }
}
