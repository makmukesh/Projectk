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

import com.vpipl.kalpamrit.AppController;
import com.vpipl.kalpamrit.R;
import com.vpipl.kalpamrit.Utils.AppUtils;
import com.vpipl.kalpamrit.Utils.SPUtils;

/**
 * Created by PC14 on 11-Apr-16.
 */
public class CheckoutToPay_Adapter extends BaseAdapter {
    String TAG = "CheckoutToPay_Adapter";
    private Context context;
    private LayoutInflater inflater = null;

    public CheckoutToPay_Adapter(Context context) {
        this.context = context;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return AppController.selectedProductsList.size();
    }

    @Override
    public Object getItem(int position) {
        return AppController.selectedProductsList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        try {
            String Usertype = (AppController.getSpUserInfo().getString(SPUtils.USER_TYPE, ""));

            final Holder holder;
            if (convertView == null) {
                convertView = inflater.inflate(R.layout.checkoutproductlist_adapter, parent, false);
                holder = new Holder();
                holder.layout_normalProduct = convertView.findViewById(R.id.layout_normalProduct);

                holder.imageView_product = convertView.findViewById(R.id.imageView_product);
                holder.txt_productName = convertView.findViewById(R.id.txt_productName);
                holder.txt_productPrice = convertView.findViewById(R.id.txt_productPrice);
                holder.txt_productWeight = convertView.findViewById(R.id.txt_productWeight);
                holder.txt_productBV = convertView.findViewById(R.id.txt_productBV);
                holder.txt_productQty = convertView.findViewById(R.id.txt_productQty);
                holder.txt_productTotalPrice = convertView.findViewById(R.id.txt_productTotalPrice);
                holder.txt_productTotalBV = convertView.findViewById(R.id.txt_productTotalBV);

                convertView.setTag(holder);
            } else {
                holder = (Holder) convertView.getTag();
            }

            holder.layout_normalProduct.setVisibility(View.VISIBLE);

            holder.txt_productName.setText(AppController.selectedProductsList.get(position).getName());
            holder.txt_productPrice.setText("Price : ₹ " + Html.fromHtml(AppController.selectedProductsList.get(position).getNewDP()));
            holder.txt_productWeight.setText("Weight (In Grams) : " + Html.fromHtml(AppController.selectedProductsList.get(position).getWeight()));

            holder.txt_productBV.setText("");
            holder.txt_productBV.setVisibility(View.GONE);

            if (Usertype.equalsIgnoreCase("DISTRIBUTOR")) {
                holder.txt_productBV.setText("BV : " + Html.fromHtml(AppController.selectedProductsList.get(position).getBV()));
                holder.txt_productBV.setVisibility(View.VISIBLE);
            }

            AppUtils.loadProductImage(context, AppController.selectedProductsList.get(position).getImagePath().replace("\\", ""), holder.imageView_product);

            double countPrice;
            countPrice = ((Double.parseDouble(AppController.selectedProductsList.get(position).getNewDP())) * (Double.parseDouble(AppController.selectedProductsList.get(position).getQty())));
            holder.txt_productTotalPrice.setText("Total Price : ₹ " + countPrice);
            holder.txt_productTotalPrice.setTag(AppController.selectedProductsList.get(position));

            double countBV;
            countBV = ((Double.parseDouble(AppController.selectedProductsList.get(position).getBV())) * (Double.parseDouble(AppController.selectedProductsList.get(position).getQty())));
            holder.txt_productTotalBV.setText("Total BV : " + ((int) countBV));
            holder.txt_productTotalBV.setTag(AppController.selectedProductsList.get(position));

            holder.txt_productQty.setText("Total Qty : " + AppController.selectedProductsList.get(position).getQty());

        } catch (Exception e) {
            e.printStackTrace();
            AppUtils.showExceptionDialog(context);
        }

        return convertView;
    }

    private static class Holder {
        TextView txt_productName, txt_productPrice,txt_productWeight, txt_productBV, txt_productQty, txt_productTotalPrice, txt_productTotalBV;
        ImageView imageView_product;

        LinearLayout layout_normalProduct;
    }
}