package com.example.yunhists.utils;

import com.example.yunhists.entity.EmailTimer;
import com.example.yunhists.entity.EmailVerification;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import static java.lang.Thread.sleep;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class EmailTimerUtilsTest {

    @Test
    public void repeatEmail_lessThanOneMinute_true() throws InterruptedException {
        Date date = new Date();
        sleep(1000);
        Timestamp timestamp = new Timestamp(date.getTime());
        EmailTimer emailTimer = mock(EmailTimer.class);
        when(emailTimer.getTimestamp()).thenReturn(timestamp);
        assertTrue(EmailTimerUtils.repeatEmail(emailTimer));
    }

}
