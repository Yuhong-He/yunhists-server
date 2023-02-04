package com.example.yunhists.utils;

import com.example.yunhists.entity.EmailVerification;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import static java.lang.Thread.sleep;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class EVUtilsTest {

    @Test
    public void createVerification_email_success() {
        EmailVerification ev = EVUtils.createVerification("test@gmail.com");
        assertEquals("test@gmail.com", ev.getEmail());
        assertEquals(6, ev.getVerificationCode().length());
    }

    @Test
    public void isExpiration_expiredTime_true() throws ParseException {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date parsedDate = format.parse("2023-02-03 23:30:55");
        Timestamp timestamp = new Timestamp(parsedDate.getTime());
        EmailVerification ev = mock(EmailVerification.class);
        when(ev.getTimestamp()).thenReturn(timestamp);
        assertTrue(EVUtils.isExpiration(ev));
    }

    @Test
    public void isExpiration_validTime_false() throws InterruptedException {
        Date date = new Date();
        sleep(1000);
        Timestamp timestamp = new Timestamp(date.getTime());
        EmailVerification ev = mock(EmailVerification.class);
        when(ev.getTimestamp()).thenReturn(timestamp);
        assertFalse(EVUtils.isExpiration(ev));
    }

    @Test
    public void compareVerification_codeMatches_true() {
        String verificationCode = "114514";
        EmailVerification ev = mock(EmailVerification.class);
        when(ev.getVerificationCode()).thenReturn(verificationCode);
        assertTrue(EVUtils.compareVerification(verificationCode, ev));
    }

}
