package com.qiniu.ufop;

public class UfopRequest {
    private String cmd;
    private UfopSrcInfo src;

    public String getCmd() {
        return cmd;
    }

    public void setCmd(String cmd) {
        this.cmd = cmd;
    }

    public UfopSrcInfo getSrc() {
        return src;
    }

    public void setSrc(UfopSrcInfo src) {
        this.src = src;
    }

}
