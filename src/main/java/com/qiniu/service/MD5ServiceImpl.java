package com.qiniu.service;


import com.google.gson.Gson;
import com.qiniu.ufop.UfopRequest;
import com.qiniu.ufop.UfopUtil;
import com.sun.net.httpserver.HttpExchange;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.URL;

/**
 * 针对小文件计算md5的服务
 */

class MD5Resp {
    private String md5;

    public String getMd5() {
        return md5;
    }

    public void setMd5(String md5) {
        this.md5 = md5;
    }
}

public class MD5ServiceImpl implements ServiceInterface {
    public String Name() {
        return "md5";
    }

    public void Do(UfopRequest ufopReq, HttpExchange exchange) {
        try {
            URL resUri = new URL(ufopReq.getSrc().getUrl());
            InputStream resInputStream = resUri.openStream();
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            int cnt = -1;
            byte[] buffer = new byte[1024];
            while ((cnt = resInputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, cnt);
            }
            resInputStream.close();
            outputStream.flush();

            //calc md5
            byte[] fileContent = outputStream.toByteArray();
            String md5Hash = UfopUtil.md5ToLower(fileContent);

            MD5Resp md5Resp = new MD5Resp();
            md5Resp.setMd5(md5Hash);
            String jsonBody = new Gson().toJson(md5Resp, MD5Resp.class);
            UfopUtil.respJson(exchange, jsonBody);

            System.out.println("md5(" + ufopReq.getSrc().getKey() + ") = " + md5Hash);
            outputStream.close();
        } catch (Exception ex) {
            try {
                UfopUtil.respError(exchange, ex.getMessage());
            } catch (Exception exp) {
                System.out.println("Exec ufop service error, " + exp.getMessage());
            }
        }
        return;
    }
}
