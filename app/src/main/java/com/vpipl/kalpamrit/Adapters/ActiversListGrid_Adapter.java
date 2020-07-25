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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by PC14 on 11-Apr-16.
 */
public class ActiversListGrid_Adapter extends BaseAdapter {
    private Context context;
    public static ArrayList<HashMap<String, String>> achiverList;
    private LayoutInflater inflater = null;

    private String comesFrom = "";

    protected RequestManager glideManager;

    public ActiversListGrid_Adapter(Context context, ArrayList<HashMap<String, String>> list, String comesFrom) {
        this.context = context;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.achiverList = list;
        this.comesFrom = comesFrom;
        glideManager = Glide.with(context);
    }

    @Override
    public int getCount() {
        return achiverList.size();
    }

    @Override
    public Object getItem(int position) {
        return achiverList.get(position);
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

                    convertView = inflater.inflate(R.layout.adapter_my_achivers, parent, false);

                    holder = new Holder();
                    holder.txt_achiever_name = convertView.findViewById(R.id.txt_achiever_name);
                    holder.txt_achiever_adddress = convertView.findViewById(R.id.txt_achiever_adddress);
                    holder.achiever_icon = convertView.findViewById(R.id.achiever_icon);
              //      holder.new_icon = convertView.findViewById(R.iddr.new_icon);

                    convertView.setTag(holder);

            } else {
                holder = (Holder) convertView.getTag();
            }

            holder.txt_achiever_name.setSelected(true);
            holder.txt_achiever_adddress.setSelected(true);

            holder.txt_achiever_name.setText(achiverList.get(position).get("achiever_name"));
            holder.txt_achiever_adddress.setText(achiverList.get(position).get("achiever_City")+","+achiverList.get(position).get("achiever_State"));

            AppUtils.loadProductImage(context, achiverList.get(position).get("achiever_Pic"), holder.achiever_icon);

/*            glideManager
                    .load(achiverList.get(position).getImagePath())
                    .placeholder(R.drawable.ic_no_image)
                    .skipMemoryCache(false)
                    .dontTransform()
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(holder.imageView_product);*/

        } catch (Exception e) {
            e.printStackTrace();
            AppUtils.showExceptionDialog(context);
        }

        return convertView;
    }

    private static class Holder {
        TextView txt_achiever_adddress, txt_achiever_name;
        ImageView achiever_icon ,new_icon;
    }
}