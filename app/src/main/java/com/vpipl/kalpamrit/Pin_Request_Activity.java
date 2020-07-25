package com.vpipl.kalpamrit;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.TextInputEditText;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.widget.Toolbar;
import android.telephony.TelephonyManager;
import android.text.Editable;
import android.text.InputType;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.method.DigitsKeyListener;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
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
import java.util.HashMap;
import java.util.List;

public class Pin_Request_Activity extends AppCompatActivity {

    String TAG = "Pin_Request_Activity";

    TextInputEditText edtxt_address, edtxt_city, edtxt_district, edtxt_pinCode, edtxt_total_bv, edtxt_amount, edtxt_rnd_off, edtxt_net_payble, edtxt_remarks;

    TextInputEditText txt_state, txt_franchisee, txt_choose_pay_mode;
    RadioGroup rg_purchase_from;
    RadioButton rb_company, rb_franchisee;

    Button btn_request;
    private String[] stateArray;

    String paymodeArray[] = {"Cash"};

    TelephonyManager telephonyManager;

    String address, city, district, pinCode, total_bv, amount, rnd_off, net_payble, remarks, state, franchisee, paymode = "Cash";

    List<EditText> allEds = new ArrayList<EditText>();
    List<TextView> allTvs = new ArrayList<TextView>();
    List<String> kitid = new ArrayList<String>();
    List<String> kitname = new ArrayList<String>();

    public ArrayList<HashMap<String, String>> companyFranchiseeList = new ArrayList<>();


    ImageView img_nav_back, img_login_logout;

    public void SetupToolbar() {

        img_nav_back = (ImageView) findViewById(R.id.img_nav_back);
        img_login_logout = (ImageView) findViewById(R.id.img_login_logout);

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
                    startActivity(new Intent(Pin_Request_Activity.this, Login_Activity.class));
                else
                    AppUtils.showDialogSignOut(Pin_Request_Activity.this);
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
        setContentView(R.layout.activity_pin_request_amount);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);

        try {
            Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
            setSupportActionBar(toolbar);
            getSupportActionBar().setTitle("");
            SetupToolbar();

            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

            telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);


            edtxt_address = findViewById(R.id.edtxt_address);
            edtxt_district = findViewById(R.id.edtxt_district);
            edtxt_city = findViewById(R.id.edtxt_city);
            edtxt_pinCode = findViewById(R.id.edtxt_pinCode);
            edtxt_total_bv = findViewById(R.id.edtxt_total_bv);
            edtxt_amount = findViewById(R.id.edtxt_amount);
            edtxt_rnd_off = findViewById(R.id.edtxt_rnd_off);
            edtxt_net_payble = findViewById(R.id.edtxt_net_payble);
            edtxt_remarks = findViewById(R.id.edtxt_remarks);

            txt_state = findViewById(R.id.txt_state);
            txt_franchisee = findViewById(R.id.txt_franchisee);
            txt_choose_pay_mode = findViewById(R.id.txt_choose_pay_mode);

            rg_purchase_from = findViewById(R.id.rg_purchase_from);
            rb_franchisee = findViewById(R.id.rb_franchisee);
            rb_company = findViewById(R.id.rb_company);

            btn_request = (Button) findViewById(R.id.btn_request);

