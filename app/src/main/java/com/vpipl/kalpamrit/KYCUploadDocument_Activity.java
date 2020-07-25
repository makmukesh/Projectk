package com.vpipl.kalpamrit;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.BottomSheetDialog;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.widget.Toolbar;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import id.zelory.compressor.Compressor;

/**
 * Created by PC14 on 12-Apr-16.
 */
public class KYCUploadDocument_Activity extends AppCompatActivity {

    private static final String TAG = "KYCUploadDocument";

    private static final int RESULT_GALLERY = 0;
    private static final int CAMERA_REQUEST = 1;
    //    layout_photoProof;
    private static final int MEDIA_TYPE_IMAGE = 1;
    private static DrawerLayout drawer;
    //    imgView_photoProof;
    private static NavigationView navigationView;
    private LinearLayout layout_AdrsProf;
    private LinearLayout layout_IdProf;
    private LinearLayout layout_pannoProf;
    private ImageView imgView_pan_card_Prof;
    private ImageView imgView_AdrsProf;
    private ImageView imgView_IdProf;
    private String whichUpload = "";
    private String selectedImagePath = "";
    private Uri imageUri;
    private Bitmap bitmap = null;

    //    isPhotoProof = false;
    private Boolean isAddressProof = false;
    private Boolean isIdProof = false;
    private Boolean isPancardProof = false;
    private BottomSheetDialog mBottomSheetDialog;
    private String heading = "View";
    private TextView txt_heading;
    private TextView txt_welcome_name;
    private TextView txt_id_number;
    private TextView txt_available_wb;
    private ArrayList<String> listDataHeader;
    private HashMap<String, List<String>> listDataChild;
    private int lastExpandedPosition = -1;
    private ExpandableListView expListView;
    private CircularImageView profileImage;

    private JSONArray HeadingJarray;
    LinearLayout ll_tab_pan_card ,ll_tab_address_proof ,ll_tab_id_proof ;
    TextView txt_tab_pan_no ,txt_tab_address_proof ,txt_tab_id_proof ;

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
                    startActivity(new Intent(KYCUploadDocument_Activity.this, Login_Activity.class));
                else
                    AppUtils.showDialogSignOut(KYCUploadDocument_Activity.this);
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
        setContentView(R.layout.activity_kyc_document);

        try {
            AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);

            Toolbar toolbar = findViewById(R.id.toolbar);
            setSupportActionBar(toolbar);
            getSupportActionBar().setTitle("");
            SetupToolbar();

            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

            drawer = findViewById(R.id.drawer_layout);
            navigationView = findViewById(R.id.nav_view);
            View navHeaderView = navigationView.getHeaderView(0);
            txt_welcome_name = navHeaderView.findViewById(R.id.txt_welcome_name);
            txt_available_wb = navHeaderView.findViewById(R.id.txt_available_wb);
            txt_id_number = navHeaderView.findViewById(R.id.txt_id_number);
            profileImage = navHeaderView.findViewById(R.id.iv_Profile_Pic);
            LinearLayout LL_Nav = navHeaderView.findViewById(R.id.LL_Nav);
            expListView = findViewById(R.id.left_drawer);

            ll_tab_pan_card = findViewById(R.id.ll_tab_pan_card);
            ll_tab_address_proof = findViewById(R.id.ll_tab_address_proof);
            ll_tab_id_proof = findViewById(R.id.ll_tab_id_proof);

            txt_tab_pan_no = findViewById(R.id.txt_tab_pan_no);
            txt_tab_address_proof = findViewById(R.id.txt_tab_address_proof);
            txt_tab_id_proof = findViewById(R.id.txt_tab_id_proof);

            listDataHeader = new ArrayList<>();
            listDataChild = new HashMap<>();

            HeadingJarray = Splash_Activity.HeadingJarray;

            LL_Nav.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String Usertype = (AppController.getSpUserInfo().getString(SPUtils.USER_TYPE, ""));
                    if (Usertype.equalsIgnoreCase("DISTRIBUTOR")) {
                        if (AppController.getSpIsLogin().getBoolean(SPUtils.IS_LOGIN, false)) {
                            startActivity(new Intent(KYCUploadDocument_Activity.this, Profile_View_Activity.class));
                        } else {
                            startActivity(new Intent(KYCUploadDocument_Activity.this, Login_Activity.class));
                        }

                        if (drawer.isDrawerOpen(GravityCompat.START)) {
                            drawer.closeDrawer(GravityCompat.START);
                        }
                    }
                }
            });

            layout_pannoProf = findViewById(R.id.layout_pannoProf);
            layout_AdrsProf = findViewById(R.id.layout_AdrsProf);
            layout_IdProf = findViewById(R.id.layout_IdProf);

