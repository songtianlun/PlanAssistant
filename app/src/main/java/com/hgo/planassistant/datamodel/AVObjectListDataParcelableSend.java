package com.hgo.planassistant.datamodel;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import com.avos.avoscloud.AVObject;

import java.util.ArrayList;
import java.util.List;

public class AVObjectListDataParcelableSend implements Parcelable {

    static private List<AVObject> AvObjectList;

    public List<AVObject> getAvObjectList(){
        return AvObjectList;
    }

    public AVObjectListDataParcelableSend(List<AVObject> in) {
        AvObjectList = new ArrayList<AVObject>();
        AvObjectList.addAll(in);
    }

    protected AVObjectListDataParcelableSend(Parcel in) {
        in.writeTypedList(AvObjectList);
    }

    // 序列化
    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeTypedList(AvObjectList);
        Log.i("AVObjectListDataPS","序列化数据");
    }

    @Override
    public int describeContents() {
        return 0;
    }

    // 反序列化
    public static final Creator<AVObjectListDataParcelableSend> CREATOR = new Creator<AVObjectListDataParcelableSend>() {
        @Override
        public AVObjectListDataParcelableSend createFromParcel(Parcel in) {
            return new AVObjectListDataParcelableSend(in);
        }

        @Override
        public AVObjectListDataParcelableSend[] newArray(int size) {
            return new AVObjectListDataParcelableSend[size];
        }
    };
}
