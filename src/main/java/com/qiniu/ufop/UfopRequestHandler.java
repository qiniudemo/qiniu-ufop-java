package com.qiniu.ufop;

import com.google.gson.Gson;
import com.qiniu.service.MD5ServiceImpl;
import com.qiniu.service.ServiceInterface;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

public class UfopRequestHandler implements HttpHandler {
    private MD5ServiceImpl md5Service;
    private Map<String, ServiceInterface> ufopServices;
    private UfopConfig ufopConfig;

    public UfopRequestHandler(UfopConfig ufopConfig) {
        this.md5Service = new MD5ServiceImpl();
        this.ufopServices = new HashMap<String, ServiceInterface>();
        this.ufopServices.put(this.md5Service.Name(), this.md5Service);
        this.ufopConfig = ufopConfig;
    }

    public void handle(HttpExchange exchange) throws IOException {
        String reqMethod = exchange.getRequestMethod();
        if (!reqMethod.equals("POST")) {
            UfopUtil.respError(exchange, "method not allowed");
            return;
        }
        InputStream inputStream = exchange.getRequestBody();
        InputStreamReader inputReader = new InputStreamReader(inputStream);
        char[] buffer = new char[1024];
        StringBuilder reqBody = new StringBuilder();
        int readCnt = -1;
        while ((readCnt = inputReader.read(buffer, 0, buffer.length)) != -1) {
            reqBody.append(new String(buffer, 0, readCnt));
        }
        inputReader.close();
        inputStream.close();

        // parse request boy
        try {
            UfopRequest ufopReq = new Gson().fromJson(reqBody.toString(), UfopRequest.class);
            // go ahead to handle the ufop command
            String[] ufopParams = ufopReq.getCmd().split("/");
            if (ufopParams.length == 0) {
                UfopUtil.respError(exchange, "invalid ufop cmd");
                return;
            }

            //ufop cmd with prefix
            String ufopCmdStr = ufopParams[0];
            String ufopCmdWithoutPrefix = ufopCmdStr.replaceFirst(this.ufopConfig.getUfopPrefix(), "");
            System.out.println("ufop cmd is: " + ufopCmdStr);
            ServiceInterface service = this.ufopServices.get(ufopCmdWithoutPrefix);
            if (service != null) {
                System.out.println("start to run service: " + ufopCmdWithoutPrefix);
                service.Do(ufopReq, exchange);
            } else {
                UfopUtil.respError(exchange, "no service found for request");
            }
        } catch (Exception e) {
            UfopUtil.respError(exchange, "parse request body error");
            return;
        }
    }


}
