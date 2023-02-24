package com.example.yunhists.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.yunhists.entity.*;
import com.example.yunhists.enumeration.CategoryEnum;
import com.example.yunhists.enumeration.ResultCodeEnum;
import com.example.yunhists.service.CategoryLinkService;
import com.example.yunhists.service.CategoryService;
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
                            Category parentCat = categoryService.getCategoryById(parentId);
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

                List<UpdateALotCatFailed> failed = new ArrayList<>();

                // 一. add cat to cat
                for(Integer c : updateAlotCat.getCategories()) {

                    // a. check child category exist
                    Category childCat = categoryService.getCategoryById(c);
                    if(childCat != null) {

                        for(Integer p : updateAlotCat.getParentCats()) {

                            // b. check parent category exist
                            Category parentCat = categoryService.getCategoryById(p);
                            if(parentCat != null) {

                                // c. check parent category is not the child category
                                if(!c.equals(p)) {

                                    // d check the category link not exist
                                    if(categoryLinkService.linkNotExist(c, p)) {

                                        // e. add new category link
                                        CategoryLink categoryLink = new CategoryLink(c, p, parentCat.getZhName(),
                                                parentCat.getEnName(), CategoryEnum.TYPE_LINK_CATEGORY.getCode(),
                                                userId);
                                        categoryLinkService.save(categoryLink);

                                        // e. update category denormalization info
                                        parentCat.setCatSubCats(parentCat.getCatSubCats() + 1);
                                        categoryService.saveOrUpdate(parentCat);
                                    } else {
                                        failed.add(new UpdateALotCatFailed(
                                                c, p, CategoryEnum.TYPE_LINK_CATEGORY.getCode(),
                                                CategoryEnum.CATEGORY_LINK_EXIST.getCode()));
                                    }
                                } else {
                                    failed.add(new UpdateALotCatFailed(
                                            c, p, CategoryEnum.TYPE_LINK_CATEGORY.getCode(),
                                            CategoryEnum.CAN_NOT_ADD_CAT_TO_ITSELF.getCode()));
                                }
                            } else {
                                failed.add(new UpdateALotCatFailed(
                                        c, p, CategoryEnum.TYPE_LINK_CATEGORY.getCode(),
                                        CategoryEnum.PARENT_CAT_NOT_EXIST.getCode()));
                            }
                        }
                    } else {
                        failed.add(new UpdateALotCatFailed(
                                c, 0, CategoryEnum.TYPE_LINK_CATEGORY.getCode(),
                                CategoryEnum.CHILD_CAT_NOT_EXIST.getCode()));
                    }
                }

                // 二. add cat to thesis
                for(Integer t : updateAlotCat.getTheses()) {
                    System.out.println(t);
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

        Category category = categoryService.getCategoryById(catId);
        if(category != null) {
            return Result.ok(category);
        } else {
            return Result.error(ResultCodeEnum.FAIL);
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
            return Result.error(ResultCodeEnum.FAIL);
        }
    }

}
