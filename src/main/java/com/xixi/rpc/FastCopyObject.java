package com.xixi.rpc;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by wangchunsee on 15/7/23.
 */
public class FastCopyObject {

    public static void initObject(Map<String,Object> map,Object des){
        Class cur=des.getClass();
        while (cur != Object.class){
            Field[] fields = cur.getDeclaredFields();
            for (Field f : fields){
                f.setAccessible(true);
                Object value = map.get(f.getName());
                invokeSetAttr(f.getName(),value,des,cur);
//                Class temp=f.getType();
//                if (value!=null && temp.isInstance(value)){
//                    try {
//                        f.set(des,value);
//                    } catch (IllegalAccessException e) {
//                        e.printStackTrace();
//                    }
//                }
            }
            cur=cur.getSuperclass();
        }
    }

    public static void fastCopy(Object src,Object des){
        Class cur=src.getClass();
        Map<String,Object> srcFields = new HashMap<String, Object>();
        while (cur != Object.class){
            Field[] fields = cur.getDeclaredFields();
            for (Field f : fields){
                f.setAccessible(true);
                Object value = null;
                try {
                    value=f.get(src);
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
                if (value!=null){
                    srcFields.put(f.getName(),value);
                }
            }
            cur=cur.getSuperclass();
        }
        cur=des.getClass();
        while (cur != Object.class){
            Field[] fields = cur.getDeclaredFields();
            for (Field f : fields){
                f.setAccessible(true);
                Object value = srcFields.get(f.getName());
                Class temp=f.getType();
                if (value!=null && temp.isInstance(value)){
                    try {
                        f.set(des,value);
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    }
                }
            }
            cur=cur.getSuperclass();
        }
    }

    public static Map<String,Object> attr2Map(Object obj) {
        Map<String ,Object> fieldsAndValues = new HashMap<String, Object>();
        Class cur=obj.getClass();
        while (cur != Object.class){
            Field[] fields = cur.getDeclaredFields();
            for(int i=0; i< fields.length; i++)
            {
                Field f = fields[i];
                Object value = invokeGetAttr(f.getName(),obj,cur);
                if (value!=null){
                    fieldsAndValues.put(f.getName(),value.toString());
                }
            }
            cur=cur.getSuperclass();
        }

        return fieldsAndValues;
    }

    /**
     *
     * 执行某个Field的getField方法
     *
     * @param fieldName 类的属性名称
     * @return
     */
    private static boolean invokeGetAttr(String fieldName,Object obj,Class ownerClass)
    {

        //fieldName -> FieldName
        String methodName = fieldName.substring(0, 1).toUpperCase()+ fieldName.substring(1);

        Method method = null;
        try
        {
            method = ownerClass.getMethod("get" + methodName);
        }
        catch (SecurityException e)
        {
            return false;
        }
        catch (NoSuchMethodException e)
        {
            return false;
        }

        //invoke getMethod
        try
        {
            method.invoke(obj);
            return true;
        }
        catch (Exception e)
        {
            return false;
        }
    }

    /**
     *
     * 执行某个Field的getField方法
     *
     * @param fieldName 类的属性名称
     * @return
     */
    private static Object invokeSetAttr(String fieldName,Object param,Object obj,Class ownerClass)
    {

        //fieldName -> FieldName
        String methodName = fieldName.substring(0, 1).toUpperCase()+ fieldName.substring(1);

        Method method = null;
        try
        {
            method = ownerClass.getMethod("set" + methodName,param.getClass());
        }
        catch (SecurityException e)
        {
            return null;
        }
        catch (NoSuchMethodException e)
        {
            return null;
        }

        //invoke getMethod
        try
        {
            return method.invoke(obj,param);
        }
        catch (Exception e)
        {
            return null;
        }
    }
}
