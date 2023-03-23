package com.example.yunhists.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.yunhists.common.BaseContext;
import com.example.yunhists.common.Result;
import com.example.yunhists.entity.*;
import com.example.yunhists.enumeration.CategoryEnum;
import com.example.yunhists.enumeration.ResultCodeEnum;
import com.example.yunhists.dto.*;
import com.example.yunhists.service.*;
import com.example.yunhists.utils.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.*;

@CrossOrigin
@RestController
@RequestMapping("/api/upload")
public class UploadController {

    @Autowired
    UserService userService;

    @Autowired
    UploadService uploadService;

    @Autowired
    ThesisService thesisService;

    @Autowired
    CategoryService categoryService;

    @Autowired
    CategoryLinkService categoryLinkService;

    @PostMapping("/add")
    public Result<Object> add(@RequestBody Upload upload) {

        // 1. get id
        int userId = Math.toIntExact(BaseContext.getCurrentId());

        // 2. check type valid
        List<Integer> validType = List.of(0,1,2,3);
        if(validType.contains(upload.getType())) {

            // 3. check copyright valid
            List<Integer> validCopyright = List.of(0,1,2);
            if(validCopyright.contains(upload.getCopyrightStatus())) {

                // 4. set uploader & status
                upload.setUploader(userId);
                upload.setStatus(0);

                // a. check category exist
                validatedCat(upload);

                // b. add thesis
                uploadService.save(upload);

                // c. email notify admin
                List<User> adminList = userService.getAllAdmin();
                for(User admin : adminList) {
                    String notify = admin.getSendEmail();
                    if(notify.equals("ON")) {
                        String email = admin.getEmail();
                        String lang = admin.getLang();
                        DirectMailUtils.sendEmail(email,
                                EmailContentHelper.getNewUploadNotificationEmailSubject(lang),
                                EmailContentHelper.getNewUploadNotificationEmailBody(lang));
                    }
                }

                return Result.ok();
            } else {
                return Result.error(ResultCodeEnum.INVALID_COPYRIGHT);
            }
        } else {
            return Result.error(ResultCodeEnum.INVALID_TYPE);
        }
    }

    @GetMapping("/myList/{pageNo}")
    public Result<Object> list(@PathVariable("pageNo") Integer pageNo,
                               @RequestParam(required = false) String title) {

        int userId = Math.toIntExact(BaseContext.getCurrentId());

        Page<Upload> page = new Page<>(pageNo, 10);
        IPage<Upload> pageRs = uploadService.getUploadBySearch(page, userId, title);
        List<Upload> list = pageRs.getRecords();
        List<UploadRow> newList = new ArrayList<>();
        for(Upload s : list) {
            ThesisIssue thesisIssue = new ThesisIssue(
                    s.getYear() == null ? "" : s.getYear().toString(),
                    s.getVolume() == null ? "" : s.getVolume().toString(),
                    s.getIssue() == null ? "" : s.getIssue()
            );
            UploadRow uploadWithUser = new UploadRow(
                    s.getId(), s.getAuthor(), s.getTitle(), s.getPublication(), thesisIssue, "",
                    s.getStatus(), ""
            );
            newList.add(uploadWithUser);
        }
        CustomPage customPage = new CustomPage(newList, pageRs.getTotal(), pageRs.getSize(),
                pageRs.getCurrent(), pageRs.getPages());
        return Result.ok(customPage);
    }

    @DeleteMapping("/delete/{uploadId}")
    public Result<Object> deleteUpload(@PathVariable("uploadId") int uploadId) {

        // 1. get id
        int userId = Math.toIntExact(BaseContext.getCurrentId());

        // 2. check upload exist
        Upload upload = uploadService.getById(uploadId);
        if(upload != null) {

            // 3. check this is my upload
            if(upload.getUploader() == userId) {

                // 4. check upload status unapproved
                if(upload.getStatus() == 0) {

                    // a. delete oss file
                    String file = upload.getFileName();
                    if (!file.isEmpty()) {
                        OSSUtils.deleteFile(file);
                    }

                    // b. delete upload
                    uploadService.removeById(upload);

                    return Result.ok();
                } else {
                    return Result.error(ResultCodeEnum.UPLOAD_CAN_NOT_UPDATE);
                }
            } else {
                return Result.error(ResultCodeEnum.NOT_YOUR_UPLOAD);
            }
        } else {
            return Result.error(ResultCodeEnum.UPLOAD_ID_NOT_EXIST);
        }
    }

