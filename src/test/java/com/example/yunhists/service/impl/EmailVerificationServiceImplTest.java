package com.example.yunhists.service.impl;

import com.example.yunhists.entity.EmailVerification;
import com.example.yunhists.service.EmailVerificationService;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Random;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@ExtendWith(SpringExtension.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class EmailVerificationServiceImplTest {

    @Autowired
    EmailVerificationService evService;

    static EmailVerification testEv = new EmailVerification("test@gmail.com", null);

    @Order(1)
    @Test
    public void create_validEV_success() {
        Random random = new Random();
        StringBuilder verificationCode = new StringBuilder();
        for (int i = 0; i < 6; i++) {
            verificationCode.append(random.nextInt(10));
        }
        testEv.setVerificationCode(String.valueOf(verificationCode));
        int result = evService.create(testEv);
        assertEquals(1, result);
    }

    @Order(2)
    @Test
    public void read_emailExist_validEV() {
        EmailVerification ev = evService.read(testEv.getEmail());
        assertEquals(ev.getVerificationCode(), testEv.getVerificationCode());
        testEv.setId(ev.getId());
    }

    @Order(3)
    @Test
    public void delete_emailExist_success() {
        int row = evService.delete(testEv.getId());
        assertEquals(1, row);
    }

}
