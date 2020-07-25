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
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.vpipl.kalpamrit.Utils.AppUtils;
import com.vpipl.kalpamrit.Utils.QueryUtils;
import com.vpipl.kalpamrit.Utils.SPUtils;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class ViewCompleteConversation_Activity extends AppCompatActivity {

    private String TAG = "ViewCompleteConversation_Activity";

    private TableLayout displayLinear;

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
                    startActivity(new Intent(ViewCompleteConversation_Activity.this, Login_Activity.class));
                else
                    AppUtils.showDialogSignOut(ViewCompleteConversation_Activity.this);
            }
        });

        if (AppController.getSpIsLogin().getBoolean(SPUtils.IS_LOGIN, false))
            img_login_logout.setImageDrawable(getResources().getDrawable(R.drawable.icon_logout_orange));
        else
            img_login_logout.setImageDrawable(getResources().getDrawable(R.drawable.icon_distributor_login_orange));
    }

    TextView txt_Ticket;
    String TicketNo = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_query_conversation);

        try {
            AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);

            Toolbar toolbar = findViewById(R.id.toolbar);
            setSupportActionBar(toolbar);
            getSupportActionBar().setTitle("");
            SetupToolbar();

            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

            displayLinear = findViewById(R.id.displayLinear);
            txt_Ticket = findViewById(R.id.txt_Ticket);

            TicketNo = getIntent().getStringExtra("TicketNo");
            txt_Ticket.setText("Ticket No. : " + TicketNo);
            if (AppUtils.isNetworkAvailable(this)) {
                createOpenQueryReportRequest();
                findViewById(R.id.ll_showData).setVisibility(View.GONE);

            } else {
                AppUtils.alertDialog(this, getResources().getString(R.string.txt_networkAlert));
            }

        } catch (Exception e) {
            e.printStackTrace();
            AppUtils.showExceptionDialog(this);
        }
    }


    private void createOpenQueryReportRequest() {

        List<NameValuePair> postParameters = new ArrayList<>();
        postParameters.add(new BasicNameValuePair("TicketId", "" + TicketNo));
        executeOpenReportRequest(postParameters);
    }

    private void executeOpenReportRequest(final List postparameters) {
        try {
            if (AppUtils.isNetworkAvailable(ViewCompleteConversation_Activity.this)) {
                new AsyncTask<Void, Void, String>() {
                    protected void onPreExecute() {
                        AppUtils.showProgressDialog(ViewCompleteConversation_Activity.this);
                    }

                    @Override
                    protected String doInBackground(Void... params) {
                        String response = "";
                        try {
                            response = AppUtils.callWebServiceWithMultiParam(ViewCompleteConversation_Activity.this,
                                    postparameters, QueryUtils.methodToOpenQuerieConversation, TAG);

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
                                AppUtils.alertDialog(ViewCompleteConversation_Activity.this, jsonObject.getString("Message"));
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            AppUtils.showExceptionDialog(ViewCompleteConversation_Activity.this);
                        }
                    }
                }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            }
        } catch (Exception e) {
            e.printStackTrace();
            AppUtils.showExceptionDialog(ViewCompleteConversation_Activity.this);
        }
    }

    private void WriteValues(final JSONArray jarray) {
        findViewById(R.id.ll_showData).setVisibility(View.VISIBLE);

        float sp = 8;
        int px = (int) (sp * getResources().getDisplayMetrics().scaledDensity);

        TableLayout ll = findViewById(R.id.displayLinear);
        ll.removeAllViews();

        Typeface typeface = ResourcesCompat.getFont(this, R.font.gisha_0);

        for (int i = 0; i < jarray.length(); i++) {
            try {

                JSONObject jobject = jarray.getJSONObject(i);

                String Complain = jobject.getString("Complain");
                String Solution = (jobject.getString("Solution"));
                String DispForwardDate = jobject.getString("DispForwardDate");
                String DispSolutionDate = (jobject.getString("DispSolutionDate"));

                TableRow row = new TableRow(this);

                TableLayout.LayoutParams lp = new TableLayout.LayoutParams(TableLayout.LayoutParams.WRAP_CONTENT, TableLayout.LayoutParams.WRAP_CONTENT);
                lp.weight = 1;
                row.setLayoutParams(lp);

                TableRow.LayoutParams lParams = new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT,TableRow.LayoutParams.WRAP_CONTENT,1);

                if (i % 2 == 0)
                    row.setBackgroundColor(Color.parseColor("#dddddd"));
                else
                    row.setBackgroundColor(Color.WHITE);

                TextView A = new TextView(this);
                TextView B = new TextView(this);

                A.setText("You : " + Complain);
                B.setText(DispForwardDate);

                A.setGravity(Gravity.CENTER|Gravity.START);
                B.setGravity(Gravity.CENTER|Gravity.END);

                A.setPadding(px, px, px, px);
                B.setPadding(px, px, px, px);

                A.setLayoutParams(lParams);
                B.setLayoutParams(lParams);

                A.setTextSize(TypedValue.COMPLEX_UNIT_SP, 13);
                B.setTextSize(TypedValue.COMPLEX_UNIT_SP, 13);

                row.addView(A);
                row.addView(B);

                View view_one = new View(this);
                view_one.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 1));
                view_one.setBackgroundColor(Color.parseColor("#cccccc"));

                TableRow row_two = new TableRow(this);
                row_two.setLayoutParams(lp);

                if (i % 2 == 0)
                    row_two.setBackgroundColor(Color.parseColor("#F0F0F0"));
                else row_two.setBackgroundColor(Color.WHITE);


                TextView A_two = new TextView(this);
                TextView B_two = new TextView(this);

                A_two.setText("Support Team : " + Solution);
                B_two.setText(DispSolutionDate);

                A_two.setGravity(Gravity.CENTER|Gravity.START);
                B_two.setGravity(Gravity.CENTER_VERTICAL|Gravity.END);

                A_two.setPadding(px, px, px, px);
                B_two.setPadding(px, px, px, px);

                A_two.setLayoutParams(lParams);
                B_two.setLayoutParams(lParams);

                A_two.setTextSize(TypedValue.COMPLEX_UNIT_SP, 13);
                B_two.setTextSize(TypedValue.COMPLEX_UNIT_SP, 13);

                row_two.addView(A_two);
                row_two.addView(B_two);

                View view_two = new View(this);
                view_two.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 1));
                view_two.setBackgroundColor(Color.parseColor("#666666"));


                ll.addView(row);
                ll.addView(view_one);

                ll.addView(row_two);
                ll.addView(view_two);

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
            AppUtils.showExceptionDialog(ViewCompleteConversation_Activity.this);
        }
    }
}
