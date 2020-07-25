package com.vpipl.kalpamrit.Adapters;

import android.content.Context;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.vpipl.kalpamrit.CheckoutToPay_Activity;
import com.vpipl.kalpamrit.R;
import com.vpipl.kalpamrit.Utils.AppUtils;

import org.apache.commons.lang3.text.WordUtils;


/**
 * Created by PC14 on 11-Apr-16.
 */
public class ChangeAddressList_Adapter extends BaseAdapter {
    String TAG = "ChangeAddressList_Adapter";
    private Context context;
    private LayoutInflater inflater = null;

    public ChangeAddressList_Adapter(Context context) {
        this.context = context;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return CheckoutToPay_Activity.deliveryAddressList.size();
    }

    @Override
    public Object getItem(int position) {
        return CheckoutToPay_Activity.deliveryAddressList.get(position);
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
                convertView = inflater.inflate(R.layout.changeaddresslist_adapter, parent, false);
                holder = new Holder();

                holder.imgView_editAddress = convertView.findViewById(R.id.imgView_editAddress);
                holder.txt_name = convertView.findViewById(R.id.txt_name);
                holder.txt_address = convertView.findViewById(R.id.txt_address);
                holder.txt_mobNo = convertView.findViewById(R.id.txt_mobNo);

                convertView.setTag(holder);
            } else {
                holder = (Holder) convertView.getTag();
            }

            holder.txt_name.setText(WordUtils.capitalizeFully(CheckoutToPay_Activity.deliveryAddressList.get(position).get("MemFirstName")));
            holder.txt_address.setText(WordUtils.capitalizeFully(Html.fromHtml(CheckoutToPay_Activity.deliveryAddressList.get(position).get("Address")) + " Mobile : " + CheckoutToPay_Activity.deliveryAddressList.get(position).get("Mobl")));
            holder.txt_mobNo.setText(CheckoutToPay_Activity.deliveryAddressList.get(position).get("Mobl"));

//            holder.imgView_editAddress.setOnClickListener(new View.OnClickListener()
//            {
//                @Override
//                public void onClick(View view)
//                {
//                    try
//                    {
//                        if(AppUtils.showLogs) Log.e("TAG", "holder.imgView_editAddress called position.."+position);
//                        Intent intent=new Intent(context, AddDeliveryAddress_Activity.class);
//                        intent.putExtra("position",""+position);
//                        ((Activity)context).startActivity(intent);
//                        ((Activity)context).//overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
//                    }
//                    catch (Exception e)
//                    {
//                        e.printStackTrace();
//                    }
//                }
//            });
        } catch (Exception e) {
            e.printStackTrace();
            AppUtils.showExceptionDialog(context);
        }

        return convertView;
    }

    private static class Holder {
        ImageView imgView_editAddress;
        TextView txt_name, txt_address, txt_mobNo;
    }
}