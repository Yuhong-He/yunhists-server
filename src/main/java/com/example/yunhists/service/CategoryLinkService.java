package com.example.yunhists.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.yunhists.entity.CategoryLink;

public interface CategoryLinkService extends IService<CategoryLink> {
    boolean linkNotExist(int catFrom, int catTo);
}
