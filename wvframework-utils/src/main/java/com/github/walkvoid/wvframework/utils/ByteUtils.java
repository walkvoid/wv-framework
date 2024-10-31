package com.github.walkvoid.wvframework.utils;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.CharBuffer;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.DoubleAdder;
import java.util.concurrent.atomic.LongAdder;


/**
 *
 */
public class ByteUtils {
    public static final ByteOrder DEFAULT_ORDER;
    public static final ByteOrder CPU_ENDIAN;
    public static final HashMap<Class<?>, Integer> NUMBER_MAX_BITS_LENGTH = new HashMap<>();


    static {
        DEFAULT_ORDER = ByteOrder.LITTLE_ENDIAN;
        CPU_ENDIAN = "little".equals(System.getProperty("sun.cpu.endian")) ? ByteOrder.LITTLE_ENDIAN : ByteOrder.BIG_ENDIAN;
        NUMBER_MAX_BITS_LENGTH.put(Byte.class, 1);
        NUMBER_MAX_BITS_LENGTH.put(Short.class, 2);
        NUMBER_MAX_BITS_LENGTH.put(Integer.class, 4);
        NUMBER_MAX_BITS_LENGTH.put(AtomicLong.class, 8);
        NUMBER_MAX_BITS_LENGTH.put(AtomicInteger.class, 4);
        NUMBER_MAX_BITS_LENGTH.put(LongAdder.class, 8);
        NUMBER_MAX_BITS_LENGTH.put(Long.class, 8);
        NUMBER_MAX_BITS_LENGTH.put(Float.class, 4);
        NUMBER_MAX_BITS_LENGTH.put(Double.class, 8);
        NUMBER_MAX_BITS_LENGTH.put(BigInteger.class, 500000000);
        NUMBER_MAX_BITS_LENGTH.put(BigDecimal.class, Integer.MAX_VALUE);
    }

    private ByteUtils() {
    }

    public static byte intToByte(int intValue) {
        return (byte)intValue;
    }

    public static int byteToUnsignedInt(byte byteValue) {
        return byteValue & 255;
    }

    public static short bytesToShort(byte[] bytes) {
        return bytesToNumber(bytes, 0, bytes.length, Short.class, false, DEFAULT_ORDER);
    }

    public static short bytesToShort(byte[] bytes, ByteOrder byteOrder) {
        return bytesToNumber(bytes, 0, bytes.length, Short.class, false, byteOrder);
    }

    public static short bytesToShort(byte[] bytes, int start, ByteOrder byteOrder) {
        return bytesToNumber(bytes, start, start + Short.BYTES, Short.class, false, byteOrder);
    }

    public static int bytesToUnsignedShort(byte[] bytes, ByteOrder byteOrder) {
        return bytesToNumber(bytes, 0, bytes.length, Integer.class, true, byteOrder);
    }


    public static byte[] shortToBytes(short shortValue) {
        return numberToBytes(shortValue, DEFAULT_ORDER);
    }

    public static byte[] shortToBytes(short shortValue, ByteOrder byteOrder) {
        return numberToBytes(shortValue, byteOrder);
    }

    public static int bytesToInt(byte[] bytes) {
        return bytesToNumber(bytes, 0, Integer.BYTES, Integer.class, false, DEFAULT_ORDER);
    }

    public static int bytesToInt(byte[] bytes, ByteOrder byteOrder) {
        return bytesToNumber(bytes, 0, Integer.BYTES, Integer.class, false, byteOrder);
    }

    public static int bytesToInt(byte[] bytes, int start, ByteOrder byteOrder) {
        return bytesToNumber(bytes, start, start + Integer.BYTES, Integer.class, false, byteOrder);
    }

    public static int bytesToInt(byte[] bytes, int startInclude, int endExclude, ByteOrder byteOrder) {
        return bytesToNumber(bytes, startInclude, endExclude, Integer.class, false, byteOrder);
    }

    public static long bytesToUnsignedInt(byte[] bytes, ByteOrder byteOrder) {
        return bytesToNumber(bytes, 0, Integer.BYTES, Long.class, true, byteOrder);
    }

    public static byte[] intToBytes(int intValue) {
        return numberToBytes(intValue, DEFAULT_ORDER);
    }

