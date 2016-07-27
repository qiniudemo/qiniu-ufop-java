package com.qiniu.ufop;

import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

public class UfopConfig {
    private String ufopPrefix;

    public static UfopConfig load(String cfgFilePath) throws IOException {
        UfopConfig config = null;
        BufferedReader bReader = new BufferedReader(new InputStreamReader(new FileInputStream(cfgFilePath)));
        config = new Gson().fromJson(bReader, UfopConfig.class);
        bReader.close();
        return config;
    }

    public String getUfopPrefix() {
        return ufopPrefix;
    }

    public void setUfopPrefix(String ufopPrefix) {
        this.ufopPrefix = ufopPrefix;
    }
}