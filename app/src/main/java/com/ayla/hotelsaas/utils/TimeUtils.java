package com.ayla.hotelsaas.utils;

import android.annotation.SuppressLint;
import android.text.TextUtils;
import android.util.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by user on 2016/7/12.
 */
public class TimeUtils {

    public static final long ONE_SECOND = 1000;
    public static final long ONE_MINUTE = ONE_SECOND * 60;
    public static final long ONE_HOUR = ONE_MINUTE * 60;
    public static final long ONE_DAY = ONE_HOUR * 24;
    public static final long ONE_MOUTH = ONE_DAY * 30;
    public static final long ONE_YEAR = ONE_DAY * 365;
    public static final String WHOLE_TIME = "yyyy-MM-dd HH:mm:ss";
    public static final String YEAR_MONTH_DAY_HOUR_MINS = "yyyy-MM-dd HH:mm";
    public static final String YEAR_MONTH_DAY = "yyyy-MM-dd";
    public static final String YEAR_MONTH = "yyyy-MM";
    public static final String TIME_WEEK = "M月d日 EEE";
    public static final String TIME_WEEK2 = "M月d日(EEE)";
    public static long nd = 1000 * 24L * 60L * 60L;//一天的毫秒数

    @SuppressLint("SimpleDateFormat")
    public static String getdetailTime(long timeStamp) {
        Date date1 = new Date(timeStamp);
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        String dateString = format.format(date1);
        return dateString;
    }

    @SuppressLint("SimpleDateFormat")
    public static String getdetailTime2(long timeStamp) {
        Date date1 = new Date(timeStamp);
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String dateString = format.format(date1);
        return dateString;
    }

    @SuppressLint("SimpleDateFormat")
    public static String getdetailTime3(long timeStamp) {
        Date date1 = new Date(timeStamp);
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        String dateString = format.format(date1);
        return dateString;
    }
    public static String getSpecifiedTime(String spDate, String formatStr) {
        SimpleDateFormat simformat = new SimpleDateFormat(YEAR_MONTH_DAY);
        SimpleDateFormat simformat2 = new SimpleDateFormat(formatStr);
        try {
            Date date1 = simformat.parse(spDate);
           return simformat2.format(date1);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return "";
    }
    public static String getSpecifiedTime(String timeStr, String inFormat, String outFormat) {
        SimpleDateFormat simformat = new SimpleDateFormat(inFormat);
        SimpleDateFormat simformat2 = new SimpleDateFormat(outFormat);
        try {
            Date date1 = simformat.parse(timeStr);
            return simformat2.format(date1);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return "";
    }


    public static String getDateTime(Date date) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        return sdf.format(date);
    }
    public static String getDateTime(Date date, String format) {
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        return sdf.format(date);
    }

    @SuppressLint("SimpleDateFormat")
    public static String getYestoday(String dateTime) {
        if (TextUtils.isEmpty(dateTime)){
            return "";
        }
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        Date date2 = null;
        String resutStr = "";
        try {
            date2 = format.parse(dateTime);
            resutStr = getdetailTime(date2.getTime() - 1000 * 60 * 60 * 24);
        } catch (ParseException e) {
        }
        return resutStr;
    }

    @SuppressLint("SimpleDateFormat")
    public static String getNextDay(String dateTime) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        Date date2 = null;
        String resutStr = "";
        try {
            date2 = format.parse(dateTime);
            resutStr = getdetailTime(date2.getTime() + 1000 * 60 * 60 * 24);
        } catch (ParseException e) {
        }
        return resutStr;
    }

    public static int getsumDays(String startDate, String endDate) {
        int days = 0;
        SimpleDateFormat dateFormat2 = new SimpleDateFormat(YEAR_MONTH_DAY);
        try {
            long start = dateFormat2.parse(startDate).getTime();
            long end = dateFormat2.parse(endDate).getTime();
            days = (int) ((end - start) / nd);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return days;
    }

    public static String getEarly4Hours(String time) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:00:00");
        Date date2 = null;
        String resutStr = "";
        try {
            date2 = format.parse(time);
            resutStr = format.format(date2.getTime() - 1000 * 60 * 60 * 3);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return resutStr;
    }

    @SuppressLint("SimpleDateFormat")
    public static String getTomorrow() {
        Date date1 = new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 24);
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        String dateString = format.format(date1);
        return dateString;
    }

