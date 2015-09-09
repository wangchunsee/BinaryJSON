package com.xixi.rpc.proxy;

import com.xixi.bjson.BjsonDeserialize;
import com.xixi.bjson.BjsonSerialize;
import com.xixi.bjson.KeyMap;
import com.xixi.bjson.KeyMapFactory;
import com.xixi.rpc.FastCopyObject;
import com.xixi.rpc.RPCException;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by wangchunsee on 15/8/28.
 */
public class ProxyBase {
    public static final String SERVICE_KEY = "service";
    public static final String FUNCTION_KEY = "function";
    public static final String PARAM_KEY = "param";


    public static final String ERROR_KEY = "error";
    public static final String MESSAGE_KEY = "message";
    public static final String RESULT_KEY = "result";

    TransmitChannel channel;

    public ProxyBase(TransmitChannel channel){
        this.channel=channel;
    }

    public Object invokeRemoteService(String service,String function,Map<String,Object> params,Class retClass) throws RPCException{
        Map<String,Object> bag = new HashMap<String, Object>();
        bag.put(SERVICE_KEY,service);
        bag.put(FUNCTION_KEY,function);
        bag.put(PARAM_KEY,params);
        BjsonSerialize serialize = new BjsonSerialize(KeyMapFactory.mutableKeyMap());
        try {
            byte[] request = serialize.serialize(bag, true);
            byte[] response = channel.sendTo(request);
            BjsonDeserialize deserialize = new BjsonDeserialize();
            Map<String,Object> result = (Map<String,Object>)deserialize.deserialize(response);
            int error = (Integer)result.get(ERROR_KEY);
            String msg = (String)result.get(MESSAGE_KEY);
            Object obj=retClass.newInstance();
//            FastCopyObject.initObject();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return null;
    }
}
