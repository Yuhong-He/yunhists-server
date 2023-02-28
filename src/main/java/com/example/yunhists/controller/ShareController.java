package com.example.yunhists.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.yunhists.entity.*;
import com.example.yunhists.enumeration.ResultCodeEnum;
import com.example.yunhists.service.CategoryService;
import com.example.yunhists.service.ShareService;
import com.example.yunhists.service.ThesisService;
import com.example.yunhists.service.UserService;
import com.example.yunhists.utils.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;

import static com.example.yunhists.utils.ControllerUtils.printException;

@CrossOrigin
@RestController
@RequestMapping("/api/share")
public class ShareController {

    @Autowired
    UserService userService;

    @Autowired
    ShareService shareService;

    @Autowired
    ThesisService thesisService;

    @Autowired
    CategoryService categoryService;

    @PostMapping("/add")
    public Result<Object> add(@RequestBody Share share,
                              HttpServletRequest request) {

        // 1. get token
        Object obj = ControllerUtils.getUserIdFromToken(request);
        try {

            // 2. get id (if obj is not number, throw exception, case token error)
            Integer userId = (Integer) obj;

            // 3. check user rights
            User user = userService.getUserById(userId);
            if(user != null && user.getUserRights() >= 0) {

                // 4. check type valid
                List<Integer> validType = List.of(0,1,2);
                if(validType.contains(share.getType())) {

                    // 5. check copyright valid
                    List<Integer> validCopyright = List.of(0,1,2);
                    if(validCopyright.contains(share.getCopyrightStatus())) {

                        // 6. set uploader & status
                        share.setUploader(userId);
                        share.setStatus(0);

                        // a. check category exist
                        String catStr = share.getCategory();
                        if(!catStr.isEmpty()) {
                            String[] catList = catStr.split(",");
                            List<String> catOkList = new ArrayList<>();
                            for(String s : catList) {
                                Category category = categoryService.getById(Integer.parseInt(s));
                                if(category != null) {
                                    catOkList.add(s);
                                }
                            }
                            String catOkStr = String.join(",", catOkList);
                            share.setCategory(catOkStr);
                        }

                        // b. add thesis
                        shareService.save(share);

                        // c. email notify admin
                        List<User> adminList = userService.getAllAdmin();
                        for(User admin : adminList) {
                            String email = admin.getEmail();
                            String lang = admin.getLang();
                            DirectMailUtils.sendEmail(email,
                                    EmailContentHelper.getNewShareNotificationEmailSubject(lang),
                                    EmailContentHelper.getNewShareNotificationEmailBody(lang));
                        }

                        return Result.ok();
                    } else {
                        return Result.error(ResultCodeEnum.INVALID_COPYRIGHT);
                    }
                } else {
                    return Result.error(ResultCodeEnum.INVALID_TYPE);
                }
            } else {
                obj = Result.error(ResultCodeEnum.NO_PERMISSION);
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

    @GetMapping("/list/{pageNo}")
    public Result<Object> list(@PathVariable("pageNo") Integer pageNo,
                               @RequestParam(required = false) String title,
                               HttpServletRequest request) {

        // 1. get token
        Object obj = ControllerUtils.getUserIdFromToken(request);
        try {

            // 2. get id (if obj is not number, throw exception, case token error)
            Integer userId = (Integer) obj;

            // 3. check user rights
            User user = userService.getUserById(userId);
            if (user != null && user.getUserRights() >= 0) {

                Page<Share> page = new Page<>(pageNo, 10);
                IPage<Share> pageRs = shareService.getShareBySearch(page, userId, title);
                return Result.ok(pageRs);
            } else {
                obj = Result.error(ResultCodeEnum.NO_PERMISSION);
                throw new Exception();
            }
        } catch (Exception e) {
            try {
                return (Result<Object>) obj;
            } catch (Exception exception) {
                printException(e);
                return Result.error(e.getMessage(), ResultCodeEnum.FAIL);
            }
        }
    }

    @PostMapping("/delete/{shareId}")
    public Result<Object> deleteShare(@PathVariable("shareId") int shareId,
                                 HttpServletRequest request) {

        // 1. get token
        Object obj = ControllerUtils.getUserIdFromToken(request);
        try {

            // 2. get id (if obj is not number, throw exception, case token error)
            Integer userId = (Integer) obj;

            // 3. check user rights
            User user = userService.getUserById(userId);
            if(user != null && user.getUserRights() >= 0) {

                // 4. check share exist
                Share share = shareService.getById(shareId);
                if(share != null) {

                    // a. delete oss file
                    String file = share.getFileName();
                    if(!file.isEmpty()) {
                        OSSUtils.deleteFile(file);
                    }

                    // b. delete share
                    shareService.removeById(share);

                    return Result.ok();
                } else {
                    return Result.error(ResultCodeEnum.SHARE_ID_NOT_EXIST);
                }
            } else {
                obj = Result.error(ResultCodeEnum.NO_PERMISSION);
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
