package com.example.yunhists.utils;

import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.aliyun.oss.model.ListObjectsRequest;
import com.aliyun.oss.model.OSSObjectSummary;
import com.aliyun.oss.model.ObjectListing;

import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Properties;

public class OSSUtils {

    static String endpoint = "oss-cn-hongkong.aliyuncs.com";
    static String bucketName = "yunhists";

    public static void moveFileToDeletedFolder(String sourceObject, String destinationObject) throws Exception {
        OSS ossClient = new OSSClientBuilder().build(endpoint, getAccessKey("id"), getAccessKey("secret"));
        ossClient.copyObject(bucketName, sourceObject, bucketName, destinationObject);
        ossClient.deleteObject(bucketName, sourceObject);
        ossClient.shutdown();
    }

    public static boolean checkFileExist(String objectName) throws IOException {
        OSS ossClient = new OSSClientBuilder().build(endpoint, getAccessKey("id"), getAccessKey("secret"));
        return ossClient.doesObjectExist(bucketName, objectName);
    }

    public static List<String> getAllFile() throws IOException {
        OSS ossClient = new OSSClientBuilder().build(endpoint, getAccessKey("id"), getAccessKey("secret"));
        ListObjectsRequest listObjectsRequest = new ListObjectsRequest(bucketName);
        ObjectListing listing = ossClient.listObjects(listObjectsRequest);
        List<String> fileList = new ArrayList<>();
        for (OSSObjectSummary objectSummary : listing.getObjectSummaries()) {
            fileList.add(objectSummary.getKey());
        }
        return fileList;
    }

    public static void deleteFile(String objectName) throws IOException {
        OSS ossClient = new OSSClientBuilder().build(endpoint, getAccessKey("id"), getAccessKey("secret"));
        ossClient.deleteObject(bucketName, objectName);
    }

    private static String getAccessKey(String s) throws IOException {
        Properties props = new Properties();
        InputStreamReader inputStreamReader = new InputStreamReader(
                Objects.requireNonNull(DirectMailUtils.class.getClassLoader().getResourceAsStream("securityKey.properties")),
                StandardCharsets.UTF_8);
        props.load(inputStreamReader);
        if(s.equals("id")) {
            return props.getProperty("aliyun.oss.accessKeyId");
        } else {
            return props.getProperty("aliyun.oss.secret");
        }
    }

}
