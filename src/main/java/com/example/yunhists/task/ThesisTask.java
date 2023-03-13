package com.example.yunhists.task;

import com.example.yunhists.entity.DelThesis;
import com.example.yunhists.entity.Share;
import com.example.yunhists.entity.Thesis;
import com.example.yunhists.service.DelThesisService;
import com.example.yunhists.service.ShareService;
import com.example.yunhists.service.ThesisService;
import com.example.yunhists.utils.OSSUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ThesisTask {

    @Autowired
    ThesisService thesisService;

    @Autowired
    DelThesisService delThesisService;

    @Autowired
    ShareService shareService;

    @Scheduled(cron ="0 0 0 * * ?")
    public void checkThesisFileDatabaseRecords() {
        List<String> fileList = OSSUtils.getAllFile();
        List<Thesis> thesisList = thesisService.getAll();
        for(Thesis thesis : thesisList) {
            if(!thesis.getFileName().isEmpty()) {
                assert fileList != null;
                if(!fileList.contains(thesis.getFileName())) {
                    thesis.setFileName("");
                    thesisService.saveOrUpdate(thesis);
                }
            }
        }
    }

    @Scheduled(cron ="0 0 0 * * mon")
    public void checkThesisFileOSS() {
        List<String> fileList = OSSUtils.getAllFile();
        assert fileList != null;
        for(String file : fileList) {
            if(file.startsWith("default/")) {
                Thesis thesis = thesisService.getThesisByFile(file);
                if(thesis == null) {
                    OSSUtils.deleteFile(file);
                }
            } else if(file.startsWith("deleted/")) {
                DelThesis delThesis = delThesisService.getThesisByFile(file);
                if(delThesis == null) {
                    OSSUtils.deleteFile(file);
                }
            } else if(file.startsWith("temp/")) {
                Share share = shareService.getShareByFile(file);
                if(share == null) {
                    OSSUtils.deleteFile(file);
                }
            }
        }
    }

}
