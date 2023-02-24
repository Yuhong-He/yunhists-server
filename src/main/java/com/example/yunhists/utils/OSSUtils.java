package com.example.yunhists.utils;

import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;

import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
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
