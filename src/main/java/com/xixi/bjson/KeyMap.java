package com.xixi.bjson;

/**
 * Created by wangchunsee on 15/8/24.
 */
public interface KeyMap {
    public int keyForName(String name);
    public String nameForKey(int key);
    /**
     * 用逗号分割连接的所有的Name
     * @return
     */
    public String serializeKeyMap();
    public int getKeyMapGroupID();
}
