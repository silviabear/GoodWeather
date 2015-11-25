package edu.illinois.cs425_mp1.types;

public enum Command {

    GREP("grep"),
    SHUTDOWN("shutdown"),
    PUT("put"),
    PUTBACK("done"),
    GET("get"),
    GETBACK("done"),
    DELETE("delete"),
    ERROR("error"),
    QUERY("query"),
    QUERYBACK("done"),
    REPLICATE("replicate");

    String cmd;

    Command(String stringcmd) {
        this.cmd = stringcmd;
    }

    @Override
    public String toString() {
        return this.cmd;
    }

}
