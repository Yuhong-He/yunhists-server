package com.example.yunhists.utils;

public class AuthenticationPathHelper {

    public static String[] unregistered() {
        return new String[]{
                "/api/user/login",
                "/api/user/google",
                "/api/user/register",
                "/api/user/sendRegisterEmail",
                "/api/user/resetPassword",
                "/api/user/validateToken",
                "/api/statistics/get",
                "/api/category/option/{catName}/{lang}",
                "/api/category/list/{lang}/{pageNo}/{pageSize}",
                "/api/category/id/{catId}",
                "/api/category/parentCats/{catId}",
                "/api/category/childCat/{catId}",
                "/api/thesis/list/{pageNo}/{pageSize}",
                "/api/thesis/cite/{id}",
                "/api/thesis/onlinePublishInfo/{id}",
                "/api/thesis/categoryTheses/{catId}",
        };
    }

}
