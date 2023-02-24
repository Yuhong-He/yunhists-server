package com.example.yunhists.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.yunhists.entity.DelThesis;
import com.example.yunhists.mapper.DelThesisMapper;
import com.example.yunhists.service.DelThesisService;
import org.springframework.stereotype.Service;

@Service("delThesisServiceImpl")
public class DelThesisServiceImpl extends ServiceImpl<DelThesisMapper, DelThesis> implements DelThesisService {
}
