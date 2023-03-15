package com.example.yunhists.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.yunhists.entity.Upload;
import com.example.yunhists.mapper.UploadMapper;
import com.example.yunhists.service.UploadService;
import org.springframework.stereotype.Service;

@Service("uploadServiceImpl")
public class UploadServiceImpl extends ServiceImpl<UploadMapper, Upload> implements UploadService {

    @Override
    public IPage<Upload> getUploadBySearch(Page<Upload> page, int userId, String title) {
        QueryWrapper<Upload> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("uploader", userId);
        if(!title.isEmpty()) {
            if(title.contains(" ")) {
                while(title.contains(" ")) {
                    String temp = title.substring(0, title.indexOf(" "));
                    title = title.substring(title.indexOf(" ") + 1);
                    queryWrapper.like("title", temp);
                }
            }
            queryWrapper.like("title", title);
        }
        queryWrapper.orderByDesc("upload_time");
        queryWrapper.select(Upload.class, info -> !info.getColumn().equals("approver")
                && !info.getColumn().equals("approve_time"));
        return baseMapper.selectPage(page, queryWrapper);
    }

    @Override
    public Upload getUploadByFile(String file) {
        QueryWrapper<Upload> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("file_name", file);
        return baseMapper.selectOne(queryWrapper);
    }

    @Override
    public IPage<Upload> getAllUploadBySearch(Page<Upload> page, String title, String unapproved) {
        QueryWrapper<Upload> queryWrapper = new QueryWrapper<>();
        if(!title.isEmpty()) {
            if(title.contains(" ")) {
                while(title.contains(" ")) {
                    String temp = title.substring(0, title.indexOf(" "));
                    title = title.substring(title.indexOf(" ") + 1);
                    queryWrapper.like("title", temp);
                }
            }
            queryWrapper.like("title", title);
        }
        if(unapproved.equals("ON")) {
            queryWrapper.eq("status", 0);
        }
        queryWrapper.orderByDesc("upload_time");
        return baseMapper.selectPage(page, queryWrapper);
    }

}
