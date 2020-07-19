package com.hgo.planassistant.datamodel;

import android.util.Log;


import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import cn.leancloud.AVObject;

public class AVObjectsSendToActivity implements Serializable {
    private static final long serialVersionUID=1L; //序列化和反序列化时保持版本的兼容性
    private List<AVObject> avlist;

    public AVObjectsSendToActivity(){
        avlist = new ArrayList<>();
    }

    public List<AVObject> getAvlist() {
        return avlist;
    }

    public void setAvlist(List<AVObject> avlist) {
        this.avlist.addAll(avlist);
//    this.avlist = avlist;
    }
}
