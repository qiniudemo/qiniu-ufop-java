package com.qiniu;

import com.qiniu.ufop.UfopConfig;
import com.qiniu.ufop.UfopServer;

public class MainEntry {

    public static void main(String[] args) {
        try {
            if (args.length != 1) {
                System.out.println("Usage: qufop qufop.conf");
                return;
            }
            UfopConfig config = UfopConfig.load(args[0]);
            System.out.println("Ufop Prefix: " + config.getUfopPrefix());
            UfopServer server = new UfopServer(config);
            System.out.println("Start to listen server ...");
            server.listen();
        } catch (Exception ex) {
            System.out.println("Start server error:" + ex.getMessage());
        }
    }

}
