package com.vpipl.kalpamrit;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.vpipl.kalpamrit.Utils.AppUtils;
import com.vpipl.kalpamrit.Utils.QueryUtils;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class Register_User_Activity extends AppCompatActivity {

    private static final String TAG = "Register_User_Activity";
    private String mobile;
    private String password;
    private String email;
    private TextInputEditText edtxt_mobile, edtxt_password, edtxt_email;

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
        setContentView(R.layout.activity_register_user);

        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
        SetupToolbar();

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);


        edtxt_email = findViewById(R.id.edtxt_email);
        edtxt_password = findViewById(R.id.edtxt_password);
        edtxt_mobile = findViewById(R.id.edtxt_mobile);

        Button button_next = findViewById(R.id.btn_next);

        TextView txt_already_have = findViewById(R.id.txt_already_have);
        txt_already_have.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AppUtils.hideKeyboardOnClick(Register_User_Activity.this, v);
                MovetoNext(new Intent(Register_User_Activity.this, Login_Activity.class).putExtra("SendToHome", true));
                finish();
            }
        });

        Bundle bundle = getIntent().getExtras();

        if (bundle != null) {
            String mobile = getIntent().getStringExtra("Mobile");
            edtxt_mobile.setText(mobile);
        }

        edtxt_password.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == R.id.login || id == EditorInfo.IME_NULL) {
                    AppUtils.hideKeyboardOnClick(Register_User_Activity.this, textView);
                    ValidateData();
                    return true;
                }
                return false;
            }
        });

        button_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AppUtils.hideKeyboardOnClick(Register_User_Activity.this, view);
                ValidateData();
            }
        });
    }

    private void ValidateData() {
        mobile = edtxt_mobile.getText().toString().trim();
        password = edtxt_password.getText().toString().trim();
        email = edtxt_email.getText().toString().trim();

        boolean cancel = false;
        View focusView = null;

        if (TextUtils.isEmpty(mobile)) {
            AppUtils.alertDialog(Register_User_Activity.this, getResources().getString(R.string.error_required_mobile_number));
            focusView = edtxt_mobile;
            cancel = true;
        } else if ((mobile).length() != 10) {
            AppUtils.alertDialog(Register_User_Activity.this, getResources().getString(R.string.error_invalid_mobile_number));
            focusView = edtxt_mobile;
            cancel = true;
        } else if (TextUtils.isEmpty(email)) {
            AppUtils.alertDialog(Register_User_Activity.this, "Email Address is Required");
            focusView = edtxt_email;
            cancel = true;
        } else if (AppUtils.isValidMail(email)) {
            AppUtils.alertDialog(Register_User_Activity.this, getResources().getString(R.string.error_invalid_email));
            focusView = edtxt_email;
            cancel = true;
        } else if (TextUtils.isEmpty(password)) {
            AppUtils.alertDialog(Register_User_Activity.this, getResources().getString(R.string.error_required_password));
            focusView = edtxt_password;
            cancel = true;
        }

        if (cancel) {
            focusView.requestFocus();
        } else {
            if (AppUtils.isNetworkAvailable(Register_User_Activity.this)) {

                Runnable runnable = new Runnable() {
                    @Override
                    public void run() {
                        createRegistrationRequest();
                    }
                };
                new Handler().postDelayed(runnable, 500);
            } else {
                AppUtils.alertDialog(Register_User_Activity.this, getResources().getString(R.string.txt_networkAlert));
            }
        }
    }

    private void createRegistrationRequest() {
        try {
            List<NameValuePair> postParameters = new ArrayList<>();

            postParameters.add(new BasicNameValuePair("MobileNo", mobile.trim()));
            postParameters.add(new BasicNameValuePair("Emailid", email));

            executeMemberRegistrationRequest(postParameters);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void executeMemberRegistrationRequest(final List<NameValuePair> postParameters) {
        try {
            if (AppUtils.isNetworkAvailable(Register_User_Activity.this)) {
                new AsyncTask<Void, Void, String>() {
                    protected void onPreExecute() {
                        AppUtils.showProgressDialog(Register_User_Activity.this);
                    }

                    @Override
                    protected String doInBackground(Void... params) {
                        String response = "";
                        try {
                            response = AppUtils.callWebServiceWithMultiParam(Register_User_Activity.this, postParameters, QueryUtils.methodtoSendOTP, TAG);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        return response;
                    }

                    @Override
                    protected void onPostExecute(String resultData) {
                        try {
                            AppUtils.dismissProgressDialog();

                            JSONObject object = new JSONObject(resultData);
                            JSONArray jsonArrayData = object.getJSONArray("Data");

                            if (jsonArrayData.length() > 0) {
                                if (object.getString("Status").equalsIgnoreCase("True")) {
                                    String OTP = jsonArrayData.getJSONObject(0).getString("OTP");
                                    executeOTPScreen(OTP);

                                } else {
                                    AppUtils.alertDialog(Register_User_Activity.this, object.getString("Message"));
                                }
                            } else {
                                AppUtils.alertDialog(Register_User_Activity.this, object.getString("Message"));
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            AppUtils.showExceptionDialog(Register_User_Activity.this);
                        }
                    }
                }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            }
        } catch (Exception e) {
            e.printStackTrace();
            AppUtils.showExceptionDialog(Register_User_Activity.this);
        }
    }

    private void executeOTPScreen(String OTP) {
        Intent intent = new Intent(Register_User_Activity.this, Register_OTP_Activity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra("Mobile", mobile);
        intent.putExtra("Password", password);
        intent.putExtra("Email", email);
        intent.putExtra("OTP", OTP);
        MovetoNext(intent);
        finish();
    }

    private void MovetoNext(Intent intent) {
        try {
            startActivity(intent);

        } catch (Exception e) {
            e.printStackTrace();
            AppUtils.showExceptionDialog(Register_User_Activity.this);
        }
    }
}