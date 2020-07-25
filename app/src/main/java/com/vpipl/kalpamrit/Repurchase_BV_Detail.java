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
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.DatePicker;
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

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Repurchase_BV_Detail extends AppCompatActivity {

    private String TAG = "Repurchase_BV_Detail";
    private TextView txt_from_joining;
    private TextView txt_to_joining;
    private TextView txt_self_bv;
    private TextView txt_team_bv;
    private TextView txt_total_bv;
    private TextView txt_count;
    private Button btn_proceed;
    private Button btn_load_more;

    private TableLayout displayLinear;

    private Calendar myCalendar;
    private SimpleDateFormat sdf;
    private String whichdate = "";

    private final DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {

        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {

            myCalendar.set(Calendar.YEAR, year);
            myCalendar.set(Calendar.MONTH, monthOfYear);
            myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

            if (new Date().after(myCalendar.getTime())) {

                if (whichdate.equalsIgnoreCase("txt_from_joining"))
                    txt_from_joining.setText(sdf.format(myCalendar.getTime()));
                else if (whichdate.equalsIgnoreCase("txt_to_joining"))
                    txt_to_joining.setText(sdf.format(myCalendar.getTime()));

            } else {

                AppUtils.alertDialog(Repurchase_BV_Detail.this, "Selected Date Can't be After today");
            }
        }
    };
    private int TopRows = 25;
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
    private DatePickerDialog datePickerDialog;


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
                    startActivity(new Intent(Repurchase_BV_Detail.this, Login_Activity.class));
                else
                    AppUtils.showDialogSignOut(Repurchase_BV_Detail.this);
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
        setContentView(R.layout.activity_repurchase_bv_detail);

        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
        SetupToolbar();
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        txt_from_joining = findViewById(R.id.txt_from_joining);
        txt_to_joining = findViewById(R.id.txt_to_joining);
        txt_self_bv = findViewById(R.id.txt_self_bv);
        txt_team_bv = findViewById(R.id.txt_team_bv);

        txt_total_bv = findViewById(R.id.txt_total_bv);

        txt_count = findViewById(R.id.txt_count);

        btn_proceed = findViewById(R.id.btn_proceed);
        btn_load_more = findViewById(R.id.btn_load_more);

        displayLinear = findViewById(R.id.displayLinear);

        btn_load_more.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                TopRows = TopRows + 25;
                createRepurchaseBvDetailMy();

            }
        });

        btn_proceed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TopRows = 25;
                createRepurchaseBvDetailMy();
            }
        });

        myCalendar = Calendar.getInstance();
        sdf = new SimpleDateFormat("dd MMM yyyy");
        txt_to_joining.setText(sdf.format(myCalendar.getTime()));

        Calendar c = Calendar.getInstance();
        c.set(Calendar.DAY_OF_MONTH, 1);
        txt_from_joining.setText(sdf.format(c.getTime()));

        txt_from_joining.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                whichdate = "txt_from_joining";
                showdatePicker();
            }
        });


        txt_to_joining.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                whichdate = "txt_to_joining";
                showdatePicker();
            }
        });

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
                        startActivity(new Intent(Repurchase_BV_Detail.this, Profile_View_Activity.class));
                    } else {
                        startActivity(new Intent(Repurchase_BV_Detail.this, Login_Activity.class));
                    }

                    if (drawer.isDrawerOpen(GravityCompat.START)) {
                        drawer.closeDrawer(GravityCompat.START);
                    }
                }
            }
        });
    }

    private void showdatePicker() {
        Calendar calendar = Calendar.getInstance();
        datePickerDialog = new DatePickerDialog(this, date, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
        datePickerDialog.getDatePicker().setMaxDate(calendar.getTime().getTime());
        datePickerDialog.show();
    }


    private void executeLoginRequest() {
        try {

            if (AppUtils.isNetworkAvailable(Repurchase_BV_Detail.this)) {
                new AsyncTask<Void, Void, String>() {
                    protected void onPreExecute() {
                        AppUtils.showProgressDialog(Repurchase_BV_Detail.this);
                    }

                    @Override
                    protected String doInBackground(Void... params) {
                        String response = "";
                        try {

                            List<NameValuePair> postParameters = new ArrayList<>();
                            postParameters.add(new BasicNameValuePair("UserID", AppController.getSpUserInfo().getString(SPUtils.USER_ID_NUMBER, "")));
                            postParameters.add(new BasicNameValuePair("Password", AppController.getSpUserInfo().getString(SPUtils.USER_PASSWORD, "")));
                            postParameters.add(new BasicNameValuePair("UserType", "D"));
                            response = AppUtils.callWebServiceWithMultiParam(Repurchase_BV_Detail.this, postParameters, QueryUtils.methodMemberLoginOnPortal, TAG);

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

                                Toast.makeText(Repurchase_BV_Detail.this, "Please Login to continue..", Toast.LENGTH_SHORT).show();

                                AppController.getSpUserInfo().edit().clear().commit();
                                AppController.getSpIsLogin().edit().clear().commit();

                                Intent intent = new Intent(Repurchase_BV_Detail.this, Login_Activity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                intent.putExtra("SendToHome", true);
                                startActivity(intent);
                                finish();

                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            AppUtils.showExceptionDialog(Repurchase_BV_Detail.this);
                        }
                    }
                }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            }
        } catch (Exception e) {
            e.printStackTrace();
            AppUtils.showExceptionDialog(Repurchase_BV_Detail.this);
        }
    }

    public void continueapp() {

        createRepurchaseBvDetailMy();

        enableExpandableList();
        LoadNavigationHeaderItems();
    }

    private void createRepurchaseBvDetailMy() {

        findViewById(R.id.ll_showData).setVisibility(View.GONE);
        findViewById(R.id.HSV).setVisibility(View.GONE);
        findViewById(R.id.LL_count).setVisibility(View.GONE);

        List<NameValuePair> postParameters = new ArrayList<>();
        postParameters.add(new BasicNameValuePair("Formno", AppController.getSpUserInfo().getString(SPUtils.USER_FORM_NUMBER, "")));
        postParameters.add(new BasicNameValuePair("TopRows", "" + TopRows));
        postParameters.add(new BasicNameValuePair("FromJD", "" + txt_from_joining.getText().toString()));
        postParameters.add(new BasicNameValuePair("ToJD", "" + txt_to_joining.getText().toString()));
        executeRepurchaseBvDetailMy(postParameters);
    }

    private void executeRepurchaseBvDetailMy(final List postparameters) {
        try {
            if (AppUtils.isNetworkAvailable(Repurchase_BV_Detail.this)) {
                new AsyncTask<Void, Void, String>() {
                    protected void onPreExecute() {
                        AppUtils.showProgressDialog(Repurchase_BV_Detail.this);
                    }

                    @Override
                    protected String doInBackground(Void... params) {
                        String response = "";
                        try {
                            response = AppUtils.callWebServiceWithMultiParam(Repurchase_BV_Detail.this, postparameters,
                                    QueryUtils.methodToGetMyRepurchaseBVDetail, TAG);

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

                            if (jsonObject.getString("Message").equalsIgnoreCase("Successfully.!")) {
                                JSONArray jsonArrayBVCount = jsonObject.getJSONArray("BVCount");
                                JSONArray jsonArrayBVDetails = jsonObject.getJSONArray("BVDetails");

                                if (jsonObject.getString("Status").equalsIgnoreCase("True")) {
                                    WriteValues(jsonArrayBVCount, jsonArrayBVDetails);

                                } else {
                                    AppUtils.alertDialog(Repurchase_BV_Detail.this, jsonObject.getString("Message"));
                                }
                            } else {
                                AppUtils.alertDialog(Repurchase_BV_Detail.this, jsonObject.getString("Message"));
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            AppUtils.showExceptionDialog(Repurchase_BV_Detail.this);
                        }
                    }
                }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            }
        } catch (Exception e) {
            e.printStackTrace();
            AppUtils.showExceptionDialog(Repurchase_BV_Detail.this);
        }
    }

    private void WriteValues(final JSONArray count, final JSONArray jarray) {
        try {

            findViewById(R.id.ll_showData).setVisibility(View.VISIBLE);

            String text = "(Showing " + jarray.length() + " records)";
            txt_count.setText(text);

            if (jarray.length() > 0) {
                findViewById(R.id.HSV).setVisibility(View.VISIBLE);
                findViewById(R.id.LL_count).setVisibility(View.VISIBLE);
            }

            float sp = 8;
            int px = (int) (sp * getResources().getDisplayMetrics().scaledDensity);

            JSONObject jsonObject = count.getJSONObject(0);

            DecimalFormat df = new DecimalFormat("#.###");

            txt_self_bv.setText(df.format(jsonObject.getDouble("SRepurchase")));
            txt_team_bv.setText(df.format(jsonObject.getDouble("DRepurchase")));
            txt_total_bv.setText(df.format(jsonObject.getDouble("TotalBV")));

            TableLayout ll = findViewById(R.id.displayLinear);
            ll.removeAllViews();

            Typeface typeface = ResourcesCompat.getFont(this,R.font.gisha_0);

            TableRow row1 = new TableRow(this);

            TableRow.LayoutParams lp1 = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT);
            row1.setLayoutParams(lp1);
            row1.setBackgroundColor(getResources().getColor(R.color.color_green_text));

//            TextView A1 = new TextView(this);
//            TextView B1 = new TextView(this);
            TextView C1 = new TextView(this);
            TextView D1 = new TextView(this);
            TextView E1 = new TextView(this);
            TextView F1 = new TextView(this);
            TextView G1 = new TextView(this);
            TextView H1 = new TextView(this);

//            A1.setText("ID No.");
//            B1.setText("Member Name");
            C1.setText("Bill Number");
            D1.setText("Bill Date");
            E1.setText("Bill Type");
            F1.setText("Bill Amount");
            G1.setText("Repurchase BV");
            H1.setText("Remarks");

            C1.setTypeface(typeface);
            D1.setTypeface(typeface);
            E1.setTypeface(typeface);
            F1.setTypeface(typeface);
            G1.setTypeface(typeface);
            H1.setTypeface(typeface);

//            A1.setPadding(px, px, px, px);
//            B1.setPadding(px, px, px, px);
            C1.setPadding(px, px, px, px);
            D1.setPadding(px, px, px, px);
            E1.setPadding(px, px, px, px);
            F1.setPadding(px, px, px, px);
            G1.setPadding(px, px, px, px);
            H1.setPadding(px, px, px, px);

//            A1.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
//            B1.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
            C1.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
            D1.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
            E1.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
            F1.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
            G1.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
            H1.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);

//            A1.setGravity(Gravity.CENTER);
//            B1.setGravity(Gravity.CENTER);
            C1.setGravity(Gravity.CENTER);
            D1.setGravity(Gravity.CENTER);
            E1.setGravity(Gravity.CENTER);
            F1.setGravity(Gravity.CENTER);
            G1.setGravity(Gravity.CENTER);
            H1.setGravity(Gravity.CENTER);

//            A1.setTextColor(Color.WHITE);
//            B1.setTextColor(Color.WHITE);
            C1.setTextColor(Color.WHITE);
            D1.setTextColor(Color.WHITE);
            E1.setTextColor(Color.WHITE);
            F1.setTextColor(Color.WHITE);
            G1.setTextColor(Color.WHITE);
            H1.setTextColor(Color.WHITE);

//            row1.addView(A1);
//            row1.addView(B1);
            row1.addView(C1);
            row1.addView(D1);
            row1.addView(E1);
            row1.addView(F1);
            row1.addView(G1);
            row1.addView(H1);

            View view = new View(this);
            view.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 1));
            view.setBackgroundColor(Color.parseColor("#cccccc"));

            ll.addView(row1);
            ll.addView(view);

            for (int i = 0; i < jarray.length(); i++) {
                try {
                    JSONObject jobject = jarray.getJSONObject(i);

//                    String member_id = jobject.getString("IdNo");
//                    String MemName = WordUtils.capitalizeFully(jobject.getString("MemName"));
                    String BillNo = jobject.getString("BillNo");
                    String BillDate = WordUtils.capitalizeFully(jobject.getString("BillDate"));
                    String TranType = jobject.getString("TranType");
                    String Amount = jobject.getString("Amount");
                    String BV = jobject.getString("BV");
                    String Remarks = WordUtils.capitalizeFully(jobject.getString("Remarks"));

                    StringBuilder sb = new StringBuilder(Remarks);

                    int ii = 0;
                    while ((ii = sb.indexOf(" ", ii + 11)) != -1) {
                        sb.replace(ii, ii + 1, "\n");
                    }

                    TableRow row = new TableRow(this);
                    TableRow.LayoutParams lp = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT);
                    row.setLayoutParams(lp);

                    if (i % 2 == 0)
                        row.setBackgroundColor(Color.WHITE);
                    else
                        row.setBackgroundColor(Color.parseColor("#dddddd"));


//                    TextView A = new TextView(this);
//                    TextView B = new TextView(this);
                    TextView C = new TextView(this);
                    TextView D = new TextView(this);
                    TextView E = new TextView(this);
                    TextView F = new TextView(this);
                    TextView G = new TextView(this);
                    TextView H = new TextView(this);

//                    A.setText(member_id);
//                    B.setText(MemName);
                    C.setText(BillNo);
                    D.setText(AppUtils.getDateFromAPIDate(BillDate));
                    E.setText(TranType);
                    F.setText(Amount);
                    G.setText(BV);
                    H.setText(sb.toString());

                    C.setTypeface(typeface);
                    D.setTypeface(typeface);
                    E.setTypeface(typeface);
                    F.setTypeface(typeface);
                    G.setTypeface(typeface);
                    H.setTypeface(typeface);

//                    A.setGravity(Gravity.CENTER);
//                    B.setGravity(Gravity.CENTER);
                    C.setGravity(Gravity.CENTER);
                    D.setGravity(Gravity.CENTER);
                    E.setGravity(Gravity.CENTER);
                    F.setGravity(Gravity.CENTER);
                    G.setGravity(Gravity.CENTER);
                    H.setGravity(Gravity.CENTER);

//                    A.setPadding(px, px, px, px);
//                    B.setPadding(px, px, px, px);
                    C.setPadding(px, px, px, px);
                    D.setPadding(px, px, px, px);
                    E.setPadding(px, px, px, px);
                    F.setPadding(px, px, px, px);
                    G.setPadding(px, px, px, px);
                    H.setPadding(px, px, px, px);

//                    A.setTextSize(TypedValue.COMPLEX_UNIT_SP, 13);
//                    B.setTextSize(TypedValue.COMPLEX_UNIT_SP, 13);
                    C.setTextSize(TypedValue.COMPLEX_UNIT_SP, 13);
                    D.setTextSize(TypedValue.COMPLEX_UNIT_SP, 13);
                    E.setTextSize(TypedValue.COMPLEX_UNIT_SP, 13);
                    F.setTextSize(TypedValue.COMPLEX_UNIT_SP, 13);
                    G.setTextSize(TypedValue.COMPLEX_UNIT_SP, 13);
                    H.setTextSize(TypedValue.COMPLEX_UNIT_SP, 13);

//                    row.addView(A);
//                    row.addView(B);
                    row.addView(C);
                    row.addView(D);
                    row.addView(E);
                    row.addView(F);
                    row.addView(G);
                    row.addView(H);

                    View view_one = new View(this);
                    view_one.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 1));
                    view.setBackgroundColor(Color.parseColor("#cccccc"));

                    ll.addView(row);
                    ll.addView(view_one);

                } catch (Exception e) {
                    e.printStackTrace();
                }
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
            AppUtils.showExceptionDialog(Repurchase_BV_Detail.this);
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
                    startActivity(new Intent(Repurchase_BV_Detail.this, DashBoard_Activity.class));
                    if (drawer.isDrawerOpen(GravityCompat.START)) {
                        drawer.closeDrawer(GravityCompat.START);
                    }
                } else if (GroupTitle.trim().equalsIgnoreCase("Logout")) {
                    AppUtils.showDialogSignOut(Repurchase_BV_Detail.this);
                } else if (GroupTitle.trim().equalsIgnoreCase("New Joining")) {
                    startActivity(new Intent(Repurchase_BV_Detail.this, Register_Activity.class));
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
                    startActivity(new Intent(Repurchase_BV_Detail.this, Profile_View_Activity.class));
                } else if (ChildItemTitle.trim().equalsIgnoreCase(getResources().getString(R.string.change_password))) {
                    startActivity(new Intent(Repurchase_BV_Detail.this, Change_Password_Activity.class));
                } else if (ChildItemTitle.trim().equalsIgnoreCase(getResources().getString(R.string.new_joining))) {
                    startActivity(new Intent(Repurchase_BV_Detail.this, Register_Activity.class));
                } else if (ChildItemTitle.trim().equalsIgnoreCase(getResources().getString(R.string.upload_kyc))) {
                    startActivity(new Intent(Repurchase_BV_Detail.this, KYCUploadDocument_Activity.class).putExtra("HEADING", "Update"));
                } else if (ChildItemTitle.trim().equalsIgnoreCase(getResources().getString(R.string.generation_structure))) {
                    startActivity(new Intent(Repurchase_BV_Detail.this, Sponsor_genealogy_Activity.class));
                } else if (ChildItemTitle.trim().equalsIgnoreCase(getResources().getString(R.string.sponsor_downline))) {
                    startActivity(new Intent(Repurchase_BV_Detail.this, Sponsor_team_details_Activity.class).putExtra("Action", "Sponsor"));
                } else if (ChildItemTitle.trim().equalsIgnoreCase(getResources().getString(R.string.bv_detail_report))) {
                    startActivity(new Intent(Repurchase_BV_Detail.this, Sponsor_team_details_Activity.class).putExtra("Action", "Direct"));
                } else if (ChildItemTitle.trim().equalsIgnoreCase(getResources().getString(R.string.welcome_letter))) {
                    startActivity(new Intent(Repurchase_BV_Detail.this, WelcomeLetter_Activity.class));
                } else if (ChildItemTitle.trim().equalsIgnoreCase(getResources().getString(R.string.purchase_reports))) {
                    startActivity(new Intent(Repurchase_BV_Detail.this, Repurchase_BV_Detail.class));
                } else if (ChildItemTitle.trim().equalsIgnoreCase("Team Repurchase BV Summary")) {
                    startActivity(new Intent(Repurchase_BV_Detail.this, Repurchase_BV_Summary_Team_Activity.class));
                } else if (ChildItemTitle.trim().equalsIgnoreCase("Repurchase Bill Summary")) {
                    startActivity(new Intent(Repurchase_BV_Detail.this, Repurchase_Bill_Summary.class));
                } else if (ChildItemTitle.trim().equalsIgnoreCase(getResources().getString(R.string.wallet_detail))) {
                    startActivity(new Intent(Repurchase_BV_Detail.this, Wallet_Transaction_Report_Activity.class));
                } else if (ChildItemTitle.trim().equalsIgnoreCase("Wallet Request Report")) {
                    startActivity(new Intent(Repurchase_BV_Detail.this, Wallet_Request_Status_Report_Activity.class));
                } else if (ChildItemTitle.trim().equalsIgnoreCase("Request For Wallet Amount")) {
                    startActivity(new Intent(Repurchase_BV_Detail.this, Wallet_Request_Amount_Activity.class));
                } else if (ChildItemTitle.trim().equalsIgnoreCase(getResources().getString(R.string.monthly_incentive))) {
                    startActivity(new Intent(Repurchase_BV_Detail.this, Monthly_Incentive_Activity.class));
                } else if (ChildItemTitle.trim().equalsIgnoreCase(getResources().getString(R.string.monthly_incentive_detail_report))) {
                    startActivity(new Intent(Repurchase_BV_Detail.this, Monthly_Incentive_Detail_Activity.class));
                } else if (ChildItemTitle.trim().equalsIgnoreCase("TDS Detail Report")) {
                    startActivity(new Intent(Repurchase_BV_Detail.this, TDS_detail_report_Activity.class));
                } else if (ChildItemTitle.trim().equalsIgnoreCase("Update Profile")) {
                    startActivity(new Intent(Repurchase_BV_Detail.this, Profile_Update_Activity.class));
                } else if (ChildItemTitle.trim().equalsIgnoreCase("View KYC Documents")) {
                    startActivity(new Intent(Repurchase_BV_Detail.this, KYCUploadDocument_Activity.class).putExtra("HEADING", "View"));
                } else if (ChildItemTitle.trim().equalsIgnoreCase("Register Enquiry/Complaint")) {
                    startActivity(new Intent(Repurchase_BV_Detail.this, Register_Complaint_Activity.class));
                } else if (ChildItemTitle.trim().equalsIgnoreCase("View Open/Pending Queries")) {
                    startActivity(new Intent(Repurchase_BV_Detail.this, ViewPendingQueries_Activity.class).putExtra("HEADING", "Open"));
                } else if (ChildItemTitle.trim().equalsIgnoreCase("View Closed Queries")) {
                    startActivity(new Intent(Repurchase_BV_Detail.this, ViewPendingQueries_Activity.class).putExtra("HEADING", "Closed"));
                } else if (ChildItemTitle.trim().equalsIgnoreCase("Product Wallet Detail")) {
                    startActivity(new Intent(Repurchase_BV_Detail.this, ProductWallet_Transaction_Report_Activity.class));
                } else if (ChildItemTitle.trim().equalsIgnoreCase("Wallet To Bank Transfer Detail")) {
                    startActivity(new Intent(Repurchase_BV_Detail.this, Wallet_Bank_Transfer_Report_Activity.class));
                }else if (ChildItemTitle.trim().equalsIgnoreCase("ID Card")) {
                    startActivity(new Intent(Repurchase_BV_Detail.this, ID_card_Activity.class));
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
            if (AppUtils.isNetworkAvailable(Repurchase_BV_Detail.this)) {
                new AsyncTask<Void, Void, String>() {
                    @Override
                    protected String doInBackground(Void... params) {
                        String response = "";
                        try {
                            List<NameValuePair> postParameters = new ArrayList<>();
                            postParameters.add(new BasicNameValuePair("Formno", AppController.getSpUserInfo().getString(SPUtils.USER_FORM_NUMBER, "")));
                            response = AppUtils.callWebServiceWithMultiParam(Repurchase_BV_Detail.this,
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
                AppUtils.showProgressDialog(Repurchase_BV_Detail.this);
            }

            @Override
            protected String doInBackground(Void... params) {

                Thread.currentThread().setPriority(Thread.MAX_PRIORITY);

                String response = "";
                try {
                    List<NameValuePair> postParameters = new ArrayList<>();
                    response = AppUtils.callWebServiceWithMultiParam(Repurchase_BV_Detail.this, postParameters, QueryUtils.methodtoGetDrawerMenuItems, TAG);

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
