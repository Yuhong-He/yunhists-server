package com.example.yunhists.utils;

import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.IAcsClient;
import com.aliyuncs.dm.model.v20151123.SingleSendMailRequest;
import com.aliyuncs.profile.DefaultProfile;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Objects;
import java.util.Properties;

@Slf4j
public class DirectMailUtils {

    public static void sendEmail(String email, String subject, String emailBody) {
        try {
            DefaultProfile profile = DefaultProfile.getProfile("ap-southeast-1", getAccessKey("id"), getAccessKey("secret"));
            DefaultProfile.addEndpoint("ap-southeast-1", "Dm",  "dm.ap-southeast-1.aliyuncs.com");
            IAcsClient client = new DefaultAcsClient(profile);

            SingleSendMailRequest request = new SingleSendMailRequest();
            request.setRegionId("ap-southeast-1");
            request.setAccountName("no_reply@email.yunnanhistory.com");
            request.setFromAlias("滇史论辑 Yunhists");
            request.setAddressType(1);
            request.setReplyToAddress(true);
            request.setToAddress(email);
            request.setSubject(subject);
            request.setHtmlBody(emailBody);
            client.getAcsResponse(request);
        } catch (Exception e) {
            log.error("Email send fail: " + e.getMessage());
        }
    }

    private static String getAccessKey(String s) throws IOException {
        Properties props = new Properties();
        InputStreamReader inputStreamReader = new InputStreamReader(
                Objects.requireNonNull(DirectMailUtils.class.getClassLoader().getResourceAsStream("securityKey.properties")),
                StandardCharsets.UTF_8);
        props.load(inputStreamReader);
        if(s.equals("id")) {
            return props.getProperty("aliyun.directMail.accessKeyId");
        } else {
            return props.getProperty("aliyun.directMail.secret");
        }
    }

}
