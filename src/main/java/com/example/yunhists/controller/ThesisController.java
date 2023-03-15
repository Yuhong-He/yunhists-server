package com.example.yunhists.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.yunhists.common.BaseContext;
import com.example.yunhists.common.Result;
import com.example.yunhists.entity.*;
import com.example.yunhists.enumeration.CategoryEnum;
import com.example.yunhists.enumeration.ResultCodeEnum;
import com.example.yunhists.pojo.*;
import com.example.yunhists.service.*;
import com.example.yunhists.utils.*;
import org.apache.commons.lang.ArrayUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.text.Collator;
import java.util.*;

@CrossOrigin
@RestController
@RequestMapping("/api/thesis")
public class ThesisController {

    @Autowired
    private UserService userService;

    @Autowired
    private ThesisService thesisService;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private CategoryLinkService categoryLinkService;

    @Autowired
    private DelThesisService delThesisService;

    @Autowired
    private UploadService uploadService;

    @PostMapping("/add")
    public Result<Object> add(@RequestBody Thesis thesis,
                              @RequestParam("category") int[] catIds) {

        // 1. get id
        int userId = Math.toIntExact(BaseContext.getCurrentId());
        User user = userService.getUserById(userId);

        // 2. check type valid
        List<Integer> validType = List.of(0,1,2,3);
        if(validType.contains(thesis.getType())) {

            // 3. check copyright valid
            List<Integer> validCopyright = List.of(0,1,2);
            if(validCopyright.contains(thesis.getCopyrightStatus())) {

                // 4. check duplicate
                if(thesisService.validateNotExist(thesis)) {

                    // 5. set uploader & approver
                    thesis.setUploader(userId);
                    thesis.setApprover(userId);

                    // a. add thesis
                    thesisService.save(thesis);
                    ArrayList<Integer> failedParentCatId = new ArrayList<>();

                    // b. loop parent category from client
                    List<String> catOkList = new ArrayList<>();
                    for (int catId : catIds) {

                        // c. check category exist
                        Category cat = categoryService.getById(catId);
                        if(cat != null) {

                            // d. add category link
                            CategoryLink categoryLink = new CategoryLink(
                                    thesis.getId(), cat.getId(), cat.getZhName(),
                                    cat.getEnName(), CategoryEnum.TYPE_LINK_THESIS.getCode(), userId);
                            categoryLinkService.save(categoryLink);

                            // e. update category statistics
                            cat.setCatTheses(cat.getCatTheses() + 1);
                            categoryService.saveOrUpdate(cat);

                            catOkList.add(String.valueOf(catId));

                        } else {
                            failedParentCatId.add(catId);
                        }
                    }

                    // f. add upload records
                    String catOkStr = String.join(",", catOkList);
                    Upload upload = new Upload(thesis, userId, catOkStr);
                    uploadService.save(upload);

                    // g. update user points
                    user.setPoints(user.getPoints() + 1);
                    userService.saveOrUpdate(user);

                    // h. return results
                    Map<String, Object> map = new LinkedHashMap<>();
                    map.put("points", user.getPoints());
                    if(failedParentCatId.isEmpty()) {
                        return Result.ok(map);
                    } else {
                        map.put("failedCatId", failedParentCatId);
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
    }

    @GetMapping("/list/{pageNo}/{pageSize}")
    public Result<Object> list(@PathVariable("pageNo") Integer pageNo,
                               @PathVariable("pageSize") Integer pageSize,
                               @RequestParam(required = false) String author,
                               @RequestParam(required = false) String title,
                               @RequestParam(required = false) String publication,
                               @RequestParam(required = false) String year,
                               @RequestParam String sortCol,
                               @RequestParam String sortOrder) {

        List<String> validSortCol = List.of("author", "title", "publication", "thesisIssue");
        if(!validSortCol.contains(sortCol)) {
            sortCol = "";
        } else if(sortCol.equals("thesisIssue")) {
            sortCol = "year";
        }
        if(!sortOrder.equals("ASC") && !sortOrder.equals("DESC")) {
            sortOrder = "";
        }

        Page<Thesis> page = new Page<>(pageNo, pageSize);
        IPage<Thesis> pageRs =  thesisService.getThesisBySearch(
                page, author, title, publication, year, sortCol, sortOrder);
        List<Thesis> list = pageRs.getRecords();
        List<ThesisRow> newList = new ArrayList<>();
        for(Thesis s : list) {
            List<CategoryName> categories = getThesisCat(s);
            ThesisIssue thesisIssue = new ThesisIssue(
                    s.getYear() == null ? "" : s.getYear().toString(),
                    s.getVolume() == null ? "" : s.getVolume().toString(),
                    s.getIssue() == null ? "" : s.getIssue()
            );
            ThesisRow thesisWithCategory = new ThesisRow(
                    s.getId(), s.getAuthor(), s.getTitle(), s.getPublication(), thesisIssue, categories
            );
            newList.add(thesisWithCategory);
        }
        CustomPage customPage = new CustomPage(newList, pageRs.getTotal(), pageRs.getSize(),
                pageRs.getCurrent(), pageRs.getPages());
        return Result.ok(customPage);
    }

    @GetMapping("/cite/{id}")
    public Result<Object> cite(@PathVariable("id") Integer id) {
        Thesis thesis = thesisService.getById(id);
        if(thesis != null) {

            String author = thesis.getAuthor() == null ? "" : thesis.getAuthor();
            String title = thesis.getTitle();
            String publication = thesis.getPublication() == null ? "" : thesis.getPublication();
            String location = thesis.getLocation();
            String publisher = thesis.getPublisher();
            String year = thesis.getYear() == null ? "" : thesis.getYear().toString();
            String volume = thesis.getVolume() == null ? "" : thesis.getVolume().toString();
            String issue = thesis.getIssue();
            String pages = thesis.getPages();
            String doi = thesis.getDoi();
            String isbn = thesis.getIsbn();
            int type = thesis.getType();

            String vancouver = ReferenceUtils.vancouverStyle(
                    author, title, publication, location, publisher, year, volume, issue, pages, doi, type);
            String harvard = ReferenceUtils.harvardStyle(
                    author, title, publication, location, publisher, year, volume, issue, pages, doi, type);
            String gbt7714 = ReferenceUtils.gbt7714Style(
                    author, title, publication, location, publisher, year, volume, issue, pages, type);
            String wikipedia = ReferenceUtils.wikipediaStyle(
                    author, title, publication, location, publisher, year, volume, issue, pages, doi, isbn, type);


            Map<String, Object> map = new LinkedHashMap<>();
            map.put("vancouver", vancouver);
            map.put("harvard", harvard);
            map.put("gbt7714", gbt7714);
            map.put("wikipedia", wikipedia);

            return Result.ok(map);
        } else {
            return Result.error(ResultCodeEnum.THESIS_ID_NOT_EXIST);
        }
    }

    @GetMapping("/onlinePublishInfo/{id}")
    public Result<Object> getOnlinePublishInfo(@PathVariable("id") Integer id) {
        Thesis thesis = thesisService.getById(id);
        if(thesis != null) {
            Map<String, Object> map = new LinkedHashMap<>();
            map.put("title", thesis.getTitle());
            map.put("onlinePublisher", thesis.getOnlinePublisher());
            map.put("url", thesis.getOnlinePublishUrl());
            map.put("copyrightStatus", thesis.getCopyrightStatus());
            return Result.ok(map);
        } else {
            return Result.error(ResultCodeEnum.THESIS_ID_NOT_EXIST);
        }
    }

    @GetMapping("/getDownloadNum")
    public Result<Object> getDownloadNum() {
        Integer userId = Math.toIntExact(BaseContext.getCurrentId());
        User user = userService.getUserById(userId);
        int remain = calculateRemain(user.getPoints(), user.getTodayDownload());
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("remain", remain);
        return Result.ok(map);
    }

    @GetMapping("/file/{id}")
    public Result<Object> getFileName(@PathVariable("id") Integer id) {

        Integer userId = Math.toIntExact(BaseContext.getCurrentId());
        User user = userService.getUserById(userId);

        // 1. check thesis exists
        Thesis thesis = thesisService.getById(id);
        if(thesis != null) {

            // 2. check file exist
            if(thesis.getFileName().length() > 0) {

                // 3. check user download today
                int remain = calculateRemain(user.getPoints(), user.getTodayDownload());
                if(remain > 0) {
                    user.setTodayDownload(user.getTodayDownload() + 1);
                    userService.saveOrUpdate(user);
                    Map<String, Object> map = new LinkedHashMap<>();
                    map.put("file", thesis.getFileName());
                    return Result.ok(map);
                } else {
                    return Result.error(ResultCodeEnum.NO_MORE_DOWNLOAD);
                }
            } else {
                return Result.error(ResultCodeEnum.THESIS_FILE_MISSING);
            }
        } else {
            return Result.error(ResultCodeEnum.THESIS_ID_NOT_EXIST);
        }
    }

    @GetMapping("/id/{thesisId}")
    public Result<Object> getThesisById(@PathVariable("thesisId") Integer id) {
        Thesis thesis = thesisService.getById(id);
        if(thesis != null) {
            List<CategoryName> categories = getThesisCat(thesis);
            Map<String, Object> map = new LinkedHashMap<>();
            map.put("thesis", thesis);
            map.put("categories", categories);
            return Result.ok(map);
        } else {
            return Result.error(ResultCodeEnum.THESIS_ID_NOT_EXIST);
        }
    }

    @DeleteMapping("/deleteFile")
    public Result<Object> deleteFileOfThesis(@RequestParam String file) {
        Thesis thesis = thesisService.getThesisByFile(file);
        if(thesis != null) {
            thesis.setFileName("");
            thesisService.saveOrUpdate(thesis);
            return Result.ok();
        } else {
            return Result.error(ResultCodeEnum.THESIS_FILE_NOT_EXIST);
        }
    }

    @PutMapping("/update")
    public Result<Object> update(@RequestParam("id") Integer thesisId,
                                 @RequestParam("category") int[] catIds,
                                 @RequestBody Thesis thesis) {

        // 1. get id (if obj is not number, throw exception, case token error)
        int userId = Math.toIntExact(BaseContext.getCurrentId());

        // 2. check type valid
        List<Integer> validType = List.of(0,1,2,3);
        if(validType.contains(thesis.getType())) {

            // 3. check copyright valid
            List<Integer> validCopyright = List.of(0,1,2);
            if(validCopyright.contains(thesis.getCopyrightStatus())) {

                // 4.check thesis exist
                Thesis targetThesis = thesisService.getById(thesisId);
                if(targetThesis != null) {

                    // 5. set uploader & approver $ approve_time
                    thesis.setId(thesisId);
                    thesis.setUploader(targetThesis.getUploader());
                    thesis.setApprover(targetThesis.getApprover());
                    thesis.setApproveTime(targetThesis.getApproveTime());

                    // 6. check duplicate
                    if(thesisService.validateNotExistWhenUpdate(thesis)) {

                        // a. update thesis
                        thesisService.saveOrUpdate(thesis);

                        // b. handle catIds, the category from client
                        List<Integer> catIdsList = new ArrayList<>(); // ready adding list
                        List<Integer> catLinksList = new ArrayList<>(); // old category (from database)
                        List<CategoryLink> categoryLinkList = categoryLinkService.getLinkByChildId(
                                thesis.getId(), CategoryEnum.TYPE_LINK_THESIS.getCode());
                        for(CategoryLink link : categoryLinkList) {
                            catLinksList.add(link.getCatTo());
                        }
                        /*
                        * b-1. clean up parent category that removed:
                        *       loop categories of the thesis in database,
                        *       if new categories (from client) does not contain the category
                        *       remove this category in database
                        *  */
                        for(CategoryLink link : categoryLinkList) {
                            if(!ArrayUtils.contains(catIds, link.getCatTo())) {
                                Category cat = categoryService.getById(link.getCatTo());
                                cat.setCatTheses(cat.getCatTheses() - 1);
                                categoryService.saveOrUpdate(cat);
                                categoryLinkService.removeById(link.getId());
                            }
                        }
                        /*
                         * b-2. clean up parent category which is new:
                         *       loop categories of the thesis from client,
                         *       if old categories (from database) does not contain the category
                         *       add the category to ready-adding list
                         *  */
                        for(int i : catIds) {
                            if(!catLinksList.contains(i)) {
                                catIdsList.add(i);
                            }
                        }

                        ArrayList<Integer> failedParentCatId = new ArrayList<>();

                        // c. loop parent category from client
                        for (Integer catId : catIdsList) {

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

                            } else {
                                failedParentCatId.add(catId);
                            }
                        }

                        // g. return results
                        if(failedParentCatId.isEmpty()) {
                            return Result.ok();
                        } else {
                            Map<String, Object> map = new LinkedHashMap<>();
                            map.put("failedCatId", failedParentCatId);
                            return Result.error(map, ResultCodeEnum.PARENT_CAT_NOT_EXIST);
                        }
                    } else {
                        return Result.error(ResultCodeEnum.THESIS_EXIST);
                    }
                } else {
                    return Result.error(ResultCodeEnum.THESIS_ID_NOT_EXIST);
                }
            } else {
                return Result.error(ResultCodeEnum.INVALID_COPYRIGHT);
            }
        } else {
            return Result.error(ResultCodeEnum.INVALID_TYPE);
        }
    }

    @DeleteMapping("/delete/{thesisId}")
    public Result<Object> deleteThesis(@PathVariable("thesisId") Integer id,
                                       @RequestBody String reason) {

        reason = decodeUrl(reason);
        reason = reason.substring(0, reason.length() - 1); // remove the last "="

        // 1. get id
        int userId = Math.toIntExact(BaseContext.getCurrentId());

        // 2. check thesis exist
        Thesis thesis = thesisService.getById(id);
        if(thesis != null) {

            // a. delete category link
            List<CategoryLink> categoryLinkList = categoryLinkService.getLinkByChildId(
                    thesis.getId(), CategoryEnum.TYPE_LINK_THESIS.getCode());
            for(CategoryLink categoryLink : categoryLinkList) {

                // a-1. update category statistics
                Category category = categoryService.getById(categoryLink.getCatTo());
                category.setCatTheses(category.getCatTheses() - 1);

                // a-2. delete category link by id
                categoryLinkService.removeById(categoryLink.getId());
            }

            // b. move oss file to delete folder
            String file = thesis.getFileName();
            String deletedFile = "";
            if(file.startsWith("default/")) {
                deletedFile = "deleted/" + file.substring("default/".length() + 1);
                OSSUtils.moveFile(file, deletedFile);
            }

            // c. add thesis to delThesis table
            DelThesis delThesis = new DelThesis(thesis, deletedFile, userId);
            delThesisService.save(delThesis);

            // d. delete thesis
            thesisService.removeById(thesis);

            // e. send email
            User uploader = userService.getUserById(thesis.getUploader());
            User admin = userService.getUserById(userId);
            if(!Objects.equals(uploader.getId(), admin.getId())) {
                if(!uploader.getEmail().isEmpty()) {
                    if(uploader.getSendEmail().equals("ON")) {
                        DirectMailUtils.sendEmail(uploader.getEmail(),
                                EmailContentHelper.getDeleteThesisNotificationEmailSubject(uploader.getLang()),
                                EmailContentHelper.getDeleteThesisNotificationEmailBody(uploader.getLang(),
                                        uploader.getUsername(), thesis.getTitle(), reason, admin.getUsername()));
                    }
                }
            }
            return Result.ok();
        } else {
            return Result.error(ResultCodeEnum.THESIS_ID_NOT_EXIST);
        }
    }

    @GetMapping("/categoryTheses/{catId}")
    public Result<Object> getCategoryTheses(@PathVariable("catId") int catId,
                                             @RequestParam String sortCol,
                                             @RequestParam String sortOrder) {

        List<String> validSortCol = List.of("author", "title", "publication", "thesisIssue");
        if(!validSortCol.contains(sortCol)) {
            sortCol = "";
        }
        if(!sortOrder.equals("ASC") && !sortOrder.equals("DESC")) {
            sortOrder = "";
        }

        Category category = categoryService.getById(catId);
        if(category != null) {
            List<CategoryLink> thesisLinks = categoryLinkService.getLinkByParentId(category.getId(),
                    CategoryEnum.TYPE_LINK_THESIS.getCode());
            List<ThesisRow> theses = new ArrayList<>();
            for(CategoryLink link : thesisLinks) {
                Thesis thesis = thesisService.getById(link.getCatFrom());
                if(thesis != null) {
                    ThesisIssue thesisIssue = new ThesisIssue(
                            thesis.getYear() == null ? "" : thesis.getYear().toString(),
                            thesis.getVolume() == null ? "" : thesis.getVolume().toString(),
                            thesis.getIssue() == null ? "" : thesis.getIssue()
                    );
                    List<CategoryName> categories = getThesisCat(thesis);
                    ThesisRow row = new ThesisRow(thesis.getId(), thesis.getAuthor(), thesis.getTitle(),
                            thesis.getPublication(), thesisIssue, categories);
                    theses.add(row);
                }
            }

            // sort by column
            if(!(sortCol.isEmpty() || sortOrder.isEmpty())) {
                String finalSortCol = sortCol;
                String finalSortOrder = sortOrder;

                List<ThesisRow> thesisSortList = new ArrayList<>();
                List<ThesisRow> thesisNullList = new ArrayList<>();
                switch (sortCol) {
                    case "author":
                        for (ThesisRow t : theses) {
                            if (t.getAuthor() == null || t.getAuthor().isEmpty()) {
                                thesisNullList.add(t);
                            } else {
                                thesisSortList.add(t);
                            }
                        }
                        break;
                    case "title":
                        for (ThesisRow t : theses) {
                            if (t.getTitle() == null || t.getTitle().isEmpty()) {
                                thesisNullList.add(t);
                            } else {
                                thesisSortList.add(t);
                            }
                        }
                        break;
                    case "publication":
                        for (ThesisRow t : theses) {
                            if (t.getPublication() == null || t.getPublication().isEmpty()) {
                                thesisNullList.add(t);
                            } else {
                                thesisSortList.add(t);
                            }
                        }
                        break;
                    case "thesisIssue":
                        for (ThesisRow t : theses) {
                            if (t.getThesisIssue().getYear().isEmpty()) {
                                thesisNullList.add(t);
                            } else {
                                thesisSortList.add(t);
                            }
                        }
                        break;
                }

                thesisSortList.sort((t1, t2) -> {
                    switch (finalSortCol) {
                        case "author":
                            if (finalSortOrder.equals("ASC")) {
                                return Collator.getInstance(Locale.CHINESE).
                                        compare(t1.getAuthor().toLowerCase(),t2.getAuthor().toLowerCase());
                            } else {
                                return Collator.getInstance(Locale.CHINESE).
                                        compare(t2.getAuthor().toLowerCase(),t1.getAuthor().toLowerCase());
                            }
                        case "title":
                            if (finalSortOrder.equals("ASC")) {
                                return Collator.getInstance(Locale.CHINESE).
                                        compare(t1.getTitle().toLowerCase(),t2.getTitle().toLowerCase());
                            } else {
                                return Collator.getInstance(Locale.CHINESE).
                                        compare(t2.getTitle().toLowerCase(),t1.getTitle().toLowerCase());
                            }
                        case "publication":
                            if (finalSortOrder.equals("ASC")) {
                                return Collator.getInstance(Locale.CHINESE).
                                        compare(t1.getPublication().toLowerCase(),t2.getPublication().toLowerCase());
                            } else {
                                return Collator.getInstance(Locale.CHINESE).
                                        compare(t2.getPublication().toLowerCase(),t1.getPublication().toLowerCase());
                            }
                        default:
                            if (finalSortOrder.equals("ASC")) {
                                return Integer.compare(
                                        Integer.parseInt(t1.getThesisIssue().getYear()),
                                        Integer.parseInt(t2.getThesisIssue().getYear()));
                            } else {
                                return Integer.compare(
                                        Integer.parseInt(t2.getThesisIssue().getYear()),
                                        Integer.parseInt(t1.getThesisIssue().getYear()));
                            }
                    }
                });

                thesisSortList.addAll(thesisNullList);
                return Result.ok(thesisSortList);
            } else {
                return Result.ok(theses);
            }
        } else {
            return Result.error(ResultCodeEnum.CATEGORY_ID_NOT_EXIST);
        }
    }

    @GetMapping("/missingFile")
    public Result<Object> missingFile() {
        List<Thesis> list = thesisService.getAll();
        List<ThesisName> result = new ArrayList<>();
        for(Thesis t : list) {
            if(t.getFileName().length() == 0) {
                result.add(new ThesisName(t.getId(), t.getTitle()));
            }
        }
        return Result.ok(result);
    }

    @GetMapping("/thesisWithoutCat")
    public Result<Object> thesisWithoutCat() {
        List<Thesis> list = thesisService.getAll();
        List<ThesisName> result = new ArrayList<>();
        for(Thesis t : list) {
            List<CategoryLink> categoryLink = categoryLinkService.getLinkByChildId(t.getId(), CategoryEnum.TYPE_LINK_THESIS.getCode());
            if(categoryLink.size() == 0) {
                result.add(new ThesisName(t.getId(), t.getTitle()));
            }
        }
        return Result.ok(result);
    }

    private List<CategoryName> getThesisCat(Thesis thesis) {
        List<CategoryLink> categoryLinkList = categoryLinkService.getLinkByChildId(
                thesis.getId(), CategoryEnum.TYPE_LINK_THESIS.getCode());
        List<CategoryName> categories = new ArrayList<>();
        for(CategoryLink link : categoryLinkList) {
            CategoryName c = new CategoryName(link.getCatTo(), link.getCatToZhName(), link.getCatToEnName());
            categories.add(c);
        }
        return categories;
    }

    private static int calculateRemain(int points, int todayDownload) {
        int level = 0;
        for(int i = 0; i < 21; i++) {
            points = points - i;
            level = i;
            if(points < 0) {
                break;
            }
        }
        return level - todayDownload;
    }

    private static String decodeUrl(String url)
    {
        String prevURL = "";
        String decodeURL = url;
        while(!prevURL.equals(decodeURL)) {
            prevURL = decodeURL;
            decodeURL = URLDecoder.decode( decodeURL, StandardCharsets.UTF_8);
        }
        return decodeURL;
    }

}
