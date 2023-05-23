package com.openccos.framework.core.redis.serial;

import java.io.Serializable;

public interface ISerialTool {
    <T extends Serializable> byte[] writeObject(T obj);
    <T extends Serializable> T readObject(byte[] data, Class<T> clazz);
}
