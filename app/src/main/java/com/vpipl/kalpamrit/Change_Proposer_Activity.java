package com.vpipl.kalpamrit;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.widget.Toolbar;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.vpipl.kalpamrit.SMS.MySMSBroadcastReceiver;
import com.vpipl.kalpamrit.Utils.AppUtils;
import com.vpipl.kalpamrit.Utils.QueryUtils;
import com.vpipl.kalpamrit.Utils.SPUtils;
import com.vpipl.kalpamrit.Utils.SmsListener;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class Change_Proposer_Activity extends AppCompatActivity {

    private static final String TAG = "Change_Proposer_Activity";
    private ScrollView sc_change_proposer,sc_change_proposer_otp;
    private TextInputEditText edtxt_idno ,edtxt_new_proposer_id,edtxt_remarks;
    private Button btn_proceed ,btn_reset;
    private String new_proposer_id;
    private String remarks;
    /*After OTP*/
    private TextInputEditText edtxt_user_otp;
    private Button btn_otp_submit;
    private String user_otp , OTP;


    @Override
    public void onBackPressed() {
        if(sc_change_proposer.getVisibility() == View.GONE){
            sc_change_proposer.setVisibility(View.VISIBLE);
            sc_change_proposer_otp.setVisibility(View.GONE);
            edtxt_user_otp.setText("");
        }
        else {
             super.onBackPressed();
        }
    }

    ImageView img_nav_back, img_login_logout;

    public void SetupToolbar() {

        img_nav_back = findViewById(R.id.img_nav_back);
        img_login_logout = findViewById(R.id.img_login_logout);

        img_nav_back.setImageDrawable(getResources().getDrawable(R.drawable.icon_nav_bar_close));

        img_nav_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(sc_change_proposer.getVisibility() == View.GONE){
                    sc_change_proposer.setVisibility(View.VISIBLE);
                    sc_change_proposer_otp.setVisibility(View.GONE);
                    edtxt_user_otp.setText("");
                }
                else {
                    onBackPressed();
                }
            }
        });

        img_login_logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!AppController.getSpIsLogin().getBoolean(SPUtils.IS_LOGIN, false))
                    startActivity(new Intent(Change_Proposer_Activity.this, Login_Activity.class));
                else
                    AppUtils.showDialogSignOut(Change_Proposer_Activity.this);
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
        setContentView(R.layout.activity_change_proposer);

        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
        SetupToolbar();

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
 
        //mobile = AppController.getSpUserInfo().getString(SPUtils.USER_MOBILE_NO, "");
        sc_change_proposer = findViewById(R.id.sc_change_proposer);
        sc_change_proposer_otp = findViewById(R.id.sc_change_proposer_otp);
        edtxt_idno = findViewById(R.id.edtxt_idno);
        edtxt_new_proposer_id = findViewById(R.id.edtxt_new_proposer_id);
        edtxt_remarks = findViewById(R.id.edtxt_remarks);
        btn_proceed = findViewById(R.id.btn_proceed);
        btn_reset = findViewById(R.id.btn_reset);

        edtxt_user_otp = findViewById(R.id.edtxt_user_otp);
        btn_otp_submit = findViewById(R.id.btn_otp_submit);

        sc_change_proposer.setVisibility(View.VISIBLE);
        sc_change_proposer_otp.setVisibility(View.GONE);

        edtxt_idno.setText("" + AppController.getSpUserInfo().getString(SPUtils.USER_ID_NUMBER, ""));
        btn_proceed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AppUtils.hideKeyboardOnClick(Change_Proposer_Activity.this, v);
                ValidateData();
            }
        });
        btn_reset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AppUtils.hideKeyboardOnClick(Change_Proposer_Activity.this, v);
                edtxt_new_proposer_id.setText("");
                edtxt_remarks.setText("");
                new_proposer_id = "";
                remarks = "";
            }
        });
        MySMSBroadcastReceiver.bindListener(new SmsListener() {
            @Override
            public void messageReceived(String messageText) {
                try {
                    if (messageText.length() == 6) {
                        edtxt_user_otp.setText(messageText);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        btn_otp_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AppUtils.hideKeyboardOnClick(Change_Proposer_Activity.this, v);
                ValidateDataOtp();
            }
        });
    }

    private void ValidateData() {
        new_proposer_id = edtxt_new_proposer_id.getText().toString().trim();
        remarks = edtxt_remarks.getText().toString().trim();

        boolean cancel = false;
        View focusView = null;

        if (TextUtils.isEmpty(new_proposer_id)) {
            AppUtils.alertDialog(Change_Proposer_Activity.this, "Please Enter New Proposer ID");
            focusView = edtxt_new_proposer_id;
            cancel = true;
        } else if (TextUtils.isEmpty(remarks)) {
            AppUtils.alertDialog(Change_Proposer_Activity.this, "Please Enter Remarks");
            focusView = edtxt_remarks;
            cancel = true;
        }

        if (cancel) {
            focusView.requestFocus();
        } else {
            if (AppUtils.isNetworkAvailable(Change_Proposer_Activity.this)) {
                Runnable runnable = new Runnable() {
                    @Override
                    public void run() {
                        createChangeProposerRequest();
                    }
                };
                new Handler().postDelayed(runnable, 500);
            } else {
                AppUtils.alertDialog(Change_Proposer_Activity.this, getResources().getString(R.string.txt_networkAlert));
            }
        }
    }

    private void createChangeProposerRequest() {
        try {
            List<NameValuePair> postParameters = new ArrayList<>();
            postParameters.add(new BasicNameValuePair("IDNo", "" + AppController.getSpUserInfo().getString(SPUtils.USER_ID_NUMBER, "")));
            postParameters.add(new BasicNameValuePair("ProposerID", new_proposer_id));
           // postParameters.add(new BasicNameValuePair("remarks", remarks));
           // postParameters.add(new BasicNameValuePair("DeviceID", Settings.Secure.getString(getApplicationContext().getContentResolver(), Settings.Secure.ANDROID_ID)));
            postParameters.add(new BasicNameValuePair("MobileNo", "9983333276"));
          //  postParameters.add(new BasicNameValuePair("IDNo", "" + AppController.getSpUserInfo().getString(SPUtils.USER_MOBILE_NO, "")));
            executeChangeProposerRequest(postParameters);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void executeChangeProposerRequest(final List<NameValuePair> postParameters) {
        try {
            if (AppUtils.isNetworkAvailable(Change_Proposer_Activity.this)) {
                new AsyncTask<Void, Void, String>() {
                    protected void onPreExecute() {
                        AppUtils.showProgressDialog(Change_Proposer_Activity.this);
                    }

                    @Override
                    protected String doInBackground(Void... params) {
                        String response = null;
                        try {
                            response = AppUtils.callWebServiceWithMultiParam(Change_Proposer_Activity.this, postParameters, QueryUtils.methodCheckProposerWithOTP, TAG);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        return response;
                    }

                    @Override
                    protected void onPostExecute(String resultData) {
                        try {
                            AppUtils.dismissProgressDialog();

                            AppUtils.dismissProgressDialog();
                            JSONObject jsonObject = new JSONObject(resultData);

                            if (jsonObject.getString("Status").equalsIgnoreCase("True")) {
                                JSONArray jsonArrayData = jsonObject.getJSONArray("Data");
                                if (jsonArrayData.length() != 0) {
                                    OTP = jsonArrayData.getJSONObject(0).getString("OTP");
                                    Log.e("ServerOTP" , OTP);
                                    sc_change_proposer.setVisibility(View.GONE);
                                    sc_change_proposer_otp.setVisibility(View.VISIBLE);
                                } else {
                                    AppUtils.alertDialog(Change_Proposer_Activity.this, jsonObject.getString("Message"));
                                }
                            } else {
                                AppUtils.alertDialog(Change_Proposer_Activity.this, jsonObject.getString("Message"));
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            AppUtils.showExceptionDialog(Change_Proposer_Activity.this);
                        }
                    }
                }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            }
        } catch (Exception e) {
            e.printStackTrace();
            AppUtils.showExceptionDialog(Change_Proposer_Activity.this);
        }
    }

    /*After OTP Recieving */
    private void ValidateDataOtp() {
        user_otp = edtxt_user_otp.getText().toString().trim();

        boolean cancel = false;
        View focusView = null;

        if (TextUtils.isEmpty(user_otp)) {
            AppUtils.alertDialog(Change_Proposer_Activity.this, "Please Enter OTP");
            focusView = edtxt_user_otp;
            cancel = true;
        } else if (!user_otp.equalsIgnoreCase(OTP)) {
            AppUtils.alertDialog(Change_Proposer_Activity.this, "Please Enter Valid OTP . OTP Not Match !!");
            focusView = edtxt_user_otp;
            cancel = true;
        }

        if (cancel) {
            focusView.requestFocus();
        } else {
            if (AppUtils.isNetworkAvailable(Change_Proposer_Activity.this)) {
                Runnable runnable = new Runnable() {
                    @Override
                    public void run() {
                        createChangeProposerOtpRequest();
                    }
                };
                new Handler().postDelayed(runnable, 500);
            } else {
                AppUtils.alertDialog(Change_Proposer_Activity.this, getResources().getString(R.string.txt_networkAlert));
            }
        }
    }

    private void createChangeProposerOtpRequest() {
        try {
            List<NameValuePair> postParameters = new ArrayList<>();
            postParameters.add(new BasicNameValuePair("IDNo", "" + AppController.getSpUserInfo().getString(SPUtils.USER_ID_NUMBER, "")));
            postParameters.add(new BasicNameValuePair("NewProposerID", new_proposer_id));
            postParameters.add(new BasicNameValuePair("Remarks", remarks));
          //  postParameters.add(new BasicNameValuePair("DeviceID", Settings.Secure.getString(getApplicationContext().getContentResolver(), Settings.Secure.ANDROID_ID)));

           // AppUtils.alertDialogWithFinish(Change_Proposer_Activity.this, "Completed");
            executeChangeProposerOtpRequest(postParameters);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void executeChangeProposerOtpRequest(final List<NameValuePair> postParameters) {
        try {
            if (AppUtils.isNetworkAvailable(Change_Proposer_Activity.this)) {
                new AsyncTask<Void, Void, String>() {
                    protected void onPreExecute() {
                        AppUtils.showProgressDialog(Change_Proposer_Activity.this);
                    }

                    @Override
                    protected String doInBackground(Void... params) {
                        String response = null;
                        try {
                            response = AppUtils.callWebServiceWithMultiParam(Change_Proposer_Activity.this, postParameters, QueryUtils.methodChangeProposer, TAG);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        return response;
                    }

                    @Override
                    protected void onPostExecute(String resultData) {
                        try {
                            AppUtils.dismissProgressDialog();

                            AppUtils.dismissProgressDialog();
                            JSONObject jsonObject = new JSONObject(resultData);

                            if (jsonObject.getString("Status").equalsIgnoreCase("True")) {
                                JSONArray jsonArrayData = jsonObject.getJSONArray("Data");
                                if (jsonArrayData.length() != 0) {
                                    AppUtils.alertDialogWithFinishHome(Change_Proposer_Activity.this, jsonObject.getString("Message"));
                                } else {
                                    AppUtils.alertDialogWithFinish(Change_Proposer_Activity.this, jsonObject.getString("Message"));
                                }
                            } else {
                                AppUtils.alertDialog(Change_Proposer_Activity.this, jsonObject.getString("Message"));
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            AppUtils.showExceptionDialog(Change_Proposer_Activity.this);
                        }
                    }
                }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            }
        } catch (Exception e) {
            e.printStackTrace();
            AppUtils.showExceptionDialog(Change_Proposer_Activity.this);
        }
    }
}