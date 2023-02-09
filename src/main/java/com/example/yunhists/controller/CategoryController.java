package com.example.yunhists.controller;

import com.example.yunhists.entity.Category;
import com.example.yunhists.entity.CategoryLink;
import com.example.yunhists.entity.CategoryName;
import com.example.yunhists.entity.CategoryWithParentCat;
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
            if(userService.getUserById(userId) != null && userService.getUserById(userId).getUserRights() == 1) {

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
                                        parentCat.getEnName(), 1, userId);
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
            return (Result<Object>) obj;
        }
    }

    @GetMapping("/option/{catName}/{lang}")
    public Result<Object> getAllAdmin(@PathVariable("catName") String catName,
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

}
