package com.xixi.bjson;

import java.io.UnsupportedEncodingException;

/**
 * 可变的Keymap
 * Created by wangchunsee on 15/8/25.
 */
public class HashKeyMap implements KeyMap {
    private int beginPos = 0;
    private int HASH_TAB_LEN = 127;
    private String[] names=new String[HASH_TAB_LEN];
    private int group;
    private int namesCount=0;

    @Override
    public int keyForName(String name) {
        int index=0;
        boolean find = false;
        int pos = hashAddr(name);
        int count = 0;
        int searchPos = pos;
        while (count<127){
            int m = 0;
            while (m<=beginPos){
                if (names[m+searchPos]!=null&&names[m+searchPos].equals(name)) {
                    find=true;
                    index=m+searchPos;
                    break;
                }
                m+=127;
            }
            searchPos++;
            if (searchPos>=127){
                searchPos=0;
            }
            count++;
        }

        if (find){
            return index;
        }else {
            return addName(name);
        }
    }

    private int addName(String name){
        namesCount++;
        if (namesCount>HASH_TAB_LEN){
            beginPos = HASH_TAB_LEN;
            HASH_TAB_LEN+=127;
        }
        int pos = hashAddr(name)+beginPos;
        while (true){
            if (names[pos]==null){
                names[pos]=name;
                break;
            }else {
                pos++;
                if (pos>=HASH_TAB_LEN){
                    pos = beginPos;
                }
            }
        }
        return pos;
    }

    private int hashAddr(String key){
        try {
            return elfHash(key);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return 0;
    }

    private int elfHash(String key) throws UnsupportedEncodingException {
        byte[] b = key.getBytes("UTF-8");
        long h=0;
        for (int i=0;i<b.length;i++)
        {
            h = (h << 4) + b[i];
            long g = h & 0xF0000000L;
            if(g>0)
                h ^= g >> 24;
            h &= ~g;
        }
        return (int) (h % HASH_TAB_LEN);
    }


    @Override
    public String nameForKey(int key) {
        if (key<names.length){
            return names[key];
        }
        return null;
    }

    @Override
    public String serializeKeyMap() {
        StringBuilder builder = new StringBuilder();
        if (names.length>0){
            builder.append(names[0]);
            for (int i=1;i<names.length;i++){
                if (names[i]!=null){
                    builder.append(",");
                    builder.append(names[i]);
                }
            }
        }
        return builder.toString();
    }

    @Override
    public int getKeyMapGroupID() {
        return group;
    }
}