    @GetMapping("/myUpload/{uploadId}")
    public Result<Object> getMyUploadById(@PathVariable("uploadId") Integer id) {

        // 1. get id
        int userId = Math.toIntExact(BaseContext.getCurrentId());

        // 2. check upload exist
        Upload upload = uploadService.getById(id);
        if(upload != null) {

            // 3. check this is my upload
            if(userId == upload.getUploader()) {

                // 4. check upload status unapproved
                if(upload.getStatus() == 0) {

                    List<CategoryName> categories = getUploadCat(upload);
                    Map<String, Object> map = new LinkedHashMap<>();
                    map.put("upload", upload);
                    map.put("categories", categories);
                    return Result.ok(map);
                } else {
                    return Result.error(ResultCodeEnum.UPLOAD_CAN_NOT_UPDATE);
                }
            } else {
                return Result.error(ResultCodeEnum.NOT_YOUR_UPLOAD);
            }
        } else {
            return Result.error(ResultCodeEnum.UPLOAD_ID_NOT_EXIST);
        }
    }

    @DeleteMapping("/deleteFile")
    public Result<Object> deleteFileOfUpload(@RequestParam String file) {

        // 1. get id
        int userId = Math.toIntExact(BaseContext.getCurrentId());

        // 2. check file exist
        Upload upload = uploadService.getUploadByFile(file);
        if(upload != null) {

            // 3. check this is my upload
            if(userId == upload.getUploader()) {

                // 4. check upload status unapproved
                if(upload.getStatus() == 0) {
                    upload.setFileName("");
                    uploadService.saveOrUpdate(upload);
                    return Result.ok();
                } else {
                    return Result.error(ResultCodeEnum.UPLOAD_CAN_NOT_UPDATE);
                }
            } else {
                return Result.error(ResultCodeEnum.NOT_YOUR_UPLOAD);
            }
        } else {
            return Result.error(ResultCodeEnum.UPLOAD_FILE_NOT_EXIST);
        }
    }

    @PutMapping("/update/{uploadId}")
    public Result<Object> update(@PathVariable("uploadId") int uploadId,
                                 @RequestBody Upload upload) {

        // 1. get id
        int userId = Math.toIntExact(BaseContext.getCurrentId());

        // 3. check uploadId exist
        Upload targetUpload = uploadService.getById(uploadId);
        if(targetUpload != null) {

            // 4. check this is my upload
            if(userId == targetUpload.getUploader()) {

                // 5. check upload status unapproved
                if(targetUpload.getStatus() == 0) {

                    // 6. check type valid
                    List<Integer> validType = List.of(0,1,2,3);
                    if(validType.contains(upload.getType())) {

                        // 7. check copyright valid
                        List<Integer> validCopyright = List.of(0,1,2);
                        if(validCopyright.contains(upload.getCopyrightStatus())) {

                            // a. set id, uploader, status
                            upload.setId(uploadId);
                            upload.setUploader(userId);
                            upload.setStatus(0);

                            // b. check category exist
                            validatedCat(upload);

                            // c. update upload
                            uploadService.saveOrUpdate(upload);

                            return Result.ok();
                        } else {
                            return Result.error(ResultCodeEnum.INVALID_COPYRIGHT);
                        }
                    } else {
                        return Result.error(ResultCodeEnum.INVALID_TYPE);
                    }
                } else {
                    return Result.error(ResultCodeEnum.UPLOAD_CAN_NOT_UPDATE);
                }
            } else {
                return Result.error(ResultCodeEnum.NOT_YOUR_UPLOAD);
            }
        } else {
            return Result.error(ResultCodeEnum.UPLOAD_ID_NOT_EXIST);
        }
    }

    @GetMapping("/listAll/{pageNo}")
    public Result<Object> listAll(@PathVariable("pageNo") Integer pageNo,
                                  @RequestParam(required = false) String title,
                                  @RequestParam(required = false) String unapproved) {

        Page<Upload> page = new Page<>(pageNo, 10);
        IPage<Upload> pageRs = uploadService.getAllUploadBySearch(page, title, unapproved);
        List<Upload> list = pageRs.getRecords();
        List<UploadRow> newList = new ArrayList<>();
        for(Upload s : list) {
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
            UploadRow uploadWithUser = new UploadRow(
                    s.getId(), s.getAuthor(), s.getTitle(), s.getPublication(), thesisIssue, uploader,
                    s.getStatus(), approver
            );
            newList.add(uploadWithUser);
        }
        CustomPage customPage = new CustomPage(newList, pageRs.getTotal(), pageRs.getSize(),
                pageRs.getCurrent(), pageRs.getPages());
        return Result.ok(customPage);
    }

    @GetMapping("/id/{uploadId}")
    public Result<Object> getUploadById(@PathVariable("uploadId") Integer id) {

        // 1. check upload exist
        Upload upload = uploadService.getById(id);
        if(upload != null) {

            // 2. check upload status unapproved
            if(upload.getStatus() == 0) {
                List<CategoryName> categories = getUploadCat(upload);
                Map<String, Object> map = new LinkedHashMap<>();
                map.put("upload", upload);
                map.put("categories", categories);
                return Result.ok(map);
            } else {
                return Result.error(ResultCodeEnum.UPLOAD_CAN_NOT_UPDATE);
            }
        } else {
            return Result.error(ResultCodeEnum.UPLOAD_ID_NOT_EXIST);
        }
    }

