package com.xixi.rpc.proxy;

import java.io.IOException;

/**
 * Created by wangchunsee on 15/8/31.
 */
public interface TransmitChannel {
    byte[] sendTo(byte[] request) throws IOException;
}
