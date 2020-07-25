package com.vpipl.kalpamrit;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.vpipl.kalpamrit.Adapters.MyOrdersDetailList_Adapter;
import com.vpipl.kalpamrit.Adapters.MyOrdersList_Adapter;
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

/**
 * Created by PC14 on 08-Apr-16.
 */
public class MyOrdersDetails_Activity extends AppCompatActivity {
    private String TAG = "MyOrdersDetails_Activity";
    private RecyclerView listView;
    private LinearLayout layout_listView;
    private LinearLayout layout_nodata;

    private MyOrdersDetailList_Adapter adapter;
    private ArrayList<HashMap<String, String>> ordersDetailList = new ArrayList<>();

    private TextView txt_orderNo;
    private TextView txt_orderDate;
    private TextView txt_orderStatus;
    private TextView txt_orderAmount;
    private TextView txt_OrderBV;
    private LinearLayout lay_bv;

    ImageView img_nav_back, img_login_logout;

    public void SetupToolbar() {

        img_nav_back = findViewById(R.id.img_nav_back);
        img_login_logout = findViewById(R.id.img_login_logout);

        img_nav_back.setImageDrawable(getResources().getDrawable(R.drawable.icon_nav_bar_close));

        img_nav_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               onBackPressed();
            }
        });

        img_login_logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!AppController.getSpIsLogin().getBoolean(SPUtils.IS_LOGIN, false))
                    startActivity(new Intent(MyOrdersDetails_Activity.this, Login_Activity.class));
                else
                    AppUtils.showDialogSignOut(MyOrdersDetails_Activity.this);
            }
        });

        if (AppController.getSpIsLogin().getBoolean(SPUtils.IS_LOGIN, false))
            img_login_logout.setImageDrawable(getResources().getDrawable(R.drawable.icon_logout_orange));
        else
            img_login_logout.setImageDrawable(getResources().getDrawable(R.drawable.icon_distributor_login_orange));
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.myordersdetails_activity);

        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
        SetupToolbar();
        try {

            listView = findViewById(R.id.listView);
            layout_listView = findViewById(R.id.layout_listView);
            layout_nodata = findViewById(R.id.layout_nodata);

            txt_orderNo = findViewById(R.id.txt_orderNo);
            txt_orderDate = findViewById(R.id.txt_orderDate);
            txt_orderStatus = findViewById(R.id.txt_orderStatus);
            txt_orderAmount = findViewById(R.id.txt_orderAmount);
            txt_OrderBV = findViewById(R.id.txt_OrderBV);

            RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
            listView.setLayoutManager(mLayoutManager);
            listView.setItemAnimator(new DefaultItemAnimator());

            lay_bv = findViewById(R.id.lay_bv);


            if (AppUtils.isNetworkAvailable(MyOrdersDetails_Activity.this)) {
                executeGetMyOrdersDetailsRequest();
            } else {
                showNoData();
                AppUtils.alertDialog(MyOrdersDetails_Activity.this, getResources().getString(R.string.txt_networkAlert));
            }

            txt_orderNo.setText(MyOrdersList_Adapter.ordersList.get(getIntent().getExtras().getInt("position")).get("OrderNo"));
            txt_orderDate.setText(AppUtils.getDateFromAPIDate(MyOrdersList_Adapter.ordersList.get(getIntent().getExtras().getInt("position")).get("ODate")));
            txt_orderStatus.setText(WordUtils.capitalizeFully(MyOrdersList_Adapter.ordersList.get(getIntent().getExtras().getInt("position")).get("OrderStatus")));
            txt_orderAmount.setText("₹ " + MyOrdersList_Adapter.ordersList.get(getIntent().getExtras().getInt("position")).get("TotalAmount"));

            txt_OrderBV.setText("");
            lay_bv.setVisibility(View.GONE);

            String Usertype = (AppController.getSpUserInfo().getString(SPUtils.USER_TYPE, ""));

            if (Usertype.equalsIgnoreCase("DISTRIBUTOR")) {
                lay_bv.setVisibility(View.VISIBLE);
                txt_OrderBV.setText(MyOrdersList_Adapter.ordersList.get(getIntent().getExtras().getInt("position")).get("OrderQvp"));
            }


        } catch (Exception e) {
            e.printStackTrace();
            AppUtils.showExceptionDialog(MyOrdersDetails_Activity.this);
        }
    }

    private void executeGetMyOrdersDetailsRequest() {
        try {
            if (AppUtils.isNetworkAvailable(MyOrdersDetails_Activity.this)) {
                new AsyncTask<Void, Void, String>() {
                    protected void onPreExecute() {
                        AppUtils.showProgressDialog(MyOrdersDetails_Activity.this);
                    }

                    @Override
                    protected String doInBackground(Void... params) {
                        String response = "";
                        try {
                            List<NameValuePair> postParameters = new ArrayList<>();
                            postParameters.add(new BasicNameValuePair("OrderNo", MyOrdersList_Adapter.ordersList.get(getIntent().getExtras().getInt("position")).get("OrderNo")));
                            response = AppUtils.callWebServiceWithMultiParam(MyOrdersDetails_Activity.this, postParameters, QueryUtils.methodToGetViewOrdersDetails, TAG);
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
                                JSONArray jsonArrayData = jsonObject.getJSONArray("Data");

                                if (jsonArrayData.length() > 0) {
                                    getOrdersDetailListResult(jsonArrayData);
                                } else {
                                    showNoData();
                                }
                            } else {
                                AppUtils.alertDialog(MyOrdersDetails_Activity.this, jsonObject.getString("Message"));
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            AppUtils.showExceptionDialog(MyOrdersDetails_Activity.this);
                        }
                    }
                }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void getOrdersDetailListResult(JSONArray jsonArray) {
        try {
            ordersDetailList.clear();

            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                HashMap<String, String> map = new HashMap<>();
                map.put("OrderNo", "" + jsonObject.getString("OrderNo"));
                map.put("ProductID", "" + jsonObject.getString("ProductID"));
                map.put("ProductName", "" + jsonObject.getString("ProductName"));
                map.put("Size", "" + jsonObject.getString("Size"));
                map.put("Color", "" + jsonObject.getString("Color"));
                map.put("Qty", "" + jsonObject.getString("Qty"));
                map.put("Netamount", "" + jsonObject.getString("Netamount"));
                map.put("ImgPath", "" + jsonObject.getString("ImgPath"));
                map.put("ProdStatus", "" + jsonObject.getString("ProdStatus"));

//                map.put("bv", ""+jsonObject.getString("bv"));
//                map.put("ProdType", ""+jsonObject.getString("ProdType"));
//                map.put("DP", ""+jsonObject.getString("DP"));
//                map.put("CVP", ""+jsonObject.getString("CVP"));
//                map.put("ShipCharges", ""+jsonObject.getString("ShipCharges"));
//                map.put("totalAmt", ""+jsonObject.getString("totalAmt"));

                ordersDetailList.add(map);
            }

            showListView();
        } catch (Exception e) {
            e.printStackTrace();
            AppUtils.showExceptionDialog(MyOrdersDetails_Activity.this);
        }
    }

    private void showListView() {
        try {
            if (ordersDetailList.size() > 0) {
                adapter = new MyOrdersDetailList_Adapter(MyOrdersDetails_Activity.this, ordersDetailList);
                listView.setAdapter(adapter);
                adapter.notifyDataSetChanged();

                layout_listView.setVisibility(View.VISIBLE);
                listView.setVisibility(View.VISIBLE);
                layout_nodata.setVisibility(View.GONE);
            } else {
                showNoData();
                AppUtils.showExceptionDialog(MyOrdersDetails_Activity.this);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void showNoData() {
        try {
            layout_listView.setVisibility(View.GONE);
            listView.setVisibility(View.GONE);
            layout_nodata.setVisibility(View.VISIBLE);
        } catch (Exception e) {
            e.printStackTrace();
            AppUtils.showExceptionDialog(MyOrdersDetails_Activity.this);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        try {
            AppUtils.dismissProgressDialog();
            //overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        } catch (Exception e) {
            e.printStackTrace();
            AppUtils.showExceptionDialog(MyOrdersDetails_Activity.this);
        }
    }
}
