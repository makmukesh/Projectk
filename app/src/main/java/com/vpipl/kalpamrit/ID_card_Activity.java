package com.vpipl.kalpamrit;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;

import com.vpipl.kalpamrit.Utils.AppUtils;
import com.vpipl.kalpamrit.Utils.QueryUtils;
import com.vpipl.kalpamrit.Utils.SPUtils;

import org.apache.commons.lang3.text.WordUtils;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by PC14 on 27/10/2018.
 */
public class ID_card_Activity extends AppCompatActivity {

    private String TAG = "ID_card_Activity";


    ImageView img_nav_back, img_login_logout,img_cart;

    public void SetupToolbar() {

        img_nav_back = findViewById(R.id.img_nav_back);
        img_login_logout = findViewById(R.id.img_login_logout);
        img_cart = findViewById(R.id.img_cart);

        img_cart.setVisibility(View.GONE);

        img_nav_back.setImageDrawable(getResources().getDrawable(R.drawable.icon_nav_bar_close));

        img_nav_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    finish();
            }
        });

        img_login_logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!AppController.getSpIsLogin().getBoolean(SPUtils.IS_LOGIN, false))
                    startActivity(new Intent(ID_card_Activity.this, Login_Activity.class));
                else
                    AppUtils.showDialogSignOut(ID_card_Activity.this);
            }
        });

        if (AppController.getSpIsLogin().getBoolean(SPUtils.IS_LOGIN, false))
            img_login_logout.setImageDrawable(getResources().getDrawable(R.drawable.icon_logout_orange));
        else
            img_login_logout.setImageDrawable(getResources().getDrawable(R.drawable.icon_distributor_login_orange));
    }
    Button btn_download ;
    TextView  tv_mem_name ,tv_rank_name ,tv_mem_mobileno ,tv_mem_emailid ,txt_heading ,tv_idno;
    ImageView iv_bc_img ;
    ScrollView sv_detail ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_business_card);

        try {
            AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);

            Toolbar toolbar = findViewById(R.id.toolbar);
            setSupportActionBar(toolbar);
            getSupportActionBar().setTitle("");
            SetupToolbar();

            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

            tv_mem_name = findViewById(R.id.tv_mem_name);
            tv_rank_name = findViewById(R.id.tv_rank_name);
            tv_mem_mobileno = findViewById(R.id.tv_mem_mobileno);
            tv_mem_emailid = findViewById(R.id.tv_mem_emailid);
            iv_bc_img = findViewById(R.id.iv_bc_img);
            sv_detail = findViewById(R.id.sv_detail);
            txt_heading = findViewById(R.id.txt_heading);
            tv_idno = findViewById(R.id.tv_idno);

            String bytecode = AppController.getSpUserInfo().getString(SPUtils.USER_profile_pic_byte_code, "");

            if (!bytecode.equalsIgnoreCase("")) {
                iv_bc_img.setImageBitmap(AppUtils.getBitmapFromString(bytecode));
            }


            if (AppUtils.isNetworkAvailable(ID_card_Activity.this)) {
                //executeToGetBusinessCardInfo();
                executeGetDashBoardDetails();
            } else {
                AppUtils.alertDialog(ID_card_Activity.this, getResources().getString(R.string.txt_networkAlert));
            }

            btn_download = findViewById(R.id.btn_download) ;
            findViewById(R.id.btn_download).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                 /*   Bitmap bitmap = takeScreenshot();
                    saveBitmap(bitmap);*/
                    sv_detail.post(new Runnable() {
                        @Override
                        public void run() {
                            sv_detail.fullScroll(ScrollView.FOCUS_UP);
                        }
                    });
                    btn_download.setVisibility(View.GONE);
                    txt_heading.setVisibility(View.GONE);
                    takeScreenshot();
                }
            });


        } catch (Exception e) {
            e.printStackTrace();
            AppUtils.showExceptionDialog(ID_card_Activity.this);
        }
    }

