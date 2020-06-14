package com.hgo.planassistant.tools;

import android.util.Log;

import java.util.Calendar;
import java.util.Date;

public class DateFormat {

    Calendar date = Calendar.getInstance();
    Date date_date = new Date();
    int year;
    int month;
    int day;
    int hour;
    int minute;
    int second;
    int week; //周日为1

    public String DateFormat(Calendar in, Boolean input){
        date = in;
        date_date = in.getTime();
        initDate(date);
        if(input)
            return GetDetailDescription();
        else
            return null;
    }

    public DateFormat(Calendar in){
        if(in!=null){
            date = in;
            date_date = in.getTime();
            initDate(date);
        }else{
            Log.i("DateFormat","输入为空！");
        }
    }

    public DateFormat(){
    }

    public DateFormat(Date in){
        date = Calendar.getInstance();
        date.setTime(in);
        date_date = in;
        initDate(date);
    }

    public String GetDetailDescription(){
        return year + "年" + month + "月" + day + "日 " + hour + ":" + minute + ":" + second;
    }

    public String GetDetailDescription(Calendar in){
        if(in!=null){
            date = in;
            initDate(date);
            return year + "年" + month + "月" + day + "日" + getChineseWeek(week) + " " + hour + ":" + minute + ":" + second;
        }else{
            Log.i("DateFormat","输入为空！");
            return null;
        }
    }

    public String GetDetailDescription(Date in){
        if(in!=null){
            date.setTime(in);
            initDate(date);
            return year + "年" + month + "月" + day + "日" + getChineseWeek(week) + " " + hour + ":" + minute + ":" + second;
        }else{
            Log.i("DateFormat","输入为空！");
            return null;
        }
    }

    public String GetHourAndMinuteDetailDescription(){
        return hour + ":" + minute;
    }
    public String GetHourAndMinuteDetailDescription(Calendar in){
        if(in!=null){
            date = in;
            initDate(date);
            return hour + ":" + minute;
        }else{
            Log.i("DateFormat","输入为空！");
            return null;
        }
    }
    public String GetHourAndMinuteDetailDescription(Date in){
        if(in!=null){
            date.setTime(in);
            initDate(date);
            return hour + ":" + minute;
        }else{
            Log.i("DateFormat","输入为空！");
            return null;
        }
    }

    private void initDate(Calendar in){
        year = in.get(Calendar.YEAR);//年
        month = in.get(Calendar.MONTH) + 1;//月（必须要+1）
        day = in.get(Calendar.DATE);//日
        hour = in.get(Calendar.HOUR_OF_DAY);//时
        minute = in.get(Calendar.MINUTE);//分
        second = in.get(Calendar.SECOND);//秒
        week = in.get(Calendar.DAY_OF_WEEK);//星期（Locale.ENGLISH情况下，周日是1,剩下自己推算）
    }

    private String getChineseWeek(int in){
        switch (in){
            case 1:
                return "星期日";
            case 2:
                return "星期一";
            case 3:
                return "星期二";
            case 4:
                return "星期三";
            case 5:
                return "星期四";
            case 6:
                return "星期五";
            case 7:
                return "星期六";
            default:
                Log.e("DateFormat","输入值有误！周几获取有效值范围“1~7”");
                return " ";
        }
    }

    public int getNowYear(){
        return Calendar.getInstance().get(Calendar.YEAR);
    }
    public int getNowMonth(){
        return (Calendar.getInstance().get(Calendar.MONTH));
    }
    public int getNowDay(){
        return Calendar.getInstance().get(Calendar.DATE);
    }
    public int getNowHourOfDay(){
        return Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
    }

    public Calendar FilterMinuteAndSecond(Calendar in){
        Calendar results = Calendar.getInstance();
        if(in!=null){
            results.set(in.get(Calendar.YEAR),in.get(Calendar.MONTH),in.get(Calendar.DATE),in.get(Calendar.HOUR_OF_DAY),0,0);
        }else{
            return null;
        }
        return results;
    }

    public Calendar FilterHourAndMinuteAndSecond(Calendar in){
        Calendar results = Calendar.getInstance();
        if(in!=null){
            results.set(in.get(Calendar.YEAR),in.get(Calendar.MONTH),in.get(Calendar.DATE),0,0,0);
        }else{
            return null;
        }
        return results;
    }

    /**
     * 计算两个时间相差多少天，两个时间 间隔 n * 24 个小时
     *
     * @param time1
     * @param time2
     * @return
     */
    public static int daysBetween(long time1, long time2) {

        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(time1);
        cal.set(Calendar.HOUR, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);

        Calendar cal2 = Calendar.getInstance();
        cal2.setTimeInMillis(time2);
        cal2.set(Calendar.HOUR, 0);
        cal2.set(Calendar.MINUTE, 0);
        cal2.set(Calendar.SECOND, 0);
        cal2.set(Calendar.MILLISECOND, 0);

        long between_days = (cal2.getTimeInMillis() - cal.getTimeInMillis()) / (1000 * 3600 * 24);
        int betweenDays = Math.abs(Integer.parseInt(String.valueOf(between_days)));
        System.out.println("daysBetween:"+betweenDays);
        return betweenDays ;
    }


}
