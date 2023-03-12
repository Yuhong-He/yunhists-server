package com.example.yunhists.common;

import com.example.yunhists.enumeration.ResultCodeEnum;
import lombok.Data;

@Data
public class Result<T> {

    private Integer code;
    private String message;
    private T data;

    public Result(){}

    private static <T> Result<T> build(T data) {
        Result<T> result = new Result<>();
        if (data != null)
            result.setData(data);
        return result;
    }

    private static <T> Result<T> build(ResultCodeEnum resultCodeEnum) {
        Result<T> result = new Result<>();
        result.setCode(resultCodeEnum.getCode());
        result.setMessage(resultCodeEnum.getMessage());
        return result;
    }

    private static <T> Result<T> build(T body, ResultCodeEnum resultCodeEnum) {
        Result<T> result = build(body);
        result.setCode(resultCodeEnum.getCode());
        result.setMessage(resultCodeEnum.getMessage());
        return result;
    }

    public static<T> Result<T> ok(T data){
        build(data);
        return build(data, ResultCodeEnum.SUCCESS);
    }

    public static<T> Result<T> ok(){
        return build(ResultCodeEnum.SUCCESS);
    }

    public static<T> Result<T> error(T data, ResultCodeEnum resultCodeEnum){
        build(data);
        return build(data, resultCodeEnum);
    }

    public static<T> Result<T> error(ResultCodeEnum resultCodeEnum){
        return build(resultCodeEnum);
    }
}
