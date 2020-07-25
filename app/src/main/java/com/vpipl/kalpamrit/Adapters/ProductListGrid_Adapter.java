package com.vpipl.kalpamrit.Adapters;

import android.content.Context;
import android.graphics.Color;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.text.style.StrikethroughSpan;
import android.text.style.StyleSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.vpipl.kalpamrit.AppController;
import com.vpipl.kalpamrit.R;
import com.vpipl.kalpamrit.Utils.AppUtils;
import com.vpipl.kalpamrit.Utils.SPUtils;
import com.vpipl.kalpamrit.model.ProductsList;

import java.util.List;

/**
 * Created by PC14 on 11-Apr-16.
 */
public class ProductListGrid_Adapter extends BaseAdapter {
    private Context context;

    private LayoutInflater inflater = null;

    private List<ProductsList> productList;

    private String comesFrom = "";

    protected RequestManager glideManager;

    public ProductListGrid_Adapter(Context context, List<ProductsList> productList, String comesFrom) {
        this.context = context;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.productList = productList;
        this.comesFrom = comesFrom;
        glideManager = Glide.with(context);
    }

    @Override
    public int getCount() {
        return productList.size();
    }

    @Override
    public Object getItem(int position) {
        return productList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        try {
            final Holder holder;
            if (convertView == null) {

                    convertView = inflater.inflate(R.layout.productgrid_adapter, parent, false);

                    holder = new Holder();

                    holder.txt_productName = convertView.findViewById(R.id.txt_productName);
                    holder.txt_productAmount = convertView.findViewById(R.id.txt_productAmount);
                    holder.txt_productBV = convertView.findViewById(R.id.txt_productBV);
                    holder.txt_disc_disp = convertView.findViewById(R.id.txt_disc_disp);
                    holder.imageView_product = convertView.findViewById(R.id.imageView_product);

                    convertView.setTag(holder);

            } else {
                holder = (Holder) convertView.getTag();
            }


            holder.txt_productName.setText("" + productList.get(position).getName().trim());

            holder.txt_productBV.setText("");
            holder.txt_productBV.setVisibility(View.GONE);

            String Usertype = (AppController.getSpUserInfo().getString(SPUtils.USER_TYPE, ""));

            if (Usertype.equalsIgnoreCase("DISTRIBUTOR")) {
                holder.txt_productBV.setVisibility(View.VISIBLE);
                holder.txt_productBV.setText("BV : " + productList.get(position).getBV());
            }

            String NewDP = "₹ " + productList.get(position).getNewDP() + "/-";
            String NewMRP = productList.get(position).getNewMRP();

            String DiscountPer = productList.get(position).getDiscountPer() + "% off";

            Spannable spanString;

            if (DiscountPer.equalsIgnoreCase("0% off")) {
                spanString = new SpannableString("" + NewDP);
                spanString.setSpan(new ForegroundColorSpan(context.getResources().getColor(R.color.color_orange_text)), 0, NewDP.length(), 0);

                //spanString.setSpan(new AbsoluteSizeSpan(20), 0,NewDP.length(),0);
            } else {
//                    spanString = new SpannableString("" + NewDP + "  " + NewMRP + "  " + DiscountPer);
                spanString = new SpannableString("" + NewDP + "  " + NewMRP);

                spanString.setSpan(new ForegroundColorSpan(context.getResources().getColor(R.color.color_orange_text)), 0, NewDP.length(), 0);
                spanString.setSpan(new RelativeSizeSpan(1.0f), 0, NewDP.length(), 0);
                StyleSpan boldSpan = new StyleSpan(android.graphics.Typeface.BOLD);
                spanString.setSpan(boldSpan, 0, NewDP.length(), 0);

                spanString.setSpan(new StrikethroughSpan(), (NewDP.length() + 2), (((NewDP.length() + 2)) + (NewMRP.length())), 0);
                spanString.setSpan(new ForegroundColorSpan(Color.GRAY), (NewDP.length() + 2), (((NewDP.length() + 2)) + (NewMRP.length())), 0);

//                spanString.setSpan(new ForegroundColorSpan(Color.GRAY), ((((NewDP.length() + 2)) + (NewMRP.length())) + 2), spanString.length(), 0);
            }
            holder.txt_productAmount.setText(spanString);

            holder.txt_disc_disp.setText(DiscountPer);

            if (productList.get(position).getIsDisplayDiscount()) {
                holder.txt_disc_disp.setVisibility(View.VISIBLE);
            } else {
                holder.txt_disc_disp.setVisibility(View.GONE);
            }

            glideManager
                    .load(productList.get(position).getImagePath())
                    .placeholder(R.drawable.ic_no_image)
                    .skipMemoryCache(false)
                    .dontTransform()
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(holder.imageView_product);

        } catch (Exception e) {
            e.printStackTrace();
            AppUtils.showExceptionDialog(context);
        }

        return convertView;
    }

    private static class Holder {
        TextView txt_productName, txt_productAmount, txt_productBV;
        TextView txt_disc_disp;
        ImageView imageView_product;
    }
}