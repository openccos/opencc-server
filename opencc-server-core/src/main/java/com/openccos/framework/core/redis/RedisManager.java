package com.openccos.framework.core.redis;

import com.openccos.framework.core.exception.CcException;
import com.openccos.framework.core.redis.serial.ISerialTool;
import com.openccos.framework.core.redis.serial.JsonTool;
import com.openccos.framework.core.redis.serial.KryoTool;
import com.openccos.framework.core.util.JsonUtil;
import io.lettuce.core.RedisClient;
import io.lettuce.core.RedisURI;
import io.lettuce.core.api.StatefulRedisConnection;
import io.lettuce.core.api.sync.RedisCommands;
import io.lettuce.core.pubsub.StatefulRedisPubSubConnection;
import io.lettuce.core.pubsub.api.sync.RedisPubSubCommands;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.pool2.impl.GenericObjectPool;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.context.ApplicationContext;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

/**
 * Redis管理器
 * @author kevin
 */
@Slf4j
public class RedisManager implements DisposableBean {
    // SESSION
    public static final String PREFIX_SESSION = "SID";
    // 定时器锁
    public static final String PREFIX_TIMER = "TIM";
    /** UserSession 超时时间 */
    public static final int SESSION_EXPIRE_SEC = 30 * 60;
    /** 订阅发布的主题 */
    private static final String SCRIBE_SYSTEM = "CC_SYSTEM_";
//    private static final String SCRIBE_CACHED = "SW_CACHED";

    private RedisClient redisClient;
    private GenericObjectPool<StatefulRedisConnection<byte[], byte[]>> pool;
    // 定义通道名称
    private final String scribeChannel;

    private final ISerialTool serialTool;

    /**
     * 初始化Redis连接池
     */
    public RedisManager(final ApplicationContext applicationContext, RedisURI redisUri, String serializeType) {
//        this.applicationContext = applicationContext;
        if (StringUtils.isNotBlank(serializeType)) {
            switch (serializeType) {
                default:
                    log.warn("serializeType config error: " +serializeType );
                case "kryo":
                    this.serialTool = new KryoTool();
                    break;
                case "json":
                    this.serialTool = new JsonTool();
                    break;
            }
        } else {
            this.serialTool = new KryoTool();
        }

        redisClient = RedisClient.create(redisUri);
        scribeChannel = SCRIBE_SYSTEM + redisUri.getDatabase();

        GenericObjectPoolConfig config = new GenericObjectPoolConfig();
        config.setTestWhileIdle(true);
        pool = new GenericObjectPool<>(new RedisPooledObjectFactory(redisClient), config);

//        pool = ConnectionPoolSupport.createGenericObjectPool(
//                () -> redisClient.connect(ByteArrayCodec.INSTANCE),
//                new GenericObjectPoolConfig(), false);
    }

    public void shutdown() {
        pool.close();
        redisClient.shutdown();
    }

    /** 执行redis命令 */
    public <T> T command(Function<RedisConnection, T> handler) {
//        StatefulRedisConnection<byte[], byte[]> connection = redisClient.connect(ByteArrayCodec.INSTANCE);

        StatefulRedisConnection<byte[], byte[]> connection = null;
        try {
            connection = pool.borrowObject();
        } catch (Exception e) {
            throw new CcException(e);
        }

        try {
            RedisCommands<byte[], byte[]> redisCommands = connection.sync();
            RedisConnection redis = new RedisConnection(serialTool, redisCommands);
            return handler.apply(redis);
        }finally {
            pool.returnObject(connection);
        }
    }

    public boolean expire(String key, int expireSec) {
        return command(redis -> {
            return redis.expire(key, expireSec);
        });
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
        return command(redis -> redis.set(key, value, seconds));
    }

    public boolean setnx(byte[] key, byte[] value, int seconds) {
        return command(redis -> redis.setnx(key, value, seconds));
    }

    public <T extends Serializable> boolean setnx(String key, T obj, int seconds) {
        return command(redis -> redis.setnx(key, obj, seconds));
    }

    public <T extends Serializable> boolean set(String key, T obj, int seconds) {
        return command(redis -> redis.set(key, obj, seconds));
    }

    public boolean del(String key) {
        return command(redis -> redis.del(key));
    }

    public Long delKeys(String...keys) {
        return command(redis -> redis.delKeys(keys));
    }

    public boolean hSet(String key, byte[] field, byte[] value) {
        return command(redis -> redis.hSet(key, field, value));
    }

    public boolean hmSet(String key, Map<byte[], byte[]> values) {
        return command(redis -> redis.hmSet(key, values));
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
        return command(redis -> redis.hGet(key, field, clazz));
    }

    public boolean hSet(String key, String field, Serializable value) {
        return command(redis -> redis.hSet(key, field, value));
    }

    public Long ttl(String key) {
        return command(redis -> redis.ttl(key));
    }

    // 发布系统消息
    public void publish(RedisBroadcastEvent message) {
        try(StatefulRedisPubSubConnection<String, String> connection = redisClient.connectPubSub()) {
            RedisPubSubCommands<String, String> sync = connection.sync();
            sync.publish(scribeChannel, JsonUtil.encodeString(message));
        }
    }

    public Map<byte[], byte[]> hGetAll(String key) {
        return command(redis -> redis.hGetAll(key));
    }

    public <T extends Serializable> T get(String key, Class<T> clazz) {
        return command(redis -> redis.get(key, clazz));
    }

    public List<String> matchKeys(String keyLike) {
        return command(redis -> redis.matchKeys(keyLike));
    }

    public Long delMatchKeys(String keyLike) {
        return command(redis -> redis.delMatchKeys(keyLike));
    }

    public byte[] writeObject(Serializable entity) {
        return serialTool.writeObject(entity);
    }

    public <T extends Serializable> T readObject(byte[] value, Class<T> clazz) {
        return serialTool.readObject(value, clazz);
    }

    public Long inc(String key, long maxValue, int expressSec) {
        return command(redis -> redis.inc(key, maxValue, expressSec));
    }

    @Override
    public void destroy() throws Exception {
        this.shutdown();
    }

}
