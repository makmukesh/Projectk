package com.vpipl.kalpamrit;

import android.app.Dialog;
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
import android.view.Window;
import android.view.WindowManager;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Monthly_Incentive_Detail_Activity extends AppCompatActivity {

    private String TAG = "Monthly_Incentive_Detail_Activity";

    private TableLayout displayLinear;

    private DrawerLayout drawer;
    private NavigationView navigationView;
    private TextView txt_welcome_name;
    private TextView txt_id_number;
    private TextView txt_available_wb;
    private ArrayList<String> listDataHeader;
    private HashMap<String, List<String>> listDataChild;
    private int lastExpandedPosition = -1;
    private ExpandableListView expListView;
    private CircularImageView profileImage;
    private JSONArray HeadingJarray;

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
                    startActivity(new Intent(Monthly_Incentive_Detail_Activity.this, Login_Activity.class));
                else
                    AppUtils.showDialogSignOut(Monthly_Incentive_Detail_Activity.this);
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
        setContentView(R.layout.activity_monthly_incentive_detail);

        try {
            AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);

            Toolbar toolbar = findViewById(R.id.toolbar);
            setSupportActionBar(toolbar);
            getSupportActionBar().setTitle("");
            SetupToolbar();

            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

            displayLinear = findViewById(R.id.displayLinear);

            if (AppUtils.isNetworkAvailable(this)) {
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
                            startActivity(new Intent(Monthly_Incentive_Detail_Activity.this, Profile_View_Activity.class));
                        } else {
                            startActivity(new Intent(Monthly_Incentive_Detail_Activity.this, Login_Activity.class));
                        }

                        if (drawer.isDrawerOpen(GravityCompat.START)) {
                            drawer.closeDrawer(GravityCompat.START);
                        }
                    }
                }
            });

            enableExpandableList();
            LoadNavigationHeaderItems();
        } catch (Exception e) {
            e.printStackTrace();
            AppUtils.showExceptionDialog(this);
        }
    }


    private void executeLoginRequest() {
        try {

            if (AppUtils.isNetworkAvailable(Monthly_Incentive_Detail_Activity.this)) {
                new AsyncTask<Void, Void, String>() {
                    protected void onPreExecute() {
                        AppUtils.showProgressDialog(Monthly_Incentive_Detail_Activity.this);
                    }

                    @Override
                    protected String doInBackground(Void... params) {
                        String response = "";
                        try {

                            List<NameValuePair> postParameters = new ArrayList<>();
                            postParameters.add(new BasicNameValuePair("UserID", AppController.getSpUserInfo().getString(SPUtils.USER_ID_NUMBER, "")));
                            postParameters.add(new BasicNameValuePair("Password", AppController.getSpUserInfo().getString(SPUtils.USER_PASSWORD, "")));
                            postParameters.add(new BasicNameValuePair("UserType", "D"));
                            response = AppUtils.callWebServiceWithMultiParam(Monthly_Incentive_Detail_Activity.this, postParameters, QueryUtils.methodMemberLoginOnPortal, TAG);

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

                                Toast.makeText(Monthly_Incentive_Detail_Activity.this, "Please Login to continue..", Toast.LENGTH_SHORT).show();

                                AppController.getSpUserInfo().edit().clear().commit();
                                AppController.getSpIsLogin().edit().clear().commit();

                                Intent intent = new Intent(Monthly_Incentive_Detail_Activity.this, Login_Activity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                intent.putExtra("SendToHome", true);
                                startActivity(intent);
                                finish();

                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            AppUtils.showExceptionDialog(Monthly_Incentive_Detail_Activity.this);
                        }
                    }
                }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            }
        } catch (Exception e) {
            e.printStackTrace();
            AppUtils.showExceptionDialog(Monthly_Incentive_Detail_Activity.this);
        }
    }

    public void continueapp() {

        executeMonthlyIncentiveDetailRequest();

        enableExpandableList();
        LoadNavigationHeaderItems();
    }

    private void executeMonthlyIncentiveDetailRequest() {
        try {
            if (AppUtils.isNetworkAvailable(Monthly_Incentive_Detail_Activity.this)) {
                new AsyncTask<Void, Void, String>() {
                    protected void onPreExecute() {
                        AppUtils.showProgressDialog(Monthly_Incentive_Detail_Activity.this);
                    }

                    @Override
                    protected String doInBackground(Void... params) {
                        String response = "";
                        try {

                            List<NameValuePair> postParameters = new ArrayList<>();
                            postParameters.add(new BasicNameValuePair("FormNo", AppController.getSpUserInfo().getString(SPUtils.USER_FORM_NUMBER, "")));
                            response = AppUtils.callWebServiceWithMultiParam(Monthly_Incentive_Detail_Activity.this, postParameters, QueryUtils.methodToGetMonthlyIncentiveDetailReport, TAG);

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        return response;
                    }

                    @Override
                    protected void onPostExecute(String resultData) {
                        AppUtils.dismissProgressDialog();

                        try {
                            JSONObject jsonObject = new JSONObject(resultData);
                            JSONArray jsonArrayData = jsonObject.getJSONArray("Data");

                            if (jsonObject.getString("Status").equalsIgnoreCase("True")) {
                                WriteValues(jsonArrayData);
                            } else {
                                AppUtils.dismissProgressDialog();
                                AppUtils.alertDialog(Monthly_Incentive_Detail_Activity.this, jsonObject.getString("Message"));
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            AppUtils.showExceptionDialog(Monthly_Incentive_Detail_Activity.this);
                        }
                    }
                }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            }
        } catch (Exception e) {
            e.printStackTrace();
            AppUtils.showExceptionDialog(Monthly_Incentive_Detail_Activity.this);
        }
    }

    private void WriteValues(JSONArray jsonArray) {

        float sp = 10;

        int px = (int) (sp * getResources().getDisplayMetrics().scaledDensity);

        TableLayout ll = findViewById(R.id.displayLinear);

        Typeface typeface = ResourcesCompat.getFont(this, R.font.gisha_0);

        TableRow row1 = new TableRow(this);

        TableRow.LayoutParams lp1 = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT);
        row1.setLayoutParams(lp1);
        row1.setBackgroundColor(getResources().getColor(R.color.color_green_text));

        TextView A1 = new TextView(this);
        TextView B1 = new TextView(this);
        TextView C1 = new TextView(this);
//        TextView D1 = new TextView(this);
//        TextView E1 = new TextView(this);
//        TextView F1 = new TextView(this);
//        TextView G1 = new TextView(this);
//        TextView H1 = new TextView(this);
//        TextView I1 = new TextView(this);
//        TextView J1 = new TextView(this);
//        TextView K1 = new TextView(this);
//        TextView L1 = new TextView(this);
//        TextView M1 = new TextView(this);
//        TextView N1 = new TextView(this);
//        TextView O1 = new TextView(this);
//        TextView P1 = new TextView(this);
//        TextView Q1 = new TextView(this);
//        TextView R1 = new TextView(this);
//        TextView S1 = new TextView(this);
//        TextView T1 = new TextView(this);
//        TextView U1 = new TextView(this);
//        TextView V1 = new TextView(this);
//        TextView W1 = new TextView(this);
//        TextView X1 = new TextView(this);
        TextView Y1 = new TextView(this);
//        TextView Z1 = new TextView(this);
//        TextView AA1 = new TextView(this);
        TextView BB1 = new TextView(this);
        TextView CC1 = new TextView(this);
        TextView DD1 = new TextView(this);

        A1.setText("Payout No.");
        B1.setText("Month Name");
        C1.setText("Performance\nBonus Slab");
//        D1.setText("Self\nPerformance\nBonus");
//        E1.setText("Team \nPerformance\nBonus");
//        F1.setText("Total \nPerformance\nBonus");
//
//        G1.setText("Self BV");
//        H1.setText("Team BV");
//        I1.setText("Total BV");
//        J1.setText("Team\nA BV");
//        K1.setText("Team\nB BV");
//        L1.setText("Team\nC BV");
//        M1.setText("Team\nD BV");
//
//        N1.setText("Active\nBonus\nPoint");
//        O1.setText("Active\nBonus");
//
//        P1.setText("Leadership\nBonus\nPoint");
//        Q1.setText("Leadership\nBonus");
//
//        R1.setText("Mentor\nBonus\nPoint");
//        S1.setText("Mentor\nBonus");
//
//        T1.setText("Recognition");
//
//        U1.setText("Education\nFund");
//        V1.setText("Travel\nFund");
//        W1.setText("CAR\nFund");
//        X1.setText("House\nFund");
//
        Y1.setText("Gross Incentive");
//        Z1.setText("TDS\nAmount");

//        AA1.setText("Service\nCharge");
//        BB1.setText("Previous\nBalance");
        BB1.setText("Cheque Amount");
        CC1.setText("Closing Balance");
        DD1.setText("View More");

        A1.setPadding(px, px, px, px);
        B1.setPadding(px, px, px, px);
        C1.setPadding(px, px, px, px);
//        D1.setPadding(px, px, px, px);
//        E1.setPadding(px, px, px, px);
//        F1.setPadding(px, px, px, px);
//        G1.setPadding(px, px, px, px);
//        H1.setPadding(px, px, px, px);
//        I1.setPadding(px, px, px, px);
//        J1.setPadding(px, px, px, px);
//        K1.setPadding(px, px, px, px);
//        L1.setPadding(px, px, px, px);
//        M1.setPadding(px, px, px, px);
//        N1.setPadding(px, px, px, px);
//        O1.setPadding(px, px, px, px);
//        P1.setPadding(px, px, px, px);
//        Q1.setPadding(px, px, px, px);
//        R1.setPadding(px, px, px, px);
//        S1.setPadding(px, px, px, px);
//        T1.setPadding(px, px, px, px);
//        U1.setPadding(px, px, px, px);
//        V1.setPadding(px, px, px, px);
//        W1.setPadding(px, px, px, px);
//        W1.setPadding(px, px, px, px);
//        X1.setPadding(px, px, px, px);
        Y1.setPadding(px, px, px, px);
//        Z1.setPadding(px, px, px, px);
//        AA1.setPadding(px, px, px, px);
        BB1.setPadding(px, px, px, px);
        CC1.setPadding(px, px, px, px);
        DD1.setPadding(px, px, px, px);

        A1.setTypeface(typeface);
        B1.setTypeface(typeface);
        C1.setTypeface(typeface);
//        D1.setTypeface(typeface);
//        E1.setTypeface(typeface);
//        F1.setTypeface(typeface);
//        G1.setTypeface(typeface);
//        H1.setTypeface(typeface);
//        I1.setTypeface(typeface);
//        J1.setTypeface(typeface);
//        K1.setTypeface(typeface);
//        L1.setTypeface(typeface);
//        M1.setTypeface(typeface);
//        N1.setTypeface(typeface);
//        O1.setTypeface(typeface);
//        P1.setTypeface(typeface);
//        Q1.setTypeface(typeface);
//        R1.setTypeface(typeface);
//        S1.setTypeface(typeface);
//        T1.setTypeface(typeface);
//        U1.setTypeface(typeface);
//        V1.setTypeface(typeface);
//        W1.setTypeface(typeface);
//        W1.setTypeface(typeface);
//        X1.setTypeface(typeface);
        Y1.setTypeface(typeface);
//        Z1.setTypeface(typeface);
//        AA1.setTypeface(typeface);
        BB1.setTypeface(typeface);
        CC1.setTypeface(typeface);
        DD1.setTypeface(typeface);

        A1.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
        B1.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
        C1.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
//        D1.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
//        E1.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
//        F1.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
//        G1.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
//        H1.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
//        I1.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
//        J1.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
//        K1.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
//        L1.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
//
//        M1.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
//        N1.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
//        O1.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
//        P1.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
//        Q1.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
//        R1.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
//        S1.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
//        T1.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
//        U1.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
//        V1.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
//        W1.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
//        X1.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
        Y1.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
//        Z1.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
//        AA1.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
        BB1.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
        CC1.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
        DD1.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);

        A1.setGravity(Gravity.CENTER);
        B1.setGravity(Gravity.CENTER);
        C1.setGravity(Gravity.CENTER);
//        D1.setGravity(Gravity.CENTER);
//        E1.setGravity(Gravity.CENTER);
//        F1.setGravity(Gravity.CENTER);
//        G1.setGravity(Gravity.CENTER);
//        H1.setGravity(Gravity.CENTER);
//        I1.setGravity(Gravity.CENTER);
//        J1.setGravity(Gravity.CENTER);
//        K1.setGravity(Gravity.CENTER);
//        L1.setGravity(Gravity.CENTER);
//
//        M1.setGravity(Gravity.CENTER);
//        N1.setGravity(Gravity.CENTER);
//        O1.setGravity(Gravity.CENTER);
//        P1.setGravity(Gravity.CENTER);
//        Q1.setGravity(Gravity.CENTER);
//        R1.setGravity(Gravity.CENTER);
//        S1.setGravity(Gravity.CENTER);
//        T1.setGravity(Gravity.CENTER);
//        U1.setGravity(Gravity.CENTER);
//        V1.setGravity(Gravity.CENTER);
//        W1.setGravity(Gravity.CENTER);
//        X1.setGravity(Gravity.CENTER);
        Y1.setGravity(Gravity.CENTER);
//        Z1.setGravity(Gravity.CENTER);
//        AA1.setGravity(Gravity.CENTER);
        BB1.setGravity(Gravity.CENTER);
        CC1.setGravity(Gravity.CENTER);
        DD1.setGravity(Gravity.CENTER);

        A1.setTextColor(Color.WHITE);
        B1.setTextColor(Color.WHITE);
        C1.setTextColor(Color.WHITE);
//        D1.setTextColor(Color.WHITE);
//        E1.setTextColor(Color.WHITE);
//        F1.setTextColor(Color.WHITE);
//        G1.setTextColor(Color.WHITE);
//        H1.setTextColor(Color.WHITE);
//        I1.setTextColor(Color.WHITE);
//        J1.setTextColor(Color.WHITE);
//        K1.setTextColor(Color.WHITE);
//        L1.setTextColor(Color.WHITE);
//        M1.setTextColor(Color.WHITE);
//        N1.setTextColor(Color.WHITE);
//        O1.setTextColor(Color.WHITE);
//        P1.setTextColor(Color.WHITE);
//        Q1.setTextColor(Color.WHITE);
//        R1.setTextColor(Color.WHITE);
//        S1.setTextColor(Color.WHITE);
//        T1.setTextColor(Color.WHITE);
//        U1.setTextColor(Color.WHITE);
//        V1.setTextColor(Color.WHITE);
//        W1.setTextColor(Color.WHITE);
//        X1.setTextColor(Color.WHITE);
        Y1.setTextColor(Color.WHITE);
//        Z1.setTextColor(Color.WHITE);
//        AA1.setTextColor(Color.WHITE);
        BB1.setTextColor(Color.WHITE);
        CC1.setTextColor(Color.WHITE);
        DD1.setTextColor(Color.WHITE);

        row1.addView(A1);
        row1.addView(B1);
        row1.addView(C1);
//        row1.addView(D1);
//        row1.addView(E1);
//        row1.addView(F1);
//        row1.addView(G1);
//        row1.addView(H1);
//        row1.addView(I1);
//        row1.addView(J1);
//        row1.addView(K1);
//        row1.addView(L1);
//        row1.addView(M1);
//        row1.addView(N1);
//        row1.addView(O1);
//        row1.addView(P1);
//        row1.addView(Q1);
//        row1.addView(R1);
//        row1.addView(S1);
//        row1.addView(T1);
//        row1.addView(U1);
//        row1.addView(V1);
//        row1.addView(W1);
//        row1.addView(X1);
        row1.addView(Y1);
//        row1.addView(Z1);
//        row1.addView(AA1);
        row1.addView(BB1);
        row1.addView(CC1);
        row1.addView(DD1);

        View view = new View(this);
        view.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 1));
        view.setBackgroundColor(Color.parseColor("#cccccc"));

        ll.addView(row1);
        ll.addView(view);

        for (int i = 0; i < jsonArray.length(); i++) {
            try {

                JSONObject jobject = jsonArray.getJSONObject(i);

                final String month_name = WordUtils.capitalizeFully(jobject.getString("MonthNm"));

                final String payout = jobject.getString("SessID");
                final String month = jobject.getString("MonthNm");

                final String self_bv = jobject.getString("SelfBV");
                final String team_bv = jobject.getString("DwnlineBV");
                final String cm_total = jobject.getString("TotalBVThisMnth");
                final String till_self_bv = jobject.getString("SelfBVCum");
                final String till_team_bv = jobject.getString("DwnlineBVCum");
                final String till_total_bv = jobject.getString("TotalBVCum");

                final String PerformanceBonusSlab = jobject.getString("PBSlab");
                final String PerformanceBonusRank = jobject.getString("PBDesignation");
                final String PerformanceBonus = jobject.getString("ProgInc");
                final String team_a_bv = jobject.getString("Leg1BV");
                final String team_b_bv = jobject.getString("Leg2BV");
                final String team_c_bv = jobject.getString("Leg3BV");

                final String PowerSideBV = jobject.getString("PwrLegBV");
                final String WeakerSideBV = jobject.getString("WkrLegBV");

                final String LoyalConsumerBonusPoint = jobject.getString("LCBPoint");
                final String LoyalConsumerBonus = jobject.getString("LCBonus");

                final String ActualSmartBonusPoint = jobject.getString("SBPointAct");

                final String SmartBonusPoint = jobject.getString("SBPoint");
                final String SmartBonus = jobject.getString("SmartBonus");
                final String DirectorBonusPoint = jobject.getString("DirectorBonusPoint");
                final String DirectorBonus = jobject.getString("DirectorBonus");
                final String RoyaltyBonusPoint = jobject.getString("RoyaltyBonusPoint");
                final String RoyaltyBonus = jobject.getString("RoyaltyBonus");

                String TravelFundStatus = jobject.getString("IsTravelQualify");

                if (TravelFundStatus.equalsIgnoreCase("N"))
                    TravelFundStatus = "Not Qualify";
                else
                    TravelFundStatus = "Qualified";

                final String TravelFund = jobject.getString("TravelFund");

                String CarFundStatus = jobject.getString("IsCarQualify");

                if (CarFundStatus.equalsIgnoreCase("N"))
                    CarFundStatus = "Not Qualify";
                else
                    CarFundStatus = "Qualified";

                final String CarFund = jobject.getString("CARFund");

                String HouseFundStatus = jobject.getString("IsHouseQualify");

                if (HouseFundStatus.equalsIgnoreCase("N"))
                    HouseFundStatus = "Not Qualify";
                else
                    HouseFundStatus = "Qualified";

                final String HouseFund = jobject.getString("HouseFund");

                final String MentorShipBonus = jobject.getString("MentorBonus");
                final String GrossIncentive = jobject.getString("NetIncome");
                final String TDSAmount = jobject.getString("TdsAmount");
                final String AdminCharge = jobject.getString("AdminCharge");
                final String PreviousBalance = jobject.getString("Prevbal");
                final String ChequeAmount = jobject.getString("ChqAmt");
                final String ClosingBalance = jobject.getString("ClsBal");

                final String payout_numbr = jobject.getString("SessID");

                StringBuilder sb = new StringBuilder(month_name);

                TableRow row = new TableRow(this);
                TableRow.LayoutParams lp = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT);
                row.setLayoutParams(lp);
                row.setPadding(0, px, 0, px);

                if (i % 2 == 0)
                    row.setBackgroundColor(Color.WHITE);
                else
                    row.setBackgroundColor(Color.parseColor("#dddddd"));


                TextView A = new TextView(this);
                TextView B = new TextView(this);
                TextView C = new TextView(this);
//                TextView D = new TextView(this);
//                TextView E = new TextView(this);
//                TextView F = new TextView(this);
//                TextView G = new TextView(this);
//                TextView H = new TextView(this);
//                TextView I = new TextView(this);
//                TextView J = new TextView(this);
//                TextView K = new TextView(this);
//                TextView L = new TextView(this);
//                TextView M = new TextView(this);
//                TextView N = new TextView(this);
//                TextView O = new TextView(this);
//                TextView P = new TextView(this);
//                TextView Q = new TextView(this);
//                TextView RR = new TextView(this);
//                TextView S = new TextView(this);
//                TextView T = new TextView(this);
//                TextView U = new TextView(this);
//                TextView V = new TextView(this);
//                TextView W = new TextView(this);
//                TextView X = new TextView(this);
                TextView Y = new TextView(this);
//                TextView Z = new TextView(this);
//                TextView AA = new TextView(this);
                TextView BB = new TextView(this);
                TextView CC = new TextView(this);
                TextView DD = new TextView(this);

                A.setText(payout_numbr);
                B.setText(sb.toString());
                C.setText(PerformanceBonusSlab);
//                D.setText(Self_Performance_Bonus);
//                E.setText(Team_Performance_Bonus);
//                F.setText(Total_Performance_Bonus);
//
//                G.setText(Self_BV);
//                H.setText(Team_BV);
//                I.setText(Total_BV);
//                J.setText(Team_A_BV);
//                K.setText(Team_B_BV);
//                L.setText(Team_C_BV);
//                M.setText(Team_D_BV);
//
//                N.setText(Active_Bonus_Point);
//                O.setText(Active_Bonus);
//
//                P.setText(Leadership_Bonus_Point);
//                Q.setText(Leadership_Bonus);
//
//                RR.setText(Mentor_Bonus_Point);
//                S.setText(Mentor_Bonus);
//
//                T.setText(Recognition);
//
//                U.setText(Education_Fund);
//                V.setText(Travel_Fund);
//                W.setText(CAR_Fund);
//                X.setText(House_Fund);
//
                Y.setText(GrossIncentive);
//                Z.setText(TDS_Amount);
//
//                AA.setText(Service_Charge);
                BB.setText(ChequeAmount);
                CC.setText(ClosingBalance);
                DD.setText("View");

                A.setPadding(px, px, px, px);
                B.setPadding(px, px, px, px);
                C.setPadding(px, px, px, px);
//                D.setPadding(px, px, px, px);
//                E.setPadding(px, px, px, px);
//                F.setPadding(px, px, px, px);
//                G.setPadding(px, px, px, px);
//                H.setPadding(px, px, px, px);
//                I.setPadding(px, px, px, px);
//                J.setPadding(px, px, px, px);
//                K.setPadding(px, px, px, px);
//                L.setPadding(px, px, px, px);
//                M.setPadding(px, px, px, px);
//                N.setPadding(px, px, px, px);
//                O.setPadding(px, px, px, px);
//                P.setPadding(px, px, px, px);
//                Q.setPadding(px, px, px, px);
//                RR.setPadding(px, px, px, px);
//                S.setPadding(px, px, px, px);
//                T.setPadding(px, px, px, px);
//                U.setPadding(px, px, px, px);
//                V.setPadding(px, px, px, px);
//                W.setPadding(px, px, px, px);
//                W.setPadding(px, px, px, px);
//                X.setPadding(px, px, px, px);
                Y.setPadding(px, px, px, px);
//                Z.setPadding(px, px, px, px);
//                AA.setPadding(px, px, px, px);
                BB.setPadding(px, px, px, px);
                CC.setPadding(px, px, px, px);
                DD.setPadding(px, px, px, px);

                A.setTypeface(typeface);
                B.setTypeface(typeface);
                C.setTypeface(typeface);
//                D.setTypeface(typeface);
//                E.setTypeface(typeface);
//                F.setTypeface(typeface);
//                G.setTypeface(typeface);
//                H.setTypeface(typeface);
//                I.setTypeface(typeface);
//                J.setTypeface(typeface);
//                K.setTypeface(typeface);
//                L.setTypeface(typeface);
//                M.setTypeface(typeface);
//                N.setTypeface(typeface);
//                O.setTypeface(typeface);
//                P.setTypeface(typeface);
//                Q.setTypeface(typeface);
//                RR.setTypeface(typeface);
//                S.setTypeface(typeface);
//                T.setTypeface(typeface);
//                U.setTypeface(typeface);
//                V.setTypeface(typeface);
//                W.setTypeface(typeface);
//                W.setTypeface(typeface);
//                X.setTypeface(typeface);
                Y.setTypeface(typeface);
//                Z.setTypeface(typeface);
//                AA.setTypeface(typeface);
                BB.setTypeface(typeface);
                CC.setTypeface(typeface);
                DD.setTypeface(typeface);

                A.setTextSize(TypedValue.COMPLEX_UNIT_SP, 13);
                B.setTextSize(TypedValue.COMPLEX_UNIT_SP, 13);
                C.setTextSize(TypedValue.COMPLEX_UNIT_SP, 13);
//                D.setTextSize(TypedValue.COMPLEX_UNIT_SP, 13);
//                E.setTextSize(TypedValue.COMPLEX_UNIT_SP, 13);
//                F.setTextSize(TypedValue.COMPLEX_UNIT_SP, 13);
//                G.setTextSize(TypedValue.COMPLEX_UNIT_SP, 13);
//                H.setTextSize(TypedValue.COMPLEX_UNIT_SP, 13);
//                I.setTextSize(TypedValue.COMPLEX_UNIT_SP, 13);
//                J.setTextSize(TypedValue.COMPLEX_UNIT_SP, 13);
//                K.setTextSize(TypedValue.COMPLEX_UNIT_SP, 13);
//                L.setTextSize(TypedValue.COMPLEX_UNIT_SP, 13);
//                M.setTextSize(TypedValue.COMPLEX_UNIT_SP, 13);
//                N.setTextSize(TypedValue.COMPLEX_UNIT_SP, 13);
//                O.setTextSize(TypedValue.COMPLEX_UNIT_SP, 13);
//                P.setTextSize(TypedValue.COMPLEX_UNIT_SP, 13);
//                Q.setTextSize(TypedValue.COMPLEX_UNIT_SP, 13);
//                RR.setTextSize(TypedValue.COMPLEX_UNIT_SP, 13);
//                S.setTextSize(TypedValue.COMPLEX_UNIT_SP, 13);
//                T.setTextSize(TypedValue.COMPLEX_UNIT_SP, 13);
//                U.setTextSize(TypedValue.COMPLEX_UNIT_SP, 13);
//                V.setTextSize(TypedValue.COMPLEX_UNIT_SP, 13);
//                W.setTextSize(TypedValue.COMPLEX_UNIT_SP, 13);
//                X.setTextSize(TypedValue.COMPLEX_UNIT_SP, 13);
                Y.setTextSize(TypedValue.COMPLEX_UNIT_SP, 13);
//                Z.setTextSize(TypedValue.COMPLEX_UNIT_SP, 13);
//                AA.setTextSize(TypedValue.COMPLEX_UNIT_SP, 13);
                BB.setTextSize(TypedValue.COMPLEX_UNIT_SP, 13);
                CC.setTextSize(TypedValue.COMPLEX_UNIT_SP, 13);
                DD.setTextSize(TypedValue.COMPLEX_UNIT_SP, 13);

                A.setGravity(Gravity.CENTER);
                B.setGravity(Gravity.CENTER);
                C.setGravity(Gravity.CENTER);
//              D.setGravity(Gravity.CENTER);
//              E.setGravity(Gravity.CENTER);
//              F.setGravity(Gravity.CENTER);
//              G.setGravity(Gravity.CENTER);
//              H.setGravity(Gravity.CENTER);
//              I.setGravity(Gravity.CENTER);
//              J.setGravity(Gravity.CENTER);
//              K.setGravity(Gravity.CENTER);
//              L.setGravity(Gravity.CENTER);
//              M.setGravity(Gravity.CENTER);
//              N.setGravity(Gravity.CENTER);
//              O.setGravity(Gravity.CENTER);
//              P.setGravity(Gravity.CENTER);
//              Q.setGravity(Gravity.CENTER);
//              RR.setGravity(Gravity.CENTER);
//              S.setGravity(Gravity.CENTER);
//              T.setGravity(Gravity.CENTER);
//              U.setGravity(Gravity.CENTER);
//              V.setGravity(Gravity.CENTER);
//              W.setGravity(Gravity.CENTER);
//              X.setGravity(Gravity.CENTER);
                Y.setGravity(Gravity.CENTER);
//              Z.setGravity(Gravity.CENTER);
//              AA.setGravity(Gravity.CENTER);
                BB.setGravity(Gravity.CENTER);
                CC.setGravity(Gravity.CENTER);
                DD.setGravity(Gravity.CENTER);

                DD.setTextColor(getResources().getColor(R.color.app_color_white));

                TableRow.LayoutParams params = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT);
                params.setMargins(px, 0, px, 0);

                DD.setBackground(getResources().getDrawable(R.drawable.bg_round_rectangle_orange_));

                DD.setLayoutParams(params);

                row.addView(A);
                row.addView(B);
                row.addView(C);
