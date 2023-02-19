package com.example.yunhists.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.example.yunhists.entity.Thesis;

public interface ThesisService extends IService<Thesis> {
    boolean validateNotExist(Thesis thesis);

    IPage<Thesis> getThesisBySearch(Page<Thesis> page, String author, String title, String publication, String year,
                                    String sortCol, String sortOrder);
}
