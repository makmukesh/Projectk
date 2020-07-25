package com.vpipl.kalpamrit;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.widget.Toolbar;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.vpipl.kalpamrit.R;
import com.vpipl.kalpamrit.Utils.AppUtils;
import com.vpipl.kalpamrit.Utils.QueryUtils;
import com.vpipl.kalpamrit.Utils.SPUtils;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class Forget_Password_Activity extends AppCompatActivity {

    private static final String TAG = "Forget_Password_Activity";
    private TextInputEditText edtxt_userid;
    private Button button_submit;
    private String userid;
    private TelephonyManager telephonyManager;
    private TextView txt_login;

    private String LoginType = "User";

    ImageView img_nav_back, img_login_logout;

    public void SetupToolbar() {

        img_nav_back = findViewById(R.id.img_nav_back);
        img_login_logout = findViewById(R.id.img_login_logout);

        img_login_logout.setVisibility(View.GONE);
        img_nav_back.setImageDrawable(getResources().getDrawable(R.drawable.icon_nav_bar_close));
        img_nav_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forget__password);

        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
        SetupToolbar();

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);

        LoginType = getIntent().getStringExtra("LoginType");

        edtxt_userid = findViewById(R.id.edtxt_userid);
        txt_login = findViewById(R.id.txt_login);
        button_submit = findViewById(R.id.button_submit);

        if (LoginType.equalsIgnoreCase("User")) {
            edtxt_userid.setHint("Registered Mobile Number");
        } else {
            edtxt_userid.setHint("User ID");
        }

        button_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AppUtils.hideKeyboardOnClick(Forget_Password_Activity.this, v);
                ValidateData();
            }
        });

        txt_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(Forget_Password_Activity.this, Login_Activity.class);
                intent.putExtra("SendToHome", true);
                startActivity(intent);
                finish();
            }
        });
    }

    private void ValidateData() {
        userid = edtxt_userid.getText().toString().trim();

        boolean cancel = false;
        View focusView = null;

        if (LoginType.equalsIgnoreCase("User")) {
            if (TextUtils.isEmpty(userid)) {
                AppUtils.alertDialog(Forget_Password_Activity.this, getResources().getString(R.string.error_required_mobile_number));
                focusView = edtxt_userid;
                cancel = true;
            } else if (userid.trim().length() != 10 && (!userid.matches("^[0-9]+$"))) {
                AppUtils.alertDialog(Forget_Password_Activity.this, getResources().getString(R.string.error_invalid_mobile_number));
                focusView = edtxt_userid;
                cancel = true;
            }
        } else {
            if (TextUtils.isEmpty(userid)) {
                AppUtils.alertDialog(Forget_Password_Activity.this, getResources().getString(R.string.error_required_user_id));
                focusView = edtxt_userid;
                cancel = true;
//            } else if (userid.trim().length() != 10) {
//                AppUtils.alertDialog(Forget_Password_Activity.this, getResources().getString(R.string.error_invalid_user_id));
//                focusView = edtxt_userid;
//                cancel = true;
            }
        }

        if (cancel) {
            focusView.requestFocus();
        } else {
            if (AppUtils.isNetworkAvailable(Forget_Password_Activity.this)) {

                Runnable runnable = new Runnable() {
                    @Override
                    public void run() {
                        executeForgetRequest();
                    }
                };
                new Handler().postDelayed(runnable, 500);
            } else {
                AppUtils.alertDialog(Forget_Password_Activity.this, getResources().getString(R.string.txt_networkAlert));
            }
        }
    }

     private void executeForgetRequest() {
        try {
            if (AppUtils.isNetworkAvailable(Forget_Password_Activity.this)) {
                new AsyncTask<Void, Void, String>() {
                    protected void onPreExecute() {
                        AppUtils.showProgressDialog(Forget_Password_Activity.this);
                    }

                    @Override
                    protected String doInBackground(Void... params) {
                        String response = null;

                        List<NameValuePair> postParameters = new ArrayList<>();
                        postParameters.add(new BasicNameValuePair("DeviceID", telephonyManager.getDeviceId()));
                        try {
                            if (LoginType.equalsIgnoreCase("User")) {
                                postParameters.add(new BasicNameValuePair("MobileNo_EmailID", userid));
                                response = AppUtils.callWebServiceWithMultiParam(Forget_Password_Activity.this, postParameters, QueryUtils.methodToForgetPasswordUser, TAG);
                            } else {
                                postParameters.add(new BasicNameValuePair("IDNo", userid));
                                response = AppUtils.callWebServiceWithMultiParam(Forget_Password_Activity.this, postParameters, QueryUtils.methodToForgetPasswordMember, TAG);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        return response;
                    }

                    @Override
                    protected void onPostExecute(String resultData) {
                        try {
                            AppUtils.dismissProgressDialog();
                            JSONObject jobject = new JSONObject(resultData);
                            if (jobject.length() > 0) {
                                if (jobject.getString("Status").equalsIgnoreCase("True")) {
                                    String message = jobject.getString("Message");
                                    ShowDialog(message);
                                } else {
                                    AppUtils.alertDialog(Forget_Password_Activity.this, jobject.getString("Message"));
                                }
                            } else {
                                AppUtils.showExceptionDialog(Forget_Password_Activity.this);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            AppUtils.showExceptionDialog(Forget_Password_Activity.this);
                        }
                    }
                }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            }
        } catch (Exception e) {
            e.printStackTrace();
            AppUtils.showExceptionDialog(Forget_Password_Activity.this);
        }
    }

    private void ShowDialog(String message) {
        final Dialog dialog = AppUtils.createDialog(Forget_Password_Activity.this, true);
        TextView dialog4all_txt = dialog.findViewById(R.id.txt_DialogTitle);
        dialog4all_txt.setText(message);

        TextView textView = dialog.findViewById(R.id.txt_submit);
        textView.setText("Login");
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();

                AppController.getSpUserInfo().edit().clear().commit();
                AppController.getSpIsLogin().edit().clear().commit();

                Intent intent = new Intent(Forget_Password_Activity.this, Login_Activity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.putExtra("SendToHome", true);
                startActivity(intent);
                finish();
            }
        });
        dialog.show();
    }
}
