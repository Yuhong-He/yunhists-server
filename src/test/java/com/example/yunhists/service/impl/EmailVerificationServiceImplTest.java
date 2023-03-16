package com.example.yunhists.service.impl;

import com.example.yunhists.entity.EmailVerification;
import com.example.yunhists.mapper.EmailVerificationMapper;
import com.example.yunhists.service.EmailVerificationService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.*;

@SpringBootTest
@ExtendWith(SpringExtension.class)
public class EmailVerificationServiceImplTest {

    @Autowired
    EmailVerificationService evService;

    @Mock
    private EmailVerificationMapper emailVerificationMapper;

    @InjectMocks
    private EmailVerificationServiceImpl evService2;

    @Test
    public void create() {
        MockitoAnnotations.openMocks(this);
        EmailVerification emailVerification = new EmailVerification();
        emailVerification.setEmail("test@test.com");
        when(emailVerificationMapper.insert(emailVerification)).thenReturn(1);
        int result = evService2.create(emailVerification);
        verify(emailVerificationMapper, times(1)).insert(emailVerification);
        assertEquals(1, result);
    }

    @Test
    public void read() {
        assertNull(evService.read("aaa"));
    }

    @Test
    public void delete() {
        int row = evService.delete(-1);
        assertEquals(0, row);
    }

}
