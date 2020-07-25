package com.vpipl.kalpamrit;

import android.Manifest;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.widget.Toolbar;
import android.telephony.TelephonyManager;
import android.text.Editable;
import android.text.Html;
import android.text.InputFilter;
import android.text.InputType;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

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

import org.apache.commons.lang3.text.WordUtils;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class Register_Activity extends AppCompatActivity {

    private static final String TAG = "Register_Activity";

    private String sponsor_id="";
    private String proposer_id="";
    private String name="";
    private String dob="";
    private String address="";
    private String mobile_number="";
    private String pan_number="";
    private String password="";
    private String confirm_password="";
    private String txnpassword="";
    private String confirm_txnpassword="";
    private String sponsor_form_no ="";
    private String proposer_formno="";
    private String sponsor_name="";
    private String proposer_name="";
    private String[] stateArray;

    private String state;
    private String district;
    private String city;
    private String pincode;
    private TextInputEditText edtxt_district;
    private TextInputEditText edtxt_city;
    private TextInputEditText edtxt_pinCode;
    private TextInputEditText txt_state;

    String OTP_received_from_server, OTP_entered_by_user;

    private boolean accepet_conditions = false;

    private TextInputEditText edtxt_sponsor_id, edtxt_proposer_id, edtxt_name, edtxt_address,
            edtxt_mobile, edtxt_pan_number, edtxt_password, txt_select_date, edtxt_otp, txt_select;
    private TextInputEditText edtxt_confirm_password, edtxt_txn_password, edtxt_txn_confirm_password;
    private TextView txt_terms_conditions;
    private TextView txt_sponsor_name, txt_proposer_name;
    private CheckBox cb_accept;

    private LinearLayout ll_button_submit;
    private Button btn_submit;
    private Button btn_cancel, btn_register, btn_resend_otp;
    private ScrollView scrollView;

    private Calendar myCalendar;
    private SimpleDateFormat sdf;

    DatePickerDialog datePickerDialog;

    boolean CheckOTP = false;

    String selecteditem = "PAN Number";

    private final DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {

        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {

            myCalendar.set(Calendar.YEAR, year);
            myCalendar.set(Calendar.MONTH, monthOfYear);
            myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

            if (new Date().after(myCalendar.getTime())) {
                txt_select_date.setText(sdf.format(myCalendar.getTime()));
            } else {
                txt_select_date.requestFocus();
                AppUtils.alertDialog(Register_Activity.this, getResources().getString(R.string.error_invalid_dob));
            }
        }
    };
    private TelephonyManager telephonyManager;

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

        img_login_logout.setVisibility(View.GONE);

    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
        SetupToolbar();

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        edtxt_sponsor_id = findViewById(R.id.edtxt_sponsor_id);
        edtxt_proposer_id = findViewById(R.id.edtxt_proposer_id);
        edtxt_name = findViewById(R.id.edtxt_name);
        edtxt_address = findViewById(R.id.edtxt_address);
        edtxt_mobile = findViewById(R.id.edtxt_mobile);
        edtxt_pan_number = findViewById(R.id.edtxt_pan_number);
        edtxt_password = findViewById(R.id.edtxt_password);
        edtxt_confirm_password = findViewById(R.id.edtxt_confirm_password);
        edtxt_txn_password = findViewById(R.id.edtxt_txn_password);
        edtxt_txn_confirm_password = findViewById(R.id.edtxt_txn_confirm_password);

        edtxt_pan_number.setFilters(new InputFilter[]{new InputFilter.AllCaps(), new InputFilter.LengthFilter(12)});

        txt_select_date = findViewById(R.id.txt_select_date);
        txt_sponsor_name = findViewById(R.id.txt_sponsor_name);
        txt_proposer_name = findViewById(R.id.txt_proposer_name);
        txt_select = findViewById(R.id.txt_select);
        edtxt_district = findViewById(R.id.edtxt_district);
        edtxt_city = findViewById(R.id.edtxt_city);
        edtxt_pinCode = findViewById(R.id.edtxt_pinCode);
        txt_state = findViewById(R.id.txt_state);

        edtxt_otp = (TextInputEditText) findViewById(R.id.edtxt_otp);

        scrollView = findViewById(R.id.scrollView);

        txt_terms_conditions = findViewById(R.id.txt_terms_conditions);
        txt_terms_conditions.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Register_Activity.this, TermsAndConditionsWebView.class);
                intent.putExtra("str_type", "LM");
                startActivity(intent);
                //  AppUtils.alertDialog(Register_Activity.this, getResources().getString(R.string.terms_conditions));
            }
        });
