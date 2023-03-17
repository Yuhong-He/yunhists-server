package com.example.yunhists.controller;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.TypeReference;
import com.example.yunhists.enumeration.ResultCodeEnum;
import com.example.yunhists.common.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ResourceLoader;
import org.springframework.util.StreamUtils;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.Map;

@CrossOrigin
@RestController
@Slf4j
@RequestMapping("/api/statistics")
public class StatisticsController {

    private final ResourceLoader resourceLoader;

    public StatisticsController(ResourceLoader resourceLoader) {
        this.resourceLoader = resourceLoader;
    }

    @GetMapping("")
    public Result<Object> get() {
        try {
            String generalJson = StreamUtils.copyToString(resourceLoader.getResource("classpath:/statistics/general.json").getInputStream(), StandardCharsets.UTF_8);
            LinkedHashMap<String, Integer> general = JSON.parseObject(generalJson, new TypeReference<LinkedHashMap<String, Integer>>() {});

            String copyrightJson = StreamUtils.copyToString(resourceLoader.getResource("classpath:/statistics/copyright.json").getInputStream(), StandardCharsets.UTF_8);
            LinkedHashMap<String, Integer> copyright = JSON.parseObject(copyrightJson, new TypeReference<LinkedHashMap<String, Integer>>() {});

            String thesisTypeJson = StreamUtils.copyToString(resourceLoader.getResource("classpath:/statistics/thesisType.json").getInputStream(), StandardCharsets.UTF_8);
            LinkedHashMap<String, Integer> thesisType = JSON.parseObject(thesisTypeJson, new TypeReference<LinkedHashMap<String, Integer>>() {});

            String yearJson = StreamUtils.copyToString(resourceLoader.getResource("classpath:/statistics/year.json").getInputStream(), StandardCharsets.UTF_8);
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