    public static String getTrainDateTomorrow(String trainDate) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        try {
            Date date = format.parse(trainDate);
            date.setTime(date.getTime() + 1000 * 60 * 60 * 24);
            return format.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

    @SuppressLint("SimpleDateFormat")
    public static String getToday() {
        Date date1 = new Date(System.currentTimeMillis());
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        String dateString = format.format(date1);
        return dateString;
    }

    @SuppressLint("SimpleDateFormat")
    public static String getOneWeekLater() {
        Date date1 = new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 24 * 7);
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        String dateString = format.format(date1);
        return dateString;
    }

    @SuppressLint("SimpleDateFormat")
    public static int getDD(long timeStamp) {
        Date date1 = new Date(timeStamp);
        SimpleDateFormat format = new SimpleDateFormat("dd");
        String dateString = format.format(date1);
        return Integer.valueOf(dateString);
    }

    @SuppressLint("SimpleDateFormat")
    public static long getyyyyMMdd(long timeStamp) {
        Date date1 = new Date(timeStamp);
        SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");
        String dateString = format.format(date1);
        return Long.valueOf(dateString);
    }

    @SuppressLint("SimpleDateFormat")
    public static long getyyyyMMddHHmmss(long timeStamp) {
        Date date1 = new Date(timeStamp);
        SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmmss");
        String dateString = format.format(date1);
        return Long.valueOf(dateString);
    }

    @SuppressLint("SimpleDateFormat")
    public static long getyyyyMMddHHmmssSSS(long timeStamp) {
        Date date1 = new Date(timeStamp);
        SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmmssSSS");
        String dateString = format.format(date1);
        return Long.valueOf(dateString);
    }

    @SuppressLint("SimpleDateFormat")
    public static int getDayOfYear(long timeStamp) {
        Date date1 = new Date(timeStamp);
        Calendar cal = Calendar.getInstance();
        cal.setTime(date1);
        return cal.get(Calendar.DAY_OF_YEAR);
    }

    /**
     * 获取时间戳
     * @param date
     * @return
     */
    public static long getTimeStamp(String date){
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        long timeStamp = 0;
        try{
            timeStamp = simpleDateFormat.parse(date).getTime();
        }catch(Exception e){
            e.printStackTrace();
        }

        return timeStamp;
    }


    public static long getTimeStamp2(String date){
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        long timeStamp = 0;
        try{
            timeStamp = simpleDateFormat.parse(date).getTime();
        }catch(Exception e){
            e.printStackTrace();
        }

        return timeStamp;
    }

    public static long getTimeStamp3(String date){
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        long timeStamp = 0;
        try{
            timeStamp = simpleDateFormat.parse(date).getTime();
        }catch(Exception e){
            e.printStackTrace();
        }

        return timeStamp;
    }

    public static long getTimeStamp4(String date){
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        long timeStamp = 0;
        try{
            timeStamp = simpleDateFormat.parse(date).getTime();
        }catch(Exception e){
            e.printStackTrace();
        }

        return timeStamp;
    }

    public static long getTimeStamp5(String date){
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        long timeStamp = 0;
        try{
            timeStamp = simpleDateFormat.parse(date).getTime();
        }catch(Exception e){
            e.printStackTrace();
        }

        return timeStamp;
    }

    public static long getTrainaBeginStamp(String date, String timeFormate){
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(timeFormate);
        long timeStamp = 0;
        try{
            timeStamp = simpleDateFormat.parse(date).getTime();
        }catch(Exception e){
            e.printStackTrace();
        }

        return timeStamp;
    }


    @SuppressLint("SimpleDateFormat")
    public static int getYear(long timeStamp) {
        Date date1 = new Date(timeStamp);
        Calendar cal = Calendar.getInstance();
        cal.setTime(date1);
        return cal.get(Calendar.YEAR);
    }


