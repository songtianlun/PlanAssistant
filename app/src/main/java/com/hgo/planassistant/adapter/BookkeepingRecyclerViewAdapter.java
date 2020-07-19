package com.hgo.planassistant.adapter;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.net.Uri;
import android.os.Bundle;
import android.text.InputFilter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.AppCompatSpinner;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.textfield.TextInputEditText;
import com.hgo.planassistant.App;
import com.hgo.planassistant.R;
import com.hgo.planassistant.tools.DateFormat;
import com.hgo.planassistant.tools.MoneyValueFilter;

import java.util.Date;
import java.util.List;

import cn.leancloud.AVObject;
import cn.leancloud.AVUser;
import cn.leancloud.callback.DeleteCallback;
import cn.leancloud.types.AVNull;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;

public class BookkeepingRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private Context mContext;
    private List<AVObject> mItems;
    private Bundle bundle;
    private int color = 0;

    public BookkeepingRecyclerViewAdapter(List<AVObject> list, Context context) {
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
        return new BookkeepingRecyclerViewAdapter.RecyclerViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_bookkeeping_recycler_view, parent, false));
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position) {

        if (holder instanceof BookkeepingRecyclerViewAdapter.RecyclerViewHolder) {
            final BookkeepingRecyclerViewAdapter.RecyclerViewHolder recyclerViewHolder = (BookkeepingRecyclerViewAdapter.RecyclerViewHolder) holder;

            Animation animation = AnimationUtils.loadAnimation(mContext, R.anim.anim_recycler_item_show);
            recyclerViewHolder.mView.startAnimation(animation);

            AlphaAnimation aa1 = new AlphaAnimation(1.0f, 0.1f);
            aa1.setDuration(400);
            recyclerViewHolder.rela_round.startAnimation(aa1);

            AlphaAnimation aa = new AlphaAnimation(0.1f, 1.0f);
            aa.setDuration(400);

            recyclerViewHolder.rela_round.startAnimation(aa);

            //加载金额，支出说明，日期

            if(mItems.get(position).getString("revenue").equals("收入")){
                recyclerViewHolder.rela_round.setBackgroundTintList(ColorStateList.valueOf(mContext.getResources().getColor(R.color.google_green)));
                recyclerViewHolder.TV_prince.setText("收入：" + mItems.get(position).get("prince") + " ￥");
            }else{
                recyclerViewHolder.rela_round.setBackgroundTintList(ColorStateList.valueOf(mContext.getResources().getColor(R.color.google_red)));
                recyclerViewHolder.TV_prince.setText("支出：" + mItems.get(position).get("prince") + " ￥");
            }

            recyclerViewHolder.TV_title.setText(mItems.get(position).getString("title"));
            DateFormat dateFormat = new DateFormat();
            recyclerViewHolder.TV_date.setText(dateFormat.GetDetailDescription(mItems.get(position).getDate("date")));


            //监听项目点击事件
            recyclerViewHolder.mView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //底部Dialog
                    BottomSheetDialog mBottomSheetDialog = new BottomSheetDialog(mContext);
                    LayoutInflater localinflater =  (LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                    View dialogView = localinflater.inflate(R.layout.dialog_bottom_bookkeeping_edit, null);
                    Button btn_ok = dialogView.findViewById(R.id.btn_dialog_bottom_bookkeeping_edit_ok);
                    Button btn_cancel = dialogView.findViewById(R.id.btn_dialog_bottom_bookkeeping_edit_cancel);
                    Button btn_delete = dialogView.findViewById(R.id.btn_dialog_bottom_bookkeeping_edit_delete);

                    AppCompatSpinner spinner_revenue = dialogView.findViewById(R.id.dialog_bottom_bookkeeping_edit_revenue);
                    AppCompatSpinner spinner_type = dialogView.findViewById(R.id.dialog_bottom_bookkeeping_edit_type);
                    TextInputEditText edit_prince = dialogView.findViewById(R.id.dialog_bottom_bookkeeping_edit_prince);
                    TextInputEditText edit_title = dialogView.findViewById(R.id.dialog_bottom_bookkeeping_edit_name_title);
                    EditText edit_description = dialogView.findViewById(R.id.dialog_bottom_bookkeeping_edit_description);
                    //默认两位小数
                    edit_prince.setFilters(new InputFilter[]{new MoneyValueFilter()});

                    edit_title.setText(mItems.get(position).getString("title"));
                    edit_prince.setText(mItems.get(position).getString("prince"));
                    edit_description.setText(mItems.get(position).getString("description"));
                    switch (mItems.get(position).getString("revenue")){
                        case "支出":
                            spinner_revenue.setSelection(0);
                            break;
                        case "收入":
                            spinner_revenue.setSelection(1);
                            break;
                    }
                    switch (mItems.get(position).getString("type")){
                        case "日常支出":
                            spinner_type.setSelection(0);
                            break;
                        case "学习提升":
                            spinner_type.setSelection(1);
                            break;
                        case "娱乐休闲":
                            spinner_type.setSelection(2);
                            break;
                        case "其他支出":
                            spinner_type.setSelection(3);
                            break;
                    }


                    spinner_revenue.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                            if(spinner_revenue.getSelectedItem().toString().equals("收入")){
                                spinner_type.setEnabled(false);
                                if(edit_title.getText().length()<1){
                                    edit_title.setText("收入");
                                }
                            }else{
                                spinner_type.setEnabled(true);
                                if(edit_title.getText().length()<1){
                                    edit_title.setText("支出");
                                }
                            }
                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> parent) {

                        }
                    });

                    btn_ok.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            if(spinner_revenue.getSelectedItem().toString().equals("收入")){
                                spinner_type.setEnabled(false);
                                if(edit_title.getText().length()<1){
                                    edit_title.setText("收入");
                                }
                            }else{
                                spinner_type.setEnabled(true);
                                if(edit_title.getText().length()<1){
                                    edit_title.setText("支出");
                                }
                            }

                            if(edit_prince.getText().length()<1){
                                Toast.makeText(mContext,"请输入金额！",Toast.LENGTH_SHORT).show();
                            }else{
                                //存到云
                                AVObject bookkeeping = AVObject.createWithoutData("Bookkeeping",mItems.get(position).getObjectId());
                                bookkeeping.put("UserId", AVUser.getCurrentUser().getObjectId());
                                bookkeeping.put("time",new Date());
                                bookkeeping.put("prince",edit_prince.getText());
                                bookkeeping.put("title",edit_title.getText());
                                bookkeeping.put("description",edit_description.getText());
                                bookkeeping.put("revenue",spinner_revenue.getSelectedItem().toString());
                                bookkeeping.put("type",spinner_type.getSelectedItem().toString());

                                bookkeeping.saveInBackground().subscribe(new Observer<AVObject>() {
                                    @Override
                                    public void onSubscribe(Disposable d) {

                                    }

                                    @Override
                                    public void onNext(AVObject avObject) {
                                        Toast.makeText(App.getContext(), "修改成功！", Toast.LENGTH_SHORT).show();
                                    }

                                    @Override
                                    public void onError(Throwable e) {
                                        Toast.makeText(App.getContext(), "保存失败，原因：" + e.getMessage(), Toast.LENGTH_SHORT).show();
                                    }

                                    @Override
                                    public void onComplete() {

                                    }
                                });

                                mBottomSheetDialog.dismiss();
                            }
                        }
                    });

                    btn_cancel.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            mBottomSheetDialog.dismiss();
                        }
                    });

                    btn_delete.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            AVObject bookkeeping = AVObject.createWithoutData("Bookkeeping",mItems.get(position).getObjectId());
                            bookkeeping.deleteInBackground().subscribe(new Observer<AVNull>() {
                                @Override
                                public void onSubscribe(Disposable d) {

                                }

                                @Override
                                public void onNext(AVNull avNull) {
                                    Toast.makeText(App.getContext(), "删除成功！（下拉刷新查看）", Toast.LENGTH_SHORT).show();
                                }

                                @Override
                                public void onError(Throwable e) {
                                    Toast.makeText(App.getContext(), "失败，原因：" + e.getMessage(), Toast.LENGTH_SHORT).show();
                                }

                                @Override
                                public void onComplete() {

                                }
                            });

                            mBottomSheetDialog.dismiss();
                        }
                    });

                    mBottomSheetDialog.setContentView(dialogView);
                    mBottomSheetDialog.show();
                }
            });
        }

    }

    @Override
    public int getItemCount() {
        return mItems.size();
    }

    private class RecyclerViewHolder extends RecyclerView.ViewHolder {
        private View mView;
        private RelativeLayout rela_round;
        private TextView TV_prince;
        private TextView TV_title;
        private TextView TV_date;
//        private com.amap.api.maps.MapView amapview;
//        private AMap amap;

        private RecyclerViewHolder(View itemView) {
            super(itemView);
            mView = itemView;
            rela_round = itemView.findViewById(R.id.item_bookkeeping_recycler_view_rela_round);
            TV_prince = (TextView) itemView.findViewById(R.id.tv_item_bookkeeping_recycler_prince);
            TV_title = (TextView) itemView.findViewById(R.id.tv_item_bookkeeping_recycler_title);
            TV_date = (TextView) itemView.findViewById(R.id.tv_item_bookkeeping_recycler_date);
//            amapview = itemView.findViewById(R.id.imrv_amapView);
        }
    }
}