//          layout_photoProof = (LinearLayout) findViewById(R.id.layout_photoProof);

            imgView_AdrsProf = findViewById(R.id.imgView_AdrsProf);
            imgView_IdProf = findViewById(R.id.imgView_IdProf);
            imgView_pan_card_Prof = findViewById(R.id.imgView_pan_card_Prof);

//          imgView_photoProof = (ImageView) findViewById(R.id.imgView_photoProof);

            // ll_tab_pan_card ,ll_tab_address_proof ,ll_tab_id_proof
            layout_pannoProf.setVisibility(View.VISIBLE);
            layout_AdrsProf.setVisibility(View.GONE);
            layout_IdProf.setVisibility(View.GONE);

            ll_tab_pan_card.setBackground(getResources().getDrawable(R.drawable.bg_round_button_orange));
            ll_tab_address_proof.setBackground(getResources().getDrawable(R.drawable.bg_round_button_eee));
            ll_tab_id_proof.setBackground(getResources().getDrawable(R.drawable.bg_round_button_eee));

            txt_tab_pan_no.setTextColor(Color.WHITE);
            txt_tab_address_proof.setTextColor(Color.BLACK);
            txt_tab_id_proof.setTextColor(Color.BLACK);

            ll_tab_pan_card.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    layout_pannoProf.setVisibility(View.VISIBLE);
                    layout_AdrsProf.setVisibility(View.GONE);
                    layout_IdProf.setVisibility(View.GONE);

                    ll_tab_pan_card.setBackground(getResources().getDrawable(R.drawable.bg_round_button_orange));
                    ll_tab_address_proof.setBackground(getResources().getDrawable(R.drawable.bg_round_button_eee));
                    ll_tab_id_proof.setBackground(getResources().getDrawable(R.drawable.bg_round_button_eee));

                    txt_tab_pan_no.setTextColor(Color.WHITE);
                    txt_tab_address_proof.setTextColor(Color.BLACK);
                    txt_tab_id_proof.setTextColor(Color.BLACK);

                }
            });
            ll_tab_address_proof.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    layout_pannoProf.setVisibility(View.GONE);
                    layout_AdrsProf.setVisibility(View.VISIBLE);
                    layout_IdProf.setVisibility(View.GONE);

                    ll_tab_pan_card.setBackground(getResources().getDrawable(R.drawable.bg_round_button_eee));
                    ll_tab_address_proof.setBackground(getResources().getDrawable(R.drawable.bg_round_button_orange));
                    ll_tab_id_proof.setBackground(getResources().getDrawable(R.drawable.bg_round_button_eee));

                    txt_tab_pan_no.setTextColor(Color.BLACK);
                    txt_tab_address_proof.setTextColor(Color.WHITE);
                    txt_tab_id_proof.setTextColor(Color.BLACK);

                }
            });
            ll_tab_id_proof.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    layout_pannoProf.setVisibility(View.GONE);
                    layout_AdrsProf.setVisibility(View.GONE);
                    layout_IdProf.setVisibility(View.VISIBLE);

                    ll_tab_pan_card.setBackground(getResources().getDrawable(R.drawable.bg_round_button_eee));
                    ll_tab_address_proof.setBackground(getResources().getDrawable(R.drawable.bg_round_button_eee));
                    ll_tab_id_proof.setBackground(getResources().getDrawable(R.drawable.bg_round_button_orange));

                    txt_tab_pan_no.setTextColor(Color.BLACK);
                    txt_tab_address_proof.setTextColor(Color.BLACK);
                    txt_tab_id_proof.setTextColor(Color.WHITE);

                }
            });

            mBottomSheetDialog = new BottomSheetDialog(KYCUploadDocument_Activity.this);
            View sheetView = this.getLayoutInflater().inflate(R.layout.bottom_sheet, null);
            mBottomSheetDialog.setContentView(sheetView);
            mBottomSheetDialog.setTitle("Complete action using...");

            heading = getIntent().getStringExtra("HEADING");
            txt_heading = findViewById(R.id.txt_heading);

            if (heading.equalsIgnoreCase("View")) {
                txt_heading.setText("View KYC Documents");
                findViewById(R.id.textView).setVisibility(View.GONE);
                findViewById(R.id.textView5).setVisibility(View.GONE);
            } else
                txt_heading.setText("Upload KYC Documents");

            if (heading.equalsIgnoreCase("Update")) {
                layout_AdrsProf.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (isAddressProof) {
                            AppUtils.alertDialog(KYCUploadDocument_Activity.this, "Sorry, You can upload only once.");
                        } else {
                            whichUpload = "AP";
                            mBottomSheetDialog.show();
                        }
                    }
                });

                layout_IdProf.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (isIdProof) {
                            AppUtils.alertDialog(KYCUploadDocument_Activity.this, "Sorry, You can upload only once.");
                        } else {
                            whichUpload = "IP";
                            mBottomSheetDialog.show();
                        }
                    }
                });
                layout_pannoProf.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (isPancardProof) {
                            AppUtils.alertDialog(KYCUploadDocument_Activity.this, "Sorry, You can upload only once.");
                        } else {
                            whichUpload = "PC";
                            mBottomSheetDialog.show();
                        }
                    }
                });
            }

            LinearLayout camera = sheetView.findViewById(R.id.bottom_sheet_camera);
            LinearLayout gallery = sheetView.findViewById(R.id.bottom_sheet_gallery);

            camera.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    imageUri = AppUtils.getOutputMediaFileUri(1, TAG, KYCUploadDocument_Activity.this);
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

            if (AppUtils.isNetworkAvailable(KYCUploadDocument_Activity.this)) {
                continueapp();
            }

        } catch (Exception e) {
            e.printStackTrace();
            AppUtils.showExceptionDialog(KYCUploadDocument_Activity.this);
        }
    }


    public void continueapp() {

        executeGetImageLoadRequest();

        enableExpandableList();
        LoadNavigationHeaderItems();
    }

    private void executeGetImageLoadRequest() {
        try {
            if (AppUtils.isNetworkAvailable(KYCUploadDocument_Activity.this)) {
                new AsyncTask<Void, Void, String>() {
                    protected void onPreExecute() {
                        AppUtils.showProgressDialog(KYCUploadDocument_Activity.this);
                    }

                    @Override
                    protected String doInBackground(Void... params) {
                        String response = "";
                        try {
                            List<NameValuePair> postParameters = new ArrayList<>();
                            postParameters.add(new BasicNameValuePair("IDNo", AppController.getSpUserInfo().getString(SPUtils.USER_ID_NUMBER, "")));
                            //ImageType----AddrProof=AP,IdentityProof=IP,PhotoProof=PP,Signature=S,CancelChq=CC,SpousePic=SP,All=*
                            postParameters.add(new BasicNameValuePair("ImageType", "*"));
                            response = AppUtils.callWebServiceWithMultiParam(KYCUploadDocument_Activity.this, postParameters, QueryUtils.methodGetImages, TAG);
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
                                if (!jsonArrayData.getJSONObject(0).getString("AddrProof").equals("")) {
                                    isAddressProof = true;
                                    imgView_AdrsProf.setImageBitmap(AppUtils.getBitmapFromString(jsonArrayData.getJSONObject(0).getString("AddrProof")));
                                }
                                if (!jsonArrayData.getJSONObject(0).getString("IdentityProof").equals("")) {
                                    isIdProof = true;
                                    imgView_IdProf.setImageBitmap(AppUtils.getBitmapFromString(jsonArrayData.getJSONObject(0).getString("IdentityProof")));
                                }
                                if (!jsonArrayData.getJSONObject(0).getString("PanProof").equals("")) {
                                    isPancardProof = true;
                                    imgView_pan_card_Prof.setImageBitmap(AppUtils.getBitmapFromString(jsonArrayData.getJSONObject(0).getString("PanProof")));
                                }
                                if (!jsonArrayData.getJSONObject(0).getString("PhotoProof").equals("")) {
                                    profileImage.setImageBitmap(AppUtils.getBitmapFromString(jsonArrayData.getJSONObject(0).getString("PhotoProof")));

                                }
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            AppUtils.showExceptionDialog(KYCUploadDocument_Activity.this);
                        }
                    }
                }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            }
        } catch (Exception e) {
            e.printStackTrace();
            AppUtils.showExceptionDialog(KYCUploadDocument_Activity.this);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        try {

            if (resultCode == Activity.RESULT_OK) {
                if (requestCode == RESULT_GALLERY) {
                    if (data != null) {
                        imageUri = data.getData();
                        String filepath = AppUtils.getPath(data.getData(), KYCUploadDocument_Activity.this);

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
            AppUtils.showExceptionDialog(KYCUploadDocument_Activity.this);
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

                int nh = 0;
                if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
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

                showUploadImageDailog(bitmap);

            } else
                AppUtils.alertDialog(this, "Selected file exceed the allowable file size limit (5 MB)");

        } catch (Exception e) {
            e.printStackTrace();
            AppUtils.showExceptionDialog(KYCUploadDocument_Activity.this);
        }
    }

    private void showUploadImageDailog(final Bitmap imageBitmap) {
        try {
            final Dialog dialog = new Dialog(KYCUploadDocument_Activity.this, R.style.ThemeDialogCustom);
            dialog.getWindow().getAttributes().windowAnimations = R.style.dialog_animation;
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setCancelable(false);
            dialog.setContentView(R.layout.custom_dialog_img_upload);

            TextView dialog4all_txt = dialog.findViewById(R.id.txt_DialogTitle);
            if (whichUpload.equals("AP")) {
                dialog4all_txt.setText("Are you sure you want to upload this image as Address Proof?");
            } else if (whichUpload.equals("IP")) {
                dialog4all_txt.setText("Are you sure you want to upload this image as Identity Proof?");
            } else if (whichUpload.equals("PC")) {
                dialog4all_txt.setText("Are you sure you want to upload this image as Pan Card ?");
            }


            final ImageView imgView_Upload = dialog.findViewById(R.id.imgView_Upload);
            imgView_Upload.setImageBitmap(imageBitmap);

            TextView txt_submit = dialog.findViewById(R.id.txt_submit);
            txt_submit.setText("Upload");
            txt_submit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AppUtils.hideKeyboardOnClick(KYCUploadDocument_Activity.this, v);
                    dialog.dismiss();
                    executePostImageUploadRequest(imageBitmap);

                }
            });

            TextView txt_cancel = dialog.findViewById(R.id.txt_cancel);
            txt_cancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                }
            });

            dialog.show();
        } catch (Exception e) {
            e.printStackTrace();
            AppUtils.showExceptionDialog(KYCUploadDocument_Activity.this);
        }
    }

    private void executePostImageUploadRequest(final Bitmap bitmap) {
        try {
            if (AppUtils.isNetworkAvailable(KYCUploadDocument_Activity.this)) {
                new AsyncTask<Void, Void, String>() {
                    protected void onPreExecute() {
                        AppUtils.showProgressDialog(KYCUploadDocument_Activity.this);
                    }

                    @Override
                    protected String doInBackground(Void... params) {
                        String response = "";
                        try {
                            String deviceId = ((TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE)).getDeviceId();
                            List<NameValuePair> postParameters = new ArrayList<>();
                            postParameters.add(new BasicNameValuePair("IDNo", AppController.getSpUserInfo().getString(SPUtils.USER_ID_NUMBER, "")));
                            postParameters.add(new BasicNameValuePair("FormNo", AppController.getSpUserInfo().getString(SPUtils.USER_FORM_NUMBER, "")));
                            postParameters.add(new BasicNameValuePair("Type", whichUpload));
                            postParameters.add(new BasicNameValuePair("ImageByteCode", AppUtils.getBase64StringFromBitmap(bitmap)));

                            try {
                                postParameters.add(new BasicNameValuePair("IPAddress", deviceId));
                            } catch (Exception ignored) {
                            }

                            response = AppUtils.callWebServiceWithMultiParam(KYCUploadDocument_Activity.this, postParameters, QueryUtils.methodUploadImages, TAG);
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
                                if (whichUpload.equals("AP")) {
                                    if (!jsonArrayData.getJSONObject(0).getString("AddrProof").equals("")) {
                                        isAddressProof = true;
                                        imgView_AdrsProf.setImageResource(android.R.color.transparent);
                                        imgView_AdrsProf.setImageBitmap(AppUtils.getBitmapFromString(jsonArrayData.getJSONObject(0).getString("AddrProof")));
                                    }
                                } else if (whichUpload.equals("IP")) {
                                    if (!jsonArrayData.getJSONObject(0).getString("IdentityProof").equals("")) {
                                        isIdProof = true;
                                        imgView_IdProf.setImageBitmap(AppUtils.getBitmapFromString(jsonArrayData.getJSONObject(0).getString("IdentityProof")));
                                    }
                                } else if (whichUpload.equals("PC")) {
                                    if (!jsonArrayData.getJSONObject(0).getString("PanProof").equals("")) {
                                        isPancardProof = true;
                                        imgView_pan_card_Prof.setImageBitmap(AppUtils.getBitmapFromString(jsonArrayData.getJSONObject(0).getString("PanProof")));
                                    }
                                }
//                                else if(whichUpload.equals("PP"))
//                                {
//                                    if(!jsonArrayData.getJSONObject(0).getString("PhotoProof").toString().equals(""))
//                                    {
//                                        isPhotoProof=true;
//                                        imgView_photoProof.setImageBitmap(AppUtils.getBitmapFromString(jsonArrayData.getJSONObject(0).getString("PhotoProof").toString()));
//                                    }
//                                }
                            } else {
                                AppUtils.alertDialog(KYCUploadDocument_Activity.this, jsonObject.getString("Message"));
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            AppUtils.showExceptionDialog(KYCUploadDocument_Activity.this);
                        }
                    }
                }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            }
        } catch (Exception e) {
            e.printStackTrace();
            AppUtils.showExceptionDialog(KYCUploadDocument_Activity.this);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        try {
            AppUtils.dismissProgressDialog();
            ////overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
            System.gc();
        } catch (Exception e) {
            e.printStackTrace();
            AppUtils.showExceptionDialog(KYCUploadDocument_Activity.this);
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
                    startActivity(new Intent(KYCUploadDocument_Activity.this, DashBoard_Activity.class));
                    if (drawer.isDrawerOpen(GravityCompat.START)) {
                        drawer.closeDrawer(GravityCompat.START);
                    }
                } else if (GroupTitle.trim().equalsIgnoreCase("Logout")) {
                    AppUtils.showDialogSignOut(KYCUploadDocument_Activity.this);
                } else if (GroupTitle.trim().equalsIgnoreCase("New Joining")) {
                    startActivity(new Intent(KYCUploadDocument_Activity.this, Register_Activity.class));
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
                    startActivity(new Intent(KYCUploadDocument_Activity.this, Profile_View_Activity.class));
                } else if (ChildItemTitle.trim().equalsIgnoreCase(getResources().getString(R.string.change_password))) {
                    startActivity(new Intent(KYCUploadDocument_Activity.this, Change_Password_Activity.class));
                } else if (ChildItemTitle.trim().equalsIgnoreCase(getResources().getString(R.string.new_joining))) {
                    startActivity(new Intent(KYCUploadDocument_Activity.this, Register_Activity.class));
                } else if (ChildItemTitle.trim().equalsIgnoreCase(getResources().getString(R.string.upload_kyc))) {
                    startActivity(new Intent(KYCUploadDocument_Activity.this, KYCUploadDocument_Activity.class).putExtra("HEADING", "Update"));
                } else if (ChildItemTitle.trim().equalsIgnoreCase(getResources().getString(R.string.generation_structure))) {
                    startActivity(new Intent(KYCUploadDocument_Activity.this, Sponsor_genealogy_Activity.class));
                } else if (ChildItemTitle.trim().equalsIgnoreCase(getResources().getString(R.string.sponsor_downline))) {
                    startActivity(new Intent(KYCUploadDocument_Activity.this, Sponsor_team_details_Activity.class).putExtra("Action", "Sponsor"));
                } else if (ChildItemTitle.trim().equalsIgnoreCase(getResources().getString(R.string.bv_detail_report))) {
                    startActivity(new Intent(KYCUploadDocument_Activity.this, Sponsor_team_details_Activity.class).putExtra("Action", "Direct"));
                } else if (ChildItemTitle.trim().equalsIgnoreCase(getResources().getString(R.string.welcome_letter))) {
                    startActivity(new Intent(KYCUploadDocument_Activity.this, WelcomeLetter_Activity.class));
                } else if (ChildItemTitle.trim().equalsIgnoreCase(getResources().getString(R.string.purchase_reports))) {
                    startActivity(new Intent(KYCUploadDocument_Activity.this, Repurchase_BV_Detail.class));
                } else if (ChildItemTitle.trim().equalsIgnoreCase("Team Repurchase BV Summary")) {
                    startActivity(new Intent(KYCUploadDocument_Activity.this, Repurchase_BV_Summary_Team_Activity.class));
                } else if (ChildItemTitle.trim().equalsIgnoreCase("Repurchase Bill Summary")) {
                    startActivity(new Intent(KYCUploadDocument_Activity.this, Repurchase_Bill_Summary.class));
                } else if (ChildItemTitle.trim().equalsIgnoreCase(getResources().getString(R.string.wallet_detail))) {
                    startActivity(new Intent(KYCUploadDocument_Activity.this, Wallet_Transaction_Report_Activity.class));
                } else if (ChildItemTitle.trim().equalsIgnoreCase("Wallet Request Report")) {
                    startActivity(new Intent(KYCUploadDocument_Activity.this, Wallet_Request_Status_Report_Activity.class));
                } else if (ChildItemTitle.trim().equalsIgnoreCase("Request For Wallet Amount")) {
                    startActivity(new Intent(KYCUploadDocument_Activity.this, Wallet_Request_Amount_Activity.class));
                } else if (ChildItemTitle.trim().equalsIgnoreCase(getResources().getString(R.string.monthly_incentive))) {
                    startActivity(new Intent(KYCUploadDocument_Activity.this, Monthly_Incentive_Activity.class));
                } else if (ChildItemTitle.trim().equalsIgnoreCase(getResources().getString(R.string.monthly_incentive_detail_report))) {
                    startActivity(new Intent(KYCUploadDocument_Activity.this, Monthly_Incentive_Detail_Activity.class));
                } else if (ChildItemTitle.trim().equalsIgnoreCase("TDS Detail Report")) {
                    startActivity(new Intent(KYCUploadDocument_Activity.this, TDS_detail_report_Activity.class));
                } else if (ChildItemTitle.trim().equalsIgnoreCase("Update Profile")) {
                    startActivity(new Intent(KYCUploadDocument_Activity.this, Profile_Update_Activity.class));
                } else if (ChildItemTitle.trim().equalsIgnoreCase("View KYC Documents")) {
                    startActivity(new Intent(KYCUploadDocument_Activity.this, KYCUploadDocument_Activity.class).putExtra("HEADING", "View"));
                } else if (ChildItemTitle.trim().equalsIgnoreCase("Register Enquiry/Complaint")) {
                    startActivity(new Intent(KYCUploadDocument_Activity.this, Register_Complaint_Activity.class));
                } else if (ChildItemTitle.trim().equalsIgnoreCase("View Open/Pending Queries")) {
                    startActivity(new Intent(KYCUploadDocument_Activity.this, ViewPendingQueries_Activity.class).putExtra("HEADING", "Open"));
                } else if (ChildItemTitle.trim().equalsIgnoreCase("View Closed Queries")) {
                    startActivity(new Intent(KYCUploadDocument_Activity.this, ViewPendingQueries_Activity.class).putExtra("HEADING", "Closed"));
                } else if (ChildItemTitle.trim().equalsIgnoreCase("Product Wallet Detail")) {
                    startActivity(new Intent(KYCUploadDocument_Activity.this, ProductWallet_Transaction_Report_Activity.class));
                } else if (ChildItemTitle.trim().equalsIgnoreCase("Wallet To Bank Transfer Detail")) {
                    startActivity(new Intent(KYCUploadDocument_Activity.this, Wallet_Bank_Transfer_Report_Activity.class));
                } else if (ChildItemTitle.trim().equalsIgnoreCase("ID Card")) {
                    startActivity(new Intent(KYCUploadDocument_Activity.this, ID_card_Activity.class));
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
            if (AppUtils.isNetworkAvailable(KYCUploadDocument_Activity.this)) {
                new AsyncTask<Void, Void, String>() {
                    @Override
                    protected String doInBackground(Void... params) {
                        String response = "";
                        try {
                            List<NameValuePair> postParameters = new ArrayList<>();
                            postParameters.add(new BasicNameValuePair("Formno", AppController.getSpUserInfo().getString(SPUtils.USER_FORM_NUMBER, "")));
                            response = AppUtils.callWebServiceWithMultiParam(KYCUploadDocument_Activity.this,
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
                AppUtils.showProgressDialog(KYCUploadDocument_Activity.this);
            }

            @Override
            protected String doInBackground(Void... params) {

                Thread.currentThread().setPriority(Thread.MAX_PRIORITY);

                String response = "";
                try {
                    List<NameValuePair> postParameters = new ArrayList<>();
                    response = AppUtils.callWebServiceWithMultiParam(KYCUploadDocument_Activity.this, postParameters, QueryUtils.methodtoGetDrawerMenuItems, TAG);

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