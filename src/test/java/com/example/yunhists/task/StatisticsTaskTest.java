package com.example.yunhists.task;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;

@RunWith(SpringRunner.class)
@SpringBootTest
public class StatisticsTaskTest {

    @Autowired
    private StatisticsTask statisticsTask;

    @Test
    public void testGenerateGeneralStatistics() throws IOException {
        statisticsTask.generateGeneralStatistics();
    }

    @Test
    public void testGenerateThesisCopyrightStatistics() throws IOException {
        statisticsTask.generateThesisCopyrightStatistics();
    }

    @Test
    public void testGenerateThesisYearStatistics() throws IOException {
        statisticsTask.generateThesisYearStatistics();
    }

}
