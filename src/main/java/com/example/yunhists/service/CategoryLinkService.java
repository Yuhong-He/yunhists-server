package com.example.yunhists.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.yunhists.entity.CategoryLink;

import java.util.List;

public interface CategoryLinkService extends IService<CategoryLink> {
    boolean linkNotExist(int catFrom, int catTo);

    List<CategoryLink> getLinkByChildId(int id, int type);
}
