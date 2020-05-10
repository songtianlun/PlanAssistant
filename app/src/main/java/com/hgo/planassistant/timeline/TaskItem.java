package com.hgo.planassistant.timeline;

import android.graphics.Color;

import com.orient.me.data.ITimeItem;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class TaskItem implements ITimeItem {

    private String name;
    private String title;
    private String detail;
    private Date date;
    private int color;
    private int res;

    public TaskItem(String name, String title, String detail, Date date, int color, int res) {
        this.name = name;
        this.title = title;
        this.detail = detail;
        this.date = date;
        this.color = color;
        this.res = res;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setColor(int color) {
        this.color = color;
    }

    public int getRes() {
        return res;
    }

    public void setRes(int res) {
        this.res = res;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    @Override
    public String getTitle() {
        return title;
    }

    @Override
    public int getColor() {
        return color;
    }

    @Override
    public int getResource() {
        return res;
    }

    public String getDetail() {
        return detail;
    }

    public void setDetail(String detail) {
        this.detail = detail;
    }

//    public static List<TaskItem> initStepInfo(){
//        List<TaskItem> items = new ArrayList<>();
//        items.add(new TaskItem("完善信息", "实践探究", "+30积分", Color.parseColor("#F57F17"), 0));
//        items.add(new TaskItem("了解基地", "实践探究", "+30积分", Color.parseColor("#F57F17"), 0));
//        items.add(new TaskItem("知识储备", "实践探究", "+30积分", Color.parseColor("#F57F17"), 0));
//        items.add(new TaskItem("安全教育主题馆", "实践探究", "+30积分", Color.parseColor("#F57F17"), 0));
//        items.add(new TaskItem("评价教师", "总结拓展", "+30积分", Color.parseColor("#0D47A1"), 0));
//        items.add(new TaskItem("评价路线", "总结拓展", "+30积分", Color.parseColor("#0D47A1"), 0));
//        return items;
//    }

//    public static List<TimeItem> initTimeInfo(){
//        List<TimeItem> items = new ArrayList<>();
//        items.add(new TimeItem("喝茶", "10-01，周二", "第一天养养生吧~", Color.parseColor("#f36c60"), R.drawable.timeline_ic_tea));
//        items.add(new TimeItem("喝酒", "06-12，周三", "今天找老徐吃烧烤", Color.parseColor("#ab47bc"), R.drawable.timeline_ic_drink));
//        items.add(new TimeItem("画画", "07-07，周四", "去鼋头渚写生", Color.parseColor("#aed581"), R.drawable.timeline_ic_draw));
//        items.add(new TimeItem("高尔夫", "08-20，周五", "约个高尔夫", Color.parseColor("#5FB29F"), R.drawable.timeline_ic_golf));
//        items.add(new TimeItem("游泳", "09-16，周六", "今天来洗个澡", Color.parseColor("#ec407a"), R.drawable.timeline_ic_bath));
//        items.add(new TimeItem("温泉", "10-01，周日", "快上班了好好休息", Color.parseColor("#ffd54f"), R.drawable.timeline_ic_footer));
//        return items;
//    }


}
