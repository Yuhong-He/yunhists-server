package com.example.yunhists.utils;

import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.aliyun.oss.model.ListObjectsRequest;
import com.aliyun.oss.model.OSSObjectSummary;
import com.aliyun.oss.model.ObjectListing;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Properties;

@Slf4j
public class OSSUtils {

    static String endpoint = "oss-cn-hongkong.aliyuncs.com";
    static String bucketName = "yunhists";

    public static void moveFile(String sourceObject, String destinationObject) {
        try {
            OSS ossClient = new OSSClientBuilder().build(endpoint, getAccessKey("id"), getAccessKey("secret"));
            ossClient.copyObject(bucketName, sourceObject, bucketName, destinationObject);
            ossClient.deleteObject(bucketName, sourceObject);
            ossClient.shutdown();
        } catch (Exception e) {
            log.error("OSS move file error: " + e);
        }
    }

    public static List<String> getAllFile() {
        try {
            OSS ossClient = new OSSClientBuilder().build(endpoint, getAccessKey("id"), getAccessKey("secret"));
            ListObjectsRequest listObjectsRequest = new ListObjectsRequest(bucketName);
            ObjectListing listing = ossClient.listObjects(listObjectsRequest);
            List<String> fileList = new ArrayList<>();
            for (OSSObjectSummary objectSummary : listing.getObjectSummaries()) {
                fileList.add(objectSummary.getKey());
            }
            return fileList;
        } catch (Exception e) {
            log.error("OSS move file error: " + e);
            return null;
        }
    }

    public static void deleteFile(String objectName) {
        try {
            OSS ossClient = new OSSClientBuilder().build(endpoint, getAccessKey("id"), getAccessKey("secret"));
            ossClient.deleteObject(bucketName, objectName);
        } catch (Exception e) {
            log.error("OSS move file error: " + e);
        }
    }

    private static String getAccessKey(String s) throws IOException {
        Properties props = new Properties();
        InputStreamReader inputStreamReader = new InputStreamReader(
                Objects.requireNonNull(OSSUtils.class.getClassLoader().getResourceAsStream("securityKey.properties")),
                StandardCharsets.UTF_8);
        props.load(inputStreamReader);
        if(s.equals("id")) {
            return props.getProperty("aliyun.oss.accessKeyId");
        } else {
            return props.getProperty("aliyun.oss.secret");
        }
    }

}
