package com.example.yunhists.service.impl;

import com.example.yunhists.entity.EmailTimer;
import com.example.yunhists.mapper.EmailTimerMapper;
import com.example.yunhists.service.EmailTimerService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;

@SpringBootTest
@ExtendWith(SpringExtension.class)
public class EmailTimerServiceImplTest {

    @Autowired
    EmailTimerService emailTimerService;

    @Mock
    private EmailTimerMapper emailTimerMapper;

    @InjectMocks
    private EmailTimerServiceImpl emailTimerService2;

    @Test
    public void createTest() {
        MockitoAnnotations.openMocks(this);
        EmailTimer emailTimer = new EmailTimer();
        emailTimer.setEmail("test@test.com");
        when(emailTimerMapper.insert(emailTimer)).thenReturn(1);
        int result = emailTimerService2.create(emailTimer);
        verify(emailTimerMapper, times(1)).insert(emailTimer);
        assertEquals(1, result);
    }

    @Test
    public void read() {
        assertNull(emailTimerService.read("aaa", "bbb"));
    }

    @Test
    public void delete() {
        int row = emailTimerService.delete(-1);
        assertEquals(0, row);
    }

}
