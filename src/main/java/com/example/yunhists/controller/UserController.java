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
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;

import static com.example.yunhists.utils.ControllerUtils.printException;

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
                    map.put("email", user.getEmail());
                    map.put("lang", user.getLang());
                    map.put("userRights", user.getUserRights());
                    map.put("points", user.getPoints());
                    map.put("sts", STSUtils.getSTS(user.getId()));

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

    @PostMapping("/google")
    public Result<Object> google(@RequestParam("email") String email,
                                 @RequestParam("username") String username,
                                 @RequestParam("lang") String lang) throws IOException {


        // 4. check lang valid
        if(UserUtils.validateLang(lang)) {

            // 5. check email registered
            User user = userService.getUserByEmail(email);
            if (user == null) { // google register

                User newUser = new User(username, "", email, lang, 1);
                int userId = userService.googleRegister(newUser);

                Map<String, Object> map = new LinkedHashMap<>();
                map.put("token", JwtHelper.createToken((long) userId));
                map.put("userId", userId);
                map.put("username", username);
                map.put("email", email);
                map.put("lang", lang);
                map.put("userRights", 0);
                map.put("points", 0);
                map.put("sts", STSUtils.getSTS(userId));

                return Result.ok(map);

            } else if(user.getRegisterType() == 1) { // google login
                Map<String, Object> map = new LinkedHashMap<>();
                map.put("token", JwtHelper.createToken(user.getId().longValue()));
                map.put("userId", user.getId());
                map.put("username", user.getUsername());
                map.put("email", user.getEmail());
                map.put("lang", user.getLang());
                map.put("userRights", user.getUserRights());
                map.put("points", user.getPoints());
                map.put("sts", STSUtils.getSTS(user.getId()));
                return Result.ok(map);

            } else { // email registered user
                return Result.error(ResultCodeEnum.EMAIL_ALREADY_REGISTERED);
            }
        } else {
            return Result.error(ResultCodeEnum.INVALID_LANG);
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

    @GetMapping("/getUserInfo")
    public Result<Object> getUserInfo(HttpServletRequest request) {
        // 1. get token
        Object obj = ControllerUtils.getUserIdFromToken(request);
        try {

            // 2. get id (if obj is not number, throw exception, case token error)
            Integer id = (Integer) obj;

            // 3. check user exist
            User user = userService.getUserById(id);
            if(user != null) {

                // 4. get userinfo
                Map<String, Object> map = new LinkedHashMap<>();
                map.put("userId", user.getId());
                map.put("username", user.getUsername());
                map.put("email", user.getEmail());
                map.put("userRights", user.getUserRights());
                map.put("points", user.getPoints());
                map.put("registration", user.getRegisterType());
                return Result.ok(map);
            } else {
                obj = Result.error(ResultCodeEnum.NO_USER);
                throw new Exception();
            }
        } catch (Exception e) {
            try{
                return (Result<Object>) obj;
            } catch (Exception exception) {
                printException(e);
                return Result.error(e.getMessage(), ResultCodeEnum.FAIL);
            }
        }
    }

    @PostMapping("/sendRegisterEmail")
    public Result<Object> sendRegisterEmail(@RequestParam("lang") String lang,
                                            @RequestParam("email") String email) {

        // 1. check email format
        if(UserUtils.validateEmail(email)) {

            // 2. check email registered
            if (userService.getUserByEmail(email) == null) {

                // 3. check that no emails have been sent within a minute
                EmailTimer oldEmailTimer = emailTimerService.read(email, "verificationCode");
                if(oldEmailTimer == null || !EmailTimerUtils.repeatEmail(oldEmailTimer)) {

                    try {
                        // a. generate verification code
                        EmailVerification emailVerification = EmailVerificationUtils.createVerification(email);

                        // b. send email (may throw exception)
                        DirectMailUtils.sendEmail(
                                email, EmailContentHelper.getRegisterVerificationEmailSubject(lang),
                                EmailContentHelper.getRegisterVerificationEmailBody(lang,
                                        emailVerification.getVerificationCode()));

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
                return Result.error(ResultCodeEnum.EMAIL_ALREADY_REGISTERED);
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
                        DirectMailUtils.sendEmail(email, EmailContentHelper.getResetPasswordEmailSubject(
                                user.getLang()), EmailContentHelper.getResetPasswordEmailBody(
                                        user.getLang(), user.getUsername(), pwd));

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
                    return Result.ok();
                } else {
                    obj = Result.error(ResultCodeEnum.INVALID_LANG);
                    throw new Exception();
                }
            } else {
                obj = Result.error(ResultCodeEnum.NO_USER);
                throw new Exception();
            }
        } catch (Exception e) {
            try{
                return (Result<Object>) obj;
            } catch (Exception exception) {
                printException(e);
                return Result.error(e.getMessage(), ResultCodeEnum.FAIL);
            }
        }
    }

    @PostMapping("/delete")
    public Result<Object> delete(HttpServletRequest request) {

        // 1. get token
        Object obj = ControllerUtils.getUserIdFromToken(request);
        try {

            // 2. get id (if obj is not number, throw exception, case token error)
            Integer id = (Integer) obj;

            // 3. check user exist
            if(userService.getUserById(id) != null) {

                // 4. Delete user
                userService.updateUserToDeletedUser(id);
                return Result.ok();
            } else {
                obj = Result.error(ResultCodeEnum.NO_USER);
                throw new Exception();
            }
        } catch (Exception e) {
            try{
                return (Result<Object>) obj;
            } catch (Exception exception) {
                printException(e);
                return Result.error(e.getMessage(), ResultCodeEnum.FAIL);
            }
        }
    }

    @PostMapping("/updateUsername")
    public Result<Object> updateUsername(@RequestParam("username") String username,
                                     HttpServletRequest request) {

        // 1. get token
        Object obj = ControllerUtils.getUserIdFromToken(request);
        try {

            // 2. get id (if obj is not number, throw exception, case token error)
            Integer id = (Integer) obj;

            // 3. check user exist
            if(userService.getUserById(id) != null) {

                if(UserUtils.validateUsername(username)) {
                    userService.updateUsername(id, username);
                    return Result.ok();
                } else {
                    return Result.error(ResultCodeEnum.USERNAME_LENGTH);
                }
            } else {
                obj = Result.error(ResultCodeEnum.NO_USER);
                throw new Exception();
            }
        } catch (Exception e) {
            try{
                return (Result<Object>) obj;
            } catch (Exception exception) {
                printException(e);
                return Result.error(e.getMessage(), ResultCodeEnum.FAIL);
            }
        }
    }

    @PostMapping("/sendChangeEmailEmail")
    public Result<Object> sendChangeEmailEmail(@RequestParam("email") String email,
                                            HttpServletRequest request) {

        // 1. get token
        Object obj = ControllerUtils.getUserIdFromToken(request);
        try {

            // 2. get id (if obj is not number, throw exception, case token error)
            Integer id = (Integer) obj;

            // 3. check user exist
            User user = userService.getUserById(id);
            if(user != null) {

                // 4. check email format
                if(UserUtils.validateEmail(email)) {

                    // 5. check email registered
                    if (userService.getUserByEmail(email) == null) {

                        // 6. check that no emails have been sent within a minute
                        EmailTimer oldEmailTimer = emailTimerService.read(email, "verificationCode");
                        if (oldEmailTimer == null || !EmailTimerUtils.repeatEmail(oldEmailTimer)) {

                            try {
                                // a. generate verification code
                                EmailVerification emailVerification =
                                        EmailVerificationUtils.createVerification(email);

                                // b. send email (may throw exception)
                                DirectMailUtils.sendEmail(
                                        email, EmailContentHelper.getChangeEmailVerificationEmailSubject(
                                                user.getLang()),
                                        EmailContentHelper.getChangeEmailVerificationEmailBody(user.getLang(),
                                                user.getUsername(), emailVerification.getVerificationCode()));

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
                        return Result.error(ResultCodeEnum.EMAIL_ALREADY_REGISTERED);
                    }
                } else {
                    return Result.error(ResultCodeEnum.INVALID_EMAIL);
                }
            } else {
                obj = Result.error(ResultCodeEnum.NO_USER);
                throw new Exception();
            }
        } catch (Exception e) {
            try{
                return (Result<Object>) obj;
            } catch (Exception exception) {
                printException(e);
                return Result.error(e.getMessage(), ResultCodeEnum.FAIL);
            }
        }
    }

    @PostMapping("/updateEmail")
    public Result<Object> updateEmail(@RequestParam("email") String email,
                                      @RequestParam("password") String password,
                                      @RequestParam("code") String code,
                                      HttpServletRequest request) {

        EmailVerification ev = evService.read(email);

        // 1. get token
        Object obj = ControllerUtils.getUserIdFromToken(request);
        try {

            // 2. get id (if obj is not number, throw exception, case token error)
            Integer id = (Integer) obj;

            // 3. check user exist
            User user = userService.getUserById(id);
            if(user != null) {

                // 4. check user registered with email
                if(user.getRegisterType() == 0) {

                    // 5. check user password
                    if(userService.login(user.getEmail(), password) != null) {

                        // 6. check email verification code send before
                        if(ev != null) {

                            // 7. check verification code expiration
                            if (!EmailVerificationUtils.isExpiration(ev)) {

                                // 8. check verification code correct
                                if (EmailVerificationUtils.compareVerification(code, ev)) {

                                    userService.updateEmail(id, email);
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
                        return Result.error(ResultCodeEnum.PASSWORD_INCORRECT);
                    }
                } else {
                    return Result.error(ResultCodeEnum.REGISTERED_WITH_GOOGLE);
                }
            } else {
                obj = Result.error(ResultCodeEnum.NO_USER);
                throw new Exception();
            }
        } catch (Exception e) {
            try{
                return (Result<Object>) obj;
            } catch (Exception exception) {
                printException(e);
                return Result.error(e.getMessage(), ResultCodeEnum.FAIL);
            }
        }
    }

    @PostMapping("/updatePassword")
    public Result<Object> updatePassword(@RequestParam("oldPwd") String oldPwd,
                                      @RequestParam("newPwd") String newPwd,
                                      @RequestParam("newPwd2") String newPwd2,
                                      HttpServletRequest request) {

        // 1. get token
        Object obj = ControllerUtils.getUserIdFromToken(request);
        try {

            // 2. get id (if obj is not number, throw exception, case token error)
            Integer id = (Integer) obj;

            // 3. check user exist
            User user = userService.getUserById(id);
            if(user != null) {

                // 4. check old password correct
                if(userService.login(user.getEmail(), oldPwd) != null) {

                    // 5. check new password
                    if(UserUtils.validatePassword(newPwd)) {

                        // 6. check confirm password
                        if(UserUtils.validateConfirmPassword(newPwd, newPwd2)) {
                            userService.updatePassword(id, newPwd);
                            return Result.ok();
                        } else {
                            return Result.error(ResultCodeEnum.PASSWORD_NOT_MATCH);
                        }
                    } else {
                        return Result.error(ResultCodeEnum.PASSWORD_LENGTH);
                    }
                } else {
                    return Result.error(ResultCodeEnum.PASSWORD_INCORRECT);
                }
            } else {
                obj = Result.error(ResultCodeEnum.NO_USER);
                throw new Exception();
            }
        } catch (Exception e) {
            try{
                return (Result<Object>) obj;
            } catch (Exception exception) {
                printException(e);
                return Result.error(e.getMessage(), ResultCodeEnum.FAIL);
            }
        }
    }

    @GetMapping("/validateToken")
    public Result<Object> validateToken(HttpServletRequest request) {

        String token = HttpServletUtils.getToken(request);
        if(!token.equals("")) {
            if(JwtHelper.isExpiration(token)) {
                return Result.error(ResultCodeEnum.TOKEN_EXPIRED);
            } else {
                return Result.ok();
            }
        } else {
            return Result.error(ResultCodeEnum.MISS_TOKEN);
        }
    }

    @GetMapping("/refreshSTS")
    public Result<Object> refreshSTS(HttpServletRequest request) {

        // 1. get token
        Object obj = ControllerUtils.getUserIdFromToken(request);
        try {

            // 2. get id (if obj is not number, throw exception, case token error)
            Integer id = (Integer) obj;

            // 3. check user exist
            if(userService.getUserById(id) != null) {

                Map<String, Object> map = new LinkedHashMap<>();
                map.put("sts", STSUtils.getSTS(id));
                return Result.ok(map);

            } else {
                obj = Result.error(ResultCodeEnum.NO_USER);
                throw new Exception();
            }
        } catch (Exception e) {
            try{
                return (Result<Object>) obj;
            } catch (Exception exception) {
                printException(e);
                return Result.error(e.getMessage(), ResultCodeEnum.FAIL);
            }
        }
    }
}
