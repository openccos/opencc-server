package com.openccos.framework.core.util;

import org.apache.commons.lang3.time.DateUtils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class DateUtil {
	private DateUtil(){}

	public static String format(Date date) {
		SimpleDateFormat longSdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

		return longSdf.format(date);
	}

	public static String format(Date date, String format) {
		SimpleDateFormat longSdf = new SimpleDateFormat(format);

		return longSdf.format(date);
	}

	// 获得当天0点时间
	public static long getTimesmorning() {
		return getTimesmorning(System.currentTimeMillis());
	}

	// 获得指定时间0点时间
	public static long getTimesmorning(long time) {
		Calendar cal = Calendar.getInstance();
		cal.setTimeInMillis(time);
		zeroTimeField(cal);
		return cal.getTimeInMillis();
	}

	// 设置时间字段为0
	public static void zeroTimeField(Calendar cal) {
		cal.set(Calendar.HOUR_OF_DAY, 0);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.MILLISECOND, 0);
	}

	// 获得昨天0点时间
	public static long getYesterdaymorning() {
		return getTimesmorning() - DateUtils.MILLIS_PER_DAY;
	}

	// 获得当天近7天时间
	public static long getWeekFromNow() {
		return getTimesmorning() - DateUtils.MILLIS_PER_DAY * 7;
	}

	// 获得当天24点时间
	public static long getTimesnight() {
		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.HOUR_OF_DAY, 24);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.MILLISECOND, 0);
		return cal.getTimeInMillis();
	}

	public static long getTimesnight(long time) {
		Calendar cal = Calendar.getInstance();
		cal.setTimeInMillis(time);
		cal.set(Calendar.HOUR_OF_DAY, 24);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.MILLISECOND, 0);
		return cal.getTimeInMillis();
	}

	// 获得本周一0点时间
	public static long getTimesWeekMorning() {
		Calendar cal = getCalendarWeekMorning();
		return cal.getTimeInMillis();
	}

	private static Calendar getCalendarWeekMorning() {
		Calendar cal = Calendar.getInstance();
		cal.set(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH), 0, 0, 0);
		cal.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
		cal.set(Calendar.MILLISECOND, 0);
		return cal;
	}

	// 获得本周日24点时间
	public static long getTimesWeekNight() {
		return getTimesWeekMorning() + DateUtils.MILLIS_PER_DAY * 7;
	}

	// 获得本月第一天0点时间
	public static long getTimesMonthMorning() {
//		Calendar cal = Calendar.getInstance();
//		cal.setTimeInMillis(System.currentTimeMillis());
//		cal.set(cal.get(Calendar.YEAR), cal.get(Calendar.MONDAY), cal.get(Calendar.DAY_OF_MONTH), 0, 0, 0);
//		cal.set(Calendar.DAY_OF_MONTH, cal.getActualMinimum(Calendar.DAY_OF_MONTH));
//		return cal.getTimeInMillis();
		Calendar cal = getCalenderMonth(System.currentTimeMillis(), Calendar.DAY_OF_MONTH);
		return cal.getTimeInMillis();
	}

	// 获得本月第一天0点的 Calender
	public static Calendar getCalenderMonthMorning() {
		return getCalenderMonth(System.currentTimeMillis(), Calendar.DAY_OF_MONTH);
	}

	private static Calendar getCalenderMonth(long ts, int dayOfMonth) {
		Calendar cal = Calendar.getInstance();
		cal.setTimeInMillis(ts);
		cal.set(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH), 0, 0, 0);
		cal.set(Calendar.DAY_OF_MONTH, cal.getActualMinimum(dayOfMonth));
		cal.set(Calendar.MILLISECOND, 0);
		return cal;
	}

	// 获得本月最后一天24点时间
	public static long getTimesMontgHight() {
		Calendar cal = Calendar.getInstance();
		cal.setTimeInMillis(System.currentTimeMillis());
		cal.set(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH), 0, 0, 0);
		cal.set(Calendar.DAY_OF_MONTH, cal.getActualMaximum(Calendar.DAY_OF_MONTH));
		cal.set(Calendar.HOUR_OF_DAY, 24);
		cal.set(Calendar.MILLISECOND, 0);
		return cal.getTimeInMillis();
	}

//	public static long getLastMonthStartMorning() {
//		Calendar cal = Calendar.getInstance();
//		cal.setTimeInMillis(getTimesMonthMorning());
//		cal.add(Calendar.MONTH, -1);
//		return cal.getTimeInMillis();
//	}

//	public static long getLastMonthStartMorning(long timeInMillis) {
//		Calendar cal = Calendar.getInstance();
//		cal.setTimeInMillis(timeInMillis);
//		cal.add(Calendar.MONTH, -1);
//		return cal.getTimeInMillis();
//	}

	public static Date getCurrentQuarterStartTime() {
		Calendar c = Calendar.getInstance();
		c.setTimeInMillis(getTimesMonthMorning());
		int currentMonth = c.get(Calendar.MONTH) + 1;
		SimpleDateFormat longSdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		SimpleDateFormat shortSdf = new SimpleDateFormat("yyyy-MM-dd");
		Date now = null;
		try {
			if (currentMonth <= 3) {
				c.set(Calendar.MONTH, 0);
			} else if (currentMonth <= 6) {
				c.set(Calendar.MONTH, 3);
			} else if (currentMonth <= 9) {
				c.set(Calendar.MONTH, 4);
			} else if (currentMonth <= 12) {
				c.set(Calendar.MONTH, 9);
			}

			c.set(Calendar.DATE, 1);
			now = longSdf.parse(shortSdf.format(c.getTime()) + " 00:00:00");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return now;
	}

	/**
	 * 当前季度的结束时间，即2012-03-31 23:59:59
	 *
	 * @return
	 */
	public static Date getCurrentQuarterEndTime() {
		Calendar cal = Calendar.getInstance();
		cal.setTime(getCurrentQuarterStartTime());
		cal.add(Calendar.MONTH, 3);
		return cal.getTime();
	}

	public static long getCurrentYearStartTime() {
		Calendar cal = getCalenderMonth(getTimesMonthMorning(), Calendar.YEAR);
		return cal.getTimeInMillis();
	}

	public static long getCurrentYearEndTime() {
		Calendar cal = Calendar.getInstance();
		cal.setTimeInMillis(getCurrentYearStartTime());
		cal.add(Calendar.YEAR, 1);
		return cal.getTimeInMillis();
	}

	public static long getLastYearStartTime() {
		Calendar cal = Calendar.getInstance();
		cal.setTimeInMillis(getCurrentYearStartTime());
		cal.add(Calendar.YEAR, -1);
		return cal.getTimeInMillis();
	}

	public static long getLastYearStartTime(long timeInMillis) {
		Calendar cal = Calendar.getInstance();
		cal.setTimeInMillis(timeInMillis);
		cal.add(Calendar.YEAR, -1);
		return cal.getTimeInMillis();
	}

	public static String parseTimeTag(Date date){
		if(date == null){
			return "";
		}
		int now = date2Day(new Date());
		int day = date2Day(date);
		int deDay = now - day;
//		int deMonth = now/100 - day/100;
		int deYear = now/10000 - day/10000;
		if(deYear < 1){
			switch(deDay){
				case 0: return new SimpleDateFormat("HH:mm").format(date);
				case 1: return "昨天";
				default:
					return new SimpleDateFormat("MM-dd").format(date);
			}
		}
		return new SimpleDateFormat("yyyy-MM-dd").format(date);
	}

	private static int date2Day(Date date){
		return Integer.parseInt(new SimpleDateFormat("yyyyMMdd").format(date));
	}
}
