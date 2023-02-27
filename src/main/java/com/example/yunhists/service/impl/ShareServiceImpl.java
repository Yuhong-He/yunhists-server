package com.example.yunhists.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.yunhists.entity.Share;
import com.example.yunhists.mapper.ShareMapper;
import com.example.yunhists.service.ShareService;
import org.springframework.stereotype.Service;

@Service("shareServiceImpl")
public class ShareServiceImpl extends ServiceImpl<ShareMapper, Share> implements ShareService {
}
