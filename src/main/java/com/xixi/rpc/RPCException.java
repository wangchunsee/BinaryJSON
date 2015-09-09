package com.xixi.rpc;

/**
 * Created by wangchunsee on 15/8/28.
 */
public class RPCException extends Exception {

    private int errorCode;

    public RPCException(int errorCode,String message){
        super(message);
        this.errorCode=errorCode;
    }

    public int getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(int errorCode) {
        this.errorCode = errorCode;
    }
}
