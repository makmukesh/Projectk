package com.vpipl.kalpamrit;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.GestureDetector;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.ViewFlipper;

import com.vpipl.kalpamrit.Utils.AppUtils;
import com.vpipl.kalpamrit.Utils.QueryUtils;
import com.vpipl.kalpamrit.Utils.SPUtils;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import static android.view.View.GONE;

public class Login_Checkout_Activity extends AppCompatActivity {
    private static final String TAG = "Login_Checkout_Activity";

    private TextView txt_login;
    private TextView txt_memberlogin;
    private View view_login;
    private View view_memberlogin;

    private ViewFlipper viewFlipper;
    private LinearLayout layout_login;
    private LinearLayout layout_memberlogin;

    private String LoginType = "Member";

    private Button button_login;
    private Button btn_new_registration;
    private TextView txt_forgot_password;
    private CheckBox cb_login_rememberMe;

    private TextInputEditText edtxt_userid, edtxt_password, edtxt_userid_member, edtxt_password_member;

    private Animation animFlipInForeward;
    private Animation animFlipOutForeward;
    private Animation animFlipInBackward;
    private Animation animFlipOutBackward;

    private boolean SendToHome = false;

    ImageView img_nav_back, img_login_logout;

    public void SetupToolbar() {

        img_nav_back = findViewById(R.id.img_nav_back);
        img_login_logout = findViewById(R.id.img_login_logout);

        img_nav_back.setVisibility(GONE);

        img_login_logout.setVisibility(GONE);

    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
        SetupToolbar();

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        animFlipInForeward = AnimationUtils.loadAnimation(this, R.anim.flipin);
        animFlipOutForeward = AnimationUtils.loadAnimation(this, R.anim.flipout);
        animFlipInBackward = AnimationUtils.loadAnimation(this, R.anim.flipin_reverse);
        animFlipOutBackward = AnimationUtils.loadAnimation(this, R.anim.flipout_reverse);

        layout_login = findViewById(R.id.layout_login);
        layout_memberlogin = findViewById(R.id.layout_memberlogin);

        txt_login = findViewById(R.id.txt_login);
        txt_memberlogin = findViewById(R.id.txt_memberlogin);

        view_login = findViewById(R.id.view_login);
        view_memberlogin = findViewById(R.id.view_memberlogin);

        txt_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                viewFlipper.setDisplayedChild(1);
                setSelectedTab();
            }
        });

        txt_memberlogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                viewFlipper.setDisplayedChild(0);
                setSelectedTab();
            }
        });

        final GestureDetector gdt = new GestureDetector(new Login_Checkout_Activity.GestureListener());

        viewFlipper = findViewById(R.id.viewFlipper);
        viewFlipper.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(final View view, final MotionEvent event) {
                gdt.onTouchEvent(event);
                return true;
            }
        });

        findViewById(R.id.LLV).setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {

                gdt.onTouchEvent(motionEvent);
                return true;
            }
        });


        edtxt_userid = findViewById(R.id.edtxt_userid);
        edtxt_userid_member = findViewById(R.id.edtxt_userid_member);
        edtxt_password = findViewById(R.id.edtxt_password);
        edtxt_password_member = findViewById(R.id.edtxt_password_member);

        button_login = findViewById(R.id.button_login);

        cb_login_rememberMe = findViewById(R.id.cb_login_rememberMe);
        txt_forgot_password = findViewById(R.id.txt_forgot_password);

        btn_new_registration = findViewById(R.id.btn_new_registration);
        btn_new_registration.setVisibility(GONE);
        findViewById(R.id.LLor).setVisibility(GONE);

        if (AppController.getSpRememberUserInfo().getBoolean(SPUtils.IS_REMEMBER_User, false)) {
            cb_login_rememberMe.setChecked(true);

            String useridmember = AppController.getSpRememberUserInfo().getString(SPUtils.IS_REMEMBER_ID_Member, "");
            if (useridmember.contains(";")) {
                String[] parts = useridmember.split(";");
                String part1 = "";
                String part2 = "";

                if (parts.length > 0)
                    part1 = parts[0];

                if (parts.length > 1)
                    part2 = parts[1];

                edtxt_userid.setText(part2);
                edtxt_userid_member.setText(part1);
            }
        }

        edtxt_password.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == R.id.login || id == EditorInfo.IME_NULL) {
                    ValidateData();
                    return true;
                }
                return false;
            }
        });

        edtxt_password_member.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == R.id.login || id == EditorInfo.IME_NULL) {
                    ValidateData();
                    return true;
                }
                return false;
            }
        });

        button_login.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                AppUtils.hideKeyboardOnClick(Login_Checkout_Activity.this, view);
                ValidateData();
            }
        });