//                row.addView(D);
//                row.addView(E);
//                row.addView(F);
//                row.addView(G);
//                row.addView(H);
//                row.addView(I);
//                row.addView(J);
//                row.addView(K);
//                row.addView(L);
//                row.addView(M);
//                row.addView(N);
//                row.addView(O);
//                row.addView(P);
//                row.addView(Q);
//                row.addView(RR);
//                row.addView(S);
//                row.addView(T);
//                row.addView(U);
//                row.addView(V);
//                row.addView(W);
//                row.addView(X);
                row.addView(Y);
//                row.addView(Z);
//                row.addView(AA);
                row.addView(BB);
                row.addView(CC);
                row.addView(DD);

//
                final String finalTravelFundStatus = TravelFundStatus;

                final String finalHouseFundStatus = HouseFundStatus;
                final String finalCarFundStatus = CarFundStatus;
                DD.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        showViewMoreDailog(payout, month, self_bv, team_bv, cm_total,
                                till_self_bv, till_team_bv, till_total_bv,
                                PerformanceBonusSlab, PerformanceBonusRank, PerformanceBonus,
                                team_a_bv, team_b_bv, team_c_bv, PowerSideBV, WeakerSideBV,
                                LoyalConsumerBonusPoint, LoyalConsumerBonus,
                                ActualSmartBonusPoint, SmartBonusPoint, SmartBonus,
                                DirectorBonusPoint, DirectorBonus, RoyaltyBonusPoint, RoyaltyBonus,
                                finalTravelFundStatus, TravelFund, finalCarFundStatus, CarFund,
                                finalHouseFundStatus, HouseFund, MentorShipBonus, GrossIncentive,
                                TDSAmount, AdminCharge, PreviousBalance, ChequeAmount, ClosingBalance);
                    }
                });

                View view_one = new View(this);
                view_one.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 1));
                view.setBackgroundColor(Color.parseColor("#cccccc"));

                ll.addView(row);

