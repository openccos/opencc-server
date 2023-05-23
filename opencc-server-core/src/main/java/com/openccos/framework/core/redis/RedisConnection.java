package com.openccos.framework.core.redis;

import com.openccos.framework.core.redis.serial.ISerialTool;
import io.lettuce.core.KeyValue;
import io.lettuce.core.SetArgs;
import io.lettuce.core.api.sync.RedisCommands;
import lombok.extern.slf4j.Slf4j;

import java.io.Serializable;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author kevin
 */
@Slf4j
public class RedisConnection {
    private final ISerialTool serialTool;
    public static final int DEL_COUNT = 200;
    private RedisCommands<byte[], byte[]> redis;

    public RedisConnection(ISerialTool serailTool, RedisCommands<byte[], byte[]> redisCommands) {
        this.serialTool = serailTool;
        this.redis = redisCommands;
    }

    /**
     * 获取指定key对应的值，并更新超时时间
     *
     * @param key 关键字KEY
     * @return byte[] 对象
     */
    public byte[] get(String key) {
        byte[] binKey = key.getBytes(StandardCharsets.UTF_8);
        return redis.get(binKey);
    }

//    public <T extends Serializable> T get(String key, Class<T> clazz) {
//        byte[] b = get(key);
//        if (b != null) {
//            return this.readObject(b, clazz);
//        }
//
//        return null;
//    }

    public boolean expire(String key, int expireSec) {
        byte[] bkey = key.getBytes(StandardCharsets.UTF_8);
        return redis.expire(bkey, expireSec);
    }

    /**
     * 设置指定key对应的值，并更新超时时间
     *
     * @param key 关键字KEY
     * @param value 值
     * @param seconds 超时删除时间(秒)
     * @return boolean 是否设置成功
     */
    public boolean set(byte[] key, byte[] value, int seconds) {
        if (value != null) {
            Boolean result = Boolean.FALSE;
            String ret;
            if (seconds > 0) {
                ret = redis.setex(key, seconds, value);
            } else {
                ret = redis.set(key, value);
            }

            if ("OK".equals(ret)) {
                result = Boolean.TRUE;
            }

            return result;
        } else {
            Long ret = redis.del(key);

            return (ret != null && ret == 1L);
        }
    }

    public boolean setnx(byte[] key, byte[] value, int seconds) {
        if (value != null) {
            Boolean result = Boolean.FALSE;
            String ret;
            if (seconds > 0) {
                ret = redis.set(key, value, new SetArgs().ex(seconds).nx());
                if ("OK".equals(ret)) {
                    result = Boolean.TRUE;
                }
            } else {
                result = redis.setnx(key, value);
            }

            return result;
        } else {
            Long ret = redis.del(key);

            return (ret != null && ret == 1L);
        }
    }

    public <T extends Serializable> boolean setnx(String key, T obj, int seconds) {
        return setnx(key.getBytes(StandardCharsets.UTF_8), serialTool.writeObject(obj), seconds);
    }

    /**
     * 获取指定key对应的值，并更新超时时间
     *
     * @param keys 关键字KEY
     * @return byte[] 对象
     */
    public List<KeyValue<byte[], byte[]>> mget(String[] keys, int expireSec) {
        byte[][] bkeys = new byte[keys.length][];
        for (int i = 0; i < keys.length; i++) {
            bkeys[i] = keys[i].getBytes(StandardCharsets.UTF_8);
        }

        List<KeyValue<byte[], byte[]>> result = redis.mget(bkeys);

        if (result != null && expireSec > 0) {
            redis.expire(bkeys[0], expireSec);
        }

        return result;
    }

    /**
     * 获取指定key里指定的域对应的值。 此时，key对应的存储对象是map。
     *
     * @param key redis关键字KEY
     * @param field map的KEY值
     * @return byte[] 对象
     */
    public byte[] hGet(String key, String field) {
        return redis.hget(key.getBytes(StandardCharsets.UTF_8), field.getBytes(StandardCharsets.UTF_8));
    }

