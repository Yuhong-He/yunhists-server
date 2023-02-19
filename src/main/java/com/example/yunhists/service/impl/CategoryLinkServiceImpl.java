package com.example.yunhists.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.yunhists.entity.CategoryLink;
import com.example.yunhists.mapper.CategoryLinkMapper;
import com.example.yunhists.service.CategoryLinkService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service("categoryLinkServiceImpl")
public class CategoryLinkServiceImpl extends ServiceImpl<CategoryLinkMapper, CategoryLink> implements CategoryLinkService {

    @Override
    public boolean linkNotExist(int catFrom, int catTo) {
        QueryWrapper<CategoryLink> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("cat_from", catFrom);
        queryWrapper.eq("cat_to", catTo);
        return baseMapper.selectOne(queryWrapper) == null;
    }

    @Override
    public List<CategoryLink> getLinkByChildId(int id, int type) {
        QueryWrapper<CategoryLink> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("cat_from", id);
        queryWrapper.eq("cat_type", type);
        return baseMapper.selectList(queryWrapper);
    }

}
