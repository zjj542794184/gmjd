
package com.tellhow.common.util;

import java.io.PrintStream;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

public class DateUtil
{
  private static final int[] dayArray = { 31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31 };

  public static String defaultDateFormat = "yyyy-MM-dd";

  public static String defaultTimeFormat = "yyyy-MM-dd HH:mm:ss";

  private static int weeks = 0;

  private static final int getMondayPlus()
  {
    Calendar cd = Calendar.getInstance();

    int dayOfWeek = cd.get(7);
    if (dayOfWeek == 1) {
      return -6;
    }
    return 2 - dayOfWeek;
  }

  public static Date getDateBefore(Date d, int day)
  {
    Calendar now = Calendar.getInstance();
    now.setTime(d);
    now.set(5, now.get(5) - day);
    return now.getTime();
  }

  public static final String getCurrentMonday()
  {
    weeks = 0;
    int mondayPlus = getMondayPlus();
    GregorianCalendar currentDate = new GregorianCalendar();
    currentDate.add(5, mondayPlus);
    Date monday = currentDate.getTime();
    DateFormat df = DateFormat.getDateInstance();
    String preMonday = df.format(monday);
    return preMonday;
  }

  public static final String addonemonth(String selectstatetime)
    throws ParseException
  {
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
    Date date = sdf.parse(selectstatetime);
    Calendar calender = Calendar.getInstance();
    calender.setTime(date);
    calender.add(2, 1);
    String enddate = sdf.format(calender.getTime());
    return enddate;
  }

  public static final String formatCurrentTime(String pattern)
  {
    return transferDateToString(new Date(), pattern);
  }

  public static final String transferDateToString(Date formatDate)
  {
    return transferDateToString(formatDate, null);
  }

  public static final String transferDateToString(Date formatDate, String pattern)
  {
    if (formatDate == null) {
      throw new IllegalArgumentException("日期对象参数不能为空");
    }
    pattern = isEmpty(pattern) ? defaultDateFormat : pattern;
    SimpleDateFormat formatter = new SimpleDateFormat(pattern);

    return formatter.format(formatDate);
  }

  public static final Date transferStringToDate(String strDate)
  {
    return transferStringToDate(strDate, null);
  }

  public static final Date transferStringToDate(String strDate, String pattern)
  {
    if (strDate == null) {
      throw new IllegalArgumentException("日期格式字符串不能为空");
    }
    pattern = isEmpty(pattern) ? defaultDateFormat : pattern;
    SimpleDateFormat formatter1 = new SimpleDateFormat(pattern);
    try {
      return formatter1.parse(strDate); } catch (Exception e) {
    }
    throw new RuntimeException("日期字符串格式错误");
  }

  public static final String formatDate(String value)
  {
    SimpleDateFormat md = new SimpleDateFormat("yyyy-MM-dd");
    Date d = null;
    try {
      d = md.parse(value);
    } catch (ParseException e) {
      md.applyPattern("yyyy/MM/dd");
      try {
        d = md.parse(value);
      } catch (ParseException e1) {
        e1.printStackTrace();
        throw new RuntimeException("日期字符串格式错误", e1);
      }
    }
    md.applyPattern("yyyy-MM-dd");
    value = md.format(d);
    return value;
  }

  public static int getCurrentYear()
  {
    return getTimeField(Calendar.getInstance().getTime(), 1);
  }

  public static int getCurrentMonth()
  {
    return getTimeField(Calendar.getInstance().getTime(), 2);
  }

  public static int getCurrentDay()
  {
    return getTimeField(Calendar.getInstance().getTime(), 5);
  }

  public static int getTimeField(Date date, int field)
  {
    Calendar cal = Calendar.getInstance();
    cal.setTime(date);

    return getTimeField(cal, field);
  }

  public static int getTimeField(Calendar cal, int field)
  {
    int fieldValue = cal.get(field);
    fieldValue = field == 2 ? fieldValue + 1 : fieldValue;
    return fieldValue;
  }

