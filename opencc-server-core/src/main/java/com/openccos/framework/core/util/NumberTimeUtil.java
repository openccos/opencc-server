package com.openccos.framework.core.util;

import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Date;

/**
 * 数据库数字型存储时间
 *  bigint时间日期字段处理器，保存格式为：yyyyMMddHHmmssSSS
 *  int日期字段处理器，保存格式为：yyyyMMdd
 *  int时间字段处理器，保存格式为：HHmmssSSS
 * @author xkliu
 */
public class NumberTimeUtil {
  public static final long MILLIS_PER_SECOND = 1000L;
  public static final long MILLIS_PER_MINUTE = 100 * MILLIS_PER_SECOND;
  public static final long MILLIS_PER_HOUR = 100 * MILLIS_PER_MINUTE;
  public static final long MILLIS_PER_DAY = 100 * MILLIS_PER_HOUR;
  public static final long MIN_VALUE = 1900 * 10000000000000L;

  private NumberTimeUtil() {
  }

  /**
   * 获取当前时间
   * @return bigint格式的当前时间
   */
  public static long now() {
    return datetime(Calendar.getInstance());
  }
  public static int nowDate() {
    return date(Calendar.getInstance());
  }

  public static long datetime(Calendar calendar) {
    return calendar.get(Calendar.YEAR) * 10000000000000L
            + (calendar.get(Calendar.MONTH) + 1) * 100000000000L
            + calendar.get(Calendar.DAY_OF_MONTH) * 1000000000L
            + calendar.get(Calendar.HOUR_OF_DAY) * 10000000L
            + calendar.get(Calendar.MINUTE) * 100000L
            + calendar.get(Calendar.SECOND) * 1000L
            + calendar.get(Calendar.MILLISECOND)
            ;
  }

  public static int date(Calendar calendar) {
    return calendar.get(Calendar.YEAR) * 10000
            + (calendar.get(Calendar.MONTH) + 1) * 100
            + calendar.get(Calendar.DAY_OF_MONTH)
            ;
  }

  /**
   * 转换时间格式
   * @return bigint格式的时间
   */
  public static long datetime(long timeInMillis) {
    Calendar calendar = Calendar.getInstance();
    calendar.setTimeInMillis(timeInMillis);

    return datetime(calendar);
  }

  public static int date(long timeInMillis) {
    Calendar calendar = Calendar.getInstance();
    calendar.setTimeInMillis(timeInMillis);

    return date(calendar);
  }

  /**
   * 转换时间格式
   * @return bigint格式的时间
   */
  public static long datetime(Date date) {
    Calendar calendar = Calendar.getInstance();
    calendar.setTime(date);

    return datetime(calendar);
  }

  public static int date(Date date) {
    Calendar calendar = Calendar.getInstance();
    calendar.setTime(date);

    return date(calendar);
  }

  /**
   * 获取当天凌晨开始时间
   * @return bigint格式的时间
   */
  public static long dayStart() {
    Calendar calendar = Calendar.getInstance();

    return calendar.get(Calendar.YEAR) * 10000000000000L
            + (calendar.get(Calendar.MONTH) + 1) * 100000000000L
            + calendar.get(Calendar.DAY_OF_MONTH) * 1000000000L
            + calendar.get(Calendar.HOUR_OF_DAY) * 10000000L
            ;
  }

  public static Date toJavaDate(long numberTime) {
    if (numberTime >= MIN_VALUE) {
      Calendar calendar = getCalendar(numberTime);

      return calendar.getTime();
    }

    return null;
  }

  public static long toJavaTimeInMillis(long numberTime) {
    if (numberTime >= MIN_VALUE) {
      Calendar calendar = getCalendar(numberTime);

      return calendar.getTimeInMillis();
    }

    return 0;
  }

  public static java.sql.Date toSqlDate(long numberTime) {
    if (numberTime >= MIN_VALUE) {
      Calendar calendar = getCalendar(numberTime);
      calendar.set(Calendar.HOUR_OF_DAY, 0);
      calendar.set(Calendar.MINUTE, 0);
      calendar.set(Calendar.SECOND, 0);
      calendar.set(Calendar.MILLISECOND, 0);

      return new java.sql.Date(calendar.getTimeInMillis());
    }

    return null;
  }

  public static Timestamp toTimestamp(long numberTime) {
    if (numberTime >= MIN_VALUE) {
      Calendar calendar = getCalendar(numberTime);

      return new Timestamp(calendar.getTimeInMillis());
    }

    return null;
  }

  private static Calendar getCalendar(long numberTime) {
    int year =   (int) (numberTime / 10000000000000L);
    int month =  (int)((numberTime % 10000000000000L) / 100000000000L);
    int day =    (int)((numberTime % 100000000000L) / 1000000000L);
    int hour =   (int)((numberTime % 1000000000L) / 10000000L);
    int minute = (int)((numberTime % 10000000L) / 100000L);
    int second = (int)((numberTime % 100000L) / 1000L);
    int ms =     (int) (numberTime % 1000L);

    Calendar calendar = Calendar.getInstance();
    calendar.set(Calendar.YEAR, year);
    calendar.set(Calendar.MONTH, month - 1);
    calendar.set(Calendar.DAY_OF_MONTH, day);
    calendar.set(Calendar.HOUR_OF_DAY,hour);
    calendar.set(Calendar.MINUTE, minute);
    calendar.set(Calendar.SECOND, second);
    calendar.set(Calendar.MILLISECOND, ms);
    return calendar;
  }

  public static String format(long numberTime) {
    return DateUtil.format(toJavaDate(numberTime));
  }

  public static String format(long numberTime, String format) {
    return DateUtil.format(toJavaDate(numberTime), format);
  }
}
