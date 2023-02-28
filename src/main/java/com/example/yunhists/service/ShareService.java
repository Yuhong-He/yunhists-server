package com.example.yunhists.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.example.yunhists.entity.Share;

public interface ShareService extends IService<Share> {

    IPage<Share> getShareBySearch(Page<Share> page, int userId, String title);

}
