package com.vpipl.kalpamrit.Adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
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

import com.vpipl.kalpamrit.AppController;
import com.vpipl.kalpamrit.GetContactDetails_Activity;
import com.vpipl.kalpamrit.ID_card_Activity;
import com.vpipl.kalpamrit.MyOrdersDetails_Activity;
import com.vpipl.kalpamrit.R;
import com.vpipl.kalpamrit.Utils.AppUtils;
import com.vpipl.kalpamrit.Utils.QueryUtils;
import com.vpipl.kalpamrit.Utils.SPUtils;

import org.apache.commons.lang3.text.WordUtils;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class GetContactDetails_Adapter extends RecyclerView.Adapter<GetContactDetails_Adapter.MyViewHolder> {
    public static ArrayList<HashMap<String, String>> ordersList;
    private LayoutInflater inflater = null;
    private Context context;
    private String TAG = "GetContactDetails_Adapter";

    public GetContactDetails_Adapter(Context con, ArrayList<HashMap<String, String>> list) {
        ordersList = list;
        inflater = (LayoutInflater) con.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        context = con;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new MyViewHolder(inflater.inflate(R.layout.getcontactdetails_adapter, parent, false));
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        try {
            holder.txt_get_name.setSelected(true);
            holder.txt_get_name.setSingleLine(true);

            holder.txt_get_address.setSelected(true);
            holder.txt_get_address.setSingleLine(true);

            holder.txt_get_name.setText("Name : " + ordersList.get(position).get("Name"));
            holder.txt_get_address.setText("Address : " + ordersList.get(position).get("Address") + ordersList.get(position).get("Address"));

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
        TextView txt_get_name, txt_get_address ;
        ImageView icon;

        public MyViewHolder(View view) {
            super(view);
            txt_get_name = view.findViewById(R.id.txt_get_name);
            txt_get_address = view.findViewById(R.id.txt_get_address);

            icon = view.findViewById(R.id.icon);

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (AppUtils.showLogs) Log.e(TAG, "setOnClickListener.." + getPosition());

                    Handler handler = new Handler();
                    Runnable runnable = new Runnable() {
                        @Override
                        public void run() {
                           /* Intent intent = new Intent(context, MyOrdersDetails_Activity.class);
                            intent.putExtra("position", getPosition());
                            context.startActivity(intent);*/
                            executeSendContactRequest(context , "1");
                        }
                    };
                    handler.postDelayed(runnable, 10);

                }
            });
        }
    }

    private void executeSendContactRequest(final Context context ,final String id) {
        try {
            if (AppUtils.isNetworkAvailable(context)) {
                new AsyncTask<Void, Void, String>() {
                    protected void onPreExecute() {
                        AppUtils.showProgressDialog(context);
                    }

                    @Override
                    protected String doInBackground(Void... params) {
                        String response = "";
                        try {
                            List<NameValuePair> postParameters = new ArrayList<>();
                          //  postParameters.add(new BasicNameValuePair("id", id));
                            postParameters.add(new BasicNameValuePair("OrderByFormNo", AppController.getSpUserInfo().getString(SPUtils.USER_FORM_NUMBER, "")));
                            postParameters.add(new BasicNameValuePair("MobileNo", AppController.getSpUserInfo().getString(SPUtils.USER_MOBILE_NO, "")));
                            postParameters.add(new BasicNameValuePair("Deviceid", "00000000" ));
                            response = AppUtils.callWebServiceWithMultiParam(context, postParameters, QueryUtils.methodToGetViewOrdersList, TAG);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        return response;
                    }

                    @Override
                    protected void onPostExecute(String resultData) {
                        try {
                            AppUtils.dismissProgressDialog();
                            JSONObject jsonObject = new JSONObject(resultData);

                            if (jsonObject.getString("Status").equalsIgnoreCase("True")) {
                                JSONArray jsonArrayData = jsonObject.getJSONArray("FillNewOrdersDetail");

                                if (jsonArrayData.length() > 0) {
                                    AppUtils.alertDialogWithFinish(context, jsonObject.getString("Message"));

                                } else {
                                    AppUtils.alertDialog(context, jsonObject.getString("Message"));
                                }
                            } else {
                                AppUtils.alertDialog(context, jsonObject.getString("Message"));
                                if (AppUtils.showLogs)
                                    Log.v(TAG, "executeGetMyOrdersRequest executed...Failed... called");
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            AppUtils.showExceptionDialog(context);
                        }
                    }
                }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            }
        } catch (Exception e) {
            e.printStackTrace();
            AppUtils.showExceptionDialog(context);
        }
    }
}