package com.example.yunhists.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.example.yunhists.entity.Upload;

public interface UploadService extends IService<Upload> {

    IPage<Upload> getUploadBySearch(Page<Upload> page, int userId, String title);

    Upload getUploadByFile(String file);

    IPage<Upload> getAllUploadBySearch(Page<Upload> page, String title, String unapproved);
}
