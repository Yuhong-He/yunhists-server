package com.example.yunhists.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.yunhists.entity.*;
import com.example.yunhists.enumeration.ResultCodeEnum;
import com.example.yunhists.service.CategoryLinkService;
import com.example.yunhists.service.CategoryService;
import com.example.yunhists.service.ThesisService;
import com.example.yunhists.service.UserService;
import com.example.yunhists.utils.ControllerUtils;
import com.example.yunhists.utils.Result;
import org.apache.commons.lang.ArrayUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

import static com.example.yunhists.utils.ControllerUtils.printException;

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

    @PostMapping("/add")
    public Result<Object> add(@RequestBody Thesis thesis,
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

                // 4. check type valid
                List<Integer> validType = List.of(0,1,2);
                if(validType.contains(thesis.getType())) {

                    // 5. check copyright valid
                    List<Integer> validCopyright = List.of(0,1,2);
                    if(validCopyright.contains(thesis.getCopyrightStatus())) {

                        // 6. check duplicate
                        if(thesisService.validateNotExist(thesis)) {

                            // 7. set uploader & approver
                            thesis.setUploader(userId);
                            thesis.setApprover(userId);

                            // a. add thesis
                            thesisService.save(thesis);
                            ArrayList<Integer> failedParentCatId = new ArrayList<>();

                            // b. loop parent category from client
                            for (int catId : catIds) {

                                // c. check category exist
                                Category cat = categoryService.getCategoryById(catId);
                                if(cat != null) {

                                    // d. add category link
                                    CategoryLink categoryLink = new CategoryLink(
                                            thesis.getId(), cat.getId(), cat.getZhName(),
                                            cat.getEnName(), 0, userId);
                                    categoryLinkService.save(categoryLink);

                                    // e. update category statistics
                                    cat.setCatTheses(cat.getCatTheses() + 1);
                                    categoryService.saveOrUpdate(cat);

                                } else {
                                    failedParentCatId.add(catId);
                                }
                            }

                            // f. update user points
                            user.setPoints(user.getPoints() + 1);
                            userService.saveOrUpdate(user);

                            // g. return results
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
        IPage<Thesis> pageRs =  thesisService.getThesisBySearch(page, author, title, publication, year, sortCol, sortOrder);
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

            String vancouver = vancouverStyle(author, title, publication, location, publisher, year, volume, issue, pages, doi, type);
            String harvard = harvardStyle(author, title, publication, location, publisher, year, volume, issue, pages, doi, type);
            String gbt7714 = gbt7714Style(author, title, publication, location, publisher, year, volume, issue, pages, type);
            String wikipedia = wikipediaStyle(author, title, publication, location, publisher, year, volume, issue, pages, doi, isbn, type);


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
    public Result<Object> getDownloadNum(HttpServletRequest request) {

        // 1. get token
        Object obj = ControllerUtils.getUserIdFromToken(request);
        try {

            // 2. get id (if obj is not number, throw exception, case token error)
            Integer userId = (Integer) obj;

            // 3. check user rights
            User user = userService.getUserById(userId);
            if(user != null && user.getUserRights() >= 0) {
                int remain = calculateRemain(user.getPoints(), user.getTodayDownload());
                Map<String, Object> map = new LinkedHashMap<>();
                map.put("remain", remain);
                return Result.ok(map);
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

    @GetMapping("/file/{id}")
    public Result<Object> getFileName(@PathVariable("id") Integer id,
                                      HttpServletRequest request) {

        // 1. get token
        Object obj = ControllerUtils.getUserIdFromToken(request);
        try {

            // 2. get id (if obj is not number, throw exception, case token error)
            Integer userId = (Integer) obj;

            // 3. check user rights
            User user = userService.getUserById(userId);
            if(user != null && user.getUserRights() >= 0) {

                // 4. check thesis exists
                Thesis thesis = thesisService.getById(id);
                if(thesis != null) {

                    // 5. check file exist
                    if(thesis.getFileName().length() > 0) {

                        // 6. check user download today
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

    @GetMapping("/id/{thesisId}")
    public Result<Object> getThesisById(@PathVariable("thesisId") Integer id,
                                        HttpServletRequest request) {

        // 1. get token
        Object obj = ControllerUtils.getUserIdFromToken(request);
        try {

            // 2. get id (if obj is not number, throw exception, case token error)
            Integer userId = (Integer) obj;

            // 3. check user rights
            User user = userService.getUserById(userId);
            if(user != null && user.getUserRights() >= 1) {

                // 4. check thesis exist
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
    public Result<Object> deleteFileOfThesis(@RequestParam String file,
                                        HttpServletRequest request) {

        // 1. get token
        Object obj = ControllerUtils.getUserIdFromToken(request);
        try {

            // 2. get id (if obj is not number, throw exception, case token error)
            Integer userId = (Integer) obj;

            // 3. check user rights
            User user = userService.getUserById(userId);
            if(user != null && user.getUserRights() >= 1) {

                // 4. check file exist
                Thesis thesis = thesisService.getThesisByFile(file);
                if(thesis != null) {
                    thesis.setFileName("");
                    thesisService.saveOrUpdate(thesis);
                    return Result.ok();
                } else {
                    return Result.error(ResultCodeEnum.THESIS_FILE_NOT_EXIST);
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


    @PostMapping("/update")
    public Result<Object> update(@RequestParam("id") Integer thesisId,
                                 @RequestParam("category") int[] catIds,
                                 @RequestBody Thesis thesis,
                                 HttpServletRequest request) {

        // 1. get token
        Object obj = ControllerUtils.getUserIdFromToken(request);
        try {

            // 2. get id (if obj is not number, throw exception, case token error)
            Integer userId = (Integer) obj;

            // 3. check user rights
            User user = userService.getUserById(userId);
            if(user != null && user.getUserRights() >= 1) {

                // 4. check type valid
                List<Integer> validType = List.of(0,1,2);
                if(validType.contains(thesis.getType())) {

                    // 5. check copyright valid
                    List<Integer> validCopyright = List.of(0,1,2);
                    if(validCopyright.contains(thesis.getCopyrightStatus())) {

                        // 6.check thesis exist
                        Thesis targetThesis = thesisService.getById(thesisId);
                        if(targetThesis != null) {

                            // 7. set uploader & approver $ approve_time
                            thesis.setId(thesisId);
                            thesis.setUploader(targetThesis.getUploader());
                            thesis.setApprover(targetThesis.getApprover());
                            thesis.setApproveTime(targetThesis.getApproveTime());

                            // 8. check duplicate
                            if(thesisService.validateNotExistWhenUpdate(thesis)) {

                                // a. update thesis
                                thesisService.saveOrUpdate(thesis);

                                // b. handle catIds, the category from client
                                List<Integer> catIdsList = new ArrayList<>(); // ready adding list
                                List<Integer> catLinksList = new ArrayList<>(); // old category (from database)
                                List<CategoryLink> categoryLinkList = categoryLinkService.getLinkByChildId(thesis.getId(), 0);
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
                                        Category cat = categoryService.getCategoryById(link.getCatTo());
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
                                    Category cat = categoryService.getCategoryById(catId);
                                    if(cat != null) {

                                        // e. add category link
                                        CategoryLink categoryLink = new CategoryLink(
                                                thesis.getId(), cat.getId(), cat.getZhName(),
                                                cat.getEnName(), 0, userId);
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

    private List<CategoryName> getThesisCat(Thesis thesis) {
        List<CategoryLink> categoryLinkList = categoryLinkService.getLinkByChildId(thesis.getId(), 0);
        List<CategoryName> categories = new ArrayList<>();
        for(CategoryLink link : categoryLinkList) {
            CategoryName c = new CategoryName(link.getCatTo(), link.getCatToZhName(), link.getCatToEnName());
            categories.add(c);
        }
        return categories;
    }

    private static String vancouverStyle(String author, String title, String publication, String location,
                                         String publisher, String year, String volume, String issue,
                                         String pages, String doi, int type) {
        StringBuilder vancouver = new StringBuilder();
        if(!author.isEmpty()) {
            vancouver.append(author);
            vancouver.append(". ");
        }
        vancouver.append(title);
        if(type == 1 || type == 2) {
            if(!publication.isEmpty()) {
                vancouver.append(". In: ");
                vancouver.append(publication);
                vancouver.append(". ");
            }
            if(!location.isEmpty()) {
                vancouver.append(location);
                vancouver.append(": ");
            }
            if(!publisher.isEmpty()) {
                vancouver.append(publisher);
                vancouver.append("; ");
            }
            if(!year.isEmpty()) {
                vancouver.append(year);
            }
        } else { // type = 0
            if(!publication.isEmpty()) {
                vancouver.append(". ");
                vancouver.append(publication);
            }
            if(!year.isEmpty()) {
                vancouver.append(". ");
                vancouver.append(year);
                vancouver.append("; ");
            }
            if(!volume.isEmpty()) {
                vancouver.append(volume);
            }
            if(!issue.isEmpty()) {
                vancouver.append("(");
                vancouver.append(issue);
                vancouver.append(")");
            }
            if(!pages.isEmpty()) {
                vancouver.append(": ");
                vancouver.append(pages);
            }
        }
        if(!doi.isEmpty()) {
            vancouver.append(". Available from: ");
            vancouver.append(doi);
        }
        vancouver.append(".");

        return String.valueOf(vancouver);
    }

    private static String harvardStyle(String author, String title, String publication, String location,
                                         String publisher, String year, String volume, String issue,
                                         String pages, String doi, int type) {
        StringBuilder harvard = new StringBuilder();
        if(!author.isEmpty()) {
            harvard.append(author);
        }
        if(!year.isEmpty()) {
            harvard.append(" (");
            harvard.append(year);
            harvard.append("). ");
        }
        harvard.append("'");
        harvard.append(title);
        harvard.append("'");
        if(type == 1 || type == 2) {
            if(!publication.isEmpty()) {
                harvard.append(" in ");
                harvard.append(publication);
                harvard.append(". ");
            }
            if(!location.isEmpty()) {
                harvard.append(location);
                harvard.append(": ");
            }
            if(!publisher.isEmpty()) {
                harvard.append(publisher);
                harvard.append(", ");
            }
        } else { // type = 0
            if(!publication.isEmpty()) {
                harvard.append(". ");
                harvard.append(publication);
            }
            if(!volume.isEmpty()) {
                harvard.append(", ");
                harvard.append(volume);
            }
            if(!issue.isEmpty()) {
                harvard.append("(");
                harvard.append(issue);
                harvard.append("), ");
            }
        }
        if(!pages.isEmpty()) {
            harvard.append("pp.");
            harvard.append(pages);
        }
        if(!doi.isEmpty()) {
            harvard.append(". doi:");
            harvard.append(doi);
        }
        harvard.append(".");

        return String.valueOf(harvard);
    }

    private static String gbt7714Style(String author, String title, String publication, String location,
                                       String publisher, String year, String volume, String issue,
                                       String pages, int type) {
        StringBuilder gbt7714 = new StringBuilder();
        if(!author.isEmpty()) {
            author = author.replaceAll(",", "，");
            gbt7714.append(author);
            gbt7714.append("．");
        }
        gbt7714.append(title);
        if(type == 0) {
            gbt7714.append("[J]");
        } else if(type == 1) {
            gbt7714.append("[G]");
        } else if(type == 2) {
            gbt7714.append("[M]");
        }
        if(type == 1 || type == 2) {
            if(!publication.isEmpty()) {
                gbt7714.append("．");
                gbt7714.append(publication);
                gbt7714.append("．");
            }
            if(!location.isEmpty()) {
                gbt7714.append(location);
                gbt7714.append("：");
            }
            if(!publisher.isEmpty()) {
                gbt7714.append(publisher);
                gbt7714.append("，");
            }
            if(!year.isEmpty()) {
                gbt7714.append(year);
            }
        } else { // type = 0
            if(!publication.isEmpty()) {
                gbt7714.append("．");
                gbt7714.append(publication);
            }
            if(!year.isEmpty()) {
                gbt7714.append("，");
                gbt7714.append(year);
                gbt7714.append("，");
            }
            if(!volume.isEmpty()) {
                gbt7714.append(volume);
            }
            if(!issue.isEmpty()) {
                gbt7714.append("(");
                gbt7714.append(issue);
                gbt7714.append(")");
            }
        }
        if(!pages.isEmpty()) {
            gbt7714.append("：");
            gbt7714.append(pages);
        }
        gbt7714.append("．");

        return String.valueOf(gbt7714);
    }

    private static String wikipediaStyle(String author, String title, String publication, String location,
                                         String publisher, String year, String volume, String issue,
                                         String pages, String isbn, String doi, int type) {
        StringBuilder wikipedia = new StringBuilder();
        if(type == 0) {
            wikipedia.append("{{cite journal");
        } else {
            wikipedia.append("{{cite book");
        }
        if(!author.isEmpty()) {
            if(author.contains(",")) {
                int i = 0;
                for (String au: author.split(",")){
                    i++;
                    wikipedia.append(" |author");
                    wikipedia.append(i);
                    wikipedia.append("=");
                    wikipedia.append(au);
                }
            } else {
                wikipedia.append(" |author=");
                wikipedia.append(author);
            }
        }
        if(type == 1 || type == 2) {
            wikipedia.append(" |chapter=");
            wikipedia.append(title);
            if(!publication.isEmpty()) {
                wikipedia.append(" |title=");
                wikipedia.append(publication);
            }
            if(!location.isEmpty()) {
                wikipedia.append(" |location=");
                wikipedia.append(location);
            }
            if(!publisher.isEmpty()) {
                wikipedia.append(" |publisher=");
                wikipedia.append(publisher);
            }
            if(!year.isEmpty()) {
                wikipedia.append(" |year=");
                wikipedia.append(year);
            }
            if(!isbn.isEmpty()) {
                wikipedia.append(" |isbn=");
                wikipedia.append(isbn);
            }
        } else { // type = 0
            wikipedia.append(" |title=");
            wikipedia.append(title);
            if(!publication.isEmpty()) {
                wikipedia.append(" |journal=");
                wikipedia.append(publication);
            }
            if(!year.isEmpty()) {
                wikipedia.append(" |year=");
                wikipedia.append(year);
            }
            if(!volume.isEmpty()) {
                wikipedia.append(" |volume=");
                wikipedia.append(volume);
            }
            if(!issue.isEmpty()) {
                wikipedia.append(" |issue=");
                wikipedia.append(issue);
            }
        }
        if(!pages.isEmpty()) {
            wikipedia.append(" |pages=");
            wikipedia.append(pages);
        }
        if(!doi.isEmpty()) {
            wikipedia.append(" |doi=");
            wikipedia.append(doi);
        }
        wikipedia.append(" }}");

        return String.valueOf(wikipedia);
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

}
