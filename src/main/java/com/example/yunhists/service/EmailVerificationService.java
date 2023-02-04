package com.example.yunhists.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.yunhists.entity.EmailVerification;

public interface EmailVerificationService extends IService<EmailVerification> {

    // Create
    int create(EmailVerification emailVerification);

    // Read
    EmailVerification read(String email);

    // Delete
    int delete(Integer id);
}
