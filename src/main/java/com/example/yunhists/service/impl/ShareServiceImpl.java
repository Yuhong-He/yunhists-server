package com.example.yunhists.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.yunhists.entity.Share;
import com.example.yunhists.mapper.ShareMapper;
import com.example.yunhists.service.ShareService;
import org.springframework.stereotype.Service;

@Service("shareServiceImpl")
public class ShareServiceImpl extends ServiceImpl<ShareMapper, Share> implements ShareService {

    @Override
    public IPage<Share> getShareBySearch(Page<Share> page, int userId, String title) {
        QueryWrapper<Share> queryWrapper = new QueryWrapper<>();
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
        queryWrapper.select(Share.class, info -> !info.getColumn().equals("approver")
                && !info.getColumn().equals("approve_time"));
        return baseMapper.selectPage(page, queryWrapper);
    }

    @Override
    public Share getShareByFile(String file) {
        QueryWrapper<Share> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("file_name", file);
        return baseMapper.selectOne(queryWrapper);
    }

    @Override
    public IPage<Share> getAllShareBySearch(Page<Share> page, String title, String unapproved) {
        QueryWrapper<Share> queryWrapper = new QueryWrapper<>();
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
