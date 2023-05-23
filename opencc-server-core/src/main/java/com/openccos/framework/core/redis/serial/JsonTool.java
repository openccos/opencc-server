package com.openccos.framework.core.redis.serial;

import com.openccos.framework.core.util.JsonUtil;

import java.io.Serializable;

public class JsonTool implements ISerialTool {

    public JsonTool() {
    }

    @Override
    public <T extends Serializable> byte[] writeObject(T obj) {
        byte[] result = null;

        if (obj != null) {
            result = JsonUtil.encodeBytes(obj);
        }

        return result;
    }

    @Override
    public <T extends Serializable> T readObject(byte[] data, Class<T> clazz) {
        T result = null;

        if (data != null) {
            result = JsonUtil.parse(data, clazz);
        }

        return result;
    }
}