    public <T extends Serializable> boolean set(String key, T obj, int seconds) {
        return set(key.getBytes(StandardCharsets.UTF_8), serialTool.writeObject(obj), seconds);
    }

    public boolean del(String key) {
        Long ret = redis.del(key.getBytes(StandardCharsets.UTF_8));
        return (ret != null && ret > 0);
    }

    public Long delKeys(String...keys) {
//        Long[] result = new Long[keys.length];

        byte[][] batchKey = new byte[keys.length][];

        for (int i = 0; i < keys.length; i++) {
            batchKey[i] = keys[i].getBytes(StandardCharsets.UTF_8);
        }

        return redis.del(batchKey);
    }

    public boolean hSet(String key, byte[] field, byte[] value) {
        return redis.hset(key.getBytes(StandardCharsets.UTF_8), field, value);
    }

    public boolean hmSet(String key, Map<byte[], byte[]> values) {
        String ret = redis.hmset(key.getBytes(StandardCharsets.UTF_8), values);
        return "OK".equals(ret);
    }

    /**
     * 获取指定key里指定的域对应的值，并转换为指定对象 此时，key对应的存储对象是map。
     *
     * @param key redis关键字KEY
     * @param field map的KEY值
     * @param clazz 对象存储类型
     * @return T 对象
     */
    public <T extends Serializable> T hGet(String key, String field, Class<T> clazz) {
        return serialTool.readObject(hGet(key, field), clazz);
    }

    public boolean hSet(String key, String field, Serializable value) {
        return hSet(key, field.getBytes(StandardCharsets.UTF_8), serialTool.writeObject(value));
    }

    public Long ttl(String key) {
        return redis.ttl(key.getBytes(StandardCharsets.UTF_8));
    }

    public <T extends Serializable> T get(String key, Class<T> clazz) {
        return serialTool.readObject(get(key), clazz);
    }

    public Map<byte[], byte[]> hGetAll(String key) {
        return redis.hgetall(key.getBytes(StandardCharsets.UTF_8));
    }

    /**
     * 模糊查询keys，影响性能，谨慎使用
     * @param keyLike 支持*表达模糊搜索
     * @return 查询到的列表
     */
    public List<String> matchKeys(String keyLike) {
        List<byte[]> list = redis.keys(keyLike.getBytes(StandardCharsets.UTF_8));
        if (list != null && !list.isEmpty()) {
            List<String> result = new ArrayList<>(list.size());
            for (byte[] b: list) {
                result.add(new String(b, StandardCharsets.UTF_8));
            }
            return result;
        }

        return null;
    }

    /**
     * 模糊删除keys，影响性能，谨慎使用
     * @param keyLike 支持*表达模糊搜索
     * @return 成功删除的条数
     */
    public Long delMatchKeys(String keyLike) {
        long result = 0;
        List<byte[]> list = redis.keys(keyLike.getBytes(StandardCharsets.UTF_8));
        if (list != null) {
            int size = list.size();

            for (int i = 0; i < size; i += DEL_COUNT) {
                int len = DEL_COUNT;
                if (i + len > size) {
                    len = size - i;
                }

                Long ret = delKeys(list, i, len);
                if (ret != null) {
                    result += ret;
                }
            }
        }

        return result;
    }

    private Long delKeys(List<byte[]> keys, int start, int len) {
         byte[][] batchKey = new byte[len][];

        for (int i = 0; i < len; i++) {
            batchKey[i] = keys.get(i + start);
        }

        return redis.del(batchKey);
    }

    public Long inc(String key, long maxValue, int expireSec) {
        byte[] binKey = key.getBytes(StandardCharsets.UTF_8);

        Long result = redis.incr(binKey);

        if (result != null && expireSec > 0 && result <= maxValue) {
            redis.expire(binKey, expireSec);
        }

        return result;
    }

}
