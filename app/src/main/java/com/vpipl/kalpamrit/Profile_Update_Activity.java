package com.vpipl.kalpamrit;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.BottomSheetDialog;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TextInputEditText;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.widget.Toolbar;
import android.telephony.TelephonyManager;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.vpipl.kalpamrit.Adapters.ExpandableListAdapter;
import com.vpipl.kalpamrit.SMS.MySMSBroadcastReceiver;
import com.vpipl.kalpamrit.Utils.AppUtils;
import com.vpipl.kalpamrit.Utils.Cache;
import com.vpipl.kalpamrit.Utils.CircularImageView;
import com.vpipl.kalpamrit.Utils.QueryUtils;
import com.vpipl.kalpamrit.Utils.SPUtils;
import com.vpipl.kalpamrit.Utils.SmsListener;
import com.vpipl.kalpamrit.Utils.SmsReceiver;

import org.apache.commons.lang3.text.WordUtils;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import id.zelory.compressor.Compressor;


/**
 * Created by PC14 on 3/22/2016.
 */
public class Profile_Update_Activity extends AppCompatActivity {

    public int RESULT_GALLERY = 0;
    public int CAMERA_REQUEST = 1;

    private static DrawerLayout drawer;
    private static NavigationView navigationView;
    private String TAG = "Profile_Update_Activity";
    private TextInputEditText edtxt_memberName;
    private TextInputEditText edtxt_father_name;
    private TextInputEditText edtxt_address;
    private TextInputEditText edtxt_district;
    private TextInputEditText edtxt_city;
    private TextInputEditText edtxt_pinCode;
    private TextInputEditText edtxt_mobileNumber;
    private TextInputEditText edtxt_phoneNumber;
    private TextInputEditText edtxt_email;
    private TextInputEditText edtxt_aadhaarNumber;
    private TextInputEditText edtxt_bankIfsc;
    private TextInputEditText edtxt_bankBranch;
    private TextInputEditText edtxt_bankAcntNumber;
    private TextInputEditText edtxt_PANNumber;
    private TextInputEditText edtxt_nomineeName;
    private TextInputEditText edtxt_nomineeRelation;
    private TextInputEditText txt_dob;
    private TextInputEditText txt_nominee_dob;
    private TextInputEditText txt_prefix;
    private TextInputEditText txt_state;
    private TextInputEditText txt_bankname;
    private Button btn_updateProfilesendotp;

    //    RadioButton rb_male, rb_female, rb_gender;
//    RadioGroup rg_gender;
    private ImageView iv_upload;
    private ImageView iv_Profile_Pic;
    private String[] stateArray;
    private String[] bankArray;
    private String[] selectRelationArray;
    private TelephonyManager telephonyManager;
    private String onWhichDateClick = "";
    private String Name;
    private String Prefix;
    private String FatherName;
    private String dob;
    private String address;
    private String state;
    private String district;
    private String city;
    private String pincode;
    private String mobile_number;
    private String aadhaarNumber;
    private String phone_number;
    private String email;
    private String nominee_name;
    private String nominee_dob;
    private String nominee_relation;
    private String bank_ifsc;
    private String bank_name;
    private String bank_account_number;

    //    String Gender;
    private String bank_branch_name;
    private String pan_number;
    private Uri imageUri;
    private Bitmap bitmap = null;
    private Calendar myCalendar;
    private SimpleDateFormat sdf;
    private String selectedImagePath = "";
    private BottomSheetDialog mBottomSheetDialog;
    private TextView txt_welcome_name;
    private TextView txt_id_number;
    private TextView txt_available_wb;
    private ArrayList<String> listDataHeader;
    private HashMap<String, List<String>> listDataChild;
    private int lastExpandedPosition = -1;
    private ExpandableListView expListView;
    private CircularImageView profileImage;
    private JSONArray HeadingJarray;
    LinearLayout ll_update_profile_data,ll_update_profile_enter_otp ;
    String recieve_otp = "" ;
    DatePickerDialog datePickerDialog;
    EditText ed_otp;
    Button btn_updateProfileafterotp ;

    private DatePickerDialog.OnDateSetListener myDateListener = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            try {

                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, monthOfYear);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

                if (new Date().after(myCalendar.getTime())) {
                    if (onWhichDateClick.equals("et_dob")) {
                        txt_dob.setText(sdf.format(myCalendar.getTime()));
                    } else {
                        txt_nominee_dob.setText(sdf.format(myCalendar.getTime()));
                    }
                } else {
                    AppUtils.alertDialog(Profile_Update_Activity.this, getResources().getString(R.string.error_invalid_dob));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };

    ImageView img_nav_back, img_login_logout;

    public void SetupToolbar() {

        img_nav_back = findViewById(R.id.img_nav_back);
        img_login_logout = findViewById(R.id.img_login_logout);


        img_nav_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (drawer.isDrawerOpen(navigationView)) {
                    img_nav_back.setImageDrawable(getResources().getDrawable(R.drawable.icon_nav_bar));
                    drawer.closeDrawer(navigationView);
                } else {
                    img_nav_back.setImageDrawable(getResources().getDrawable(R.drawable.icon_nav_bar_close));
                    drawer.openDrawer(navigationView);
                }
            }
        });

        img_login_logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!AppController.getSpIsLogin().getBoolean(SPUtils.IS_LOGIN, false))
                    startActivity(new Intent(Profile_Update_Activity.this, Login_Activity.class));
                else
                    AppUtils.showDialogSignOut(Profile_Update_Activity.this);
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
        setContentView(R.layout.activity_profile_update);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);

        try {
            AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);

            Toolbar toolbar = findViewById(R.id.toolbar);
            setSupportActionBar(toolbar);
            getSupportActionBar().setTitle("");
            SetupToolbar();


            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);


            selectRelationArray = getResources().getStringArray(R.array.selectRelation);
            telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);

            myCalendar = Calendar.getInstance();
            sdf = new SimpleDateFormat("dd MMM yyyy");

            iv_Profile_Pic = findViewById(R.id.iv_Profile_Pic);
            iv_upload = findViewById(R.id.iv_upload);

            txt_dob = findViewById(R.id.txt_dob);
            txt_nominee_dob = findViewById(R.id.txt_nominee_dob);
            txt_prefix = findViewById(R.id.txt_prefix);
            txt_state = findViewById(R.id.txt_state);
            txt_bankname = findViewById(R.id.txt_bankname);
            ed_otp = findViewById(R.id.ed_otp);
            btn_updateProfileafterotp = findViewById(R.id.btn_updateProfileafterotp);

