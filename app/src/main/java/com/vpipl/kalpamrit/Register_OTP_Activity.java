package com.vpipl.kalpamrit;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.widget.Toolbar;
import android.telephony.TelephonyManager;
import android.text.Html;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.ScrollView;
import android.widget.TextView;

import com.google.android.gms.auth.api.phone.SmsRetriever;
import com.google.android.gms.auth.api.phone.SmsRetrieverClient;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.vpipl.kalpamrit.SMS.MySMSBroadcastReceiver;
import com.vpipl.kalpamrit.Utils.AppUtils;
import com.vpipl.kalpamrit.Utils.QueryUtils;
import com.vpipl.kalpamrit.Utils.SmsListener;
import com.vpipl.kalpamrit.Utils.SmsReceiver;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by admin on 31-05-2017.
 */
public class Register_OTP_Activity extends AppCompatActivity {

    private static final String TAG = "Register_OTP_Activity";
    String message;
    private String mobile;
    private String OTP;
    private String Fname;
    private String Lname;
    private String Gender;
    private String password;
    private String email;
    private ScrollView scrollView;
    private TextInputEditText edtxt_otp;
    private TextInputEditText edtxt_fname;
    private TextInputEditText edtxt_lname;
    private RadioGroup rg_gender;
    private CheckBox cb_accept;
    private Button button_next;

    private boolean accepet_conditions = false;
    private TextView txt_terms_conditions;

    private TelephonyManager telephonyManager;

    ImageView img_nav_back, img_login_logout;

    public void SetupToolbar() {

        img_nav_back = findViewById(R.id.img_nav_back);
        img_login_logout = findViewById(R.id.img_login_logout);

        img_nav_back.setVisibility(View.GONE);
        img_login_logout.setVisibility(View.GONE);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register__otp);

        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
        SetupToolbar();

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        mobile = getIntent().getStringExtra("Mobile");
        password = getIntent().getStringExtra("Password");
        email = getIntent().getStringExtra("Email");
        OTP = getIntent().getStringExtra("OTP");

        TextView txt_change_mobile = findViewById(R.id.txt_change_mobile);
        TextView textView = findViewById(R.id.textView2);
        edtxt_otp = findViewById(R.id.edtxt_otp);
        edtxt_fname = findViewById(R.id.edtxt_fname);
        edtxt_lname = findViewById(R.id.edtxt_lname);

        scrollView = findViewById(R.id.scrollView);

        button_next = findViewById(R.id.button_next);
        rg_gender = findViewById(R.id.rg_gender);


        RadioButton radioButton1 = findViewById(R.id.rb_male);
        RadioButton radioButton2 = findViewById(R.id.rb_female);

        if (Build.VERSION.SDK_INT >= 21) {
            ColorStateList colorStateList = new ColorStateList(
                    new int[][]{
                            new int[]{-android.R.attr.state_enabled}, //disabled
                            new int[]{android.R.attr.state_enabled} //enabled
                    },
                    new int[]{
                            getResources().getColor(R.color.color_666666) //disabled
                            , getResources().getColor(R.color.colorPrimary) //enabled
                    }
            );

            radioButton1.setButtonTintList(colorStateList);//set the color tint list
            radioButton1.invalidate(); //could not be necessary
            radioButton2.setButtonTintList(colorStateList);//set the color tint list
            radioButton2.invalidate(); //could not be necessary
        }

        cb_accept = findViewById(R.id.cb_accept);

        telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);

        String text = getResources().getString(R.string.enter_otp_sent) + " " + mobile;
        textView.setText(text);

        txt_terms_conditions = findViewById(R.id.txt_terms_conditions);
//        txt_terms_conditions.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                AppUtils.alertDialog(Register_OTP_Activity.this, getResources().getString(R.string.terms_conditions));
//            }
//        });

        txt_change_mobile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(Register_OTP_Activity.this, Register_User_Activity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.putExtra("Mobile", mobile);

                startActivity(intent);
//                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                finish();
            }
        });

        button_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AppUtils.hideKeyboardOnClick(Register_OTP_Activity.this, v);
                ValidateData();
            }
        });

        cb_accept.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                accepet_conditions = b;

                if (cb_accept.isChecked()) {
                    button_next.setVisibility(View.VISIBLE);
                    scrollView.post(new Runnable() {
                        @Override
                        public void run() {
                            scrollView.smoothScrollTo(0, scrollView.getHeight());
                        }
                    });
                } else {
                    button_next.setVisibility(View.GONE);
                }
            }
        });

