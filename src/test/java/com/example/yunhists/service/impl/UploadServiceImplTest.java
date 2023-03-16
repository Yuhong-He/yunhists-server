package com.example.yunhists.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.yunhists.entity.Upload;
import com.example.yunhists.service.UploadService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

@SpringBootTest
@ExtendWith(SpringExtension.class)
public class UploadServiceImplTest {

    @Autowired
    UploadService uploadService;

    @Test
    public void getUploadBySearch() {
        Page<Upload> page = new Page<>(1, 5);
        assertNotNull(uploadService.getUploadBySearch(page, 1, "a b c"));
    }

    @Test
    public void getUploadByFile() {
        assertNull(uploadService.getUploadByFile("abc"));
    }

    @Test
    public void getAllUploadBySearch() {
        Page<Upload> page = new Page<>(1, 5);
        assertNotNull(uploadService.getAllUploadBySearch(page, "a b c", "ON"));
    }

}