//        txt_terms_conditions.setText(Html.fromHtml(getResources().getString(R.string.terms_conditions)));

        cb_accept = findViewById(R.id.cb_accept);

        ll_button_submit = findViewById(R.id.ll_button_submit);
        ll_button_submit.setVisibility(View.GONE);

        myCalendar = Calendar.getInstance();

        telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);

        sdf = new SimpleDateFormat("dd MMM yyyy");
        txt_select_date.setText("");

        btn_submit = (Button) findViewById(R.id.btn_submit);
        btn_register = (Button) findViewById(R.id.btn_register);
        btn_cancel = (Button) findViewById(R.id.btn_cancel);
        btn_resend_otp = (Button) findViewById(R.id.btn_resend_otp);

        txt_state.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (b) {
                    if (AppController.stateList.size() != 0) {
                        showStateDialog();
                        txt_state.clearFocus();
                    } else {
                        executeStateRequest();
                    }
                }
            }
        });
        btn_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AppUtils.hideKeyboardOnClick(Register_Activity.this, view);
                ValidateData();
            }
        });

        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AppUtils.hideKeyboardOnClick(Register_Activity.this, v);
                finish();
            }
        });

        btn_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AppUtils.hideKeyboardOnClick(Register_Activity.this, v);
                ValidateDataWithOTP();
            }
        });

        btn_resend_otp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AppUtils.hideKeyboardOnClick(Register_Activity.this, v);
                ValidateData();
            }
        });

        cb_accept.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                accepet_conditions = b;

                if (cb_accept.isChecked()) {
                    ll_button_submit.setVisibility(View.VISIBLE);
                    scrollView.post(new Runnable() {
                        @Override
                        public void run() {
                            scrollView.smoothScrollTo(0, scrollView.getHeight());
                        }
                    });
                } else {
                    ll_button_submit.setVisibility(View.GONE);
                }
            }
        });
        edtxt_sponsor_id.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
//                String id = edtxt_sponsor_id.getText().toString().trim();
//                if (id.length() == 10)
//                {
                sponsor_form_no = "";
                sponsor_name = "";
                executetoCheckSponsorName(edtxt_sponsor_id.getText().toString());
//                }
            }
        });
        edtxt_proposer_id.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
//                String id = edtxt_sponsor_id.getText().toString().trim();
//                if (id.length() == 10)
//                {
                proposer_formno = "";
                proposer_name = "";
                if(!sponsor_form_no.equalsIgnoreCase("")) {
                    executetoCheckProposerName(edtxt_proposer_id.getText().toString(), edtxt_sponsor_id.getText().toString());
                }
                else {
                    Toast.makeText(Register_Activity.this, "Please Enter first Sponsor ID !!", Toast.LENGTH_SHORT).show();
                }
//                }
            }
        });

        txt_select_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AppUtils.hideKeyboardOnClick(Register_Activity.this, view);
                showdatePicker();
                txt_select_date.clearFocus();
                txt_select_date.setError(null);
            }
        });


        txt_select.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AppUtils.hideKeyboardOnClick(Register_Activity.this, view);
                showSelectDialog();
                txt_select.clearFocus();
                txt_select.setError(null);
            }
        });
