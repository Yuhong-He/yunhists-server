package com.example.yunhists.controller;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.TypeReference;
import com.example.yunhists.enumeration.ResultCodeEnum;
import com.example.yunhists.task.StatisticsTask;
import com.example.yunhists.common.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.LinkedHashMap;
import java.util.Map;

@CrossOrigin
@RestController
@Slf4j
@RequestMapping("/api/statistics")
public class StatisticsController {

    @GetMapping("")
    public Result<Object> get() {
        try {
            String generalJson = new String(Files.readAllBytes(Paths.get(StatisticsTask.filePath + "general.json")));
            LinkedHashMap<String, Integer> general = JSON.parseObject(generalJson, new TypeReference<LinkedHashMap<String, Integer>>() {});

            String copyrightJson = new String(Files.readAllBytes(Paths.get(StatisticsTask.filePath + "copyright.json")));
            LinkedHashMap<String, Integer> copyright = JSON.parseObject(copyrightJson, new TypeReference<LinkedHashMap<String, Integer>>() {});

            String thesisTypeJson = new String(Files.readAllBytes(Paths.get(StatisticsTask.filePath + "thesisType.json")));
            LinkedHashMap<String, Integer> thesisType = JSON.parseObject(thesisTypeJson, new TypeReference<LinkedHashMap<String, Integer>>() {});

            String yearJson = new String(Files.readAllBytes(Paths.get(StatisticsTask.filePath + "year.json")));
            LinkedHashMap<String, Integer> year = JSON.parseObject(yearJson, new TypeReference<LinkedHashMap<String, Integer>>() {});

            Map<String, Object> map = new LinkedHashMap<>();
            map.put("general", general);
            map.put("copyright", copyright);
            map.put("thesisType", thesisType);
            map.put("year", year);
            return Result.ok(map);
        } catch (IOException e) {
            log.error(e.getMessage());
            return Result.error(ResultCodeEnum.FAIL);
        }
    }

}
