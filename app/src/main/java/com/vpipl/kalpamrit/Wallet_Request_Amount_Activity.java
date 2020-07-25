package com.vpipl.kalpamrit;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.BottomSheetDialog;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TextInputEditText;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.widget.Toolbar;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.LinearLayout;
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

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import id.zelory.compressor.Compressor;

/**
 * Created by PC14 on 3/22/2016.
 */
public class Wallet_Request_Amount_Activity extends AppCompatActivity {

    public int RESULT_GALLERY = 0;
    public int CAMERA_REQUEST = 1;

    private static final int MEDIA_TYPE_IMAGE = 1;
    private static DrawerLayout drawer;
    private static NavigationView navigationView;
    private String TAG = "Wallet_Request_Amount_Activity";
    private TextInputEditText edtxt_amount;
    private TextInputEditText edtxt_transaction_no;
    private TextInputEditText txt_transaction_date;
    private TextInputEditText txt_choose_bank;
    private TextView txt_reference_receipt;
    private Button btn_request ,btn_reset;
    private Button btn_choose_file;
    private ImageView iv_selected_file;
    private String[] bankArray;
    private TelephonyManager telephonyManager;
    private String amount;
    private String transaction_no;
    private String transaction_date;
    private String bank_name;
    private Uri imageUri;
    private Bitmap bitmap = null;
    private Calendar myCalendar;
    private SimpleDateFormat sdf;
    private String selectedImagePath;
    private BottomSheetDialog mBottomSheetDialog;
    private TextView txt_welcome_name;
    private TextView txt_id_number;
    private TextView txt_available_wb;
    private ArrayList<String> listDataHeader;
    private HashMap<String, List<String>> listDataChild;
    private int lastExpandedPosition = -1;
    private ExpandableListView expListView;
    private CircularImageView profileImage;
    private JSONArray HeadingJarray;

    private DatePickerDialog.OnDateSetListener myDateListener = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            try {

                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, monthOfYear);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

