package com.vpipl.kalpamrit;

import android.Manifest;
import android.app.Dialog;
import android.app.DownloadManager;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import com.vpipl.kalpamrit.R;
import com.vpipl.kalpamrit.Utils.AppUtils;
import com.vpipl.kalpamrit.Utils.Cache;
import com.vpipl.kalpamrit.Utils.QueryUtils;
import com.vpipl.kalpamrit.Utils.SPUtils;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static android.view.View.GONE;

public class Splash_Activity extends AppCompatActivity {

    private static final String TAG = "Splash_Activity";
    public static JSONArray HeadingJarray;

    private String[] PermissionGroup = new String[]{Manifest.permission.READ_PHONE_STATE, Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA, Manifest.permission.GET_ACCOUNTS};

    private int versionCode;
    String version;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_splash);

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();

        StrictMode.setThreadPolicy(policy);

        try {

            String manufacturer = Build.MANUFACTURER;
            String model = Build.MODEL;

            String DeviceModel = manufacturer + " " + model;

            AppController.getSpIsInstall().edit()
                    .putString(SPUtils.IS_INSTALL_DeviceModel, "" + DeviceModel)
                    .putString(SPUtils.IS_INSTALL_DeviceName, "" + DeviceModel).commit();

            PackageManager manager = getApplicationContext().getPackageManager();
            PackageInfo info = manager.getPackageInfo(getApplicationContext().getPackageName(), 0);
            version = info.versionName;
            versionCode = info.versionCode;

            int permissionCheck = ContextCompat.checkSelfPermission(this, Manifest.permission.GET_ACCOUNTS);

            if (permissionCheck == PackageManager.PERMISSION_GRANTED) {
                if (AppUtils.isNetworkAvailable(Splash_Activity.this)) {
                    executeApplicationStatus();
                } else {
                    AppUtils.alertDialogWithFinish(Splash_Activity.this, getResources().getString(R.string.txt_networkAlert));
                }
            } else ActivityCompat.requestPermissions(this, PermissionGroup, 84);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void executeApplicationStatus() {
        new AsyncTask<Void, Void, String>() {

            @Override
            protected String doInBackground(Void... params) {
                String response = "";
                try {
                    List<NameValuePair> postParameters = new ArrayList<>();
                    response = AppUtils.callWebServiceWithMultiParam(Splash_Activity.this, postParameters, QueryUtils.methodToApplicationStatus, "Splash");
                } catch (Exception ignored) {
                }
                return response;
            }

            @Override
            protected void onPostExecute(String resultData) {
                System.gc();
                Runtime.getRuntime().gc();
                try {
                    JSONObject jsonObject = new JSONObject(resultData);

                    if (jsonObject.getString("Status").equalsIgnoreCase("True")) {

                        executesGetVersionRequest();

                    } else {
                        final Dialog dialog = AppUtils.createDialog(Splash_Activity.this, true);
                        TextView dialog4all_txt = dialog.findViewById(R.id.txt_DialogTitle);
                        dialog4all_txt.setText(jsonObject.getString("Message"));

                        TextView txtsubmit = dialog.findViewById(R.id.txt_submit);
                        txtsubmit.setText("Exit");
                        txtsubmit.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                dialog.dismiss();

                                Intent homeIntent = new Intent(Intent.ACTION_MAIN);
                                homeIntent.addCategory(Intent.CATEGORY_HOME);
                                homeIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                startActivity(homeIntent);
                                finish();
                                System.exit(0);
                            }
                        });
                        dialog.show();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    private void executesGetVersionRequest() {
        try {
            if (AppUtils.isNetworkAvailable(Splash_Activity.this)) {
                new AsyncTask<Void, Void, String>() {

                    @Override
                    protected String doInBackground(Void... params) {
                        String response = null;
                        try {
                            List<NameValuePair> postParameters = new ArrayList<>();
                            postParameters.add(new BasicNameValuePair("Versioninfo", "" + versionCode));
                            response = AppUtils.callWebServiceWithMultiParam(Splash_Activity.this, postParameters, QueryUtils.methodToGetVersion, TAG);
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

                            if (jsonArrayData.getJSONObject(0).getString("Status").equalsIgnoreCase("False")) {
                                showUpdateDialog(jsonArrayData.getJSONObject(0).getString("Msg"), jsonArrayData.getJSONObject(0).getString("AppDownloadURL"));
                            } else {
                                executeToGetDrawerShopMenu();
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            AppUtils.showExceptionDialog(Splash_Activity.this);
                        }
                    }
                }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            }
        } catch (Exception e) {
            e.printStackTrace();
            AppUtils.showExceptionDialog(Splash_Activity.this);
        }
    }

    private void showUpdateDialog(String Msg, final String IsCompulsory) {
        try {
            final Dialog dialog = AppUtils.createDialog(Splash_Activity.this, false);
            dialog.setCancelable(false);

            TextView txt_DialogTitle = dialog.findViewById(R.id.txt_DialogTitle);
            txt_DialogTitle.setText(Html.fromHtml(Msg));

            TextView txt_submit = dialog.findViewById(R.id.txt_submit);
            txt_submit.setText("Update Now");
            txt_submit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {

                        dialog.dismiss();
                        finish();

                       final String appPackageName = getPackageName(); // getPackageName() from Context or Activity object
                        try {
                            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
                        } catch (android.content.ActivityNotFoundException anfe) {
                            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
                        }

                        finish();

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });

            TextView txt_cancel = dialog.findViewById(R.id.txt_cancel);
            txt_cancel.setVisibility(GONE);
            txt_cancel.setText("Update Later");
            txt_cancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        dialog.dismiss();
                        executeToGetDrawerShopMenu();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });

            dialog.show();

        } catch (Exception e) {
            e.printStackTrace();
            AppUtils.showExceptionDialog(Splash_Activity.this);
        }
    }

    private void executeToGetDrawerShopMenu() {
        new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... params) {
                String response = "";
                try {
                    List<NameValuePair> postParameters = new ArrayList<>();
                    response = AppUtils.callWebServiceWithMultiParam(Splash_Activity.this, postParameters, QueryUtils.  methodAppAllCategory, TAG);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return response;
            }

            @Override
            protected void onPostExecute(String resultData) {
                try {

                    JSONObject jsonObject = new JSONObject(resultData);

                    if (jsonObject.getString("Status").equalsIgnoreCase("True")) {
                        JSONArray jsonArrayHeadingMenu = jsonObject.getJSONArray("HeadingMenu");
                        JSONArray jsonArrayCategroyMenu = jsonObject.getJSONArray("CategroyMenu");
                        JSONArray jsonArraySubcategoryLevel1 = jsonObject.getJSONArray("SubcategoryLevel1");
                        JSONArray jsonArraySubcategoryLevel2 = jsonObject.getJSONArray("SubcategoryLevel2");

                        getHeadingMenuResult(jsonArrayHeadingMenu, jsonArrayCategroyMenu, jsonArraySubcategoryLevel1, jsonArraySubcategoryLevel2);
                    } else {
                        AppUtils.alertDialog(Splash_Activity.this, "Sorry Seems to be a server error. Please try again!!!");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    private void getHeadingMenuResult(JSONArray jsonArrayHeadingMenu, JSONArray jsonArrayCategroyMenu, JSONArray jsonArraySubcategoryLevel1, JSONArray jsonArraySubcategoryLevel2) {
        try {
            AppController.category1.clear();
            for (int i = 0; i < jsonArrayHeadingMenu.length(); i++) {
                JSONObject jsonObject = jsonArrayHeadingMenu.getJSONObject(i);

                HashMap<String, String> map = new HashMap<>();
                map.put("Type", jsonObject.getString("Type"));
                map.put("HID", jsonObject.getString("HID"));
                map.put("Heading", AppUtils.CapsFirstLetterString(jsonObject.getString("Heading").trim().toUpperCase()));
                map.put("ImgPath", jsonObject.getString("ImgPath"));
                map.put("AppHeadingIconPath", jsonObject.getString("AppHeadingIconPath"));
                map.put("IsComboPack", jsonObject.getString("IsComboPack"));

                AppController.category1.add(map);
            }

            AppController.category2.clear();
            for (int i = 0; i < jsonArrayCategroyMenu.length(); i++) {
                JSONObject jsonObject = jsonArrayCategroyMenu.getJSONObject(i);
                HashMap<String, String> map = new HashMap<>();
                map.put("Type", "C");
                map.put("HID", jsonObject.getString("HID"));
                map.put("CID", jsonObject.getString("CID"));
                map.put("Category", AppUtils.CapsFirstLetterString(jsonObject.getString("Category")));

                AppController.category2.add(map);
            }

            AppController.category3.clear();
            for (int i = 0; i < jsonArraySubcategoryLevel1.length(); i++) {
                JSONObject jsonObject = jsonArraySubcategoryLevel1.getJSONObject(i);
                HashMap<String, String> map = new HashMap<>();
                map.put("Type", jsonObject.getString("Type"));
                map.put("SCID", jsonObject.getString("SCID"));
                map.put("CID", jsonObject.getString("CID"));
                map.put("HID", jsonObject.getString("HID"));
                map.put("SubCategory", AppUtils.CapsFirstLetterString(jsonObject.getString("SubCategory")));
                map.put("Description", jsonObject.getString("Description"));
                AppController.category3.add(map);
            }

            AppController.category4.clear();
            for (int i = 0; i < jsonArraySubcategoryLevel2.length(); i++) {
                JSONObject jsonObject = jsonArraySubcategoryLevel2.getJSONObject(i);
                HashMap<String, String> map = new HashMap<>();
                map.put("Type", jsonObject.getString("Type"));
                map.put("SCID2", jsonObject.getString("SCID2"));
                map.put("SCID", jsonObject.getString("SCID"));
                map.put("CID", jsonObject.getString("CID"));
                map.put("HID", jsonObject.getString("HID"));
                map.put("SubCat", AppUtils.CapsFirstLetterString(jsonObject.getString("SubCat")));
                map.put("Remarks", jsonObject.getString("Remarks"));
                AppController.category4.add(map);
            }

            executeTogetDrawerMenuItems();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void executeTogetDrawerMenuItems() {
        new AsyncTask<Void, Void, String>() {

            @Override
            protected String doInBackground(Void... params) {

                Thread.currentThread().setPriority(Thread.MAX_PRIORITY);

                String response = "";
                try {
                    List<NameValuePair> postParameters = new ArrayList<>();
                    response = AppUtils.callWebServiceWithMultiParam(Splash_Activity.this, postParameters, QueryUtils.methodtoGetDrawerMenuItems, TAG);

                } catch (Exception e) {
                    e.printStackTrace();
                }
                return response;
            }

            @Override
            protected void onPostExecute(String resultData) {
                try {

                    JSONObject jsonObject = new JSONObject(resultData);

                    if (jsonObject.getString("Status").equalsIgnoreCase("True")) {
                        HeadingJarray = jsonObject.getJSONArray("Data");
                    }

                    moveNextScreen();

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    private void startSplash(final Intent intent) {
        try {

            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void moveNextScreen() {
        try {

//            if (AppController.getSpIsInstall().getBoolean(SPUtils.IS_INSTALL, false)) {

//                if (AppController.getSpIsLogin().getBoolean(SPUtils.IS_LOGIN, false)) {
//                    startSplash(new Intent(Splash_Activity.this, DashBoard_Activity.class));
//                } else {
//                    startSplash(new Intent(Splash_Activity.this, Login_Activity.class));
//                }
            AppController.getSpUserInfo().edit().putString(SPUtils.USER_PopupSts, "T").commit();
              startSplash(new Intent(Splash_Activity.this, Home_Activity.class));
//            } else {
                //First Time Installed
//                 showChooseUsertypedialog();
//            }


//            startActivity(new Intent(this, ThanksScreen_Activity.class).putExtra("ORDERNUMBER","42766193"));


        } catch (Exception e) {
            e.printStackTrace();
            AppUtils.showExceptionDialog(Splash_Activity.this);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == 84) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                executeApplicationStatus();
            } else {

                if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                        || ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        || ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_PHONE_STATE)
                        || ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.CAMERA)
                        || ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.CALL_PHONE)
                        || ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.CLEAR_APP_CACHE)
                        || ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.GET_ACCOUNTS)
                        ) {

                    showDialogOK(
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    switch (which) {
                                        case DialogInterface.BUTTON_POSITIVE:
                                            ActivityCompat.requestPermissions(Splash_Activity.this, PermissionGroup, 84);
                                            break;
                                        case DialogInterface.BUTTON_NEGATIVE:
                                            break;
                                    }
                                }
                            });
                }
                //permission is denied (and never ask again is  checked)
                //shouldShowRequestPermissionRationale will return false
                else {
                    AppUtils.alertDialogWithFinish(this, "Go to settings and Manually Enable these permissions");
                    //proceed with logic by disabling the related features or quit the app.
                }
            }
        }
    }

    private void showDialogOK(DialogInterface.OnClickListener okListener) {
        new AlertDialog.Builder(this)
                .setMessage("These Permissions are required for use this Application")
                .setPositiveButton("OK", okListener)
                .setNegativeButton("Cancel", okListener)
                .create()
                .show();
    }
}