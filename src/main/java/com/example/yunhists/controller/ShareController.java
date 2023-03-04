package com.example.yunhists.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.yunhists.entity.*;
import com.example.yunhists.enumeration.CategoryEnum;
import com.example.yunhists.enumeration.ResultCodeEnum;
import com.example.yunhists.pojo.*;
import com.example.yunhists.service.*;
import com.example.yunhists.utils.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
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

    @Autowired
    CategoryLinkService categoryLinkService;

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
                            String notify = admin.getSendEmail();
                            if(notify.equals("ON")) {
                                String email = admin.getEmail();
                                String lang = admin.getLang();
                                DirectMailUtils.sendEmail(email,
                                        EmailContentHelper.getNewShareNotificationEmailSubject(lang),
                                        EmailContentHelper.getNewShareNotificationEmailBody(lang));
                            }
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

    @GetMapping("/myList/{pageNo}")
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
                List<Share> list = pageRs.getRecords();
                List<ShareRow> newList = new ArrayList<>();
                for(Share s : list) {
                    ThesisIssue thesisIssue = new ThesisIssue(
                            s.getYear() == null ? "" : s.getYear().toString(),
                            s.getVolume() == null ? "" : s.getVolume().toString(),
                            s.getIssue() == null ? "" : s.getIssue()
                    );
                    ShareRow shareWithUser = new ShareRow(
                            s.getId(), s.getAuthor(), s.getTitle(), s.getPublication(), thesisIssue, "",
                            s.getStatus(), ""
                    );
                    newList.add(shareWithUser);
                }
                CustomPage customPage = new CustomPage(newList, pageRs.getTotal(), pageRs.getSize(),
                        pageRs.getCurrent(), pageRs.getPages());
                return Result.ok(customPage);
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

    @GetMapping("/myShare/{shareId}")
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

    @GetMapping("/listAll/{pageNo}")
    public Result<Object> listAll(@PathVariable("pageNo") Integer pageNo,
                                  @RequestParam(required = false) String title,
                                  @RequestParam(required = false) String unapproved,
                                  HttpServletRequest request) {

        // 1. get token
        Object obj = ControllerUtils.getUserIdFromToken(request);
        try {

            // 2. get id (if obj is not number, throw exception, case token error)
            Integer userId = (Integer) obj;

            // 3. check user rights
            User user = userService.getUserById(userId);
            if (user != null && user.getUserRights() >= 1) {

                Page<Share> page = new Page<>(pageNo, 10);
                IPage<Share> pageRs = shareService.getAllShareBySearch(page, title, unapproved);
                List<Share> list = pageRs.getRecords();
                List<ShareRow> newList = new ArrayList<>();
                for(Share s : list) {
                    ThesisIssue thesisIssue = new ThesisIssue(
                            s.getYear() == null ? "" : s.getYear().toString(),
                            s.getVolume() == null ? "" : s.getVolume().toString(),
                            s.getIssue() == null ? "" : s.getIssue()
                    );
                    String uploader = userService.getUserById(s.getUploader()).getUsername();
                    String approver = "";
                    if(s.getApprover() != null) {
                        approver = userService.getUserById(s.getApprover()).getUsername();
                    }
                    ShareRow shareWithUser = new ShareRow(
                            s.getId(), s.getAuthor(), s.getTitle(), s.getPublication(), thesisIssue, uploader,
                            s.getStatus(), approver
                    );
                    newList.add(shareWithUser);
                }
                CustomPage customPage = new CustomPage(newList, pageRs.getTotal(), pageRs.getSize(),
                        pageRs.getCurrent(), pageRs.getPages());
                return Result.ok(customPage);
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

    @GetMapping("/id/{shareId}")
    public Result<Object> getShareById(@PathVariable("shareId") Integer id,
                                       HttpServletRequest request) {

        // 1. get token
        Object obj = ControllerUtils.getUserIdFromToken(request);
        try {

            // 2. get id (if obj is not number, throw exception, case token error)
            Integer userId = (Integer) obj;

            // 3. check user rights
            User user = userService.getUserById(userId);
            if(user != null && user.getUserRights() >= 1) {

                // 4. check share exist
                Share share = shareService.getById(id);
                if(share != null) {

                    // 6. check share status unapproved
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

    @PostMapping("/approve/{shareId}")
    public Result<Object> approve(@PathVariable("shareId") int shareId,
                                  @RequestBody Share share,
                                  @RequestParam("category") int[] catIds,
                                  HttpServletRequest request) {

        // 1. get token
        Object obj = ControllerUtils.getUserIdFromToken(request);
        try {

            // 2. get id (if obj is not number, throw exception, case token error)
            Integer userId = (Integer) obj;

            // 3. check user rights
            User user = userService.getUserById(userId);
            if(user != null && user.getUserRights() >= 1) {

                // 4. check share id exist
                Share targetShare = shareService.getById(shareId);
                if(targetShare != null) {

                    // 5. check share status unapproved
                    if(targetShare.getStatus() == 0) {

                        // 4. check type valid
                        List<Integer> validType = List.of(0,1,2);
                        if(validType.contains(share.getType())) {

                            // 5. check copyright valid
                            List<Integer> validCopyright = List.of(0,1,2);
                            if(validCopyright.contains(share.getCopyrightStatus())) {

                                // 6. check duplicate
                                Thesis thesis = new Thesis(share, targetShare.getUploader(), userId);
                                if(thesisService.validateNotExist(thesis)) {

                                    // a. move file
                                    String file = thesis.getFileName();
                                    String targetFile = "";
                                    if(file.startsWith("temp/")) {
                                        targetFile = "default/" + file.substring("temp/".length() + 1);
                                        try{
                                            OSSUtils.moveFile(file, targetFile);
                                        } catch (Exception e) {
                                            return Result.error(ResultCodeEnum.MOVE_SHARE_FILE_FAIL);
                                        }
                                    }
                                    thesis.setFileName(targetFile);

                                    // b. add thesis
                                    thesisService.save(thesis);
                                    ArrayList<Integer> failedParentCatId = new ArrayList<>();

                                    // c. loop parent category from client
                                    List<String> catOkList = new ArrayList<>();
                                    for (int catId : catIds) {

                                        // d. check category exist
                                        Category cat = categoryService.getById(catId);
                                        if(cat != null) {

                                            // e. add category link
                                            CategoryLink categoryLink = new CategoryLink(
                                                    thesis.getId(), cat.getId(), cat.getZhName(),
                                                    cat.getEnName(), CategoryEnum.TYPE_LINK_THESIS.getCode(), userId);
                                            categoryLinkService.save(categoryLink);

                                            // f. update category statistics
                                            cat.setCatTheses(cat.getCatTheses() + 1);
                                            categoryService.saveOrUpdate(cat);

                                            catOkList.add(String.valueOf(catId));

                                        } else {
                                            failedParentCatId.add(catId);
                                        }
                                    }

                                    // g. update share records
                                    share.setId(targetShare.getId());
                                    share.setFileName(targetFile);
                                    String catOkStr = String.join(",", catOkList);
                                    share.setCategory(catOkStr);
                                    share.setUploader(targetShare.getUploader());
                                    share.setUploadTime(targetShare.getUploadTime());
                                    share.setStatus(1);
                                    share.setApprover(userId);
                                    share.setApproveTime(Timestamp.valueOf(
                                            new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(
                                                    new Timestamp(new Date().getTime()))));
                                    shareService.saveOrUpdate(share);

                                    // h. update user points
                                    User uploader = userService.getUserById(share.getUploader());
                                    uploader.setPoints(uploader.getPoints() + 1);
                                    userService.saveOrUpdate(uploader);

                                    // i. email notify user
                                    if(!uploader.getEmail().isEmpty()) {
                                        if(uploader.getSendEmail().equals("ON")) {
                                            DirectMailUtils.sendEmail(uploader.getEmail(),
                                                    EmailContentHelper.getShareApprovedNotificationEmailSubject(uploader.getLang()),
                                                    EmailContentHelper.getShareApprovedNotificationEmailBody(uploader.getLang(),
                                                            uploader.getUsername(), thesis.getTitle()));
                                        }
                                    }

                                    // j. return results
                                    Map<String, Object> map = new LinkedHashMap<>();
                                    map.put("failedCatId", failedParentCatId);
                                    if(failedParentCatId.isEmpty()) {
                                        return Result.ok(map);
                                    } else {
                                        return Result.error(map, ResultCodeEnum.PARENT_CAT_NOT_EXIST);
                                    }
                                } else {
                                    return Result.error(ResultCodeEnum.THESIS_EXIST);
                                }
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

    @PostMapping("/reject/{shareId}")
    public Result<Object> reject(@PathVariable("shareId") int shareId,
                                 @RequestParam("reason") String reason,
                                 HttpServletRequest request) {

        // 1. get token
        Object obj = ControllerUtils.getUserIdFromToken(request);
        try {

            // 2. get id (if obj is not number, throw exception, case token error)
            Integer userId = (Integer) obj;

            // 3. check user rights
            User user = userService.getUserById(userId);
            if(user != null && user.getUserRights() >= 1) {

                // 4. check share id exist
                Share share = shareService.getById(shareId);
                if(share != null) {

                    // 5. check share status unapproved
                    if(share.getStatus() == 0) {

                        // a. remove file
                        String file = share.getFileName();
                        if(file.startsWith("temp/")) {
                            try{
                                OSSUtils.deleteFile(file);
                            } catch (Exception e) {
                                return Result.error(ResultCodeEnum.REMOVE_FILE_FAIL);
                            }
                        }

                        // b. update share records
                        share.setFileName("");
                        share.setStatus(2);
                        share.setApprover(userId);
                        share.setApproveTime(Timestamp.valueOf(
                                new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(
                                        new Timestamp(new Date().getTime()))));
                        shareService.saveOrUpdate(share);

                        // c. email notify user
                        User uploader = userService.getUserById(share.getUploader());
                        if(!uploader.getEmail().isEmpty()) {
                            if(uploader.getSendEmail().equals("ON")) {
                                DirectMailUtils.sendEmail(uploader.getEmail(),
                                        EmailContentHelper.getShareRejectedNotificationEmailSubject(uploader.getLang()),
                                        EmailContentHelper.getShareRejectedNotificationEmailBody(uploader.getLang(),
                                                uploader.getUsername(), share.getTitle(), reason));
                            }
                        }

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
