package com.xixi.rpc;

import com.google.gson.Gson;
import com.xixi.bjson.BjsonDeserialize;
import com.xixi.bjson.BjsonSerialize;
import com.xixi.bjson.KeyMapFactory;

import javax.print.attribute.standard.PrinterURI;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.*;

/**
 * Created by wangchunsee on 15/8/27.
 */
public class ServiceDispatcher {
    public static final String SERVICE_KEY = "service";
    public static final String FUNCTION_KEY = "function";
    public static final String PARAM_KEY = "param";


    public static final String ERROR_KEY = "error";
    public static final String MESSAGE_KEY = "message";
    public static final String RESULT_KEY = "result";

    private Map<String,Object> serviceMap = new HashMap<String, Object>();

    public byte[] dispatchService(byte[] content) {
        try {
            Object ret=dispatch(content);
            Map<String,Object> resp = new HashMap<String, Object>(3);
            resp.put(ERROR_KEY,0);
            if (ret!=null){
                resp.put(RESULT_KEY,FastCopyObject.attr2Map(ret));
            }
            return new BjsonSerialize(KeyMapFactory.mutableKeyMap()).serialize(resp,true);
        } catch (Throwable e) {
            Map<String,Object> resp = new HashMap<String, Object>(3);
            if (e instanceof RPCException){
                resp.put(ERROR_KEY,((RPCException)e).getErrorCode());
                resp.put(MESSAGE_KEY,e.getMessage());
            }else {
                resp.put(ERROR_KEY,-10000);
                resp.put(MESSAGE_KEY,e.getMessage());
            }
            try {
                return new BjsonSerialize(KeyMapFactory.mutableKeyMap()).serialize(resp,true);
            } catch (IOException e1) {
                return null;
            }
        }

    }

    private Object dispatch(byte[] content) throws Exception {
        BjsonDeserialize deserialize = new BjsonDeserialize();
        Object obj = deserialize.deserialize(content);
        if (!(obj instanceof Map)){
            throw new Exception("params error");
        }
        Map<String,Object> serviceBag = (Map<String,Object>)obj;
        String sname = (String)serviceBag.get(SERVICE_KEY);
        String fname = (String)serviceBag.get(FUNCTION_KEY);
        Object service = serviceMap.get(sname);
        if (service==null){
            throw new Exception("service not find");
        }
        Method[] methods = service.getClass().getMethods();
        List paramsSrc=(List)serviceBag.get(PARAM_KEY);
        Method selMethod = null;
        for (Method m : methods){
            if (m.getName().equals(fname)&&m.getParameterTypes().length==paramsSrc.size()){
                selMethod=m;
                break;
            }
        }
        Class<?>[] ps=selMethod.getParameterTypes();
        Object resp = null;
        if (ps.length>0){
            Object[] params=new Object[ps.length];
            for (int i=0;i<ps.length;i++){
                Object o=ps[i].newInstance();
                FastCopyObject.initObject((Map)paramsSrc.get(i),o);
                params[i]=o;
            }
            resp = selMethod.invoke(service,params);
        }else {
            resp = selMethod.invoke(service);
        }
        return FastCopyObject.attr2Map(resp);
    }

}