            txt_choose_pay_mode.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    showpaymodeDialog();
                }
            });

            txt_state.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showStateDialog();
                }
            });

            txt_franchisee.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showCompanyFranchiseeDialog();
                }
            });

            btn_request.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    AppUtils.hideKeyboardOnClick(Pin_Request_Activity.this, view);
                    if (paymode.equalsIgnoreCase("Cash"))
                        ValidateDataCash();
                }
            });


            rg_purchase_from.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(RadioGroup group, int checkedId) {
                    int selectedId = group.getCheckedRadioButtonId();

                    RadioButton radioButton = findViewById(selectedId);

                    if (radioButton == rb_company) {
                        executeCompanyRequest();
                    } else {
                        executeFranchiseeRequest();
                    }

                    TableLayout ll = (TableLayout) findViewById(R.id.displayLinear);
                    ll.removeAllViews();

                    txt_franchisee.setText("");
                    edtxt_amount.setText("0");
                    edtxt_net_payble.setText("0");
                    edtxt_rnd_off.setText("0");
                    edtxt_total_bv.setText("0");
                    edtxt_remarks.setText("");
                }
            });

            executeStateRequest();

            executeCompanyRequest();

        } catch (Exception e) {
            e.printStackTrace();
            AppUtils.showExceptionDialog(Pin_Request_Activity.this);
        }
    }

    private void ValidateDataCash() {
        try {

            address = edtxt_address.getText().toString().trim();
            city = edtxt_city.getText().toString().trim();
            district = edtxt_district.getText().toString().trim();
            state = txt_state.getText().toString().trim();
            pinCode = edtxt_pinCode.getText().toString().trim();
            franchisee = txt_franchisee.getText().toString().trim();

            total_bv = edtxt_total_bv.getText().toString().trim();
            amount = edtxt_amount.getText().toString().trim();
            rnd_off = edtxt_rnd_off.getText().toString().trim();
            net_payble = edtxt_net_payble.getText().toString().trim();

            remarks = edtxt_remarks.getText().toString().trim();


            float amt = 0;
            try {
                amt = Float.parseFloat(amount);
            } catch (Exception ignored) {

            }
            if (TextUtils.isEmpty(address)) {
                AppUtils.alertDialog(Pin_Request_Activity.this, "Address is Required");
                edtxt_address.requestFocus();
            } else if (TextUtils.isEmpty(city)) {
                AppUtils.alertDialog(Pin_Request_Activity.this, "City is Required");
                edtxt_city.requestFocus();
            } else if (TextUtils.isEmpty(district)) {
                AppUtils.alertDialog(Pin_Request_Activity.this, "District is Required");
                edtxt_district.requestFocus();
            } else if (TextUtils.isEmpty(state)) {
                AppUtils.alertDialog(Pin_Request_Activity.this, "State is Required");
                txt_state.requestFocus();
            } else if (TextUtils.isEmpty(pinCode)) {
                AppUtils.alertDialog(Pin_Request_Activity.this, "Pincode is Required");
                edtxt_pinCode.requestFocus();
            } else if (pinCode.length() != 6) {
                AppUtils.alertDialog(Pin_Request_Activity.this, "Invalid Pincode");
                edtxt_pinCode.requestFocus();
            } else if (rg_purchase_from.getCheckedRadioButtonId() == -1) {
                AppUtils.alertDialog(Pin_Request_Activity.this, "Select Purchase From");
            } else if (TextUtils.isEmpty(franchisee)) {
                AppUtils.alertDialog(Pin_Request_Activity.this, "Select Company/Franchisee");
                txt_franchisee.requestFocus();
            } else if (!AppUtils.isNetworkAvailable(Pin_Request_Activity.this)) {
                AppUtils.alertDialog(Pin_Request_Activity.this, getResources().getString(R.string.txt_networkAlert));
            } else {
                startRequestAmountCash();
            }
        } catch (Exception e) {
            e.printStackTrace();
            AppUtils.showExceptionDialog(Pin_Request_Activity.this);
        }
    }

    private void startRequestAmountCash() {
        try {
            if (AppUtils.isNetworkAvailable(Pin_Request_Activity.this)) {

                List<NameValuePair> postParameters = new ArrayList<>();
                postParameters.add(new BasicNameValuePair("DepositAmount", amount));
                postParameters.add(new BasicNameValuePair("PayMode", "Bank"));
                postParameters.add(new BasicNameValuePair("AccountNo", "0"));
                postParameters.add(new BasicNameValuePair("BranchName", "Cash"));
                postParameters.add(new BasicNameValuePair("BankName", "Cash"));
                postParameters.add(new BasicNameValuePair("TransactionNo", "0"));
                postParameters.add(new BasicNameValuePair("FormNo", AppController.getSpUserInfo().getString(SPUtils.USER_FORM_NUMBER, "")));
                postParameters.add(new BasicNameValuePair("IdNo", AppController.getSpUserInfo().getString(SPUtils.USER_ID_NUMBER, "")));
                postParameters.add(new BasicNameValuePair("DeviceID", telephonyManager.getDeviceId()));

                JSONArray jsonArrayOrder = new JSONArray();

                int[] qtyarr = new int[allEds.size()];
                int[] amtarr = new int[allEds.size()];

                for (int i = 0; i < allEds.size(); i++) {
                    if (allEds.get(i).getText().toString().trim().length() > 0)
                        qtyarr[i] = Math.round(Float.parseFloat(allEds.get(i).getText().toString().trim()));
                    else
                        qtyarr[i] = 0;

                    if (allTvs.get(i).getText().toString().trim().length() > 0)
                        amtarr[i] = Math.round(Float.parseFloat(allTvs.get(i).getText().toString().trim()));
                    else
                        amtarr[i] = 0;
                }

                for (int i = 0; i < kitid.size(); i++) {
                    JSONObject jsonObjectDetail = new JSONObject();
                    jsonObjectDetail.put("KitId", "" + kitid.get(i).trim().replace(",", " "));
                    jsonObjectDetail.put("KitName", "" + kitname.get(i).trim().replace(",", " "));
                    jsonObjectDetail.put("Qty", "" + qtyarr[i]);
                    jsonObjectDetail.put("Amount", amtarr[i]);

                    jsonArrayOrder.put(jsonObjectDetail);
                }

                postParameters.add(new BasicNameValuePair("PackageDetails", jsonArrayOrder.toString().trim()));

                executeRequestAmount(postParameters);

            } else {
                AppUtils.alertDialog(Pin_Request_Activity.this, getResources().getString(R.string.txt_networkAlert));
            }
        } catch (Exception e) {
            e.printStackTrace();
            AppUtils.showExceptionDialog(Pin_Request_Activity.this);
        }
    }


    private void showpaymodeDialog() {
        try {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Choose Payment Mode");
            builder.setItems(paymodeArray, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int item) {
                    txt_choose_pay_mode.setText(paymodeArray[item]);
                    paymode = paymodeArray[item];
                }
            });
            builder.create().show();
        } catch (Exception e) {
            e.printStackTrace();
            AppUtils.showExceptionDialog(Pin_Request_Activity.this);
        }
    }

    private void executeRequestAmount(final List<NameValuePair> postParameters) {
        try {
            if (AppUtils.isNetworkAvailable(Pin_Request_Activity.this)) {
                new AsyncTask<Void, Void, String>() {
                    protected void onPreExecute() {
                        AppUtils.showProgressDialog(Pin_Request_Activity.this);
                    }

                    @Override
                    protected String doInBackground(Void... params) {
                        String response = "";
                        try {
                            if (paymode.equalsIgnoreCase("Cash"))
                                response = AppUtils.callWebServiceWithMultiParam(Pin_Request_Activity.this, postParameters, QueryUtils.methodToPurchaseProducts, TAG);

                        } catch (Exception e) {
                            e.printStackTrace();
                            AppUtils.showExceptionDialog(Pin_Request_Activity.this);
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
                                AppUtils.alertDialogWithFinish(Pin_Request_Activity.this, "" + jsonObject.getString("Message"));
                            } else {
                                AppUtils.alertDialog(Pin_Request_Activity.this, jsonObject.getString("Message"));
                            }

                        } catch (Exception e) {
                            e.printStackTrace();
                            AppUtils.showExceptionDialog(Pin_Request_Activity.this);
                        }
                    }
                }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            }
        } catch (Exception e) {
            e.printStackTrace();
            AppUtils.showExceptionDialog(Pin_Request_Activity.this);
        }
    }

    private void executeLoadProducts(final String franchiseecode) {
        try {
            if (AppUtils.isNetworkAvailable(Pin_Request_Activity.this)) {
                new AsyncTask<Void, Void, String>() {
                    protected void onPreExecute() {
                        AppUtils.showProgressDialog(Pin_Request_Activity.this);
                    }

                    @Override
                    protected String doInBackground(Void... params) {
                        String response = "";
                        try {
                            List<NameValuePair> postParameters = new ArrayList<>();
                            postParameters.add(new BasicNameValuePair("franchiseecode", franchiseecode));
                            response = AppUtils.callWebServiceWithMultiParam(Pin_Request_Activity.this, postParameters, QueryUtils.methodToLoadProducts, TAG);

                        } catch (Exception e) {
                            e.printStackTrace();
                            AppUtils.showExceptionDialog(Pin_Request_Activity.this);
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
                                AppUtils.alertDialog(Pin_Request_Activity.this, jsonObject.getString("Message"));
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            AppUtils.showExceptionDialog(Pin_Request_Activity.this);
                        }
                    }
                }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            }
        } catch (Exception e) {
            e.printStackTrace();
            AppUtils.showExceptionDialog(Pin_Request_Activity.this);
        }
    }

    public void WriteValues(JSONArray jsonArray) {
        float sp = 8;
        int px = (int) (sp * getResources().getDisplayMetrics().scaledDensity);

        allEds.clear();
        allTvs.clear();
        kitid.clear();
        kitname.clear();

        Typeface typeface = ResourcesCompat.getFont(this, R.font.gisha_0);
        TableLayout ll = (TableLayout) findViewById(R.id.displayLinear);

        TableRow row1 = new TableRow(this);

        TableRow.LayoutParams lp1 = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT);
        row1.setLayoutParams(lp1);
        row1.setBackgroundColor(getResources().getColor(R.color.color_green_text));

        TextView A1 = new TextView(this);
        TextView B1 = new TextView(this);
        TextView C1 = new TextView(this);
        TextView D1 = new TextView(this);

        A1.setText("Kit Name");
        B1.setText("Kit Amount");
        C1.setText("Qty.");
        D1.setText("Amount");

        A1.setPadding(px, px, px, px);
        B1.setPadding(px, px, px, px);
        C1.setPadding(px, px, px, px);
        D1.setPadding(px, px, px, px);

        A1.setTypeface(typeface);
        B1.setTypeface(typeface);
        C1.setTypeface(typeface);
        D1.setTypeface(typeface);

        A1.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
        B1.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
        C1.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
        D1.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);

        A1.setGravity(Gravity.CENTER);
        B1.setGravity(Gravity.CENTER);
        C1.setGravity(Gravity.CENTER);
        D1.setGravity(Gravity.CENTER);

        A1.setTextColor(Color.WHITE);
        B1.setTextColor(Color.WHITE);
        C1.setTextColor(Color.WHITE);
        D1.setTextColor(Color.WHITE);

        row1.addView(A1);
        row1.addView(B1);
        row1.addView(C1);
        row1.addView(D1);


        ll.addView(row1);


        for (int i = 0; i < jsonArray.length(); i++) {
            try {
                JSONObject jobject = jsonArray.getJSONObject(i);

                int payout_number = jobject.getInt("KitId");

                String KitName = jobject.getString("KitName");
                final int KitAmount = jobject.getInt("KitAmount");

                kitid.add("" + payout_number);
                kitname.add(KitName);

                TableRow row = new TableRow(this);
                TableRow.LayoutParams lp = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT);
                row.setLayoutParams(lp);

                if (i % 2 == 0)
                    row.setBackgroundColor(Color.WHITE);
                else
                    row.setBackgroundColor(Color.parseColor("#dddddd"));


                TextView A = new TextView(this);
                TextView B = new TextView(this);
                EditText C = new EditText(this);
                final TextView D = new TextView(this);

                A.setText("" + KitName);
                B.setText("" + KitAmount);
                C.setText("0");
                D.setText("0");

                C.setInputType(InputType.TYPE_CLASS_PHONE);
                C.setKeyListener(DigitsKeyListener.getInstance("0123456789"));

                C.setId(payout_number);
                D.setId(payout_number);

                A.setGravity(Gravity.CENTER);
                B.setGravity(Gravity.CENTER);
                C.setGravity(Gravity.CENTER);
                D.setGravity(Gravity.CENTER);

                A.setPadding(px, px, px, px);
                B.setPadding(px, px, px, px);
                C.setPadding(px, px, px, px);
                D.setPadding(px, px, px, px);

                A.setTypeface(typeface);
                B.setTypeface(typeface);
                C.setTypeface(typeface);
                D.setTypeface(typeface);

                A.setTextColor(Color.BLACK);
                B.setTextColor(Color.BLACK);
                C.setTextColor(Color.BLACK);
                D.setTextColor(Color.BLACK);

                A.setTextSize(TypedValue.COMPLEX_UNIT_SP, 13);
                B.setTextSize(TypedValue.COMPLEX_UNIT_SP, 13);
                C.setTextSize(TypedValue.COMPLEX_UNIT_SP, 13);
                D.setTextSize(TypedValue.COMPLEX_UNIT_SP, 13);

                allEds.add(C);
                allTvs.add(D);

                C.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {

                    }

                    @Override
                    public void afterTextChanged(Editable s) {

                        int abc = 0;
                        try {
                            if (s.length() > 0)
                                abc = Math.round(Float.parseFloat(s.toString().trim()));
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        D.setText("" + (abc * KitAmount));

                        int[] strings = new int[allEds.size()];

                        for (int i = 0; i < allEds.size(); i++) {
                            if (allEds.get(i).getText().toString().trim().length() > 0)
                                strings[i] = Math.round(Float.parseFloat(allEds.get(i).getText().toString().trim()));
                            else
                                strings[i] = 0;
                        }

                        int sum = 0;

                        for (int i = 0; i < strings.length; i++) {
                            sum += strings[i];
                        }

//                        edtxt_total_pin.setText("" + sum);

                    }
                });

                D.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {

                    }

                    @Override
                    public void afterTextChanged(Editable s) {

                        int[] strings = new int[allTvs.size()];

                        for (int i = 0; i < allTvs.size(); i++) {
                            strings[i] = Math.round(Float.parseFloat(allTvs.get(i).getText().toString().trim()));
                        }

                        int sum = 0;

                        for (int i = 0; i < strings.length; i++) {
                            sum += strings[i];
                        }
                        edtxt_amount.setText("" + sum);
                    }
                });

                row.addView(A);
                row.addView(B);
                row.addView(C);
                row.addView(D);

                View view_one = new View(this);
                view_one.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 1));
                view_one.setBackgroundColor(getResources().getColor(R.color.app_color_green_one));

                ll.addView(row);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        try {
            switch (item.getItemId()) {
                case android.R.id.home:
                    finish();
                    break;

            }
        } catch (Exception e) {
            e.printStackTrace();
            AppUtils.showExceptionDialog(Pin_Request_Activity.this);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        try {
            AppUtils.dismissProgressDialog();
        } catch (Exception e) {
            e.printStackTrace();
            AppUtils.showExceptionDialog(Pin_Request_Activity.this);
        }

        System.gc();
    }

    private void executeStateRequest() {
        new AsyncTask<Void, Void, String>() {
            protected void onPreExecute() {
                AppUtils.showProgressDialog(Pin_Request_Activity.this);
            }

            @Override
            protected String doInBackground(Void... params) {
                String response = "";
                try {
                    List<NameValuePair> postParameters = new ArrayList<>();
                    response = AppUtils.callWebServiceWithMultiParam(Pin_Request_Activity.this, postParameters, QueryUtils.methodMaster_FillState, TAG);
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
                            AppUtils.alertDialog(Pin_Request_Activity.this, jsonObject.getString("Message"));
                        }
                    } else {
                        AppUtils.alertDialog(Pin_Request_Activity.this, jsonObject.getString("Message"));
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
            AppUtils.showExceptionDialog(Pin_Request_Activity.this);
        }
    }


    private void executeCompanyRequest() {
        new AsyncTask<Void, Void, String>() {
            protected void onPreExecute() {
                AppUtils.showProgressDialog(Pin_Request_Activity.this);
            }

            @Override
            protected String doInBackground(Void... params) {
                String response = "";
                try {
                    List<NameValuePair> postParameters = new ArrayList<>();
                    response = AppUtils.callWebServiceWithMultiParam(Pin_Request_Activity.this, postParameters, QueryUtils.methodMaster_FillCompany, TAG);
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
                            getCompanyResult(jsonArrayData);
                        } else {
                            AppUtils.alertDialog(Pin_Request_Activity.this, jsonObject.getString("Message"));
                        }
                    } else {
                        AppUtils.alertDialog(Pin_Request_Activity.this, jsonObject.getString("Message"));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    private void getCompanyResult(JSONArray jsonArray) {
        try {
            companyFranchiseeList.clear();


            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                HashMap<String, String> map = new HashMap<>();

                map.put("STATECODE", jsonObject.getString("STATECODE"));
                map.put("State", WordUtils.capitalizeFully(jsonObject.getString("State")));

                companyFranchiseeList.add(map);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void executeFranchiseeRequest() {
        new AsyncTask<Void, Void, String>() {
            protected void onPreExecute() {
                AppUtils.showProgressDialog(Pin_Request_Activity.this);
            }

            @Override
            protected String doInBackground(Void... params) {
                String response = "";
                try {
                    List<NameValuePair> postParameters = new ArrayList<>();
                    response = AppUtils.callWebServiceWithMultiParam(Pin_Request_Activity.this, postParameters, QueryUtils.methodMaster_FillFranchisee, TAG);
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
                            getFranchiseeResult(jsonArrayData);
                        } else {
                            AppUtils.alertDialog(Pin_Request_Activity.this, jsonObject.getString("Message"));
                        }
                    } else {
                        AppUtils.alertDialog(Pin_Request_Activity.this, jsonObject.getString("Message"));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    private void getFranchiseeResult(JSONArray jsonArray) {
        try {
            companyFranchiseeList.clear();

            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                HashMap<String, String> map = new HashMap<>();

                map.put("STATECODE", jsonObject.getString("STATECODE"));
                map.put("State", WordUtils.capitalizeFully(jsonObject.getString("State")));

                companyFranchiseeList.add(map);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void showCompanyFranchiseeDialog() {
        try {
            final String[] stateArray = new String[companyFranchiseeList.size()];
            for (int i = 0; i < companyFranchiseeList.size(); i++) {
                stateArray[i] = companyFranchiseeList.get(i).get("State");
            }

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Select");
            builder.setItems(stateArray, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int item) {
                    // Do something with the selection

                    txt_franchisee.setText(stateArray[item]);

                    String franchiseecode = "0";
                    for (int i = 0; i < companyFranchiseeList.size(); i++) {
                        if (stateArray[item].equals(companyFranchiseeList.get(i).get("State"))) {
                            franchiseecode = companyFranchiseeList.get(i).get("STATECODE");
                        }
                    }

                    executeLoadProducts(franchiseecode);
                }
            });
            builder.create().show();
        } catch (Exception e) {
            e.printStackTrace();
            AppUtils.showExceptionDialog(Pin_Request_Activity.this);
        }
    }


}