package com.example.yunhists.controller;

import com.example.yunhists.entity.*;
import com.example.yunhists.enumeration.ResultCodeEnum;
import com.example.yunhists.service.CategoryService;
import com.example.yunhists.service.ShareService;
import com.example.yunhists.service.ThesisService;
import com.example.yunhists.service.UserService;
import com.example.yunhists.utils.ControllerUtils;
import com.example.yunhists.utils.Result;
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

                        // 7. set uploader & status
                        share.setUploader(userId);
                        share.setStatus(0);

                        // a. check category exist
                        String catStr = share.getCategory();
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

                        // b. add thesis
                        shareService.save(share);

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

}
