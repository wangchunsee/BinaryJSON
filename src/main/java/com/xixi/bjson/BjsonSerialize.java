package com.xixi.bjson;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.*;

/**
 * Created by wangchunsee on 15/8/24.
 *
 * 第一个字节第一位:1 表示包含KeyMap,0表示不包含KeyMap
 * 包含KeyMap的情况: 1 + KeyMap长度 + KeyMap + 内容编码
 * 不包含KeyMap的情况: 0 + KeyMap的GroupID + 内容编码
 *
 * 具体类型编码规则:
 * object(map) : 第一个字节前2位 0b00+对象属性个数+对象属性编码(id+内容)
 * array : 第一个字节前2位 0b01 + 数组个数+ 数组编码
 * String : 第一个字节前3位 0b101+字符串长度+字符串的UTF8编码
 * byte[] : 第一个字节前3位 0b100+byte数组长度+数组
 * number : 第一个字节前2位 0b11+number转成字符串的长度+number
 *
 */
public class BjsonSerialize {
    public KeyMap keyMap;


    public BjsonSerialize(KeyMap keyMap){
        this.keyMap=keyMap;
    }

    /**
     * 序列化对象
     * @param in
     * @param withKeyMap 是否携带KeyMap
     * @return
     */

    public byte[] serialize(Object in,boolean withKeyMap) throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        byte[] content=serializeObject(in);
        if (withKeyMap) {
            byte[] key = keyMap.serializeKeyMap().getBytes("UTF-8");
            int tailLen = key.length - 63 ;
            byte[] ret = null;
            if (tailLen>0){
                byte[] bLen = writeUInt(tailLen);
                ret=new byte[bLen.length+1];
                ret[0]=(byte)255;
                System.arraycopy(bLen,0,ret,1,bLen.length);
            }else {
                ret = new byte[1];
                ret[0] = (byte) (1<<7 | key.length);

            }
            out.write(ret);
            out.write(key);
            out.write(content);
        }else {
            int group=keyMap.getKeyMapGroupID();
            int tailLen = group - 63 ;
            byte[] ret = null;
            if (tailLen>0){
                byte[] bLen = writeUInt(tailLen);
                ret=new byte[bLen.length+1];
                ret[0]=(byte)255;
                System.arraycopy(bLen,0,ret,1,bLen.length);
            }else {
                ret = new byte[1];
                ret[0] = (byte)group;
            }
            out.write(ret);
            out.write(content);
        }
        byte[] result = out.toByteArray();
        out.close();
        return result;
    }

    private byte[] writeUInt(int param){

        int headLen=param/127+(param%127==0?0:1);
        byte[] ret=new byte[headLen==0?1:headLen];
        int curPos=0;
        try {
            while (true){
                if (param>127){
                    ret[curPos]= (byte)-127;
                    param-=127;
                    curPos++;
                }else {
                    ret[curPos]=(byte)param;
                    curPos++;
                    break;
                }
            }
        }catch (Throwable e){
            e.printStackTrace();
        }

        return ret;
    }

    private byte[] serializeString(String in) throws UnsupportedEncodingException {
        byte[] b=in.getBytes("UTF-8");
        int tailLen = b.length - 15 ;
        byte[] ret= null;
        if (tailLen>0){
            byte[] bLen = writeUInt(tailLen);
            ret = new byte[b.length+bLen.length+1];
            ret[0] = (byte)(176 | 15);
            System.arraycopy(bLen,0,ret,1,bLen.length);
            System.arraycopy(b,0,ret,1+bLen.length,b.length);
        }else {
            ret=new byte[b.length+1];
            ret[0] = (byte) (160 | b.length);
            System.arraycopy(b,0,ret,1,b.length);
        }
        return ret;
    }

    private byte[] serializeByteArray(byte[] in) throws UnsupportedEncodingException {
        int tailLen = in.length - 15 ;
        byte[] ret= null;
        if (tailLen>0){
            byte[] bLen = writeUInt(tailLen);
            ret = new byte[in.length+bLen.length+1];
            ret[0] = (byte)(144 | 15);
            System.arraycopy(bLen,0,ret,1,bLen.length);
            System.arraycopy(in,0,ret,1+bLen.length,in.length);
        }else {
            ret=new byte[in.length+1];
            ret[0] = (byte) (128 | in.length);
            System.arraycopy(in,0,ret,1,in.length);
        }
        return ret;
    }


    private byte[] serializeNumber(Number in) throws UnsupportedEncodingException {
        byte[] b=String.valueOf(in).getBytes("UTF-8");
        int tailLen = b.length - 31 ;
        byte[] ret = null;
        if (tailLen>0){
            byte[] bLen = writeUInt(tailLen);
            ret=new byte[b.length+bLen.length+1];
            ret[0] = (byte)255;
            System.arraycopy(bLen,0,ret,1,bLen.length);
            System.arraycopy(b,0,ret,1+bLen.length,b.length);
        }else {
            ret=new byte[b.length+1];
            ret[0] = (byte)(192 | b.length);
            System.arraycopy(b,0,ret,1,b.length);
        }
        return ret;
    }


    private byte[] serializeArray(Collection obj) throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        int tailLen = obj.size() - 31 ;
        byte[] ret=null;
        if (tailLen>0){
            byte[] bLen = writeUInt(tailLen);
            ret = new byte[bLen.length+1];
            ret[0] = (byte)127;
            System.arraycopy(bLen,0,ret,1,bLen.length);
        }else {
            ret = new byte[1];
            ret[0] = (byte) (64 | obj.size());
        }
        out.write(ret);
        for (Object one : obj){
            byte[] temp = serializeObject(one);
            if (temp==null){
                continue;
            }
            out.write(temp);
        }
        byte[] result = out.toByteArray();
        out.close();
        return result;
    }

    private byte[] serializeMap(Map<String,Object> obj) throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        int tailLen = obj.size() - 31 ;
        byte[] ret = null;
        if (tailLen>0){
            byte[] bLen = writeUInt(tailLen);
            ret = new byte[bLen.length+1];
            ret[0] = (byte)63;
            System.arraycopy(bLen,0,ret,1,bLen.length);
        }else {
            ret = new byte[1];
            ret[0] =(byte)obj.size();
        }
        out.write(ret);
        Set<String> keys = obj.keySet();
        for (String k : keys){
            int key = keyMap.keyForName(k);
            if (key<0){
                continue;
            }
            Object one=obj.get(k);
            byte[] temp = serializeObject(one);
            if (temp == null){
                continue;
            }
            byte[] bLen = writeUInt(key);
            out.write(bLen);
            out.write(temp);
        }
        byte[] result = out.toByteArray();
        out.close();
        return result;

    }

    private byte[] serializeObject(Object one) throws IOException {
       byte[] temp=null;
        if (one instanceof Map){
            temp = serializeMap((Map)one);
        }else if (one instanceof Collection){
            temp = serializeArray((Collection)one);
        }else if (one instanceof String){
            temp = serializeString((String)one);
        }else if (one instanceof Number){
            temp = serializeNumber((Number)one);
        }else if (one instanceof Boolean){
            temp = serializeNumber((Boolean)one?1:0);
        }else if (one instanceof byte[]){
            temp = serializeByteArray((byte[])one);
        }else if (one instanceof NullObject){
        }
        return temp;

    }

}
