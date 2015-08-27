package test.com.xixi.bjson; 

import com.xixi.bjson.*;
import org.junit.Assert;
import org.junit.Test;
import org.junit.Before; 
import org.junit.After;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;

/** 
* BjsonSerialize Tester. 
* 
* @author <Authors name> 
* @since <pre>八月 27, 2015</pre> 
* @version 1.0 
*/ 
public class BjsonSerializeTest { 

@Before
public void before() throws Exception { 
} 

@After
public void after() throws Exception { 
} 

/** 
* 
* Method: serialize(Object in, boolean withKeyMap) 
* 
*/ 
@Test
public void testSerialize() throws Exception { 
//TODO: Test goes here... 
} 


/** 
* 
* Method: writeUInt(int param) 
* 
*/ 
@Test
public void testWriteUInt() throws Exception { 
//TODO: Test goes here... 
/* 
try { 
   Method method = BjsonSerialize.getClass().getMethod("writeUInt", int.class); 
   method.setAccessible(true); 
   method.invoke(<Object>, <Parameters>); 
} catch(NoSuchMethodException e) { 
} catch(IllegalAccessException e) { 
} catch(InvocationTargetException e) { 
} 
*/ 
} 

/** 
* 
* Method: serializeString(String in) 
* 
*/ 
@Test
public void testSerializeString() throws Exception { 
   BjsonSerialize serialize = new BjsonSerialize(KeyMapFactory.mutableKeyMap());
   Method method = BjsonSerialize.class.getDeclaredMethod("serializeString", String.class);
   method.setAccessible(true);
   byte[] b=(byte[])method.invoke(serialize, "我从哪里来");
   BjsonDeserialize deserialize = new BjsonDeserialize();
   String s=deserialize.deserializeString(b,new BjsonDeserialize.IndexPath(0));
   Assert.assertEquals("字符", "我从哪里来", s);
} 

/** 
* 
* Method: serializeByteArray(byte[] in) 
* 
*/ 
@Test
public void testSerializeByteArray() throws Exception { 
   BjsonSerialize serialize = new BjsonSerialize(KeyMapFactory.mutableKeyMap());
   Method method = BjsonSerialize.class.getDeclaredMethod("serializeByteArray", byte[].class);
   method.setAccessible(true);
   byte[] b=(byte[])method.invoke(serialize, "我从哪里来".getBytes("UTF-8"));
   BjsonDeserialize deserialize = new BjsonDeserialize();
   byte[] s=deserialize.deserializeByteArray(b, new BjsonDeserialize.IndexPath(0));
   Assert.assertEquals("字符", "我从哪里来", new String(s,"UTF-8"));
} 

/** 
* 
* Method: serializeNumber(Number in) 
* 
*/ 
@Test
public void testSerializeNumber() throws Exception { 
   BjsonSerialize serialize = new BjsonSerialize(KeyMapFactory.mutableKeyMap());
   Method method = BjsonSerialize.class.getDeclaredMethod("serializeNumber", Number.class);
   method.setAccessible(true);
   byte[] b=(byte[])method.invoke(serialize, 123.123);
   BjsonDeserialize deserialize = new BjsonDeserialize();
   double s=deserialize.deserializeNumber(b, new BjsonDeserialize.IndexPath(0));
   Assert.assertEquals(123.123, s, 0.0000001);
} 

/** 
* 
* Method: serializeArray(Collection obj) 
* 
*/ 
@Test
public void testSerializeArray() throws Exception { 
   BjsonSerialize serialize = new BjsonSerialize(KeyMapFactory.mutableKeyMap());
   Method method = BjsonSerialize.class.getDeclaredMethod("serializeArray", Collection.class);
   method.setAccessible(true);

   List str = new ArrayList();
   for (int i=0;i<6;i++){
      str.add("test"+i);
   }
   byte[] b=(byte[])method.invoke(serialize, str);
   BjsonDeserialize deserialize = new BjsonDeserialize();
   List s=deserialize.deserializeArray(b, new BjsonDeserialize.IndexPath(0));
   Assert.assertEquals(s.size(),str.size());
   for (int i=0;i<s.size();i++){
      Assert.assertEquals(str.get(i), s.get(i));
   }
/*
try {
   Method method = BjsonSerialize.getClass().getMethod("serializeArray", Collection.class);
   method.setAccessible(true);
   method.invoke(<Object>, <Parameters>);
} catch(NoSuchMethodException e) {
} catch(IllegalAccessException e) {
} catch(InvocationTargetException e) {
}
*/
} 

/** 
* 
* Method: serializeMap(Map<String,Object> obj) 
* 
*/ 
@Test
public void testSerializeMap() throws Exception {
   BjsonSerialize serialize = new BjsonSerialize(KeyMapFactory.mutableKeyMap());
   Method method = BjsonSerialize.class.getDeclaredMethod("serializeMap", Map.class);
   method.setAccessible(true);
   Map<String,Object> test = new HashMap<String, Object>();
   for (int i=0;i<6;i++){
      test.put(""+i,"tese"+i);
   }
   byte[] b=(byte[])method.invoke(serialize, test);
   BjsonDeserialize deserialize = new BjsonDeserialize();
   deserialize.keyMap=serialize.keyMap;
   Map<String,Object> s=deserialize.deserializeMap(b, new BjsonDeserialize.IndexPath(0));
   Assert.assertEquals(6,s.size());
   Set<String> keys=s.keySet();
   for (String k : keys){
      Assert.assertEquals(test.get(k),s.get(k));
   }
/* 
try { 
   Method method = BjsonSerialize.getClass().getMethod("serializeMap", Map<String,Object>.class); 
   method.setAccessible(true); 
   method.invoke(<Object>, <Parameters>); 
} catch(NoSuchMethodException e) { 
} catch(IllegalAccessException e) { 
} catch(InvocationTargetException e) { 
} 
*/ 
} 

/** 
* 
* Method: serializeObject(Object one) 
* 
*/ 
@Test
public void testSerializeObject() throws Exception { 
//TODO: Test goes here... 
/* 
try { 
   Method method = BjsonSerialize.getClass().getMethod("serializeObject", Object.class); 
   method.setAccessible(true); 
   method.invoke(<Object>, <Parameters>); 
} catch(NoSuchMethodException e) { 
} catch(IllegalAccessException e) { 
} catch(InvocationTargetException e) { 
} 
*/ 
} 

} 
