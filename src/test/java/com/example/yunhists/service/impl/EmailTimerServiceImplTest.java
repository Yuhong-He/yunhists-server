package com.example.yunhists.service.impl;

import com.example.yunhists.entity.EmailTimer;
import com.example.yunhists.service.EmailTimerService;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
@ExtendWith(SpringExtension.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class EmailTimerServiceImplTest {

    @Autowired
    EmailTimerService emailTimerService;

    static EmailTimer emailTimer = new EmailTimer("test@gmail.com", "resetPwd");

    @Order(1)
    @Test
    public void create_validEmailTimer_success() {
        int result = emailTimerService.create(emailTimer);
        assertEquals(1, result);
    }

    @Order(2)
    @Test
    public void read_emailAndActionExist_validEmailTimer() {
        EmailTimer et = emailTimerService.read(emailTimer.getEmail(), emailTimer.getAction());
        assertNotNull(et);
        emailTimer.setId(et.getId());
    }

    @Order(3)
    @Test
    public void delete_emailExist_success() {
        int row = emailTimerService.delete(emailTimer.getId());
        assertEquals(1, row);
    }

}
