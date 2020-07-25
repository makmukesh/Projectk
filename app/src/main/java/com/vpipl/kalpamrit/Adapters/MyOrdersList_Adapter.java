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

import java.util.ArrayList;
import java.util.HashMap;

public class MyOrdersList_Adapter extends RecyclerView.Adapter<MyOrdersList_Adapter.MyViewHolder> {
    public static ArrayList<HashMap<String, String>> ordersList;
    private LayoutInflater inflater = null;
    private Context context;
    private String TAG = "MyOrdersList_Adapter";

    public MyOrdersList_Adapter(Context con, ArrayList<HashMap<String, String>> list) {
        ordersList = list;
        inflater = (LayoutInflater) con.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        context = con;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new MyViewHolder(inflater.inflate(R.layout.myorders_adapter, parent, false));
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        try {
            holder.txt_OrderNo.setText(ordersList.get(position).get("OrderNo"));
            holder.txt_OrderAmt.setText("₹ " + ordersList.get(position).get("TotalAmount"));
            holder.txt_OrderDate.setText(AppUtils.getDateFromAPIDate(ordersList.get(position).get("ODate")));
            holder.txt_OrderStatus.setText(WordUtils.capitalizeFully(ordersList.get(position).get("OrderStatus")));

//			String Usertype = (AppController.getSpUserInfo().getString(SPUtils.USER_TYPE, ""));

//			if (Usertype.equalsIgnoreCase("DISTRIBUTOR")){
//				holder.lay_bv.setVisibility(View.VISIBLE);
//				holder.txt_OrderBV.setText((ordersList.get(position).get("OrderQvp")));
//			}

            Drawable upArrow = ResourcesCompat.getDrawable(context.getResources(), R.drawable.ic_arrow, null);
            upArrow.setColorFilter(ResourcesCompat.getColor(context.getResources(), R.color.colorAccent, null), PorterDuff.Mode.SRC_ATOP);
            holder.icon.setBackgroundDrawable(upArrow);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return ordersList.size();
    }


    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView txt_OrderNo, txt_OrderAmt, txt_OrderDate, txt_OrderStatus, txt_OrderBV;
        LinearLayout lay_bv;
        ImageView icon;

        public MyViewHolder(View view) {
            super(view);
            txt_OrderNo = view.findViewById(R.id.txt_OrderNo);
            txt_OrderAmt = view.findViewById(R.id.txt_OrderAmt);
            txt_OrderDate = view.findViewById(R.id.txt_OrderDate);
            txt_OrderStatus = view.findViewById(R.id.txt_OrderStatus);
            txt_OrderBV = view.findViewById(R.id.txt_OrderBV);

            lay_bv = view.findViewById(R.id.lay_bv);

            icon = view.findViewById(R.id.icon);

            view.setOnClickListener(new View.OnClickListener() {
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
            });
        }
    }
}