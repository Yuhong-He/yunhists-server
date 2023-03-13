package com.example.yunhists.utils;

public class AuthenticationPathHelper {

    public static String[] unregistered() {
        return new String[]{
                "/api/user/login",
                "/api/user/google",
                "/api/user/sendRegisterEmail",
                "/api/user/register",
                "/api/user/resetPassword",
                "/api/user/validateToken",
                "/api/statistics",
                "/api/category/list/{lang}/{pageNo}/{pageSize}",
                "/api/category/id/{catId}",
                "/api/category/ids",
                "/api/category/parentCats/{catId}",
                "/api/category/childCat/{catId}",
                "/api/thesis/list/{pageNo}/{pageSize}",
                "/api/thesis/cite/{id}",
                "/api/thesis/onlinePublishInfo/{id}",
                "/api/thesis/categoryTheses/{catId}",
        };
    }

    public static String[] banned() {
        return new String[]{
                "/api/user/getUserInfo",
                "/api/user/updateLang",
                "/api/user/updateEmailNotification",
                "/api/user/delete",
                "/api/user/updateUsername",
                "/api/user/sendChangeEmailEmail",
                "/api/user/updateEmail",
                "/api/user/updatePassword",
                "/api/user/validateToken",
                "/api/share/myList/{pageNo}",
        };
    }

    public static String[] user() {
        return new String[]{
                "/api/user/refreshSTS",
                "/api/thesis/getDownloadNum",
                "/api/thesis/file/{id}",
                "/api/category/option/{catName}/{lang}",
                "/api/share/add",
                "/api/share/delete/{shareId}",
                "/api/share/myShare/{shareId}",
                "/api/share/deleteFile",
                "/api/share/update/{shareId}",
        };
    }

    public static String[] admin() {
        return new String[]{
                "/api/category/add",
                "/api/category/addCatALot",
                "/api/category/updateCatName/{catId}",
                "/api/category/updateCatParentCat/{catId}",
                "/api/category/removeFromCat/{catId}",
                "/api/category/moveTo",
                "/api/category/delete/{catId}",
                "/api/category/catWithoutCat",
                "/api/category/emptyCat",
                "/api/thesis/add",
                "/api/thesis/id/{thesisId}",
                "/api/thesis/deleteFile",
                "/api/thesis/update",
                "/api/thesis/delete/{thesisId}",
                "/api/thesis/missingFile",
                "/api/thesis/thesisWithoutCat",
                "/api/share/listAll/{pageNo}",
                "/api/share/id/{shareId}",
                "/api/share/approve/{shareId}",
                "/api/share/reject/{shareId}",
        };
    }

}
