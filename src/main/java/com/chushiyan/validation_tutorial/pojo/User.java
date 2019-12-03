package com.chushiyan.validation_tutorial.pojo;

import com.chushiyan.validation_tutorial.validate.GroupA;
import lombok.Data;

import javax.validation.constraints.*;
import java.io.Serializable;

/**
 * @author chushiyan
 * @email chushiyan0415@163.com
 * @description
 */
@Data
public class User implements Serializable {

    @NotNull(groups = GroupA.class, message = "id不能为空")
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
