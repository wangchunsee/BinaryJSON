package com.xixi.bjson;

import java.util.List;

/**
 * Created by wangchunsee on 15/8/25.
 */
public class SimpleKeyMap implements KeyMap{
    String[] names;

    public SimpleKeyMap(String name){
        names = name.split(",");
    }

    @Override
    public int keyForName(String name) {
        int index=0;
        for (String n : names){
            if (n.equals(name)){
                break;
            }
            index++;
        }
        return index;
    }

    @Override
    public String nameForKey(int key) {
        if (key>names.length){
            return null;
        }
        return names[key];
    }

    @Override
    public String serializeKeyMap() {
        StringBuilder builder = new StringBuilder();
        if (names!=null&&names.length>0){
            builder.append(names[0]);
            for (int i=1;i<names.length;i++){
                builder.append(",");
                builder.append(names[i]);
            }
        }
        return builder.toString();
    }

    @Override
    public int getKeyMapGroupID() {
        return 0;
    }
}
