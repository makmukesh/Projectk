package com.vpipl.kalpamrit.Adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.vpipl.kalpamrit.R;
import com.vpipl.kalpamrit.Utils.AppUtils;

import org.apache.commons.lang3.text.WordUtils;

import java.util.ArrayList;
import java.util.HashMap;

public class MyOrdersDetailList_Adapter extends RecyclerView.Adapter<MyOrdersDetailList_Adapter.MyViewHolder> {
    private static ArrayList<HashMap<String, String>> ordersList;
    String TAG = "MyOrdersDetailList_Adapter";
    private LayoutInflater inflater = null;
    private Context context;

    public MyOrdersDetailList_Adapter(Context con, ArrayList<HashMap<String, String>> list) {
        ordersList = list;
        inflater = (LayoutInflater) con.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        context = con;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new MyViewHolder(inflater.inflate(R.layout.myordersdetails_adapter, parent, false));
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        try {
            holder.txt_productName.setText(WordUtils.capitalizeFully(ordersList.get(position).get("ProductName")));
            holder.txt_productId.setText(Html.fromHtml("Product ID " + ordersList.get(position).get("ProductID")));
            holder.txt_productAmount.setText("₹ " + Html.fromHtml(ordersList.get(position).get("Netamount")));
            holder.txt_productQty.setText(Html.fromHtml(ordersList.get(position).get("Qty")));

            holder.txt_productStatus.setText(Html.fromHtml(ordersList.get(position).get("ProdStatus")));

            AppUtils.loadProductImage(context, ordersList.get(position).get("ImgPath"), holder.imageView_product);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return ordersList.size();
    }


    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView txt_productName, txt_productId, txt_productQty, txt_productAmount, txt_productStatus;
        ImageView imageView_product;

        public MyViewHolder(View view) {
            super(view);

            txt_productId = view.findViewById(R.id.txt_productId);
            txt_productName = view.findViewById(R.id.txt_productName);
            txt_productQty = view.findViewById(R.id.txt_productQty);
            txt_productAmount = view.findViewById(R.id.txt_productAmount);
            txt_productStatus = view.findViewById(R.id.txt_productStatus);
            imageView_product = view.findViewById(R.id.imageView_product);
        }
    }
}