//                if (i < jsonArray.length() - 1)
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
            AppUtils.showExceptionDialog(Monthly_Incentive_Detail_Activity.this);
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
                    startActivity(new Intent(Monthly_Incentive_Detail_Activity.this, DashBoard_Activity.class));
                    if (drawer.isDrawerOpen(GravityCompat.START)) {
                        drawer.closeDrawer(GravityCompat.START);
                    }
                } else if (GroupTitle.trim().equalsIgnoreCase("Logout")) {
                    AppUtils.showDialogSignOut(Monthly_Incentive_Detail_Activity.this);
                } else if (GroupTitle.trim().equalsIgnoreCase("New Joining")) {
                    startActivity(new Intent(Monthly_Incentive_Detail_Activity.this, Register_Activity.class));
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
                    startActivity(new Intent(Monthly_Incentive_Detail_Activity.this, Profile_View_Activity.class));
                } else if (ChildItemTitle.trim().equalsIgnoreCase(getResources().getString(R.string.change_password))) {
                    startActivity(new Intent(Monthly_Incentive_Detail_Activity.this, Change_Password_Activity.class));
                } else if (ChildItemTitle.trim().equalsIgnoreCase(getResources().getString(R.string.new_joining))) {
                    startActivity(new Intent(Monthly_Incentive_Detail_Activity.this, Register_Activity.class));
                } else if (ChildItemTitle.trim().equalsIgnoreCase(getResources().getString(R.string.upload_kyc))) {
                    startActivity(new Intent(Monthly_Incentive_Detail_Activity.this, KYCUploadDocument_Activity.class).putExtra("HEADING", "Update"));
                } else if (ChildItemTitle.trim().equalsIgnoreCase(getResources().getString(R.string.generation_structure))) {
                    startActivity(new Intent(Monthly_Incentive_Detail_Activity.this, Sponsor_genealogy_Activity.class));
                } else if (ChildItemTitle.trim().equalsIgnoreCase(getResources().getString(R.string.sponsor_downline))) {
                    startActivity(new Intent(Monthly_Incentive_Detail_Activity.this, Sponsor_team_details_Activity.class).putExtra("Action", "Sponsor"));
                } else if (ChildItemTitle.trim().equalsIgnoreCase(getResources().getString(R.string.bv_detail_report))) {
                    startActivity(new Intent(Monthly_Incentive_Detail_Activity.this, Sponsor_team_details_Activity.class).putExtra("Action", "Direct"));
                } else if (ChildItemTitle.trim().equalsIgnoreCase(getResources().getString(R.string.welcome_letter))) {
                    startActivity(new Intent(Monthly_Incentive_Detail_Activity.this, WelcomeLetter_Activity.class));
                } else if (ChildItemTitle.trim().equalsIgnoreCase(getResources().getString(R.string.purchase_reports))) {
                    startActivity(new Intent(Monthly_Incentive_Detail_Activity.this, Repurchase_BV_Detail.class));
                } else if (ChildItemTitle.trim().equalsIgnoreCase("Team Repurchase BV Summary")) {
                    startActivity(new Intent(Monthly_Incentive_Detail_Activity.this, Repurchase_BV_Summary_Team_Activity.class));
                } else if (ChildItemTitle.trim().equalsIgnoreCase("Repurchase Bill Summary")) {
                    startActivity(new Intent(Monthly_Incentive_Detail_Activity.this, Repurchase_Bill_Summary.class));
                } else if (ChildItemTitle.trim().equalsIgnoreCase(getResources().getString(R.string.wallet_detail))) {
                    startActivity(new Intent(Monthly_Incentive_Detail_Activity.this, Wallet_Transaction_Report_Activity.class));
                } else if (ChildItemTitle.trim().equalsIgnoreCase("Wallet Request Report")) {
                    startActivity(new Intent(Monthly_Incentive_Detail_Activity.this, Wallet_Request_Status_Report_Activity.class));
                } else if (ChildItemTitle.trim().equalsIgnoreCase("Request For Wallet Amount")) {
                    startActivity(new Intent(Monthly_Incentive_Detail_Activity.this, Wallet_Request_Amount_Activity.class));
                } else if (ChildItemTitle.trim().equalsIgnoreCase(getResources().getString(R.string.monthly_incentive))) {
                    startActivity(new Intent(Monthly_Incentive_Detail_Activity.this, Monthly_Incentive_Activity.class));
                } else if (ChildItemTitle.trim().equalsIgnoreCase(getResources().getString(R.string.monthly_incentive_detail_report))) {
                    startActivity(new Intent(Monthly_Incentive_Detail_Activity.this, Monthly_Incentive_Detail_Activity.class));
                } else if (ChildItemTitle.trim().equalsIgnoreCase("TDS Detail Report")) {
                    startActivity(new Intent(Monthly_Incentive_Detail_Activity.this, TDS_detail_report_Activity.class));
                } else if (ChildItemTitle.trim().equalsIgnoreCase("Update Profile")) {
                    startActivity(new Intent(Monthly_Incentive_Detail_Activity.this, Profile_Update_Activity.class));
                } else if (ChildItemTitle.trim().equalsIgnoreCase("View KYC Documents")) {
                    startActivity(new Intent(Monthly_Incentive_Detail_Activity.this, KYCUploadDocument_Activity.class).putExtra("HEADING", "View"));
                } else if (ChildItemTitle.trim().equalsIgnoreCase("Register Enquiry/Complaint")) {
                    startActivity(new Intent(Monthly_Incentive_Detail_Activity.this, Register_Complaint_Activity.class));
                } else if (ChildItemTitle.trim().equalsIgnoreCase("View Open/Pending Queries")) {
                    startActivity(new Intent(Monthly_Incentive_Detail_Activity.this, ViewPendingQueries_Activity.class).putExtra("HEADING", "Open"));
                } else if (ChildItemTitle.trim().equalsIgnoreCase("View Closed Queries")) {
                    startActivity(new Intent(Monthly_Incentive_Detail_Activity.this, ViewPendingQueries_Activity.class).putExtra("HEADING", "Closed"));
                } else if (ChildItemTitle.trim().equalsIgnoreCase("Product Wallet Detail")) {
                    startActivity(new Intent(Monthly_Incentive_Detail_Activity.this, ProductWallet_Transaction_Report_Activity.class));
                } else if (ChildItemTitle.trim().equalsIgnoreCase("Wallet To Bank Transfer Detail")) {
                    startActivity(new Intent(Monthly_Incentive_Detail_Activity.this, Wallet_Bank_Transfer_Report_Activity.class));
                } else if (ChildItemTitle.trim().equalsIgnoreCase("ID Card")) {
                    startActivity(new Intent(Monthly_Incentive_Detail_Activity.this, ID_card_Activity.class));
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

    private void executeWalletBalanceRequest() {
        try {
            if (AppUtils.isNetworkAvailable(Monthly_Incentive_Detail_Activity.this)) {
                new AsyncTask<Void, Void, String>() {
                    @Override
                    protected String doInBackground(Void... params) {
                        String response = "";
                        try {
                            List<NameValuePair> postParameters = new ArrayList<>();
                            postParameters.add(new BasicNameValuePair("Formno", AppController.getSpUserInfo().getString(SPUtils.USER_FORM_NUMBER, "")));
                            response = AppUtils.callWebServiceWithMultiParam(Monthly_Incentive_Detail_Activity.this,
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
                AppUtils.showProgressDialog(Monthly_Incentive_Detail_Activity.this);
            }

            @Override
            protected String doInBackground(Void... params) {

                Thread.currentThread().setPriority(Thread.MAX_PRIORITY);

                String response = "";
                try {
                    List<NameValuePair> postParameters = new ArrayList<>();
                    response = AppUtils.callWebServiceWithMultiParam(Monthly_Incentive_Detail_Activity.this, postParameters, QueryUtils.methodtoGetDrawerMenuItems, TAG);

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
    private void showViewMoreDailog(String payout, String month, String self_bv, String team_bv, String cm_total,
                                    String till_self_bv, String till_team_bv, String till_total_bv,
                                    String PerformanceBonusSlab, String PerformanceBonusRank, String PerformanceBonus,
                                    String team_a_bv, String team_b_bv, String team_c_bv, String PowerSideBV, String WeakerSideBV,
                                    String LoyalConsumerBonusPoint, String LoyalConsumerBonus,
                                    String ActualSmartBonusPoint, String SmartBonusPoint, String SmartBonus,
                                    String DirectorBonusPoint, String DirectorBonus, String RoyaltyBonusPoint, String RoyaltyBonus,
                                    String TravelFundStatus, String TravelFund, String CarFundStatus, String CarFund,
                                    String HouseFundStatus, String HouseFund, String MentorShipBonus, String GrossIncentive,
                                    String TDSAmount, String AdminCharge, String PreviousBalance, String ChequeAmount, String ClosingBalance) {
        try {
            final Dialog dialog = new Dialog(this, R.style.ThemeDialogCustom);
            dialog.getWindow().getAttributes().windowAnimations = R.style.dialog_animation;
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setCancelable(false);
            dialog.setContentView(R.layout.custom_dialog_view_more_incentive_detail);


            TextView txt_payout = dialog.findViewById(R.id.txt_payout);
            TextView txt_month = dialog.findViewById(R.id.txt_month);
            TextView txt_self_bv = dialog.findViewById(R.id.txt_self_bv);
            TextView txt_team_bv = dialog.findViewById(R.id.txt_team_bv);
            TextView txt_cm_total = dialog.findViewById(R.id.txt_cm_total);

            TextView txt_till_self_bv = dialog.findViewById(R.id.txt_till_self_bv);
            TextView txt_till_team_bv = dialog.findViewById(R.id.txt_till_team_bv);
            TextView txt_till_total_bv = dialog.findViewById(R.id.txt_till_total_bv);

            TextView txt_PerformanceBonusSlab = dialog.findViewById(R.id.txt_PerformanceBonusSlab);
            TextView txt_PerformanceBonusRank = dialog.findViewById(R.id.txt_PerformanceBonusRank);
            TextView txt_PerformanceBonus = dialog.findViewById(R.id.txt_PerformanceBonus);

            TextView txt_team_a_bv = dialog.findViewById(R.id.txt_team_a_bv);
            TextView txt_team_b_bv = dialog.findViewById(R.id.txt_team_b_bv);
            TextView txt_team_c_bv = dialog.findViewById(R.id.txt_team_c_bv);
            TextView txt_PowerSideBV = dialog.findViewById(R.id.txt_PowerSideBV);
            TextView txt_WeakerSideBV = dialog.findViewById(R.id.txt_WeakerSideBV);

            TextView txt_LoyalConsumerBonusPoint = dialog.findViewById(R.id.txt_LoyalConsumerBonusPoint);
            TextView txt_LoyalConsumerBonus = dialog.findViewById(R.id.txt_LoyalConsumerBonus);

            TextView txt_ActualSmartBonusPoint = dialog.findViewById(R.id.txt_ActualSmartBonusPoint);
            TextView txt_SmartBonusPoint = dialog.findViewById(R.id.txt_SmartBonusPoint);
            TextView txt_SmartBonus = dialog.findViewById(R.id.txt_SmartBonus);
            TextView txt_DirectorBonusPoint = dialog.findViewById(R.id.txt_DirectorBonusPoint);
            TextView txt_DirectorBonus = dialog.findViewById(R.id.txt_DirectorBonus);
            TextView txt_RoyaltyBonusPoint = dialog.findViewById(R.id.txt_RoyaltyBonusPoint);
            TextView txt_RoyaltyBonus = dialog.findViewById(R.id.txt_RoyaltyBonus);
            TextView txt_TravelFundStatus = dialog.findViewById(R.id.txt_TravelFundStatus);
            TextView txt_TravelFund = dialog.findViewById(R.id.txt_TravelFund);

            TextView txt_CarFundStatus = dialog.findViewById(R.id.txt_CarFundStatus);
            TextView txt_CarFund = dialog.findViewById(R.id.txt_CarFund);

            TextView txt_HouseFundStatus = dialog.findViewById(R.id.txt_HouseFundStatus);
            TextView txt_HouseFund = dialog.findViewById(R.id.txt_HouseFund);

            TextView txt_MentorShipBonus = dialog.findViewById(R.id.txt_MentorShipBonus);
            TextView txt_GrossIncentive = dialog.findViewById(R.id.txt_GrossIncentive);
            TextView txt_TDSAmount = dialog.findViewById(R.id.txt_TDSAmount);
            TextView txt_AdminCharge = dialog.findViewById(R.id.txt_AdminCharge);
            TextView txt_PreviousBalance = dialog.findViewById(R.id.txt_PreviousBalance);
            TextView txt_ChequeAmount = dialog.findViewById(R.id.txt_ChequeAmount);
            TextView txt_ClosingBalance = dialog.findViewById(R.id.txt_ClosingBalance);

            txt_payout.setText(payout);
            txt_month.setText(month);
            txt_self_bv.setText(self_bv);
            txt_team_bv.setText(team_bv);
            txt_cm_total.setText(cm_total);
            txt_till_self_bv.setText(till_self_bv);
            txt_till_team_bv.setText(String.format("%.0f", Double.parseDouble(till_team_bv)));
            txt_till_total_bv.setText(String.format("%.0f", Double.parseDouble(till_total_bv)));
            txt_PerformanceBonusSlab.setText(PerformanceBonusSlab);
            txt_PerformanceBonusRank.setText(PerformanceBonusRank);
            txt_PerformanceBonus.setText(PerformanceBonus);
            txt_team_a_bv.setText(team_a_bv);
            txt_team_b_bv.setText(team_b_bv);
            txt_team_c_bv.setText(team_c_bv);
            txt_PowerSideBV.setText(PowerSideBV);
            txt_WeakerSideBV.setText(WeakerSideBV);
            txt_LoyalConsumerBonusPoint.setText(LoyalConsumerBonusPoint);
            txt_LoyalConsumerBonus.setText(LoyalConsumerBonus);
            txt_ActualSmartBonusPoint.setText(ActualSmartBonusPoint);
            txt_SmartBonusPoint.setText(SmartBonusPoint);
            txt_SmartBonus.setText(SmartBonus);
            txt_DirectorBonusPoint.setText(DirectorBonusPoint);
            txt_DirectorBonus.setText(DirectorBonus);
            txt_RoyaltyBonusPoint.setText(RoyaltyBonusPoint);
            txt_RoyaltyBonus.setText(RoyaltyBonus);
            txt_TravelFundStatus.setText(TravelFundStatus);
            txt_TravelFund.setText(TravelFund);
            txt_CarFundStatus.setText(CarFundStatus);
            txt_CarFund.setText(CarFund);
            txt_HouseFundStatus.setText(HouseFundStatus);
            txt_HouseFund.setText(HouseFund);
            txt_MentorShipBonus.setText(MentorShipBonus);
            txt_GrossIncentive.setText(GrossIncentive);
            txt_TDSAmount.setText(TDSAmount);
            txt_AdminCharge.setText(AdminCharge);
            txt_PreviousBalance.setText(PreviousBalance);
            txt_ChequeAmount.setText(ChequeAmount);
            txt_ClosingBalance.setText(ClosingBalance);


            dialog.findViewById(R.id.txt_submit).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                }
            });

            dialog.findViewById(R.id.textView3).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                }
            });

            dialog.show();

        } catch (Exception e) {
            e.printStackTrace();
            AppUtils.showExceptionDialog(this);
        }
    }


}