/*
        SmsReceiver.bindListener(new SmsListener() {
            @Override
            public void messageReceived(String messageText) {
                try {
                    if (messageText.length() == 6) {
                        edtxt_otp.setText(messageText);
                    }
                } catch (Exception ignored) {
                }
            }
        });*/

        MySMSBroadcastReceiver.bindListener(new SmsListener() {
            @Override
            public void messageReceived(String messageText) {
                try {
                    if (messageText.length() == 6) {
                        edtxt_otp.setText(messageText);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        executeOTPPermission();
        executeStateRequestone();

//        String Popupmsz = Html.fromHtml("<html><p><div align=\"justify\">Dear member," +
//                " please enter your PAN card number very carefully. In the case of wrong PAN number, company will deduct 20% TDS on your payout which you can’t get back from INCOME TAX DEPARTMENT. Only 5% TDS will be deducted If you submit correct PAN number.<br>" +
//                "Be careful and save your money during PAN number submission.<br><br>" +
//                "प्रिय सदस्य, अपना पैन कार्ड नंबर ध्यान से भरे | पैन कार्ड नंबर गलत होने के स्थिति में कंपनी आप के पेआउट में से 20% TDS कटेगी, यह आप कभी इनकम टैक्स डिपार्टमेंट से वापिस नहीं ले सकते | पैन कार्ड सही होने पर केवल 5% TDS काटा जायेगा |<br>" +
//                "पैन कार्ड ध्यान से भरे और पैसे की बचत करें |</div></p>").toString();

      /*  String Popupmsz = ("Dear member, please enter your PAN card number very carefully. In the case of wrong PAN number, company will deduct 20% TDS on your payout which you can’t get back from INCOME TAX DEPARTMENT. Only 5% TDS will be deducted If you submit correct PAN number.\n" +
                "Be careful and save your money during PAN number submission.\n\n" +
                "प्रिय सदस्य, अपना पैन कार्ड नंबर ध्यान से भरे | पैन कार्ड नंबर गलत होने के स्थिति में कंपनी आप के पेआउट में से 20% TDS कटेगी, यह आप कभी इनकम टैक्स डिपार्टमेंट से वापिस नहीं ले सकते | पैन कार्ड सही होने पर केवल 5% TDS काटा जायेगा |\n" +
                "पैन कार्ड ध्यान से भरे और पैसे की बचत करें |");*/
        String Popupmsz = ("Dear member, please upload your PAN card in KYC section after joining. In the case of wrong or Missing PAN Card, company will deduct 20% TDS on your payout which you can’t get back from INCOME TAX DEPARTMENT. Only 5% TDS will be deducted If you submit correct PAN number.\n" +
                "please Be careful and save your money during PAN number submission.\n\n" +
                "प्रिय सदस्य, कृपया जॉइन करने के बाद केवाईसी सेक्शन में अपना पैन कार्ड अपलोड करें| पैन कार्ड नंबर गलत होने के स्थिति में कंपनी आप के पेआउट में से 20% TDS कटेगी, यह आप कभी इनकम टैक्स डिपार्टमेंट से वापिस नहीं ले सकते | पैन कार्ड सही होने पर केवल 5% TDS काटा जायेगा |\n" +
                "पैन कार्ड ध्यान से अपलोड करें और पैसे की बचत करें |");

        alertDialog(this, Popupmsz);
    }

    public void alertDialog(Context context, String message) {
        try {
            final Dialog dialog = new Dialog(context, R.style.ThemeDialogCustom);
            dialog.getWindow().getAttributes().windowAnimations = R.style.dialog_animation;
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setCancelable(true);
            dialog.setContentView(R.layout.custom_dialog_imp_info);

            TextView dialog4all_txt = dialog.findViewById(R.id.txt_DialogTitle);
            dialog4all_txt.setText(message);
            dialog.findViewById(R.id.txt_submit).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                }
            });

            dialog.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void showSelectDialog() {
        try {
            final String[] stateArray = {"Aadhaar Number", "PAN Number"};
            //  final String[] stateArray = {"Aadhaar Number"};

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Select");
            builder.setItems(stateArray, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int item) {
                    // Do something with the selection
                    txt_select.setText(stateArray[item]);
                    selecteditem = stateArray[item];

                    edtxt_pan_number.setText("");
                    edtxt_pan_number.setHint(stateArray[item]);

                    if (stateArray[item].equalsIgnoreCase("Aadhaar Number"))
                        edtxt_pan_number.setInputType(InputType.TYPE_CLASS_NUMBER);
                    else
                        edtxt_pan_number.setInputType(InputType.TYPE_CLASS_TEXT);

                    edtxt_pan_number.setSelection(edtxt_pan_number.getText().length());

                }
            });
            builder.create().show();

        } catch (Exception e) {
            e.printStackTrace();
            AppUtils.showExceptionDialog(Register_Activity.this);
        }
    }

    private void showdatePicker() {
        Calendar calendar = Calendar.getInstance();
        datePickerDialog = new DatePickerDialog(Register_Activity.this, date, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
        calendar.add(Calendar.YEAR, -18);
        datePickerDialog.getDatePicker().setMaxDate(calendar.getTime().getTime());
        datePickerDialog.show();
    }

    private void ValidateData() {

        sponsor_id = edtxt_sponsor_id.getText().toString().trim();
        proposer_id = edtxt_proposer_id.getText().toString().trim();
        name = edtxt_name.getText().toString().trim();
        dob = txt_select_date.getText().toString().trim();
        address = edtxt_address.getText().toString().trim();
        mobile_number = edtxt_mobile.getText().toString().trim();

        pan_number = edtxt_pan_number.getText().toString().trim();
        password = edtxt_password.getText().toString().trim();
        confirm_password = edtxt_confirm_password.getText().toString().trim();
        txnpassword = edtxt_txn_password.getText().toString().trim();
        confirm_txnpassword = edtxt_txn_confirm_password.getText().toString().trim();

        pincode = edtxt_pinCode.getText().toString().trim();
        city = edtxt_city.getText().toString().trim();
        district = edtxt_district.getText().toString().trim();
        state = txt_state.getText().toString().trim();

        boolean cancel = false;
        View focusView = null;

        if (TextUtils.isEmpty(sponsor_id.trim())) {
            AppUtils.alertDialog(Register_Activity.this, getResources().getString(R.string.error_required_sponsorID));
            focusView = edtxt_sponsor_id;
            cancel = true;
        } else if (TextUtils.isEmpty(proposer_id.trim())) {
            AppUtils.alertDialog(Register_Activity.this, "Please Enter Proposer ID");
            focusView = edtxt_proposer_id;
            cancel = true;
        } else if (TextUtils.isEmpty(name.trim())) {
            AppUtils.alertDialog(Register_Activity.this, getResources().getString(R.string.error_required_name));
            focusView = edtxt_name;
            cancel = true;
        } else if (TextUtils.isEmpty(dob.trim())) {
            AppUtils.alertDialog(Register_Activity.this, getResources().getString(R.string.error_required_dob));
            focusView = txt_select_date;
            cancel = true;
        } else if (TextUtils.isEmpty(address.trim())) {
            AppUtils.alertDialog(Register_Activity.this, getResources().getString(R.string.error_required_address));
            focusView = edtxt_address;
            cancel = true;
        } else if (TextUtils.isEmpty(pincode.trim())) {
            AppUtils.alertDialog(Register_Activity.this, "Please enter pincode.");
            focusView = edtxt_pinCode;
            cancel = true;
        } else if (TextUtils.isEmpty(city.trim())) {
            AppUtils.alertDialog(Register_Activity.this, "Please enter city.");
            focusView = edtxt_city;
            cancel = true;
        } else if (TextUtils.isEmpty(district.trim())) {
            AppUtils.alertDialog(Register_Activity.this, "Please enter district.");
            focusView = edtxt_district;
            cancel = true;
        } else if (TextUtils.isEmpty(state.trim())) {
            AppUtils.alertDialog(Register_Activity.this, "Please select state.");
            focusView = edtxt_district;
            cancel = true;
        } else if (TextUtils.isEmpty(mobile_number.trim())) {
            AppUtils.alertDialog(Register_Activity.this, getResources().getString(R.string.error_required_mobile_number));
            focusView = edtxt_mobile;
            cancel = true;
        } else if ((mobile_number.trim()).length() != 10) {
            AppUtils.alertDialog(Register_Activity.this, getResources().getString(R.string.error_invalid_mobile_number));
            focusView = edtxt_mobile;
            cancel = true;
        } /*else if (!TextUtils.isEmpty(pan_number.trim()) && selecteditem.equalsIgnoreCase("PAN Number") && !pan_number.matches(AppUtils.mPANPattern)) {
            AppUtils.alertDialog(Register_Activity.this, getResources().getString(R.string.error_invalid_PANno));
            focusView = edtxt_pan_number;
            cancel = true;
        }*/ else if (TextUtils.isEmpty(password.trim())) {
            AppUtils.alertDialog(Register_Activity.this, getResources().getString(R.string.error_required_password));
            focusView = edtxt_password;
            cancel = true;
        } else if (password.length() < 8 && !AppUtils.isValidPassword(password)) {
            AppUtils.alertDialog(Register_Activity.this, "Password should contain at least 1 digit , 1 lowercase,1 uppercase ,1 symbol *[@#$%^&+=!] , length atleast 8 digits");
            focusView = edtxt_password;
            cancel = true;
            //  System.out.println("Password should contain at least 1 digit , 1 lowercase,1 uppercase ,1 symbol *[@#$%^&+=!] , length atleast 8 digits");
        } else if (!password.trim().equals(confirm_password)) {
            AppUtils.alertDialog(Register_Activity.this, "Password or Confirm password not match");
            focusView = edtxt_confirm_password;
            cancel = true;
        } else if (TextUtils.isEmpty(txnpassword.trim())) {
            AppUtils.alertDialog(Register_Activity.this, getResources().getString(R.string.error_txnrequired_password));
            focusView = edtxt_txn_password;
            cancel = true;
        } else if (txnpassword.length() < 8 && !AppUtils.isValidPassword(txnpassword)) {
            AppUtils.alertDialog(Register_Activity.this, "Tranaction Password should contain at least 1 digit , 1 lowercase,1 uppercase ,1 symbol *[@#$%^&+=!] , length atleast 8 digits");
            focusView = edtxt_txn_password;
            cancel = true;
            //  System.out.println("Password should contain at least 1 digit , 1 lowercase,1 uppercase ,1 symbol *[@#$%^&+=!] , length atleast 8 digits");
        } else if (!txnpassword.trim().equals(confirm_txnpassword)) {
            AppUtils.alertDialog(Register_Activity.this, "Tranaction Password or Confirm Tranaction password not match");
            focusView = edtxt_txn_confirm_password;
            cancel = true;
        } else if (TextUtils.isEmpty(sponsor_form_no.trim())) {
            AppUtils.alertDialog(Register_Activity.this, getResources().getString(R.string.error_invalid_sponsorID));
            focusView = edtxt_sponsor_id;
            cancel = true;
        } else if (sponsor_form_no.trim().length() < 4) {
            AppUtils.alertDialog(Register_Activity.this, getResources().getString(R.string.error_invalid_sponsorID));
            focusView = edtxt_sponsor_id;
            cancel = true;
        }/* else if (selecteditem.equalsIgnoreCase("Aadhaar Number")) {
            if (TextUtils.isEmpty(pan_number.trim())) {
                AppUtils.alertDialog(Register_Activity.this, "Please Enter Aadhaar Number.");
                focusView = edtxt_pan_number;
                cancel = true;
            } else if (pan_number.length() != 12) {
                AppUtils.alertDialog(Register_Activity.this, "Invalid Aadhaar Number.");
                focusView = edtxt_pan_number;
                cancel = true;
            }
        }*/

        if (cancel) {
            focusView.requestFocus();
        } else {
            if (AppUtils.isNetworkAvailable(Register_Activity.this)) {
                if (CheckOTP)
                    executeSendOTP();
                else
                    createRegistrationRequest();
            } else {
                AppUtils.alertDialog(Register_Activity.this, getResources().getString(R.string.txt_networkAlert));
            }

/*            if (AppUtils.isNetworkAvailable(Register_Activity.this)) {
                if (TextUtils.isEmpty(pan_number.trim()))
                    showConfirmationDialog();
                else {
                    if (CheckOTP)
                        executeSendOTP();
                    else
                        createRegistrationRequest();
                }
            } else {
                AppUtils.alertDialog(Register_Activity.this, getResources().getString(R.string.txt_networkAlert));
            }*/
        }
    }

    private void ValidateDataWithOTP() {
        sponsor_id = edtxt_sponsor_id.getText().toString().trim();
        proposer_id = edtxt_proposer_id.getText().toString().trim();
        name = edtxt_name.getText().toString().trim();
        dob = txt_select_date.getText().toString().trim();
        address = edtxt_address.getText().toString().trim();
        mobile_number = edtxt_mobile.getText().toString().trim();
        pan_number = edtxt_pan_number.getText().toString().trim();
        password = edtxt_password.getText().toString().trim();
        OTP_entered_by_user = edtxt_otp.getText().toString().trim();

        boolean cancel = false;
        View focusView = null;

        if (TextUtils.isEmpty(sponsor_id.trim())) {
            AppUtils.alertDialog(Register_Activity.this, getResources().getString(R.string.error_required_sponsorID));
            focusView = edtxt_sponsor_id;
            cancel = true;
        } else if (TextUtils.isEmpty(proposer_id.trim())) {
            AppUtils.alertDialog(Register_Activity.this, "Please Enter Proposer Id");
            focusView = edtxt_proposer_id;
            cancel = true;
        } else if (TextUtils.isEmpty(name.trim())) {
            AppUtils.alertDialog(Register_Activity.this, getResources().getString(R.string.error_required_name));
            focusView = edtxt_name;
            cancel = true;
        } else if (TextUtils.isEmpty(dob.trim())) {
            AppUtils.alertDialog(Register_Activity.this, getResources().getString(R.string.error_required_dob));
            focusView = txt_select_date;
            cancel = true;
        } else if (TextUtils.isEmpty(address.trim())) {
            AppUtils.alertDialog(Register_Activity.this, getResources().getString(R.string.error_required_address));
            focusView = edtxt_address;
            cancel = true;
        } else if (TextUtils.isEmpty(mobile_number.trim())) {
            AppUtils.alertDialog(Register_Activity.this, getResources().getString(R.string.error_required_mobile_number));
            focusView = edtxt_mobile;
            cancel = true;
        } else if ((mobile_number.trim()).length() != 10) {
            AppUtils.alertDialog(Register_Activity.this, getResources().getString(R.string.error_invalid_mobile_number));
            focusView = edtxt_mobile;
            cancel = true;

        }/* else if (!TextUtils.isEmpty(pan_number.trim()) && selecteditem.equalsIgnoreCase("PAN Number") && !pan_number.matches(AppUtils.mPANPattern)) {
            AppUtils.alertDialog(Register_Activity.this, getResources().getString(R.string.error_invalid_PANno));
            focusView = edtxt_pan_number;
            cancel = true;
        } */ else if (TextUtils.isEmpty(password.trim())) {
            AppUtils.alertDialog(Register_Activity.this, getResources().getString(R.string.error_required_password));
            focusView = edtxt_password;
            cancel = true;
        } else if (TextUtils.isEmpty(sponsor_form_no.trim())) {
            AppUtils.alertDialog(Register_Activity.this, getResources().getString(R.string.error_invalid_sponsorID));
            focusView = edtxt_sponsor_id;
            cancel = true;
        } else if (sponsor_form_no.trim().length() < 4) {
            AppUtils.alertDialog(Register_Activity.this, getResources().getString(R.string.error_invalid_sponsorID));
            focusView = edtxt_sponsor_id;
            cancel = true;
        } else if (TextUtils.isEmpty(OTP_entered_by_user)) {
            AppUtils.alertDialog(Register_Activity.this, "Please Enter OTP");
            focusView = edtxt_otp;
            cancel = true;
        } else if (!OTP_entered_by_user.equalsIgnoreCase(OTP_received_from_server)) {
            AppUtils.alertDialog(Register_Activity.this, "Invalid / Incorrect OTP");
            focusView = edtxt_otp;
            cancel = true;
        } /*else if (selecteditem.equalsIgnoreCase("Aadhaar Number")) {
            if (TextUtils.isEmpty(pan_number.trim())) {
                AppUtils.alertDialog(Register_Activity.this, "Please Enter Aadhaar Number.");
                focusView = edtxt_pan_number;
                cancel = true;
            } else if (pan_number.length() != 12) {
                AppUtils.alertDialog(Register_Activity.this, "Invalid Aadhaar Number.");
                focusView = edtxt_pan_number;
                cancel = true;
            } else {
            }
        }*/


        if (cancel) {
            focusView.requestFocus();
        } else {
            if (AppUtils.isNetworkAvailable(Register_Activity.this)) {
                Runnable runnable = new Runnable() {
                    @Override
                    public void run() {
                        createRegistrationRequest();
                    }
                };
                new Handler().postDelayed(runnable, 500);

            } else {
                AppUtils.alertDialog(Register_Activity.this, getResources().getString(R.string.txt_networkAlert));
            }
        }
    }

    private void createRegistrationRequest() {
        try {
            List<NameValuePair> postParameters = new ArrayList<>();

            postParameters.add(new BasicNameValuePair("SponsorFormNo", "" + sponsor_form_no));
            postParameters.add(new BasicNameValuePair("Name", "" + name.trim()));
            postParameters.add(new BasicNameValuePair("DOB", "" + dob));
            postParameters.add(new BasicNameValuePair("Address", "" + address.trim()));
            postParameters.add(new BasicNameValuePair("MobileNo", "" + mobile_number));

            if (selecteditem.equalsIgnoreCase("PAN Number")) {
                postParameters.add(new BasicNameValuePair("PanNo", "" + pan_number.trim()));
                postParameters.add(new BasicNameValuePair("AdhaarNo", ""));
            } else {
                postParameters.add(new BasicNameValuePair("PanNo", ""));
                postParameters.add(new BasicNameValuePair("AdhaarNo", "" + pan_number.trim()));
            }

            postParameters.add(new BasicNameValuePair("Password", "" + password));
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
            postParameters.add(new BasicNameValuePair("DeviceID", "" + telephonyManager.getDeviceId()));

            /*Parameter added by mukesh 02-11-2019 10:49 AM*/
            String stateCode = "0";
            for (int i = 0; i < AppController.stateList.size(); i++) {
                if (state.equals(AppController.stateList.get(i).get("State"))) {
                    stateCode = AppController.stateList.get(i).get("STATECODE");
                }
            }

            postParameters.add(new BasicNameValuePair("TranPassword", "" + txnpassword.trim()));
            postParameters.add(new BasicNameValuePair("CmbState", "" + stateCode));
            postParameters.add(new BasicNameValuePair("Tehsil", "" + city.trim()));
            postParameters.add(new BasicNameValuePair("District", "" + district.trim()));
            postParameters.add(new BasicNameValuePair("Pincode", "" + pincode.trim()));
            postParameters.add(new BasicNameValuePair("ProposerID", "" + proposer_id.trim()));
            postParameters.add(new BasicNameValuePair("ProposerFormNo", "" + proposer_formno.trim()));
            postParameters.add(new BasicNameValuePair("SponsorID", "" + sponsor_id.trim()));

            executeMemberRegistrationRequest(postParameters);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void executeMemberRegistrationRequest(final List<NameValuePair> postParameters) {
        try {
            if (AppUtils.isNetworkAvailable(Register_Activity.this)) {
                new AsyncTask<Void, Void, String>() {
                    protected void onPreExecute() {
                        AppUtils.showProgressDialog(Register_Activity.this);
                    }

                    @Override
                    protected String doInBackground(Void... params) {
                        String response = "";
                        try {
                            response = AppUtils.callWebServiceWithMultiParam(Register_Activity.this, postParameters, QueryUtils.methodtoNewJoiningNew4, TAG);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        return response;
                    }

                    @Override
                    protected void onPostExecute(String resultData) {
                        try {
                            AppUtils.dismissProgressDialog();

                            JSONArray jsonArray;
                            JSONObject jsonObject;

                            try {
                                jsonArray = new JSONArray(resultData);
                                jsonObject = jsonArray.getJSONObject(0);
                            } catch (Exception e) {
                                e.printStackTrace();
                                jsonObject = new JSONObject(resultData);
                            }

                            if (jsonObject.length() > 0) {
                                if (jsonObject.getString("Status").equalsIgnoreCase("True")) {
                                    MovetoNext(new Intent(Register_Activity.this, WelcomeLetter_Activity.class).putExtra("Form Number", jsonObject.getString("formno")));
                                } else {
                                    AppUtils.alertDialog(Register_Activity.this, jsonObject.getString("Message"));
                                }
                            } else {
                                AppUtils.showExceptionDialog(Register_Activity.this);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            AppUtils.showExceptionDialog(Register_Activity.this);
                        }
                    }
                }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            } else {
                AppUtils.alertDialog(Register_Activity.this, getResources().getString(R.string.txt_networkAlert));
            }
        } catch (Exception e) {
            e.printStackTrace();
            AppUtils.showExceptionDialog(Register_Activity.this);
        }
    }

    private void executetoCheckSponsorName(final String sponsorid) {
        try {
            if (AppUtils.isNetworkAvailable(Register_Activity.this)) {
                new AsyncTask<Void, Void, String>() {

                    @Override
                    protected String doInBackground(Void... params) {
                        String response = null;
                        try {
                            List<NameValuePair> postParameters = new ArrayList<>();
                            postParameters.add(new BasicNameValuePair("SponsorID", "" + sponsorid));
                            response = AppUtils.callWebServiceWithMultiParam(Register_Activity.this, postParameters, QueryUtils.methodCheckSponsor, TAG);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        return response;
                    }

                    @Override
                    protected void onPostExecute(String resultData) {
                        try {
                            JSONArray jsonArrayData = new JSONArray(resultData);
                            JSONObject Jobject = jsonArrayData.getJSONObject(0);
                            setSponsorName(Jobject);
                        } catch (Exception e) {
                            e.printStackTrace();
                            AppUtils.showExceptionDialog(Register_Activity.this);
                        }
                    }
                }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            } else {
                AppUtils.alertDialog(Register_Activity.this, getResources().getString(R.string.txt_networkAlert));
            }
        } catch (Exception e) {
            e.printStackTrace();
            AppUtils.showExceptionDialog(Register_Activity.this);
        }
    }

    private void setSponsorName(JSONObject jobject) {
        try {
            if (jobject.getString("Status").equalsIgnoreCase("True")) {
                sponsor_form_no = jobject.getString("FormNo");
                sponsor_name = jobject.getString("MemName");
                txt_sponsor_name.setText(sponsor_name);
                txt_sponsor_name.setVisibility(View.VISIBLE);
                cb_accept.setEnabled(true);

            } else {
                sponsor_form_no = "";
                sponsor_name = "";
                txt_sponsor_name.setText(jobject.getString("Message"));
                txt_sponsor_name.setVisibility(View.VISIBLE);
                cb_accept.setEnabled(false);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void executetoCheckProposerName(final String ProposerID, final String SponsorID) {
        try {
            if (AppUtils.isNetworkAvailable(Register_Activity.this)) {
                new AsyncTask<Void, Void, String>() {

                    @Override
                    protected String doInBackground(Void... params) {
                        String response = null;
                        try {
                            List<NameValuePair> postParameters = new ArrayList<>();
                            postParameters.add(new BasicNameValuePair("SponsorID", "" + SponsorID));
                            postParameters.add(new BasicNameValuePair("ProposerID", "" + ProposerID));
                            response = AppUtils.callWebServiceWithMultiParam(Register_Activity.this, postParameters, QueryUtils.methodtoCheckProposer, TAG);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        return response;
                    }

                    @Override
                    protected void onPostExecute(String resultData) {
                        try {
                            JSONArray jsonArrayData = new JSONArray(resultData);
                            JSONObject Jobject = jsonArrayData.getJSONObject(0);
                            setProposerName(Jobject);
                        } catch (Exception e) {
                            e.printStackTrace();
                            AppUtils.showExceptionDialog(Register_Activity.this);
                        }
                    }
                }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            } else {
                AppUtils.alertDialog(Register_Activity.this, getResources().getString(R.string.txt_networkAlert));
            }
        } catch (Exception e) {
            e.printStackTrace();
            AppUtils.showExceptionDialog(Register_Activity.this);
        }
    }

    private void setProposerName(JSONObject jobject) {
        try {
            if (jobject.getString("Status").equalsIgnoreCase("True")) {
                proposer_formno = jobject.getString("ProposerFormNo");
                proposer_name = jobject.getString("ProposerName");
                txt_proposer_name.setText(proposer_name);
                txt_proposer_name.setVisibility(View.VISIBLE);
                cb_accept.setEnabled(true);
            } else {
                proposer_formno = "";
                proposer_name = "";
                txt_proposer_name.setText(jobject.getString("Message"));
                txt_proposer_name.setVisibility(View.VISIBLE);
                cb_accept.setEnabled(false);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void MovetoNext(Intent intent) {
        try {
            startActivity(intent);
            finish();

        } catch (Exception e) {
            e.printStackTrace();
            AppUtils.showExceptionDialog(Register_Activity.this);
        }
    }

    private void executeOTPPermission() {
        new AsyncTask<Void, Void, String>() {

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                AppUtils.showProgressDialog(Register_Activity.this);
            }

            @Override
            protected String doInBackground(Void... params) {
                String response = "";
                try {
                    List<NameValuePair> postParameters = new ArrayList<>();
                    response = AppUtils.callWebServiceWithMultiParam(Register_Activity.this, postParameters, QueryUtils.methodToCheck_OTPPermission, "Splash");
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return response;
            }

            @Override
            protected void onPostExecute(String resultData) {
                System.gc();
                Runtime.getRuntime().gc();
                AppUtils.dismissProgressDialog();
                try {
                    JSONObject jsonObject = new JSONObject(resultData);

                    if (jsonObject.getString("Status").equalsIgnoreCase("True")) {
                        JSONArray jsonArray = jsonObject.getJSONArray("Data");
                        String managestock = jsonArray.getJSONObject(0).getString("IsJoinOTP");

                        if (managestock.equalsIgnoreCase("N")) {
                            CheckOTP = false;
                        } else {
                            CheckOTP = true;
                        }
                    }

                } catch (Exception e) {
                    AppUtils.showExceptionDialog(Register_Activity.this);
                    e.printStackTrace();
                }
            }
        }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    private void executeSendOTP() {
        try {
            if (AppUtils.isNetworkAvailable(Register_Activity.this)) {
                new AsyncTask<Void, Void, String>() {
                    protected void onPreExecute() {
                        AppUtils.showProgressDialog(Register_Activity.this);
                    }

                    @Override
                    protected String doInBackground(Void... params) {
                        String response = "";
                        try {
                            List<NameValuePair> postParameters = new ArrayList<>();
                            postParameters.add(new BasicNameValuePair("MobileNo", "" + mobile_number));
                            response = AppUtils.callWebServiceWithMultiParam(Register_Activity.this, postParameters, QueryUtils.methodtoSendJoiningOTP, TAG);
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
                                JSONArray jsonArray = jsonObject.getJSONArray("Data");
                                String managestock = jsonArray.getJSONObject(0).getString("OTP");

                                OTP_received_from_server = managestock;

                                findViewById(R.id.ll_button_register).setVisibility(View.VISIBLE);
                                findViewById(R.id.ll_button_submit).setVisibility(View.GONE);

                                edtxt_sponsor_id.setEnabled(false);
                                edtxt_name.setEnabled(false);
                                edtxt_address.setEnabled(false);
                                edtxt_mobile.setEnabled(false);
                                edtxt_pan_number.setEnabled(false);
                                edtxt_password.setEnabled(false);
                                txt_select_date.setEnabled(false);

                            } else {
                                AppUtils.alertDialog(Register_Activity.this, jsonObject.getString("Message"));
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            AppUtils.showExceptionDialog(Register_Activity.this);
                        }
                    }
                }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            } else {
                AppUtils.alertDialog(Register_Activity.this, getResources().getString(R.string.txt_networkAlert));
            }
        } catch (Exception e) {
            e.printStackTrace();
            AppUtils.showExceptionDialog(Register_Activity.this);
        }
    }

    private void showConfirmationDialog() {
        try {
            final Dialog dialog = AppUtils.createDialog(Register_Activity.this, false);
            dialog.setCancelable(false);

            TextView txt_DialogTitle = dialog.findViewById(R.id.txt_DialogTitle);
            txt_DialogTitle.setText(Html.fromHtml("20% TDS will be deducted on not submitting the pan card."));

            TextView txt_submit = dialog.findViewById(R.id.txt_submit);
            txt_submit.setText("Agree");
            txt_submit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {

                        if (CheckOTP)
                            executeSendOTP();
                        else
                            createRegistrationRequest();

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });

            TextView txt_cancel = dialog.findViewById(R.id.txt_cancel);
            txt_cancel.setText("Decline");
            txt_cancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        dialog.dismiss();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
            dialog.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void showWelcomeDialog(String LoginId) {
        try {
            final Dialog dialog = new Dialog(this, R.style.ThemeDialogCustom);
            dialog.getWindow().getAttributes().windowAnimations = R.style.dialog_animation;
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setCancelable(false);
            dialog.setContentView(R.layout.custom_dialog_one);

            TextView txt_DialogTitle = dialog.findViewById(R.id.txt_DialogTitle);

            txt_DialogTitle.setText(Html.fromHtml("<p align=\"justify\"><font color=\"#0073b7\"><b>Dear " + name + ",</b></font><br>" +
                    "<b><font color=\"#ff851b\">Congratulations,</font> </b>On your decision to soar sky high with us.<br><br><br></p>" +
                    "<p align=\"justify\">You are now a part of the opportunity of the millennium. Kalpamrit Marketing an exciting people business. A business that has the potential to Turn Your dreams into reality. As you build your business, you will establish lifelong friendship and develope support system unparalleled in any other business.</p>" +
                    "<p align=\"justify\"><br>Your Login Detail has been sent to your Mobile Number and PFB same for reference.<br>" +
                    "Login ID - <font color=\"#ff851b\"><b>" + LoginId + "</b></font><br>" +
                    "Password - <font color=\"#ff851b\"><b>" + password + "</b></font></p><br>" +
                    "<p align=\"justify\">Keep it up! See you at the top!<br><br>" +
                    "Thanks,<br>Kalpamrit Marketing</p>"));

            TextView txt_submit = dialog.findViewById(R.id.txt_submit);
            txt_submit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {

                        dialog.dismiss();
                        Register_Activity.this.finish();

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });

            dialog.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /*State list added on 01-11-2019 12:26 PM*/
    private void executeStateRequestone() {
        new AsyncTask<Void, Void, String>() {
            protected void onPreExecute() {
                AppUtils.showProgressDialog(Register_Activity.this);
            }

            @Override
            protected String doInBackground(Void... params) {
                String response = "";
                try {
                    List<NameValuePair> postParameters = new ArrayList<>();
                    response = AppUtils.callWebServiceWithMultiParam(Register_Activity.this, postParameters, QueryUtils.methodMaster_FillState, TAG);
                } catch (Exception ignored) {
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
                            getStateResultone(jsonArrayData);
                        } else {
                            AppUtils.alertDialog(Register_Activity.this, jsonObject.getString("Message"));
                        }
                    } else {
                        AppUtils.alertDialog(Register_Activity.this, jsonObject.getString("Message"));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    private void getStateResultone(JSONArray jsonArray) {
        try {
            AppController.stateList.clear();

            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                HashMap<String, String> map = new HashMap<>();

                map.put("STATECODE", jsonObject.getString("STATECODE"));
                map.put("State", WordUtils.capitalizeFully(jsonObject.getString("State")));

                AppController.stateList.add(map);
            }

            //  showStateDialog();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void executeStateRequest() {
        new AsyncTask<Void, Void, String>() {
            protected void onPreExecute() {
                AppUtils.showProgressDialog(Register_Activity.this);
            }

            @Override
            protected String doInBackground(Void... params) {
                String response = "";
                try {
                    List<NameValuePair> postParameters = new ArrayList<>();
                    response = AppUtils.callWebServiceWithMultiParam(Register_Activity.this, postParameters, QueryUtils.methodMaster_FillState, TAG);
                } catch (Exception ignored) {
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
                            AppUtils.alertDialog(Register_Activity.this, jsonObject.getString("Message"));
                        }
                    } else {
                        AppUtils.alertDialog(Register_Activity.this, jsonObject.getString("Message"));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    private void getStateResult(JSONArray jsonArray) {
        try {
            AppController.stateList.clear();

            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                HashMap<String, String> map = new HashMap<>();

                map.put("STATECODE", jsonObject.getString("STATECODE"));
                map.put("State", WordUtils.capitalizeFully(jsonObject.getString("State")));

                AppController.stateList.add(map);
            }

            showStateDialog();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void showStateDialog() {
        try {
            stateArray = new String[AppController.stateList.size()];
            for (int i = 0; i < AppController.stateList.size(); i++) {
                stateArray[i] = AppController.stateList.get(i).get("State");
            }

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Select State");
            builder.setItems(stateArray, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int item) {
                    // Do something with the selection
                    txt_state.setText(stateArray[item]);
                }
            });
            builder.create().show();
        } catch (Exception e) {
            e.printStackTrace();
            AppUtils.showExceptionDialog(Register_Activity.this);
        }
    }

}