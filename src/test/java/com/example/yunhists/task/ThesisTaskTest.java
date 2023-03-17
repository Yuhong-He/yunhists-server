package com.example.yunhists.task;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class ThesisTaskTest {

    @Autowired
    private ThesisTask thesisTask;

    @Test
    public void testCheckThesisFileDatabaseRecords() {
        thesisTask.checkThesisFileDatabaseRecords();
    }

    @Test
    public void testCheckThesisFileOSS() {
        thesisTask.checkThesisFileOSS();
    }

}
