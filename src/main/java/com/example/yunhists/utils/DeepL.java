package com.example.yunhists.utils;

import com.deepl.api.TextResult;
import com.deepl.api.Translator;

import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Objects;
import java.util.Properties;

public class DeepL {

    static Translator translator;

    public static String translateToZh(String str) {
        try {
            translator = new Translator(getAccessKey());
            TextResult result = translator.translateText(str, null, "zh");
            return result.getText();
        } catch (Exception e) {
            ControllerUtils.printException(e);
        }
        return "DeepL Translation ERROR";
    }

    public static String translateToEn(String str) {
        try {
            translator = new Translator(getAccessKey());
            TextResult result = translator.translateText(str, null, "en-GB");
            return result.getText();
        } catch (Exception e) {
            ControllerUtils.printException(e);
        }
        return "DeepL Translation ERROR";
    }

    private static String getAccessKey() throws IOException {
        Properties props = new Properties();
        InputStreamReader inputStreamReader = new InputStreamReader(
                Objects.requireNonNull(DeepL.class.getClassLoader().getResourceAsStream("securityKey.properties")),
                StandardCharsets.UTF_8);
        props.load(inputStreamReader);
        return props.getProperty("deepl.key");
    }
}
