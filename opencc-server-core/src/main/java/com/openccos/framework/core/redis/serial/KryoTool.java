package com.openccos.framework.core.redis.serial;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.esotericsoftware.kryo.pool.KryoFactory;
import com.esotericsoftware.kryo.pool.KryoPool;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.Serializable;

public class KryoTool implements ISerialTool {

    private KryoPool pool;

    public KryoTool() {
        KryoFactory factory = () -> {
            Kryo kryo = new Kryo();
            kryo.setReferences(false);
            // 关闭注册
            kryo.setRegistrationRequired(false);
            return kryo;
        };

        // Build pool with SoftReferences enabled (optional)
        pool = new KryoPool.Builder(factory).softReferences().build();
    }

    @Override
    public <T extends Serializable> byte[] writeObject(T obj) {
        byte[] result = null;

        if (obj != null) {
            Kryo kryo = pool.borrow();
            try {
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                Output output = new Output(baos);
                kryo.writeObject(output, obj);
                output.close();

                result = baos.toByteArray();
            } finally {
                pool.release(kryo);
            }
        }

        return result;
    }

    @Override
    public <T extends Serializable> T readObject(byte[] data, Class<T> clazz) {
        T result = null;

        if (data != null) {
            Kryo kryo = pool.borrow();
            try {
                ByteArrayInputStream bais = new ByteArrayInputStream(data);
                Input input = new Input(bais);
                result = kryo.readObject(input, clazz);
            } finally {
                pool.release(kryo);
            }
        }

        return result;
    }
}
