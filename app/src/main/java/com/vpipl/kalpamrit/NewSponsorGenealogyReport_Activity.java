package com.vpipl.kalpamrit;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.widget.Toolbar;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.vpipl.kalpamrit.Adapters.ExpandableListAdapter;
import com.vpipl.kalpamrit.Utils.AppUtils;
import com.vpipl.kalpamrit.Utils.CircularImageView;
import com.vpipl.kalpamrit.Utils.QueryUtils;
import com.vpipl.kalpamrit.Utils.SPUtils;

import org.apache.commons.lang3.text.WordUtils;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NewSponsorGenealogyReport_Activity extends AppCompatActivity {

    private static DrawerLayout drawer;
    private static NavigationView navigationView;
    private String TAG = "NewSponsorGenealogyReport_Activity";
    private Button btn_load_more;
    private TableLayout displayLinear;
    private Calendar myCalendar;
    private SimpleDateFormat sdf;
    private String whichdate = "";
    private TextView txt_ttl_transfer_amt, txt_paid_transfer_amt, txt_unpaid_transfer_amt;

    private TextView txt_WB;
    private int TopRows = 25;
    private TextView txt_welcome_name;
    private TextView txt_id_number;
    private TextView txt_available_wb;
    private ArrayList<String> listDataHeader;
    private HashMap<String, List<String>> listDataChild;
    private int lastExpandedPosition = -1;
    private ExpandableListView expListView;
    private CircularImageView profileImage;
    private JSONArray HeadingJarray;
    private DatePickerDialog datePickerDialog;
    String SpoFormno = "";

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
                    startActivity(new Intent(NewSponsorGenealogyReport_Activity.this, Login_Activity.class));
                else
                    AppUtils.showDialogSignOut(NewSponsorGenealogyReport_Activity.this);
            }
        });

        if (AppController.getSpIsLogin().getBoolean(SPUtils.IS_LOGIN, false))
            img_login_logout.setImageDrawable(getResources().getDrawable(R.drawable.icon_logout_orange));
        else
            img_login_logout.setImageDrawable(getResources().getDrawable(R.drawable.icon_distributor_login_orange));
    }

    TextView txt_heading, txt_dist_id;
    EditText et_search_dist_id;
    Button btn_search, btn_reset;
    TableLayout ll ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_newsponsorgenealogy_report);

        try {
            AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);

            Toolbar toolbar = findViewById(R.id.toolbar);
            setSupportActionBar(toolbar);
            getSupportActionBar().setTitle("");
            SetupToolbar();

            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
            ll = findViewById(R.id.displayLinear);
            btn_search = findViewById(R.id.btn_search);
            btn_reset = findViewById(R.id.btn_reset);
            et_search_dist_id = findViewById(R.id.et_search_dist_id);
            txt_heading = findViewById(R.id.txt_heading);
            txt_dist_id = findViewById(R.id.txt_dist_id);

            txt_WB = findViewById(R.id.txt_ttl_transfer_amt);

            btn_load_more = findViewById(R.id.btn_load_more);

            displayLinear = findViewById(R.id.displayLinear);
            txt_ttl_transfer_amt = findViewById(R.id.txt_ttl_transfer_amt);
            txt_paid_transfer_amt = findViewById(R.id.txt_paid_transfer_amt);
            txt_unpaid_transfer_amt = findViewById(R.id.txt_unpaid_transfer_amt);
            //   txt_heading.setText("Wallet Summary Report");

            txt_dist_id.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(NewSponsorGenealogyReport_Activity.this, NewSponsorGenealogyReport_Activity.class);
                    intent.putExtra("SpoFormno", "" + SpoFormno);
                    startActivity(intent);
                    finish();
                }
            });
            btn_load_more.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    TopRows = TopRows + 25;
                    createWalletBankTransferReportRequest();

                }
            });
            btn_search.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    SpoFormno = et_search_dist_id.getText().toString();
                    TopRows = TopRows + 25;
                    createWalletBankTransferReportRequest();
                }
            });
            btn_reset.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    et_search_dist_id.setText("");
                    SpoFormno = AppController.getSpUserInfo().getString(SPUtils.USER_ID_NUMBER, "");
                    createWalletBankTransferReportRequest();
                }
            });
            Bundle bundle = getIntent().getExtras();
            if (bundle != null) {
                SpoFormno = getIntent().getStringExtra("SpoFormno");
                txt_dist_id.setText("Downline of : " + SpoFormno);
            } else {
                SpoFormno = AppController.getSpUserInfo().getString(SPUtils.USER_ID_NUMBER, "");
                txt_dist_id.setText("Downline of : " + SpoFormno);
            }

            if (AppUtils.isNetworkAvailable(this)) {
                findViewById(R.id.ll_showData).setVisibility(View.GONE);
                executeLoginRequest();
            } else {
                AppUtils.alertDialog(this, getResources().getString(R.string.txt_networkAlert));
            }

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

            LL_Nav.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String Usertype = (AppController.getSpUserInfo().getString(SPUtils.USER_TYPE, ""));
                    if (Usertype.equalsIgnoreCase("DISTRIBUTOR")) {
                        if (AppController.getSpIsLogin().getBoolean(SPUtils.IS_LOGIN, false)) {
                            startActivity(new Intent(NewSponsorGenealogyReport_Activity.this, Profile_View_Activity.class));
                        } else {
                            startActivity(new Intent(NewSponsorGenealogyReport_Activity.this, Login_Activity.class));
                        }

                        if (drawer.isDrawerOpen(GravityCompat.START)) {
                            drawer.closeDrawer(GravityCompat.START);
                        }
                    }
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
            AppUtils.showExceptionDialog(this);
        }
    }

    private void executeLoginRequest() {
        try {

            if (AppUtils.isNetworkAvailable(NewSponsorGenealogyReport_Activity.this)) {
                new AsyncTask<Void, Void, String>() {
                    protected void onPreExecute() {
                        AppUtils.showProgressDialog(NewSponsorGenealogyReport_Activity.this);
                    }

                    @Override
                    protected String doInBackground(Void... params) {
                        String response = "";
                        try {

                            List<NameValuePair> postParameters = new ArrayList<>();
                            postParameters.add(new BasicNameValuePair("UserID", AppController.getSpUserInfo().getString(SPUtils.USER_ID_NUMBER, "")));
                            postParameters.add(new BasicNameValuePair("Password", AppController.getSpUserInfo().getString(SPUtils.USER_PASSWORD, "")));
                            postParameters.add(new BasicNameValuePair("UserType", "D"));
                            response = AppUtils.callWebServiceWithMultiParam(NewSponsorGenealogyReport_Activity.this, postParameters, QueryUtils.methodMemberLoginOnPortal, TAG);

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

                                Toast.makeText(NewSponsorGenealogyReport_Activity.this, "Please Login to continue..", Toast.LENGTH_SHORT).show();

                                AppController.getSpUserInfo().edit().clear().commit();
                                AppController.getSpIsLogin().edit().clear().commit();

                                Intent intent = new Intent(NewSponsorGenealogyReport_Activity.this, Login_Activity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                intent.putExtra("SendToHome", true);
                                startActivity(intent);
                                finish();

                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            AppUtils.showExceptionDialog(NewSponsorGenealogyReport_Activity.this);
                        }
                    }
                }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            }
        } catch (Exception e) {
            e.printStackTrace();
            AppUtils.showExceptionDialog(NewSponsorGenealogyReport_Activity.this);
        }
    }

    public void continueapp() {

        createWalletBankTransferReportRequest();
        enableExpandableList();
        LoadNavigationHeaderItems();
    }

    private void createWalletBankTransferReportRequest() {
        List<NameValuePair> postParameters = new ArrayList<>();
        postParameters.add(new BasicNameValuePair("LoginFormNo", "" + AppController.getSpUserInfo().getString(SPUtils.USER_FORM_NUMBER, "")));
        postParameters.add(new BasicNameValuePair("LoginIDNo", "" + AppController.getSpUserInfo().getString(SPUtils.USER_ID_NUMBER, "")));
        postParameters.add(new BasicNameValuePair("IDNo", "" + SpoFormno));
        executeWalletBankTransferReportRequest(postParameters);
    }

    private void executeWalletBankTransferReportRequest(final List postparameters) {
        try {
            if (AppUtils.isNetworkAvailable(NewSponsorGenealogyReport_Activity.this)) {
                new AsyncTask<Void, Void, String>() {
                    protected void onPreExecute() {
                        AppUtils.showProgressDialog(NewSponsorGenealogyReport_Activity.this);
                    }

                    @Override
                    protected String doInBackground(Void... params) {
                        String response = "";
                        try {
                            response = AppUtils.callWebServiceWithMultiParam(NewSponsorGenealogyReport_Activity.this,
                                    postparameters, QueryUtils.methodtoNewSponsorGenealogy, TAG);

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        return response;
                    }

                    @Override
                    protected void onPostExecute(String resultData) {
                        AppUtils.dismissProgressDialog();

                        try {
                            executeWalletBalanceRequest();

                            JSONObject jsonObject = new JSONObject(resultData);

                            if (jsonObject.getString("Status").equalsIgnoreCase("True")) {
                                JSONArray jsonArrayData = jsonObject.getJSONArray("Data");

                                if (jsonObject.getString("Message").equalsIgnoreCase("Successfully.!")) {
                                    WriteValues(jsonArrayData);
                                } else {
                                    AppUtils.alertDialog(NewSponsorGenealogyReport_Activity.this, jsonObject.getString("Message"));
                                    ll.removeAllViews();
                                    btn_load_more.setVisibility(View.GONE);
                                }
                            } else {
                                AppUtils.alertDialog(NewSponsorGenealogyReport_Activity.this, jsonObject.getString("Message"));
                                ll.removeAllViews();
                                btn_load_more.setVisibility(View.GONE);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            AppUtils.showExceptionDialog(NewSponsorGenealogyReport_Activity.this);
                        }
                    }
                }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            }
        } catch (Exception e) {
            e.printStackTrace();
            AppUtils.showExceptionDialog(NewSponsorGenealogyReport_Activity.this);
        }
    }

    private void executeWalletBalanceRequest() {
        try {
            if (AppUtils.isNetworkAvailable(NewSponsorGenealogyReport_Activity.this)) {
                new AsyncTask<Void, Void, String>() {
                    @Override
                    protected String doInBackground(Void... params) {
                        String response = "";
                        try {
                            List<NameValuePair> postParameters = new ArrayList<>();
                            postParameters.add(new BasicNameValuePair("Formno", AppController.getSpUserInfo().getString(SPUtils.USER_FORM_NUMBER, "")));
                            response = AppUtils.callWebServiceWithMultiParam(NewSponsorGenealogyReport_Activity.this,
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
                                    String count_text = "(Available Wallet Balance ₹ " + jsonArrayData.getJSONObject(0).getString("WBalance") + " )";
                                    //  txt_WB.setText(count_text);

                                    count_text = "Wallet Balance : \u20B9 " + jsonArrayData.getJSONObject(0).getString("WBalance");
                                    txt_available_wb.setText(count_text);
                                    txt_available_wb.setVisibility(View.VISIBLE);
                                } else {
                                    AppUtils.alertDialog(NewSponsorGenealogyReport_Activity.this, jsonObject.getString("Message"));
                                }
                            } else {
                                AppUtils.alertDialog(NewSponsorGenealogyReport_Activity.this, jsonObject.getString("Message"));
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            AppUtils.showExceptionDialog(NewSponsorGenealogyReport_Activity.this);
                        }
                    }
                }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            }
        } catch (Exception e) {
            e.printStackTrace();
            AppUtils.showExceptionDialog(NewSponsorGenealogyReport_Activity.this);
        }
    }

    private void WriteValues(final JSONArray jarray) {
        findViewById(R.id.ll_showData).setVisibility(View.VISIBLE);

        float sp = 8;
        int px = (int) (sp * getResources().getDisplayMetrics().scaledDensity);


        ll.removeAllViews();

        Typeface typeface = ResourcesCompat.getFont(this, R.font.gisha_0);

        TableRow row1 = new TableRow(this);

        TableRow.LayoutParams lp1 = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT);
        row1.setLayoutParams(lp1);
        row1.setBackgroundColor(getResources().getColor(R.color.color_green_text));

        TextView A1 = new TextView(this);
        TextView B1 = new TextView(this);
        TextView C1 = new TextView(this);
        TextView D1 = new TextView(this);
        TextView E1 = new TextView(this);
        TextView F1 = new TextView(this);
        TextView G1 = new TextView(this);
        TextView H1 = new TextView(this);
        TextView I1 = new TextView(this);
        TextView J1 = new TextView(this);
        TextView K1 = new TextView(this);
        TextView L1 = new TextView(this);

        A1.setText("Distributor ID");
        B1.setText("Designation");
        C1.setText("Name");
        D1.setText("%");
        E1.setText("Self BV");
        F1.setText("Group BV");
        G1.setText("Total BV");
        H1.setText("View");
        I1.setText("");
        J1.setText("");

        K1.setText("");
        L1.setText("");

        A1.setPadding(px, px, px, px);
        B1.setPadding(px, px, px, px);
        C1.setPadding(px, px, px, px);
        D1.setPadding(px, px, px, px);
        E1.setPadding(px, px, px, px);
        F1.setPadding(px, px, px, px);
        G1.setPadding(px, px, px, px);
        H1.setPadding(px, px, px, px);
        I1.setPadding(px, px, px, px);
        J1.setPadding(px, px, px, px);
        K1.setPadding(px, px, px, px);
        L1.setPadding(px, px, px, px);

        A1.setTypeface(typeface);
        B1.setTypeface(typeface);
        C1.setTypeface(typeface);
        D1.setTypeface(typeface);
        E1.setTypeface(typeface);
        F1.setTypeface(typeface);
        G1.setTypeface(typeface);
        H1.setTypeface(typeface);
        I1.setTypeface(typeface);
        J1.setTypeface(typeface);
        K1.setTypeface(typeface);
        L1.setTypeface(typeface);

        A1.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
        B1.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
        C1.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
        D1.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
        E1.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
        F1.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
        G1.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
        H1.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
        I1.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
        J1.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
        L1.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
        J1.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
        K1.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
        L1.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);

        A1.setGravity(Gravity.CENTER);
        B1.setGravity(Gravity.CENTER);
        C1.setGravity(Gravity.CENTER);
        D1.setGravity(Gravity.CENTER);
        E1.setGravity(Gravity.CENTER);
        F1.setGravity(Gravity.CENTER);
        G1.setGravity(Gravity.CENTER);
        H1.setGravity(Gravity.CENTER);
        I1.setGravity(Gravity.CENTER);
        J1.setGravity(Gravity.CENTER);
        L1.setGravity(Gravity.CENTER);
        E1.setGravity(Gravity.CENTER);
        F1.setGravity(Gravity.CENTER);
        G1.setGravity(Gravity.CENTER);
        H1.setGravity(Gravity.CENTER);
        I1.setGravity(Gravity.CENTER);
        J1.setGravity(Gravity.CENTER);
        K1.setGravity(Gravity.CENTER);
        L1.setGravity(Gravity.CENTER);

        A1.setTextColor(Color.WHITE);
        B1.setTextColor(Color.WHITE);
        C1.setTextColor(Color.WHITE);
        D1.setTextColor(Color.WHITE);
        E1.setTextColor(Color.WHITE);
        F1.setTextColor(Color.WHITE);
        G1.setTextColor(Color.WHITE);
        H1.setTextColor(Color.WHITE);
        I1.setTextColor(Color.WHITE);
        J1.setTextColor(Color.WHITE);
        L1.setTextColor(Color.WHITE);
        D1.setTextColor(Color.WHITE);
        E1.setTextColor(Color.WHITE);
        F1.setTextColor(Color.WHITE);
        G1.setTextColor(Color.WHITE);
        H1.setTextColor(Color.WHITE);
        I1.setTextColor(Color.WHITE);
        J1.setTextColor(Color.WHITE);
        K1.setTextColor(Color.WHITE);
        L1.setTextColor(Color.WHITE);

        row1.addView(A1);
        row1.addView(B1);
        row1.addView(C1);
        row1.addView(D1);
        row1.addView(E1);
        row1.addView(F1);
        row1.addView(G1);
        row1.addView(H1);
        row1.addView(I1);
        row1.addView(J1);

        // row1.addView(K1);
        // row1.addView(L1);

        View view = new View(this);
        view.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 1));
        view.setBackgroundColor(Color.parseColor("#cccccc"));

        ll.addView(row1);
        ll.addView(view);


        for (int i = 0; i < jarray.length(); i++) {
            try {

                JSONObject jobject = jarray.getJSONObject(i);
                final String DistributorID = jobject.getString("IdNo");
                final String Designation = jobject.getString("Designation");
                final String Name = jobject.getString("MemName");
                final String percentage = jobject.getString("Slab");
                final String SelfBV = jobject.getString("SRepurchase");
                final String GroupBV = jobject.getString("DRepurchase");
                final String TotalBV = jobject.getString("TotalBVCum");

                final String Upline = jobject.getString("Upline");
                final String upline_FormNoDwn = jobject.getString("FormNoDwn");

/*
                StringBuilder sb = new StringBuilder(TransferRemarks);

                int ii = 0;
                while ((ii = sb.indexOf(" ", ii + 15)) != -1) {
                    sb.replace(ii, ii + 1, "\n");
                } */
                TableRow row = new TableRow(this);
                TableRow.LayoutParams lp = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT);
                row.setLayoutParams(lp);
                row.setPadding(0, px, 0, px);

                if (i % 2 == 0)
                    row.setBackgroundColor(Color.WHITE);
                else
                    row.setBackgroundColor(Color.parseColor("#dddddd"));


                final TextView A = new TextView(this);
                TextView B = new TextView(this);
                TextView C = new TextView(this);
                TextView D = new TextView(this);
                TextView E = new TextView(this);
                TextView F = new TextView(this);
                TextView G = new TextView(this);
                TextView H = new TextView(this);
                TextView I = new TextView(this);
                TextView J = new TextView(this);
                TextView K = new TextView(this);
                final TextView L = new TextView(this);

                A.setText(DistributorID);
                B.setText(Designation);
                C.setText(Name);
                D.setText(percentage);
                E.setText(SelfBV);
                F.setText(GroupBV);
                G.setText(TotalBV);
                H.setText("Downline");
                I.setText("Upline");
                J.setText("Main Upline");

               /* K.setText(TranNo);
                L.setText(PaidRemark);*/

                A.setPadding(px, px, px, px);
                B.setPadding(px, px, px, px);
                C.setPadding(px, px, px, px);
                D.setPadding(px, px, px, px);
                E.setPadding(px, px, px, px);
                F.setPadding(px, px, px, px);
                G.setPadding(px, px, px, px);
                H.setPadding(px, px, px, px);
                I.setPadding(px, px, px, px);
                J.setPadding(px, px, px, px);
                K.setPadding(px, px, px, px);
                L.setPadding(px, px, px, px);

                A.setTypeface(typeface);
                B.setTypeface(typeface);
                C.setTypeface(typeface);
                D.setTypeface(typeface);
                E.setTypeface(typeface);
                F.setTypeface(typeface);
                G.setTypeface(typeface);
                H.setTypeface(typeface);
                I.setTypeface(typeface);
                J.setTypeface(typeface);
                K.setTypeface(typeface);
                L.setTypeface(typeface);

                A.setTextSize(TypedValue.COMPLEX_UNIT_SP, 13);
                B.setTextSize(TypedValue.COMPLEX_UNIT_SP, 13);
                C.setTextSize(TypedValue.COMPLEX_UNIT_SP, 13);
                D.setTextSize(TypedValue.COMPLEX_UNIT_SP, 13);
                E.setTextSize(TypedValue.COMPLEX_UNIT_SP, 13);
                F.setTextSize(TypedValue.COMPLEX_UNIT_SP, 13);
                G.setTextSize(TypedValue.COMPLEX_UNIT_SP, 13);
                H.setTextSize(TypedValue.COMPLEX_UNIT_SP, 13);
                I.setTextSize(TypedValue.COMPLEX_UNIT_SP, 13);
                J.setTextSize(TypedValue.COMPLEX_UNIT_SP, 13);
                K.setTextSize(TypedValue.COMPLEX_UNIT_SP, 13);
                L.setTextSize(TypedValue.COMPLEX_UNIT_SP, 13);

                A.setGravity(Gravity.CENTER);
                B.setGravity(Gravity.CENTER);
                C.setGravity(Gravity.CENTER);
                D.setGravity(Gravity.CENTER);
                E.setGravity(Gravity.CENTER);
                F.setGravity(Gravity.CENTER);
                G.setGravity(Gravity.CENTER);
                H.setGravity(Gravity.CENTER);
                I.setGravity(Gravity.CENTER);
                J.setGravity(Gravity.CENTER);
                K.setGravity(Gravity.CENTER);
                L.setGravity(Gravity.CENTER);

                A.setTextColor(Color.BLACK);
                B.setTextColor(Color.BLACK);
                C.setTextColor(Color.BLACK);
                D.setTextColor(Color.BLACK);
                E.setTextColor(Color.BLACK);
                F.setTextColor(Color.BLACK);
                G.setTextColor(Color.BLACK);
                H.setTextColor(Color.BLACK);
                I.setTextColor(Color.BLACK);
                J.setTextColor(Color.BLACK);
                L.setTextColor(Color.BLACK);
                D.setTextColor(Color.BLACK);
                E.setTextColor(Color.BLACK);
                F.setTextColor(Color.BLACK);
                G.setTextColor(Color.BLACK);

                H.setTextColor(Color.WHITE);
                I.setTextColor(Color.WHITE);
                J.setTextColor(Color.WHITE);

                K.setTextColor(Color.WHITE);
                L.setTextColor(Color.WHITE);

                /*H1.setTextColor(getResources().getColor(R.color.app_color_white));
                I1.setTextColor(getResources().getColor(R.color.app_color_white));
                J1.setTextColor(getResources().getColor(R.color.app_color_white));
*/
                TableRow.LayoutParams params = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT);
                params.setMargins(px, 0, px, 0);

                H.setBackground(getResources().getDrawable(R.drawable.bg_round_rectangle_orange_));
                H.setLayoutParams(params);

                H.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        //   executeEncryptPayoutNoRequest("" + A.getId());
                        Intent intent = new Intent(NewSponsorGenealogyReport_Activity.this, NewSponsorGenealogyReport_Activity.class);
                        intent.putExtra("SpoFormno", "" + DistributorID);
                        startActivity(intent);
                        finish();
                    }
                });
                I.setBackground(getResources().getDrawable(R.drawable.bg_round_rectangle_orange_));
                I.setLayoutParams(params);

                I.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        //   executeEncryptPayoutNoRequest("" + A.getId());
                        Intent intent = new Intent(NewSponsorGenealogyReport_Activity.this, NewSponsorGenealogyReport_Activity.class);
                        intent.putExtra("SpoFormno", "" + Upline);
                        startActivity(intent);
                        finish();
                    }
                });
                J.setBackground(getResources().getDrawable(R.drawable.bg_round_rectangle_orange_));
                J.setLayoutParams(params);

                J.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        //   executeEncryptPayoutNoRequest("" + A.getId());
                        Intent intent = new Intent(NewSponsorGenealogyReport_Activity.this, NewSponsorGenealogyReport_Activity.class);
                        //   intent.putExtra("URL" , "https://kalpamritmarketing.com/admin/NewSponsorGenealogy.aspx?IDNumber=KI116482");
                        intent.putExtra("SpoFormno", "" + AppController.getSpUserInfo().getString(SPUtils.USER_ID_NUMBER, ""));
                        startActivity(intent);
                        finish();
                    }
                });

                row.addView(A);
                row.addView(B);
                row.addView(C);
                row.addView(D);
                row.addView(E);
                row.addView(F);
                row.addView(G);
                row.addView(H);
                row.addView(I);
                row.addView(J);

                // row.addView(K);
                // row.addView(L);

                View view_one = new View(this);
                view_one.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 1));
                view.setBackgroundColor(Color.parseColor("#cccccc"));

                ll.addView(row);
                ll.addView(view_one);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        try {
            AppUtils.dismissProgressDialog();
        } catch (Exception e) {
            e.printStackTrace();
            AppUtils.showExceptionDialog(NewSponsorGenealogyReport_Activity.this);
        }
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
                    startActivity(new Intent(NewSponsorGenealogyReport_Activity.this, DashBoard_Activity.class));
                    if (drawer.isDrawerOpen(GravityCompat.START)) {
                        drawer.closeDrawer(GravityCompat.START);
                    }
                } else if (GroupTitle.trim().equalsIgnoreCase("Logout")) {
                    AppUtils.showDialogSignOut(NewSponsorGenealogyReport_Activity.this);
                } else if (GroupTitle.trim().equalsIgnoreCase("New Joining")) {
                    startActivity(new Intent(NewSponsorGenealogyReport_Activity.this, Register_Activity.class));
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
                    startActivity(new Intent(NewSponsorGenealogyReport_Activity.this, Profile_View_Activity.class));
                } else if (ChildItemTitle.trim().equalsIgnoreCase(getResources().getString(R.string.change_password))) {
                    startActivity(new Intent(NewSponsorGenealogyReport_Activity.this, Change_Password_Activity.class));
                } else if (ChildItemTitle.trim().equalsIgnoreCase(getResources().getString(R.string.new_joining))) {
                    startActivity(new Intent(NewSponsorGenealogyReport_Activity.this, Register_Activity.class));
                } else if (ChildItemTitle.trim().equalsIgnoreCase(getResources().getString(R.string.upload_kyc))) {
                    startActivity(new Intent(NewSponsorGenealogyReport_Activity.this, KYCUploadDocument_Activity.class).putExtra("HEADING", "Update"));
                } else if (ChildItemTitle.trim().equalsIgnoreCase(getResources().getString(R.string.generation_structure))) {
                    startActivity(new Intent(NewSponsorGenealogyReport_Activity.this, Sponsor_genealogy_Activity.class));
                } else if (ChildItemTitle.trim().equalsIgnoreCase(getResources().getString(R.string.sponsor_downline))) {
                    startActivity(new Intent(NewSponsorGenealogyReport_Activity.this, Sponsor_team_details_Activity.class).putExtra("Action", "Sponsor"));
                } else if (ChildItemTitle.trim().equalsIgnoreCase(getResources().getString(R.string.bv_detail_report))) {
                    startActivity(new Intent(NewSponsorGenealogyReport_Activity.this, Sponsor_team_details_Activity.class).putExtra("Action", "Direct"));
                } else if (ChildItemTitle.trim().equalsIgnoreCase(getResources().getString(R.string.welcome_letter))) {
                    startActivity(new Intent(NewSponsorGenealogyReport_Activity.this, WelcomeLetter_Activity.class));
                } else if (ChildItemTitle.trim().equalsIgnoreCase(getResources().getString(R.string.purchase_reports))) {
                    startActivity(new Intent(NewSponsorGenealogyReport_Activity.this, Repurchase_BV_Detail.class));
                } else if (ChildItemTitle.trim().equalsIgnoreCase("Team Repurchase BV Summary")) {
                    startActivity(new Intent(NewSponsorGenealogyReport_Activity.this, Repurchase_BV_Summary_Team_Activity.class));
                } else if (ChildItemTitle.trim().equalsIgnoreCase("Repurchase Bill Summary")) {
                    startActivity(new Intent(NewSponsorGenealogyReport_Activity.this, Repurchase_Bill_Summary.class));
                } else if (ChildItemTitle.trim().equalsIgnoreCase(getResources().getString(R.string.wallet_detail))) {
                    startActivity(new Intent(NewSponsorGenealogyReport_Activity.this, Wallet_Transaction_Report_Activity.class));
                } else if (ChildItemTitle.trim().equalsIgnoreCase("Wallet Request Report")) {
                    startActivity(new Intent(NewSponsorGenealogyReport_Activity.this, Wallet_Request_Status_Report_Activity.class));
                } else if (ChildItemTitle.trim().equalsIgnoreCase("Request For Wallet Amount")) {
                    startActivity(new Intent(NewSponsorGenealogyReport_Activity.this, Wallet_Request_Amount_Activity.class));
                } else if (ChildItemTitle.trim().equalsIgnoreCase(getResources().getString(R.string.monthly_incentive))) {
                    startActivity(new Intent(NewSponsorGenealogyReport_Activity.this, Monthly_Incentive_Activity.class));
                } else if (ChildItemTitle.trim().equalsIgnoreCase(getResources().getString(R.string.monthly_incentive_detail_report))) {
                    startActivity(new Intent(NewSponsorGenealogyReport_Activity.this, Monthly_Incentive_Detail_Activity.class));
                } else if (ChildItemTitle.trim().equalsIgnoreCase("TDS Detail Report")) {
                    startActivity(new Intent(NewSponsorGenealogyReport_Activity.this, TDS_detail_report_Activity.class));
                } else if (ChildItemTitle.trim().equalsIgnoreCase("Update Profile")) {
                    startActivity(new Intent(NewSponsorGenealogyReport_Activity.this, Profile_Update_Activity.class));
                } else if (ChildItemTitle.trim().equalsIgnoreCase("View KYC Documents")) {
                    startActivity(new Intent(NewSponsorGenealogyReport_Activity.this, KYCUploadDocument_Activity.class).putExtra("HEADING", "View"));
                } else if (ChildItemTitle.trim().equalsIgnoreCase("Register Enquiry/Complaint")) {
                    startActivity(new Intent(NewSponsorGenealogyReport_Activity.this, Register_Complaint_Activity.class));
                } else if (ChildItemTitle.trim().equalsIgnoreCase("View Open/Pending Queries")) {
                    startActivity(new Intent(NewSponsorGenealogyReport_Activity.this, ViewPendingQueries_Activity.class).putExtra("HEADING", "Open"));
                } else if (ChildItemTitle.trim().equalsIgnoreCase("View Closed Queries")) {
                    startActivity(new Intent(NewSponsorGenealogyReport_Activity.this, ViewPendingQueries_Activity.class).putExtra("HEADING", "Closed"));
                } else if (ChildItemTitle.trim().equalsIgnoreCase("Product Wallet Detail")) {
                    startActivity(new Intent(NewSponsorGenealogyReport_Activity.this, ProductWallet_Transaction_Report_Activity.class));
                } else if (ChildItemTitle.trim().equalsIgnoreCase("Wallet To Bank Transfer Detail")) {
                    startActivity(new Intent(NewSponsorGenealogyReport_Activity.this, NewSponsorGenealogyReport_Activity.class));
                } else if (ChildItemTitle.trim().equalsIgnoreCase("ID Card")) {
                    startActivity(new Intent(NewSponsorGenealogyReport_Activity.this, ID_card_Activity.class));
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

            if (!bytecode.equalsIgnoreCase(""))
                profileImage.setImageBitmap(AppUtils.getBitmapFromString(bytecode));
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
                AppUtils.showProgressDialog(NewSponsorGenealogyReport_Activity.this);
            }

            @Override
            protected String doInBackground(Void... params) {

                Thread.currentThread().setPriority(Thread.MAX_PRIORITY);

                String response = "";
                try {
                    List<NameValuePair> postParameters = new ArrayList<>();
                    response = AppUtils.callWebServiceWithMultiParam(NewSponsorGenealogyReport_Activity.this, postParameters, QueryUtils.methodtoGetDrawerMenuItems, TAG);

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

}