    public static byte[] intToBytes(int intValue, ByteOrder byteOrder) {
        return numberToBytes(intValue, byteOrder);
    }

    public static long bytesToLong(byte[] bytes) {
        return bytesToNumber(bytes, 0, Long.BYTES, Long.class, false, DEFAULT_ORDER);
    }

    public static long bytesToLong(byte[] bytes, ByteOrder byteOrder) {
        return bytesToNumber(bytes, 0, Long.BYTES, Long.class, false, byteOrder);
    }

    public static long bytesToLong(byte[] bytes, int start, ByteOrder byteOrder) {
        return bytesToNumber(bytes, start, start + Long.BYTES, Long.class, false, byteOrder);
    }

    public static long bytesToLong(byte[] bytes, int startInclude, int endExclude, ByteOrder byteOrder) {
        return bytesToNumber(bytes, startInclude, endExclude, Long.class, false, byteOrder);
    }

    public static byte[] longToBytes(long longValue) {
        return numberToBytes(longValue, DEFAULT_ORDER);
    }

    public static byte[] longToBytes(long longValue, ByteOrder byteOrder) {
        return numberToBytes(longValue, byteOrder);
    }

    public static double bytesToFloat(byte[] bytes) {
        return bytesToNumber(bytes, 0, bytes.length, Float.class, false, DEFAULT_ORDER);
    }

    public static float bytesToFloat(byte[] bytes, ByteOrder byteOrder) {
        return bytesToNumber(bytes, 0, bytes.length, Float.class, false, byteOrder);
    }

    public static float bytesToFloat(byte[] bytes, int startInclude, int endExclude, ByteOrder byteOrder) {
        return bytesToNumber(bytes, startInclude, endExclude, Float.class, false, byteOrder);
    }

    public static float bytesToUnsignedFloat(byte[] bytes, ByteOrder byteOrder) {
        return bytesToNumber(bytes, 0, bytes.length, Float.class, true, byteOrder);
    }

    public static byte[] floatToBytes(float floatValue) {
        return numberToBytes(floatValue, DEFAULT_ORDER);
    }

    public static byte[] floatToBytes(float floatValue, ByteOrder byteOrder) {
        return numberToBytes(floatValue, byteOrder);
    }

    public static double bytesToDouble(byte[] bytes) {
        return bytesToNumber(bytes, 0, bytes.length, Double.class, false, DEFAULT_ORDER);
    }

    public static double bytesToDouble(byte[] bytes, ByteOrder byteOrder) {
        return bytesToNumber(bytes, 0, bytes.length, Double.class, false, byteOrder);
    }

    public static double bytesToDouble(byte[] bytes, int startInclude, int endExclude, ByteOrder byteOrder) {
        return bytesToNumber(bytes, startInclude, endExclude, Double.class, false, byteOrder);
    }

    public static double bytesToUnsignedDouble(byte[] bytes, ByteOrder byteOrder) {
        return bytesToNumber(bytes, 0, bytes.length, Double.class, true, byteOrder);
    }

    public static byte[] doubleToBytes(double doubleValue) {
        return numberToBytes(doubleValue, DEFAULT_ORDER);
    }

    public static byte[] doubleToBytes(double doubleValue, ByteOrder byteOrder) {
        return numberToBytes(doubleValue, byteOrder);
    }


    /**
     *
     * @param number number的子类
     * @param byteOrder 字节存储顺序
     * @return
     */
    public static byte[] numberToBytes(Number number, ByteOrder byteOrder) {
        Integer maxBitsLength = NUMBER_MAX_BITS_LENGTH.get(number.getClass());
        if (maxBitsLength == null) {
            throw new IllegalArgumentException("Unsupported Number type: " + number.getClass().getName());
        }
        byte[] bytes = null;
        if (number instanceof Byte) {
            return new byte[]{number.byteValue()};
        } else if (number instanceof Short || number instanceof Integer || number instanceof Long
                || number instanceof AtomicInteger || number instanceof AtomicLong || number instanceof LongAdder) {
            bytes =  binaryStringToBytes(Long.toBinaryString(number.longValue()), maxBitsLength);
        } else if (number instanceof Float) {
            int floatToIntBits = Float.floatToIntBits(number.floatValue());
            bytes = numberToBytes(Integer.valueOf(floatToIntBits), null);
        } else if (number instanceof Double) {
            long floatToLongBits = Double.doubleToLongBits(number.doubleValue());
            bytes = numberToBytes(Long.valueOf(floatToLongBits), null);
        } else if (number instanceof BigInteger) {
            bytes = ((BigInteger) number).toByteArray();
            reverse(bytes, 0, bytes.length);
        } else if (number instanceof BigDecimal) {
            bytes = number.toString().getBytes(StandardCharsets.UTF_8);
        }
        if (bytes != null && ByteOrder.BIG_ENDIAN.equals(byteOrder)) {
            reverse(bytes, 0, bytes.length);
        }
        return bytes;
    }