  public static int getLastDayOfMonth(int month)
  {
    if ((month < 1) || (month > 12)) {
      return -1;
    }
    int retn = 0;
    if ((month == 2) && (isLeapYear()))
      retn = 29;
    else {
      retn = dayArray[(month - 1)];
    }
    return retn;
  }

  public static int getLastDayOfMonth(int year, int month)
  {
    if ((month < 1) || (month > 12)) {
      return -1;
    }
    int retn = 0;
    if ((month == 2) && (isLeapYear(year)))
      retn = 29;
    else {
      retn = dayArray[(month - 1)];
    }
    return retn;
  }

  public static int getLastDayOfMonth(Date date)
  {
    Calendar cal = Calendar.getInstance();
    cal.setTime(date);
    return getLastDayOfMonth(cal);
  }

  public static int getLastDayOfMonth(Calendar cal)
  {
    int year = getTimeField(cal, 1);
    int month = getTimeField(cal, 2);

    if ((month < 1) || (month > 12)) {
      return -1;
    }
    int retn = 0;
    if ((month == 2) && (isLeapYear(year)))
      retn = 29;
    else {
      retn = dayArray[(month - 1)];
    }
    return retn;
  }

  public static boolean isLeapYear()
  {
    Calendar cal = Calendar.getInstance();
    int year = cal.get(1);
    return isLeapYear(year);
  }

  public static boolean isLeapYear(int year)
  {
    return ((year % 4 == 0) && (year % 100 != 0)) || (year % 400 == 0);
  }

  public static Date backYears(Date curDate, int backYearNum)
  {
    Calendar cal = Calendar.getInstance();
    cal.setTime(curDate);
    int curYearNum = cal.get(1);
    int curDayofmonth = cal.get(5);

    if ((isLeapYear(curYearNum)) && (cal.get(2) == 1) && (curDayofmonth == 29) && (!isLeapYear(curYearNum - backYearNum))) {
      cal.set(5, 28);
    }
    cal.set(1, curYearNum - backYearNum);
    return new Date(cal.getTimeInMillis());
  }

  public static Date addDays(Date curDate, int addDaysNum)
  {
    long curDateMills = curDate.getTime();
    long addDaysMills = addDaysNum * 24 * 3600 * 1000;
    return new Date(curDateMills + addDaysMills);
  }

  public static String addStringDays(String curDate, int addDaysNum)
  {
    Date curdate = transferStringToDate(curDate);
    Date adddate = addDays(curdate, addDaysNum);
    String addenddate = transferDateToString(adddate);
    return addenddate;
  }

  private static boolean isEmpty(String value)
  {
    return (value == null) || (value.trim().equals(""));
  }

  public static List<String> getWeeks(int year, int month)
  {
    List list = new ArrayList();
    int weeknum = 0;
    int week = 0;
    Calendar calendar = Calendar.getInstance();
    calendar.set(1, year);
    calendar.set(2, month - 1);
    int day = calendar.getActualMaximum(5);
    for (int i = 1; i <= day; i++) {
      calendar.set(5, i);
      if (calendar.get(7) == 2) {
        if (weeknum == 0)
        {
          week = calendar.get(3);
        }
        list.add(week + weeknum);
        weeknum++;
      }
    }
    return list;
  }

  public static String getWeekOfDate(Date dt)
  {
    String[] weekDays = { "星期日", "星期一", "星期二", "星期三", "星期四", "星期五", "星期六" };
    Calendar cal = Calendar.getInstance();
    cal.setTime(dt);
    int w = cal.get(7) - 1;
    if (w < 0)
      w = 0;
    return weekDays[w];
  }

  public static void main(String[] args) {
    Date date = transferStringToDate("2008-2-29");

    List<String> weeks = getWeeks(2012, 9);
    String sql = "select t.* from OA_BOSSSCHEDULE t where t.state=2 ";
    for (String mon : weeks) {
      sql = sql + "( t.arrangementtime like '%" + mon + "%'" + " or ";
    }
    sql = sql.substring(0, sql.lastIndexOf("or"));
    System.out.println(sql + ")");
  }
}
