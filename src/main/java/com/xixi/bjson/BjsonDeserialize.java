package com.xixi.bjson;

import java.util.*;
import java.util.concurrent.SynchronousQueue;

/**
 * Created by wangchunsee on 15/8/25.
 */
public class BjsonDeserialize {

    public KeyMap keyMap;
    public Object deserialize(byte[] data){
        IndexPath indexPath=new IndexPath(0);
        if( (data[0] & 128) == 0 ){
            int groupId = readUInt(data, indexPath,7);
            this.keyMap = KeyMapFactory.keyMapFromGroup(groupId);
            return deserializeContent(data,indexPath);
        }else {
            int len = readUInt(data, indexPath,7);
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
        int size = readUInt(data, indexPath,6);
        Map<String,Object> ret=new HashMap<String, Object>(size);
        for (int i=0;i<size;i++){
            int key = readUInt(data, indexPath,8);
            Object content = deserializeContent(data,indexPath);
            String name = this.keyMap.nameForKey(key);
            if (name==null){
                continue;
            }
            ret.put(name,content);
        }
        return ret;
    }

    public int readUInt(byte[] data, IndexPath indexPath,int firstByteLen){
        int cur = indexPath.index;
        int len = (byte) (data[cur] & (0xFF >>> (8-firstByteLen+1)));
        if ((data[cur] & (1<<(firstByteLen-1))) == 0){
            cur++;
        }else {
            cur++;
            len = (len << 7);
            while (true){
                len = len | (data[cur] & 0x7F) ;
                cur++;
                if((data[cur-1]&128)==0){
                    break;
                }
                len=(len<<7);
            }
        }
        indexPath.index=cur;
        return len;

    }



    public List deserializeArray(byte[] data,IndexPath indexPath){
        int len = readUInt(data, indexPath,6);
        List<Object> ret=new ArrayList<Object>(len);
        for (int i=0;i<len;i++){
            Object content = deserializeContent(data, indexPath);
            ret.add(content);
        }
        return ret;
    }

    public String deserializeString(byte[] data,IndexPath indexPath){
        int len = readUInt(data, indexPath,5);
        String str = new String(data,indexPath.index,len);
        indexPath.index += len;
        return str;
    }

    public byte[] deserializeByteArray(byte[] data,IndexPath indexPath){
        int len = readUInt(data, indexPath,5);
        byte[] ret = new byte[len];
        System.arraycopy(data,indexPath.index,ret,0,len);
        indexPath.index += len;
        return ret;
    }

    public Double deserializeNumber(byte[] data,IndexPath indexPath){
        int len = readUInt(data, indexPath,6);
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