    /**
     * 字节数组的字符串转byte数组
     * 由于str时是long的字节数组，所以要对str做一些裁截和拼接处理
     * @param str
     * @param length
     * @return
     */
    public static byte[] binaryStringToBytes(String str, int length) {
        StringBuilder stringBuilder = new StringBuilder();
        int lackBitsLength = 8 * length - str.length();
        if (lackBitsLength > 0) {
            for (int i = 0; i < lackBitsLength; i++) {
                stringBuilder.append("0");
            }
            stringBuilder.append(str);
        } else {
            stringBuilder.append(str,str.length() - 8*length, str.length());
        }
        byte[] bytes = new byte[length];
        for (int i = 0; i < stringBuilder.length(); i+=8) {
            bytes[i/8] = (byte) Integer.parseInt(stringBuilder.substring(i, i+8), 2);
        }
        return bytes;
    }


    /**
     * bytesToNumber的重载方法
     * @param bytes
     * @param targetClass
     * @param byteOrder
     * @return singed number
     * @param <T>
     * @throws IllegalArgumentException
     */
    public static <T extends Number> T bytesToNumber(byte[] bytes, Class<T> targetClass, ByteOrder byteOrder){
        return bytesToNumber(bytes, 0, bytes.length, targetClass,false, byteOrder);
    }

    /**
     * bytesToNumber的重载方法,获取一个无符号的数
     * @param bytes
     * @param targetClass
     * @param byteOrder
     * @return singed number
     * @param <T>
     */
    public static <T extends Number> T bytesToUnsignedNumber(byte[] bytes, Class<T> targetClass, ByteOrder byteOrder){
        return bytesToNumber(bytes, 0, bytes.length, targetClass,true, byteOrder);
    }


