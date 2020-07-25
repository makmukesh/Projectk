package com.vpipl.kalpamrit;

import android.app.Activity;
import android.app.AlertDialog;
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
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.vpipl.kalpamrit.Utils.AppUtils;
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
import java.util.HashMap;
import java.util.List;


/**
 * Created by PC14 on 3/22/2016.
 */
public class Register_Complaint_Activity extends AppCompatActivity {

    private String TAG = "Register_Complaint_Activity";
    private static final int MEDIA_TYPE_IMAGE = 1;

    private TextInputEditText edtxt_IDNumber;
    private TextInputEditText edtxt_name;
    private TextInputEditText edtxt_mobileNumber;
    private TextInputEditText edtxt_email;

    private TextInputEditText txt_department;
    private TextInputEditText edtxt_Query;

    private Button btn_Submit ,btn_reset;

    private String Name;
    private String mobile_number;
    private String email;

    private String department;
    private String Query;
    private Uri imageUri;
    private Bitmap bitmap = null;
    private String selectedImagePath;
    private BottomSheetDialog mBottomSheetDialog;
    private TextView txt_reference_receipt;
    private Button btn_request;
    private Button btn_choose_file;
    private ImageView iv_selected_file;

    public static ArrayList<HashMap<String, String>> departmentList = new ArrayList<>();


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
                    startActivity(new Intent(Register_Complaint_Activity.this, Login_Activity.class));
                else
                    AppUtils.showDialogSignOut(Register_Complaint_Activity.this);
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
        setContentView(R.layout.activity_register_complaint);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);

        try {
            AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);

            Toolbar toolbar = findViewById(R.id.toolbar);
            setSupportActionBar(toolbar);
            getSupportActionBar().setTitle("");
            SetupToolbar();


            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);


