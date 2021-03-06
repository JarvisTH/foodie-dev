package com.imooc.pojo.bo.center;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.*;

/**
 * @author Jarvis(Tang Hui)
 * @version 1.0
 * @date 2020/4/12 18:09
 */
@ApiModel(value = "用户对象",description = "从客户端传入的数据封装在此entity中")
public class CenterUserBO {

    @ApiModelProperty(value = "用户名",name = "username",example = "imooc",required = true)
    private String username;
    @ApiModelProperty(value = "密码",name = "password",example = "123456",required = true)
    private String password;
    @ApiModelProperty(value = "确认密码",name = "confirmPassword",example = "123456",required = false)
    private String confirmPassword;

    @NotBlank(message = "用户昵称不能为空")
    @Length(max = 12,message = "用户昵称不能超过12位")
    @ApiModelProperty(value = "昵称",name = "nickname",example = "imooc",required = true)
    private String nickname;

    @NotBlank(message = "用户真实姓名不能为空")
    @Length(max = 12,message = "用户真实姓名不能超过12位")
    @ApiModelProperty(value = "真实姓名",name = "realname",example = "imooc",required = true)
    private String realname;

    @Pattern(regexp ="^(((13[0-9]{1})|(15[0-9]{1})|(18[0-9]{1}))+\\d{8})$",message = "手机号格式不正确")
    @ApiModelProperty(value = "手机号",name = "mobile",example = "13000000000",required = false)
    private String mobile;

    @Email
    @ApiModelProperty(value = "邮箱地址",name = "email",example = "123@qq.com",required = true)
    private String email;

    @Min(value = 1,message = "性别选择不正确")
    @Max(value = 3,message = "性别选择不正确")
    @ApiModelProperty(value = "性别",name = "sex",example = "1,女；2，男；3保密",required = true)
    private String sex;
    @ApiModelProperty(value = "生日",name = "birthday",example = "1900-01-01",required = false)
    private String birthday;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getConfirmPassword() {
        return confirmPassword;
    }

    public void setConfirmPassword(String confirmPassword) {
        this.confirmPassword = confirmPassword;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getRealname() {
        return realname;
    }

    public void setRealname(String realname) {
        this.realname = realname;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public String getBirthday() {
        return birthday;
    }

    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }
}