    /**
     * 将字节数组指定的子序列转换Number的子类
     * @param bytes 源数组
     * @param startInclude 开始的数组下标（包含）
     * @param endExclusive 结束的数组下标（不包含）
     * @param tClass 需要转换的目标类型
     * @param byteOrder 存储顺序
     * @return
     * @param <T> T extends Number
     * @throws IllegalArgumentException
     */
    @SuppressWarnings("unchecked")
    public static <T extends Number> T bytesToNumber(byte[] bytes, int startInclude, int endExclusive, Class<T> tClass,
                                                     boolean unsigned, ByteOrder byteOrder) throws IllegalArgumentException {
        if (bytes.length == 0) {
            throw new IllegalArgumentException("bytes is null.");
        }
        if (startInclude >= endExclusive) {
            throw new IllegalArgumentException("startInclude should less than endExclusive.");
        }
        if (tClass == null || !Number.class.isAssignableFrom(tClass)) {
            throw new IllegalArgumentException("tClass is null or tClass should be subclass of Number.class.");
        }
        Integer maxBitsLength = NUMBER_MAX_BITS_LENGTH.get(tClass);
        if (endExclusive - startInclude > maxBitsLength) {
            throw new IllegalArgumentException("startInclude should less than endExclusive.");
        }
        if (ByteOrder.BIG_ENDIAN.equals(byteOrder)) {
            reverse(bytes, startInclude, endExclusive);
        }
        T t = null;
        if (Byte.class == tClass) {
            t =  (T)new Byte(bytes[startInclude]);
        } else if (Integer.class == tClass || Long.class == tClass || Short.class == tClass || LongAdder.class == tClass) {
            Long result =  null;
            for (int i = 0; i <endExclusive - startInclude; i++) {
                if (i == 0) {
                    if (unsigned) {
                        result = (long)(bytes[startInclude]&0xff) << (8*(endExclusive-startInclude-1));
                    } else {
                        result = (long)bytes[startInclude] << (8*(endExclusive-startInclude-1));
                    }
                } else {
                    result |=  (long)(bytes[startInclude+i] & 0xff) << (8*(endExclusive-startInclude-1-i));
                }
            }
            t = longToNumber(result, tClass);
        } else if (Float.class == tClass ){
            //先将byte数组转换成int,为了复用Float.intBitsToFloat()方法
            Integer intBits = bytesToNumber(bytes, startInclude, endExclusive,  Integer.class, unsigned,null);
            if (intBits != null) {
                t =  (T)Float.valueOf(Float.intBitsToFloat(intBits));
            }
        } else if (Double.class == tClass || DoubleAdder.class == tClass) {
            //先将byte数组转换成long,为了复用Double.longBitsToDouble()方法
            Long longBits = bytesToNumber(bytes, startInclude, endExclusive, Long.class, unsigned,null);
            if (longBits != null) {
                double v = Double.longBitsToDouble(longBits);
                if (Double.class == tClass) {
                    t =  (T)Double.valueOf(v);
                } else {
                    DoubleAdder doubleAdder = new DoubleAdder();
                    doubleAdder.add(v);
                    t =  (T)doubleAdder;
                }
            }
        } else if (BigInteger.class == tClass) {
            byte[] sub = subBytes(bytes, startInclude, endExclusive);
            if (ByteOrder.LITTLE_ENDIAN.equals(byteOrder)) {
                reverse(sub, 0, sub.length);
            }
            t =  (T)new BigInteger(sub);
        } else if (BigDecimal.class == tClass) {
            byte[] sub = subBytes(bytes, startInclude, endExclusive);
            ByteBuffer bb = ByteBuffer.allocate(sub.length);
            bb.put(sub).flip();
            CharBuffer decode = StandardCharsets.UTF_8.decode(bb);
            t =  (T)new BigDecimal(decode.array());
        } else  {
            throw new IllegalArgumentException("Unsupported Number type: " + tClass.getName());
        }
        if (t == null) {
            throw new RuntimeException("bytes convert number fail. ");
        }
        return t;
    }

    /**
     * 获取子序列
     * @param bytes 源数组
     * @param startInclude 开始的数组下标（包含）
     * @param endExclusive 结束的数组下标（不包含）
     */
    private static byte[] subBytes(byte[] bytes, int startInclude, int endExclusive) {
        byte[] dest = new byte[endExclusive-startInclude];
        System.arraycopy(bytes, startInclude, dest, 0, endExclusive-startInclude);
        return dest;
    }

    /**
     * 反转字节数组指定子序列的顺序
     * @param bytes 源数组
     * @param startInclude 开始的数组下标（包含）
     * @param endExclusive 结束的数组下标（不包含）
     */
    private static void reverse(byte[] bytes, int startInclude, int endExclusive) {
        for (int i = 0; i < endExclusive-startInclude; i++) {
            if (startInclude + i < endExclusive-1-i) {
                byte tmp = bytes[startInclude+i];
                bytes[startInclude+i] = bytes[endExclusive-1-i];
                bytes[endExclusive-1-i] = tmp;
            }
        }
    }

    /**
     * 将long转换成number
     * @param value
     * @param tClass 指定Number的子类
     * @return
     * @param <T>
     */
    private static <T extends Number> T longToNumber(Long value, Class<T> tClass) {
        if (tClass == Short.class) {
            return (T)(new Short(value.shortValue()));
        } else if (tClass == Integer.class) {
            return (T)(new Integer(value.intValue()));
        } else if (tClass == AtomicInteger.class) {
            return (T)(new AtomicInteger(value.intValue()));
        } else if (tClass == Long.class) {
            return (T)value;
        } else if (tClass == AtomicLong.class) {
            return (T)(new AtomicLong(value));
        } else if (tClass == LongAdder.class){
            LongAdder longAdder = new LongAdder();
            longAdder.add(value);
            return (T)longAdder;
        }
        return null;
    }


}
