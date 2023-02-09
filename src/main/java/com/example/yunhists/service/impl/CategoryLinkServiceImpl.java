package com.example.yunhists.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.yunhists.entity.CategoryLink;
import com.example.yunhists.mapper.CategoryLinkMapper;
import com.example.yunhists.service.CategoryLinkService;
import org.springframework.stereotype.Service;

@Service("categoryLinkServiceImpl")
public class CategoryLinkServiceImpl extends ServiceImpl<CategoryLinkMapper, CategoryLink> implements CategoryLinkService {
}
