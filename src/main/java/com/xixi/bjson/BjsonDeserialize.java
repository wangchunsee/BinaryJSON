package com.xixi.bjson;

import java.util.*;

/**
 * Created by wangchunsee on 15/8/25.
 */
public class BjsonDeserialize {

    public KeyMap keyMap;
    public Object deserialize(byte[] data){
        IndexPath indexPath=new IndexPath(1);
        if( (data[0] & 128) == 0 ){
            int groupId = data[0]&63;
            if ((data[0]&64)>0) {
                int len= readUInt(data, indexPath);
                groupId+=len;
            }
            this.keyMap = KeyMapFactory.keyMapFromGroup(groupId);
            return deserializeContent(data,indexPath);
        }else {
            int len = data[0]&63;
            if ((data[0]&64)>0){
                len+= readUInt(data, indexPath);
            }
            String str = new String(data,indexPath.index,len);
            indexPath.index+=len;
            this.keyMap = KeyMapFactory.keyMapFromString(str);
            return deserializeContent(data,indexPath);
        }
    }

    public Object deserializeContent(byte[] data,IndexPath indexPath){
        int type = data[indexPath.index]&192;
        Object ret = null;
        switch (type){
            case 0: {//Map
                ret = deserializeMap(data,indexPath);
            }break;
            case 64: {//Array
                ret = deserializeArray(data,indexPath);
            }break;
            case 128: {//字符串或二进制
                if ((data[indexPath.index]&32)>0){
                    ret = deserializeString(data,indexPath);
                }else {
                    ret = deserializeByteArray(data,indexPath);
                }
            }break;
            case 192: {//数字
                ret = deserializeNumber(data,indexPath);
            }break;
            default:
                break;
        }
        return ret;
    }

    public Map<String,Object> deserializeMap(byte[] data,IndexPath indexPath){
        int len = data[indexPath.index]&31;
        if ((data[indexPath.index]&32)>0) {
            indexPath.index+=1 ;
            len+= readUInt(data, indexPath);
        }else {
            indexPath.index+=1 ;
        }
        Map<String,Object> ret=new HashMap<String, Object>(len);
        for (int i=0;i<len;i++){
            int key = readUInt(data, indexPath);
            Object content = deserializeContent(data,indexPath);
            String name = this.keyMap.nameForKey(key);
            if (name==null){
                continue;
            }
            ret.put(name,content);
        }
        return ret;
    }

    public int readUInt(byte[] data, IndexPath indexPath){
        int len = 0;
        int cur = indexPath.index;
        while (true){
            len += data[cur]&127;
            cur++;
            if((data[cur-1]&128)==0){
                break;
            }
        }
        indexPath.index=cur;
        return len;
    }



    public List deserializeArray(byte[] data,IndexPath indexPath){
        int len = data[indexPath.index]&31;
        if ((data[indexPath.index]&32)>0) {
            indexPath.index+=1 ;
            len+= readUInt(data, indexPath);
        }else {
            indexPath.index+=1 ;
        }
        List<Object> ret=new ArrayList<Object>(len);
        for (int i=0;i<len;i++){
            Object content = deserializeContent(data, indexPath);
            ret.add(content);
        }
        return ret;
    }

    public String deserializeString(byte[] data,IndexPath indexPath){
        int len = data[indexPath.index]&15;
        if ((data[indexPath.index]&16)>0) {
            indexPath.index+=1 ;
            len+= readUInt(data, indexPath);
        }else {
            indexPath.index+=1 ;
        }
        String str = new String(data,indexPath.index,len);
        indexPath.index += len;
        return str;
    }

    public byte[] deserializeByteArray(byte[] data,IndexPath indexPath){
        int len = data[indexPath.index]&15;
        if ((data[indexPath.index]&16)>0) {
            indexPath.index+=1 ;
            len+= readUInt(data, indexPath);
        }else {
            indexPath.index+=1 ;
        }
        byte[] ret = new byte[len];
        System.arraycopy(data,indexPath.index,ret,0,len);
        indexPath.index += len;
        return ret;
    }

    public Double deserializeNumber(byte[] data,IndexPath indexPath){
        int len = data[indexPath.index]&31;

        if ((data[indexPath.index]&32)>0) {
            indexPath.index+=1 ;
            len+= readUInt(data, indexPath);
        }else {
            indexPath.index+=1 ;
        }
        String str = new String(data,indexPath.index,len);
        indexPath.index += len;
        return Double.parseDouble(str);
    }


    public static class IndexPath{
        public int index;

        public IndexPath(int index){
            this.index=index;
        }
    }
}