//        btn_new_registration.setOnClickListener(new OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                AppUtils.hideKeyboardOnClick(Login_Checkout_Activity.this, v);
//                if (btn_new_registration.getText().toString().equalsIgnoreCase("Sign Up"))
//                    MovetoNext(new Intent(Login_Checkout_Activity.this, Register_User_Activity.class));
//                else
//                    MovetoNext(new Intent(Login_Checkout_Activity.this, Register_Activity.class));
//            }
//        });

        txt_forgot_password.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                AppUtils.hideKeyboardOnClick(Login_Checkout_Activity.this, v);
                MovetoNext(new Intent(Login_Checkout_Activity.this, Forget_Password_Activity.class).putExtra("LoginType", LoginType));
            }
        });

        if (getIntent().getExtras() != null)
            SendToHome = getIntent().getBooleanExtra("SendToHome", false);

        if(AppController.selectedProductsListUpdated.size() > 0){
            AppController.selectedProductsList = AppController.selectedProductsListUpdated ;
        }
    }

    private void SwipeRight() {
        viewFlipper.setInAnimation(animFlipInBackward);
        viewFlipper.setOutAnimation(animFlipOutBackward);
        viewFlipper.showPrevious();
        setSelectedTab();
    }

    private void SwipeLeft() {
        viewFlipper.setInAnimation(animFlipInForeward);
        viewFlipper.setOutAnimation(animFlipOutForeward);
        viewFlipper.showNext();
        setSelectedTab();
    }

    private void setSelectedTab() {
        try {
            if (viewFlipper.getDisplayedChild() == 1) {
                txt_login.setTextColor(getResources().getColor(android.R.color.black));
                txt_memberlogin.setTextColor(getResources().getColor(android.R.color.darker_gray));

                view_login.setBackgroundColor(getResources().getColor(R.color.color_orange_text));
                view_memberlogin.setBackgroundColor(getResources().getColor(R.color.color_f4f4f4));
                btn_new_registration.setText("Sign Up");
                LoginType = "User";
            } else if (viewFlipper.getDisplayedChild() == 0) {
                txt_login.setTextColor(getResources().getColor(android.R.color.darker_gray));
                txt_memberlogin.setTextColor(getResources().getColor(android.R.color.black));

                view_login.setBackgroundColor(getResources().getColor(R.color.color_f4f4f4));
                view_memberlogin.setBackgroundColor(getResources().getColor(R.color.color_orange_text));

                btn_new_registration.setText("Join Us");
                LoginType = "Member";
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void ValidateData() {
        edtxt_userid.setError(null);
        edtxt_password.setError(null);

        String userid;
        String password;

        if (LoginType.equalsIgnoreCase("User")) {
            userid = edtxt_userid.getText().toString().trim();
            password = edtxt_password.getText().toString().trim();
        } else {
            userid = edtxt_userid_member.getText().toString().trim();
            password = edtxt_password_member.getText().toString().trim();
        }

        if (TextUtils.isEmpty(userid)) {
            if (LoginType.equalsIgnoreCase("User")) {
                AppUtils.alertDialog(Login_Checkout_Activity.this, "Mobile Number or Email is Required");
                edtxt_userid.requestFocus();
            } else {
                AppUtils.alertDialog(Login_Checkout_Activity.this, getResources().getString(R.string.error_required_user_id));
                edtxt_userid_member.requestFocus();
            }
        } else {
//            if ((userid.trim()).length() != 10)
//            {
//                if (LoginType.equalsIgnoreCase("Member")) {
//                    AppUtils.alertDialog(Login_Checkout_Activity.this, getResources().getString(R.string.error_invalid_user_id));
//                    edtxt_userid_member.requestFocus();
//                } else {
//                    if (AppUtils.isValidMail(userid)) {
//                        AppUtils.alertDialog(Login_Checkout_Activity.this, getResources().getString(R.string.error_invalid_email));
//                        edtxt_userid.requestFocus();
//                    }
//                }
//            }
//            else {
            if (TextUtils.isEmpty(password)) {
                AppUtils.alertDialog(Login_Checkout_Activity.this, getResources().getString(R.string.error_required_password));
                if (LoginType.equalsIgnoreCase("Member"))
                    edtxt_password_member.requestFocus();
                else
                    edtxt_password.requestFocus();
            } else {
                if (AppUtils.isNetworkAvailable(Login_Checkout_Activity.this)) {

                    executeLoginRequest(userid, password);

                } else {
                    AppUtils.alertDialog(Login_Checkout_Activity.this, getResources().getString(R.string.txt_networkAlert));
                }
            }
//            }
        }
    }

    private void executeLoginRequest(final String userId, final String passwd) {
        try {

            if (AppUtils.isNetworkAvailable(Login_Checkout_Activity.this)) {
                new AsyncTask<Void, Void, String>() {
                    protected void onPreExecute() {
                        AppUtils.showProgressDialog(Login_Checkout_Activity.this);
                    }

                    @Override
                    protected String doInBackground(Void... params) {
                        String response = "";
                        try {

                            List<NameValuePair> postParameters = new ArrayList<>();
                            postParameters.add(new BasicNameValuePair("UserID", userId));
                            postParameters.add(new BasicNameValuePair("Password", passwd));

                            if (LoginType.equalsIgnoreCase("Member")) {
                                postParameters.add(new BasicNameValuePair("UserType", "D"));
                                response = AppUtils.callWebServiceWithMultiParam(Login_Checkout_Activity.this, postParameters, QueryUtils.methodMemberLoginOnPortal, TAG);

                            } else {
                                postParameters.add(new BasicNameValuePair("UserType", "N"));
                                response = AppUtils.callWebServiceWithMultiParam(Login_Checkout_Activity.this, postParameters, QueryUtils.methodUserLoginOnPortal, TAG);
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

                            JSONObject jsonObject = new JSONObject(resultData);

                            if (jsonObject.getString("Status").equalsIgnoreCase("True")) {

                                JSONArray jsonArrayData = jsonObject.getJSONArray("Data");

                                if (jsonArrayData.length() != 0) {
                                    saveLoginUserInfo(jsonArrayData);
                                } else {
                                    AppUtils.alertDialog(Login_Checkout_Activity.this, jsonObject.getString("Message"));
                                }
                            } else {
                                AppUtils.alertDialog(Login_Checkout_Activity.this, jsonObject.getString("Message"));
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            AppUtils.showExceptionDialog(Login_Checkout_Activity.this);
                        }
                    }
                }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            }
        } catch (Exception e) {
            e.printStackTrace();
            AppUtils.showExceptionDialog(Login_Checkout_Activity.this);
        }
    }

    private void saveLoginUserInfo(final JSONArray jsonArray) {
        try {
            AppUtils.dismissProgressDialog();

            if (cb_login_rememberMe.isChecked()) {
                String userid = edtxt_userid.getText().toString().trim();
                String memberuserid = edtxt_userid_member.getText().toString().trim();

                AppController.getSpRememberUserInfo().edit().putBoolean(SPUtils.IS_REMEMBER_User, true)
                        .putString(SPUtils.IS_REMEMBER_ID_Member, memberuserid + ";" + userid).commit();
            } else {
                AppController.getSpRememberUserInfo().edit().putBoolean(SPUtils.IS_REMEMBER_User, false)
                        .putString(SPUtils.IS_REMEMBER_ID_Member, " ; ").commit();
            }


            AppController.getSpUserInfo().edit().clear().commit();

            if (LoginType.equalsIgnoreCase("Member")) {
                AppController.getSpUserInfo().edit()
                        .putString(SPUtils.USER_TYPE, "DISTRIBUTOR")
                        .putString(SPUtils.USER_ID_NUMBER, jsonArray.getJSONObject(0).getString("IDNo"))
                        .putString(SPUtils.USER_PASSWORD, jsonArray.getJSONObject(0).getString("Passw"))
                        .putString(SPUtils.USER_FORM_NUMBER, jsonArray.getJSONObject(0).getString("FormNo"))
                        .putString(SPUtils.USER_FIRST_NAME, jsonArray.getJSONObject(0).getString("MemFirstName"))
                        .putString(SPUtils.USER_LAST_NAME, jsonArray.getJSONObject(0).getString("MemLastName"))
                        .putString(SPUtils.USER_MOBILE_NO, jsonArray.getJSONObject(0).getString("Mobl"))
                        .putString(SPUtils.USER_KIT_ID, jsonArray.getJSONObject(0).getString("KitID"))
                        .putString(SPUtils.USER_STATE_CODE, jsonArray.getJSONObject(0).getString("StateCode"))
                        .putString(SPUtils.USER_ACTIVE_STATUS, jsonArray.getJSONObject(0).getString("activestatus"))
                        .putString(SPUtils.USER_KIT_NAME, jsonArray.getJSONObject(0).getString("KitName"))
                        .putString(SPUtils.USER_IS_PORTAL, jsonArray.getJSONObject(0).getString("isportal"))
                        .putString(SPUtils.USER_EMAIL, jsonArray.getJSONObject(0).getString("EMail"))
                        .putString(SPUtils.USER_DOJ, AppUtils.getDateFromAPIDate(jsonArray.getJSONObject(0).getString("Doj")))
                        .commit();
            } else {

                AppController.getSpUserInfo().edit()
                        .putString(SPUtils.USER_TYPE, "CUSTOMER")
                        .putString(SPUtils.USER_DOJ, AppUtils.getDateFromAPIDate(jsonArray.getJSONObject(0).getString("Doj")))
                        .putString(SPUtils.USER_FIRST_NAME, jsonArray.getJSONObject(0).getString("MemFirstName"))
                        .putString(SPUtils.USER_LAST_NAME, jsonArray.getJSONObject(0).getString("MemLastName"))
                        .putString(SPUtils.USER_GENDER, jsonArray.getJSONObject(0).getString("Gender"))
                        .putString(SPUtils.USER_MOBILE_NO, jsonArray.getJSONObject(0).getString("Mobl"))
                        .putString(SPUtils.USER_EMAIL, jsonArray.getJSONObject(0).getString("EMail"))
                        .putString(SPUtils.USER_ACTIVE_STATUS, jsonArray.getJSONObject(0).getString("ActiveStatus"))
                        .putString(SPUtils.USER_FORM_NUMBER, jsonArray.getJSONObject(0).getString("FormNo"))
                        .putString(SPUtils.USER_PINCODE, jsonArray.getJSONObject(0).getString("PinCode"))

                        .putString(SPUtils.USER_STATE_CODE, jsonArray.getJSONObject(0).getString("StateCode"))
                        .putString(SPUtils.USER_ADDRESS, jsonArray.getJSONObject(0).getString("Address1"))
                        .putString(SPUtils.USER_ADDRESS2, jsonArray.getJSONObject(0).getString("Address2"))
                        .putString(SPUtils.USER_CITY, jsonArray.getJSONObject(0).getString("City"))
                        .commit();

            }
            AppController.getSpIsLogin().edit().putBoolean(SPUtils.IS_LOGIN, true).commit();

            MovetoNext(new Intent(Login_Checkout_Activity.this, AddCartCheckOut_Activity.class));

        } catch (Exception e) {
            e.printStackTrace();
            AppUtils.showExceptionDialog(Login_Checkout_Activity.this);
        }
    }


    private void MovetoNext(Intent intent) {
        try {
            startActivity(intent);
            finish();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private class GestureListener extends GestureDetector.SimpleOnGestureListener {
        private static final int SWIPE_MIN_DISTANCE = 120;
        private static final int SWIPE_THRESHOLD_VELOCITY = 200;

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            if (e1.getX() - e2.getX() > SWIPE_MIN_DISTANCE && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
                SwipeLeft();
                return false; // Right to left
            } else if (e2.getX() - e1.getX() > SWIPE_MIN_DISTANCE && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
                SwipeRight();
                return false; // Left to right
            }

            if (e1.getY() - e2.getY() > SWIPE_MIN_DISTANCE && Math.abs(velocityY) > SWIPE_THRESHOLD_VELOCITY) {
                return false; // Bottom to top
            } else if (e2.getY() - e1.getY() > SWIPE_MIN_DISTANCE && Math.abs(velocityY) > SWIPE_THRESHOLD_VELOCITY) {
                return false; // Top to bottom
            }
            return false;
        }
    }
}