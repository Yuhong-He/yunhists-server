package com.example.yunhists.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.yunhists.entity.*;
import com.example.yunhists.enumeration.CategoryEnum;
import com.example.yunhists.enumeration.ResultCodeEnum;
import com.example.yunhists.pojo.BatchOperateCatFailed;
import com.example.yunhists.pojo.CategoryName;
import com.example.yunhists.pojo.CategoryWithParentCat;
import com.example.yunhists.pojo.UpdateALotCat;
import com.example.yunhists.service.CategoryLinkService;
import com.example.yunhists.service.CategoryService;
import com.example.yunhists.service.ThesisService;
import com.example.yunhists.service.UserService;
import com.example.yunhists.utils.ControllerUtils;
import com.example.yunhists.utils.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

import static com.example.yunhists.utils.ControllerUtils.printException;

@CrossOrigin
@RestController
@RequestMapping("/api/category")
public class CategoryController {

    @Autowired
    private UserService userService;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private CategoryLinkService categoryLinkService;

    @Autowired
    private ThesisService thesisService;

    @PostMapping("/add")
    public Result<Object> add(@RequestBody CategoryWithParentCat categoryWithParentCat,
                              HttpServletRequest request) {

        // 1. get token
        Object obj = ControllerUtils.getUserIdFromToken(request);
        try {

            // 2. get id (if obj is not number, throw exception, case token error)
            Integer userId = (Integer) obj;

            // 3. check user rights
            if(userService.getUserById(userId) != null && userService.getUserById(userId).getUserRights() >= 1) {

                String zhName = categoryWithParentCat.getZhName();
                String enName = categoryWithParentCat.getEnName();

                // 4. check chinese name
                if(categoryService.validateChineseName(zhName)) {

                    // 5. check english name
                    if(categoryService.validateEnglishName(enName)) {

                        // a. Add category
                        Category category = new Category(zhName, enName, userId);
                        categoryService.save(category);
                        int catId = category.getId();
                        ArrayList<Integer> failedParentCatId = new ArrayList<>();

                        // b. loop parent category from client
                        List<Integer> list = categoryWithParentCat.getParentCat();
                        for (Integer parentId : list) {

                            // c. check parent category exist
                            Category parentCat = categoryService.getById(parentId);
                            if(parentCat != null) {

                                // d. add category link
                                CategoryLink categoryLink = new CategoryLink(
                                        catId, parentCat.getId(), parentCat.getZhName(),
                                        parentCat.getEnName(), CategoryEnum.TYPE_LINK_CATEGORY.getCode(), userId);
                                categoryLinkService.save(categoryLink);

                                // e. update category statistics
                                parentCat.setCatSubCats(parentCat.getCatSubCats() + 1);
                                categoryService.saveOrUpdate(parentCat);

                            } else {
                                failedParentCatId.add(parentId);
                            }
                        }
                        if(failedParentCatId.isEmpty()) {
                            return Result.ok();
                        } else {
                            Map<String, Object> map = new LinkedHashMap<>();
                            map.put("failedParentCatId", failedParentCatId);
                            return Result.error(map, ResultCodeEnum.PARENT_CAT_NOT_EXIST);
                        }
                    } else {
                        return Result.error(ResultCodeEnum.REPEAT_EN_NAME);
                    }
                } else {
                    return Result.error(ResultCodeEnum.REPEAT_ZH_NAME);
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

    @GetMapping("/option/{catName}/{lang}")
    public Result<Object> catOptions(@PathVariable("catName") String catName,
                                      @PathVariable("lang") String lang) {
        if(!catName.isEmpty()) {
            Map<String, Object> response = new LinkedHashMap<>();
            List<CategoryName> catOptions = new ArrayList<>();
            List<Category> catList = categoryService.getCategories(catName, lang);
            for(Category cat : catList) {
                catOptions.add(new CategoryName(cat.getId(), cat.getZhName(), cat.getEnName()));
            }
            response.put("catOptions", catOptions);
            return Result.ok(response);
        } else {
            return Result.error(ResultCodeEnum.EMPTY_SEARCH);
        }
    }

    @GetMapping("/list/{lang}/{pageNo}/{pageSize}")
    public Result<Object> list(@PathVariable("lang") String lang,
                               @PathVariable("pageNo") Integer pageNo,
                               @PathVariable("pageSize") Integer pageSize,
                               @RequestParam String name,
                               @RequestParam String sortCol,
                               @RequestParam String sortOrder) {

        if(sortCol.equals("catTheses")) {
            sortCol = "cat_theses";
        } else if (sortCol.equals("catSubCats")) {
            sortCol = "cat_subcats";
        } else {
            sortCol = "";
        }
        if(!sortOrder.equals("ASC") && !sortOrder.equals("DESC")) {
            sortOrder = "";
        }

        Page<Category> page = new Page<>(pageNo, pageSize);
        IPage<Category> pageRs =  categoryService.getCategoryByNameLike(page, name, lang, sortCol, sortOrder);
        return Result.ok(pageRs);
    }

    @PostMapping("/addCatALot")
    public Result<Object> addCatALot(@RequestBody UpdateALotCat updateAlotCat,
                                     HttpServletRequest request) {

        // 1. get token
        Object obj = ControllerUtils.getUserIdFromToken(request);
        try {

            // 2. get id (if obj is not number, throw exception, case token error)
            Integer userId = (Integer) obj;

            // 3. check user rights
            if(userService.getUserById(userId) != null && userService.getUserById(userId).getUserRights() >= 1) {

                List<BatchOperateCatFailed> failed = new ArrayList<>();

                // 一. add cat to cat
                for(Integer c : updateAlotCat.getCategories()) {

                    // a. check child category exist
                    Category childCat = categoryService.getById(c);
                    if(childCat != null) {

                        for(Integer p : updateAlotCat.getParentCats()) {

                            // b. check parent category exist
                            Category parentCat = categoryService.getById(p);
                            if(parentCat != null) {

                                // c. check parent category is not the child category
                                if(!c.equals(p)) {

                                    // d. check the category link not exist
                                    if(categoryLinkService.linkNotExist(c, p, CategoryEnum.TYPE_LINK_CATEGORY.getCode())) {

                                        // e. add new category link
                                        CategoryLink categoryLink = new CategoryLink(c, p, parentCat.getZhName(),
                                                parentCat.getEnName(), CategoryEnum.TYPE_LINK_CATEGORY.getCode(),
                                                userId);
                                        categoryLinkService.save(categoryLink);

                                        // f. update category denormalization info
                                        parentCat.setCatSubCats(parentCat.getCatSubCats() + 1);
                                        categoryService.saveOrUpdate(parentCat);
                                    } else {
                                        failed.add(new BatchOperateCatFailed(
                                                c, p, CategoryEnum.TYPE_LINK_CATEGORY.getCode(),
                                                CategoryEnum.CATEGORY_LINK_EXIST.getCode()));
                                    }
                                } else {
                                    failed.add(new BatchOperateCatFailed(
                                            c, p, CategoryEnum.TYPE_LINK_CATEGORY.getCode(),
                                            CategoryEnum.CAN_NOT_ADD_CAT_TO_ITSELF.getCode()));
                                }
                            } else {
                                failed.add(new BatchOperateCatFailed(
                                        c, p, CategoryEnum.TYPE_LINK_CATEGORY.getCode(),
                                        CategoryEnum.PARENT_CAT_NOT_EXIST.getCode()));
                            }
                        }
                    } else {
                        failed.add(new BatchOperateCatFailed(
                                c, 0, CategoryEnum.TYPE_LINK_CATEGORY.getCode(),
                                CategoryEnum.CHILD_CAT_NOT_EXIST.getCode()));
                    }
                }

                // 二. add cat to thesis
                for(Integer t : updateAlotCat.getTheses()) {

                    // a. check thesis exist
                    Thesis thesis = thesisService.getById(t);
                    if(thesis != null) {

                        for(Integer p : updateAlotCat.getParentCats()) {

                            // b. check parent category exist
                            Category parentCat = categoryService.getById(p);
                            if(parentCat != null) {

                                // c check the category link not exist
                                if(categoryLinkService.linkNotExist(t, p, CategoryEnum.TYPE_LINK_THESIS.getCode())) {

                                    // d. add new category link
                                    CategoryLink categoryLink = new CategoryLink(t, p, parentCat.getZhName(),
                                            parentCat.getEnName(), CategoryEnum.TYPE_LINK_THESIS.getCode(),
                                            userId);
                                    categoryLinkService.save(categoryLink);

                                    // e. update category denormalization info
                                    parentCat.setCatTheses(parentCat.getCatTheses() + 1);
                                    categoryService.saveOrUpdate(parentCat);
                                } else {
                                    failed.add(new BatchOperateCatFailed(
                                            t, p, CategoryEnum.TYPE_LINK_THESIS.getCode(),
                                            CategoryEnum.CATEGORY_LINK_EXIST.getCode()));
                                }
                            } else {
                                failed.add(new BatchOperateCatFailed(
                                        t, p, CategoryEnum.TYPE_LINK_THESIS.getCode(),
                                        CategoryEnum.PARENT_CAT_NOT_EXIST.getCode()));
                            }
                        }
                    } else {
                        failed.add(new BatchOperateCatFailed(
                                t, 0, CategoryEnum.TYPE_LINK_THESIS.getCode(),
                                CategoryEnum.THESIS_NOT_EXIST.getCode()));
                    }
                }

                // 三. return results
                Map<String, Object> result = new LinkedHashMap<>();
                result.put("failed", failed);
                return Result.ok(result);

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

    @GetMapping("/id/{catId}")
    public Result<Object> getCategoryById(@PathVariable("catId") int catId) {

        Category category = categoryService.getCategoryByIdWithoutPrivacy(catId);
        if(category != null) {
            return Result.ok(category);
        } else {
            return Result.error(ResultCodeEnum.CATEGORY_ID_NOT_EXIST);
        }
    }

    @GetMapping("/ids")
    public Result<Object> getCategoriesByBatchId(@RequestParam("ids") int[] catId) {

        List<Integer> list = new ArrayList<>();
        for (int i : catId) {
            list.add(i);
        }
        List<Category> categories = categoryService.getCategoriesByBatchId(list);
        if(categories != null) {
            List<CategoryName> categoryNames = new ArrayList<>();
            for(Category c : categories) {
                categoryNames.add(new CategoryName(c.getId(), c.getZhName(), c.getEnName()));
            }
            return Result.ok(categoryNames);
        } else {
            return Result.error(ResultCodeEnum.CATEGORY_ID_NOT_EXIST);
        }
    }

    @GetMapping("/parentCats/{catId}")
    public Result<Object> getCategoryParentCats(@PathVariable("catId") int catId) {

        Category category = categoryService.getById(catId);
        if(category != null) {
            List<CategoryLink> parentCatLinks = categoryLinkService.getLinkByChildId(category.getId(),
                    CategoryEnum.TYPE_LINK_CATEGORY.getCode());
            List<CategoryName> parentCats = new ArrayList<>();
            for(CategoryLink link : parentCatLinks) {
                CategoryName parentCat = new CategoryName(link.getCatTo(), link.getCatToZhName(), link.getCatToEnName());
                parentCats.add(parentCat);
            }
            return Result.ok(parentCats);
        } else {
            return Result.error(ResultCodeEnum.CATEGORY_ID_NOT_EXIST);
        }
    }

    @GetMapping("/childCat/{catId}")
    public Result<Object> getCategoryChildCats(@PathVariable("catId") int catId,
                                               @RequestParam String sortCol,
                                               @RequestParam String sortOrder) {

        if(!sortCol.equals("catTheses") && !sortCol.equals("catSubCats")) {
            sortCol = "";
        }
        if(!sortOrder.equals("ASC") && !sortOrder.equals("DESC")) {
            sortOrder = "";
        }

        Category category = categoryService.getById(catId);
        if(category != null) {
            List<CategoryLink> childCatLinks = categoryLinkService.getLinkByParentId(category.getId(),
                    CategoryEnum.TYPE_LINK_CATEGORY.getCode());
            List<Category> childCats = new ArrayList<>();
            for(CategoryLink link : childCatLinks) {
                Category childCat = categoryService.getCategoryByIdWithoutPrivacy(link.getCatFrom());
                if(childCat != null) { // avoid data inconsistency and loss of category leading to program errors
                    childCats.add(childCat);
                }
            }

            // sort by column
            if(!(sortCol.isEmpty() || sortOrder.isEmpty())) {
                String finalSortCol = sortCol;
                String finalSortOrder = sortOrder;
                childCats.sort((c1, c2) -> {
                    if (finalSortCol.equals("catTheses")) {
                        if (finalSortOrder.equals("ASC")) {
                            return Integer.compare(c1.getCatTheses(), c2.getCatTheses());
                        } else {
                            return Integer.compare(c2.getCatTheses(), c1.getCatTheses());
                        }
                    } else {
                        if (finalSortOrder.equals("ASC")) {
                            return Integer.compare(c1.getCatSubCats(), c2.getCatSubCats());
                        } else {
                            return Integer.compare(c2.getCatSubCats(), c1.getCatSubCats());
                        }
                    }
                });
            }

            return Result.ok(childCats);
        } else {
            return Result.error(ResultCodeEnum.CATEGORY_ID_NOT_EXIST);
        }
    }

    @PostMapping("/updateCatName/{catId}")
    public Result<Object> updateCatName(@PathVariable("catId") int catId,
                                        @RequestParam String zhName,
                                        @RequestParam String enName,
                                        HttpServletRequest request) {

        // 1. get token
        Object obj = ControllerUtils.getUserIdFromToken(request);
        try {

            // 2. get id (if obj is not number, throw exception, case token error)
            Integer userId = (Integer) obj;

            // 3. check user rights
            if(userService.getUserById(userId) != null && userService.getUserById(userId).getUserRights() >= 1) {

                // 4. check category id exist
                Category category = categoryService.getById(catId);
                if(category != null) {

                    // 5. check chinese name
                    if(categoryService.validateChineseName(catId, zhName)) {

                        // 6. check english name
                        if(categoryService.validateEnglishName(catId, enName)) {

                            category.setZhName(zhName);
                            category.setEnName(enName);
                            categoryService.saveOrUpdate(category);

                            List<CategoryLink> list = categoryLinkService.getLinkByParentId(catId);
                            for(CategoryLink link : list) {
                                link.setCatToZhName(zhName);
                                link.setCatToEnName(enName);
                                categoryLinkService.saveOrUpdate(link);
                            }

                            return Result.ok();
                        } else {
                            return Result.error(ResultCodeEnum.REPEAT_EN_NAME);
                        }
                    } else {
                        return Result.error(ResultCodeEnum.REPEAT_ZH_NAME);
                    }
                } else {
                    return Result.error(ResultCodeEnum.CATEGORY_ID_NOT_EXIST);
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

    @PostMapping("/updateCatParentCat/{catId}")
    public Result<Object> updateCatParentCat(@PathVariable("catId") int catId,
                                             @RequestParam("lang") String lang,
                                             @RequestParam("categories") String[] catNames,
                                             HttpServletRequest request) {

        // 1. get token
        Object obj = ControllerUtils.getUserIdFromToken(request);
        try {

            // 2. get id (if obj is not number, throw exception, case token error)
            Integer userId = (Integer) obj;

            // 3. check user rights
            if(userService.getUserById(userId) != null && userService.getUserById(userId).getUserRights() >= 1) {

                // 4. check category id exist
                Category category = categoryService.getById(catId);
                if(category != null) {

                    // a. get the catIds of the catNames
                    ArrayList<Integer> catIds = new ArrayList<>();
                    ArrayList<String> failedParentCatName = new ArrayList<>();
                    for(String catName : catNames) {
                        try {
                            int parentCatId = categoryService.getCatIdByName(catName, lang);
                            catIds.add(parentCatId);
                        } catch (Exception e) {
                            failedParentCatName.add(catName);
                        }
                    }

                    // b. handle catIds, the category from client
                    List<Integer> catIdsList = new ArrayList<>(); // ready adding list
                    List<Integer> catLinksList = new ArrayList<>(); // old category (from database)
                    List<CategoryLink> categoryLinkList = categoryLinkService.getLinkByChildId(
                            category.getId(), CategoryEnum.TYPE_LINK_CATEGORY.getCode());
                    for(CategoryLink link : categoryLinkList) {
                        catLinksList.add(link.getCatTo());
                    }
                    /*
                     * b-1. clean up parent category that removed:
                     *       loop categories of the category in database,
                     *       if new categories (from client) does not contain the category
                     *       remove this category in database
                     *  */
                    for(CategoryLink link : categoryLinkList) {
                        if(!catIds.contains(link.getCatTo())) {
                            Category cat = categoryService.getById(link.getCatTo());
                            cat.setCatSubCats(cat.getCatSubCats() - 1);
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

                    List<BatchOperateCatFailed> failed = new ArrayList<>();
                    // c. loop parent category from client
                    for (Integer parentCatId : catIdsList) {

                        // d. check parent category is not the child category
                        if(parentCatId != catId) {

                            // e. add category link
                            Category cat = categoryService.getById(parentCatId);
                            CategoryLink categoryLink = new CategoryLink(
                                    category.getId(), cat.getId(), cat.getZhName(),
                                    cat.getEnName(), CategoryEnum.TYPE_LINK_CATEGORY.getCode(), userId);
                            categoryLinkService.save(categoryLink);

                            // f. update category statistics
                            cat.setCatSubCats(cat.getCatSubCats() + 1);
                            categoryService.saveOrUpdate(cat);
                        } else {
                            failed.add(new BatchOperateCatFailed(
                                    catId, parentCatId, CategoryEnum.TYPE_LINK_CATEGORY.getCode(),
                                    CategoryEnum.CAN_NOT_ADD_CAT_TO_ITSELF.getCode()));
                        }
                    }

                    // g. return results
                    Map<String, Object> result = new LinkedHashMap<>();
                    result.put("failed", failed);
                    result.put("failedParentCatName", failedParentCatName);
                    return Result.ok(result);
                } else {
                    return Result.error(ResultCodeEnum.CATEGORY_ID_NOT_EXIST);
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

    @PostMapping("/removeFromCat/{catId}")
    public Result<Object> removeFromCat(@PathVariable("catId") int parentCatId,
                                        @RequestParam("subCats") int[] subCats,
                                        @RequestParam("subTheses") int[] subTheses,
                                        HttpServletRequest request) {

        // 1. get token
        Object obj = ControllerUtils.getUserIdFromToken(request);
        try {

            // 2. get id (if obj is not number, throw exception, case token error)
            Integer userId = (Integer) obj;

            // 3. check user rights
            if(userService.getUserById(userId) != null && userService.getUserById(userId).getUserRights() >= 1) {

                List<BatchOperateCatFailed> failed = new ArrayList<>();

                // 一. check parent category exist
                Category parentCat = categoryService.getById(parentCatId);
                if(parentCat != null) {

                    // 二. remove subCats from the cat
                    for(int c : subCats) {

                        // b. remove category link
                        CategoryLink categoryLink = categoryLinkService.getCategoryLinkByQuery(c, parentCatId,
                                CategoryEnum.TYPE_LINK_CATEGORY.getCode());
                        categoryLinkService.removeById(categoryLink);

                        // c. update category denormalization info
                        parentCat.setCatSubCats(parentCat.getCatSubCats() - 1);
                        categoryService.saveOrUpdate(parentCat);
                    }

                    // 三. remove subTheses from the cat
                    for(int t : subTheses) {

                        // b. remove category link
                        CategoryLink categoryLink = categoryLinkService.getCategoryLinkByQuery(t, parentCatId,
                                CategoryEnum.TYPE_LINK_THESIS.getCode());
                        categoryLinkService.removeById(categoryLink);

                        // c. update category denormalization info
                        parentCat.setCatTheses(parentCat.getCatTheses() - 1);
                        categoryService.saveOrUpdate(parentCat);
                    }
                } else {
                    failed.add(new BatchOperateCatFailed(
                            0, parentCatId, CategoryEnum.TYPE_LINK_CATEGORY.getCode(),
                            CategoryEnum.PARENT_CAT_NOT_EXIST.getCode()));
                }

                // 四. return results
                Map<String, Object> result = new LinkedHashMap<>();
                result.put("failed", failed);
                return Result.ok(result);
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

    @PostMapping("/moveTo")
    public Result<Object> moveTo(@RequestParam("originId") int originId,
                                 @RequestParam("destId") int destId,
                                 @RequestParam("subCats") int[] subCats,
                                 @RequestParam("subTheses") int[] subTheses,
                                 HttpServletRequest request) {

        // 1. get token
        Object obj = ControllerUtils.getUserIdFromToken(request);
        try {

            // 2. get id (if obj is not number, throw exception, case token error)
            Integer userId = (Integer) obj;

            // 3. check user rights
            if(userService.getUserById(userId) != null && userService.getUserById(userId).getUserRights() >= 1) {

                List<BatchOperateCatFailed> failed = new ArrayList<>();

                // 4. check originId exist
                Category origin = categoryService.getById(originId);
                if(origin != null) {

                    // 5. check destId exist
                    Category dest = categoryService.getById(destId);
                    if(dest != null) {

                        // 6. check dest is not origin
                        if(originId != destId) {

                            // 一. move subCats
                            for(int c : subCats) {

                                // a. check child category exist
                                Category childCat = categoryService.getById(c);
                                if(childCat != null) {

                                    // b. check parent category is not the child category
                                    if(c != destId) {

                                        // c. check the category link not exist
                                        if(categoryLinkService.linkNotExist(c, destId, CategoryEnum.TYPE_LINK_CATEGORY.getCode())) {

                                            // d. delete origin category link
                                            CategoryLink originLink = categoryLinkService.getCategoryLinkByQuery(
                                                    c, originId, CategoryEnum.TYPE_LINK_CATEGORY.getCode());
                                            if(originLink != null) { // if link null (case deleted recently), do nothing
                                                categoryLinkService.removeById(originLink);

                                                // e. update origin category denormalization info
                                                origin.setCatSubCats(origin.getCatSubCats() - 1);
                                                categoryService.saveOrUpdate(origin);
                                            }

                                            // f. add dest category link
                                            CategoryLink categoryLink = new CategoryLink(c, destId, dest.getZhName(),
                                                    dest.getEnName(), CategoryEnum.TYPE_LINK_CATEGORY.getCode(),
                                                    userId);
                                            categoryLinkService.save(categoryLink);

                                            // g. update dest category denormalization info
                                            dest.setCatSubCats(dest.getCatSubCats() + 1);
                                            categoryService.saveOrUpdate(dest);
                                        } else {
                                            failed.add(new BatchOperateCatFailed(
                                                    c, destId, CategoryEnum.TYPE_LINK_CATEGORY.getCode(),
                                                    CategoryEnum.CATEGORY_LINK_EXIST.getCode()));
                                        }
                                    } else {
                                        failed.add(new BatchOperateCatFailed(
                                                c, destId, CategoryEnum.TYPE_LINK_CATEGORY.getCode(),
                                                CategoryEnum.CAN_NOT_ADD_CAT_TO_ITSELF.getCode()));
                                    }
                                } else {
                                    failed.add(new BatchOperateCatFailed(
                                            c, destId, CategoryEnum.TYPE_LINK_CATEGORY.getCode(),
                                            CategoryEnum.CHILD_CAT_NOT_EXIST.getCode()));
                                }
                            }

                            // 二. move subTheses
                            for(int t : subTheses) {

                                // a. check thesis exist
                                Thesis childThesis = thesisService.getById(t);
                                if(childThesis != null) {

                                    // b. check the category link not exist
                                    if(categoryLinkService.linkNotExist(t, destId, CategoryEnum.TYPE_LINK_THESIS.getCode())) {

                                        // c. delete origin category link
                                        CategoryLink originLink = categoryLinkService.getCategoryLinkByQuery(
                                                t, originId, CategoryEnum.TYPE_LINK_THESIS.getCode());
                                        if(originLink != null) { // if link null (case deleted recently), do nothing
                                            categoryLinkService.removeById(originLink);

                                            // d. update origin category denormalization info
                                            origin.setCatTheses(origin.getCatTheses() - 1);
                                            categoryService.saveOrUpdate(origin);
                                        }

                                        // e. add dest category link
                                        CategoryLink categoryLink = new CategoryLink(t, destId, dest.getZhName(),
                                                dest.getEnName(), CategoryEnum.TYPE_LINK_THESIS.getCode(),
                                                userId);
                                        categoryLinkService.save(categoryLink);

                                        // f. update dest category denormalization info
                                        dest.setCatTheses(dest.getCatTheses() + 1);
                                        categoryService.saveOrUpdate(dest);
                                    } else {
                                        failed.add(new BatchOperateCatFailed(
                                                t, destId, CategoryEnum.TYPE_LINK_THESIS.getCode(),
                                                CategoryEnum.CATEGORY_LINK_EXIST.getCode()));
                                    }
                                } else {
                                    failed.add(new BatchOperateCatFailed(
                                            t, destId, CategoryEnum.TYPE_LINK_THESIS.getCode(),
                                            CategoryEnum.THESIS_NOT_EXIST.getCode()));
                                }
                            }
                        } else {
                            failed.add(new BatchOperateCatFailed(
                                    originId, destId, CategoryEnum.TYPE_LINK_CATEGORY.getCode(),
                                    CategoryEnum.MOVE_TO_ITSELF.getCode()));
                        }
                    } else {
                        failed.add(new BatchOperateCatFailed(
                                originId, destId, CategoryEnum.TYPE_LINK_CATEGORY.getCode(),
                                CategoryEnum.PARENT_CAT_NOT_EXIST.getCode()));
                    }
                } else {
                    failed.add(new BatchOperateCatFailed(
                            0, originId, CategoryEnum.TYPE_LINK_CATEGORY.getCode(),
                            CategoryEnum.PARENT_CAT_NOT_EXIST.getCode()));
                }

                // 7. return results
                Map<String, Object> result = new LinkedHashMap<>();
                result.put("failed", failed);
                return Result.ok(result);
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

    @PostMapping("/delete/{catId}")
    public Result<Object> delete(@PathVariable("catId") int catId,
                                 HttpServletRequest request) {

        // 1. get token
        Object obj = ControllerUtils.getUserIdFromToken(request);
        try {

            // 2. get id (if obj is not number, throw exception, case token error)
            Integer userId = (Integer) obj;

            // 3. check user rights
            if(userService.getUserById(userId) != null && userService.getUserById(userId).getUserRights() >= 1) {

                // 4. check cat exist
                Category category = categoryService.getById(catId);
                if(category != null) {

                    // 5. check cat empty
                    List<CategoryLink> parentList = categoryLinkService.getLinkByParentId(catId);
                    if(parentList.size() == 0) {

                        // a. delete category link
                        List<CategoryLink> childList = categoryLinkService.getLinkByChildId(catId,
                                CategoryEnum.TYPE_LINK_CATEGORY.getCode());
                        for(CategoryLink link : childList) {
                            // b. update parent cat statistics
                            Category parentCat = categoryService.getById(link.getCatTo());
                            parentCat.setCatSubCats(parentCat.getCatSubCats() - 1);
                            categoryService.saveOrUpdate(parentCat);
                            categoryLinkService.removeById(link);
                        }

                        // c. delete category
                        categoryService.removeById(category);
                        return Result.ok();
                    } else {
                        return Result.error(ResultCodeEnum.CAN_ONLY_DELETE_EMPTY_CAT);
                    }
                } else {
                    return Result.error(ResultCodeEnum.CATEGORY_ID_NOT_EXIST);
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