    /**
     * 获取目标时间和当前时间之间的差距
     *
     * @param date
     * @return
     */
    public static String getTimestampString(Date date) {
        Date curDate = new Date();
        long splitTime = curDate.getTime() - date.getTime();
        if (splitTime < ONE_YEAR) {
            if (splitTime < ONE_MINUTE) {
                return "刚刚";
            }
            if (splitTime < ONE_HOUR) {
                return String.format("%d分钟前", splitTime / ONE_MINUTE);
            }

            if (splitTime < ONE_DAY) {
                return String.format("%d小时前", splitTime / ONE_HOUR);
            }
//            if (splitTime < ONE_MOUTH) {
            return String.format("%d天前", splitTime / ONE_DAY);
//            }
//            return String.format("%d月前", splitTime / ONE_MOUTH);
        }
        return String.format("%d年前", splitTime / ONE_YEAR);
    }

    /**
     * String 转换 Date
     *
     * @param str
     * @param format
     * @return
     */
    public static Date string2Date(String str, String format) {
        try {
            return new SimpleDateFormat(format).parse(str);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return new Date();
    }

    /**
     * 将接口返回的日期格式转换成需要的格式
     *
     * @param time
     * @return
     */
    public static String getyyyy_MM_dd(String time) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy/MM/dd");
        Date date2 = null;
        try {
            date2 = format.parse(time);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return date2Format(date2, "yyyy-MM-dd");
    }

    public static String getMMdd(String time) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        Date date2 = null;
        try {
            date2 = format.parse(time);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return date2Format(date2, "MM-dd");
    }

    public static String getYYMMdd(String time) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        Date date2 = null;
        try {
            date2 = format.parse(time);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return date2Format(date2, "yyyy-MM-dd");
    }

    public static String getMMdd2(String time) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy/MM/dd");
        Date date2 = null;
        try {
            date2 = format.parse(time);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return date2Format(date2, "MM-dd");
    }


    public static String date2Format(Date date, String format) {
        return new SimpleDateFormat(format).format(date);
    }


    public static String getDateDay(Date date) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd");
        return sdf.format(date);
    }

    public static String getJavaTime(String timeString) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-ddHH:mm:ss");
//        if(TextUtils.isEmpty( timeString)){
//            timeString = "1900-00-00 00:00" ;
//        }
        Date date = new Date();
        try {
            date = format.parse(timeString);
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return getSecondTime(date.getTime());
    }

    public static String getSecondTime(long timeStamp) {
        Date date1 = new Date(timeStamp);
        SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmmss");
        String dateString = format.format(date1);
        return dateString;
    }

    public static String getStandardDate(long timeStamp) {
        Date date1 = new Date(timeStamp);
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        String dateString = format.format(date1);
        return dateString;
    }

    public static String getTomorrowWeekDay(String date) {
        long dateLong = Long.valueOf(date.substring(0, 4) + date.substring(5, 7) + date.substring(8, 10));
        if (date.equals(getdetailTime(System.currentTimeMillis()))) {
            return "今天";
        } else if (dateLong - getyyyyMMdd(System.currentTimeMillis()) == 1) {
            return "明天";
        } else if (dateLong - getyyyyMMdd(System.currentTimeMillis()) == 2) {
            return "后天";
        } else {
            return getWeekDay(date);
        }
    }

