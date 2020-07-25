package com.vpipl.kalpamrit;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TextInputEditText;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
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

public class Repurchase_Bill_Summary extends AppCompatActivity {

    private String TAG = "Repurchase_Bill_Summary";
    private TextView txt_from_joining;
    private TextView txt_to_joining;
    private TextView txt_self_bv;
    private TextView txt_team_bv;
    private TextView txt_total_bv;
    private TextView txt_count;
    private Button btn_proceed;
    private Button btn_load_more;

    private TextInputEditText txt_search_option;
    private TextInputEditText txt_search_keyword;

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
                AppUtils.alertDialog(Repurchase_Bill_Summary.this, "Selected Date Can't be After today");
            }
        }
    };
    private int TopRows = 25;



    private DatePickerDialog datePickerDialog;


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

        img_login_logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!AppController.getSpIsLogin().getBoolean(SPUtils.IS_LOGIN, false))
                    startActivity(new Intent(Repurchase_Bill_Summary.this, Login_Activity.class));
                else
                    AppUtils.showDialogSignOut(Repurchase_Bill_Summary.this);
            }
        });

        if (AppController.getSpIsLogin().getBoolean(SPUtils.IS_LOGIN, false))
            img_login_logout.setImageDrawable(getResources().getDrawable(R.drawable.icon_logout_orange));
        else
            img_login_logout.setImageDrawable(getResources().getDrawable(R.drawable.icon_distributor_login_orange));
    }
            TextView txt_heading ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_repurchase_bill_summary);

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
        txt_heading = findViewById(R.id.txt_heading);

        txt_heading.setText("Repurchase Bill Summary");

        txt_total_bv = findViewById(R.id.txt_total_bv);

        txt_count = findViewById(R.id.txt_count);

        btn_proceed = findViewById(R.id.btn_proceed);
        btn_load_more = findViewById(R.id.btn_load_more);

        txt_search_option = findViewById(R.id.txt_search_option);
        txt_search_keyword = findViewById(R.id.txt_search_keyword);

        displayLinear = findViewById(R.id.displayLinear);

        btn_load_more.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                TopRows = TopRows + 25;
                createRepurchaseBillSummary();

            }
        });

        btn_proceed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TopRows = 25;
                createRepurchaseBillSummary();
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
            continueapp();
        } else {
            AppUtils.alertDialog(this, getResources().getString(R.string.txt_networkAlert));
        }

        txt_search_option.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog();
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

            if (AppUtils.isNetworkAvailable(Repurchase_Bill_Summary.this)) {
                new AsyncTask<Void, Void, String>() {
                    protected void onPreExecute() {
                        AppUtils.showProgressDialog(Repurchase_Bill_Summary.this);
                    }

                    @Override
                    protected String doInBackground(Void... params) {
                        String response = "";
                        try {

                            List<NameValuePair> postParameters = new ArrayList<>();
                            postParameters.add(new BasicNameValuePair("UserID", AppController.getSpUserInfo().getString(SPUtils.USER_ID_NUMBER, "")));
                            postParameters.add(new BasicNameValuePair("Password", AppController.getSpUserInfo().getString(SPUtils.USER_PASSWORD, "")));
                            postParameters.add(new BasicNameValuePair("UserType", "D"));
                            response = AppUtils.callWebServiceWithMultiParam(Repurchase_Bill_Summary.this, postParameters, QueryUtils.methodMemberLoginOnPortal, TAG);

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

                            } else {

                                Toast.makeText(Repurchase_Bill_Summary.this, "Please Login to continue..", Toast.LENGTH_SHORT).show();

                                AppController.getSpUserInfo().edit().clear().commit();
                                AppController.getSpIsLogin().edit().clear().commit();

                                Intent intent = new Intent(Repurchase_Bill_Summary.this, Login_Activity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                intent.putExtra("SendToHome", true);
                                startActivity(intent);
                                finish();

                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            AppUtils.showExceptionDialog(Repurchase_Bill_Summary.this);
                        }
                    }
                }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            }
        } catch (Exception e) {
            e.printStackTrace();
            AppUtils.showExceptionDialog(Repurchase_Bill_Summary.this);
        }
    }

    public void continueapp() {

        createRepurchaseBillSummary();
    }

    private void createRepurchaseBillSummary() {

        findViewById(R.id.ll_showData).setVisibility(View.GONE);
        findViewById(R.id.HSV).setVisibility(View.GONE);
        findViewById(R.id.LL_count).setVisibility(View.GONE);

        String SearchEntity = txt_search_option.getText().toString().trim();

        if (SearchEntity.equalsIgnoreCase("Purchase From"))
            SearchEntity = "PartyName";
        else if(SearchEntity.equalsIgnoreCase("Order Number"))
            SearchEntity = "OrderNo";


        List<NameValuePair> postParameters = new ArrayList<>();
        postParameters.add(new BasicNameValuePair("IDNo", AppController.getSpUserInfo().getString(SPUtils.USER_ID_NUMBER, "")));
        postParameters.add(new BasicNameValuePair("TopRows", "" + TopRows));
        postParameters.add(new BasicNameValuePair("FromDate", "" + txt_from_joining.getText().toString()));
        postParameters.add(new BasicNameValuePair("ToDate", "" + txt_to_joining.getText().toString()));
        postParameters.add(new BasicNameValuePair("SearchValue", "" + txt_search_keyword.getText().toString()));
        postParameters.add(new BasicNameValuePair("SearchEntity", "" + SearchEntity));
        executeRepurchaseBillSummary(postParameters);
    }

    private void executeRepurchaseBillSummary(final List postparameters) {
        try {
            if (AppUtils.isNetworkAvailable(Repurchase_Bill_Summary.this)) {
                new AsyncTask<Void, Void, String>() {
                    protected void onPreExecute() {
                        AppUtils.showProgressDialog(Repurchase_Bill_Summary.this);
                    }

                    @Override
                    protected String doInBackground(Void... params) {
                        String response = "";
                        try {
                            response = AppUtils.callWebServiceWithMultiParam(Repurchase_Bill_Summary.this, postparameters,
                                    QueryUtils.methodToGetRepurchaseBillSummary, TAG);

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
                                JSONArray jsonArrayBVDetails = jsonObject.getJSONArray("Data");

                                if (jsonObject.getString("Status").equalsIgnoreCase("True")) {
                                    WriteValues( jsonArrayBVDetails);

                                } else {
                                    AppUtils.alertDialog(Repurchase_Bill_Summary.this, jsonObject.getString("Message"));
                                }
                            } else {
                                AppUtils.alertDialog(Repurchase_Bill_Summary.this, jsonObject.getString("Message"));
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            AppUtils.showExceptionDialog(Repurchase_Bill_Summary.this);
                        }
                    }
                }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            }
        } catch (Exception e) {
            e.printStackTrace();
            AppUtils.showExceptionDialog(Repurchase_Bill_Summary.this);
        }
    }

    private void WriteValues(final JSONArray jarray) {
        try {

            findViewById(R.id.ll_showData).setVisibility(View.VISIBLE);
            findViewById(R.id.HSV).setVisibility(View.VISIBLE);

            String text = "(Showing " + jarray.length() + " records)";

            float sp = 8;
            int px = (int) (sp * getResources().getDisplayMetrics().scaledDensity);

            DecimalFormat df = new DecimalFormat("#.###");

            TableLayout ll = findViewById(R.id.displayLinear);
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

            A1.setText("Purchase From");
            B1.setText("Bill Type");
            C1.setText("Order No");
            D1.setText("Bill No");
            E1.setText("Bill Date");
            F1.setText("B.V.Value");
            G1.setText("Amount");
            H1.setText("CGST \nAmount");
            I1.setText("SGST \nunt");
            J1.setText("IGST \nAmount");
            K1.setText("Net Payable");
            L1.setText("Pay Mode");

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
            row1.addView(K1);
            row1.addView(L1);

            View view = new View(this);
            view.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 1));
            view.setBackgroundColor(Color.parseColor("#cccccc"));

            ll.addView(row1);
            ll.addView(view);

            for (int i = 0; i < jarray.length(); i++) {
                try {
                    JSONObject jobject = jarray.getJSONObject(i);

                    String PurchaseFrom = jobject.getString("PartyName");
                    String BillType = jobject.getString("BillType");
                    String OrderNo = jobject.getString("OrderNo");
                    final String BillNo = jobject.getString("BillNo");
                    String BillDate = WordUtils.capitalizeFully(jobject.getString("DispBillDate"));
                    String BVValue = jobject.getString("BVValue");
                    String Amount = jobject.getString("Amount");
                    String CGSTAmount = jobject.getString("CGSTAmount");
                    String SGSTAmount = jobject.getString("TaxAmount");
                    String IGSTAmount = jobject.getString("IGSTAmount");
                    String NetAmt = jobject.getString("NetAmt");
                    String PayMode = jobject.getString("PayMode");

                    TableRow row = new TableRow(this);
                    TableRow.LayoutParams lp = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT);
                    row.setLayoutParams(lp);

                    if (i % 2 == 0)
                        row.setBackgroundColor(Color.WHITE);
                    else
                        row.setBackgroundColor(Color.parseColor("#dddddd"));

                    TextView A = new TextView(this);
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
                    TextView L = new TextView(this);
                    final TextView M = new TextView(this);

                    A.setText(PurchaseFrom);
                    B.setText(BillType);
                    C.setText(OrderNo);
                    D.setText(BillNo);
                    E.setText(AppUtils.getDateFromAPIDate(BillDate));
                    F.setText(BVValue);
                    G.setText(Amount);
                    H.setText(CGSTAmount);
                    I.setText(SGSTAmount);
                    J.setText(IGSTAmount);
                    K.setText(NetAmt);
                    L.setText(PayMode);
                    M.setText("Invoice Print");
                 //   M.setId(BillNo);

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
                    M.setTypeface(typeface);

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
                    M.setGravity(Gravity.CENTER);

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
                    M.setPadding(px, px, px, px);

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
                    M.setTextSize(TypedValue.COMPLEX_UNIT_SP, 13);

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
                    row.addView(K);
                    row.addView(L);
                    row.addView(M);

                    View view_one = new View(this);
                    view_one.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 1));
                    view.setBackgroundColor(Color.parseColor("#cccccc"));

                    M.setTextColor(getResources().getColor(R.color.color_green_text));

                    TableRow.LayoutParams params = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT);
                    params.setMargins(px, 0, px, 0);


                    M.setLayoutParams(params);

                    M.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {

                         //   executeEncryptPayoutNoRequest("" + M.getId());
                            executeEncryptPayoutNoRequest("" + BillNo );
                        }
                    });

                    ll.addView(row);
