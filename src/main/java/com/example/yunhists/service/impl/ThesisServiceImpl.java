package com.example.yunhists.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.yunhists.entity.Thesis;
import com.example.yunhists.mapper.ThesisMapper;
import com.example.yunhists.service.ThesisService;
import org.springframework.stereotype.Service;

@Service("thesisServiceImpl")
public class ThesisServiceImpl extends ServiceImpl<ThesisMapper, Thesis> implements ThesisService {

    @Override
    public boolean validateNotExist(Thesis thesis) {
        QueryWrapper<Thesis> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("author", thesis.getAuthor());
        queryWrapper.eq("title", thesis.getTitle());
        queryWrapper.eq("publication", thesis.getPublication());
        Thesis t = baseMapper.selectOne(queryWrapper);
        return t == null;
    }

}
