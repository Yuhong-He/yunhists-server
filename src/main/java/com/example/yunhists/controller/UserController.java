package com.example.yunhists.controller;

import com.example.yunhists.entity.User;
import com.example.yunhists.enumeration.ResultCodeEnum;
import com.example.yunhists.service.UserService;
import com.example.yunhists.utils.JwtHelper;
import com.example.yunhists.utils.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.LinkedHashMap;
import java.util.Map;

@CrossOrigin
@RestController
@RequestMapping("/api/user")
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping("/login")
    public Result<Object> login(@RequestParam("email") String email,
                                @RequestParam("password") String password) {

        // 1. Check user exist
        User u = userService.getUserByEmail(email);
        if(u != null) {

            // 2. Check user register type, only allow user who registered with email
            if(u.getRegisterType() == 0) {
                Map<String, Object> map = new LinkedHashMap<>();
                    User user = userService.login(email, password);
                    if(user != null){
                        map.put("token", JwtHelper.createToken(user.getId().longValue(), user.getUserRights()));
                        map.put("userId", user.getId());
                        map.put("username", user.getUsername());
                        map.put("userRights", user.getUserRights());
                        return Result.ok(map);
                    } else {
                        return Result.error(ResultCodeEnum.WRONG_PWD);
                    }
            } else {
                return Result.error(ResultCodeEnum.REGISTERED_WITH_GOOGLE);
            }
        } else {
            return Result.error(ResultCodeEnum.EMAIL_NOT_REGISTERED);
        }
    }
}
