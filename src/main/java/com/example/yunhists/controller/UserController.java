package com.example.yunhists.controller;

import com.example.yunhists.common.BaseContext;
import com.example.yunhists.common.Result;
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

import java.util.LinkedHashMap;
import java.util.List;
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

    @GetMapping("/login")
    public Result<Object> login(@RequestParam("email") String email,
                                @RequestParam("password") String password) {

        // 1. Check user exist
        User u = userService.getUserByEmail(email);
        if(u != null) {

            // 2. Check user register type, only allow user who registered with email
            if(u.getRegisterType() == 0) {

                // 3. check password
                User user = userService.login(email, password);
                if(user != null){

                    Map<String, Object> map = new LinkedHashMap<>();
                    map.put("access_token", JwtHelper.createAccessToken(user.getId().longValue()));
                    map.put("refresh_token", JwtHelper.createRefreshToken(user.getId().longValue()));
                    map.put("expired_time", JwtHelper.getExpiredTime());
                    map.put("username", user.getUsername());
                    map.put("lang", user.getLang());
                    map.put("userRights", user.getUserRights());
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

    @GetMapping("/google")
    public Result<Object> google(@RequestParam("email") String email,
                                 @RequestParam("username") String username,
                                 @RequestParam("lang") String lang) {


        // 4. check lang valid
        if(UserUtils.validateLang(lang)) {

            // 5. check email registered
            User user = userService.getUserByEmail(email);
            if (user == null) { // google register

                User newUser = new User(username, "", email, lang, 1);
                userService.googleRegister(newUser);
                int userId = newUser.getId();

                Map<String, Object> map = new LinkedHashMap<>();
                map.put("access_token", JwtHelper.createAccessToken((long) userId));
                map.put("refresh_token", JwtHelper.createRefreshToken((long) userId));
                map.put("expired_time", JwtHelper.getExpiredTime());
                map.put("username", username);
                map.put("lang", lang);
                map.put("userRights", 0);
                map.put("sts", STSUtils.getSTS(userId));

                return Result.ok(map);

            } else if(user.getRegisterType() == 1) { // google login
                Map<String, Object> map = new LinkedHashMap<>();
                map.put("access_token", JwtHelper.createAccessToken(user.getId().longValue()));
                map.put("refresh_token", JwtHelper.createRefreshToken(user.getId().longValue()));
                map.put("expired_time", JwtHelper.getExpiredTime());
                map.put("username", user.getUsername());
                map.put("lang", user.getLang());
                map.put("userRights", user.getUserRights());
                map.put("sts", STSUtils.getSTS(user.getId()));
                return Result.ok(map);

            } else { // email registered user
                return Result.error(ResultCodeEnum.EMAIL_ALREADY_REGISTERED);
            }
        } else {
            return Result.error(ResultCodeEnum.INVALID_LANG);
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
                if (pwd.equals(pwd2)) {

                    // 4. check lang valid
                    if(UserUtils.validateLang(lang)) {

                        // 5. check email registered
                        if (userService.getUserByEmail(email) == null) {

                            // 6. check email verification code send before
                            if(ev != null) {

                                // 7. check verification code expiration
                                if (!EmailVerificationUtils.isExpiration(ev)) {

                                    // 8. check verification code correct
                                    if (code.equals(ev.getVerificationCode())) {
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

    @PostMapping("/resetPassword")
    public Result<Object> resetPassword(@RequestParam("email") String email) {

        // 1. check email format
        if(UserUtils.validateEmail(email)) {

            // 2. check user exist
            User user = userService.getUserByEmail(email);
            if(user != null) {

                // 3. check user registered with email
                if(user.getRegisterType() == 0) {

                    // 4. check that no emails have been sent within a minute
                    EmailTimer oldEmailTimer = emailTimerService.read(email, "resetPwd");
                    if(oldEmailTimer == null || !EmailTimerUtils.repeatEmail(oldEmailTimer)) {

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
                    } else {
                        return Result.error(ResultCodeEnum.LESS_THAN_ONE_MINUTE);
                    }
                } else {
                    return Result.error(ResultCodeEnum.REGISTERED_WITH_GOOGLE);
                }
            } else {
                return Result.error(ResultCodeEnum.EMAIL_NOT_REGISTERED);
            }
        } else {
            return Result.error(ResultCodeEnum.INVALID_EMAIL);
        }
    }

    @GetMapping("/getUserInfo")
    public Result<Object> getUserInfo() {

        Integer id = Math.toIntExact(BaseContext.getCurrentId());

        User user = userService.getUserById(id);
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("userId", user.getId());
        map.put("username", user.getUsername());
        map.put("email", user.getEmail());
        map.put("userRights", user.getUserRights());
        map.put("points", user.getPoints());
        map.put("sendEmail", user.getSendEmail());
        map.put("registration", user.getRegisterType());
        return Result.ok(map);
    }

    @PutMapping("/updateLang")
    public Result<Object> updateLang(@RequestParam("lang") String lang) {

        Integer id = Math.toIntExact(BaseContext.getCurrentId());

        if(UserUtils.validateLang(lang)) {
            User user = userService.getUserById(id);
            user.setLang(lang);
            userService.saveOrUpdate(user);
            return Result.ok();
        } else {
            return Result.error(ResultCodeEnum.INVALID_LANG);
        }
    }

    @PutMapping("/updateEmailNotification")
    public Result<Object> updateEmailNotification(@RequestParam("status") String status) {

        Integer id = Math.toIntExact(BaseContext.getCurrentId());

        if(List.of("ON", "OFF").contains(status)) {
            User user = userService.getUserById(id);
            user.setSendEmail(status);
            userService.saveOrUpdate(user);
            return Result.ok();
        } else {
            return Result.error(ResultCodeEnum.INVALID_PARAM);
        }
    }

    @DeleteMapping("/delete")
    public Result<Object> delete() {

        Integer id = Math.toIntExact(BaseContext.getCurrentId());
        User user = userService.getUserById(id);
        user.setUsername("Deleted User");
        user.setEmail("");
        user.setPassword("");
        user.setUserRights(0);
        userService.saveOrUpdate(user);
        return Result.ok();
    }

    @PutMapping("/updateUsername")
    public Result<Object> updateUsername(@RequestParam("username") String username) {

        Integer id = Math.toIntExact(BaseContext.getCurrentId());

        if(UserUtils.validateUsername(username)) {
            User user = userService.getUserById(id);
            user.setUsername(username);
            userService.saveOrUpdate(user);
            return Result.ok();
        } else {
            return Result.error(ResultCodeEnum.USERNAME_LENGTH);
        }
    }

    @PostMapping("/sendChangeEmailEmail")
    public Result<Object> sendChangeEmailEmail(@RequestParam("email") String email) {

        // 1. get id
        Integer id = Math.toIntExact(BaseContext.getCurrentId());
        User user = userService.getUserById(id);

        // 2. check user registered with email
        if(user.getRegisterType() == 0) {

            // 3. check email format
            if(UserUtils.validateEmail(email)) {

                // 4. check email registered
                if (userService.getUserByEmail(email) == null) {

                    // 5. check that no emails have been sent within a minute
                    EmailTimer oldEmailTimer = emailTimerService.read(email, "verificationCode");
                    if (oldEmailTimer == null || !EmailTimerUtils.repeatEmail(oldEmailTimer)) {

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
            return Result.error(ResultCodeEnum.REGISTERED_WITH_GOOGLE);
        }
    }

    @PutMapping("/updateEmail")
    public Result<Object> updateEmail(@RequestParam("email") String email,
                                      @RequestParam("password") String password,
                                      @RequestParam("code") String code) {

        EmailVerification ev = evService.read(email);

        // 1. get id
        Integer id = Math.toIntExact(BaseContext.getCurrentId());
        User user = userService.getUserById(id);

        // 2. check user registered with email
        if(user.getRegisterType() == 0) {

            // 3. check user password
            if(userService.login(user.getEmail(), password) != null) {

                // 4. check email verification code send before
                if(ev != null) {

                    // 5. check verification code expiration
                    if (!EmailVerificationUtils.isExpiration(ev)) {

                        // 6. check verification code correct
                        if (code.equals(ev.getVerificationCode())) {

                            user.setEmail(email);
                            userService.saveOrUpdate(user);
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
    }

    @PutMapping("/updatePassword")
    public Result<Object> updatePassword(@RequestParam("oldPwd") String oldPwd,
                                      @RequestParam("newPwd") String newPwd,
                                      @RequestParam("newPwd2") String newPwd2) {

        // 1. get id
        Integer id = Math.toIntExact(BaseContext.getCurrentId());
        User user = userService.getUserById(id);

        // 2. check user registered with email
        if(user.getRegisterType() == 0) {

            // 3. check old password correct
            if(userService.login(user.getEmail(), oldPwd) != null) {

                // 4. check new password
                if(UserUtils.validatePassword(newPwd)) {

                    // 5. check confirm password
                    if(newPwd.equals(newPwd2)) {
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
            return Result.error(ResultCodeEnum.REGISTERED_WITH_GOOGLE);
        }
    }

    @GetMapping("/refreshSTS")
    public Result<Object> refreshSTS() {

        Integer id = Math.toIntExact(BaseContext.getCurrentId());

        Map<String, Object> map = new LinkedHashMap<>();
        map.put("sts", STSUtils.getSTS(id));
        return Result.ok(map);
    }

    @PostMapping("/refreshToken")
    public Result<Object> refreshToken(@RequestBody String refresh_token) {

        // a. check refresh_token exist
        if(refresh_token != null && !refresh_token.isEmpty()) {

            // b. check access_token expired
            if(JwtHelper.notExpired(refresh_token)) {

                // c. check access_token valid
                Long userId = JwtHelper.getUserId(refresh_token);

                // d. check userId exist (valid refresh_token should contain a valid userid)
                if(userId != null) {
                    Integer userIdInt = Math.toIntExact(userId);
                    User user = userService.getUserById(userIdInt);

                    // e. check user exist
                    if(user != null) {

                        Map<String, Object> map = new LinkedHashMap<>();
                        map.put("access_token", JwtHelper.createAccessToken(user.getId().longValue()));
                        map.put("expired_time", JwtHelper.getExpiredTime());
                        map.put("sts", STSUtils.getSTS(user.getId()));
                        return Result.ok(map);

                    } else {
                        return Result.error(ResultCodeEnum.NO_USER);
                    }
                } else {
                    return Result.error(ResultCodeEnum.TOKEN_ERROR);
                }
            } else {
                return Result.error(ResultCodeEnum.TOKEN_EXPIRED);
            }
        } else {
            return Result.error(ResultCodeEnum.MISS_TOKEN);
        }
    }
}
