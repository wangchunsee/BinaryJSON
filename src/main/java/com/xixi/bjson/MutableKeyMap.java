package com.xixi.bjson;

import java.util.ArrayList;
import java.util.List;

/**
 * 可变的Keymap
 * Created by wangchunsee on 15/8/25.
 */
public class MutableKeyMap implements KeyMap {

    private List<String> names=new ArrayList<String>();
    private int group;


    @Override
    public int keyForName(String name) {
        int index=0;
        boolean find = false;
        for (String n : names){
            if (n.equals(name)){
                find=true;
                break;
            }
            index++;
        }
        if (find){
            return index;
        }else {
            names.add(name);
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
