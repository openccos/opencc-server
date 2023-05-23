package com.openccos.framework.core.util;

public class NumberUtil {
  private NumberUtil() {}

  public static byte[] longToBytes(long l) {
    byte[] result = new byte[8];
    for (int i = 7; i >= 0; i--) {
      result[i] = (byte)(l & 0xFF);
      l >>= 8;
    }
    return result;
  }

  public static long bytesToLong(byte[] b) {
    long result = 0;
    for (int i = 0; i < 8; i++) {
      result <<= 8;
      result |= (b[i] & 0xFF);
    }
    return result;
  }

  public static boolean longEquals(Long l1, Long l2) {
    if (l1 != null) {
      return l1.equals(l2);
    }

    return l2 == null;
  }
}