    public static String getWeekDay(String date) {
        String Week = "周";
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");//也可将此值当参数传进来

        Calendar c = Calendar.getInstance();
        try {
            c.setTime(format.parse(date));
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        switch (c.get(Calendar.DAY_OF_WEEK)) {
            case 1:
                Week += "日";
                break;
            case 2:
                Week += "一";
                break;
            case 3:
                Week += "二";
                break;
            case 4:
                Week += "三";
                break;
            case 5:
                Week += "四";
                break;
            case 6:
                Week += "五";
                break;
            case 7:
                Week += "六";
                break;
            default:
                break;
        }
        return Week;
    }

    public static String getWeekDay2(String date) {
        String Week = "周";
        SimpleDateFormat format = new SimpleDateFormat("yyyy/MM/dd");//也可将此值当参数传进来

        Calendar c = Calendar.getInstance();
        try {
            c.setTime(format.parse(date));
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        switch (c.get(Calendar.DAY_OF_WEEK)) {
            case 1:
                Week += "日";
                break;
            case 2:
                Week += "一";
                break;
            case 3:
                Week += "二";
                break;
            case 4:
                Week += "三";
                break;
            case 5:
                Week += "四";
                break;
            case 6:
                Week += "五";
                break;
            case 7:
                Week += "六";
                break;
            default:
                break;
        }
        return Week;
    }

    /**
     * @param date "yyyy-MM-dd"格式
     * @return mm月dd日的形式
     */
    public static String getHYDate(String date) {
        Calendar c = Calendar.getInstance();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        StringBuilder sb = new StringBuilder();
        try {
            c.setTime(format.parse(date));
            int mm = c.get(Calendar.MONTH) + 1;
            String mmStr = mm < 10 ? "" + 0 + mm : mm + "";
            sb.append(mmStr);
            sb.append("月");
            int dd = c.get(Calendar.DAY_OF_MONTH);
            String ddStr = dd < 10 ? "" + 0 + dd : dd + "";
            sb.append(ddStr);
            sb.append("日");
            return sb.toString();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }
    /**
     * @param date "yyyy-MM-dd"格式
     * @return mm月dd日的形式
     */
    public static String getHY_Date(String date) {
        Calendar c = Calendar.getInstance();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        StringBuilder sb = new StringBuilder();
        try {
            c.setTime(format.parse(date));
            int mm = c.get(Calendar.MONTH) + 1;
            String mmStr = mm < 10 ? "" + 0 + mm : mm + "";
            sb.append(mmStr);
            sb.append("/");
            int dd = c.get(Calendar.DAY_OF_MONTH);
            String ddStr = dd < 10 ? "" + 0 + dd : dd + "";
            sb.append(ddStr);
            return sb.toString();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * @param date "yyyy-MM-dd"格式
     * @return mm月dd日的形式
     */
    public static String getDateHY(String date) {
        Calendar c = Calendar.getInstance();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        StringBuilder sb = new StringBuilder();
        try {
            c.setTime(format.parse(date));
            int mm = c.get(Calendar.MONTH) + 1;
            String mmStr = mm < 10 ? "" + 0 + mm : mm + "";
            sb.append(mmStr);
            sb.append("-");
            int dd = c.get(Calendar.DAY_OF_MONTH);
            String ddStr = dd < 10 ? "" + 0 + dd : dd + "";
            sb.append(ddStr);
            return sb.toString();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 日期比较，返回两个日期差的天数
     *
     * @param date1 较早的 格式 yyyy-MM-dd
     * @param date2 较晚的
     * @return date2 - date1所差的天数
     */
    public static int compareDay(String date1, String date2) {
        int delta = 0 ;
        int yyyy1 = Integer.parseInt(date1.substring(0, 4));
        int yyyy2 = Integer.parseInt(date2.substring(0, 4));
        Log.d("TAG", "compareDay: " + date1);
        int mm1 = Integer.parseInt(date1.substring(5, 7));
        int mm2 = Integer.parseInt(date2.substring(5, 7));
        int dd1 = Integer.parseInt(date1.substring(8, 10));
        int dd2 = Integer.parseInt(date2.substring(8, 10));
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        if (yyyy1 == yyyy2) {
            try {
                int dayOfYear1 = getDayOfYear(format.parse(date1).getTime());
                int dayOfYear2 = getDayOfYear(format.parse(date2).getTime());
                delta = dayOfYear2 - dayOfYear1;
            } catch (ParseException e) {
                e.printStackTrace();
            }
        } else {
            if (yyyy1 % 4 == 0 && yyyy1 % 100 != 0 || (yyyy1 % 400 == 0)) {
                try {
                    delta =  getDelta(366, date1, date2, format);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            } else {
                try {
                    delta =  getDelta(365, date1, date2, format);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
        }
        return delta;
    }

    private static int getDelta(int i, String date1, String date2, SimpleDateFormat format) throws ParseException {
        return i - getDayOfYear(format.parse(date1).getTime()) + getDayOfYear(format.parse(date2).getTime());
    }

    /**
     * <li>功能描述：时间相减得到天数
     * @param beginDateStr
     * @param endDateStr
     * @return
     * long
     * @author Administrator
     */
    public static long getDaySub(String beginDateStr, String endDateStr)
    {
        long day=0;
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        Date beginDate;
        Date endDate;
        try
        {
            beginDate = format.parse(beginDateStr);
            endDate= format.parse(endDateStr);
            day=(endDate.getTime()-beginDate.getTime())/(24*60*60*1000);
            //System.out.println("相隔的天数="+day);
        } catch (ParseException e)
        {
            // TODO 自动生成 catch 块
            e.printStackTrace();
        }
        return day;
    }

    /**
     * 计算飞行时间
     * @param duration
     * @return
     */
    public static String getFlyDuration(int duration){
        if(duration >= 60){
            if(duration % 60 == 0){
                return (duration / 60) + "小时";
            }else{
                return (duration / 60) + "小时" + (duration % 60) + "分";
            }
        }else{
            return duration + "分";
        }
    }

    public static String getTrainDuration(int duration){
        if(duration >= 60){
                return (duration / 60) + "小时" + (duration % 60) + "分";
        }else{
            return duration + "分";
        }
    }
    public static String getTrainDuration(String time){
        SimpleDateFormat sformat = new SimpleDateFormat("HH:mm");
        try {
            Date date=sformat.parse(time);
            Date date2=sformat.parse("00:00");
            return getTrainDuration((int) ((date.getTime()-date2.getTime())/60000));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return "";
    }

    public static String getFlyDuration(String time){
        SimpleDateFormat sformat = new SimpleDateFormat("HH:mm");
        try {
            Date date=sformat.parse(time);
            Date date2=sformat.parse("00:00");
            return getFlyDuration((int) ((date.getTime()-date2.getTime())/60000));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return "";
    }

    /**
     * 计算飞行时间
     * @param duration
     * @return
     */
    public static String getInternationalFlyDuration(String duration){
        if(duration.contains("h") && duration.contains("m")){
            duration = duration.replace("h","时");
            duration = duration.replace("m","分");
            return duration;
        }else if(duration.contains("h") && !duration.contains("m")){
            duration = duration.replace("h","时");
            return duration;
        }else if(!duration.contains("h") && duration.contains("m")){
            duration = duration.replace("m","分");
            return duration;
        }else{
            return duration;
        }

    }
    /*
       *时间差的计算
       *返回 小时 分钟 格式
     */
    public static String getTimeDifference(String starTime, String endTime) {
        String timeString = "";
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        Date parse = null;
        try {
            parse = dateFormat.parse(starTime);
            Date parse1 = dateFormat.parse(endTime);

            long diff = parse1.getTime() - parse.getTime();

            long day = diff / (24 * 60 * 60 * 1000);
            long hour = (diff / (60 * 60 * 1000) - day * 24);
            long min = ((diff / (60 * 1000)) - day * 24 * 60 - hour * 60);
            long s = (diff / 1000 - day * 24 * 60 * 60 - hour * 60 * 60 - min * 60);
            long ms = (diff - day * 24 * 60 * 60 * 1000 - hour * 60 * 60 * 1000
                    - min * 60 * 1000 - s * 1000);

            long hour1 = diff / (60 * 60 * 1000);
            String hourString = hour1 + "";
            long min1 = ((diff / (60 * 1000)) - hour1 * 60);
            timeString = hour1 + "时" + min1 + "分";


        } catch (ParseException e) {
            e.printStackTrace();
        }


        return timeString;

    }

    /*
     *时间差的计算
     *返回 分钟
     */
    public static int getTimeDiffMin(String starTime, String endTime) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        Date parse = null;
        try {
            parse = dateFormat.parse(starTime);
            Date parse1 = dateFormat.parse(endTime);

            long diff = parse1.getTime() - parse.getTime();
            return (int) (diff / (60 * 1000));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return -1;
    }

    public static int getTimeDiffSec(String starTime, String endTime) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        Date parse = null;
        try {
            parse = dateFormat.parse(starTime);
            Date parse1 = dateFormat.parse(endTime);

            long diff = parse1.getTime() - parse.getTime();
            return (int) (diff / 1000);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return -1;
    }

    public static int getReturnTimeDiffSec(String starTime, String endTime) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date parse = null;
        try {
            parse = dateFormat.parse(starTime);
            Date parse1 = dateFormat.parse(endTime);

            long diff = parse1.getTime() - parse.getTime();
            return (int) (diff / 1000);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return -1;
    }


    //判断对应的人是否满了大于和等于18岁。
    public static boolean checkAgeEighteenAdult(String Birty_Day) throws ParseException {
        Calendar current = Calendar.getInstance();
        Calendar birthDay = Calendar.getInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date parse = dateFormat.parse(Birty_Day);
        birthDay.setTime(parse);
        Integer year = current.get(Calendar.YEAR) - birthDay.get(Calendar.YEAR);
        if (year > 18) {
            return true;
        } else if (year < 18) {
            return false;
        }
        // 如果年相等，就比较月份
        Integer month = current.get(Calendar.MONTH) - birthDay.get(Calendar.MONTH);
        if (month > 0) {
            return true;
        } else if (month < 0) {
            return false;
        }
        // 如果月也相等，就比较天
        Integer day = current.get(Calendar.DAY_OF_MONTH) - birthDay.get(Calendar.DAY_OF_MONTH);
        return day >= 0;
    }


    //判断对应的人是否满了大于80岁。
    public static boolean checkAgeEightyAdult(String Birty_Day) throws ParseException {
        Calendar current = Calendar.getInstance();
        Calendar birthDay = Calendar.getInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date parse = dateFormat.parse(Birty_Day);
        birthDay.setTime(parse);
        Integer year = current.get(Calendar.YEAR) - birthDay.get(Calendar.YEAR);
        if (year > 81) {
            return true;
        } else if (year < 81) {
            return false;
        }
        // 如果年相等，就比较月份
        Integer month = current.get(Calendar.MONTH) - birthDay.get(Calendar.MONTH);
        if (month > 0) {
            return true;
        } else if (month < 0) {
            return false;
        }
        // 如果月也相等，就比较天
        Integer day = current.get(Calendar.DAY_OF_MONTH) - birthDay.get(Calendar.DAY_OF_MONTH);
        return day >= 0;
    }

    //退改签秒转为日期
    public static long  getDateMethod(String flay_time, String end_time) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date parse_one = null;
        long  retrun_date= 0;
        try {
            parse_one = dateFormat.parse(flay_time);
            if (end_time.contains("#")){
                String replace_end_time = end_time.replace("#", "");
                long num = Long.parseLong(replace_end_time);
                long  end_time_num = num * 3600 * 1000;
                long diff = parse_one.getTime() + end_time_num;
                retrun_date =diff;
            }else if (end_time.contains("*")){
                end_time.replace("*","");
                String replace_end_time = end_time.replace("*", "");
                long num = Long.parseLong(replace_end_time);
                long  end_time_num = num * 3600 * 1000;
                long diff = parse_one.getTime() + end_time_num;
                retrun_date = diff;
            }else {
                long num = Long.parseLong(end_time);
                long  end_time_num = num * 3600 * 1000;
                long diff = parse_one.getTime() + end_time_num;
                retrun_date = diff;
            }
            return retrun_date;
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return retrun_date;
    }

    public static String getLongToDate(long timeStamp) {
        Date date1 = new Date(timeStamp);
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String dateString = format.format(date1);
        return dateString;
    }


    public static Date getTimeDate(String time){
        SimpleDateFormat sformat = new SimpleDateFormat(YEAR_MONTH_DAY);
        try {
            Date date=sformat.parse(time);
            return date;
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

}