    @PutMapping("/approve/{uploadId}")
    public Result<Object> approve(@PathVariable("uploadId") int uploadId,
                                  @RequestBody Upload upload,
                                  @RequestParam("category") int[] catIds) {

        // 1. get id
        int userId = Math.toIntExact(BaseContext.getCurrentId());

        // 2. check upload id exist
        Upload targetUpload = uploadService.getById(uploadId);
        if(targetUpload != null) {

            // 3. check upload status unapproved
            if(targetUpload.getStatus() == 0) {

                // 4. check type valid
                List<Integer> validType = List.of(0,1,2,3);
                if(validType.contains(upload.getType())) {

                    // 5. check copyright valid
                    List<Integer> validCopyright = List.of(0,1,2);
                    if(validCopyright.contains(upload.getCopyrightStatus())) {

                        // 6. check duplicate
                        Thesis thesis = new Thesis(upload, targetUpload.getUploader(), userId);
                        if(thesisService.validateNotExist(thesis)) {

                            // a. move file
                            String file = thesis.getFileName();
                            String targetFile = "";
                            if(file.startsWith("temp/")) {
                                targetFile = "default/" + file.substring("temp/".length() + 1);
                                OSSUtils.moveFile(file, targetFile);
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

                            // g. update upload records
                            upload.setId(targetUpload.getId());
                            upload.setFileName(targetFile);
                            String catOkStr = String.join(",", catOkList);
                            upload.setCategory(catOkStr);
                            upload.setUploader(targetUpload.getUploader());
                            upload.setUploadTime(targetUpload.getUploadTime());
                            upload.setStatus(1);
                            upload.setApprover(userId);
                            upload.setApproveTime(Timestamp.valueOf(
                                    new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(
                                            new Timestamp(new Date().getTime()))));
                            uploadService.saveOrUpdate(upload);

                            // h. update user points
                            User uploader = userService.getUserById(upload.getUploader());
                            uploader.setPoints(uploader.getPoints() + 1);
                            userService.saveOrUpdate(uploader);

                            // i. email notify user
                            if(!uploader.getEmail().isEmpty()) {
                                if(uploader.getSendEmail().equals("ON")) {
                                    DirectMailUtils.sendEmail(uploader.getEmail(),
                                            EmailContentHelper.getUploadApprovedNotificationEmailSubject(uploader.getLang()),
                                            EmailContentHelper.getUploadApprovedNotificationEmailBody(uploader.getLang(),
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
                return Result.error(ResultCodeEnum.UPLOAD_CAN_NOT_UPDATE);
            }
        } else {
            return Result.error(ResultCodeEnum.UPLOAD_ID_NOT_EXIST);
        }
    }

    @PutMapping("/reject/{uploadId}")
    public Result<Object> reject(@PathVariable("uploadId") int uploadId,
                                 @RequestParam("reason") String reason) {

        // 1. get id
        Integer userId = Math.toIntExact(BaseContext.getCurrentId());

        // 2. check upload id exist
        Upload upload = uploadService.getById(uploadId);
        if(upload != null) {

            // 3. check upload status unapproved
            if(upload.getStatus() == 0) {

                // a. remove file
                String file = upload.getFileName();
                if(file.startsWith("temp/")) {
                    OSSUtils.deleteFile(file);
                }

                // b. update upload records
                upload.setFileName("");
                upload.setStatus(2);
                upload.setApprover(userId);
                upload.setApproveTime(Timestamp.valueOf(
                        new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(
                                new Timestamp(new Date().getTime()))));
                uploadService.saveOrUpdate(upload);

                // c. email notify user
                User uploader = userService.getUserById(upload.getUploader());
                if(!uploader.getEmail().isEmpty()) {
                    if(uploader.getSendEmail().equals("ON")) {
                        DirectMailUtils.sendEmail(uploader.getEmail(),
                                EmailContentHelper.getUploadRejectedNotificationEmailSubject(uploader.getLang()),
                                EmailContentHelper.getUploadRejectedNotificationEmailBody(uploader.getLang(),
                                        uploader.getUsername(), upload.getTitle(), reason));
                    }
                }

                return Result.ok();
            } else {
                return Result.error(ResultCodeEnum.UPLOAD_CAN_NOT_UPDATE);
            }
        } else {
            return Result.error(ResultCodeEnum.UPLOAD_ID_NOT_EXIST);
        }
    }

    private List<CategoryName> getUploadCat(Upload upload) {
        String catStr = upload.getCategory();
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

    private void validatedCat(Upload upload) {
        String catStr = upload.getCategory();
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
            upload.setCategory(catOkStr);
        }
    }
}
