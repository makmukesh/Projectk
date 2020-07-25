package com.vpipl.kalpamrit.Adapters;

import android.content.Context;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.vpipl.kalpamrit.R;
import com.vpipl.kalpamrit.ThanksScreen_Activity;
import com.vpipl.kalpamrit.Utils.AppUtils;


/**
 * Created by PC14 on 11-Apr-16.
 */
public class ThanksOrderDetail_Adapter extends BaseAdapter {
    String TAG = "ThanksOrderDetail_Adapter";
    private Context context;
    private LayoutInflater inflater = null;

    public ThanksOrderDetail_Adapter(Context context) {
        this.context = context;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return ThanksScreen_Activity.orderDetailsList.size();
    }

    @Override
    public Object getItem(int position) {
        return ThanksScreen_Activity.orderDetailsList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        try {
            final Holder holder;
            if (convertView == null) {
                convertView = inflater.inflate(R.layout.thanks_orderdetail_adapter, parent, false);
                holder = new Holder();
                holder.layout_normalProduct = convertView.findViewById(R.id.layout_normalProduct);

                holder.imageView_product = convertView.findViewById(R.id.imageView_product);
                holder.txt_productName = convertView.findViewById(R.id.txt_productName);
                holder.txt_productQty = convertView.findViewById(R.id.txt_productQty);
                holder.txt_productTotalPrice = convertView.findViewById(R.id.txt_productTotalPrice);

                convertView.setTag(holder);
            } else {
                holder = (Holder) convertView.getTag();
            }

//            if(ThanksScreen_Activity.orderDetailsList.get(position).get("IsKit").equals("N"))
//            {
            holder.layout_normalProduct.setVisibility(View.VISIBLE);

            holder.txt_productName.setText("" + ThanksScreen_Activity.orderDetailsList.get(position).get("ProductName"));
            holder.txt_productQty.setText("Qty : " + ThanksScreen_Activity.orderDetailsList.get(position).get("Quantity"));
            holder.txt_productTotalPrice.setText(Html.fromHtml("Total Amount : &#8377 " + ThanksScreen_Activity.orderDetailsList.get(position).get("NetAmount")));

            AppUtils.loadProductImage(context, ThanksScreen_Activity.orderDetailsList.get(position).get("ImageUrl"), holder.imageView_product);
//            }
        } catch (Exception e) {
            e.printStackTrace();
            AppUtils.showExceptionDialog(context);
        }

        return convertView;
    }

    private static class Holder {
        ImageView imageView_product;
        TextView txt_productName, txt_productQty, txt_productTotalPrice;

        LinearLayout layout_normalProduct;
    }
}