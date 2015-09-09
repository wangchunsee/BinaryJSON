package com.xixi.bjson;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 可变的Keymap
 * Created by wangchunsee on 15/8/25.
 */
public class MutableHashKeyMap implements KeyMap {

    private Map<String,Integer> namesIndex = new HashMap<String, Integer>();
    private List<String> names=new ArrayList<String>();
    private int group;


    @Override
    public int keyForName(String name) {
        Integer index=namesIndex.get(name);
        if (index!=null){
            return index;
        }else {
            names.add(name);
            namesIndex.put(name,names.size()-1);
            return names.size()-1;
        }
    }

    @Override
    public String nameForKey(int key) {
        if (key<names.size()){
            return names.get(key);
        }
        return null;
    }

    @Override
    public String serializeKeyMap() {
        StringBuilder builder = new StringBuilder();
        if (names!=null&&!names.isEmpty()){
            builder.append(names.get(0));
            for (int i=1;i<names.size();i++){
                builder.append(",");
                builder.append(names.get(i));
            }
        }
        return builder.toString();
    }

    @Override
    public int getKeyMapGroupID() {
        return group;
    }
}