//                    if (i < jarray.length() - 1)
                    ll.addView(view_one);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void showDialog() {
        try {
            final String[] stateArray = {"Purchase From", "Order Number"};

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Select Search Option");
            builder.setItems(stateArray, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int item) {

                    txt_search_option.setText(stateArray[item]);

                    if (stateArray[item].equalsIgnoreCase("Purchase From"))
                        txt_search_keyword.setHint("Enter Name");
                    else
                        txt_search_keyword.setHint("Enter Order Number");

                }
            });
            builder.create().show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void executeEncryptPayoutNoRequest(final String payoutno) {
        try {
            if (AppUtils.isNetworkAvailable(Repurchase_Bill_Summary.this)) {
                new AsyncTask<Void, Void, String>() {
                    protected void onPreExecute() {
                        AppUtils.showProgressDialog(Repurchase_Bill_Summary.this);
                    }

                    @Override
                    protected String doInBackground(Void... params) {
                        String response = "";
                        try {
                            List<NameValuePair> postParameters = new ArrayList<>();
                            postParameters.add(new BasicNameValuePair("BillNo", payoutno));
                            response = AppUtils.callWebServiceWithMultiParam(Repurchase_Bill_Summary.this, postParameters, QueryUtils.methodToWebEncryption, TAG);

                        } catch (Exception e) {
                            e.printStackTrace();
                            AppUtils.showExceptionDialog(Repurchase_Bill_Summary.this);
                        }
                        return response;
                    }

                    @Override
                    protected void onPostExecute(String resultData) {
                        AppUtils.dismissProgressDialog();

                        try {
                            JSONObject jsonObject = new JSONObject(resultData);

                            if (jsonObject.getString("Status").equalsIgnoreCase("True")) {
                                String encryptedFormno = jsonObject.getString("Message");
                                String path = AppUtils.InvoicePrint() + encryptedFormno ;
                                Log.e("Path", path);
                                startActivity(new Intent(Repurchase_Bill_Summary.this, Incentive_Statement_Activity.class).putExtra("URL", path));
                            } else {
                                AppUtils.alertDialog(Repurchase_Bill_Summary.this, jsonObject.getString("Message"));
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            AppUtils.showExceptionDialog(Repurchase_Bill_Summary.this);
                        }
                    }
                }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            }
        } catch (Exception e) {
            e.printStackTrace();
            AppUtils.showExceptionDialog(Repurchase_Bill_Summary.this);
        }
    }

}
