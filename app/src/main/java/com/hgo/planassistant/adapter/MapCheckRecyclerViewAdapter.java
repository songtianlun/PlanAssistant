package com.hgo.planassistant.adapter;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.Poi;
import com.amap.api.navi.AmapNaviPage;
import com.amap.api.navi.AmapNaviParams;
import com.amap.api.navi.AmapNaviType;
import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVGeoPoint;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.DeleteCallback;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.hgo.planassistant.R;
import com.hgo.planassistant.activity.DetailPMapActivity;

import java.text.DateFormat;
import java.util.Date;
import java.util.List;

public class MapCheckRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private Context mContext;
    private List<AVObject> mItems;
    private Bundle bundle;

    public MapCheckRecyclerViewAdapter(List<AVObject> list, Context context) {
        this.mContext = context;
        this.mItems = list;
    }

    public void Updatelist(List<AVObject> list){
//        this.mItems.clear();
        this.mItems = list;
        notifyDataSetChanged();
    }

    public void setBundle(Bundle bundle){
        this.bundle = bundle;
    }

    public void addItem(int position, AVObject insertData) {
        mItems.add(position, insertData);
        notifyItemInserted(position);
    }
    public void addItem(AVObject insertData) {
        int position = 0;
        mItems.add(position, insertData);
        notifyItemInserted(position);
    }
    public void RemoveItem(int position){
        mItems.remove(position);
        notifyItemRemoved(position);
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new MapCheckRecyclerViewAdapter.RecyclerViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_map_check_recycler_view, parent, false));
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position) {

        if (holder instanceof MapCheckRecyclerViewAdapter.RecyclerViewHolder) {
            final MapCheckRecyclerViewAdapter.RecyclerViewHolder recyclerViewHolder = (MapCheckRecyclerViewAdapter.RecyclerViewHolder) holder;

            //加载地图
//            recyclerViewHolder.amapview.onCreate(bundle); // 此方法须覆写，虚拟机需要在很多情况下保存地图绘制的当前状态。
//            if (recyclerViewHolder.amap == null) {
//                recyclerViewHolder.amap = recyclerViewHolder.amapview.getMap();
//            }

            //加载名称、备注、事件
            recyclerViewHolder.TV_title.setText(mItems.get(position).getString("title"));
            recyclerViewHolder.TV_snippet.setText(mItems.get(position).getString("snippet"));
            recyclerViewHolder.TV_province.setText(mItems.get(position).getString("provinceName") + " " + mItems.get(position).getString("cityName"));


            //监听项目点击事件
            recyclerViewHolder.mItem.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    new AlertDialog.Builder(mContext)
                            .setTitle(mItems.get(position).getString("title"))
                            .setMessage(mItems.get(position).getString("snippet"))
                            .setPositiveButton(mContext.getString(R.string.dialog_navigation), new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    AVGeoPoint avGeoPoint = mItems.get(position).getAVGeoPoint("latLonPoint");
                                    Poi end = new Poi(mItems.get(position).getString("title"), new LatLng(avGeoPoint.getLatitude(), avGeoPoint.getLongitude()), "");
                                    AmapNaviParams params = new AmapNaviParams(null, null, end, AmapNaviType.DRIVER);
                                    params.setUseInnerVoice(true);
                                    params.setMultipleRouteNaviMode(true);
                                    params.setNeedDestroyDriveManagerInstanceWhenNaviExit(true);
                                    AmapNaviPage.getInstance().showRouteActivity(mContext,params, null);
                                    // APP_NAME  自己应用的名字
//                                    Log.d("MapCheckRecycleViewAp","准备调起高德地图");
//                                    Intent intent = new Intent();
//                                    intent.setAction(Intent.ACTION_VIEW);
//                                    intent.addCategory(Intent.CATEGORY_DEFAULT);
//                                    //将功能Scheme以URI的方式传入data
//                                    String text =
//                                            "androidamap://navi?sourceApplication="
//                                                    + "PlanAssistant"
//                                                    + "&amp;poiname="
//                                                    + mItems.get(position).getString("title")
//                                                    + "&amp;lat=" + avGeoPoint.getLatitude()
//                                                    + "&amp;lon=" + avGeoPoint.getLongitude()
//                                                    + "&amp;dev=1&amp;style=2";
//                                    Log.d("MapCheckRecycleViewAp","调起高德地图："+text);
//                                    Uri uri = Uri.parse(text);
//                                    intent.setData(uri);
//                                    //启动该页面即可
//                                    mContext.startActivity(intent);

                                }
                            })
                            .setNegativeButton(mContext.getString(R.string.dialog_delete), new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    AVObject del = AVObject.createWithoutData("MapCheck",mItems.get(position).getObjectId());
                                    del.deleteInBackground(new DeleteCallback() {
                                        @Override
                                        public void done(AVException e) {
                                            Toast.makeText(mContext,"删除成功，下拉刷新查看！",Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                }
                            })
                            .show();
                }
            });
        }

    }

    @Override
    public int getItemCount() {
        return mItems.size();
    }

    private class RecyclerViewHolder extends RecyclerView.ViewHolder {
        private TextView TV_title;
        private TextView TV_snippet;
        private TextView TV_province;
        private CardView mItem;
//        private com.amap.api.maps.MapView amapview;
//        private AMap amap;

        private RecyclerViewHolder(View itemView) {
            super(itemView);
            TV_title = (TextView) itemView.findViewById(R.id.tv_item_map_check_recycler_title);
            TV_snippet = (TextView) itemView.findViewById(R.id.tv_item_map_check_recycler_snippet);
            TV_province = (TextView) itemView.findViewById(R.id.tv_item_map_check_recycler_province);
            mItem = (CardView) itemView.findViewById(R.id.card_view_item_map_check_view);
//            amapview = itemView.findViewById(R.id.imrv_amapView);
        }
    }
}
