package com.example.yunhists.task;

import com.example.yunhists.entity.DelThesis;
import com.example.yunhists.entity.Upload;
import com.example.yunhists.entity.Thesis;
import com.example.yunhists.service.DelThesisService;
import com.example.yunhists.service.UploadService;
import com.example.yunhists.service.ThesisService;
import com.example.yunhists.utils.OSSUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Slf4j
public class ThesisTask {

    @Autowired
    ThesisService thesisService;

    @Autowired
    DelThesisService delThesisService;

    @Autowired
    UploadService uploadService;

    @Scheduled(cron ="0 0 0 * * ?")
    public void checkThesisFileDatabaseRecords() {

        log.info("---------Start checkThesisFileDatabaseRecords()---------");

        List<String> fileList = OSSUtils.getAllFile();
        List<Thesis> thesisList = thesisService.getAll();
        for(Thesis thesis : thesisList) {
            if(!thesis.getFileName().isEmpty()) {
                assert fileList != null;
                if(!fileList.contains(thesis.getFileName())) {
                    log.info("Thesis " + thesis.getTitle() + " missing file: " + thesis.getFileName());
                    thesis.setFileName("");
                    thesisService.saveOrUpdate(thesis);
                }
            }
        }

        log.info("---------End checkThesisFileDatabaseRecords()---------");

    }

    @Scheduled(cron ="0 0 0 * * ?")
    public void checkThesisFileOSS() {

        log.info("---------Start checkThesisFileOSS()---------");

        List<String> fileList = OSSUtils.getAllFile();
        assert fileList != null;
        for(String file : fileList) {
            if(file.startsWith("default/")) {
                Thesis thesis = thesisService.getThesisByFile(file);
                if(thesis == null) {
                    log.info("OSS file " + file + " not recorded in database");
                    OSSUtils.deleteFile(file);
                }
            } else if(file.startsWith("deleted/")) {
                DelThesis delThesis = delThesisService.getThesisByFile(file);
                if(delThesis == null) {
                    log.info("OSS file " + file + " not recorded in database");
                    OSSUtils.deleteFile(file);
                }
            } else if(file.startsWith("temp/")) {
                Upload upload = uploadService.getUploadByFile(file);
                if(upload == null) {
                    log.info("OSS file " + file + " not recorded in database");
                    OSSUtils.deleteFile(file);
                }
            }
        }

        log.info("---------End checkThesisFileOSS()---------");

    }

}
