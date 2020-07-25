package com.vpipl.kalpamrit;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.vpipl.kalpamrit.Utils.AppUtils;
import com.vpipl.kalpamrit.Utils.QueryUtils;
import com.vpipl.kalpamrit.Utils.SPUtils;

import org.apache.commons.lang3.text.WordUtils;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class Repurchase_BV_Details_Summary_Activity extends AppCompatActivity {

    private static final String TAG = "Repurchase_BV_Details_Summary_Activity";
    private TextView txt_from_to;
    private TextView txt_heading;
    private TextView txt_count;
    private Button btn_load_more;

    private TableLayout displayLinear;

    private String Action = "";
    private String From;
    private String to;
    private String FormNumber;

    private int TopRows = 25;


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
                    startActivity(new Intent(Repurchase_BV_Details_Summary_Activity.this, Login_Activity.class));
                else
                    AppUtils.showDialogSignOut(Repurchase_BV_Details_Summary_Activity.this);
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
        setContentView(R.layout.activity_repurchase_bv_details_summary);

        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
        SetupToolbar();

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        Action = getIntent().getStringExtra("Action");
        From = getIntent().getStringExtra("From");
        to = getIntent().getStringExtra("To");
        FormNumber = getIntent().getStringExtra("FormNumber");

        final String DateRange = "From " + From + " To " + to;


        txt_from_to = findViewById(R.id.txt_from_to);
        txt_heading = findViewById(R.id.txt_heading);

        txt_count = findViewById(R.id.txt_count);

        btn_load_more = findViewById(R.id.btn_load_more);

        displayLinear = findViewById(R.id.displayLinear);

        if (Action.equalsIgnoreCase("Team Repurchase BV Details"))
            txt_heading.setText("Team Repurchase BV Details");
        else if (Action.equalsIgnoreCase("Self Repurchase BV Details"))
            txt_heading.setText("Self Repurchase BV Details");

        txt_from_to.setText(DateRange);

        btn_load_more.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                TopRows = TopRows + 25;

                executeRepurchaseBVSummaryDetail();

            }
        });

        if (AppUtils.isNetworkAvailable(this)) {
            executeRepurchaseBVSummaryDetail();
        } else {
            AppUtils.alertDialog(this, getResources().getString(R.string.txt_networkAlert));
        }
    }

    private void executeRepurchaseBVSummaryDetail() {

        //findViewById(R.id.ll_showData).setVisibility(View.GONE);

        try {
            if (AppUtils.isNetworkAvailable(Repurchase_BV_Details_Summary_Activity.this)) {
                new AsyncTask<Void, Void, String>() {
                    protected void onPreExecute() {
                        AppUtils.showProgressDialog(Repurchase_BV_Details_Summary_Activity.this);
                    }

                    @Override
                    protected String doInBackground(Void... params) {
                        String response = "";
                        try {
                            String type = "S";
                            if (Action.equalsIgnoreCase("Team Repurchase BV Details")) {
                                type = "D";
                            } else if (Action.equalsIgnoreCase("Self Repurchase BV Details")) {
                                type = "S";
                            }

                            List<NameValuePair> postParameters = new ArrayList<>();
                            postParameters.add(new BasicNameValuePair("FormNo", "" + FormNumber));
                            postParameters.add(new BasicNameValuePair("TopRows", "" + TopRows));
                            postParameters.add(new BasicNameValuePair("type", "" + type));
                            postParameters.add(new BasicNameValuePair("Level", "0"));
                            postParameters.add(new BasicNameValuePair("FDate", From));
                            postParameters.add(new BasicNameValuePair("TDate", to));

                            response = AppUtils.callWebServiceWithMultiParam(Repurchase_BV_Details_Summary_Activity.this,
                                    postParameters, QueryUtils.methodToGetRepurchaseBVSummaryDetail, TAG);

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        return response;
                    }

                    @Override
                    protected void onPostExecute(String resultData) {
                        AppUtils.dismissProgressDialog();

                        try {
                            if (!TextUtils.isEmpty(resultData)) {
                                JSONObject jsonObject = new JSONObject(resultData);
                                JSONArray jsonArrayData = jsonObject.getJSONArray("Data");

                                if (jsonObject.getString("Status").equalsIgnoreCase("True")) {
                                    if (jsonObject.getString("Message").equalsIgnoreCase("Successfully.!")) {
                                        WriteValues(jsonArrayData);
                                    } else {
                                        AppUtils.alertDialog(Repurchase_BV_Details_Summary_Activity.this, jsonObject.getString("Message"));
                                    }
                                } else {
                                    AppUtils.alertDialog(Repurchase_BV_Details_Summary_Activity.this, jsonObject.getString("Message"));
                                }
                            } else {
                                AppUtils.alertDialog(Repurchase_BV_Details_Summary_Activity.this, "No Data Found");
                            }

                        } catch (Exception e) {
                            e.printStackTrace();
                            AppUtils.showExceptionDialog(Repurchase_BV_Details_Summary_Activity.this);
                        }
                    }
                }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            }
        } catch (Exception e) {
            e.printStackTrace();
            AppUtils.showExceptionDialog(Repurchase_BV_Details_Summary_Activity.this);
        }
    }

    private void WriteValues(final JSONArray jarray) {

        findViewById(R.id.ll_showData).setVisibility(View.VISIBLE);

        String text = "(Showing " + jarray.length() + " records)";
        txt_count.setText(text);

        float sp = 8;
        int px = (int) (sp * getResources().getDisplayMetrics().scaledDensity);

        TableLayout ll = findViewById(R.id.displayLinear);
        ll.removeAllViews();

        Typeface typeface = ResourcesCompat.getFont(this,R.font.gisha_0);


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

        A1.setText("S.No");
        B1.setText("ID Number");
        C1.setText("Member Name");
        D1.setText("Bill Number");
        E1.setText("Bill Date");
        F1.setText("Bill Type");
        G1.setText("Bill Amount");
        H1.setText("Repurchase BV");
        I1.setText("Remarks");

        A1.setPadding(px, px, px, px);
        B1.setPadding(px, px, px, px);
        C1.setPadding(px, px, px, px);
        D1.setPadding(px, px, px, px);
        E1.setPadding(px, px, px, px);
        F1.setPadding(px, px, px, px);
        G1.setPadding(px, px, px, px);
        H1.setPadding(px, px, px, px);
        I1.setPadding(px, px, px, px);

          A1.setTypeface(typeface);
        B1.setTypeface(typeface);
        C1.setTypeface(typeface);
        D1.setTypeface(typeface);
        E1.setTypeface(typeface);
        F1.setTypeface(typeface);
        G1.setTypeface(typeface);
        H1.setTypeface(typeface);
        I1.setTypeface(typeface);

        A1.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
        B1.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
        C1.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
        D1.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
        E1.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
        F1.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
        G1.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
        H1.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
        I1.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);

        A1.setGravity(Gravity.CENTER);
        B1.setGravity(Gravity.CENTER);
        C1.setGravity(Gravity.CENTER);
        D1.setGravity(Gravity.CENTER);
        E1.setGravity(Gravity.CENTER);
        F1.setGravity(Gravity.CENTER);
        G1.setGravity(Gravity.CENTER);
        H1.setGravity(Gravity.CENTER);
        I1.setGravity(Gravity.CENTER);

        A1.setTextColor(Color.WHITE);
        B1.setTextColor(Color.WHITE);
        C1.setTextColor(Color.WHITE);
        D1.setTextColor(Color.WHITE);
        E1.setTextColor(Color.WHITE);
        F1.setTextColor(Color.WHITE);
        G1.setTextColor(Color.WHITE);
        H1.setTextColor(Color.WHITE);
        I1.setTextColor(Color.WHITE);

        row1.addView(A1);
        row1.addView(B1);
        row1.addView(C1);
        row1.addView(D1);
        row1.addView(E1);
        row1.addView(F1);
        row1.addView(G1);
        row1.addView(H1);
        row1.addView(I1);

        View view = new View(this);
        view.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 1));
        view.setBackgroundColor(Color.parseColor("#cccccc"));
        
        ll.addView(row1);
        ll.addView(view);

        for (int i = 0; i < jarray.length(); i++) {
            try {

                JSONObject jobject = jarray.getJSONObject(i);

                String ID_Number = jobject.getString("IdNo");
                String Member_Name = WordUtils.capitalizeFully(jobject.getString("MemName"));
                String Bill_Number = jobject.getString("BillNo");
                String Bill_Date = jobject.getString("BillDate");
                String Bill_Type = WordUtils.capitalizeFully(jobject.getString("TranType"));
                String Bill_Amount = jobject.getString("Amount");
                String Repurchase_BV = jobject.getString("BV");
                String Remarks = WordUtils.capitalizeFully(jobject.getString("Remarks"));

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

                A.setText("" + (i + 1));
                B.setText(ID_Number);
                C.setText(Member_Name);
                D.setText(Bill_Number);
                E.setText(AppUtils.getDateFromAPIDate(Bill_Date));
                F.setText(Bill_Type);
                G.setText(Bill_Amount);
                H.setText(Repurchase_BV);
                I.setText(Remarks);

                A.setGravity(Gravity.CENTER);
                B.setGravity(Gravity.CENTER);
                C.setGravity(Gravity.CENTER);
                D.setGravity(Gravity.CENTER);
                E.setGravity(Gravity.CENTER);
                F.setGravity(Gravity.CENTER);
                G.setGravity(Gravity.CENTER);
                H.setGravity(Gravity.CENTER);
                I.setGravity(Gravity.CENTER);

                  A.setTypeface(typeface);
                B.setTypeface(typeface);
                C.setTypeface(typeface);
                D.setTypeface(typeface);
                E.setTypeface(typeface);
                F.setTypeface(typeface);
                G.setTypeface(typeface);
                H.setTypeface(typeface);
                I.setTypeface(typeface);

                A.setPadding(px, px, px, px);
                B.setPadding(px, px, px, px);
                C.setPadding(px, px, px, px);
                D.setPadding(px, px, px, px);
                E.setPadding(px, px, px, px);
                F.setPadding(px, px, px, px);
                G.setPadding(px, px, px, px);
                H.setPadding(px, px, px, px);
                I.setPadding(px, px, px, px);

                A.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
                B.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
                C.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
                D.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
                E.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
                F.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
                G.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
                H.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
                I.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);

                row.addView(A);
                row.addView(B);
                row.addView(C);
                row.addView(D);
                row.addView(E);
                row.addView(F);
                row.addView(G);
                row.addView(H);
                row.addView(I);

                View view_one = new View(this);
                view_one.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 1));
                view.setBackgroundColor(Color.parseColor("#cccccc"));

                ll.addView(row);
//                if (i < jarray.length() - 1)
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
            ////overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        } catch (Exception e) {
            e.printStackTrace();
            AppUtils.showExceptionDialog(Repurchase_BV_Details_Summary_Activity.this);
        }
    }



}
