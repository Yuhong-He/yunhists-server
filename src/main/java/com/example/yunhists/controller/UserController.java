package com.example.yunhists.controller;

import com.example.yunhists.entity.EmailTimer;
import com.example.yunhists.entity.EmailVerification;
import com.example.yunhists.entity.User;
import com.example.yunhists.enumeration.ResultCodeEnum;
import com.example.yunhists.service.EmailTimerService;
import com.example.yunhists.service.EmailVerificationService;
import com.example.yunhists.service.UserService;
import com.example.yunhists.utils.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
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

    @Autowired
    private EmailTimerService emailTimerService;

    @PostMapping("/login")
    public Result<Object> login(@RequestParam("email") String email,
                                @RequestParam("password") String password) throws IOException {

        // 1. Check user exist
        User u = userService.getUserByEmail(email);
        if(u != null) {

            // 2. Check user register type, only allow user who registered with email
            if(u.getRegisterType() == 0) {

                    // 3. check password
                    User user = userService.login(email, password);
                    if(user != null){

                        Map<String, Object> map = new LinkedHashMap<>();
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

        // 1. check username valid
        if (UserUtils.validateUsername(username)) {

            // 2. check password length
            if (UserUtils.validatePassword(pwd)) {

                // 3. check password matches
                if (UserUtils.validateConfirmPassword(pwd, pwd2)) {

                    // 4. check lang valid
                    if(UserUtils.validateLang(lang)) {

                        // 5. check email registered
                        if (userService.getUserByEmail(email) == null) {

                            // 6. check email verification code send before
                            if(ev != null) {

                                // 7. check verification code expiration
                                if (!EmailVerificationUtils.isExpiration(ev)) {

                                    // 8. check verification code correct
                                    if (EmailVerificationUtils.compareVerification(code, ev)) {
                                        User user = new User(username, pwd, email, lang, 0);
                                        userService.register(user);
                                        return Result.ok();
                                    } else {
                                        return Result.error(ResultCodeEnum.VERIFY_CODE_INCORRECT);
                                    }
                                } else {
                                    return Result.error(ResultCodeEnum.VERIFY_CODE_EXPIRED);
                                }
                            } else {
                                return Result.error(ResultCodeEnum.NO_VERIFICATION_CODE);
                            }
                        } else {
                            return Result.error(ResultCodeEnum.EMAIL_ALREADY_REGISTERED);
                        }
                    } else {
                        return Result.error(ResultCodeEnum.INVALID_LANG);
                    }
                } else {
                    return Result.error(ResultCodeEnum.PASSWORD_NOT_MATCH);
                }
            } else {
                return Result.error(ResultCodeEnum.PASSWORD_LENGTH);
            }
        } else {
            return Result.error(ResultCodeEnum.USERNAME_LENGTH);
        }
    }

    @PostMapping("/sendVerificationEmail")
    public Result<Object> sendVerificationEmail(@RequestParam("lang") String lang,
                                                @RequestParam("email") String email) {

        // 1. check email format
        if(UserUtils.validateEmail(email)) {

            // 2. check that no emails have been sent within a minute
            EmailTimer oldEmailTimer = emailTimerService.read(email, "verificationCode");
            if(oldEmailTimer == null || !EmailTimerUtils.repeatEmail(oldEmailTimer)) {

                try {
                    // a. generate verification code
                    EmailVerification emailVerification = EmailVerificationUtils.createVerification(email);

                    // b. send email (may throw exception)
                    DirectMailUtils.sendEmail(email, EmailContentHelper.getRegisterVerificationEmailSubject(lang), EmailContentHelper.getRegisterVerificationEmailBody(lang, emailVerification.getVerificationCode()));

                    // c. record verification code
                    evService.create(emailVerification);

                    // d. record the action in email timer
                    EmailTimer newEmailTimer = new EmailTimer(email, "verificationCode");
                    emailTimerService.create(newEmailTimer);

                    return Result.ok();

                } catch (Exception e) {
                    System.out.println("Email Error: " + e);
                    return Result.error(ResultCodeEnum.EMAIL_FAIL);
                }
            } else {
                return Result.error(ResultCodeEnum.LESS_THAN_ONE_MINUTE);
            }
        } else {
            return Result.error(ResultCodeEnum.INVALID_EMAIL);
        }
    }

    @PostMapping("/resetPassword")
    public Result<Object> resetPassword(@RequestParam("email") String email) {

        // 1. check email format
        if(UserUtils.validateEmail(email)) {

            // 2. check user exist
            User user = userService.getUserByEmail(email);
            if(user != null) {

                // 3. check that no emails have been sent within a minute
                EmailTimer oldEmailTimer = emailTimerService.read(email, "resetPwd");
                if(oldEmailTimer == null || !EmailTimerUtils.repeatEmail(oldEmailTimer)) {

                    try {
                        // a. generate new password
                        String pwd = UserUtils.generateRandomPwd();

                        // b. send email (may throw exception)
                        DirectMailUtils.sendEmail(email, EmailContentHelper.getResetPasswordEmailSubject(user.getLang()), EmailContentHelper.getResetPasswordEmailBody(user.getLang(), user.getUsername(), pwd));

                        // c. update user password
                        userService.updatePassword(user.getId(), pwd);

                        // d. record the action in email timer
                        EmailTimer newEmailTimer = new EmailTimer(email, "resetPwd");
                        emailTimerService.create(newEmailTimer);

                        return Result.ok();

                    } catch (Exception e) {
                        System.out.println("Email Error: " + e);
                        return Result.error(ResultCodeEnum.EMAIL_FAIL);
                    }
                } else {
                    return Result.error(ResultCodeEnum.LESS_THAN_ONE_MINUTE);
                }
            } else {
                return Result.error(ResultCodeEnum.EMAIL_NOT_REGISTERED);
            }
        } else {
            return Result.error(ResultCodeEnum.INVALID_EMAIL);
        }
    }

    @PostMapping("/updateLang")
    public Result<Object> updateLang(@RequestParam("lang") String lang,
                                     HttpServletRequest request) {

        // 1. get token
        Object obj = ControllerUtils.getUserIdFromToken(request);
        try {

            // 2. get id (if obj is not number, throw exception, case token error)
            Integer id = (Integer) obj;

            // 3. check user exist
            if(userService.getUserById(id) != null) {

                // 4. check lang valid
                if(UserUtils.validateLang(lang)) {
                    userService.updateLang(id, lang);
                    Map<String, Object> map = new LinkedHashMap<>();
                    map.put("token", JwtHelper.createToken(Long.valueOf(id)));
                    return Result.ok(map);
                } else {
                    obj = Result.error(ResultCodeEnum.INVALID_LANG);
                    throw new Exception();
                }
            } else {
                obj = Result.error(ResultCodeEnum.NO_USER);
                throw new Exception();
            }
        } catch (Exception e) {
            return (Result<Object>) obj;
        }
    }
}
