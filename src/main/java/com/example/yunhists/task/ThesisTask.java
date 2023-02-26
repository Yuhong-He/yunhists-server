package com.example.yunhists.task;

import com.example.yunhists.entity.DelThesis;
import com.example.yunhists.entity.Thesis;
import com.example.yunhists.service.DelThesisService;
import com.example.yunhists.service.ThesisService;
import com.example.yunhists.utils.OSSUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;

@Component
public class ThesisTask {

    @Autowired
    ThesisService thesisService;

    @Autowired
    DelThesisService delThesisService;

    @Scheduled(cron ="0 0 0 * * ?")
    public void checkThesisFileDatabaseRecords() throws IOException {
        List<Thesis> thesisList = thesisService.getThesisWithFileNotNull();
        for(Thesis thesis : thesisList) {
            if(!OSSUtils.checkFileExist(thesis.getFileName())) {
                thesis.setFileName("");
                thesisService.saveOrUpdate(thesis);
            }
        }
    }

    @Scheduled(cron ="0 0 0 * * mon")
    public void checkThesisFileOSS() throws IOException {
        List<String> fileList = OSSUtils.getAllFile();
        for(String file : fileList) {
            if(file.startsWith("default/")) {
                String fileInDB = "/" + file;
                Thesis thesis = thesisService.getThesisByFile(fileInDB);
                if(thesis == null) {
                    OSSUtils.deleteFile(file);
                }
            } else if(file.startsWith("deleted/")) {
                DelThesis delThesis = delThesisService.getThesisByFile(file);
                if(delThesis == null) {
                    OSSUtils.deleteFile(file);
                }
            }
        }
    }

}
