package com.vpipl.kalpamrit.Adapters;

import android.content.Context;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.vpipl.kalpamrit.AddCartCheckOut_Activity;
import com.vpipl.kalpamrit.AppController;
import com.vpipl.kalpamrit.R;
import com.vpipl.kalpamrit.Utils.AppUtils;
import com.vpipl.kalpamrit.Utils.SPUtils;
import com.vpipl.kalpamrit.model.ProductsList;

/**
 * Created by PC14 on 11-Apr-16.
 */
public class AddCartCheckout_Adapter extends BaseAdapter {
    private String TAG = "AddCartCheckout_Adapter";
    private Context context;
    private LayoutInflater inflater = null;

    public AddCartCheckout_Adapter(Context context) {
        this.context = context;
        inflater = (LayoutInflater) context.getSystemService
                (Context.LAYOUT_INFLATER_SERVICE);

        if (AppUtils.showLogs)
            Log.e(TAG, " AppController.selectedProductsList.size..." + AppController.selectedProductsList.size());
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
            final Holder holder;
            if (convertView == null) {
                convertView = inflater.inflate(R.layout.cartproductlist_adapter, parent, false);
                holder = new Holder();
                holder.layout_normalProduct = convertView.findViewById(R.id.layout_normalProduct);

                holder.imageView_product = convertView.findViewById(R.id.imageView_product);
                holder.txt_productName = convertView.findViewById(R.id.txt_productName);
                holder.img_delete = convertView.findViewById(R.id.img_delete);
                holder.txt_productPrice = convertView.findViewById(R.id.txt_productPrice);
                holder.txt_productBV = convertView.findViewById(R.id.txt_productBV);
                holder.txt_productWeight = convertView.findViewById(R.id.txt_productWeight);
                holder.txt_productPlus = convertView.findViewById(R.id.txt_productPlus);
                holder.txt_productValue = convertView.findViewById(R.id.txt_productValue);
                holder.txt_productMinus = convertView.findViewById(R.id.txt_productMinus);
                holder.txt_productTotalPrice = convertView.findViewById(R.id.txt_productTotalPrice);
                holder.txt_productTotalBV = convertView.findViewById(R.id.txt_productTotalBV);

                holder.img_delete.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {
                        deleteProduct(((int) v.getTag()));
                    }
                });

                holder.txt_productMinus.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {
                        ProductsList selectedProducts = (ProductsList) v.getTag();
                        int i;
                        i = Integer.parseInt(selectedProducts.getQty());
                        if (i > 1) {
                            i = i - 1;
                        }
                        selectedProducts.setQty("" + i);

                        String ProId = selectedProducts.getID();
                        notifyDataSetChanged();
                        AddCartCheckOut_Activity.setNetPaybleValue();
                    }
                });

                holder.txt_productPlus.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {
                        ProductsList selectedProducts = (ProductsList) v.getTag();
                        int i;
                        i = Integer.parseInt(selectedProducts.getQty());
                        i = i + 1;
                        selectedProducts.setQty("" + i);

                        String ProId = selectedProducts.getID();

                        notifyDataSetChanged();

                        AddCartCheckOut_Activity.setNetPaybleValue();
                    }
                });

                convertView.setTag(holder);
            } else {
                holder = (Holder) convertView.getTag();
            }

            holder.layout_normalProduct.setVisibility(View.VISIBLE);
            String Usertype = (AppController.getSpUserInfo().getString(SPUtils.USER_TYPE, ""));

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

            holder.txt_productValue.setText(AppController.selectedProductsList.get(position).getQty());
            holder.txt_productValue.setTag(AppController.selectedProductsList.get(position));

            holder.txt_productPlus.setTag(AppController.selectedProductsList.get(position));
            holder.txt_productMinus.setTag(AppController.selectedProductsList.get(position));

            holder.img_delete.setTag(position);
        } catch (Exception e) {
            e.printStackTrace();
            AppUtils.showExceptionDialog(context);
        }

        return convertView;
    }

    private void deleteProduct(final int position) {
        try {
            if (AppUtils.showLogs) Log.e(TAG, "holder.img_delete called..." + position);
            AppController.selectedProductsList.remove(position);

            if (AppController.selectedProductsList.size() > 0) {
                notifyDataSetChanged();
                AddCartCheckOut_Activity.setNetPaybleValue();
            } else {
                AddCartCheckOut_Activity.showEmptyCart();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static class Holder {
        TextView txt_productName, txt_productPrice, txt_productBV, txt_productWeight, txt_productPlus, txt_productValue, txt_productMinus, txt_productTotalPrice, txt_productTotalBV;
        ImageView img_delete, imageView_product;

        TextView txt_packageName, txt_packagePrice, txt_packageBV;

        LinearLayout layout_normalProduct;
    }
}