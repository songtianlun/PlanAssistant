package com.hgo.planassistant.datamodel;

import android.os.Parcel;
import android.os.Parcelable;

import com.avos.avoscloud.AVObject;

import java.util.ArrayList;
import java.util.List;

public class AVObjectsParcelable implements Parcelable {

    private List<AVObject> send_list;

    public AVObjectsParcelable(){
        send_list = new ArrayList<>();
    }

    protected AVObjectsParcelable(Parcel in) {
        send_list = in.createTypedArrayList(AVObject.CREATOR);
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeTypedList(send_list);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<AVObjectsParcelable> CREATOR = new Creator<AVObjectsParcelable>() {
        @Override
        public AVObjectsParcelable createFromParcel(Parcel in) {
            return new AVObjectsParcelable(in);
        }

        @Override
        public AVObjectsParcelable[] newArray(int size) {
            return new AVObjectsParcelable[size];
        }
    };

    public void setSend_list(List<AVObject> send_list) {
        this.send_list = send_list;
//        this.send_list.addAll(send_list);
    }

    public List<AVObject> getSend_list() {
        return send_list;
    }
}