//             telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);

            txt_department = findViewById(R.id.txt_department);


            edtxt_IDNumber = findViewById(R.id.edtxt_IDNumber);
            edtxt_name = findViewById(R.id.edtxt_memberName);
            edtxt_mobileNumber = findViewById(R.id.edtxt_mobileNumber);
            edtxt_email = findViewById(R.id.edtxt_email);
            edtxt_Query = findViewById(R.id.edtxt_Query);

            txt_reference_receipt = (TextView) findViewById(R.id.txt_reference_receipt);

            iv_selected_file = (ImageView) findViewById(R.id.iv_selected_file);

            mBottomSheetDialog = new BottomSheetDialog(this);
            View sheetView = this.getLayoutInflater().inflate(R.layout.bottom_sheet, null);
            mBottomSheetDialog.setContentView(sheetView);
            mBottomSheetDialog.setTitle("Complete action using...");

            btn_choose_file = (Button) findViewById(R.id.btn_choose_file);
            btn_request = (Button) findViewById(R.id.btn_request);

            btn_choose_file.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mBottomSheetDialog.show();
                }
            });

            LinearLayout camera = sheetView.findViewById(R.id.bottom_sheet_camera);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                camera.setVisibility(View.GONE);
            } else {
                camera.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        try {

                            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                            imageUri = AppUtils.getOutputMediaFileUri(MEDIA_TYPE_IMAGE, TAG, Register_Complaint_Activity.this);
                            intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
                            startActivityForResult(intent, 1);


                            mBottomSheetDialog.dismiss();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
            }

            LinearLayout gallery = sheetView.findViewById(R.id.bottom_sheet_gallery);
            gallery.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent galleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    startActivityForResult(galleryIntent, 0);


                    mBottomSheetDialog.dismiss();
                }
            });


            edtxt_IDNumber.setText(AppController.getSpUserInfo().getString(SPUtils.USER_ID_NUMBER, ""));
            edtxt_name.setText(AppController.getSpUserInfo().getString(SPUtils.USER_FIRST_NAME, ""));
            edtxt_email.setText(AppController.getSpUserInfo().getString(SPUtils.USER_EMAIL, ""));
            edtxt_mobileNumber.setText(AppController.getSpUserInfo().getString(SPUtils.USER_MOBILE_NO, ""));

            txt_department.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (departmentList.size() != 0) {
                        showDepartmentDialog();
                        txt_department.clearFocus();
                    }
                }
            });

            btn_Submit = findViewById(R.id.btn_Submit);
            btn_reset = findViewById(R.id.btn_reset);
            btn_Submit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    AppUtils.hideKeyboardOnClick(Register_Complaint_Activity.this, view);
                    validateQueryRequest();
                }
            });
            btn_reset.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    AppUtils.hideKeyboardOnClick(Register_Complaint_Activity.this, view);
                    ResetData();
                }
            });

            if (AppUtils.isNetworkAvailable(this))
                executeDepartmentRequest();
            else
                AppUtils.alertDialogWithFinish(this,getString(R.string.txt_networkAlert));

        } catch (Exception e) {
            e.printStackTrace();
            AppUtils.showExceptionDialog(Register_Complaint_Activity.this);
        }
    }
    private void ResetData() {
        try {
            edtxt_Query.setText("");
         } catch (Exception e) {
            e.printStackTrace();
            AppUtils.showExceptionDialog(Register_Complaint_Activity.this);
        }
    }

    private void validateQueryRequest() {
        try {

            Name = edtxt_name.getText().toString().trim();
            department = txt_department.getText().toString().trim();
            Query = edtxt_Query.getText().toString().trim();
            mobile_number = edtxt_mobileNumber.getText().toString().trim();
            email = edtxt_email.getText().toString().trim();

            if (TextUtils.isEmpty(Query)) {
                AppUtils.alertDialog(Register_Complaint_Activity.this, "Please Enter Query/Complaint");
                edtxt_Query.requestFocus();
            } else if (TextUtils.isEmpty(department)) {
                AppUtils.alertDialog(Register_Complaint_Activity.this, "Please Select Department");
                txt_department.requestFocus();
            } else if (!AppUtils.isNetworkAvailable(Register_Complaint_Activity.this)) {
                AppUtils.alertDialog(Register_Complaint_Activity.this, getResources().getString(R.string.txt_networkAlert));
            } else {
                startSubmitQuery();
            }
        } catch (Exception e) {
            e.printStackTrace();
            AppUtils.showExceptionDialog(Register_Complaint_Activity.this);
        }
    }

    private void startSubmitQuery() {
        try {
            if (AppUtils.isNetworkAvailable(Register_Complaint_Activity.this)) {

                List<NameValuePair> postParameters = new ArrayList<>();

                postParameters.add(new BasicNameValuePair("FormNo", AppController.getSpUserInfo().getString(SPUtils.USER_FORM_NUMBER, "")));
                postParameters.add(new BasicNameValuePair("IDNo", AppController.getSpUserInfo().getString(SPUtils.USER_ID_NUMBER, "")));
                postParameters.add(new BasicNameValuePair("MemName", AppController.getSpUserInfo().getString(SPUtils.USER_FIRST_NAME, "")));
                postParameters.add(new BasicNameValuePair("MobileNo", "" + mobile_number.trim()));
                postParameters.add(new BasicNameValuePair("EmailID", "" + email.trim()));

                String DepartmentId = "0";
                for (int i = 0; i < departmentList.size(); i++) {
                    if (department.equalsIgnoreCase(departmentList.get(i).get("DeptName"))) {
                        DepartmentId = departmentList.get(i).get("DeptCode");
                    }
                }

                postParameters.add(new BasicNameValuePair("DepartmentID", "" + DepartmentId));
                postParameters.add(new BasicNameValuePair("DepartmentText", "" + department));
                postParameters.add(new BasicNameValuePair("ComplaintText", "" + Query.trim()));
                postParameters.add(new BasicNameValuePair("Photo", ""+AppUtils.getBase64StringFromBitmap(bitmap)));

//                postParameters.add(new BasicNameValuePair("IPAddress", telephonyManager.getDeviceId()));

                executeSubmitQueryRequest(postParameters);
            } else {
                AppUtils.alertDialog(Register_Complaint_Activity.this, getResources().getString(R.string.txt_networkAlert));
            }
        } catch (Exception e) {
            e.printStackTrace();
            AppUtils.showExceptionDialog(Register_Complaint_Activity.this);
        }
    }

    private void executeDepartmentRequest() {
        new AsyncTask<Void, Void, String>() {
            protected void onPreExecute() {
                AppUtils.showProgressDialog(Register_Complaint_Activity.this);
            }

            @Override
            protected String doInBackground(Void... params) {
                String response = "";
                try {
                    List<NameValuePair> postParameters = new ArrayList<>();
                    response = AppUtils.callWebServiceWithMultiParam(Register_Complaint_Activity.this, postParameters, QueryUtils.methodMaster_FillDepartmentQuery, TAG);
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
                            getDepartmentResult(jsonArrayData);
                        } else {
                            AppUtils.alertDialog(Register_Complaint_Activity.this, jsonObject.getString("Message"));
                        }
                    } else {
                        AppUtils.alertDialog(Register_Complaint_Activity.this, jsonObject.getString("Message"));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    private void getDepartmentResult(JSONArray jsonArray) {
        try {
            departmentList.clear();

            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                HashMap<String, String> map = new HashMap<>();

                map.put("DeptCode", jsonObject.getString("DeptCode"));
                map.put("DeptName", WordUtils.capitalizeFully(jsonObject.getString("DeptName")));

                departmentList.add(map);

                txt_department.setText(WordUtils.capitalizeFully(jsonObject.getString("DeptName")));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void showDepartmentDialog() {
        try {
            final String[] stateArray = new String[departmentList.size()];
            for (int i = 0; i < departmentList.size(); i++) {
                stateArray[i] = departmentList.get(i).get("DeptName");
            }

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Select Department");
            builder.setItems(stateArray, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int item) {
                    // Do something with the selection
                    txt_department.setText(stateArray[item]);
                }
            });
            builder.create().show();
        } catch (Exception e) {
            e.printStackTrace();
            AppUtils.showExceptionDialog(Register_Complaint_Activity.this);
        }
    }

    private void executeSubmitQueryRequest(final List<NameValuePair> postParameters) {
        try {
            if (AppUtils.isNetworkAvailable(Register_Complaint_Activity.this)) {
                new AsyncTask<Void, Void, String>() {
                    protected void onPreExecute() {
                        AppUtils.showProgressDialog(Register_Complaint_Activity.this);
                    }

                    @Override
                    protected String doInBackground(Void... params) {
                        String response = "";
                        try {
                            response = AppUtils.callWebServiceWithMultiParam(Register_Complaint_Activity.this, postParameters, QueryUtils.methodToSubmitQuery, TAG);
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
                            JSONArray jsonArrayData = jsonObject.getJSONArray("Data");

                            if (jsonObject.getString("Status").equalsIgnoreCase("True")) {
                                if (jsonArrayData.length() != 0) {

                                    AppUtils.alertDialogWithFinish(Register_Complaint_Activity.this, "" + jsonObject.getString("Message"));
                                } else {
                                    AppUtils.alertDialog(Register_Complaint_Activity.this, jsonObject.getString("Message"));
                                }
                            } else {
                                AppUtils.alertDialog(Register_Complaint_Activity.this, jsonObject.getString("Message"));
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            AppUtils.showExceptionDialog(Register_Complaint_Activity.this);
                        }
                    }
                }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            }
        } catch (Exception e) {
            e.printStackTrace();
            AppUtils.showExceptionDialog(Register_Complaint_Activity.this);
        }
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        try {

            if (resultCode == Activity.RESULT_OK) {
                if (requestCode == 0) {
                    if (data != null) {
                        String filepath = AppUtils.getPath(data.getData(), Register_Complaint_Activity.this);

                        if (filepath.length() > 0) {
                            selectedImagePath = filepath;
                            pickImageFromGallery();
                        }
                    }
                } else if (requestCode == 1) {

                    Uri selectedImageUri = imageUri;
                    selectedImagePath = selectedImageUri.getPath();
                    pickImageFromGallery();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            AppUtils.showExceptionDialog(Register_Complaint_Activity.this);
        }
    }

    private void pickImageFromGallery() {
        try {
            Matrix matrix = new Matrix();
            int rotate = 0;

            File imageFile = new File(selectedImagePath);
            String selectedImageName = imageFile.getName();
            txt_reference_receipt.setText(selectedImageName);

            try {
                ExifInterface exif = new ExifInterface(imageFile.getAbsolutePath());
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
                AppUtils.showExceptionDialog(Register_Complaint_Activity.this);
            }

            // Set the Image in ImageView after decoding the String
            matrix.postRotate(rotate);
            Bitmap user_image = BitmapFactory.decodeFile(selectedImagePath);

            if (imageFile.length() > 10000) {
                bitmap = AppUtils.compressImage(selectedImagePath);
                File fileSize = new File(AppUtils.lastCompressedImageFileName);
                if (fileSize.length() > 1050000) {
                    AppUtils.alertDialog(Register_Complaint_Activity.this, "Maximum image size limit 1 MB.");
                } else {
                    iv_selected_file.setVisibility(View.VISIBLE);
                    iv_selected_file.setImageBitmap(bitmap);
                }
            } else {
                int width, height;
                if (user_image.getWidth() > 500) {
                    width = 500;
                } else {
                    width = user_image.getWidth();
                }
                if (user_image.getHeight() > 500) {
                    height = 500;
                } else {
                    height = user_image.getHeight();
                }

                bitmap = Bitmap.createBitmap(user_image, 0, 0, width, height, matrix, true);
                iv_selected_file.setVisibility(View.VISIBLE);
                iv_selected_file.setImageBitmap(bitmap);
            }
        } catch (Exception e) {
            e.printStackTrace();
            AppUtils.showExceptionDialog(Register_Complaint_Activity.this);
        }
    }
}