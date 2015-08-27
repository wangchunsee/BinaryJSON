package com.xixi.bjson;

/**
 * Created by wangchunsee on 15/8/25.
 */
public class KeyMapFactory {

    public static KeyMap keyMapFromString(String keyMapString){
        return new SimpleKeyMap(keyMapString);
    }

    public static KeyMap mutableKeyMap(){
        return new MutableKeyMap();
    }

    public static KeyMap keyMapFromGroup(int groupId){
        return null;
    }
}
