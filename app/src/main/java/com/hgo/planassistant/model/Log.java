package com.hgo.planassistant.model;

import org.litepal.crud.LitePalSupport;

import java.util.Date;

public class Log extends LitePalSupport {
    private String UseId; //记录用户Id
    private Date time;  // 记录日志时间
    private String label; // 上下文，记录activity或是service
//    private String behavior; // 进入或退出
    private String grade; // 等级 Verbose 详细、Debug 测试、Info 信息、Warning 警告、Error 错误
    private String log; //记录日志说明

    public void setUseId(String useId) {
        UseId = useId;
    }
    public void setTime(Date in_time){
        time = in_time;
    }
    public void setLabel(String in){
        label = in;
    }
//    public void setBehavior(String in){
//        behavior = in;
//    }
    public void setGrade(String in){
        grade = in;
    }
    public void setLog(String in){
        log = in;
    }

    public String getUseId() {
        return UseId;
    }

    public Date getTime() {
        return time;
    }

    public String getGrade() {
        return grade;
    }

    public String getLabel() {
        return label;
    }

    public String getLog() {
        return log;
    }
}