                if (new Date().after(myCalendar.getTime())) {

                    txt_transaction_date.setText(sdf.format(myCalendar.getTime()));

                } else {
                    AppUtils.alertDialog(Wallet_Request_Amount_Activity.this, "Select Previous Dates");
                }

            } catch (Exception e) {
                e.printStackTrace();

            }
        }
    };

    private DatePickerDialog datePickerDialog;

    private void showdatePicker() {
        Calendar calendar = Calendar.getInstance();
        datePickerDialog = new DatePickerDialog(this, myDateListener, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
        datePickerDialog.getDatePicker().setMaxDate(calendar.getTime().getTime());
        datePickerDialog.show();
    }

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
                    startActivity(new Intent(Wallet_Request_Amount_Activity.this, Login_Activity.class));
                else
                    AppUtils.showDialogSignOut(Wallet_Request_Amount_Activity.this);
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
        setContentView(R.layout.activity_wallet_request_amount);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);

        try {
            AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);

            Toolbar toolbar = findViewById(R.id.toolbar);
            setSupportActionBar(toolbar);
            getSupportActionBar().setTitle("");
            SetupToolbar();

            executeLoginRequest();

            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);


            telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);

            myCalendar = Calendar.getInstance();
            sdf = new SimpleDateFormat("dd MMM yyyy");

            txt_transaction_date = findViewById(R.id.txt_transaction_date);
            txt_choose_bank = findViewById(R.id.txt_choose_bank);
            txt_reference_receipt = findViewById(R.id.txt_reference_receipt);

            edtxt_amount = findViewById(R.id.edtxt_amount);
            edtxt_transaction_no = findViewById(R.id.edtxt_transaction_no);

            iv_selected_file = findViewById(R.id.iv_selected_file);

            mBottomSheetDialog = new BottomSheetDialog(this);
            View sheetView = this.getLayoutInflater().inflate(R.layout.bottom_sheet, null);
            mBottomSheetDialog.setContentView(sheetView);
            mBottomSheetDialog.setTitle("Complete action using...");

            btn_choose_file = findViewById(R.id.btn_choose_file);
            btn_request = findViewById(R.id.btn_request);
            btn_reset = findViewById(R.id.btn_reset);

            btn_choose_file.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mBottomSheetDialog.show();
                }
            });

            LinearLayout camera = sheetView.findViewById(R.id.bottom_sheet_camera);
            LinearLayout gallery = sheetView.findViewById(R.id.bottom_sheet_gallery);

            camera.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    imageUri = AppUtils.getOutputMediaFileUri(1, TAG, Wallet_Request_Amount_Activity.this);
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
                    startActivityForResult(intent, CAMERA_REQUEST);
                    mBottomSheetDialog.dismiss();
                }
            });

            gallery.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent galleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    startActivityForResult(galleryIntent, RESULT_GALLERY);
                    mBottomSheetDialog.dismiss();
                }
            });

            txt_choose_bank.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (AppController.bankList.size() != 0) {
                        showBankDialog();
                    } else {
                        executeBankRequest();
                    }
                }
            });

            btn_request.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    AppUtils.hideKeyboardOnClick(Wallet_Request_Amount_Activity.this, view);
                    ValidateData();
                }
            });

            btn_reset.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    AppUtils.hideKeyboardOnClick(Wallet_Request_Amount_Activity.this, view);
                    ResetData();
                }
            });

            txt_transaction_date.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showdatePicker();
                }
            });

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
                            startActivity(new Intent(Wallet_Request_Amount_Activity.this, Profile_View_Activity.class));
                        } else {
                            startActivity(new Intent(Wallet_Request_Amount_Activity.this, Login_Activity.class));
                        }

                        if (drawer.isDrawerOpen(GravityCompat.START)) {
                            drawer.closeDrawer(GravityCompat.START);
                        }
                    }
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
            AppUtils.showExceptionDialog(Wallet_Request_Amount_Activity.this);
        }
    }
    private void ResetData() {
        try {
            edtxt_amount.setText("");
            edtxt_transaction_no.setText("");
            txt_transaction_date.setText("");
            txt_choose_bank.setText("");
            iv_selected_file.setVisibility(View.GONE);
        } catch (Exception e) {
            e.printStackTrace();
            AppUtils.showExceptionDialog(Wallet_Request_Amount_Activity.this);
        }
    }
    private void ValidateData() {
        try {

            amount = edtxt_amount.getText().toString().trim();
            transaction_no = edtxt_transaction_no.getText().toString().trim();
            transaction_date = txt_transaction_date.getText().toString().trim();
            bank_name = txt_choose_bank.getText().toString().trim();

            float amt = 0;
            try {
                amt = Float.parseFloat(amount);
            } catch (Exception ignored) {

            }
            if (TextUtils.isEmpty(amount)) {
                AppUtils.alertDialog(Wallet_Request_Amount_Activity.this, "Amount is Required");
                edtxt_amount.requestFocus();
            } else if (amt <= 0) {
                AppUtils.alertDialog(Wallet_Request_Amount_Activity.this, "Invalid Amount");
                edtxt_amount.requestFocus();
            } else if (amt > 99999) {
                AppUtils.alertDialog(Wallet_Request_Amount_Activity.this, "Maximum Amount Limit is 99999");
                edtxt_amount.requestFocus();
            } else if (TextUtils.isEmpty(bank_name)) {
                AppUtils.alertDialog(Wallet_Request_Amount_Activity.this, "Select Bank");
                txt_choose_bank.requestFocus();
            } else if (TextUtils.isEmpty(transaction_no)) {
                AppUtils.alertDialog(Wallet_Request_Amount_Activity.this, "Transaction Number is Required");
                edtxt_transaction_no.requestFocus();
            } else if (TextUtils.isEmpty(transaction_date)) {
                AppUtils.alertDialog(Wallet_Request_Amount_Activity.this, "Transaction Date is Required");
                txt_transaction_date.requestFocus();
            } else if (TextUtils.isEmpty(selectedImagePath)) {
                AppUtils.alertDialog(Wallet_Request_Amount_Activity.this, "Reference Receipt is Required");
                btn_choose_file.requestFocus();
            } else if (!AppUtils.isNetworkAvailable(Wallet_Request_Amount_Activity.this)) {
                AppUtils.alertDialog(Wallet_Request_Amount_Activity.this, getResources().getString(R.string.txt_networkAlert));
            } else {
                startRequestAmount();
            }
        } catch (Exception e) {
            e.printStackTrace();
            AppUtils.showExceptionDialog(Wallet_Request_Amount_Activity.this);
        }
    }

    private void startRequestAmount() {
        try {
            if (AppUtils.isNetworkAvailable(Wallet_Request_Amount_Activity.this)) {

                List<NameValuePair> postParameters = new ArrayList<>();
                postParameters.add(new BasicNameValuePair("FormNo", AppController.getSpUserInfo().getString(SPUtils.USER_FORM_NUMBER, "")));
                postParameters.add(new BasicNameValuePair("IdNo", AppController.getSpUserInfo().getString(SPUtils.USER_ID_NUMBER, "")));
                postParameters.add(new BasicNameValuePair("BankName", bank_name));
                postParameters.add(new BasicNameValuePair("RequestedAmount", amount));
                postParameters.add(new BasicNameValuePair("DateValue", transaction_date));
                postParameters.add(new BasicNameValuePair("TransNo", transaction_no));
                postParameters.add(new BasicNameValuePair("ImageCode", AppUtils.getBase64StringFromBitmap(bitmap)));

                String Bankid = "0";
                for (int i = 0; i < AppController.bankList.size(); i++) {
                    if (bank_name.equalsIgnoreCase(AppController.bankList.get(i).get("Bank"))) {
                        Bankid = AppController.bankList.get(i).get("BID");
                    }
                }

                postParameters.add(new BasicNameValuePair("BankID", "" + Bankid));
                postParameters.add(new BasicNameValuePair("IP", telephonyManager.getDeviceId()));

                executeRequestAmount(postParameters);

            } else {
                AppUtils.alertDialog(Wallet_Request_Amount_Activity.this, getResources().getString(R.string.txt_networkAlert));
            }
        } catch (Exception e) {
            e.printStackTrace();
            AppUtils.showExceptionDialog(Wallet_Request_Amount_Activity.this);
        }
    }

    private void executeBankRequest() {
        new AsyncTask<Void, Void, String>() {
            protected void onPreExecute() {
                AppUtils.showProgressDialog(Wallet_Request_Amount_Activity.this);
            }

            @Override
            protected String doInBackground(Void... params) {
                String response = "";
                try {
                    List<NameValuePair> postParameters = new ArrayList<>();
                    response = AppUtils.callWebServiceWithMultiParam(Wallet_Request_Amount_Activity.this, postParameters, QueryUtils.methodMaster_FillBank, TAG);
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
                            getBankResult(jsonArrayData);
                        } else {
                            AppUtils.alertDialog(Wallet_Request_Amount_Activity.this, jsonObject.getString("Message"));
                        }
                    } else {
                        AppUtils.alertDialog(Wallet_Request_Amount_Activity.this, jsonObject.getString("Message"));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    private void getBankResult(JSONArray jsonArray) {
        try {
            AppController.bankList.clear();

            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                HashMap<String, String> map = new HashMap<>();

                map.put("BID", jsonObject.getString("BID"));
                map.put("Bank", WordUtils.capitalizeFully(jsonObject.getString("Bank")));

                AppController.bankList.add(map);
            }


            showBankDialog();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void showBankDialog() {

        try {
            bankArray = new String[AppController.bankList.size()];
            for (int i = 0; i < AppController.bankList.size(); i++) {
                bankArray[i] = AppController.bankList.get(i).get("Bank");
            }

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Select Bank");
            builder.setItems(bankArray, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int item) {
                    txt_choose_bank.setText(bankArray[item]);
                }
            });
            builder.create().show();
        } catch (Exception e) {
            e.printStackTrace();
            AppUtils.showExceptionDialog(Wallet_Request_Amount_Activity.this);
        }
    }

    private void executeRequestAmount(final List<NameValuePair> postParameters) {
        try {
            if (AppUtils.isNetworkAvailable(Wallet_Request_Amount_Activity.this)) {
                new AsyncTask<Void, Void, String>() {
                    protected void onPreExecute() {
                        AppUtils.showProgressDialog(Wallet_Request_Amount_Activity.this);
                    }

                    @Override
                    protected String doInBackground(Void... params) {
                        String response = "";
                        try {
                            response = AppUtils.callWebServiceWithMultiParam(Wallet_Request_Amount_Activity.this, postParameters, QueryUtils.methodToRequestWalletAmount, TAG);
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
//                            JSONArray jsonArrayData = jsonObject.getJSONArray("Data");

                            if (jsonObject.getString("Status").equalsIgnoreCase("True")) {
                                ResetClass();
                                AppUtils.alertDialogWithFinish(Wallet_Request_Amount_Activity.this, "" + jsonObject.getString("Message"));
                            } else {
                                //ResetClass();
                                AppUtils.alertDialog(Wallet_Request_Amount_Activity.this, jsonObject.getString("Message"));
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            AppUtils.showExceptionDialog(Wallet_Request_Amount_Activity.this);
                        }
                    }
                }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            }
        } catch (Exception e) {
            e.printStackTrace();
            AppUtils.showExceptionDialog(Wallet_Request_Amount_Activity.this);
        }
    }

    private void ResetClass() {
        iv_selected_file.setVisibility(View.GONE);
        edtxt_amount.setText("");
        edtxt_transaction_no.setText("");
        txt_transaction_date.setText("");
        txt_choose_bank.setText("");
        txt_reference_receipt.setText("");
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        try {

            if (resultCode == Activity.RESULT_OK) {
                if (requestCode == RESULT_GALLERY) {
                    if (data != null) {
                        imageUri = data.getData();
                        String filepath = AppUtils.getPath(data.getData(), Wallet_Request_Amount_Activity.this);

                        if (filepath.length() > 0) {
                            selectedImagePath = filepath;
                            pickImageFromGallery();
                        }
                    }
                } else if (requestCode == CAMERA_REQUEST) {

                    Uri selectedImageUri = imageUri;
                    selectedImagePath = selectedImageUri.getPath();
                    pickImageFromGallery();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            AppUtils.showExceptionDialog(Wallet_Request_Amount_Activity.this);
        }
    }

    private void pickImageFromGallery() {
        try {

            Matrix matrix = new Matrix();
            int rotate = 0;

            File imageFile = new File(selectedImagePath);
            long fileSizeInBytes = imageFile.length();
            long fileSizeInKB = fileSizeInBytes / 1024;
            Log.e(TAG, "Image Size(KB) before compress : " + fileSizeInKB);

            if (fileSizeInKB <= 5130) {
                try {
                    ExifInterface exif = new ExifInterface((imageFile.getName()));
                    int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);

                    switch (orientation) {
                        case ExifInterface.ORIENTATION_ROTATE_270:
                            rotate -= 90;
                            break;
                        case ExifInterface.ORIENTATION_ROTATE_180:
                            rotate -= 90;
                            break;
                        case ExifInterface.ORIENTATION_ROTATE_90:
                            rotate -= 90;
                            break;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

                int nh =0;
                if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
                {
                    Bitmap bitmapImage = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imageUri);

                    nh = (int) (bitmapImage.getHeight() * (550.0 / bitmapImage.getWidth()));

                    matrix.postRotate(rotate);
                    bitmap = Bitmap.createBitmap(bitmapImage, 0, 0, bitmapImage.getWidth(), bitmapImage.getHeight(), matrix, true);
                    bitmapImage = null;
                } else {

                    File compressedImageFile = new Compressor(this).compressToFile(imageFile);
                    Log.e(TAG, "Image Size(KB) after compress one: " + compressedImageFile.length() / 1024);
                    Bitmap compressedImageBitmap = new Compressor(this).compressToBitmap(compressedImageFile);

                    nh = (int) (compressedImageBitmap.getHeight() * (480.0 / compressedImageBitmap.getWidth()));

                    matrix.postRotate(rotate);
                    bitmap = Bitmap.createBitmap(compressedImageBitmap, 0, 0, compressedImageBitmap.getWidth(), compressedImageBitmap.getHeight(), matrix, true);
                    compressedImageBitmap = null;
                }

                if (bitmap.getWidth() > 500) {
                    Bitmap scaled = Bitmap.createScaledBitmap(bitmap, 480, nh, true);
                    bitmap = scaled;
                    scaled = null;
                }

                iv_selected_file.setVisibility(View.VISIBLE);
                iv_selected_file.setImageBitmap(bitmap);

            } else
                AppUtils.alertDialog(this, "Selected file exceed the allowable file size limit (5 MB)");

        } catch (Exception e) {
            e.printStackTrace();
            AppUtils.showExceptionDialog(Wallet_Request_Amount_Activity.this);
        }
    }


    private void executeLoginRequest() {
        try {

            if (AppUtils.isNetworkAvailable(Wallet_Request_Amount_Activity.this)) {
                new AsyncTask<Void, Void, String>() {
                    protected void onPreExecute() {
                        AppUtils.showProgressDialog(Wallet_Request_Amount_Activity.this);
                    }

                    @Override
                    protected String doInBackground(Void... params) {
                        String response = "";
                        try {

                            List<NameValuePair> postParameters = new ArrayList<>();
                            postParameters.add(new BasicNameValuePair("UserID", AppController.getSpUserInfo().getString(SPUtils.USER_ID_NUMBER, "")));
                            postParameters.add(new BasicNameValuePair("Password", AppController.getSpUserInfo().getString(SPUtils.USER_PASSWORD, "")));
                            postParameters.add(new BasicNameValuePair("UserType", "D"));
                            response = AppUtils.callWebServiceWithMultiParam(Wallet_Request_Amount_Activity.this, postParameters, QueryUtils.methodMemberLoginOnPortal, TAG);

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

                                Toast.makeText(Wallet_Request_Amount_Activity.this, "Please Login to continue..", Toast.LENGTH_SHORT).show();

                                AppController.getSpUserInfo().edit().clear().commit();
                                AppController.getSpIsLogin().edit().clear().commit();

                                Intent intent = new Intent(Wallet_Request_Amount_Activity.this, Login_Activity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                intent.putExtra("SendToHome", true);
                                startActivity(intent);
                                finish();

                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            AppUtils.showExceptionDialog(Wallet_Request_Amount_Activity.this);
                        }
                    }
                }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            }
        } catch (Exception e) {
            e.printStackTrace();
            AppUtils.showExceptionDialog(Wallet_Request_Amount_Activity.this);
        }
    }

    public void continueapp() {

        enableExpandableList();
        LoadNavigationHeaderItems();
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();

        try {
            AppUtils.dismissProgressDialog();
            ////overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        } catch (Exception e) {
            e.printStackTrace();
            AppUtils.showExceptionDialog(Wallet_Request_Amount_Activity.this);
        }

        System.gc();
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
                    startActivity(new Intent(Wallet_Request_Amount_Activity.this, DashBoard_Activity.class));
                    if (drawer.isDrawerOpen(GravityCompat.START)) {
                        drawer.closeDrawer(GravityCompat.START);
                    }
                } else if (GroupTitle.trim().equalsIgnoreCase("Logout")) {
                    AppUtils.showDialogSignOut(Wallet_Request_Amount_Activity.this);
                } else if (GroupTitle.trim().equalsIgnoreCase("New Joining")) {
                    startActivity(new Intent(Wallet_Request_Amount_Activity.this, Register_Activity.class));
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
                    startActivity(new Intent(Wallet_Request_Amount_Activity.this, Profile_View_Activity.class));
                } else if (ChildItemTitle.trim().equalsIgnoreCase(getResources().getString(R.string.change_password))) {
                    startActivity(new Intent(Wallet_Request_Amount_Activity.this, Change_Password_Activity.class));
                } else if (ChildItemTitle.trim().equalsIgnoreCase(getResources().getString(R.string.new_joining))) {
                    startActivity(new Intent(Wallet_Request_Amount_Activity.this, Register_Activity.class));
                } else if (ChildItemTitle.trim().equalsIgnoreCase(getResources().getString(R.string.upload_kyc))) {
                    startActivity(new Intent(Wallet_Request_Amount_Activity.this, KYCUploadDocument_Activity.class).putExtra("HEADING", "Update"));
                } else if (ChildItemTitle.trim().equalsIgnoreCase(getResources().getString(R.string.generation_structure))) {
                    startActivity(new Intent(Wallet_Request_Amount_Activity.this, Sponsor_genealogy_Activity.class));
                } else if (ChildItemTitle.trim().equalsIgnoreCase(getResources().getString(R.string.sponsor_downline))) {
                    startActivity(new Intent(Wallet_Request_Amount_Activity.this, Sponsor_team_details_Activity.class).putExtra("Action", "Sponsor"));
                } else if (ChildItemTitle.trim().equalsIgnoreCase(getResources().getString(R.string.bv_detail_report))) {
                    startActivity(new Intent(Wallet_Request_Amount_Activity.this, Sponsor_team_details_Activity.class).putExtra("Action", "Direct"));
                } else if (ChildItemTitle.trim().equalsIgnoreCase(getResources().getString(R.string.welcome_letter))) {
                    startActivity(new Intent(Wallet_Request_Amount_Activity.this, WelcomeLetter_Activity.class));
                } else if (ChildItemTitle.trim().equalsIgnoreCase(getResources().getString(R.string.purchase_reports))) {
                    startActivity(new Intent(Wallet_Request_Amount_Activity.this, Repurchase_BV_Detail.class));
                } else if (ChildItemTitle.trim().equalsIgnoreCase("Team Repurchase BV Summary")) {
                    startActivity(new Intent(Wallet_Request_Amount_Activity.this, Repurchase_BV_Summary_Team_Activity.class));
                } else if (ChildItemTitle.trim().equalsIgnoreCase("Repurchase Bill Summary")) {
                    startActivity(new Intent(Wallet_Request_Amount_Activity.this, Repurchase_Bill_Summary.class));
                } else if (ChildItemTitle.trim().equalsIgnoreCase(getResources().getString(R.string.wallet_detail))) {
                    startActivity(new Intent(Wallet_Request_Amount_Activity.this, Wallet_Transaction_Report_Activity.class));
                } else if (ChildItemTitle.trim().equalsIgnoreCase("Wallet Request Report")) {
                    startActivity(new Intent(Wallet_Request_Amount_Activity.this, Wallet_Request_Status_Report_Activity.class));
                } else if (ChildItemTitle.trim().equalsIgnoreCase("Request For Wallet Amount")) {
                    startActivity(new Intent(Wallet_Request_Amount_Activity.this, Wallet_Request_Amount_Activity.class));
                } else if (ChildItemTitle.trim().equalsIgnoreCase(getResources().getString(R.string.monthly_incentive))) {
                    startActivity(new Intent(Wallet_Request_Amount_Activity.this, Monthly_Incentive_Activity.class));
                } else if (ChildItemTitle.trim().equalsIgnoreCase(getResources().getString(R.string.monthly_incentive_detail_report))) {
                    startActivity(new Intent(Wallet_Request_Amount_Activity.this, Monthly_Incentive_Detail_Activity.class));
                } else if (ChildItemTitle.trim().equalsIgnoreCase("TDS Detail Report")) {
                    startActivity(new Intent(Wallet_Request_Amount_Activity.this, TDS_detail_report_Activity.class));
                } else if (ChildItemTitle.trim().equalsIgnoreCase("Update Profile")) {
                    startActivity(new Intent(Wallet_Request_Amount_Activity.this, Profile_Update_Activity.class));
                } else if (ChildItemTitle.trim().equalsIgnoreCase("View KYC Documents")) {
                    startActivity(new Intent(Wallet_Request_Amount_Activity.this, KYCUploadDocument_Activity.class).putExtra("HEADING", "View"));
                } else if (ChildItemTitle.trim().equalsIgnoreCase("Register Enquiry/Complaint")) {
                    startActivity(new Intent(Wallet_Request_Amount_Activity.this, Register_Complaint_Activity.class));
                } else if (ChildItemTitle.trim().equalsIgnoreCase("View Open/Pending Queries")) {
                    startActivity(new Intent(Wallet_Request_Amount_Activity.this, ViewPendingQueries_Activity.class).putExtra("HEADING", "Open"));
                } else if (ChildItemTitle.trim().equalsIgnoreCase("View Closed Queries")) {
                    startActivity(new Intent(Wallet_Request_Amount_Activity.this, ViewPendingQueries_Activity.class).putExtra("HEADING", "Closed"));
                } else if (ChildItemTitle.trim().equalsIgnoreCase("Product Wallet Detail")) {
                    startActivity(new Intent(Wallet_Request_Amount_Activity.this, ProductWallet_Transaction_Report_Activity.class));
                } else if (ChildItemTitle.trim().equalsIgnoreCase("Wallet To Bank Transfer Detail")) {
                    startActivity(new Intent(Wallet_Request_Amount_Activity.this, Wallet_Bank_Transfer_Report_Activity.class));
                }else if (ChildItemTitle.trim().equalsIgnoreCase("ID Card")) {
                    startActivity(new Intent(Wallet_Request_Amount_Activity.this, ID_card_Activity.class));
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
            if (AppUtils.isNetworkAvailable(Wallet_Request_Amount_Activity.this)) {
                new AsyncTask<Void, Void, String>() {
                    @Override
                    protected String doInBackground(Void... params) {
                        String response = "";
                        try {
                            List<NameValuePair> postParameters = new ArrayList<>();
                            postParameters.add(new BasicNameValuePair("Formno", AppController.getSpUserInfo().getString(SPUtils.USER_FORM_NUMBER, "")));
                            response = AppUtils.callWebServiceWithMultiParam(Wallet_Request_Amount_Activity.this,
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
                AppUtils.showProgressDialog(Wallet_Request_Amount_Activity.this);
            }

            @Override
            protected String doInBackground(Void... params) {

                Thread.currentThread().setPriority(Thread.MAX_PRIORITY);

                String response = "";
                try {
                    List<NameValuePair> postParameters = new ArrayList<>();
                    response = AppUtils.callWebServiceWithMultiParam(Wallet_Request_Amount_Activity.this, postParameters, QueryUtils.methodtoGetDrawerMenuItems, TAG);

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