package com.github.walkvoid.wvframework.utils;

import com.github.walkvoid.wvframework.models.TimePattern;
import com.github.walkvoid.wvframework.models.spi.SnowFlake;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashSet;
import java.util.ServiceLoader;
import java.util.Set;
import java.util.UUID;

/**
 * @author jiangjunqing
 * @date 2024/6/6
 * @description:
 * @version:
 */
public class IDGenerateUtils {

    private final static DateTimeFormatter DF = DateTimeFormatter.ofPattern(TimePattern.P5);

    private static SnowFlake SNOWFLAKE;

    private IDGenerateUtils() {}

    /**
     * 生成yyyyMMddHHmmssSSS型式的Id
     * @return
     */
    public static Long getTimeStyleId(){
        return Long.parseLong(LocalDateTime.now().format(DF));
    }

    /**
     * 生成yyyyMMddHHmmssSSS带前缀的Id
     * @param prefix 传入的前缀
     * @return
     */
    public static String getTimeStyleId(String prefix){
        return prefix + getTimeStyleId();
    }

    /**
     * 生成yyyyMMddHHmmssSSS型式的Id
     * @return
     */
    public static String getUUID(){
        return UUID.randomUUID().toString().replace("-","");
    }

    /**
     * 生成雪花Id
     * @return
     */
    public static Long getSnowflakeId(){
        if (SNOWFLAKE == null) {
            synchronized (IDGenerateUtils.class) {
                ServiceLoader<SnowFlake> load = ServiceLoader.load(SnowFlake.class);
                if (load.iterator().hasNext()) {
                    SNOWFLAKE =  load.iterator().next();
                } else {
                    SNOWFLAKE =  DefaultSnowFlake.getInstance();
                }
            }
        }
        return SNOWFLAKE.nextId();
    }

    /**
     * 默认的雪花算法实现
     *
     */
    private static class DefaultSnowFlake implements SnowFlake {


        /**
         * 起始的时间戳 2024-01-01
         */
        private final static long START_TIMESTAMP = 1704038400000L;

        /**
         * 每一部分占用的位数
         */
        private final static long SEQUENCE_BIT = 12; //序列号占用的位数
        private final static long MACHINE_BIT = 5;   //机器标识占用的位数
        private final static long DATACENTER_BIT = 5;//数据中心占用的位数

        /**
         * 每一部分的最大值
         */
        private final static long MAX_DATACENTER_NUM = -1L ^ (-1L << DATACENTER_BIT);
        private final static long MAX_MACHINE_NUM = -1L ^ (-1L << MACHINE_BIT);
        private final static long MAX_SEQUENCE = -1L ^ (-1L << SEQUENCE_BIT);

        /**
         * 每一部分向左的位移
         */
        private final static long MACHINE_LEFT = SEQUENCE_BIT;
        private final static long DATACENTER_LEFT = SEQUENCE_BIT + MACHINE_BIT;
        private final static long TIMESTMP_LEFT = DATACENTER_LEFT + DATACENTER_BIT;

        private long datacenterId;  //数据中心
        private long machineId;     //机器标识
        private long sequence = 0L; //序列号
        private long lastTimestamp = -1L;//上一次时间戳

        private static DefaultSnowFlake INSTANCE;

        static {
            int identify = randomIdentify();
            INSTANCE = new DefaultSnowFlake((identify>>5) & 0x1f,identify & 0x1f);
        }

        public static DefaultSnowFlake getInstance(){
            return INSTANCE;
        }


        private DefaultSnowFlake(long datacenterId, long machineId) {
            if (datacenterId > MAX_DATACENTER_NUM || datacenterId < 0) {
                throw new IllegalArgumentException("datacenterId can't be greater than MAX_DATACENTER_NUM or less than 0");
            }
            if (machineId > MAX_MACHINE_NUM || machineId < 0) {
                throw new IllegalArgumentException("machineId can't be greater than MAX_MACHINE_NUM or less than 0");
            }
            this.datacenterId = datacenterId;
            this.machineId = machineId;
        }


        @Override
        public Long nextId() {
            return this.nextId0();
        }

        /**
         * 产生下一个ID
         *
         * @return
         */
        private synchronized long nextId0() {
            long currStmp = getNewstmp();
            if (currStmp < lastTimestamp) {
                throw new RuntimeException("Clock moved backwards.  Refusing to generate id");
            }

            if (currStmp == lastTimestamp) {
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

            lastTimestamp = currStmp;

            return (currStmp - START_TIMESTAMP) << TIMESTMP_LEFT //时间戳部分
                    | datacenterId << DATACENTER_LEFT       //数据中心部分
                    | machineId << MACHINE_LEFT             //机器标识部分
                    | sequence;                             //序列号部分
        }

        private long getNextMill() {
            long mill = getNewstmp();
            while (mill <= lastTimestamp) {
                mill = getNewstmp();
            }
            return mill;
        }

        private long getNewstmp() {
            return System.currentTimeMillis();
        }

        private static int randomIdentify() {
            int hash = UUID.randomUUID().hashCode();
            return  (hash ^ (hash>>>16)) & 1023;
        }
    }
}
