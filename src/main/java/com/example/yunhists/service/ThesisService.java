package com.example.yunhists.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.yunhists.entity.Thesis;

public interface ThesisService extends IService<Thesis> {
    boolean validateNotExist(Thesis thesis);
}
