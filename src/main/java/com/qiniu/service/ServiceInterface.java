package com.qiniu.service;


import com.qiniu.ufop.UfopRequest;
import com.sun.net.httpserver.HttpExchange;

/**
 * Created by jemy on 7/27/16.
 */
public interface ServiceInterface {
    String Name();

    void Do(UfopRequest ufopReq, HttpExchange exchange);
}
