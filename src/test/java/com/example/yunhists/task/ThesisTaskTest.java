package com.example.yunhists.task;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;

@RunWith(SpringRunner.class)
@SpringBootTest
public class ThesisTaskTest {

    @Autowired
    private ThesisTask thesisTask;

    @Test
    public void testCheckThesisFileDatabaseRecords() throws IOException {
        thesisTask.checkThesisFileDatabaseRecords();
    }

    @Test
    public void testCheckThesisFileOSS() throws IOException {
        thesisTask.checkThesisFileOSS();
    }

}
