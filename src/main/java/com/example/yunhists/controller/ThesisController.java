package com.example.yunhists.controller;

import com.example.yunhists.entity.Category;
import com.example.yunhists.entity.CategoryLink;
import com.example.yunhists.entity.Thesis;
import com.example.yunhists.entity.User;
import com.example.yunhists.enumeration.ResultCodeEnum;
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

                // 4. check title exist
                if(!thesis.getTitle().isEmpty()) {

                    // 5. check type valid
                    List<Integer> validType = List.of(0,1,2);
                    if(validType.contains(thesis.getType())) {

                        // 6. check copyright valid
                        List<Integer> validCopyright = List.of(0,1,2);
                        if(validCopyright.contains(thesis.getCopyrightStatus())) {

                            // 7. check filename exist
                            if(!thesis.getFileName().isEmpty()) {

                                // 8. check duplicate
                                if(thesisService.validateNotExist(thesis)) {

                                    // 9. set uploader & approver
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
                                return Result.error(ResultCodeEnum.NO_FILE);
                            }
                        } else {
                            return Result.error(ResultCodeEnum.INVALID_COPYRIGHT);
                        }
                    } else {
                        return Result.error(ResultCodeEnum.INVALID_TYPE);
                    }
                } else {
                    return Result.error(ResultCodeEnum.NO_TITLE);
                }
            } else {
                obj = Result.error(ResultCodeEnum.NO_PERMISSION);
                throw new Exception();
            }
        } catch (Exception e) {
            return (Result<Object>) obj;
        }
    }

}
