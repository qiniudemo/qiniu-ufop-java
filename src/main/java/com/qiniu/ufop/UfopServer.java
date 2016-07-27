package com.qiniu.ufop;

import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.net.InetSocketAddress;

@SuppressWarnings("restriction")
public class UfopServer {

    private HttpServer mServer;

    public UfopServer(UfopConfig ufopConfig) throws IOException {
        this.mServer = HttpServer.create(new InetSocketAddress(9100), 100);
        this.mServer.createContext("/uop", new UfopRequestHandler(ufopConfig));
        this.mServer.setExecutor(null);
    }

    public void listen() {
        this.mServer.start();
    }

}
