package com.vpipl.kalpamrit.Adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.vpipl.kalpamrit.MyOrdersDetails_Activity;
import com.vpipl.kalpamrit.R;
import com.vpipl.kalpamrit.Utils.AppUtils;

import org.apache.commons.lang3.text.WordUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import pl.droidsonroids.gif.AnimationListener;
import pl.droidsonroids.gif.GifDrawable;

public class MyAchiversList_Adapter extends RecyclerView.Adapter<MyAchiversList_Adapter.MyViewHolder> {
    public static ArrayList<HashMap<String, String>> achiverList;
    private LayoutInflater inflater = null;
    private Context context;
    private String TAG = "MyAchiversList_Adapter";

    public MyAchiversList_Adapter(Context con, ArrayList<HashMap<String, String>> list) {
        achiverList = list;
        inflater = (LayoutInflater) con.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        context = con;
    }


    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new MyViewHolder(inflater.inflate(R.layout.adapter_my_achivers, parent, false));
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        try {
            holder.txt_achiever_name.setText(achiverList.get(position).get("achiever_name"));
            holder.txt_achiever_adddress.setText(achiverList.get(position).get("achiever_City")+","+achiverList.get(position).get("achiever_State"));

            AppUtils.loadProductImage(context, achiverList.get(position).get("achiever_Pic"), holder.achiever_icon);

                   /* GifDrawable gifDrawable = null;
                    try {
                        gifDrawable = new GifDrawable(context.getAssets(), "new_label.gif" );
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    if (gifDrawable != null) {
                        gifDrawable.addAnimationListener(new AnimationListener() {
                            @Override
                            public void onAnimationCompleted(int loopNumber) {
                                Log.d("splashscreen", "Gif animation completed");

                            }
                        });
                        holder.new_icon.setImageDrawable(gifDrawable);
                    }*/

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return achiverList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView txt_achiever_adddress, txt_achiever_name;
        ImageView achiever_icon ,new_icon;

        public MyViewHolder(View view) {
            super(view);
            txt_achiever_name = view.findViewById(R.id.txt_achiever_name);
            txt_achiever_adddress = view.findViewById(R.id.txt_achiever_adddress);
            achiever_icon = view.findViewById(R.id.achiever_icon);
       //     new_icon = view.findViewById(R.id.new_icon);

           /* view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (AppUtils.showLogs) Log.e(TAG, "setOnClickListener.." + getPosition());

                    Handler handler = new Handler();
                    Runnable runnable = new Runnable() {
                        @Override
                        public void run() {
                            Intent intent = new Intent(context, MyOrdersDetails_Activity.class);
                            intent.putExtra("position", getPosition());
                            context.startActivity(intent);
                        }
                    };
                    handler.postDelayed(runnable, 10);

                }
            });*/
        }
    }
}