package com.openccos.framework.core.db.jdbc;
/**
 * 主键生成器 tweeter的snowflake 算法
 *    time—42bits,精确到ms，那就意味着其可以表示长达(2^42-1)/(1000360024*365)=139.5年
 *   (a) id构成: 42位的时间前缀 + 10位的节点标识 + 12位的sequence避免并发的数字(12位不够用时强制得到新的时间前缀)
 *       注意这里进行了小改动: snowkflake是5位的datacenter加5位的机器id; 这里变成使用10位的机器id
 *   (b) 对系统时间的依赖性非常强，需关闭ntp的时间同步功能。当检测到ntp时间调整后，将会拒绝分配id
 * @author xkliu
 */
public class IdGenerator implements IIdGenerator {
    /**
     * 起始的时间戳 epoch 2017-1-1
     */
    private final static long EPOCH_STMP = 1483200000000L;

    /**
     * 每一部分占用的位数
     */
    //序列号占用的位数
    private final static long SEQUENCE_BIT = 12;
    //机器标识占用的位数
    private final static long MACHINE_BIT = 10;

    /**
     * 每一部分的最大值
     */
    private final static long MAX_MACHINE_NUM = ~(-1L << MACHINE_BIT);
    private final static long MAX_SEQUENCE = ~(-1L << SEQUENCE_BIT);

    /**
     * 每一部分向左的位移
     */
    private final static long MACHINE_LEFT = SEQUENCE_BIT;
    private final static long TIMESTMP_LEFT = SEQUENCE_BIT + MACHINE_BIT;

    // private long datacenterId;  //数据中心
    // 机器标识
    private long machineIdShift;
    // 序列号
    private long sequence = 0L;
    // 上一次时间戳
    private long lastStmp = -1L;

    // 0 ~ 1023
    public IdGenerator(long machineId) {
        if (machineId > MAX_MACHINE_NUM || machineId < 0) {
            throw new IllegalArgumentException("machineId " + machineId + " can't be greater than " + MAX_MACHINE_NUM + " or less than 0");
        }

        this.machineIdShift = machineId << MACHINE_LEFT;
    }

    /**
     * 产生下一个ID
     *
     * @return ID值
     */
    @Override
    public synchronized long nextId() {
        long currStmp = getNewStamp();
        if (currStmp < lastStmp) {
            throw new RuntimeException("Clock moved backwards.  Refusing to generate id");
        }

        if (currStmp == lastStmp) {
            //相同毫秒内，序列号自增
            sequence = (sequence + 1) & MAX_SEQUENCE;
            //同一毫秒的序列数已经达到最大
            if (sequence == 0L) {
                currStmp = getNextMill();
            }
        } else {
            //不同毫秒内，序列号置为0
            sequence = 0L;
        }

        lastStmp = currStmp;

        // 时间戳部分 + 机器标识部分 + 序列号部分
        return (currStmp - EPOCH_STMP) << TIMESTMP_LEFT
                | machineIdShift
                | sequence;
    }

    private long getNextMill() {
        long mill = getNewStamp();
        while (mill <= lastStmp) {
            mill = getNewStamp();
        }
        return mill;
    }

    private long getNewStamp() {
        return System.currentTimeMillis();
    }

    // 根据ID值反向计算时间戳
    public static long queryTimestamp(long id) {
        return (id >>> TIMESTMP_LEFT) + EPOCH_STMP;
    }
}
