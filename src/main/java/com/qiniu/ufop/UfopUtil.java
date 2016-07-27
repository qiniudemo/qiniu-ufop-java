package com.qiniu.ufop;

import com.google.gson.Gson;
import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import org.apache.commons.codec.binary.Hex;

import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Created by jemy on 7/27/16.
 */
public class UfopUtil {
    public static void respError(HttpExchange exchange, String errMsg) throws IOException {
        Headers headers = exchange.getResponseHeaders();
        headers.add("Content-Type", "application/json");
        UfopError ufopErr = new UfopError();
        ufopErr.setError(errMsg);
        String respStr = new Gson().toJson(ufopErr, UfopError.class);
        byte[] data = respStr.getBytes("utf-8");
        exchange.sendResponseHeaders(400, data.length);
        OutputStream output = exchange.getResponseBody();
        output.write(data);
        output.flush();
        output.close();
        exchange.close();
    }

    public static void respJson(HttpExchange exchange, String jsonBody) throws IOException {
        Headers headers = exchange.getResponseHeaders();
        headers.add("Content-Type", "application/json");
        byte[] data = jsonBody.getBytes("utf-8");
        exchange.sendResponseHeaders(200, data.length);
        OutputStream output = exchange.getResponseBody();
        output.write(data);
        output.flush();
        output.close();
        exchange.close();
    }

    public static String md5ToLower(String src) throws UnsupportedEncodingException, NoSuchAlgorithmException {
        MessageDigest digest = MessageDigest.getInstance("MD5");
        digest.update(src.getBytes("utf-8"));
        byte[] md5Bytes = digest.digest();
        return Hex.encodeHexString(md5Bytes);
    }

    public static String md5ToLower(byte[] data) throws UnsupportedEncodingException, NoSuchAlgorithmException {
        MessageDigest digest = MessageDigest.getInstance("MD5");
        digest.update(data);
        byte[] md5Bytes = digest.digest();
        return Hex.encodeHexString(md5Bytes);
    }
}