/*    public Bitmap takeScreenshot() {
        View rootView = findViewById(android.R.id.content).getRootView();
        rootView.setDrawingCacheEnabled(true);
        return rootView.getDrawingCache();
    }*/
    public void saveBitmap(Bitmap bitmap) {
        File imagePath = new File(Environment.getExternalStorageDirectory() + "/screenshot.png");
        FileOutputStream fos;
        try {
            fos = new FileOutputStream(imagePath);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            fos.flush();
            fos.close();
        } catch (FileNotFoundException e) {
            Log.e("GREC", e.getMessage(), e);
        } catch (IOException e) {
            Log.e("GREC", e.getMessage(), e);
        }
    }

    private void executeToGetBusinessCardInfo() {
        try {
            if (AppUtils.isNetworkAvailable(ID_card_Activity.this)) {
                new AsyncTask<Void, Void, String>() {
                    protected void onPreExecute() {
                        AppUtils.showProgressDialog(ID_card_Activity.this);
                    }

                    @Override
                    protected String doInBackground(Void... params) {
                        String response = "";
                        try {
                            List<NameValuePair> postParameters = new ArrayList<>();
                            postParameters.add(new BasicNameValuePair("Formno", AppController.getSpUserInfo().getString(SPUtils.USER_FORM_NUMBER, "")));
                            response = AppUtils.callWebServiceWithMultiParam(ID_card_Activity.this, postParameters, QueryUtils.methodToBuniessCard, TAG);
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
                                    setDetails(jsonArrayData);
                                } else {
                                    AppUtils.alertDialog(ID_card_Activity.this, jsonObject.getString("Message"));
                                }
                            } else {
                                AppUtils.alertDialog(ID_card_Activity.this, jsonObject.getString("Message"));
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            AppUtils.showExceptionDialog(ID_card_Activity.this);
                        }
                    }
                }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            }
        } catch (Exception e) {
            e.printStackTrace();
            AppUtils.showExceptionDialog(ID_card_Activity.this);
        }
    }

    private void setDetails(JSONArray jsonArray) {
        try {

            if(jsonArray.length() > 0) {

                tv_mem_name.setText("" + WordUtils.capitalizeFully(jsonArray.getJSONObject(0).getString("MemName")));
                tv_rank_name.setText("" + WordUtils.capitalizeFully(jsonArray.getJSONObject(0).getString("Rank")));
                tv_mem_mobileno.setText("" + WordUtils.capitalizeFully(jsonArray.getJSONObject(0).getString("MobileNo")));
                tv_mem_emailid.setText("" + jsonArray.getJSONObject(0).getString("Email"));
              /*  txt_trans_pwd.setText("" + jsonArray.getJSONObject(0).getString("Photo"));
                txt_spo_id.setText("" + jsonArray.getJSONObject(0).getString("UpLnId"));
                txt_placeunder_id.setText("" + jsonArray.getJSONObject(0).getString("RefId"));*/

            }
        } catch (Exception e) {
            e.printStackTrace();
            AppUtils.showExceptionDialog(ID_card_Activity.this);
        }
    }

    private void executeGetDashBoardDetails() {
        try {
            if (AppUtils.isNetworkAvailable(ID_card_Activity.this)) {
                new AsyncTask<Void, Void, String>() {
                    @Override
                    protected void onPreExecute() {
                        super.onPreExecute();
                        AppUtils.showProgressDialog(ID_card_Activity.this);
                    }

                    @Override
                    protected String doInBackground(Void... params) {
                        String response = "";
                        try {
                            Thread.currentThread().setPriority(Thread.MAX_PRIORITY);

                            List<NameValuePair> postParameters = new ArrayList<>();
                            postParameters.add(new BasicNameValuePair("FormNo", AppController.getSpUserInfo().getString(SPUtils.USER_FORM_NUMBER, "")));

                            response = AppUtils.callWebServiceWithMultiParam(ID_card_Activity.this, postParameters, QueryUtils.methodToGetDashboardDetail, TAG);
                        } catch (Exception e) {
                            e.printStackTrace();
                            AppUtils.showExceptionDialog(ID_card_Activity.this);
                        }

                        return response;
                    }

                    @Override
                    protected void onPostExecute(String resultData) {
                        AppUtils.dismissProgressDialog();
                        try {
                            JSONObject jsonObject = new JSONObject(resultData);

                            JSONArray jsonArrayMembers = jsonObject.getJSONArray("Members");
                            JSONArray jsonArrayMemberRank = jsonObject.getJSONArray("MemberRank");

                            if (jsonObject.getString("Status").equalsIgnoreCase("True")) {
                                WriteValues(jsonArrayMembers ,jsonArrayMemberRank);
                            } else {
                                AppUtils.alertDialog(ID_card_Activity.this, jsonObject.getString("Message"));
                                if (AppUtils.showLogs)
                                    Log.v(TAG, "executeGetKYCUploadRequest executed...Failed... called");
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            AppUtils.showExceptionDialog(ID_card_Activity.this);
                        }
                    }
                }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            }
        } catch (Exception e) {
            e.printStackTrace();
            AppUtils.showExceptionDialog(ID_card_Activity.this);
        }
    }

    private void WriteValues(JSONArray jsonArrayMembers,JSONArray jsonArrayMemberRank ) {

        try {
            tv_mem_name.setText("" + AppController.getSpUserInfo().getString(SPUtils.USER_FIRST_NAME, ""));
            tv_mem_mobileno.setText("" + AppController.getSpUserInfo().getString(SPUtils.USER_MOBILE_NO, ""));
            tv_mem_emailid.setText("" + AppController.getSpUserInfo().getString(SPUtils.USER_EMAIL, ""));
            tv_idno.setText("" + AppController.getSpUserInfo().getString(SPUtils.USER_ID_NUMBER, ""));

            if (jsonArrayMemberRank.length() > 0) {
                tv_rank_name.setText("Rank : " + jsonArrayMemberRank.getJSONObject(0).getString("MaxPBSlab"));
            }
            else {
                tv_rank_name.setText("Rank : NA");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    @Override
    protected void onDestroy() {
        super.onDestroy();

        try {
            AppUtils.dismissProgressDialog();
            ////overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        } catch (Exception e) {
            e.printStackTrace();
            AppUtils.showExceptionDialog(ID_card_Activity.this);
        }

        System.gc();
    }
    private void takeScreenshot() {
        Date now = new Date();
        android.text.format.DateFormat.format("yyyy-MM-dd_hh:mm:ss", now);

        try {
            // image naming and path  to include sd card  appending name you choose for file
            String mPath = Environment.getExternalStorageDirectory().toString() + "/" + now + ".jpg";

            // create bitmap screen capture
            View v1 = getWindow().getDecorView().getRootView();
            v1.setDrawingCacheEnabled(true);
            Bitmap bitmap = Bitmap.createBitmap(v1.getDrawingCache());
            v1.setDrawingCacheEnabled(false);

            File imageFile = new File(mPath);

            FileOutputStream outputStream = new FileOutputStream(imageFile);
            int quality = 100;
            bitmap.compress(Bitmap.CompressFormat.JPEG, quality, outputStream);
            outputStream.flush();
            outputStream.close();

            openScreenshot(imageFile);
        } catch (Throwable e) {
            // Several error may come out with file handling or DOM
            e.printStackTrace();
        }
    }
    private void openScreenshot(File imageFile) {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_VIEW);
        Uri uri = Uri.fromFile(imageFile);
        intent.setDataAndType(uri, "image/*");
        startActivity(intent);
    }
}
