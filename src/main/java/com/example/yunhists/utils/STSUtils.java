package com.example.yunhists.utils;

import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.auth.sts.AssumeRoleRequest;
import com.aliyuncs.auth.sts.AssumeRoleResponse;
import com.aliyuncs.exceptions.ClientException;
import com.aliyuncs.http.MethodType;
import com.aliyuncs.profile.DefaultProfile;
import com.aliyuncs.profile.IClientProfile;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Properties;

@Slf4j
public class STSUtils {

    public static Map<String, String> getSTS(Integer userId) throws IOException {
        AssumeRoleResponse assumeRoleResponse = generateSTS(userId);
        Map<String, String> sts = new HashMap<>();
        if(assumeRoleResponse != null) {
            sts.put("accessKeyId", assumeRoleResponse.getCredentials().getAccessKeyId());
            sts.put("accessKeySecret", assumeRoleResponse.getCredentials().getAccessKeySecret());
            sts.put("stsToken", assumeRoleResponse.getCredentials().getSecurityToken());
        } else {
            sts.put("msg", "Alibaba Cloud STS Error");
        }
        return sts;
    }

    private static AssumeRoleResponse generateSTS(Integer userId) throws IOException {

        String endpoint = "sts.cn-hongkong.aliyuncs.com";
        String accessKeyId = getAccessKey("id");
        String accessKeySecret = getAccessKey("secret");
        String roleArn = getAccessKey("arn");
        String roleSessionName = "YunhistsUserId_" + userId;
        Long durationSeconds = 3600L; // max 1 hour

        try {
            String regionId = "cn-hongkong";
            DefaultProfile.addEndpoint(regionId, "Sts", endpoint);
            IClientProfile profile = DefaultProfile.getProfile(regionId, accessKeyId, accessKeySecret);
            DefaultAcsClient client = new DefaultAcsClient(profile);
            final AssumeRoleRequest request = new AssumeRoleRequest();
            request.setMethod(MethodType.POST);
            request.setRoleArn(roleArn);
            request.setRoleSessionName(roleSessionName);
            request.setPolicy(null); // get all rights of the role
            request.setDurationSeconds(durationSeconds);

            return client.getAcsResponse(request);

        } catch (ClientException e) {
            log.error("Aliyun STS error: " + e.getErrMsg());
            return null;
        }
    }

    private static String getAccessKey(String s) throws IOException {
        Properties props = new Properties();
        InputStreamReader inputStreamReader = new InputStreamReader(
                Objects.requireNonNull(DirectMailUtils.class.getClassLoader().getResourceAsStream("securityKey.properties")),
                StandardCharsets.UTF_8);
        props.load(inputStreamReader);
        if(s.equals("id")) {
            return props.getProperty("aliyun.sts.accessKeyId");
        } else if(s.equals("secret")) {
            return props.getProperty("aliyun.sts.secret");
        } else {
            return props.getProperty("aliyun.sts.arn");
        }
    }

}
