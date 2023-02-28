package com.example.yunhists.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.yunhists.entity.*;
import com.example.yunhists.enumeration.ResultCodeEnum;
import com.example.yunhists.pojo.CategoryName;
import com.example.yunhists.service.CategoryService;
import com.example.yunhists.service.ShareService;
import com.example.yunhists.service.ThesisService;
import com.example.yunhists.service.UserService;
import com.example.yunhists.utils.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

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
                        validatedCat(share);

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

                    // 5. check share status unapproved
                    if(share.getStatus() == 0) {

                        // a. delete oss file
                        String file = share.getFileName();
                        if (!file.isEmpty()) {
                            OSSUtils.deleteFile(file);
                        }

                        // b. delete share
                        shareService.removeById(share);

                        return Result.ok();
                    } else {
                        return Result.error(ResultCodeEnum.SHARE_CAN_NOT_UPDATE);
                    }
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

    @GetMapping("/id/{shareId}")
    public Result<Object> getMyShareById(@PathVariable("shareId") Integer id,
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
                Share share = shareService.getById(id);
                if(share != null) {

                    // 5. check this is my share
                    if(Objects.equals(share.getUploader(), userId)) {

                        // 6. check the share in unapproved
                        if(share.getStatus() == 0) {
                            List<CategoryName> categories = getShareCat(share);
                            Map<String, Object> map = new LinkedHashMap<>();
                            map.put("share", share);
                            map.put("categories", categories);
                            return Result.ok(map);
                        } else {
                            return Result.error(ResultCodeEnum.SHARE_CAN_NOT_UPDATE);
                        }
                    } else {
                        return Result.error(ResultCodeEnum.NOT_YOUR_SHARE);
                    }
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

    @PostMapping("/deleteFile")
    public Result<Object> deleteFileOfShare(@RequestParam String file,
                                            HttpServletRequest request) {

        // 1. get token
        Object obj = ControllerUtils.getUserIdFromToken(request);
        try {

            // 2. get id (if obj is not number, throw exception, case token error)
            Integer userId = (Integer) obj;

            // 3. check user rights
            User user = userService.getUserById(userId);
            if(user != null && user.getUserRights() >= 0) {

                // 4. check file exist
                Share share = shareService.getShareByFile(file);
                if(share != null) {

                    // 5. check share status unapproved
                    if(share.getStatus() == 0) {
                        share.setFileName("");
                        shareService.saveOrUpdate(share);
                        return Result.ok();
                    } else {
                        return Result.error(ResultCodeEnum.SHARE_CAN_NOT_UPDATE);
                    }
                } else {
                    return Result.error(ResultCodeEnum.SHARE_FILE_NOT_EXIST);
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

    @PostMapping("/update/{shareId}")
    public Result<Object> update(@PathVariable("shareId") int shareId,
                                 @RequestBody Share share,
                                 HttpServletRequest request) {

        // 1. get token
        Object obj = ControllerUtils.getUserIdFromToken(request);
        try {

            // 2. get id (if obj is not number, throw exception, case token error)
            Integer userId = (Integer) obj;

            // 3. check user rights
            User user = userService.getUserById(userId);
            if(user != null && user.getUserRights() >= 0) {

                // 4. check shareId exist
                Share targetShare = shareService.getById(shareId);
                if(targetShare != null) {

                    // 5. check share status unapproved
                    if(targetShare.getStatus() == 0) {

                        // 6. check type valid
                        List<Integer> validType = List.of(0,1,2);
                        if(validType.contains(share.getType())) {

                            // 7. check copyright valid
                            List<Integer> validCopyright = List.of(0,1,2);
                            if(validCopyright.contains(share.getCopyrightStatus())) {

                                // a. set id, uploader, status
                                share.setId(shareId);
                                share.setUploader(userId);
                                share.setStatus(0);

                                // b. check category exist
                                validatedCat(share);

                                // c. update share
                                shareService.saveOrUpdate(share);

                                return Result.ok();
                            } else {
                                return Result.error(ResultCodeEnum.INVALID_COPYRIGHT);
                            }
                        } else {
                            return Result.error(ResultCodeEnum.INVALID_TYPE);
                        }
                    } else {
                        return Result.error(ResultCodeEnum.SHARE_CAN_NOT_UPDATE);
                    }
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

    private List<CategoryName> getShareCat(Share share) {
        String catStr = share.getCategory();
        List<CategoryName> categories = new ArrayList<>();
        if(!catStr.isEmpty()) {
            String[] catList = catStr.split(",");
            for(String s : catList) {
                Category c = categoryService.getById(Integer.parseInt(s));
                if(c != null) {
                    categories.add(new CategoryName(c.getId(), c.getZhName(), c.getEnName()));
                }
            }
        }
        return categories;
    }

    private void validatedCat(Share share) {
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
    }
}
