package com.xixi.bjson;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

/**
 * 可变的Keymap
 * Created by wangchunsee on 15/8/25.
 */
public class MutableHashKeyMap__ implements KeyMap {
    private int beginPos = 0;
    private int HASH_TAB_LEN = 127;

    private String[] namesHash=new String[HASH_TAB_LEN];
    private int[] namesIndex=new int[HASH_TAB_LEN];

    private List<String> names=new ArrayList<String>();
    private int group;

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
                if (namesHash[m+searchPos]!=null&&namesHash[m+searchPos].equals(name)) {
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
            return namesIndex[index];
        }else {
            return addName(name);
        }
    }

    private int addName(String name){
        names.add(name);
        if (names.size()>HASH_TAB_LEN){
            beginPos = HASH_TAB_LEN;
            HASH_TAB_LEN+=127;
            String[] temp = new String[HASH_TAB_LEN];
            System.arraycopy(namesHash,0,temp,0,beginPos);
            namesHash=temp;
            int[] tempInt = new int[HASH_TAB_LEN];
            System.arraycopy(namesIndex,0,tempInt,0,beginPos);
            namesIndex=tempInt;
        }
        int pos = hashAddr(name)+beginPos;
        while (true){
            if (namesHash[pos]==null){
                namesHash[pos]=name;
                break;
            }else {
                pos++;
                if (pos>=HASH_TAB_LEN){
                    pos = beginPos;
                }
            }
        }
        namesIndex[pos]=names.size()-1;
        return namesIndex[pos];
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
        if (key<names.size()){
            return names.get(key);
        }
        return null;
    }

    @Override
    public String serializeKeyMap() {
        StringBuilder builder = new StringBuilder();
        if (names.size()>0){
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