/*
        SmsReceiver.bindListener(new SmsListener() {
            @Override
            public void messageReceived(String messageText) {
                try {
                    if (messageText.length() == 6) {
                        edtxt_otp.setText(messageText);
                        edtxt_otp.setEnabled(false);
                    }
                } catch (Exception ignored) {
                }
            }
        });
*/
// Get an instance of SmsRetrieverClient, used to start listening for a matching
// SMS message.
        SmsRetrieverClient client = SmsRetriever.getClient(this /* context */);

        Task<Void> task = client.startSmsRetriever();

        task.addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                //     Toast.makeText(RegisterActivity.this, "Success", Toast.LENGTH_SHORT).show();
            }
        });

        task.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                //    Toast.makeText(RegisterActivity.this, "Failed", Toast.LENGTH_SHORT).show();
            }
        });


        MySMSBroadcastReceiver.bindListener(new SmsListener() {
            @Override
            public void messageReceived(String messageText) {
                try {
                    if (messageText.length() == 6) {
                        edtxt_otp.setText(messageText);
                        edtxt_otp.setEnabled(false);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void ValidateData() {
        String Otp = edtxt_otp.getText().toString().trim();
        Fname = edtxt_fname.getText().toString().trim();
        Lname = edtxt_lname.getText().toString().trim();

        int selectedId = rg_gender.getCheckedRadioButtonId();
        RadioButton rb_gender = findViewById(selectedId);
        Gender = rb_gender.getText().toString().trim();

        if (Gender.equalsIgnoreCase("Male"))
            Gender = "M";
        else
            Gender = "F";

        boolean cancel = false;
        View focusView = null;

        if (TextUtils.isEmpty(Otp)) {
            AppUtils.alertDialog(Register_OTP_Activity.this, "OTP is Required");
            focusView = edtxt_otp;
            cancel = true;
        } else if (!OTP.equalsIgnoreCase(Otp)) {
            AppUtils.alertDialog(Register_OTP_Activity.this, "Invalid OTP");
            focusView = edtxt_otp;
            cancel = true;
        } else if (TextUtils.isEmpty(Fname)) {
            AppUtils.alertDialog(Register_OTP_Activity.this, "First Name is Required");
            focusView = edtxt_fname;
            cancel = true;
        } else if (TextUtils.isEmpty(Lname)) {
            AppUtils.alertDialog(Register_OTP_Activity.this, "Last Name is Required");
            focusView = edtxt_lname;
            cancel = true;
        } else if (!cb_accept.isChecked()) {
            AppUtils.alertDialog(Register_OTP_Activity.this, "Accept Terms & Conditions");
            focusView = cb_accept;
            cancel = true;
        }

        if (cancel) {
            focusView.requestFocus();
        } else {
            if (AppUtils.isNetworkAvailable(Register_OTP_Activity.this)) {
                createRegistrationRequest();
            } else {
                AppUtils.alertDialog(Register_OTP_Activity.this, getResources().getString(R.string.txt_networkAlert));
            }
        }
    }


    private void createRegistrationRequest() {
        try {
            List<NameValuePair> postParameters = new ArrayList<>();

            postParameters.add(new BasicNameValuePair("Fristname", Fname));
            postParameters.add(new BasicNameValuePair("LastName", Lname));
            postParameters.add(new BasicNameValuePair("Password", password));
            postParameters.add(new BasicNameValuePair("MobileNo", mobile));
            postParameters.add(new BasicNameValuePair("Emailid", email));
            postParameters.add(new BasicNameValuePair("DeviceID", telephonyManager.getDeviceId()));
            postParameters.add(new BasicNameValuePair("Gender", Gender));


            executeMemberRegistrationRequest(postParameters);
        } catch (Exception ignored) {

        }
    }

    private void executeMemberRegistrationRequest(final List<NameValuePair> postParameters) {
        try {
            if (AppUtils.isNetworkAvailable(Register_OTP_Activity.this)) {
                new AsyncTask<Void, Void, String>() {
                    protected void onPreExecute() {
                        AppUtils.showProgressDialog(Register_OTP_Activity.this);
                    }

                    @Override
                    protected String doInBackground(Void... params) {
                        String response = null;
                        try {
                            response = AppUtils.callWebServiceWithMultiParam(Register_OTP_Activity.this, postParameters, QueryUtils.methodtoGuestUserReg, TAG);
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
                                showLoginDialog();
                            } else {
                                AppUtils.alertDialog(Register_OTP_Activity.this, jsonObject.getString("Message"));
                            }

                        } catch (Exception e) {
                            e.printStackTrace();
                            AppUtils.showExceptionDialog(Register_OTP_Activity.this);
                        }
                    }
                }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            }
        } catch (Exception e) {
            e.printStackTrace();
            AppUtils.showExceptionDialog(Register_OTP_Activity.this);
        }
    }


    private void showLoginDialog() {
        try {
            final Dialog dialog = AppUtils.createDialog(Register_OTP_Activity.this, false);
            dialog.setCancelable(false);

            TextView txt_DialogTitle = dialog.findViewById(R.id.txt_DialogTitle);
            txt_DialogTitle.setText(Html.fromHtml("Registration is Completed, Please Login to Continue"));

            TextView txt_submit = dialog.findViewById(R.id.txt_submit);
            txt_submit.setText("Login");
            txt_submit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {

                        Intent intent = new Intent(Register_OTP_Activity.this, Login_Activity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                        intent.putExtra("SendToHome", true);
                        startActivity(intent);
                        finish();

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
            dialog.show();

        } catch (Exception e) {
            e.printStackTrace();
            AppUtils.showExceptionDialog(Register_OTP_Activity.this);
        }
    }
}