package com.vpipl.kalpamrit;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.widget.Toolbar;
import android.telephony.TelephonyManager;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.vpipl.kalpamrit.R;
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

/**
 * Created by PC14 on 08-Apr-16.
 */
public class AddDeliveryAddress_Activity extends AppCompatActivity {
    private String TAG = "AddDeliveryAddress_Activity";

    private Button btn_save;
    private Button btn_cancel;
    private TextInputEditText et_firstName;
    private TextInputEditText et_lastName;
    private TextInputEditText et_address1;
    private TextInputEditText et_address2;
    private TextInputEditText et_city;
    private TextInputEditText et_district;
    private TextInputEditText et_state;
    private TextInputEditText et_pinCode;
    private TextInputEditText et_mobileNumber;
    private TextInputEditText et_email;
    private TelephonyManager telephonyManager;
    private String[] state;

    ImageView img_menu;

    ImageView img_cart;
    ImageView img_user;

    public void SetupToolbar() {
        img_menu = findViewById(R.id.img_nav_back);

        img_cart = findViewById(R.id.img_cart);
        img_user = findViewById(R.id.img_login_logout);

        img_user.setVisibility(View.GONE);

        img_cart.setVisibility(View.GONE);

        img_menu.setImageResource(R.drawable.icon_nav_bar_close);

        img_menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.adddeliveryaddress_activity);

        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);

        try {

            Toolbar toolbar = findViewById(R.id.toolbar);
            setSupportActionBar(toolbar);
            getSupportActionBar().setTitle("");
            SetupToolbar();

            telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);

            btn_cancel = findViewById(R.id.btn_cancel);
            btn_save = findViewById(R.id.btn_save);

            et_firstName = findViewById(R.id.et_firstName);
            et_lastName = findViewById(R.id.et_lastName);
            et_address1 = findViewById(R.id.et_address1);
            et_address2 = findViewById(R.id.et_address2);
            et_city = findViewById(R.id.et_city);
            et_district = findViewById(R.id.et_district);
            et_state = findViewById(R.id.et_state);
            et_pinCode = findViewById(R.id.et_pinCode);
            et_mobileNumber = findViewById(R.id.et_mobileNumber);
            et_email = findViewById(R.id.et_email);

            et_state.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (AppController.stateList.size() != 0) {
                        showStateDialog();
                    } else {
                        executeStateRequest();
                    }
                }
            });

            btn_cancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    finish();
                }
            });

            btn_save.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    AppUtils.hideKeyboardOnClick(AddDeliveryAddress_Activity.this, view);
                    validateAddressRequest();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            AppUtils.showExceptionDialog(AddDeliveryAddress_Activity.this);
        }
    }

    private void validateAddressRequest() {
        try {
            if (et_firstName.getText().toString().isEmpty()) {
                AppUtils.alertDialog(AddDeliveryAddress_Activity.this, "Please Enter Name.");
                et_firstName.requestFocus();
            } else if (et_lastName.getText().toString().isEmpty()) {
                AppUtils.alertDialog(AddDeliveryAddress_Activity.this, "Please Enter Last Name.");
                et_lastName.requestFocus();
            } else if (et_address1.getText().toString().isEmpty()) {
                AppUtils.alertDialog(AddDeliveryAddress_Activity.this, "Please Enter Address");
                et_address1.requestFocus();
            } else if (et_address2.getText().toString().isEmpty()) {
                AppUtils.alertDialog(AddDeliveryAddress_Activity.this, "Please Enter Address Line 2.");
                et_address2.requestFocus();
            } else if (et_city.getText().toString().isEmpty()) {
                AppUtils.alertDialog(AddDeliveryAddress_Activity.this, "Please Enter City.");
                et_city.requestFocus();
            } else if (et_district.getText().toString().isEmpty()) {
                AppUtils.alertDialog(AddDeliveryAddress_Activity.this, "Please Enter District.");
                et_district.requestFocus();
            } else if (et_state.getText().toString().isEmpty()) {
                AppUtils.alertDialog(AddDeliveryAddress_Activity.this, "Please Select State.");
                et_state.requestFocus();
            } else if (et_pinCode.getText().toString().isEmpty()) {
                AppUtils.alertDialog(AddDeliveryAddress_Activity.this, "Please Enter PinCode.");
                et_pinCode.requestFocus();
            } else if (!et_pinCode.getText().toString().matches(AppUtils.mPINCodePattern)) {
                AppUtils.alertDialog(AddDeliveryAddress_Activity.this, getResources().getString(R.string.error_et_mr_PINno));
                et_pinCode.requestFocus();
            } else if (et_mobileNumber.getText().toString().isEmpty()) {
                AppUtils.alertDialog(AddDeliveryAddress_Activity.this, "Please Enter Mobile Number");
                et_mobileNumber.requestFocus();
            } else if (et_mobileNumber.getText().toString().trim().length() != 10) {
                AppUtils.alertDialog(AddDeliveryAddress_Activity.this,"Please Enter Valid Mobile Number");
                et_mobileNumber.requestFocus();
            } else if (et_email.getText().toString().isEmpty()) {
                AppUtils.alertDialog(AddDeliveryAddress_Activity.this, "Please Enter Email Address");
                et_email.requestFocus();
            } else if (!et_email.getText().toString().isEmpty() && AppUtils.isValidMail(et_email.getText().toString())) {
                AppUtils.alertDialog(AddDeliveryAddress_Activity.this, "Please Enter Correct Email.");
                et_email.requestFocus();
            } else {
                startAddressRequest();
            }
        } catch (Exception e) {
            e.printStackTrace();
            AppUtils.showExceptionDialog(AddDeliveryAddress_Activity.this);
        }
    }

    private void startAddressRequest() {
        try {
            if (AppUtils.isNetworkAvailable(AddDeliveryAddress_Activity.this)) {
                List<NameValuePair> postParameters = new ArrayList<>();
                postParameters.add(new BasicNameValuePair("Formno", AppController.getSpUserInfo().getString(SPUtils.USER_FORM_NUMBER, "")));
                postParameters.add(new BasicNameValuePair("FirstName", et_firstName.getText().toString().trim().replace(",", " ")));
                postParameters.add(new BasicNameValuePair("LastName", et_lastName.getText().toString().trim().replace(",", " ")));
                postParameters.add(new BasicNameValuePair("Address1", et_address1.getText().toString().trim().replace(",", " ")));
                postParameters.add(new BasicNameValuePair("Address2", et_address2.getText().toString().trim().replace(",", " ")));

                String stateCode = "0";
                for (int i = 0; i < AppController.stateList.size(); i++) {
                    if (et_state.getText().toString().equals(AppController.stateList.get(i).get("State"))) {
                        stateCode = AppController.stateList.get(i).get("STATECODE");
                    }
                }

                postParameters.add(new BasicNameValuePair("StateCode", "" + stateCode.trim().replace(",", " ")));
                postParameters.add(new BasicNameValuePair("District", et_district.getText().toString().trim().replace(",", " ")));
                postParameters.add(new BasicNameValuePair("City", et_city.getText().toString().trim().replace(",", " ")));
                postParameters.add(new BasicNameValuePair("PinCode", et_pinCode.getText().toString().trim().replace(",", " ")));
                postParameters.add(new BasicNameValuePair("MobileNo", et_mobileNumber.getText().toString().trim().replace(",", " ")));
                postParameters.add(new BasicNameValuePair("Email", et_email.getText().toString().trim().replace(",", " ")));


                String Usertype = (AppController.getSpUserInfo().getString(SPUtils.USER_TYPE, ""));
                if (Usertype.equalsIgnoreCase("CUSTOMER"))
                    postParameters.add(new BasicNameValuePair("UserType", "N"));
                else if (Usertype.equalsIgnoreCase("DISTRIBUTOR"))
                    postParameters.add(new BasicNameValuePair("UserType", "D"));
                else
                    postParameters.add(new BasicNameValuePair("UserType", "N"));


                executeAddAddressRequest(postParameters);
            } else {
                AppUtils.alertDialog(AddDeliveryAddress_Activity.this, getResources().getString(R.string.txt_networkAlert));
            }
        } catch (Exception e) {
            e.printStackTrace();
            AppUtils.showExceptionDialog(AddDeliveryAddress_Activity.this);
        }
    }

    private void executeAddAddressRequest(final List<NameValuePair> postParameters) {
        try {
            if (AppUtils.isNetworkAvailable(AddDeliveryAddress_Activity.this)) {
                new AsyncTask<Void, Void, String>() {
                    protected void onPreExecute() {
                        AppUtils.showProgressDialog(AddDeliveryAddress_Activity.this);
                    }

                    @Override
                    protected String doInBackground(Void... params) {
                        String response = "";
                        try {
                            response = AppUtils.callWebServiceWithMultiParam(AddDeliveryAddress_Activity.this, postParameters, QueryUtils.methodToAddCheckOutDeliveryAddress, TAG);
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
                                    saveDeliveryAddressInfo(jsonArrayData);
                                } else {
                                    AppUtils.alertDialog(AddDeliveryAddress_Activity.this, jsonObject.getString("Message"));
                                }
                            } else {
                                AppUtils.alertDialog(AddDeliveryAddress_Activity.this, jsonObject.getString("Message"));
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            AppUtils.showExceptionDialog(AddDeliveryAddress_Activity.this);
                        }
                    }
                }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            }
        } catch (Exception e) {
            e.printStackTrace();
            AppUtils.showExceptionDialog(AddDeliveryAddress_Activity.this);
        }
    }

    private void saveDeliveryAddressInfo(JSONArray jsonArrayData) {
        try {
            CheckoutToPay_Activity.deliveryAddressList.clear();

            for (int i = 0; i < jsonArrayData.length(); i++) {
                JSONObject jsonObject = jsonArrayData.getJSONObject(i);

                HashMap<String, String> map = new HashMap<>();
                map.put("ID", "" + jsonObject.getString("ID"));
                map.put("MemFirstName", "" + WordUtils.capitalizeFully(jsonObject.getString("MemFirstName")));
                map.put("MemLastName", "" + WordUtils.capitalizeFully(jsonObject.getString("MemLastName")));
                map.put("Address1", "" + WordUtils.capitalizeFully(jsonObject.getString("Address1")));
                map.put("Address2", "" + WordUtils.capitalizeFully(jsonObject.getString("Address2")));
                map.put("CountryID", "" + jsonObject.getString("CountryID"));
                map.put("CountryName", "" + WordUtils.capitalizeFully(jsonObject.getString("CountryName")));
                map.put("StateCode", "" + jsonObject.getString("StateCode"));
                map.put("StateName", "" + WordUtils.capitalizeFully(jsonObject.getString("StateName")));
                map.put("District", "" + WordUtils.capitalizeFully(jsonObject.getString("District")));
                map.put("City", "" + WordUtils.capitalizeFully(jsonObject.getString("City")));
                map.put("PinCode", "" + jsonObject.getString("PinCode"));
                map.put("Email", "" + jsonObject.getString("MailID"));
                map.put("Mobl", "" + jsonObject.getString("Mobl"));
                map.put("EntryType", "" + jsonObject.getString("EntryType"));
                map.put("Address", "" + WordUtils.capitalizeFully(jsonObject.getString("Address").replace("&nbsp;", " ")));
                CheckoutToPay_Activity.deliveryAddressList.add(map);
            }

            final Dialog dialog = AppUtils.createDialog(AddDeliveryAddress_Activity.this, true);
            TextView dialog4all_txt = dialog.findViewById(R.id.txt_DialogTitle);
            dialog4all_txt.setText("Your delivery address is added successfully!!!");
            dialog.findViewById(R.id.txt_submit).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                    finish();

                    Bundle bundle = getIntent().getExtras();
                    if (bundle != null) {
                        if (getIntent().getExtras().getString("ComesFrom").equalsIgnoreCase("CheckoutToPay_Activity"))
                            startActivity(new Intent(AddDeliveryAddress_Activity.this, CheckoutToPay_Activity.class));
                    }

                    AppUtils.dismissProgressDialog();
                    //overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                }
            });

            dialog.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void executeStateRequest() {
        new AsyncTask<Void, Void, String>() {
            protected void onPreExecute() {
                AppUtils.showProgressDialog(AddDeliveryAddress_Activity.this);
            }

            @Override
            protected String doInBackground(Void... params) {
                String response = "";
                try {
                    List<NameValuePair> postParameters = new ArrayList<>();
                    response = AppUtils.callWebServiceWithMultiParam(AddDeliveryAddress_Activity.this, postParameters, QueryUtils.methodMaster_FillState, TAG);
                } catch (Exception ignored) {
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
                        if (jsonArrayData.length() != 0) {
                            getStateResult(jsonArrayData);
                        } else {
                            AppUtils.alertDialog(AddDeliveryAddress_Activity.this, jsonObject.getString("Message"));
                        }
                    } else {
                        AppUtils.alertDialog(AddDeliveryAddress_Activity.this, jsonObject.getString("Message"));
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
            state = new String[AppController.stateList.size()];
            for (int i = 0; i < AppController.stateList.size(); i++) {
                state[i] = AppController.stateList.get(i).get("State");
            }

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Select State");
            builder.setItems(state, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int item) {
                    // Do something with the selection
                    et_state.setText(state[item]);
                }
            });
            builder.create().show();
        } catch (Exception e) {
            e.printStackTrace();
            AppUtils.showExceptionDialog(AddDeliveryAddress_Activity.this);
        }
    }
}
