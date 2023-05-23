package com.openccos.framework.core.redis;

import io.lettuce.core.RedisClient;
import io.lettuce.core.api.StatefulRedisConnection;
import io.lettuce.core.codec.ByteArrayCodec;
import org.apache.commons.pool2.PooledObject;
import org.apache.commons.pool2.PooledObjectFactory;
import org.apache.commons.pool2.impl.DefaultPooledObject;

class RedisPooledObjectFactory implements PooledObjectFactory<StatefulRedisConnection<byte[], byte[]>> {
  private RedisClient redisClient;

  public RedisPooledObjectFactory(RedisClient redisClient) {
    this.redisClient = redisClient;
  }

  @Override
  public void activateObject(PooledObject<StatefulRedisConnection<byte[], byte[]>> pooledObject) throws Exception {
  }

  @Override
  public void destroyObject(PooledObject<StatefulRedisConnection<byte[], byte[]>> pooledObject) throws Exception {
    pooledObject.getObject().close();
  }

  @Override
  public PooledObject<StatefulRedisConnection<byte[], byte[]>> makeObject() throws Exception {
    return new DefaultPooledObject<>(redisClient.connect(ByteArrayCodec.INSTANCE));
  }

  @Override
  public void passivateObject(PooledObject<StatefulRedisConnection<byte[], byte[]>> pooledObject) throws Exception {
  }

  @Override
  public boolean validateObject(PooledObject<StatefulRedisConnection<byte[], byte[]>> pooledObject) {
    return pooledObject.getObject().isOpen();
  }
}
