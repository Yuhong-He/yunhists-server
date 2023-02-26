package com.example.yunhists.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.yunhists.entity.DelThesis;
import com.example.yunhists.mapper.DelThesisMapper;
import com.example.yunhists.service.DelThesisService;
import org.springframework.stereotype.Service;

@Service("delThesisServiceImpl")
public class DelThesisServiceImpl extends ServiceImpl<DelThesisMapper, DelThesis> implements DelThesisService {

    @Override
    public DelThesis getThesisByFile(String file) {
        QueryWrapper<DelThesis> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("file_name", file);
        return baseMapper.selectOne(queryWrapper);
    }

}
