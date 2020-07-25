package com.vpipl.kalpamrit;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.vpipl.kalpamrit.Adapters.ActiversListGrid_Adapter;
import com.vpipl.kalpamrit.Adapters.ProductListGrid_Adapter;
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
public class MyAchivers_Activity extends AppCompatActivity {
    private static ActiversListGrid_Adapter adapter;
    private String TAG = "MyAchivers_Activity";
  //  private RecyclerView listView;
    private LinearLayout layout_listView;
    private LinearLayout layout_nodata;
    private ArrayList<HashMap<String, String>> achiversList = new ArrayList<>();
    private ListView list_products;
    private GridView gridView_products;

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
                    startActivity(new Intent(MyAchivers_Activity.this, Login_Activity.class));
                else
                    AppUtils.showDialogSignOut(MyAchivers_Activity.this);
            }
        });

        if (AppController.getSpIsLogin().getBoolean(SPUtils.IS_LOGIN, false))
            img_login_logout.setImageDrawable(getResources().getDrawable(R.drawable.icon_logout_orange));
        else
            img_login_logout.setImageDrawable(getResources().getDrawable(R.drawable.icon_distributor_login_orange));
    }
    TextInputEditText txt_achiever_category ;
    String selectOrderArray[],str_order_sts = "1" , order_sts_short = "0";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_achivers);

        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
        SetupToolbar();

        try {

         //   listView = findViewById(R.id.listView);
            txt_achiever_category = findViewById(R.id.txt_achiever_category);
            layout_listView = findViewById(R.id.layout_listView);
            layout_nodata = findViewById(R.id.layout_nodata);
            list_products = findViewById(R.id.list_products);
            gridView_products = findViewById(R.id.gridView_products);
            RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
          //  listView.setLayoutManager(mLayoutManager);
          //  listView.setItemAnimator(new DefaultItemAnimator());


            txt_achiever_category.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (AppUtils.isNetworkAvailable(MyAchivers_Activity.this)) {
                        executeStateRequest();
                    } else {
                       AppUtils.alertDialog(MyAchivers_Activity.this, getResources().getString(R.string.txt_networkAlert));
                    }
                }
            });

            if (AppUtils.isNetworkAvailable(MyAchivers_Activity.this)) {
             //   executeStateRequest();
                executeGetMyachiversRequest();
            } else {
             //   showNoData();
                AppUtils.alertDialog(MyAchivers_Activity.this, getResources().getString(R.string.txt_networkAlert));
            }
        } catch (Exception e) {
            e.printStackTrace();
            AppUtils.showExceptionDialog(MyAchivers_Activity.this);
        }
    }
   /* private void showOrderStsDialog() {
        try {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Select Achiver Category");
            builder.setItems(AppController.RewardList, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int item) {
                    // Do something with the selection
                    txt_achiever_category.setText(AppController.RewardList[item]);
                    str_order_sts = selectOrderArray[item] ;
                    OrderStsRpt();
                    if (StateID.equalsIgnoreCase("0"))
                        for (int i = 0; i < AppController.RewardList.size(); i++) {
                            if (txt_achiever_category.getText().toString().equalsIgnoreCase(AppController.RewardList.get(i).get("Reward"))) {
                                str_order_sts = AppController.RewardList.get(i).get("RewardId");
                            }
                        }
                    executeGetMyachiversRequest();
                }
            });
            builder.create().show();
        } catch (Exception e) {
            e.printStackTrace();
            AppUtils.showExceptionDialog(MyAchivers_Activity.this);
        }
    }*/
    /***********************************************************************************************/
    private void executeStateRequest() {
        new AsyncTask<Void, Void, String>() {
            protected void onPreExecute() {
                AppUtils.showProgressDialog(MyAchivers_Activity.this);
            }

            @Override
            protected String doInBackground(Void... params) {
                String response = "";
                try {
                    List<NameValuePair> postParameters = new ArrayList<>();
                    response = AppUtils.callWebServiceWithMultiParam(MyAchivers_Activity.this, postParameters, QueryUtils.methodToGetRewardsNameList, TAG);
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
                    JSONArray jsonArrayData = jsonObject.getJSONArray("Data");

                    if (jsonObject.getString("Status").equalsIgnoreCase("True")) {
                        if (jsonArrayData.length() != 0) {
                            getStateResult(jsonArrayData);
                        } else {
                            AppUtils.alertDialog(MyAchivers_Activity.this, jsonObject.getString("Message"));
                        }
                    } else {
                        AppUtils.alertDialog(MyAchivers_Activity.this, jsonObject.getString("Message"));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }
    private void getStateResult(JSONArray jsonArray) {
        try {
            AppController.RewardList.clear();

            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                HashMap<String, String> map = new HashMap<>();

                map.put("RewardId", jsonObject.getString("RewardId"));
                map.put("Reward", WordUtils.capitalizeFully(jsonObject.getString("Reward")));

                AppController.RewardList.add(map);
            }

            showStateDialog();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private void showStateDialog() {
        try {
            final String[] stateArray = new String[AppController.RewardList.size()];
            for (int i = 0; i < AppController.RewardList.size(); i++) {
                stateArray[i] = AppController.RewardList.get(i).get("Reward");
            }

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Select Achiver Category");
            builder.setItems(stateArray, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int item) {
                    // Do something with the selection
                    txt_achiever_category.setText(stateArray[item]);

                //    str_order_sts = stateArray[item] ;
                    if (str_order_sts.equalsIgnoreCase("1"))
                        for (int i = 0; i < AppController.RewardList.size(); i++) {
                            if (txt_achiever_category.getText().toString().equalsIgnoreCase(AppController.RewardList.get(i).get("Reward"))) {
                                str_order_sts = AppController.RewardList.get(i).get("RewardId");
                            }
                        }
                    executeGetMyachiversRequest();

                }
            });
            builder.create().show();
        } catch (Exception e) {
            e.printStackTrace();
            AppUtils.showExceptionDialog(MyAchivers_Activity.this);
        }
    }
    /***********************************************************************************************/
    
    
    
    private void executeGetMyachiversRequest() {
        try {
            if (AppUtils.isNetworkAvailable(MyAchivers_Activity.this)) {
                new AsyncTask<Void, Void, String>() {
                    protected void onPreExecute() {
                        AppUtils.showProgressDialog(MyAchivers_Activity.this);
                    }

                    @Override
                    protected String doInBackground(Void... params) {
                        String response = "";
                        try {
                            List<NameValuePair> postParameters = new ArrayList<>();
                            postParameters.add(new BasicNameValuePair("RewardID", ""+ str_order_sts));
                            response = AppUtils.callWebServiceWithMultiParam(MyAchivers_Activity.this, postParameters, QueryUtils.methodToGetViewachiversList, TAG);
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
                                    getachiversListResult(jsonArrayData);
                                } else {
                                    AppUtils.alertDialog(MyAchivers_Activity.this, jsonObject.getString("Message"));
                                }
                            } else {
                                AppUtils.alertDialog(MyAchivers_Activity.this, jsonObject.getString("Message"));
                                if (AppUtils.showLogs)
                                    Log.v(TAG, "executeGetMyOrdersRequest executed...Failed... called");
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            AppUtils.showExceptionDialog(MyAchivers_Activity.this);
                        }
                    }
                }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            }
        } catch (Exception e) {
            e.printStackTrace();
            AppUtils.showExceptionDialog(MyAchivers_Activity.this);
        }
    }

    private void getachiversListResult(JSONArray jsonArray) {
        try {
            achiversList.clear();

            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);

                HashMap<String, String> map = new HashMap<>();
                map.put("achiever_name", "" + jsonObject.getString("Name"));
                map.put("achiever_City", "" + jsonObject.getString("City"));
                map.put("achiever_State", "" + jsonObject.getString("State"));
                map.put("achiever_Pic", getResources().getString(R.string.productImageURL)+"" + jsonObject.getString("Pic"));

                achiversList.add(map);
            }

            if (AppUtils.showLogs) Log.v(TAG, "achiversList..." + achiversList);
            showListView();
        } catch (Exception e) {
            e.printStackTrace();
            AppUtils.showExceptionDialog(MyAchivers_Activity.this);
        }
    }

    private void showListView() {
        try {
            if (achiversList.size() > 0) {
               // list_products.setVisibility(View.GONE);

                adapter = new ActiversListGrid_Adapter(MyAchivers_Activity.this, achiversList, "Grid");

                gridView_products.setAdapter(adapter);


/*
                adapter = new ActiversListGrid_Adapter(MyAchivers_Activity.this, achiversList);
                listView.setAdapter(adapter);
                adapter.notifyDataSetChanged();*/

                layout_listView.setVisibility(View.VISIBLE);
                gridView_products.setVisibility(View.VISIBLE);
                layout_nodata.setVisibility(View.GONE);
            } else {
                showNoData();
            }
        } catch (Exception e) {
            e.printStackTrace();
            AppUtils.showExceptionDialog(MyAchivers_Activity.this);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        try {
            if (adapter != null)
                adapter.notifyDataSetChanged();
        } catch (Exception e) {
            e.printStackTrace();
            AppUtils.showExceptionDialog(MyAchivers_Activity.this);
        }
    }

    private void showNoData() {
        try {
            layout_listView.setVisibility(View.GONE);
        //    listView.setVisibility(View.GONE);
            layout_nodata.setVisibility(View.VISIBLE);
        } catch (Exception e) {
            e.printStackTrace();
            AppUtils.showExceptionDialog(MyAchivers_Activity.this);
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
            AppUtils.showExceptionDialog(MyAchivers_Activity.this);
        }
    }
}
