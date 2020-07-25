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
import com.vpipl.kalpamrit.Adapters.Download_Adapter;
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
public class DownloadSection_Activity extends AppCompatActivity {
    private static Download_Adapter adapter;
    private String TAG = "DownloadSection_Activity";
    private RecyclerView listView;
    private LinearLayout layout_listView;
    private LinearLayout layout_nodata;
    private ArrayList<HashMap<String, String>> array_list = new ArrayList<>();

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
                    startActivity(new Intent(DownloadSection_Activity.this, Login_Activity.class));
                else
                    AppUtils.showDialogSignOut(DownloadSection_Activity.this);
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
        setContentView(R.layout.activity_download);

        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
        SetupToolbar();

        try {

            listView = findViewById(R.id.listView);
            layout_listView = findViewById(R.id.layout_listView);
            layout_nodata = findViewById(R.id.layout_nodata);
            RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
            listView.setLayoutManager(mLayoutManager);
            listView.setItemAnimator(new DefaultItemAnimator());

            if (AppUtils.isNetworkAvailable(DownloadSection_Activity.this)) {
                executeGetDownloadsectionRequest();
            } else {
                AppUtils.alertDialog(DownloadSection_Activity.this, getResources().getString(R.string.txt_networkAlert));
            }
        } catch (Exception e) {
            e.printStackTrace();
            AppUtils.showExceptionDialog(DownloadSection_Activity.this);
        }
    }
    
    private void executeGetDownloadsectionRequest() {
        try {
            if (AppUtils.isNetworkAvailable(DownloadSection_Activity.this)) {
                new AsyncTask<Void, Void, String>() {
                    protected void onPreExecute() {
                        AppUtils.showProgressDialog(DownloadSection_Activity.this);
                    }

                    @Override
                    protected String doInBackground(Void... params) {
                        String response = "";
                        try {
                            List<NameValuePair> postParameters = new ArrayList<>();
                            response = AppUtils.callWebServiceWithMultiParam(DownloadSection_Activity.this, postParameters, QueryUtils.methodtoSelectDownLoadFileList, TAG);
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
                                    getdownloadListResult(jsonArrayData);
                                } else {
                                    AppUtils.alertDialog(DownloadSection_Activity.this, jsonObject.getString("Message"));
                                }
                            } else {
                                AppUtils.alertDialog(DownloadSection_Activity.this, jsonObject.getString("Message"));
                                if (AppUtils.showLogs)
                                    Log.v(TAG, "executeGetMyOrdersRequest executed...Failed... called");
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            AppUtils.showExceptionDialog(DownloadSection_Activity.this);
                        }
                    }
                }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            }
        } catch (Exception e) {
            e.printStackTrace();
            AppUtils.showExceptionDialog(DownloadSection_Activity.this);
        }
    }

    private void getdownloadListResult(JSONArray jsonArray) {
        try {
            array_list.clear();

            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);

                HashMap<String, String> map = new HashMap<>();
                map.put("Autoid", "" + jsonObject.getString("Autoid"));
                map.put("FileHeading", "" + jsonObject.getString("FileHeading"));
                map.put("Description", "" + jsonObject.getString("Description"));
                map.put("File_URL", getResources().getString(R.string.downloadFileURL)+"" + jsonObject.getString("Image"));

                array_list.add(map);
            }

            if (AppUtils.showLogs) Log.v(TAG, "array_list..." + array_list);
            showListView();
        } catch (Exception e) {
            e.printStackTrace();
            AppUtils.showExceptionDialog(DownloadSection_Activity.this);
        }
    }

    private void showListView() {
        try {
            if (array_list.size() > 0) {
              //  adapter = new ActiversListGrid_Adapter(DownloadSection_Activity.this, array_list, "Grid");

                adapter = new Download_Adapter(DownloadSection_Activity.this, array_list);
                listView.setAdapter(adapter);
                adapter.notifyDataSetChanged();

                layout_listView.setVisibility(View.VISIBLE);
                layout_nodata.setVisibility(View.GONE);
            } else {
                showNoData();
            }
        } catch (Exception e) {
            e.printStackTrace();
            AppUtils.showExceptionDialog(DownloadSection_Activity.this);
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
            AppUtils.showExceptionDialog(DownloadSection_Activity.this);
        }
    }

    private void showNoData() {
        try {
            layout_listView.setVisibility(View.GONE);
            layout_nodata.setVisibility(View.VISIBLE);
        } catch (Exception e) {
            e.printStackTrace();
            AppUtils.showExceptionDialog(DownloadSection_Activity.this);
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
            AppUtils.showExceptionDialog(DownloadSection_Activity.this);
        }
    }
}
