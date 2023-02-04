package com.example.yunhists.controller;

import com.example.yunhists.entity.EmailVerification;
import com.example.yunhists.entity.User;
import com.example.yunhists.enumeration.ResultCodeEnum;
import com.example.yunhists.service.EmailVerificationService;
import com.example.yunhists.service.UserService;
import com.example.yunhists.utils.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.LinkedHashMap;
import java.util.Map;

@CrossOrigin
@RestController
@RequestMapping("/api/user")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private EmailVerificationService evService;

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
                        map.put("token", JwtHelper.createToken(user.getId().longValue()));
                        map.put("userId", user.getId());
                        map.put("username", user.getUsername());
                        map.put("userRights", user.getUserRights());
                        map.put("lang", user.getLang());
                        return Result.ok(map);
                    } else {
                        return Result.error(ResultCodeEnum.PASSWORD_INCORRECT);
                    }
            } else {
                return Result.error(ResultCodeEnum.REGISTERED_WITH_GOOGLE);
            }
        } else {
            return Result.error(ResultCodeEnum.EMAIL_NOT_REGISTERED);
        }
    }

    @PostMapping("/register")
    public Result<Object> register(
            @RequestParam("lang") String lang,
            @RequestParam("email") String email,
            @RequestParam("username") String username,
            @RequestParam("password") String pwd,
            @RequestParam("password2") String pwd2,
            @RequestParam("code") String code) {

        EmailVerification ev = evService.read(email);

        // 1. check email verification code send before
        if(ev != null) {

            // 2. check verification code expiration
            if (!EmailVerificationUtils.isExpiration(ev)) {

                // 3. check verification code correct
                if (EmailVerificationUtils.compareVerification(code, ev)) {

                    // 4. check email valid
                    if (UserUtils.validateEmail(email)) {

                        // 5. check email registered
                        if (userService.getUserByEmail(email) == null) {

                            // 6. check username valid
                            if (UserUtils.validateUsername(username)) {

                                // 7. check password length
                                if (UserUtils.validatePassword(pwd)) {

                                    // 8. check password matches
                                    if (UserUtils.validateConfirmPassword(pwd, pwd2)) {
                                        User user = new User(username, pwd, email, lang, 0);
                                        userService.register(user);
                                        return Result.ok();
                                    } else {
                                        return Result.error(ResultCodeEnum.PASSWORD_NOT_MATCH);
                                    }
                                } else {
                                    return Result.error(ResultCodeEnum.PASSWORD_LENGTH);
                                }
                            } else {
                                return Result.error(ResultCodeEnum.USERNAME_LENGTH);
                            }
                        } else {
                            return Result.error(ResultCodeEnum.EMAIL_ALREADY_REGISTERED);
                        }
                    } else {
                        return Result.error(ResultCodeEnum.INVALID_EMAIL);
                    }
                } else {
                    return Result.error(ResultCodeEnum.VERIFY_CODE_INCORRECT);
                }
            } else {
                return Result.error(ResultCodeEnum.VERIFY_CODE_EXPIRED);
            }
        } else {
            return Result.error(ResultCodeEnum.NO_VERIFICATION_CODE);
        }
    }

    @PostMapping("/updateLang")
    public Result<Object> updateLang(@RequestParam("lang") String lang,
                                     HttpServletRequest request) {
        Object obj = ControllerUtils.getUserIdFromToken(request);
        try {
            Integer id = (Integer) obj;
            if(userService.getUserById(id) != null) {
                userService.updateLang(id, lang);
                Map<String, Object> map = new LinkedHashMap<>();
                map.put("token", JwtHelper.createToken(Long.valueOf(id)));
                return Result.ok(map);
            } else {
                obj = Result.error(ResultCodeEnum.NO_USER);
                throw new Exception();
            }
        } catch (Exception e) {
            return (Result<Object>) obj;
        }
    }

    @PostMapping("/sendVerificationEmail")
    public Result<Object> sendVerificationEmail(@RequestParam("lang") String lang,
                                                @RequestParam("email") String email) {
        if(UserUtils.validateEmail(email)) {
            EmailVerification oldEv = evService.read(email);
            if(oldEv == null || !EmailVerificationUtils.repeatEmail(oldEv)) {
                try {
                    EmailVerification newEv = EmailVerificationUtils.createVerification(email);
                    DirectMailUtils.sendEmail(email, EmailContentHelper.getRegisterVerificationEmailSubject(lang), EmailContentHelper.getRegisterVerificationEmailBody(lang, newEv.getVerificationCode()));
                    evService.create(newEv);
                } catch (Exception e) {
                    return Result.error(ResultCodeEnum.EMAIL_FAIL);
                }
                return Result.ok();
            } else {
                return Result.error(ResultCodeEnum.LESS_THAN_ONE_MINUTE);
            }
        } else {
            return Result.error(ResultCodeEnum.INVALID_EMAIL);
        }
    }
}
