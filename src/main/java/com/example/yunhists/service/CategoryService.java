package com.example.yunhists.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.example.yunhists.entity.Category;

import java.util.List;
import java.util.Map;

public interface CategoryService extends IService<Category> {
    boolean validateChineseName(String zhName);

    boolean validateEnglishName(String enName);

    Category getCategoryById(int id);

    List<Category> getCategoriesByBatchId(List<Integer> ids);

    IPage<Category> getCategoryByNameLike(Page<Category> page, String name,
                                          String lang, String sortCol, String sortOrder);

    List<Category> getCategories(String catName, String lang);
}