//            rg_gender = (RadioGroup) findViewById(R.id.rg_gender);
//            rb_male = (RadioButton) findViewById(R.id.rb_male);
//            rb_female = (RadioButton) findViewById(R.id.rb_female);

            edtxt_memberName = findViewById(R.id.edtxt_memberName);
            edtxt_father_name = findViewById(R.id.edtxt_father_name);
            edtxt_address = findViewById(R.id.edtxt_address);
            edtxt_district = findViewById(R.id.edtxt_district);
            edtxt_city = findViewById(R.id.edtxt_city);
            edtxt_pinCode = findViewById(R.id.edtxt_pinCode);
            edtxt_mobileNumber = findViewById(R.id.edtxt_mobileNumber);
            edtxt_phoneNumber = findViewById(R.id.edtxt_phoneNumber);
            edtxt_email = findViewById(R.id.edtxt_email);
            edtxt_aadhaarNumber = findViewById(R.id.edtxt_aadhaarNumber);
            edtxt_nomineeName = findViewById(R.id.edtxt_nomineeName);
            edtxt_nomineeRelation = findViewById(R.id.edtxt_nomineeRelation);
            edtxt_bankIfsc = findViewById(R.id.edtxt_bankIfsc);
            edtxt_bankAcntNumber = findViewById(R.id.edtxt_bankAcntNumber);
            edtxt_bankBranch = findViewById(R.id.edtxt_bankBranch);
            edtxt_PANNumber = findViewById(R.id.edtxt_PANNumber);
            ll_update_profile_enter_otp = findViewById(R.id.ll_update_profile_enter_otp);
            ll_update_profile_data = findViewById(R.id.ll_update_profile_data);

            edtxt_PANNumber.setFilters(new InputFilter[]{new InputFilter.AllCaps()});
            edtxt_bankIfsc.setFilters(new InputFilter[]{new InputFilter.AllCaps()});

            mBottomSheetDialog = new BottomSheetDialog(this);
            View sheetView = this.getLayoutInflater().inflate(R.layout.bottom_sheet, null);
            mBottomSheetDialog.setContentView(sheetView);
            mBottomSheetDialog.setTitle("Complete action using...");

            iv_upload.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    mBottomSheetDialog.show();
                }
            });

            LinearLayout camera = sheetView.findViewById(R.id.bottom_sheet_camera);
            LinearLayout gallery = sheetView.findViewById(R.id.bottom_sheet_gallery);

            camera.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    imageUri = AppUtils.getOutputMediaFileUri(1, TAG, Profile_Update_Activity.this);
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
                    startActivityForResult(intent, CAMERA_REQUEST);
                    mBottomSheetDialog.dismiss();
                }
            });

            gallery.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent galleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    startActivityForResult(galleryIntent, RESULT_GALLERY);
                    mBottomSheetDialog.dismiss();
                }
            });


            edtxt_bankIfsc.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {

                    final int DRAWABLE_RIGHT = 2;

                    if (event.getAction() == MotionEvent.ACTION_UP) {
                        if (event.getRawX() >= (edtxt_bankIfsc.getRight() - edtxt_bankIfsc.getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width())) {
                            if (AppUtils.isNetworkAvailable(Profile_Update_Activity.this)) {
                                if (!edtxt_bankIfsc.getText().toString().isEmpty()) {
                                    if (edtxt_bankIfsc.getText().toString().trim().length() == 11) {

                                        executeToGetIFSCInfo(edtxt_bankIfsc.getText().toString());
                                    } else {
                                        AppUtils.alertDialog(Profile_Update_Activity.this, getResources().getString(R.string.error_et_mr_Ifsc));
                                        edtxt_bankIfsc.requestFocus();
                                    }
                                } else {
                                    AppUtils.alertDialog(Profile_Update_Activity.this, getResources().getString(R.string.error_et_Ifsc));
                                    edtxt_bankIfsc.requestFocus();
                                }
                            } else {
                                AppUtils.alertDialog(Profile_Update_Activity.this, getResources().getString(R.string.txt_networkAlert));
                            }
                            return true;
                        }
                    }
                    return false;
                }
            });

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


            txt_bankname.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View view, boolean b) {
                    if (b) {
                        if (AppController.bankList.size() != 0) {
                            showBankDialog();
                            txt_bankname.clearFocus();
                        } else {
                            executeBankRequest();
                        }
                    }
                }
            });
            /*SmsReceiver.bindListener(new SmsListener() {
                @Override
                public void messageReceived(String messageText) {
                    try {
                        if (messageText.length() == 6) {
                            ed_otp.setText(messageText);
                            ValidateData();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });*/
            MySMSBroadcastReceiver.bindListener(new SmsListener() {
                @Override
                public void messageReceived(String messageText) {
                    try {
                        if (messageText.length() == 6) {
                            ed_otp.setText(messageText);
                            ValidateData();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
            ed_otp.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {

                }

                @Override
                public void afterTextChanged(Editable s)
                {
                    if (s.length() >= 6) {
                        btn_updateProfileafterotp.setVisibility(View.VISIBLE);
                      /*  tv_otp_expired.setVisibility(View.GONE);
                        tv_resend.setVisibility(View.GONE);*/
                    } else
                    {
                        btn_updateProfileafterotp.setVisibility(View.GONE);
                    }
                }
            });



            txt_prefix.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View view, boolean b) {

                    if (b) {
                        if (selectRelationArray.length != 0) {
                            showMemRelationDialog();
                            txt_prefix.clearFocus();
                        } else {
                            AppUtils.showExceptionDialog(Profile_Update_Activity.this);
                        }
                    }
                }
            });

            btn_updateProfilesendotp = findViewById(R.id.btn_updateProfilesendotp);
            btn_updateProfilesendotp.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    AppUtils.hideKeyboardOnClick(Profile_Update_Activity.this, view);
                    validateUpdateProfileRequest();
                }
            });

            txt_dob.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onWhichDateClick = "et_dob";
                    showDOBPicker();
                }
            });

            txt_nominee_dob.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onWhichDateClick = "et_nomineeDob";
                    showdatePicker();
                }
            });

            drawer = findViewById(R.id.drawer_layout);
            navigationView = findViewById(R.id.nav_view);
            View navHeaderView = navigationView.getHeaderView(0);
            txt_welcome_name = navHeaderView.findViewById(R.id.txt_welcome_name);
            txt_available_wb = navHeaderView.findViewById(R.id.txt_available_wb);
            txt_id_number = navHeaderView.findViewById(R.id.txt_id_number);
            profileImage = navHeaderView.findViewById(R.id.iv_Profile_Pic);
            LinearLayout LL_Nav = navHeaderView.findViewById(R.id.LL_Nav);
            expListView = findViewById(R.id.left_drawer);

            listDataHeader = new ArrayList<>();
            listDataChild = new HashMap<>();

            HeadingJarray = Splash_Activity.HeadingJarray;

            btn_updateProfileafterotp.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ValidateData();
                }
            });

            LL_Nav.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String Usertype = (AppController.getSpUserInfo().getString(SPUtils.USER_TYPE, ""));
                    if (Usertype.equalsIgnoreCase("DISTRIBUTOR")) {
                        if (AppController.getSpIsLogin().getBoolean(SPUtils.IS_LOGIN, false)) {
                            startActivity(new Intent(Profile_Update_Activity.this, Profile_View_Activity.class));
                        } else {
                            startActivity(new Intent(Profile_Update_Activity.this, Login_Activity.class));
                        }

                        if (drawer.isDrawerOpen(GravityCompat.START)) {
                            drawer.closeDrawer(GravityCompat.START);
                        }
                    }
                }
            });

            if (AppUtils.isNetworkAvailable(Profile_Update_Activity.this)) {
                executeLoginRequest();
                executeBankRequestone();
                executeStateRequestone();
            } else {
                AppUtils.alertDialog(Profile_Update_Activity.this, getResources().getString(R.string.txt_networkAlert));
            }


        } catch (Exception e) {
            e.printStackTrace();
            AppUtils.showExceptionDialog(Profile_Update_Activity.this);
        }
    }

    private void showDOBPicker() {
        Calendar calendar = Calendar.getInstance();
        datePickerDialog = new DatePickerDialog(this, myDateListener, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
        calendar.add(Calendar.YEAR, -18);
        datePickerDialog.getDatePicker().setMaxDate(calendar.getTime().getTime());
        datePickerDialog.show();
    }

    private void showdatePicker() {
        Calendar calendar = Calendar.getInstance();
        datePickerDialog = new DatePickerDialog(this, myDateListener, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
        datePickerDialog.getDatePicker().setMaxDate(calendar.getTime().getTime());
        datePickerDialog.show();
    }


    private void executeLoginRequest() {
        try {

            if (AppUtils.isNetworkAvailable(Profile_Update_Activity.this)) {
                new AsyncTask<Void, Void, String>() {
                    protected void onPreExecute() {
                        AppUtils.showProgressDialog(Profile_Update_Activity.this);
                    }

                    @Override
                    protected String doInBackground(Void... params) {
                        String response = "";
                        try {

                            List<NameValuePair> postParameters = new ArrayList<>();
//                            postParameters.add(new BasicNameValuePair("UserID", userId));
//                            postParameters.add(new BasicNameValuePair("Password", passwd));

                            postParameters.add(new BasicNameValuePair("UserID", AppController.getSpUserInfo().getString(SPUtils.USER_ID_NUMBER, "")));
                            postParameters.add(new BasicNameValuePair("Password", AppController.getSpUserInfo().getString(SPUtils.USER_PASSWORD, "")));

                            postParameters.add(new BasicNameValuePair("UserType", "D"));
                            response = AppUtils.callWebServiceWithMultiParam(Profile_Update_Activity.this, postParameters, QueryUtils.methodMemberLoginOnPortal, TAG);

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
                                continueapp();
                            } else {

                                Toast.makeText(Profile_Update_Activity.this, "Please Login to continue..", Toast.LENGTH_SHORT).show();

                                AppController.getSpUserInfo().edit().clear().commit();
                                AppController.getSpIsLogin().edit().clear().commit();

                                Intent intent = new Intent(Profile_Update_Activity.this, Login_Activity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                intent.putExtra("SendToHome", true);
                                startActivity(intent);
                                finish();

                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            AppUtils.showExceptionDialog(Profile_Update_Activity.this);
                        }
                    }
                }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            }
        } catch (Exception e) {
            e.printStackTrace();
            AppUtils.showExceptionDialog(Profile_Update_Activity.this);
        }
    }

    public void continueapp() {

        executeToGetProfileInfo();
        enableExpandableList();
        LoadNavigationHeaderItems();
    }


    private void validateUpdateProfileRequest() {
        try {

            Name = edtxt_memberName.getText().toString().trim();
            Prefix = txt_prefix.getText().toString().trim();
            FatherName = edtxt_father_name.getText().toString().trim();

//            int selectedId = rg_gender.getCheckedRadioButtonId();
//            RadioButton rb_gender = (RadioButton) findViewById(selectedId);
//            Gender = rb_gender.getText().toString().trim();

            dob = txt_dob.getText().toString().trim();
            address = edtxt_address.getText().toString().trim();
            state = txt_state.getText().toString().trim();

            district = edtxt_district.getText().toString().trim();
            city = edtxt_city.getText().toString().trim();
            pincode = edtxt_pinCode.getText().toString().trim();
            mobile_number = edtxt_mobileNumber.getText().toString().trim();
            aadhaarNumber = edtxt_aadhaarNumber.getText().toString().trim();
            phone_number = edtxt_phoneNumber.getText().toString().trim();
            email = edtxt_email.getText().toString().trim();
            nominee_name = edtxt_nomineeName.getText().toString().trim();
            nominee_dob = txt_nominee_dob.getText().toString().trim();
            nominee_relation = edtxt_nomineeRelation.getText().toString().trim();
            bank_ifsc = edtxt_bankIfsc.getText().toString().trim();
            bank_name = txt_bankname.getText().toString().trim();
            bank_account_number = edtxt_bankAcntNumber.getText().toString().trim();
            bank_branch_name = edtxt_bankBranch.getText().toString().trim();
            pan_number = edtxt_PANNumber.getText().toString().trim();

            if (TextUtils.isEmpty(Name)) {
                AppUtils.alertDialog(Profile_Update_Activity.this, getResources().getString(R.string.error_et_mr_name));
                edtxt_memberName.requestFocus();
//            } else if (TextUtils.isEmpty(Prefix)) {
//                AppUtils.alertDialog(Profile_Update_Activity.this, getResources().getString(R.string.error_et_mr_fname_prefix));
//            } else if (TextUtils.isEmpty(FatherName)) {
//                AppUtils.alertDialog(Profile_Update_Activity.this, getResources().getString(R.string.error_et_mr_fname));
//                edtxt_father_name.requestFocus();
//            } else if (TextUtils.isEmpty(dob)) {
//                AppUtils.alertDialog(Profile_Update_Activity.this, getResources().getString(R.string.error_et_mr_date));
//                txt_dob.requestFocus();
            } else if (TextUtils.isEmpty(address)) {
                AppUtils.alertDialog(Profile_Update_Activity.this, getResources().getString(R.string.error_et_mr_address));
                edtxt_address.requestFocus();
//            } else if (TextUtils.isEmpty(city)) {
//                AppUtils.alertDialog(Profile_Update_Activity.this, "Please Enter City.");
//                edtxt_city.requestFocus();
//            } else if (TextUtils.isEmpty(district)) {
//                AppUtils.alertDialog(Profile_Update_Activity.this, "Please Enter District.");
//                edtxt_district.requestFocus();
//            } else if (TextUtils.isEmpty(state)) {
//                AppUtils.alertDialog(Profile_Update_Activity.this, getResources().getString(R.string.error_et_mr_state));
//                txt_state.requestFocus();
//            } else if (TextUtils.isEmpty(pincode)) {
//                AppUtils.alertDialog(Profile_Update_Activity.this, "Please Enter PinCode.");
//                edtxt_pinCode.requestFocus();
            } else if (!TextUtils.isEmpty(pincode) && !pincode.trim().matches(AppUtils.mPINCodePattern)) {
                AppUtils.alertDialog(Profile_Update_Activity.this, getResources().getString(R.string.error_et_mr_PINno));
                edtxt_pinCode.requestFocus();
            } else if (TextUtils.isEmpty(mobile_number)) {
                AppUtils.alertDialog(Profile_Update_Activity.this, getResources().getString(R.string.error_required_mobile_number));
                edtxt_mobileNumber.requestFocus();
            } else if (mobile_number.trim().length() != 10) {
                AppUtils.alertDialog(Profile_Update_Activity.this, getResources().getString(R.string.error_invalid_mobile_number));
                edtxt_mobileNumber.requestFocus();
//            } else if (!TextUtils.isEmpty(phone_number))
//            {
//                AppUtils.alertDialog(Profile_Update_Activity.this, getResources().getString(R.string.error_et_mr_phoneNumber));
//                edtxt_phoneNumber.requestFocus();
            } else if (!TextUtils.isEmpty(phone_number) && phone_number.trim().length() != 10) {
                AppUtils.alertDialog(Profile_Update_Activity.this, "Alternate Mobile Number is Invalid");
                edtxt_phoneNumber.requestFocus();
            } else if (!TextUtils.isEmpty(email) && AppUtils.isValidMail(email.trim())) {
                AppUtils.alertDialog(Profile_Update_Activity.this, getResources().getString(R.string.error_invalid_email));
                edtxt_email.requestFocus();
            } /*else if (!TextUtils.isEmpty(aadhaarNumber) && aadhaarNumber.length() != 12) {
                AppUtils.alertDialog(Profile_Update_Activity.this, "Invalid Aadhaar Number");
                edtxt_aadhaarNumber.requestFocus();
            } else if (!TextUtils.isEmpty(pan_number) && !pan_number.matches(AppUtils.mPANPattern)) {
                AppUtils.alertDialog(Profile_Update_Activity.this, getResources().getString(R.string.error_invalid_PANno));
                edtxt_PANNumber.requestFocus();
            }*/ else if (!AppUtils.isNetworkAvailable(Profile_Update_Activity.this)) {
                AppUtils.alertDialog(Profile_Update_Activity.this, getResources().getString(R.string.txt_networkAlert));
            } else {
               // startUpdateProfile();
                executeSendOtpForUpdateProfileRequest();
            }
        } catch (Exception e) {
            e.printStackTrace();
            AppUtils.showExceptionDialog(Profile_Update_Activity.this);
        }
    }

    private void startUpdateProfile() {
        try {
            if (AppUtils.isNetworkAvailable(Profile_Update_Activity.this)) {

                List<NameValuePair> postParameters = new ArrayList<>();
                postParameters.add(new BasicNameValuePair("FormNo", AppController.getSpUserInfo().getString(SPUtils.USER_FORM_NUMBER, "")));
                postParameters.add(new BasicNameValuePair("MemRelation", "" + Prefix));
                postParameters.add(new BasicNameValuePair("FatherName", "" + FatherName));
                postParameters.add(new BasicNameValuePair("DOB", "" + dob.trim()));
                postParameters.add(new BasicNameValuePair("Address", "" + address.trim()));
                postParameters.add(new BasicNameValuePair("City", "" + city.trim()));
                postParameters.add(new BasicNameValuePair("District", "" + district.trim()));

                String stateCode = "0";
                for (int i = 0; i < AppController.stateList.size(); i++) {
                    if (state.equals(AppController.stateList.get(i).get("State"))) {
                        stateCode = AppController.stateList.get(i).get("STATECODE");
                    }
                }

                postParameters.add(new BasicNameValuePair("StateCode", "" + stateCode));
                postParameters.add(new BasicNameValuePair("PinCode", "" + pincode.trim()));
                postParameters.add(new BasicNameValuePair("PhoneNo", "" + phone_number.trim()));
                postParameters.add(new BasicNameValuePair("MobileNo", "" + mobile_number.trim()));
                postParameters.add(new BasicNameValuePair("EMailID", "" + email.trim()));
                postParameters.add(new BasicNameValuePair("PanNo", "" + pan_number.trim()));
                postParameters.add(new BasicNameValuePair("AdhaarNo", "" + aadhaarNumber.trim()));

                String Bankid = "0";
                for (int i = 0; i < AppController.bankList.size(); i++) {
                    if (bank_name.equalsIgnoreCase(AppController.bankList.get(i).get("Bank"))) {
                        Bankid = AppController.bankList.get(i).get("BID");
                    }
                }

                postParameters.add(new BasicNameValuePair("BankId", "" + Bankid));
                postParameters.add(new BasicNameValuePair("BranchName", "" + bank_branch_name.trim()));
                postParameters.add(new BasicNameValuePair("AccountNo", "" + bank_account_number.trim()));
                postParameters.add(new BasicNameValuePair("IFSCCode", "" + bank_ifsc.trim()));
                postParameters.add(new BasicNameValuePair("NomineeName", "" + nominee_name.trim()));
                postParameters.add(new BasicNameValuePair("NomineeDOB", "" + nominee_dob.trim()));
                postParameters.add(new BasicNameValuePair("Relation", "" + nominee_relation.trim()));
                postParameters.add(new BasicNameValuePair("IPAddress", telephonyManager.getDeviceId()));

                executeUpdateprofileRequest(postParameters);
            } else {
                AppUtils.alertDialog(Profile_Update_Activity.this, getResources().getString(R.string.txt_networkAlert));
            }
        } catch (Exception e) {
            e.printStackTrace();
            AppUtils.showExceptionDialog(Profile_Update_Activity.this);
        }
    }

    private void executeToGetIFSCInfo(final String ifscCode) {
        new AsyncTask<Void, Void, String>() {
            protected void onPreExecute() {
                AppUtils.showProgressDialog(Profile_Update_Activity.this);
            }

            @Override
            protected String doInBackground(Void... params) {
                String response = "";
                try {
                    List<NameValuePair> postParameters = new ArrayList<>();
                    postParameters.add(new BasicNameValuePair("IFSCCode", ifscCode));

                    response = AppUtils.callWebServiceWithMultiParam(Profile_Update_Activity.this, postParameters, QueryUtils.methodGet_BankDetail, TAG);
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

                            for (int i = 0; i < AppController.bankList.size(); i++) {
                                if (jsonArrayData.getJSONObject(0).getString("Branchcode").equals(AppController.bankList.get(i).get("BID"))) {
                                    txt_bankname.setText(AppController.bankList.get(i).get("Bank"));
                                }
                            }
                            edtxt_bankBranch.setText(jsonArrayData.getJSONObject(0).getString("Branch"));
                        } else {
                            AppUtils.alertDialog(Profile_Update_Activity.this, jsonObject.getString("Message"));
                        }
                    } else {
                        AppUtils.alertDialog(Profile_Update_Activity.this, jsonObject.getString("Message"));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    private void executeStateRequest() {
        new AsyncTask<Void, Void, String>() {
            protected void onPreExecute() {
                AppUtils.showProgressDialog(Profile_Update_Activity.this);
            }

            @Override
            protected String doInBackground(Void... params) {
                String response = "";
                try {
                    List<NameValuePair> postParameters = new ArrayList<>();
                    response = AppUtils.callWebServiceWithMultiParam(Profile_Update_Activity.this, postParameters, QueryUtils.methodMaster_FillState, TAG);
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
                            AppUtils.alertDialog(Profile_Update_Activity.this, jsonObject.getString("Message"));
                        }
                    } else {
                        AppUtils.alertDialog(Profile_Update_Activity.this, jsonObject.getString("Message"));
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

    private void executeBankRequest() {
        new AsyncTask<Void, Void, String>() {
            protected void onPreExecute() {
                AppUtils.showProgressDialog(Profile_Update_Activity.this);
            }

            @Override
            protected String doInBackground(Void... params) {
                String response = "";
                try {
                    List<NameValuePair> postParameters = new ArrayList<>();
                    response = AppUtils.callWebServiceWithMultiParam(Profile_Update_Activity.this, postParameters, QueryUtils.methodMaster_FillBank, TAG);
                } catch (Exception ignored) {
                }
                return response;
            }

            @Override
            protected void onPostExecute(String resultData) {
                try {
                    JSONObject jsonObject = new JSONObject(resultData);
                    JSONArray jsonArrayData = jsonObject.getJSONArray("Data");

                    if (jsonObject.getString("Status").equalsIgnoreCase("True")) {
                        if (jsonArrayData.length() != 0) {
                            getBankResult(jsonArrayData);
                        } else {
                            AppUtils.alertDialog(Profile_Update_Activity.this, jsonObject.getString("Message"));
                        }
                    } else {
                        AppUtils.alertDialog(Profile_Update_Activity.this, jsonObject.getString("Message"));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    private void getBankResult(JSONArray jsonArray) {
        try {
            AppController.bankList.clear();

            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                HashMap<String, String> map = new HashMap<>();

                map.put("BID", jsonObject.getString("BID"));
                map.put("Bank", WordUtils.capitalizeFully(jsonObject.getString("Bank")));

                AppController.bankList.add(map);
            }


            showBankDialog();
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
            AppUtils.showExceptionDialog(Profile_Update_Activity.this);
        }
    }

    private void showBankDialog() {
        try {
            bankArray = new String[AppController.bankList.size()];
            for (int i = 0; i < AppController.bankList.size(); i++) {
                bankArray[i] = AppController.bankList.get(i).get("Bank");
            }

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Select Bank");
            builder.setItems(bankArray, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int item) {
                    // Do something with the selection
                    txt_bankname.setText(bankArray[item]);
                }
            });
            builder.create().show();
        } catch (Exception e) {
            e.printStackTrace();
            AppUtils.showExceptionDialog(Profile_Update_Activity.this);
        }
    }

    private void executeToGetProfileInfo() {
        try {
            if (AppUtils.isNetworkAvailable(Profile_Update_Activity.this)) {
                new AsyncTask<Void, Void, String>() {
                    protected void onPreExecute() {
                        AppUtils.showProgressDialog(Profile_Update_Activity.this);
                    }

                    @Override
                    protected String doInBackground(Void... params) {
                        String response = "";
                        try {
                            List<NameValuePair> postParameters = new ArrayList<>();
                            postParameters.add(new BasicNameValuePair("Formno", AppController.getSpUserInfo().getString(SPUtils.USER_FORM_NUMBER, "")));
                            response = AppUtils.callWebServiceWithMultiParam(Profile_Update_Activity.this, postParameters, QueryUtils.methodToGetUserProfile, TAG);
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
                                    getProfileInfo(jsonArrayData);
                                } else {
                                    AppUtils.alertDialog(Profile_Update_Activity.this, jsonObject.getString("Message"));
                                }
                            } else {
                                AppUtils.alertDialog(Profile_Update_Activity.this, jsonObject.getString("Message"));
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            AppUtils.showExceptionDialog(Profile_Update_Activity.this);
                        }
                    }
                }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            }
        } catch (Exception e) {
            e.printStackTrace();
            AppUtils.showExceptionDialog(Profile_Update_Activity.this);
        }
    }

    private void getProfileInfo(JSONArray jsonArray) {
        try {

            AppController.getSpUserInfo().edit()
                    .putString(SPUtils.USER_ID_NUMBER, jsonArray.getJSONObject(0).getString("IDNo"))
                    .putString(SPUtils.USER_FORM_NUMBER, jsonArray.getJSONObject(0).getString("FormNo"))
                    .putString(SPUtils.USER_FIRST_NAME, jsonArray.getJSONObject(0).getString("MemName"))
                    .putString(SPUtils.USER_PINCODE, jsonArray.getJSONObject(0).getString("Pincode"))
                    .putString(SPUtils.USER_CITY, jsonArray.getJSONObject(0).getString("CityName"))
                    .putString(SPUtils.USER_STATE_CODE, jsonArray.getJSONObject(0).getString("StateCode"))
                    .commit();

            Profile_View_Activity.myprofileDetailsList.clear();
            HashMap<String, String> map = new HashMap<>();
            map.put(SPUtils.USER_ID_NUMBER, "" + jsonArray.getJSONObject(0).getString("IDNo"));
            map.put(SPUtils.USER_CoSponsorID, "" + jsonArray.getJSONObject(0).getString("UpLnFormNo"));
            map.put(SPUtils.USER_NAME, "" + jsonArray.getJSONObject(0).getString("MemName"));
            map.put(SPUtils.USER_FATHER_NAME, "" + jsonArray.getJSONObject(0).getString("MemFName"));
            map.put(SPUtils.USER_Relation_Prefix, "" + jsonArray.getJSONObject(0).getString("MemRelation"));
            map.put(SPUtils.USER_FORM_NUMBER, "" + jsonArray.getJSONObject(0).getString("FormNo"));
            map.put(SPUtils.USER_PASSWORD, "" + jsonArray.getJSONObject(0).getString("Passw"));
            map.put(SPUtils.USER_ADDRESS, "" + jsonArray.getJSONObject(0).getString("Address1"));
            map.put(SPUtils.USER_MOBILE_NO, "" + jsonArray.getJSONObject(0).getString("Mobl"));
            map.put(SPUtils.USER_Phone_NO, "" + jsonArray.getJSONObject(0).getString("PhN1"));
            map.put(SPUtils.USER_DOB, "" + AppUtils.getDateFromAPIDate(jsonArray.getJSONObject(0).getString("MemDOB")));
            map.put(SPUtils.USER_GENDER, "" + jsonArray.getJSONObject(0).getString("Gen"));
            map.put(SPUtils.USER_EMAIL, "" + jsonArray.getJSONObject(0).getString("Email"));
            map.put(SPUtils.USER_AADHAAR, "" + jsonArray.getJSONObject(0).getString("AdhaarNo"));
            map.put(SPUtils.USER_CITY, "" + jsonArray.getJSONObject(0).getString("CityName"));

            String StateName = "";
            for (int i = 0; i < AppController.stateList.size(); i++) {
                if (jsonArray.getJSONObject(0).getString("StateCode").equals(AppController.stateList.get(i).get("STATECODE"))) {
                    StateName = AppController.stateList.get(i).get("State");
                }
            }
            map.put(SPUtils.USER_STATE, "" + StateName);

            map.put(SPUtils.USER_DISTRICT, "" + jsonArray.getJSONObject(0).getString("DistrictName"));
            map.put(SPUtils.USER_PINCODE, "" + jsonArray.getJSONObject(0).getString("Pincode"));
            map.put(SPUtils.USER_PAN, "" + jsonArray.getJSONObject(0).getString("PanNo"));
            map.put(SPUtils.USER_CATEGORY, "" + jsonArray.getJSONObject(0).getString("Category"));
            map.put(SPUtils.USER_SPONSOR_ID, "" + jsonArray.getJSONObject(0).getString("UpLnId"));
            map.put(SPUtils.USER_SPONSOR_NAME, "" + jsonArray.getJSONObject(0).getString("UpLnName"));

            String BankName = "";
            for (int i = 0; i < AppController.bankList.size(); i++) {
                if (jsonArray.getJSONObject(0).getString("BankID").equals(AppController.bankList.get(i).get("BID"))) {
                    BankName = AppController.bankList.get(i).get("Bank");
                }
            }
            map.put(SPUtils.USER_BANKNAME, "" + BankName);
            map.put(SPUtils.USER_BANKACNTNUM, "" + jsonArray.getJSONObject(0).getString("AcNo"));
            map.put(SPUtils.USER_BANKIFSC, "" + jsonArray.getJSONObject(0).getString("IFSCode"));
            map.put(SPUtils.USER_BANKBRANCH, "" + jsonArray.getJSONObject(0).getString("Fld4"));
            map.put(SPUtils.USER_NOMINEE_NAME, "" + jsonArray.getJSONObject(0).getString("NomineeName"));
            map.put(SPUtils.USER_NOMINEE_RELATION, "" + jsonArray.getJSONObject(0).getString("Relation"));
            map.put(SPUtils.USER_NOMINEE_DOB, "" + AppUtils.getDateFromAPIDate(jsonArray.getJSONObject(0).getString("NomineeDob")));

            Profile_View_Activity.myprofileDetailsList.add(map);

            setProfileDetails();

        } catch (Exception e) {
            e.printStackTrace();
            AppUtils.showExceptionDialog(Profile_Update_Activity.this);
        }
    }

    private void setProfileDetails() {
        try {

            edtxt_memberName.setText("" + Profile_View_Activity.myprofileDetailsList.get(0).get(SPUtils.USER_NAME));
            edtxt_father_name.setText("" + Profile_View_Activity.myprofileDetailsList.get(0).get(SPUtils.USER_FATHER_NAME));
            txt_dob.setText("" + Profile_View_Activity.myprofileDetailsList.get(0).get(SPUtils.USER_DOB));
            edtxt_address.setText("" + Profile_View_Activity.myprofileDetailsList.get(0).get(SPUtils.USER_ADDRESS));
            txt_state.setText("" + Profile_View_Activity.myprofileDetailsList.get(0).get(SPUtils.USER_STATE));
            edtxt_district.setText("" + Profile_View_Activity.myprofileDetailsList.get(0).get(SPUtils.USER_DISTRICT));
            edtxt_city.setText("" + Profile_View_Activity.myprofileDetailsList.get(0).get(SPUtils.USER_CITY));
            edtxt_pinCode.setText("" + Profile_View_Activity.myprofileDetailsList.get(0).get(SPUtils.USER_PINCODE));
            edtxt_mobileNumber.setText("" + Profile_View_Activity.myprofileDetailsList.get(0).get(SPUtils.USER_MOBILE_NO));
            edtxt_phoneNumber.setText("" + Profile_View_Activity.myprofileDetailsList.get(0).get(SPUtils.USER_Phone_NO));
            edtxt_email.setText("" + Profile_View_Activity.myprofileDetailsList.get(0).get(SPUtils.USER_EMAIL));
            edtxt_aadhaarNumber.setText("" + Profile_View_Activity.myprofileDetailsList.get(0).get(SPUtils.USER_AADHAAR));
            edtxt_nomineeName.setText("" + Profile_View_Activity.myprofileDetailsList.get(0).get(SPUtils.USER_NOMINEE_NAME));
            txt_nominee_dob.setText("" + Profile_View_Activity.myprofileDetailsList.get(0).get(SPUtils.USER_NOMINEE_DOB));
            edtxt_nomineeRelation.setText("" + Profile_View_Activity.myprofileDetailsList.get(0).get(SPUtils.USER_NOMINEE_RELATION));

            edtxt_bankAcntNumber.setText("" + Profile_View_Activity.myprofileDetailsList.get(0).get(SPUtils.USER_BANKACNTNUM));
            txt_bankname.setText("" + Profile_View_Activity.myprofileDetailsList.get(0).get(SPUtils.USER_BANKNAME));
            edtxt_bankBranch.setText("" + Profile_View_Activity.myprofileDetailsList.get(0).get(SPUtils.USER_BANKBRANCH));
            edtxt_bankIfsc.setText("" + Profile_View_Activity.myprofileDetailsList.get(0).get(SPUtils.USER_BANKIFSC));
            edtxt_PANNumber.setText("" + Profile_View_Activity.myprofileDetailsList.get(0).get(SPUtils.USER_PAN));

            txt_prefix.setText("" + Profile_View_Activity.myprofileDetailsList.get(0).get(SPUtils.USER_Relation_Prefix));

//            String G = Profile_View_Activity.myprofileDetailsList.get(0).get(SPUtils.USER_Relation_Prefix);

//            if (G.equalsIgnoreCase("male"))
//                rb_male.setChecked(true);
//            else if (G.equalsIgnoreCase("female"))
//                rb_female.setChecked(true);
//            else
//                rb_male.setChecked(true);

            edtxt_memberName.setClickable(false);
//          edtxt_memberName.setEnabled(false);
            edtxt_memberName.setFocusable(false);
            edtxt_memberName.setFocusableInTouchMode(false);
            edtxt_memberName.setCursorVisible(false);

            edtxt_mobileNumber.setClickable(false);
            edtxt_mobileNumber.setFocusable(false);
            edtxt_mobileNumber.setFocusableInTouchMode(false);
            edtxt_mobileNumber.setCursorVisible(false);

         /*   if (!edtxt_bankIfsc.getText().toString().isEmpty()) {
                edtxt_bankIfsc.setClickable(false);
                edtxt_bankIfsc.setFocusable(false);
                edtxt_bankIfsc.setFocusableInTouchMode(false);
                edtxt_bankIfsc.setCursorVisible(false);
            }

            if (!edtxt_address.getText().toString().isEmpty()) {
                edtxt_address.setClickable(false);
                edtxt_address.setFocusable(false);
                edtxt_address.setFocusableInTouchMode(false);
                edtxt_address.setCursorVisible(false);
            }

            if (!edtxt_bankBranch.getText().toString().isEmpty()) {
                edtxt_bankBranch.setClickable(false);
                edtxt_bankBranch.setFocusable(false);
                edtxt_bankBranch.setFocusableInTouchMode(false);
                edtxt_bankBranch.setCursorVisible(false);
            }

            if (!edtxt_bankAcntNumber.getText().toString().isEmpty()) {
                edtxt_bankAcntNumber.setClickable(false);
                edtxt_bankAcntNumber.setFocusable(false);
                edtxt_bankAcntNumber.setFocusableInTouchMode(false);
                edtxt_bankAcntNumber.setCursorVisible(false);
            }

            if (!edtxt_aadhaarNumber.getText().toString().isEmpty()) {
                edtxt_aadhaarNumber.setClickable(false);
                edtxt_aadhaarNumber.setFocusable(false);
                edtxt_aadhaarNumber.setFocusableInTouchMode(false);
                edtxt_aadhaarNumber.setCursorVisible(false);
            }

            if (!txt_bankname.getText().toString().isEmpty() && !txt_bankname.getText().toString().equalsIgnoreCase("-- No Bank Found --")) {
                txt_bankname.setClickable(false);
                txt_bankname.setFocusable(false);
                txt_bankname.setFocusableInTouchMode(false);
                txt_bankname.setCursorVisible(false);
            }
*/
            String bytecode = AppController.getSpUserInfo().getString(SPUtils.USER_profile_pic_byte_code, "");
            if (bytecode.length() > 0) {
                iv_Profile_Pic.setImageBitmap(AppUtils.getBitmapFromString(AppController.getSpUserInfo().getString(SPUtils.USER_profile_pic_byte_code, "")));
            }

        } catch (Exception e) {
            e.printStackTrace();
            AppUtils.showExceptionDialog(Profile_Update_Activity.this);
        }
    }

    private void executeUpdateprofileRequest(final List<NameValuePair> postParameters) {
        try {
            if (AppUtils.isNetworkAvailable(Profile_Update_Activity.this)) {
                new AsyncTask<Void, Void, String>() {
                    protected void onPreExecute() {
                        AppUtils.showProgressDialog(Profile_Update_Activity.this);
                    }

                    @Override
                    protected String doInBackground(Void... params) {
                        String response = "";
                        try {
                            response = AppUtils.callWebServiceWithMultiParam(Profile_Update_Activity.this, postParameters, QueryUtils.methodToUpdateUserProfile, TAG);
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

                                    AppUtils.alertDialogWithFinish(Profile_Update_Activity.this, "" + jsonObject.getString("Message"));
                                    getProfileInfo(jsonArrayData);
                                } else {
                                    AppUtils.alertDialog(Profile_Update_Activity.this, jsonObject.getString("Message"));
                                }
                            } else {
                                AppUtils.alertDialog(Profile_Update_Activity.this, jsonObject.getString("Message"));
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            AppUtils.showExceptionDialog(Profile_Update_Activity.this);
                        }
                    }
                }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            }
        } catch (Exception e) {
            e.printStackTrace();
            AppUtils.showExceptionDialog(Profile_Update_Activity.this);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        try {

            if (resultCode == Activity.RESULT_OK) {
                if (requestCode == RESULT_GALLERY) {
                    if (data != null) {
                        imageUri = data.getData();
                        String filepath = AppUtils.getPath(data.getData(), Profile_Update_Activity.this);

                        if (filepath.length() > 0) {
                            selectedImagePath = filepath;
                            pickImageFromGallery();
                        }
                    }
                } else if (requestCode == CAMERA_REQUEST) {

                    Uri selectedImageUri = imageUri;
                    selectedImagePath = selectedImageUri.getPath();
                    pickImageFromGallery();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            AppUtils.showExceptionDialog(Profile_Update_Activity.this);
        }
    }


    private void pickImageFromGallery() {
        try {


            Matrix matrix = new Matrix();
            int rotate = 0;

            File imageFile = new File(selectedImagePath);
            long fileSizeInBytes = imageFile.length();
            long fileSizeInKB = fileSizeInBytes / 1024;
            Log.e(TAG, "Image Size(KB) before compress : " + fileSizeInKB);

            if (fileSizeInKB <= 5130) {
                try {
                    ExifInterface exif = new ExifInterface((imageFile.getName()));
                    int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);

                    switch (orientation) {
                        case ExifInterface.ORIENTATION_ROTATE_270:
                            rotate -= 90;
                            break;
                        case ExifInterface.ORIENTATION_ROTATE_180:
                            rotate -= 90;
                            break;
                        case ExifInterface.ORIENTATION_ROTATE_90:
                            rotate -= 90;
                            break;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

                int nh = 0;
                if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    Bitmap bitmapImage = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imageUri);

                    nh = (int) (bitmapImage.getHeight() * (550.0 / bitmapImage.getWidth()));

                    matrix.postRotate(rotate);
                    bitmap = Bitmap.createBitmap(bitmapImage, 0, 0, bitmapImage.getWidth(), bitmapImage.getHeight(), matrix, true);
                    bitmapImage = null;
                } else {

                    File compressedImageFile = new Compressor(this).compressToFile(imageFile);
                    Log.e(TAG, "Image Size(KB) after compress one: " + compressedImageFile.length() / 1024);
                    Bitmap compressedImageBitmap = new Compressor(this).compressToBitmap(compressedImageFile);

                    nh = (int) (compressedImageBitmap.getHeight() * (480.0 / compressedImageBitmap.getWidth()));

                    matrix.postRotate(rotate);
                    bitmap = Bitmap.createBitmap(compressedImageBitmap, 0, 0, compressedImageBitmap.getWidth(), compressedImageBitmap.getHeight(), matrix, true);
                    compressedImageBitmap = null;
                }

                if (bitmap.getWidth() > 500) {
                    Bitmap scaled = Bitmap.createScaledBitmap(bitmap, 480, nh, true);
                    bitmap = scaled;
                    scaled = null;
                }

                showUploadImageDailog(bitmap);

            } else
                AppUtils.alertDialog(this, "Selected file exceed the allowable file size limit (5 MB)");

        } catch (Exception e) {
            e.printStackTrace();
            AppUtils.showExceptionDialog(Profile_Update_Activity.this);
        }
    }

    private void showUploadImageDailog(final Bitmap imageBitmap) {
        try {
            final Dialog dialog = new Dialog(Profile_Update_Activity.this, R.style.ThemeDialogCustom);
            dialog.getWindow().getAttributes().windowAnimations = R.style.dialog_animation;
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setCancelable(false);
            dialog.setContentView(R.layout.custom_dialog_img_upload);

            TextView dialog4all_txt = dialog.findViewById(R.id.txt_DialogTitle);
            dialog4all_txt.setText("Are you sure you want to upload this image as Profile Picture ?");

            final ImageView imgView_Upload = dialog.findViewById(R.id.imgView_Upload);
            imgView_Upload.setImageBitmap(imageBitmap);

            TextView txt_submit = dialog.findViewById(R.id.txt_submit);
            txt_submit.setText("Upload");
            txt_submit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    AppUtils.hideKeyboardOnClick(Profile_Update_Activity.this, v);
                    dialog.dismiss();
                    executePostProfilePictureUploadRequest(imageBitmap);
                    iv_Profile_Pic.setImageBitmap(imageBitmap);
                }
            });

            TextView txt_cancel = dialog.findViewById(R.id.txt_cancel);
            txt_cancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                }
            });

            dialog.show();
        } catch (Exception e) {
            e.printStackTrace();
            AppUtils.showExceptionDialog(Profile_Update_Activity.this);
        }
    }

    private void executePostProfilePictureUploadRequest(final Bitmap bitmap) {
        try {
            if (AppUtils.isNetworkAvailable(Profile_Update_Activity.this)) {
                new AsyncTask<Void, Void, String>() {
                    protected void onPreExecute() {
                        AppUtils.showProgressDialog(Profile_Update_Activity.this);
                    }

                    @Override
                    protected String doInBackground(Void... params) {
                        String response = "";
                        try {
                            String deviceId = ((TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE)).getDeviceId();
                            List<NameValuePair> postParameters = new ArrayList<>();
                            postParameters.add(new BasicNameValuePair("IDNo", AppController.getSpUserInfo().getString(SPUtils.USER_ID_NUMBER, "")));
                            postParameters.add(new BasicNameValuePair("FormNo", AppController.getSpUserInfo().getString(SPUtils.USER_FORM_NUMBER, "")));
                            postParameters.add(new BasicNameValuePair("Type", "PP"));
                            postParameters.add(new BasicNameValuePair("ImageByteCode", AppUtils.getBase64StringFromBitmap(bitmap)));

                            try {
                                postParameters.add(new BasicNameValuePair("IPAddress", deviceId));
                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                            response = AppUtils.callWebServiceWithMultiParam(Profile_Update_Activity.this, postParameters, QueryUtils.methodUploadImages, TAG);
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
                                if (!jsonArrayData.getJSONObject(0).getString("PhotoProof").equals("")) {
                                    iv_Profile_Pic.setImageBitmap(AppUtils.getBitmapFromString(jsonArrayData.getJSONObject(0).getString("PhotoProof")));
                                    Cache.getInstance().getLru().put("profileImage", AppUtils.getBitmapFromString(jsonArrayData.getJSONObject(0).getString("PhotoProof")));
                                    AppController.getSpUserInfo().edit().putString(SPUtils.USER_profile_pic_byte_code, (jsonArrayData.getJSONObject(0).getString("PhotoProof"))).commit();
                                }
                            } else {
                                AppUtils.alertDialog(Profile_Update_Activity.this, jsonObject.getString("Message"));
                                if (AppUtils.showLogs)
                                    Log.v(TAG, "executeGetKYCUploadRequest executed...Failed... called");
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            AppUtils.showExceptionDialog(Profile_Update_Activity.this);
                        }
                    }
                }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            }
        } catch (Exception e) {
            e.printStackTrace();
            AppUtils.showExceptionDialog(Profile_Update_Activity.this);
        }
    }

    private void showMemRelationDialog() {
        try {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Select Member Relation");
            builder.setItems(selectRelationArray, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int item) {
                    // Do something with the selection
                    txt_prefix.setText(selectRelationArray[item]);
                }
            });
            builder.create().show();
        } catch (Exception e) {
            e.printStackTrace();
            AppUtils.showExceptionDialog(Profile_Update_Activity.this);
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
            AppUtils.showExceptionDialog(Profile_Update_Activity.this);
        }

        System.gc();
    }

    private void enableExpandableList() {

        listDataHeader = new ArrayList<>();
        listDataChild = new HashMap<>();

        if (AppController.getSpIsLogin().getBoolean(SPUtils.IS_LOGIN, false)) {
            if (HeadingJarray != null && HeadingJarray.length() > 0)
                prepareListDataDistributor(listDataHeader, listDataChild, HeadingJarray);
            else
                executeTogetDrawerMenuItems();
        }

        ExpandableListAdapter listAdapter = new ExpandableListAdapter(this, listDataHeader, listDataChild);
        expListView.setAdapter(listAdapter);

        expListView.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {

            @Override
            public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id) {

                String GroupTitle = listDataHeader.get(groupPosition);

                if (GroupTitle.trim().equalsIgnoreCase(getResources().getString(R.string.dashboard))) {
                    startActivity(new Intent(Profile_Update_Activity.this, DashBoard_Activity.class));
                    if (drawer.isDrawerOpen(GravityCompat.START)) {
                        drawer.closeDrawer(GravityCompat.START);
                    }
                } else if (GroupTitle.trim().equalsIgnoreCase("Logout")) {
                    AppUtils.showDialogSignOut(Profile_Update_Activity.this);
                } else if (GroupTitle.trim().equalsIgnoreCase("New Joining")) {
                    startActivity(new Intent(Profile_Update_Activity.this, Register_Activity.class));
                    if (drawer.isDrawerOpen(GravityCompat.START)) {
                        drawer.closeDrawer(GravityCompat.START);
                    }
                }
                return false;
            }
        });

        expListView.setOnGroupExpandListener(new ExpandableListView.OnGroupExpandListener() {

            @Override
            public void onGroupExpand(int groupPosition) {

                if (lastExpandedPosition != -1
                        && groupPosition != lastExpandedPosition) {
                    expListView.collapseGroup(lastExpandedPosition);
                }
                lastExpandedPosition = groupPosition;
            }

        });

        expListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {

            @Override
            public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
                String ChildItemTitle = listDataChild.get(listDataHeader.get(groupPosition)).get(childPosition);

                if (ChildItemTitle.trim().equalsIgnoreCase(getResources().getString(R.string.view_profile))) {
                    startActivity(new Intent(Profile_Update_Activity.this, Profile_View_Activity.class));
                } else if (ChildItemTitle.trim().equalsIgnoreCase(getResources().getString(R.string.change_password))) {
                    startActivity(new Intent(Profile_Update_Activity.this, Change_Password_Activity.class));
                } else if (ChildItemTitle.trim().equalsIgnoreCase(getResources().getString(R.string.new_joining))) {
                    startActivity(new Intent(Profile_Update_Activity.this, Register_Activity.class));
                } else if (ChildItemTitle.trim().equalsIgnoreCase(getResources().getString(R.string.upload_kyc))) {
                    startActivity(new Intent(Profile_Update_Activity.this, KYCUploadDocument_Activity.class).putExtra("HEADING", "Update"));
                } else if (ChildItemTitle.trim().equalsIgnoreCase(getResources().getString(R.string.generation_structure))) {
                    startActivity(new Intent(Profile_Update_Activity.this, Sponsor_genealogy_Activity.class));
                } else if (ChildItemTitle.trim().equalsIgnoreCase(getResources().getString(R.string.sponsor_downline))) {
                    startActivity(new Intent(Profile_Update_Activity.this, Sponsor_team_details_Activity.class).putExtra("Action", "Sponsor"));
                } else if (ChildItemTitle.trim().equalsIgnoreCase(getResources().getString(R.string.bv_detail_report))) {
                    startActivity(new Intent(Profile_Update_Activity.this, Sponsor_team_details_Activity.class).putExtra("Action", "Direct"));
                } else if (ChildItemTitle.trim().equalsIgnoreCase(getResources().getString(R.string.welcome_letter))) {
                    startActivity(new Intent(Profile_Update_Activity.this, WelcomeLetter_Activity.class));
                } else if (ChildItemTitle.trim().equalsIgnoreCase(getResources().getString(R.string.purchase_reports))) {
                    startActivity(new Intent(Profile_Update_Activity.this, Repurchase_BV_Detail.class));
                } else if (ChildItemTitle.trim().equalsIgnoreCase("Team Repurchase BV Summary")) {
                    startActivity(new Intent(Profile_Update_Activity.this, Repurchase_BV_Summary_Team_Activity.class));
                } else if (ChildItemTitle.trim().equalsIgnoreCase("Repurchase Bill Summary")) {
                    startActivity(new Intent(Profile_Update_Activity.this, Repurchase_Bill_Summary.class));
                } else if (ChildItemTitle.trim().equalsIgnoreCase(getResources().getString(R.string.wallet_detail))) {
                    startActivity(new Intent(Profile_Update_Activity.this, Wallet_Transaction_Report_Activity.class));
                } else if (ChildItemTitle.trim().equalsIgnoreCase("Wallet Request Report")) {
                    startActivity(new Intent(Profile_Update_Activity.this, Wallet_Request_Status_Report_Activity.class));
                } else if (ChildItemTitle.trim().equalsIgnoreCase("Request For Wallet Amount")) {
                    startActivity(new Intent(Profile_Update_Activity.this, Wallet_Request_Amount_Activity.class));
                } else if (ChildItemTitle.trim().equalsIgnoreCase(getResources().getString(R.string.monthly_incentive))) {
                    startActivity(new Intent(Profile_Update_Activity.this, Monthly_Incentive_Activity.class));
                } else if (ChildItemTitle.trim().equalsIgnoreCase(getResources().getString(R.string.monthly_incentive_detail_report))) {
                    startActivity(new Intent(Profile_Update_Activity.this, Monthly_Incentive_Detail_Activity.class));
                } else if (ChildItemTitle.trim().equalsIgnoreCase("TDS Detail Report")) {
                    startActivity(new Intent(Profile_Update_Activity.this, TDS_detail_report_Activity.class));
                } else if (ChildItemTitle.trim().equalsIgnoreCase("Update Profile")) {
                    startActivity(new Intent(Profile_Update_Activity.this, Profile_Update_Activity.class));
                } else if (ChildItemTitle.trim().equalsIgnoreCase("View KYC Documents")) {
                    startActivity(new Intent(Profile_Update_Activity.this, KYCUploadDocument_Activity.class).putExtra("HEADING", "View"));
                } else if (ChildItemTitle.trim().equalsIgnoreCase("Register Enquiry/Complaint")) {
                    startActivity(new Intent(Profile_Update_Activity.this, Register_Complaint_Activity.class));
                } else if (ChildItemTitle.trim().equalsIgnoreCase("View Open/Pending Queries")) {
                    startActivity(new Intent(Profile_Update_Activity.this, ViewPendingQueries_Activity.class).putExtra("HEADING", "Open"));
                } else if (ChildItemTitle.trim().equalsIgnoreCase("View Closed Queries")) {
                    startActivity(new Intent(Profile_Update_Activity.this, ViewPendingQueries_Activity.class).putExtra("HEADING", "Closed"));
                } else if (ChildItemTitle.trim().equalsIgnoreCase("Product Wallet Detail")) {
                    startActivity(new Intent(Profile_Update_Activity.this, ProductWallet_Transaction_Report_Activity.class));
                } else if (ChildItemTitle.trim().equalsIgnoreCase("Wallet To Bank Transfer Detail")) {
                    startActivity(new Intent(Profile_Update_Activity.this, Wallet_Bank_Transfer_Report_Activity.class));
                } else if (ChildItemTitle.trim().equalsIgnoreCase("ID Card")) {
                    startActivity(new Intent(Profile_Update_Activity.this, ID_card_Activity.class));
                }


                if (drawer.isDrawerOpen(GravityCompat.START)) {
                    drawer.closeDrawer(GravityCompat.START);
                }
                return false;
            }
        });
    }

    private void LoadNavigationHeaderItems() {
        txt_id_number.setText("");
        txt_id_number.setVisibility(View.GONE);

        txt_available_wb.setText("");
        txt_available_wb.setVisibility(View.GONE);

        txt_welcome_name.setText("Guest");

        if (AppController.getSpIsLogin().getBoolean(SPUtils.IS_LOGIN, false)) {
            String welcome_text = WordUtils.capitalizeFully(AppController.getSpUserInfo().getString(SPUtils.USER_FIRST_NAME, ""));
            txt_welcome_name.setText(welcome_text);

            Bitmap largeIcon = BitmapFactory.decodeResource(getResources(), R.drawable.ic_icon_user);
            profileImage.setImageBitmap(largeIcon);

            String userid = AppController.getSpUserInfo().getString(SPUtils.USER_ID_NUMBER, "");
            txt_id_number.setText(userid);
            txt_id_number.setVisibility(View.VISIBLE);

            executeWalletBalanceRequest();

            String bytecode = AppController.getSpUserInfo().getString(SPUtils.USER_profile_pic_byte_code, "");

            if (bytecode.equalsIgnoreCase(""))
                executeGetProfilePicture();
            else
                profileImage.setImageBitmap(AppUtils.getBitmapFromString(bytecode));
        }
    }

    private void executeWalletBalanceRequest() {
        try {
            if (AppUtils.isNetworkAvailable(Profile_Update_Activity.this)) {
                new AsyncTask<Void, Void, String>() {
                    @Override
                    protected String doInBackground(Void... params) {
                        String response = "";
                        try {
                            List<NameValuePair> postParameters = new ArrayList<>();
                            postParameters.add(new BasicNameValuePair("Formno", AppController.getSpUserInfo().getString(SPUtils.USER_FORM_NUMBER, "")));
                            response = AppUtils.callWebServiceWithMultiParam(Profile_Update_Activity.this,
                                    postParameters, QueryUtils.methodToGetWalletBalance, TAG);

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        return response;
                    }

                    @Override
                    protected void onPostExecute(String resultData) {
                        try {
                            JSONObject jsonObject = new JSONObject(resultData);
                            JSONArray jsonArrayData = jsonObject.getJSONArray("Data");

                            if (jsonObject.getString("Status").equalsIgnoreCase("True")) {
                                if (jsonObject.getString("Message").equalsIgnoreCase("Successfully.!")) {
                                    String count_text = "Wallet Balance : \u20B9 " + jsonArrayData.getJSONObject(0).getString("WBalance");
                                    txt_available_wb.setText(count_text);
                                    txt_available_wb.setVisibility(View.VISIBLE);
                                }
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void prepareListDataDistributor(List<String> listDataHeader, Map<String, List<String>> listDataChild, JSONArray HeadingJarray) {

        List<String> Empty = new ArrayList<>();
        try {
            ArrayList<String> MenuAl = new ArrayList<>();
            for (int i = 0; i < HeadingJarray.length(); i++) {
                if (HeadingJarray.getJSONObject(i).getInt("ParentId") == 0)
                    MenuAl.add(HeadingJarray.getJSONObject(i).getString("MenuName"));
            }

            for (int aa = 0; aa < MenuAl.size(); aa++) {
                ArrayList<String> SubMenuAl = new ArrayList<>();

                for (int bb = 0; bb < HeadingJarray.length(); bb++) {
                    if (HeadingJarray.getJSONObject(aa).getInt("MenuId") == HeadingJarray.getJSONObject(bb).getInt("ParentId")) {
                        SubMenuAl.add(AppUtils.CapsFirstLetterString(HeadingJarray.getJSONObject(bb).getString("MenuName")));
                    }
                }
                listDataHeader.add(AppUtils.CapsFirstLetterString(MenuAl.get(aa)));
                listDataChild.put(listDataHeader.get(aa), SubMenuAl);
            }
            listDataHeader.add("Logout");
            listDataChild.put(listDataHeader.get(listDataHeader.size() - 1), Empty);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void executeTogetDrawerMenuItems() {
        new AsyncTask<Void, Void, String>() {

            @Override
            protected void onPreExecute() {
                AppUtils.showProgressDialog(Profile_Update_Activity.this);
            }

            @Override
            protected String doInBackground(Void... params) {

                Thread.currentThread().setPriority(Thread.MAX_PRIORITY);

                String response = "";
                try {
                    List<NameValuePair> postParameters = new ArrayList<>();
                    response = AppUtils.callWebServiceWithMultiParam(Profile_Update_Activity.this, postParameters, QueryUtils.methodtoGetDrawerMenuItems, TAG);

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
                        HeadingJarray = jsonObject.getJSONArray("Data");
                        prepareListDataDistributor(listDataHeader, listDataChild, HeadingJarray);
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    private void executeGetProfilePicture() {
        try {
            if (AppUtils.isNetworkAvailable(Profile_Update_Activity.this)) {
                new AsyncTask<Void, Void, String>() {
                    @Override
                    protected String doInBackground(Void... params) {
                        String response = "";
                        try {
                            List<NameValuePair> postParameters = new ArrayList<>();
                            postParameters.add(new BasicNameValuePair("IDNo", AppController.getSpUserInfo().getString(SPUtils.USER_ID_NUMBER, "")));
                            //ImageType----AddrProof=AP,IdentityProof=IP,PhotoProof=PP,Signature=S,CancelChq=CC,SpousePic=SP,All=*
                            postParameters.add(new BasicNameValuePair("ImageType", "PP"));

                            response = AppUtils.callWebServiceWithMultiParam(Profile_Update_Activity.this, postParameters, QueryUtils.methodGetImages, TAG);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        return response;
                    }

                    @Override
                    protected void onPostExecute(String resultData) {
                        try {
                            JSONObject jsonObject = new JSONObject(resultData);
                            JSONArray jsonArrayData = jsonObject.getJSONArray("Data");

                            if (jsonObject.getString("Status").equalsIgnoreCase("True")) {
                                if (!jsonArrayData.getJSONObject(0).getString("PhotoProof").equals("")) {

                                    AppController.getSpUserInfo().edit().putString(SPUtils.USER_profile_pic_byte_code, jsonArrayData.getJSONObject(0).getString("PhotoProof")).commit();
                                    profileImage.setImageBitmap(AppUtils.getBitmapFromString(jsonArrayData.getJSONObject(0).getString("PhotoProof")));
                                }
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            AppUtils.showExceptionDialog(Profile_Update_Activity.this);
                        }
                    }
                }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            }
        } catch (Exception e) {
            e.printStackTrace();
            AppUtils.showExceptionDialog(Profile_Update_Activity.this);
        }
    }
    /*******************OTP System**************************/
    private void executeSendOtpForUpdateProfileRequest() {
        try {
            if (AppUtils.isNetworkAvailable(Profile_Update_Activity.this)) {
                new AsyncTask<Void, Void, String>() {


                    @Override
                    protected void onPreExecute() {
                        super.onPreExecute();
                        AppUtils.showProgressDialog(Profile_Update_Activity.this);
                    }


                    @Override
                    protected String doInBackground(Void... params) {
                        String response = "";
                        try {
                            List<NameValuePair> postParameters = new ArrayList<>();
                            postParameters.add(new BasicNameValuePair("MobileNo", "" + AppController.getSpUserInfo().getString(SPUtils.USER_MOBILE_NO, "")));
                            response = AppUtils.callWebServiceWithMultiParam(Profile_Update_Activity.this,
                                    postParameters, QueryUtils.methodToSendOTPOnUpdateProfile, TAG);

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
                                JSONArray jsonArray = jsonObject.getJSONArray("Data");
                                btn_updateProfilesendotp.setVisibility(View.GONE);
                                ll_update_profile_enter_otp.setVisibility(View.VISIBLE);
                                ll_update_profile_data.setVisibility(View.GONE);
                               // ed_otp.setVisibility(View.VISIBLE);

                                recieve_otp = jsonArray.getJSONObject(0).getString("OTP");
                                AppUtils.alertDialog(Profile_Update_Activity.this, "OTP Send Succefully on your number");
                            } else {
                                AppUtils.alertDialog(Profile_Update_Activity.this, jsonObject.getString("Message"));
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            AppUtils.showExceptionDialog(Profile_Update_Activity.this);
                        }
                    }
                }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            }
        } catch (Exception e) {
            e.printStackTrace();
            AppUtils.showExceptionDialog(Profile_Update_Activity.this);
        }
    }
    public void ValidateData() {

        String Otp = ed_otp.getText().toString();
        if (TextUtils.isEmpty(Otp)) {
            ed_otp.setError("OTP is Required");
            ed_otp.requestFocus();
        } else if (!recieve_otp.equalsIgnoreCase(Otp)) {
            ed_otp.setError("Invalid OTP");
            ed_otp.requestFocus();
            //   tv_resend.setVisibility(View.VISIBLE);
        } else {
            if (AppUtils.isNetworkAvailable(this)){
                startUpdateProfile();
            } else {
                AppUtils.alertDialog(this, getResources().getString(R.string.txt_networkAlert));
            }
        }
    }

    /*Code added by 01-12-2018 06-19 Pm*/

    private void executeStateRequestone() {
        new AsyncTask<Void, Void, String>() {
            protected void onPreExecute() {
                AppUtils.showProgressDialog(Profile_Update_Activity.this);
            }

            @Override
            protected String doInBackground(Void... params) {
                String response = "";
                try {
                    List<NameValuePair> postParameters = new ArrayList<>();
                    response = AppUtils.callWebServiceWithMultiParam(Profile_Update_Activity.this, postParameters, QueryUtils.methodMaster_FillState, TAG);
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
                            AppUtils.alertDialog(Profile_Update_Activity.this, jsonObject.getString("Message"));
                        }
                    } else {
                        AppUtils.alertDialog(Profile_Update_Activity.this, jsonObject.getString("Message"));
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

    private void executeBankRequestone() {
        new AsyncTask<Void, Void, String>() {
            protected void onPreExecute() {
                AppUtils.showProgressDialog(Profile_Update_Activity.this);
            }

            @Override
            protected String doInBackground(Void... params) {
                String response = "";
                try {
                    List<NameValuePair> postParameters = new ArrayList<>();
                    response = AppUtils.callWebServiceWithMultiParam(Profile_Update_Activity.this, postParameters, QueryUtils.methodMaster_FillBank, TAG);
                } catch (Exception ignored) {
                }
                return response;
            }

            @Override
            protected void onPostExecute(String resultData) {
                try {
                    JSONObject jsonObject = new JSONObject(resultData);
                    JSONArray jsonArrayData = jsonObject.getJSONArray("Data");

                    if (jsonObject.getString("Status").equalsIgnoreCase("True")) {
                        if (jsonArrayData.length() != 0) {
                            getBankResultone(jsonArrayData);
                        } else {
                            AppUtils.alertDialog(Profile_Update_Activity.this, jsonObject.getString("Message"));
                        }
                    } else {
                        AppUtils.alertDialog(Profile_Update_Activity.this, jsonObject.getString("Message"));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    private void getBankResultone(JSONArray jsonArray) {
        try {
            AppController.bankList.clear();

            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                HashMap<String, String> map = new HashMap<>();

                map.put("BID", jsonObject.getString("BID"));
                map.put("Bank", WordUtils.capitalizeFully(jsonObject.getString("Bank")));

                AppController.bankList.add(map);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}