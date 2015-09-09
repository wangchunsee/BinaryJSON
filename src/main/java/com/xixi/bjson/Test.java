package com.xixi.bjson;

import org.stringtree.json.JSONReader;
import org.stringtree.json.JSONWriter;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Created by wangchunsee on 15/8/26.
 */
public class Test {

    public static void main(String[] args){
        try {
            BjsonSerialize serialize = new BjsonSerialize(KeyMapFactory.mutableKeyMap());
            JSONReader reader = new JSONReader();
            byte[] txt = readTestFile("test.json");
            String str = new String(txt,"UTF-8");
            long time = System.currentTimeMillis();
            Object obj=reader.read(str);
            System.out.println("deserialize JSON:"+(System.currentTimeMillis()-time));
            time=System.currentTimeMillis();

            byte[] result = serialize.serialize(obj,true);

            System.out.println("serialize BJSON:"+(System.currentTimeMillis()-time));

            writeFile("test.bj",result);
            BjsonDeserialize deserialize = new BjsonDeserialize();

            time=System.currentTimeMillis();
            Object object=deserialize.deserialize(result);
            System.out.println("deserialize BJSON:"+(System.currentTimeMillis()-time));

            JSONWriter writer=new JSONWriter();

            time=System.currentTimeMillis();
            String dst=writer.write(object);
            System.out.println("serialize JSON:"+(System.currentTimeMillis()-time));

            writeFile("test.bj.json",dst.getBytes("UTF-8"));

            time=System.currentTimeMillis();
            for (int i=0;i<1000;i++){
                reader.read(str);
            }
            System.out.println("read JSON:"+(System.currentTimeMillis()-time));

            time=System.currentTimeMillis();
            for (int i=0;i<1000;i++){
                serialize.serialize(obj, false);
            }
            System.out.println("serialize JSON:"+(System.currentTimeMillis()-time));

            time=System.currentTimeMillis();
            for (int i=0;i<1000;i++){
                deserialize.deserialize(result);
            }
            System.out.println("deserialize JSON:"+(System.currentTimeMillis()-time));

            time=System.currentTimeMillis();
            for (int i=0;i<1000;i++){
                writer.write(object);
            }
            System.out.println("write JSON:"+(System.currentTimeMillis()-time));

//            BjsonSerialize serialize = new BjsonSerialize(KeyMapFactory.mutableKeyMap());
//            byte[] b = serialize.writeUInt(1,6);
//            BjsonDeserialize deserialize = new BjsonDeserialize();
//            int k=deserialize.readUInt(b,new BjsonDeserialize.IndexPath(0),5);


//            BjsonSerialize serialize = new BjsonSerialize(KeyMapFactory.mutableKeyMap());
//            Method method = BjsonSerialize.class.getDeclaredMethod("serializeMap", Map.class);
//            method.setAccessible(true);
//            Map<String,Object> test = new HashMap<String, Object>();
//            for (int i=0;i<6;i++){
//                test.put(""+i,"tese"+i);
//            }
//            byte[] b=(byte[])method.invoke(serialize, test);
//            BjsonDeserialize deserialize = new BjsonDeserialize();
//            deserialize.keyMap=serialize.keyMap;
//            Map<String,Object> s=deserialize.deserializeMap(b, new BjsonDeserialize.IndexPath(0));

            System.out.println("run finish");
        }catch (Exception e){
            e.printStackTrace();
        }

    }


    private static byte[] readTestFile(String fileName) throws IOException {
        File file = new File(fileName);
        int len=(int)file.length();
        FileInputStream fileReader = new FileInputStream(file);
        byte[] buf = new byte[len];
        int cur = 0;
        while (cur<len){
            cur+=fileReader.read(buf,cur,len-cur);
        }
        return buf;
    }

    private static void writeFile(String fileName,byte[] content) throws IOException {
        File file = new File(fileName);
        FileOutputStream fileWriter = new FileOutputStream(file);
        fileWriter.write(content);
    }
}
