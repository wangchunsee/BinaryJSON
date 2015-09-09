package com.xixi.rpc;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by wangchunsee on 15/8/27.
 */
public class ServiceBag {
    private String serviceName;//服务名称
    private String funName;//方法名称
    private List params ;//参数

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public String getFunName() {
        return funName;
    }

    public void setFunName(String funName) {
        this.funName = funName;
    }

    public List getParams() {
        return params;
    }

    public void setParams(List params) {
        this.params = params;
    }
}
