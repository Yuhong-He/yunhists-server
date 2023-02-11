package com.example.yunhists.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.yunhists.entity.Category;
import com.example.yunhists.mapper.CategoryMapper;
import com.example.yunhists.service.CategoryService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service("categoryServiceImpl")
public class CategoryServiceImpl extends ServiceImpl<CategoryMapper, Category> implements CategoryService {

    @Override
    public boolean validateChineseName(String zhName) {
        QueryWrapper<Category> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("zh_name", zhName);
        Category category = baseMapper.selectOne(queryWrapper);
        return category == null;
    }

    @Override
    public boolean validateEnglishName(String enName) {
        QueryWrapper<Category> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("en_name", enName);
        Category category = baseMapper.selectOne(queryWrapper);
        return category == null;
    }

    @Override
    public Category getCategoryById(int id) {
        return baseMapper.selectById(id);
    }

    @Override
    public List<Category> getCategoriesByBatchId(List<Integer> ids) {
        return baseMapper.selectBatchIds(ids);
    }

    @Override
    public IPage<Category> getCategoryByNameLike(Page<Category> page, String name,
                                                 String lang, String sortCol, String sortOrder) {
        QueryWrapper<Category> queryWrapper = new QueryWrapper<>();
        if(!name.isEmpty()){
            if(lang.equals("zh")) {
                queryWrapper.like("zh_name", name);
            } else {
                queryWrapper.like("en_name", name);
            }
        }
        if(!sortCol.isEmpty() && !sortOrder.isEmpty()) {
            queryWrapper.last(" ORDER BY " + sortCol + " " + sortOrder);
        } else {
            queryWrapper.orderByDesc("created_at");
        }
        return baseMapper.selectPage(page, queryWrapper);
    }

    @Override
    public List<Category> getCategories(String catName, String lang) {
        QueryWrapper<Category> queryWrapper = new QueryWrapper<>();
        if(lang.equals("zh")) {
            queryWrapper.like("zh_name", catName);
            queryWrapper.last("ORDER BY CONVERT ( zh_name USING gbk ) ASC");
        } else {
            queryWrapper.like("en_name", catName);
            queryWrapper.orderByAsc("en_name");
        }
        return baseMapper.selectList(queryWrapper);
    }

}
