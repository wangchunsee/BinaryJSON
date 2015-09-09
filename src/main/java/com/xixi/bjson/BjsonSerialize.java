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
            byte[] ret = writeUInt(key.length,7);
            ret[0]=(byte) ( ret[0] | (1<<7));
            out.write(ret);
            out.write(key);
            out.write(content);
        }else {
            int group=keyMap.getKeyMapGroupID();
            byte[] ret = writeUInt(group,7);
            out.write(ret);
            out.write(content);
        }
        byte[] result = out.toByteArray();
        out.close();
        return result;
    }

    public byte[] writeUInt(int param,int firstByteLen){
        if (param==0){
            byte[] b=new byte[1];
            b[0]=0;
            return b;
        }
        int one = 16;
        int possible = 16;
        int posBit=31;
        while (one>0&&one<32){
            if (((param << one) >>> one)==param ){
                if ( one+1==32 || ((param << (one+1)) >>> (one+1))!=param){
                    posBit=one;
                    break;
                }else {
                    possible=possible/2;
                    one += (possible==0?1:possible);
                }
            }else {
                if (((param << (one-1)) >>> (one-1))==param){
                    posBit=one-1;
                    break;
                }else {
                    possible=possible/2;
                    one -= (possible==0?1:possible);
                }
            }
        }
        int bitLen = 32-(firstByteLen-1)-posBit;
        bitLen=bitLen>0?bitLen:0;
        int len = bitLen/7+(bitLen%7==0?0:1)+1;
        byte[] ret=new byte[len];
        if (len==1){
            ret[0]= (byte)param;
        }else {
            for (int i=len-1;i>=0;i--){
                if (i==0){
                    ret[i]=(byte)  ((param >>> ((len-1)*7)) | (1<<(firstByteLen-1)) );
                }else if (i == (len-1)){
                    ret[i]=(byte) ( ((param<<(32-(len-i)*7)) >>> (32-7)));
                }else {
                    ret[i]=(byte)( ((param<<(32-(len-i)*7)) >>> (32-7)) | (1<<7) ) ;
                }
            }
        }
        return ret;


//        byte[] ret=new byte[4];
//        int curPos=0;
//        while (true){
//            int temp = param & 0x7F;
//            param = param>>7;
//            if (param==0){
//                ret[curPos]=(byte)temp;
//                break;
//            }else {
//                temp = temp | 0x80 ;
//                ret[curPos]=(byte)temp;
//                curPos++;
//            }
//        }
//        byte[] result = new byte[curPos+1];
//        for (int i=0;i<curPos+1;i++){
//            result[i]=ret[curPos-i];
//        }
//        return result;
    }

    private byte[] serializeString(String in) throws UnsupportedEncodingException {
        byte[] b=in.getBytes("UTF-8");
        byte[] bLen = writeUInt(b.length,5);
        byte[] ret = new byte[b.length+bLen.length];
        bLen[0] = (byte)(bLen[0] | 0xA0);
        System.arraycopy(bLen,0,ret,0,bLen.length);
        System.arraycopy(b,0,ret,bLen.length,b.length);
        return ret;
    }

    private byte[] serializeByteArray(byte[] in) throws UnsupportedEncodingException {
        byte[] bLen = writeUInt(in.length,5);
        byte[] ret = new byte[in.length+bLen.length];
        bLen[0] = (byte)(bLen[0] | 0x80);
        System.arraycopy(bLen,0,ret,0,bLen.length);
        System.arraycopy(in,0,ret,bLen.length,in.length);
        return ret;
    }

    private byte[] serializeNumber(Number in) throws UnsupportedEncodingException {
        byte[] b=String.valueOf(in).getBytes("UTF-8");
        byte[] bLen = writeUInt(b.length,6);
        byte[] ret=new byte[b.length+bLen.length];
        bLen[0] = (byte)(bLen[0] | 0xC0);
        System.arraycopy(bLen,0,ret,0,bLen.length);
        System.arraycopy(b,0,ret,bLen.length,b.length);
        return ret;
    }


    private byte[] serializeArray(Collection obj) throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        byte[] ret = writeUInt(obj.size(),6);
        ret[0] = (byte)(0x40 | ret[0]);
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
        byte[] ret = writeUInt(obj.size(),6);
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
            byte[] bLen = writeUInt(key,8);
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
