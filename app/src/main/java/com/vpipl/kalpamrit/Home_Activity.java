package com.vpipl.kalpamrit;


import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.AnimatedVectorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.speech.RecognizerIntent;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.Html;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.text.style.StrikethroughSpan;
import android.text.style.StyleSpan;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.Interpolator;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.vpipl.kalpamrit.Adapters.ExpandableListAdapter;
import com.vpipl.kalpamrit.Adapters.ImageSliderViewPagerAdapter;
import com.vpipl.kalpamrit.Utils.AppUtils;
import com.vpipl.kalpamrit.Utils.BadgeDrawable;
import com.vpipl.kalpamrit.Utils.CirclePageIndicator;
import com.vpipl.kalpamrit.Utils.CircularImageView;
import com.vpipl.kalpamrit.Utils.QueryUtils;
import com.vpipl.kalpamrit.Utils.SPUtils;

import org.apache.commons.lang3.text.WordUtils;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by PC14 on 3/16/2016.
 */

public class Home_Activity extends AppCompatActivity {

    public LinkedList<HashMap<String, String>> imagesBelowSlider = new LinkedList<>(Collections.nCopies(10, new HashMap<String, String>()));

    TextView speak;
    private static final int REQUEST_CODE = 1234;

    public static DrawerLayout drawer;
    public static NavigationView navigationView;
    public static ArrayList<HashMap<String, String>> imageSlider = new ArrayList<>();
    public static JSONObject hotsellingJObject;
    public static JSONObject allproductsJObject;
    private static String TAG = "Home_Activity";

    private ActionBarDrawerToggle drawerToggle;
    private CirclePageIndicator imagePageIndicator;

    private Button btn_view_all;

    private EditText et_search;

    private CircularImageView profileImage;
    private LinearLayout LLBottom, LLBottom_Newly;
    TextView txt_thought_of_the_day;

    private int currentPage = 0;
    private Timer timer;

    private TextView txt_welcome_name;
    private TextView txt_available_wb;
    private TextView txt_id_number;
    private ViewPager viewPager;
    private ArrayList<String> listDataHeader;
    private HashMap<String, List<String>> listDataChild;
    private ExpandableListView expListView;
    private int lastExpandedPosition = -1;
    private JSONArray HeadingJarray;

    ImageView imageView1, imageView2, imageView3, imageView4, imageView5;

    ImageView img_menu, img_cart, img_user;

    public void SetupToolbar() {
        img_menu = findViewById(R.id.img_nav_back);

        img_cart = findViewById(R.id.img_cart);
        img_user = findViewById(R.id.img_login_logout);

        img_cart.setVisibility(View.VISIBLE);

        img_menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (drawer.isDrawerOpen(navigationView)) {
                    img_menu.setImageDrawable(getResources().getDrawable(R.drawable.icon_nav_bar));
                    drawer.closeDrawer(navigationView);
                } else {
                    img_menu.setImageDrawable(getResources().getDrawable(R.drawable.icon_nav_bar_close));
                    drawer.openDrawer(navigationView);
                }
            }
        });

        img_cart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Home_Activity.this, AddCartCheckOut_Activity.class));

            }
        });

        img_user.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!AppController.getSpIsLogin().getBoolean(SPUtils.IS_LOGIN, false))
                    startActivity(new Intent(Home_Activity.this, Login_Activity.class));
                else
                    AppUtils.showDialogSignOut(Home_Activity.this);
            }
        });

        if (AppController.getSpIsLogin().getBoolean(SPUtils.IS_LOGIN, false))
            img_user.setImageDrawable(getResources().getDrawable(R.drawable.icon_logout_orange));
        else
            img_user.setImageDrawable(getResources().getDrawable(R.drawable.icon_distributor_login_orange));
    }

    LinearLayout ll_thoughtoftheday;

    private Bitmap getBitmapFromView(View view) {
        Bitmap returnedBitmap = Bitmap.createBitmap(view.getWidth(), view.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(returnedBitmap);
        Drawable bgDrawable = view.getBackground();
        if (bgDrawable != null) {
            //has background drawable, then draw it on the canvas
            bgDrawable.draw(canvas);
        } else {
            //does not have background drawable, then draw white background on the canvas
            canvas.drawColor(Color.WHITE);
        }
        view.draw(canvas);
        return returnedBitmap;
    }

    private ImageView iv;
    private EditText text;
    private AnimatedVectorDrawable searchToBar;
    private AnimatedVectorDrawable barToSearch;
    private float offset;
    private Interpolator interp;
    private int duration;
    private boolean expanded = false;
    AdView mAdView, adView1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_activity);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        try {
            AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);

            Toolbar toolbar = findViewById(R.id.toolbar);
            setSupportActionBar(toolbar);
            getSupportActionBar().setTitle("");
            SetupToolbar();

            txt_thought_of_the_day = findViewById(R.id.txt_thought_of_the_day);

            txt_thought_of_the_day.setSelected(true);
            txt_thought_of_the_day.setSingleLine(true);

            drawer = findViewById(R.id.drawer_layout);
            viewPager = findViewById(R.id.viewPager);
            imagePageIndicator = findViewById(R.id.imagePageIndicator);
            navigationView = findViewById(R.id.nav_view);

            View navHeaderView = navigationView.getHeaderView(0);
            txt_welcome_name = navHeaderView.findViewById(R.id.txt_welcome_name);
            txt_available_wb = navHeaderView.findViewById(R.id.txt_available_wb);
            txt_id_number = navHeaderView.findViewById(R.id.txt_id_number);
            profileImage = navHeaderView.findViewById(R.id.iv_Profile_Pic);

            et_search = findViewById(R.id.et_search);

            LLBottom = findViewById(R.id.LLBottom);
            LLBottom_Newly = findViewById(R.id.LLBottom_Newly);

            btn_view_all = findViewById(R.id.btn_view_all);

            expListView = findViewById(R.id.left_drawer);

            listDataHeader = new ArrayList<>();
            listDataChild = new HashMap<>();

            HeadingJarray = Splash_Activity.HeadingJarray;

            imageView1 = findViewById(R.id.imageView1);
            imageView2 = findViewById(R.id.imageView2);
            imageView3 = findViewById(R.id.imageView3);
            imageView4 = findViewById(R.id.imageView4);
            imageView5 = findViewById(R.id.imageView5);

            ll_thoughtoftheday = findViewById(R.id.ll_thoughtoftheday);

            txt_thought_of_the_day.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(Home_Activity.this, Change_Proposer_Activity.class);
                    startActivity(intent);
                }
            });

            findViewById(R.id.txt_all_products).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //     startActivity(new Intent(Home_Activity.this ,GetContactDetails_Activity.class));
                    Bitmap bitmap = getBitmapFromView(imageView1);
                    try {
                        File file = new File(Home_Activity.this.getExternalCacheDir(), "logicchip.png");
                        FileOutputStream fOut = new FileOutputStream(file);
                        bitmap.compress(Bitmap.CompressFormat.PNG, 100, fOut);
                        fOut.flush();
                        fOut.close();
                        file.setReadable(true, false);
                        final Intent intent = new Intent(android.content.Intent.ACTION_SEND);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        intent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(file));
                        intent.setType("image/png");
                        startActivity(Intent.createChooser(intent, "Share image via"));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });

            btn_view_all.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(Home_Activity.this, FeatureProductListGrid_Activity.class);
                    startActivity(intent);
                }
            });

            findViewById(R.id.btn_view_all_products).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (allproductsJObject.length() > 0) {
                        Intent intent = new Intent(Home_Activity.this, FeatureProductListGrid_Activity.class);
                        startActivity(intent);
                    } else {

                    }
                }
            });

            et_search.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                @Override
                public boolean onEditorAction(TextView view, int actionId, KeyEvent event) {
                    if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                        AppUtils.hideKeyboardOnClick(Home_Activity.this, view);
                        performSearch();
                        return true;
                    }
                    return false;
                }
            });

            navHeaderView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String Usertype = (AppController.getSpUserInfo().getString(SPUtils.USER_TYPE, ""));
                    if (Usertype.equalsIgnoreCase("DISTRIBUTOR")) {
                        if (AppController.getSpIsLogin().getBoolean(SPUtils.IS_LOGIN, false)) {
                            startActivity(new Intent(Home_Activity.this, Profile_View_Activity.class));
                        } else {
                            startActivity(new Intent(Home_Activity.this, Login_Activity.class).putExtra("SendToHome", true));
                        }

                        if (drawer.isDrawerOpen(GravityCompat.START)) {
                            drawer.closeDrawer(GravityCompat.START);
                        }
                    }
                }
            });

            enableExpandableList();
            executeGetProfilePicture();

            LoadNavigationHeaderItems();
            executeThoughtOfTheDay();

            String str_USER_PopupSts = AppController.getSpUserInfo().getString(SPUtils.USER_PopupSts, "");

            if (str_USER_PopupSts.equalsIgnoreCase("T")) {
               // showDialog("https://image.flaticon.com/icons/svg/145/145802.svg");
                // executeGetPopUpPictureExit();
            }

            if (imageSlider.size() > 0)
                setImageSlider();
            else
                executeToGetImageSlider();

            drawer.addDrawerListener(new DrawerLayout.DrawerListener() {
                @Override
                public void onDrawerSlide(@NonNull View drawerView, float slideOffset) {

                }

                @Override
                public void onDrawerOpened(@NonNull View drawerView) {
                    img_menu.setImageDrawable(getResources().getDrawable(R.drawable.icon_nav_bar_close));
                }

                @Override
                public void onDrawerClosed(@NonNull View drawerView) {
                    img_menu.setImageDrawable(getResources().getDrawable(R.drawable.icon_nav_bar));
                }

                @Override
                public void onDrawerStateChanged(int newState) {

                }
            });

            /****************************/
            iv = (ImageView) findViewById(R.id.search);
            text = (EditText) findViewById(R.id.text);
            searchToBar = (AnimatedVectorDrawable) getResources().getDrawable(R.drawable.anim_search_to_bar);
            barToSearch = (AnimatedVectorDrawable) getResources().getDrawable(R.drawable.anim_bar_to_search);
            interp = AnimationUtils.loadInterpolator(this, android.R.interpolator.linear_out_slow_in);
            duration = getResources().getInteger(R.integer.duration_bar);
            // iv is sized to hold the search+bar so when only showing the search icon, translate the
            // whole view left by half the difference to keep it centered
            offset = -71f * (int) getResources().getDisplayMetrics().scaledDensity;
            iv.setTranslationX(offset);

            iv.setImageDrawable(barToSearch);
            barToSearch.start();
            iv.animate().translationX(offset).setDuration(duration).setInterpolator(interp);
            text.setAlpha(0f);
            /****************************/

        } catch (Exception e) {
            e.printStackTrace();
        }

        /*Searching by voice code*/
        speak = (TextView) findViewById(R.id.speakButton);

        final Animation myAnim = AnimationUtils.loadAnimation(Home_Activity.this, R.anim.bounce);
        speak.startAnimation(myAnim);

        PackageManager pm = getPackageManager();
        final List<ResolveInfo> activities = pm.queryIntentActivities(new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH), 0);

        speak.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (activities.size() == 0) {
                    //   speak.setEnabled(false);
                    // speak.setText("Recognizer not present");
                    Toast.makeText(Home_Activity.this, "Voice Recognizer not present", Toast.LENGTH_SHORT).show();
                } else {
                    startVoiceRecognitionActivity();
                }
            }
        });

        if (activities.size() == 0) {
            //  speak.setEnabled(false);
            // speak.setText("Recognizer not present");
            // Toast.makeText(this, "Recognizer not present", Toast.LENGTH_SHORT).show();
        }
        et_search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // TODO Auto-generated method stub
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // TODO Auto-generated method stub
            }

            @Override
            public void afterTextChanged(Editable s) {
                // TODO Auto-generated method stub
                // AppUtils.hideKeyboardOnClick(Home_Activity.this, view);


                //    speak.setEnabled(false);
            }
        });

        /*Admob ads added by mukesh 04-12-2019 11:00 AM*/

        MobileAds.initialize(this, getString(R.string.ads_app_id));
        mAdView = (AdView) findViewById(R.id.adView);

        AdRequest adRequest = new AdRequest.Builder()
                .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
                // Check the LogCat to get your test device ID
                // .addTestDevice("B3EEABB8EE11C2BE770B684D95219ECB")
                .build();

        mAdView.setAdListener(new AdListener() {
            @Override
            public void onAdLoaded() {
            }

            @Override
            public void onAdClosed() {

                Log.e("log1", "Ad is closed!");
            }

            @Override
            public void onAdFailedToLoad(int errorCode) {

                Log.e("log2", "Ad failed to load! error code: " + errorCode);
                //  Toast.makeText(getApplicationContext(), "Ad failed to load! error code: "+errorCode, Toast.LENGTH_LONG).show();
            }

            @Override
            public void onAdLeftApplication() {
                Log.e("log2", "Ad left application!");

            }

            @Override
            public void onAdOpened() {
                super.onAdOpened();
            }
        });

        mAdView.loadAd(adRequest);

        /*Second ads*/

        adView1 = (AdView) findViewById(R.id.adView1);

        adView1.setAdListener(new AdListener() {
            @Override
            public void onAdLoaded() {
            }

            @Override
            public void onAdClosed() {

                Log.e("log1", "Ad is closed!");
            }

            @Override
            public void onAdFailedToLoad(int errorCode) {

                Log.e("log2", "Ad failed to load! error code: " + errorCode);
                //  Toast.makeText(getApplicationContext(), "Ad failed to load! error code: "+errorCode, Toast.LENGTH_LONG).show();
            }

            @Override
            public void onAdLeftApplication() {
                Log.e("log2", "Ad left application!");

            }

            @Override
            public void onAdOpened() {
                super.onAdOpened();
            }
        });

        adView1.loadAd(adRequest);

    }

    public void animate(View view) {

        if (!expanded) {
            iv.setImageDrawable(searchToBar);
            searchToBar.start();
            iv.animate().translationX(0f).setDuration(duration).setInterpolator(interp);
            text.animate().alpha(1f).setStartDelay(duration - 100).setDuration(100).setInterpolator(interp);
        } else {
            iv.setImageDrawable(barToSearch);
            barToSearch.start();
            iv.animate().translationX(offset).setDuration(duration).setInterpolator(interp);
            text.setAlpha(0f);
        }
        expanded = !expanded;
    }

    /**
     * Fire an intent to start the voice recognition activity.
     */
    private void startVoiceRecognitionActivity() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Voice searching...");
        startActivityForResult(intent, REQUEST_CODE);
    }

    /**
     * Handle the results from the voice recognition activity.
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CODE && resultCode == RESULT_OK) {
            // Populate the wordsList with the String values the recognition engine thought it heard
            final ArrayList<String> matches = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
            if (!matches.isEmpty()) {
                et_search.setText("");
                String Query = matches.get(0);
                et_search.setText(Query);
                performSearch();
                // speak.setEnabled(false);
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void performSearch() {
        if (et_search.getText().toString().isEmpty()) {
            AppUtils.alertDialog(Home_Activity.this, "Please enter search keyword.");
            et_search.requestFocus();
        } else {
            startActivity(new Intent(this, SearchProducts_Activity.class).putExtra("Keyword", et_search.getText().toString()));
        }
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

            String Usertype = (AppController.getSpUserInfo().getString(SPUtils.USER_TYPE, ""));
            if (Usertype.equalsIgnoreCase("DISTRIBUTOR")) {
                String userid = AppController.getSpUserInfo().getString(SPUtils.USER_ID_NUMBER, "");
                txt_id_number.setText(userid);
                txt_id_number.setVisibility(View.VISIBLE);

                executeWalletBalanceRequest();

                String bytecode = AppController.getSpUserInfo().getString(SPUtils.USER_profile_pic_byte_code, "");

                if (bytecode.equalsIgnoreCase(""))
                    executeGetProfilePicture();
                else
                    profileImage.setImageBitmap(AppUtils.getBitmapFromString(bytecode));
            }
        }
    }

    private void executeGetProfilePicture() {
        try {
            if (AppUtils.isNetworkAvailable(Home_Activity.this)) {
                new AsyncTask<Void, Void, String>() {
                    @Override
                    protected String doInBackground(Void... params) {
                        String response = "";
                        try {
                            List<NameValuePair> postParameters = new ArrayList<>();
                            postParameters.add(new BasicNameValuePair("IDNo", AppController.getSpUserInfo().getString(SPUtils.USER_ID_NUMBER, "")));
                            //ImageType----AddrProof=AP,IdentityProof=IP,PhotoProof=PP,Signature=S,CancelChq=CC,SpousePic=SP,All=*
                            postParameters.add(new BasicNameValuePair("ImageType", "PP"));

                            response = AppUtils.callWebServiceWithMultiParam(Home_Activity.this, postParameters, QueryUtils.methodGetImages, TAG);
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
                                if (!jsonArrayData.getJSONObject(0).getString("PhotoProof").equals("")) {

                                    AppController.getSpUserInfo().edit().putString(SPUtils.USER_profile_pic_byte_code, jsonArrayData.getJSONObject(0).getString("PhotoProof")).commit();
                                    profileImage.setImageBitmap(AppUtils.getBitmapFromString(jsonArrayData.getJSONObject(0).getString("PhotoProof")));
                                }
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            AppUtils.showExceptionDialog(Home_Activity.this);
                        }
                    }
                }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            }
        } catch (Exception e) {
            e.printStackTrace();
            AppUtils.showExceptionDialog(Home_Activity.this);
        }
    }

    private void setImageSlider() {
        try {
            viewPager.setAdapter(new ImageSliderViewPagerAdapter(Home_Activity.this));

            final float density = getResources().getDisplayMetrics().density;
            imagePageIndicator.setFillColor(getResources().getColor(R.color.colorPrimaryDark));
            imagePageIndicator.setStrokeColor(getResources().getColor(R.color.app_color_gry));
            imagePageIndicator.setPageColor(Color.parseColor("#dddddd"));
            imagePageIndicator.setStrokeWidth();
            imagePageIndicator.setRadius(4 * density);
            imagePageIndicator.setViewPager(viewPager);

            final Handler handler = new Handler();
            final Runnable Update = new Runnable() {
                public void run() {
                    if (currentPage == imageSlider.size()) {
                        currentPage = 0;
                    }
                    viewPager.setCurrentItem(currentPage++, true);
                }
            };

            timer = new Timer(); // This will create a new Thread
            timer.schedule(new TimerTask() { // task to be scheduled

                @Override
                public void run() {
                    handler.post(Update);
                }
            }, 500, 3000);

            viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                @Override
                public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                    currentPage = position;
                }

                @Override
                public void onPageSelected(int position) {
                    currentPage = position;
                }

                @Override
                public void onPageScrollStateChanged(int state) {

                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }

        /*Code commented by muksh on 08-11-2019 06:13 PM*/
        //  executeToGetBestSellerProducts();
        executeToGetProductListRequest();
    }

    private void DrawNewlyProducts(JSONArray Jarray, String Type) {

        if (Type.equalsIgnoreCase("Newly"))
            LLBottom_Newly.removeAllViews();
        else
            LLBottom.removeAllViews();


        Typeface typeface = ResourcesCompat.getFont(this, R.font.gisha_0);

        float density = getResources().getDisplayMetrics().density;

        int paddingDp = (int) (10 * density);

        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        layoutParams.setMargins((int) (10 * density), (int) (10 * density), (int) (10 * density), (int) (10 * density));

        LinearLayout.LayoutParams textparams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        textparams.setMargins(0, (int) (5 * density), 0, 0);

        LinearLayout.LayoutParams textparams2 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        textparams2.weight = 1.0f;
        textparams2.gravity = Gravity.RIGHT | Gravity.END;

        LinearLayout.LayoutParams imageparams = new LinearLayout.LayoutParams((int) (200 * density), (int) (200 * density));

        String Usertype = (AppController.getSpUserInfo().getString(SPUtils.USER_TYPE, ""));

        for (int i = 0; i < Jarray.length(); i++) {
            try {
                JSONObject Jobject = Jarray.getJSONObject(i);
                int ProdID = Jobject.getInt("ProdID");

                String ProductName = AppUtils.CapsFirstLetterString(Jobject.getString("ProductName"));

                String NewMRP = Jobject.getString("NewMRP");
                String Discount = Jobject.getString("DiscountPer");
                String NDP = Jobject.getString("NewDP");
                String imgpath = Jobject.getString("NewImgPath");
                String BV = Jobject.getString("BV");
                String ImagePath = getResources().getString(R.string.productImageURL) + imgpath;

                boolean DiscDisp = Jobject.getBoolean("DiscDisp");

                FrameLayout FL = new FrameLayout(getApplicationContext());
                FL.setLayoutParams(layoutParams);

                final LinearLayout LL = new LinearLayout(getApplicationContext());
                LL.setOrientation(LinearLayout.VERTICAL);
                LL.setMinimumHeight((int) (200 * density));
                LL.setMinimumWidth((int) (200 * density));

                ImageView imageView = new ImageView(getApplicationContext());
                imageView.setBackground(getResources().getDrawable(R.drawable.bg_round_rectangle_gray));
                imageView.setLayoutParams(imageparams);
                imageView.setPadding((int) (10 * density), (int) (10 * density), (int) (10 * density), (int) (10 * density));


                loadProductImage(ImagePath, imageView);

                TextView tvproductname = new TextView(getApplicationContext());
                tvproductname.setLayoutParams(textparams);
                tvproductname.setMaxLines(1);
                tvproductname.setEllipsize(TextUtils.TruncateAt.END);
                tvproductname.setTypeface(typeface);
                tvproductname.setTextColor(getResources().getColor(android.R.color.black));
                tvproductname.setText(ProductName);


                textparams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                textparams.setMargins(0, (int) (5 * density), 0, 0);

                final LinearLayout LL_MRP = new LinearLayout(getApplicationContext());
                LL_MRP.setOrientation(LinearLayout.HORIZONTAL);
                LL_MRP.setLayoutParams(textparams);


                TextView tvproductmrp = new TextView(getApplicationContext());
                tvproductmrp.setTextSize(TypedValue.COMPLEX_UNIT_SP, 13);

                tvproductmrp.setTextColor(getResources().getColor(android.R.color.black));
                tvproductmrp.setTypeface(typeface);

                String NewDP = "₹ " + " " + NDP + "/-";
                String DiscountPer = Discount + "% off";
                Spannable spanString;

                if (DiscountPer.equalsIgnoreCase("0% off") || DiscountPer.equalsIgnoreCase("0.0% off")) {
                    spanString = new SpannableString("" + NewDP);
                    spanString.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.color_orange_text)), 0, NewDP.length(), 0);
                    spanString.setSpan(new RelativeSizeSpan(1.0f), 0, NewDP.length(), 0);
                } else {
                    spanString = new SpannableString("" + NewDP + "  " + NewMRP + "  ");
                    spanString.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.color_orange_text)), 0, NewDP.length(), 0);
                    spanString.setSpan(new RelativeSizeSpan(1.0f), 0, NewDP.length(), 0);
                    StyleSpan boldSpan = new StyleSpan(Typeface.BOLD);
                    spanString.setSpan(boldSpan, 0, NewDP.length(), 0);
                    spanString.setSpan(new StrikethroughSpan(), (NewDP.length() + 2), (((NewDP.length() + 2)) + (NewMRP.length())), 0);
                    spanString.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.app_color_grayicon)), (NewDP.length() + 2), (((NewDP.length() + 2)) + (NewMRP.length())), 0);
                    spanString.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.app_color_grayicon)), ((((NewDP.length() + 2)) + (NewMRP.length())) + 2), spanString.length(), 0);
                }
                tvproductmrp.setText(spanString);


                TextView tvBV = new TextView(getApplicationContext());
                tvBV.setLayoutParams(textparams2);
                tvBV.setMaxLines(1);
                tvBV.setTypeface(typeface);
                tvBV.setGravity(Gravity.END);
                tvBV.setTextColor(getResources().getColor(android.R.color.black));
                tvBV.setText("BV:" + BV);

                LL_MRP.addView(tvproductmrp);

                if (Usertype.equalsIgnoreCase("DISTRIBUTOR"))
                    LL_MRP.addView(tvBV);

                FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT, Gravity.TOP | Gravity.RIGHT);

                TextView newtag = new TextView(getApplicationContext());
                newtag.setBackgroundColor(getResources().getColor(R.color.color_orange_text));
                newtag.setPadding(paddingDp / 2, paddingDp / 2, paddingDp / 2, paddingDp / 2);
                newtag.setText(DiscountPer);
                newtag.setTextColor(Color.WHITE);
                newtag.setLayoutParams(params);
                newtag.setTypeface(typeface);

                LL.setId(ProdID);
                LL.addView(imageView);
                LL.addView(LL_MRP);
                LL.addView(tvproductname);

                LL.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(Home_Activity.this, ProductDetail_Activity.class);
                        intent.putExtra("productID", "" + LL.getId());
                        startActivity(intent);
                    }
                });

                FL.addView(LL);

                if (DiscDisp)
                    FL.addView(newtag);


                if (Type.equalsIgnoreCase("Newly"))
                    LLBottom_Newly.addView(FL);
                else
                    LLBottom.addView(FL);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void setOptionMenu() {
        try {

            setBadgeCount(Home_Activity.this, (AppController.selectedProductsList.size()));

            if (AppController.getSpIsLogin().getBoolean(SPUtils.IS_LOGIN, false)) {
                img_user.setImageDrawable(getResources().getDrawable(R.drawable.icon_logout_orange));
            } else {
                img_user.setImageDrawable(getResources().getDrawable(R.drawable.icon_distributor_login_orange));
                setBadgeCount(Home_Activity.this, (AppController.selectedProductsList.size()));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setBadgeCount(Context context, int count) {
        try {
            ImageView imageView = findViewById(R.id.img_cart);
            if (imageView != null) {
                LayerDrawable icon = (LayerDrawable) imageView.getDrawable();

                BadgeDrawable badge;// Reuse drawable if possible
                Drawable reuse = icon.findDrawableByLayerId(R.id.ic_badge); //getting the layer 2
                if (reuse != null && reuse instanceof BadgeDrawable) {
                    badge = (BadgeDrawable) reuse;
                } else {
                    badge = new BadgeDrawable(context);
                }
                badge.setCount("" + count);
                icon.mutate();
                icon.setDrawableByLayerId(R.id.ic_badge, badge);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void showExitDialog() {
        try {
            final Dialog dialog = AppUtils.createDialog(Home_Activity.this, false);
            dialog.setCancelable(false);

            TextView txt_DialogTitle = dialog.findViewById(R.id.txt_DialogTitle);
            //  txt_DialogTitle.setText(Html.fromHtml("Are you sure!!! Do you want to Exit?"));
            txt_DialogTitle.setText(Html.fromHtml("Do you want to exit from this application!"));
            TextView txt_submit = dialog.findViewById(R.id.txt_submit);
            txt_submit.setText("Yes");
            txt_submit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        android.os.Process.killProcess(android.os.Process.myPid());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });

            TextView txt_cancel = dialog.findViewById(R.id.txt_cancel);
            txt_cancel.setText(getResources().getString(R.string.txt_signout_no));
            txt_cancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        dialog.dismiss();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
            dialog.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void executeToGetImageSlider() {
        new AsyncTask<Void, Void, String>() {
            protected void onPreExecute() {
                AppUtils.showProgressDialog(Home_Activity.this);
            }

            @Override
            protected String doInBackground(Void... params) {
                String response = "";
                try {
                    List<NameValuePair> postParameters = new ArrayList<>();
                    response = AppUtils.callWebServiceWithMultiParam(Home_Activity.this, postParameters, QueryUtils.methodHomePageSlider, TAG);
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
                        getImageSliderResult(jsonObject.getJSONArray("Data"));
                    } else {
                        AppUtils.alertDialog(Home_Activity.this, "Sorry Seems to be an server error. Please try again!!!");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    private void executeToGetBestSellerProducts() {
        new AsyncTask<Void, Void, String>() {
            protected void onPreExecute() {
                AppUtils.showProgressDialog(Home_Activity.this);
            }

            @Override
            protected String doInBackground(Void... params) {
                String response = "";
                try {
                    List<NameValuePair> postParameters = new ArrayList<>();
                    if (AppController.getSpIsLogin().getBoolean(SPUtils.IS_LOGIN, false)) {
                        String Usertype = (AppController.getSpUserInfo().getString(SPUtils.USER_TYPE, ""));
                        if (Usertype.equalsIgnoreCase("CUSTOMER"))
                            postParameters.add(new BasicNameValuePair("UserType", "N"));
                        else if (Usertype.equalsIgnoreCase("DISTRIBUTOR"))
                            postParameters.add(new BasicNameValuePair("UserType", "D"));
                        else
                            postParameters.add(new BasicNameValuePair("UserType", "N"));
                    } else
                        postParameters.add(new BasicNameValuePair("UserType", "N"));

                    response = AppUtils.callWebServiceWithMultiParam(Home_Activity.this, postParameters, QueryUtils.methodHotSellingProducts, TAG);
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

                    hotsellingJObject = jsonObject;

                    // executeToGetNewlyLaunchedProducts();
                    executeToGetProductListRequest();

                    if (jsonObject.getString("Status").equalsIgnoreCase("True")) {
                        if (jsonObject.getJSONArray("Data").length() > 0) {
                            DrawNewlyProducts(jsonObject.getJSONArray("Data"), "Best");
                        }
                    } else {
                        AppUtils.alertDialog(Home_Activity.this, "Sorry Seems to be a server error. Please try again!!!");
                    }
                } catch (Exception ignored) {

                }
            }
        }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    private void executeToGetNewlyLaunchedProducts() {
        new AsyncTask<Void, Void, String>() {
            protected void onPreExecute() {
                AppUtils.showProgressDialog(Home_Activity.this);
            }

            @Override
            protected String doInBackground(Void... params) {
                String response = "";
                try {
                    List<NameValuePair> postParameters = new ArrayList<>();
                    if (AppController.getSpIsLogin().getBoolean(SPUtils.IS_LOGIN, false)) {
                        String Usertype = (AppController.getSpUserInfo().getString(SPUtils.USER_TYPE, ""));
                        if (Usertype.equalsIgnoreCase("CUSTOMER"))
                            postParameters.add(new BasicNameValuePair("UserType", "N"));
                        else if (Usertype.equalsIgnoreCase("DISTRIBUTOR"))
                            postParameters.add(new BasicNameValuePair("UserType", "D"));
                        else
                            postParameters.add(new BasicNameValuePair("UserType", "N"));
                    } else
                        postParameters.add(new BasicNameValuePair("UserType", "N"));

                    response = AppUtils.callWebServiceWithMultiParam(Home_Activity.this, postParameters, QueryUtils.methodToGetNewlyProducts, TAG);
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

                    executeToGetSectionBelowSlider();

                    if (jsonObject.getString("Status").equalsIgnoreCase("True")) {
                        if (jsonObject.getJSONArray("Data").length() > 0) {
                            DrawNewlyProducts(jsonObject.getJSONArray("Data"), "Newly");
                        }
                    } else {
                        AppUtils.alertDialog(Home_Activity.this, "Sorry Seems to be a server error. Please try again!!!");
                    }
                } catch (Exception ignored) {

                }
            }
        }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    private void executeTogetDrawerMenuItems() {
        new AsyncTask<Void, Void, String>() {

            @Override
            protected void onPreExecute() {
                AppUtils.showProgressDialog(Home_Activity.this);
            }

            @Override
            protected String doInBackground(Void... params) {

                Thread.currentThread().setPriority(Thread.MAX_PRIORITY);

                String response = "";
                try {
                    List<NameValuePair> postParameters = new ArrayList<>();
                    response = AppUtils.callWebServiceWithMultiParam(Home_Activity.this, postParameters, QueryUtils.methodtoGetDrawerMenuItems, TAG);

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

    private void getImageSliderResult(JSONArray jsonArrayData) {

        try {
            imageSlider.clear();

            for (int i = 0; i < jsonArrayData.length(); i++) {
                final JSONObject jsonObject = jsonArrayData.getJSONObject(i);
                HashMap<String, String> map = new HashMap<>();
                map.put("Images", jsonObject.getString("Images"));
                map.put("NavigateURL", jsonObject.getString("NavigateURL"));
                imageSlider.add(map);
            }

            setImageSlider();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void enableExpandableList() {
        listDataHeader = new ArrayList<>();
        listDataChild = new HashMap<>();

        List<String> Empty = new ArrayList<>();

        List<String> cat2 = new ArrayList<>();

        for (int i = 0; i < AppController.category1.size(); i++) {
            for (int j = 0; j < AppController.category2.size(); j++) {
                if (AppController.category1.get(i).get("HID").equals(AppController.category2.get(j).get("HID"))) {
                    cat2.add((AppController.category2.get(j).get("Category")));
                }
            }
        }

        listDataHeader.add("Shop Product");
        listDataChild.put(listDataHeader.get(0), cat2);

        if (AppController.getSpIsLogin().getBoolean(SPUtils.IS_LOGIN, false)) {
            String Usertype = (AppController.getSpUserInfo().getString(SPUtils.USER_TYPE, ""));

            if (Usertype.equalsIgnoreCase("DISTRIBUTOR")) {
                listDataHeader.add(getResources().getString(R.string.dashboard));
                listDataChild.put(listDataHeader.get(listDataHeader.size() - 1), Empty);
            }
            listDataHeader.add("My Orders");
            listDataChild.put(listDataHeader.get(listDataHeader.size() - 1), Empty);

            listDataHeader.add("Achievers Gallery");
            listDataChild.put(listDataHeader.get(listDataHeader.size() - 1), Empty);
        }
        listDataHeader.add("Downloads");
        listDataChild.put(listDataHeader.get(listDataHeader.size() - 1), Empty);

        ExpandableListAdapter listAdapter = new ExpandableListAdapter(this, listDataHeader, listDataChild);
        expListView.setAdapter(listAdapter);

        expListView.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {

            @Override
            public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id) {

                String GroupTitle = listDataHeader.get(groupPosition);

                if (GroupTitle.trim().equalsIgnoreCase(getResources().getString(R.string.dashboard))) {
                    startActivity(new Intent(Home_Activity.this, DashBoard_Activity.class));

                    if (drawer.isDrawerOpen(GravityCompat.START)) {
                        drawer.closeDrawer(GravityCompat.START);
                    }
                } else if (GroupTitle.trim().equalsIgnoreCase("My Orders")) {
                    startActivity(new Intent(Home_Activity.this, MyOrders_Activity.class));
                } else if (GroupTitle.trim().equalsIgnoreCase("Achievers Gallery")) {
                    startActivity(new Intent(Home_Activity.this, MyAchivers_Activity.class));

                    if (drawer.isDrawerOpen(GravityCompat.START)) {
                        drawer.closeDrawer(GravityCompat.START);
                    }
                } else if (GroupTitle.trim().equalsIgnoreCase("Downloads")) {
                    startActivity(new Intent(Home_Activity.this, DownloadSection_Activity.class));

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
                    startActivity(new Intent(Home_Activity.this, Profile_View_Activity.class));
                } else if (ChildItemTitle.trim().equalsIgnoreCase(getResources().getString(R.string.change_password))) {
                    startActivity(new Intent(Home_Activity.this, Change_Password_Activity.class));
                } else if (ChildItemTitle.trim().equalsIgnoreCase(getResources().getString(R.string.new_joining))) {
                    startActivity(new Intent(Home_Activity.this, Register_Activity.class));
                } else if (ChildItemTitle.trim().equalsIgnoreCase(getResources().getString(R.string.upload_kyc))) {
                    startActivity(new Intent(Home_Activity.this, KYCUploadDocument_Activity.class));
                } else if (ChildItemTitle.trim().equalsIgnoreCase(getResources().getString(R.string.generation_structure))) {
                    startActivity(new Intent(Home_Activity.this, Sponsor_genealogy_Activity.class));
                } else if (ChildItemTitle.trim().equalsIgnoreCase(getResources().getString(R.string.sponsor_downline))) {
                    startActivity(new Intent(Home_Activity.this, Sponsor_team_details_Activity.class).putExtra("Action", "Sponsor"));
                } else if (ChildItemTitle.trim().equalsIgnoreCase(getResources().getString(R.string.bv_detail_report))) {
                    startActivity(new Intent(Home_Activity.this, Sponsor_team_details_Activity.class).putExtra("Action", "Direct"));
                } else if (ChildItemTitle.trim().equalsIgnoreCase(getResources().getString(R.string.welcome_letter))) {
                    startActivity(new Intent(Home_Activity.this, WelcomeLetter_Activity.class));
                } else if (ChildItemTitle.trim().equalsIgnoreCase(getResources().getString(R.string.purchase_reports))) {
                    startActivity(new Intent(Home_Activity.this, Repurchase_BV_Detail.class));
                } else if (ChildItemTitle.trim().equalsIgnoreCase("Team Repurchase BV Summary")) {
                    startActivity(new Intent(Home_Activity.this, Repurchase_BV_Summary_Team_Activity.class));
                } else if (ChildItemTitle.trim().equalsIgnoreCase(getResources().getString(R.string.wallet_detail))) {
                    startActivity(new Intent(Home_Activity.this, Wallet_Transaction_Report_Activity.class));
                } else if (ChildItemTitle.trim().equalsIgnoreCase("Wallet Request Report")) {
                    startActivity(new Intent(Home_Activity.this, Wallet_Request_Status_Report_Activity.class));
                } else if (ChildItemTitle.trim().equalsIgnoreCase("Request For Wallet Amount")) {
                    startActivity(new Intent(Home_Activity.this, Wallet_Request_Amount_Activity.class));
                } else if (ChildItemTitle.trim().equalsIgnoreCase(getResources().getString(R.string.monthly_incentive))) {
                    startActivity(new Intent(Home_Activity.this, Monthly_Incentive_Activity.class));
                } else if (ChildItemTitle.trim().equalsIgnoreCase(getResources().getString(R.string.monthly_incentive_detail_report))) {
                    startActivity(new Intent(Home_Activity.this, Monthly_Incentive_Detail_Activity.class));
                } else if (ChildItemTitle.trim().equalsIgnoreCase("TDS Detail Report")) {
                    startActivity(new Intent(Home_Activity.this, TDS_detail_report_Activity.class));
                } else {
                    Intent intent = new Intent(Home_Activity.this, ProductListGrid_Activity.class);
                    String CID = "";
                    String HID = "";

                    for (int i = 0; i < AppController.category2.size(); i++) {
                        String category = AppController.category2.get(i).get("Category");

                        if ((ChildItemTitle.trim().equalsIgnoreCase(category))) {
                            CID = AppController.category2.get(i).get("CID");
                            HID = AppController.category2.get(i).get("HID");
                        }
                    }

/*
                    intent.putExtra("HID", "" + HID);
                    intent.putExtra("CID", "" + CID);
                    startActivity(intent);
*/
                    intent.putExtra("Type", "" + "C");
                    intent.putExtra("categoryID", "" + CID);
                    startActivity(intent);
                }

                if (drawer.isDrawerOpen(GravityCompat.START)) {
                    drawer.closeDrawer(GravityCompat.START);
                }
                return false;
            }
        });
    }

    private void prepareListDataDistributor(List<String> listDataHeader, Map<String, List<String>> listDataChild, JSONArray HeadingJarray) {
        List<String> Empty = new ArrayList<>();
        try {
//            ArrayList<String> MenuAl = new ArrayList<>();
//            for (int i = 0; i < HeadingJarray.length(); i++) {
//                if (HeadingJarray.getJSONObject(i).getInt("ParentId") == 0)
//                    MenuAl.add(HeadingJarray.getJSONObject(i).getString("MenuName"));
//            }
//
//            for (int aa = 0; aa < MenuAl.size(); aa++) {
//                ArrayList<String> SubMenuAl = new ArrayList<>();
//
//                for (int bb = 0; bb < HeadingJarray.length(); bb++) {
//                    if (HeadingJarray.getJSONObject(aa).getInt("MenuId") == HeadingJarray.getJSONObject(bb).getInt("ParentId")) {
//                        SubMenuAl.add(AppUtils.CapsFirstLetterString(HeadingJarray.getJSONObject(bb).getString("MenuName")));
//                    }
//                }
//                listDataHeader.add(AppUtils.CapsFirstLetterString(MenuAl.get(aa)));
//                listDataChild.put(listDataHeader.get(aa + 1), SubMenuAl);
//            }

            listDataHeader.add("My Orders");
            listDataChild.put(listDataHeader.get(listDataHeader.size() - 1), Empty);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void executeWalletBalanceRequest() {
        try {
            if (AppUtils.isNetworkAvailable(Home_Activity.this)) {
                new AsyncTask<Void, Void, String>() {
                    @Override
                    protected String doInBackground(Void... params) {
                        String response = "";
                        try {
                            List<NameValuePair> postParameters = new ArrayList<>();
                            postParameters.add(new BasicNameValuePair("Formno", AppController.getSpUserInfo().getString(SPUtils.USER_FORM_NUMBER, "")));
                            response = AppUtils.callWebServiceWithMultiParam(Home_Activity.this,
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
                                    String count_text = "Wallet Balance " + jsonArrayData.getJSONObject(0).getString("WBalance");
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

    @Override
    protected void onRestart() {
        super.onRestart();
        try {
            setOptionMenu();
            enableExpandableList();
            LoadNavigationHeaderItems();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        setOptionMenu();
        LoadNavigationHeaderItems();

        if (AppController.getSpIsLogin().getBoolean(SPUtils.IS_LOGIN, false)) {
            if (timer != null) {
                timer.cancel();
                Log.i("Main timer", "cancel timer");
                timer = null;
            }
        } else {
            //   Toast.makeText(this, "Already logout", Toast.LENGTH_SHORT).show();
            Log.i("Main timer", "Already logout");
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
            AppUtils.dismissProgressDialog();
        } catch (Exception e) {
            e.printStackTrace();
            AppUtils.showExceptionDialog(Home_Activity.this);
        }
    }

    @Override
    public void onBackPressed() {
        try {
            if (drawer.isDrawerOpen(navigationView)) {
                drawer.closeDrawer(navigationView);
            } else {
                showExitDialog();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        if (drawerToggle != null) {
            drawerToggle.syncState();
        }
    }


    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (drawerToggle != null) {
            drawerToggle.onConfigurationChanged(newConfig);
        }
    }

    public void loadProductImage(String imageURL, ImageView imageView) {
        try {
            if (!Home_Activity.this.isFinishing()) {
                Glide.with(Home_Activity.this)
                        .load(imageURL)
                        .placeholder(R.drawable.ic_no_image)
                        .error(R.drawable.ic_no_image)
                        .fallback(R.drawable.ic_no_image)
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .animate(AppUtils.getAnimatorImageLoading())
                        .into(imageView);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void executeToGetSectionBelowSlider() {
        new AsyncTask<Void, Void, String>() {
            protected void onPreExecute() {
                AppUtils.showProgressDialog(Home_Activity.this);
            }

            @Override
            protected String doInBackground(Void... params) {
                String response = "";
                try {
                    List<NameValuePair> postParameters = new ArrayList<>();
                    response = AppUtils.callWebServiceWithMultiParam(Home_Activity.this, postParameters, QueryUtils.methodHomePageSectionjustdowntoSlider, TAG);
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
                        if (jsonObject.getJSONArray("Data").length() > 0) {
                            LoadHomePageSectionjustdowntoSlider(jsonObject.getJSONArray("Data"));
                        }
                    } else {
                        AppUtils.alertDialog(Home_Activity.this, "Sorry Seems to be an server error. Please try again!!!");
                    }
                } catch (Exception ignored) {

                }
            }
        }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    public void LoadHomePageSectionjustdowntoSlider(JSONArray Jarray) {
        for (int i = 0; i < Jarray.length(); i++) {
            try {
                HashMap<String, String> map = new HashMap<>();

                JSONObject Jobject = Jarray.getJSONObject(i);

                String imgpath = Jobject.getString("ImgPath");
                String App_Category = Jobject.getString("App_Category");
                String App_Level_1SubCategory = Jobject.getString("App_Level_1SubCategory");
                String App_Level_2SubCategory = Jobject.getString("App_Level_2SubCategory");
                String App_ProductID = Jobject.getString("App_ProductID");
                String ImgText = Jobject.getString("ImgText");
                int index = Jobject.getInt("SeqNo");

                map.put("Images", imgpath);
                map.put("App_Category", App_Category);
                map.put("App_Level_1SubCategory", App_Level_1SubCategory);
                map.put("App_Level_2SubCategory", App_Level_2SubCategory);
                map.put("App_ProductID", App_ProductID);
                map.put("ImgText", ImgText);
                imagesBelowSlider.add(index, map);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        if (!TextUtils.isEmpty(imagesBelowSlider.get(1).get("Images")))
            AppUtils.loadHomePageImage(Home_Activity.this, imagesBelowSlider.get(1).get("Images"), imageView1);

        if (!TextUtils.isEmpty(imagesBelowSlider.get(2).get("Images")))
            AppUtils.loadHomePageImage(Home_Activity.this, imagesBelowSlider.get(2).get("Images"), imageView2);

        if (!TextUtils.isEmpty(imagesBelowSlider.get(3).get("Images")))
            AppUtils.loadHomePageImage(Home_Activity.this, imagesBelowSlider.get(3).get("Images"), imageView3);

        if (!TextUtils.isEmpty(imagesBelowSlider.get(4).get("Images")))
            AppUtils.loadHomePageImage(Home_Activity.this, imagesBelowSlider.get(4).get("Images"), imageView4);

        if (!TextUtils.isEmpty(imagesBelowSlider.get(5).get("Images")))
            AppUtils.loadHomePageImage(Home_Activity.this, imagesBelowSlider.get(5).get("Images"), imageView5);


        imageView1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HandleOnClikOnItems(1);
            }
        });
        imageView2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HandleOnClikOnItems(2);
            }
        });
        imageView3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HandleOnClikOnItems(3);
            }
        });
        imageView4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HandleOnClikOnItems(4);
            }
        });
        imageView5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HandleOnClikOnItems(5);
            }
        });
    }

    private void HandleOnClikOnItems(int position) {

        try {
            String App_Category = imagesBelowSlider.get(position).get("App_Category");
            String App_Level_1SubCategory = imagesBelowSlider.get(position).get("App_Level_1SubCategory");
            String App_Level_2SubCategory = imagesBelowSlider.get(position).get("App_Level_2SubCategory");
            String App_ProductID = imagesBelowSlider.get(position).get("App_ProductID");

            if (!App_ProductID.equalsIgnoreCase("0")) {
                Intent intent = new Intent(Home_Activity.this, ProductDetail_Activity.class);
                intent.putExtra("productID", "" + App_ProductID);
                startActivity(intent);
            } else if (!App_Level_2SubCategory.equalsIgnoreCase("0")) {
                Intent intent = new Intent(Home_Activity.this, ProductListGrid_Activity.class);
                intent.putExtra("Type", "D");
                intent.putExtra("categoryID", "" + App_Level_2SubCategory);
                startActivity(intent);
            } else if (!App_Level_1SubCategory.equalsIgnoreCase("0")) {
                Intent intent = new Intent(Home_Activity.this, ProductListGrid_Activity.class);
                intent.putExtra("Type", "S");
                intent.putExtra("categoryID", "" + App_Level_1SubCategory);
                startActivity(intent);
            } else if (!App_Category.equalsIgnoreCase("0")) {
                Intent intent = new Intent(Home_Activity.this, ProductExpand_Activity.class);
                intent.putExtra("HID", "1");
                intent.putExtra("CID", "" + App_Category);
                startActivity(intent);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void executeThoughtOfTheDay() {
        try {
            if (AppUtils.isNetworkAvailable(Home_Activity.this)) {
                new AsyncTask<Void, Void, String>() {
                    @Override
                    protected String doInBackground(Void... params) {
                        String response = "";
                        try {
                            List<NameValuePair> postParameters = new ArrayList<>();
                            //   postParameters.add(new BasicNameValuePair("Formno", AppController.getSpUserInfo().getString(SPUtils.USER_FORM_NUMBER, "")));
                            response = AppUtils.callWebServiceWithMultiParam(Home_Activity.this,
                                    postParameters, QueryUtils.methodToLoad_ThoughtOfTheDay, TAG);

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
                                    getBreakingNewsResults(jsonArrayData);
                                  /*  String count_text =  jsonArrayData.getJSONObject(0).getString("NewsHdr");
                                    txt_thought_of_the_day.setText("" +count_text);
                                    txt_thought_of_the_day.setVisibility(View.VISIBLE);*/
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

    public void getBreakingNewsResults(JSONArray jsonArrayData) {
        try {
            String str_breaking_news = "";
            for (int i = 0; i < jsonArrayData.length(); i++) {
                JSONObject jsonArrayDataJSONObject = jsonArrayData.getJSONObject(i);
                str_breaking_news = str_breaking_news + jsonArrayDataJSONObject.getString("NewsHdr") + ".";
            }
            if (str_breaking_news.startsWith(".")) {
                str_breaking_news.replaceAll(".", " ");
            } else if (str_breaking_news.endsWith("...")) {
                str_breaking_news.replaceAll(".", " ");
            } else {
                str_breaking_news = str_breaking_news;
            }
            ll_thoughtoftheday.setVisibility(View.VISIBLE);
            txt_thought_of_the_day.setText(str_breaking_news);

            //  executemethodLoad_HomeNewsSection1();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /*code added by mukesh 31-10-2019 06:15 PM */
    private void executeToGetProductListRequest() {
        try {
            if (AppUtils.isNetworkAvailable(Home_Activity.this)) {
                new AsyncTask<Void, Void, String>() {
                    protected void onPreExecute() {
                        AppUtils.showProgressDialog(Home_Activity.this);
                    }

                    @Override
                    protected String doInBackground(Void... params) {
                        String response = "";
                        try {
                            List<NameValuePair> postParameters = new ArrayList<>();
                            postParameters.add(new BasicNameValuePair("Type", "H"));
                            postParameters.add(new BasicNameValuePair("CategoryID", "1"));
                            postParameters.add(new BasicNameValuePair("Sort", "1"));

                            postParameters.add(new BasicNameValuePair("PageIndex", "1"));
                            postParameters.add(new BasicNameValuePair("NumOfRows", "1000"));

                            postParameters.add(new BasicNameValuePair("FiltersCondition", "" + AppController.FiltersCondition));

                            if (AppController.getSpIsLogin().getBoolean(SPUtils.IS_LOGIN, false)) {
                                String Usertype = (AppController.getSpUserInfo().getString(SPUtils.USER_TYPE, ""));
                                if (Usertype.equalsIgnoreCase("CUSTOMER"))
                                    postParameters.add(new BasicNameValuePair("UserType", "N"));
                                else if (Usertype.equalsIgnoreCase("DISTRIBUTOR"))
                                    postParameters.add(new BasicNameValuePair("UserType", "D"));
                                else
                                    postParameters.add(new BasicNameValuePair("UserType", "N"));
                            } else
                                postParameters.add(new BasicNameValuePair("UserType", "N"));

                            response = AppUtils.callWebServiceWithMultiParam(Home_Activity.this, postParameters,
                                    QueryUtils.methodToGetProductList, TAG);
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
                            allproductsJObject = jsonObject;
                            if (jsonObject.getString("Status").equalsIgnoreCase("True")) {
                                JSONArray jsonArrayProductList = jsonObject.getJSONArray("ProductList");

                                if (jsonArrayProductList.length() > 0) {
                                    AllProducts(jsonArrayProductList, "Newly");
                                } else {
                                    //  noDataFound();
                                }
                            } else {
                                // noDataFound();
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            AppUtils.showExceptionDialog(Home_Activity.this);
                        }
                    }
                }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            }
        } catch (Exception e) {
            e.printStackTrace();
            AppUtils.showExceptionDialog(Home_Activity.this);
        }
    }

    private void AllProducts(JSONArray Jarray, String Type) {

        if (Type.equalsIgnoreCase("Newly"))
            LLBottom_Newly.removeAllViews();
        else
            LLBottom.removeAllViews();

        Typeface typeface = ResourcesCompat.getFont(this, R.font.gisha_0);

        float density = getResources().getDisplayMetrics().density;
        int paddingDp = (int) (10 * density);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        layoutParams.setMargins((int) (10 * density), (int) (10 * density), (int) (10 * density), (int) (10 * density));
        LinearLayout.LayoutParams textparams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        textparams.setMargins(0, (int) (5 * density), 0, 0);
        LinearLayout.LayoutParams textparams2 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        textparams2.weight = 1.0f;
        textparams2.gravity = Gravity.RIGHT | Gravity.END;

        LinearLayout.LayoutParams imageparams = new LinearLayout.LayoutParams((int) (200 * density), (int) (200 * density));

        String Usertype = (AppController.getSpUserInfo().getString(SPUtils.USER_TYPE, ""));

        if (Jarray.length() > 0) {
            try {
                Jarray = shuffleJsonArray(Jarray);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        for (int i = 0; i < Jarray.length(); i++) {

            try {
                  /*    products.setID("" + jsonObjectProducts.getString("ProdID"));
                products.setcode("" + jsonObjectProducts.getString("ProductCode"));
                products.setName("" + WordUtils.capitalizeFully(jsonObjectProducts.getString("ProductName")));
                products.setDiscount("" + jsonObjectProducts.getString("Discount"));
                products.setAvailFor("" + jsonObjectProducts.getString("AvailFor"));
                products.setBV("" + jsonObjectProducts.getString("BV"));
                products.setDiscountPer("" + jsonObjectProducts.getString("DiscountPer"));
                products.setIsDisplayDiscount(jsonObjectProducts.getBoolean("DiscDisp"));
                products.setImagePath("" + getResources().getString(R.string.productImageURL) + jsonObjectProducts.getString("NewImgPath"));
                products.setNewMRP("" + jsonObjectProducts.getString("NewMRP"));
                products.setNewDP("" + jsonObjectProducts.getString("NewDP"));
*/
                JSONObject Jobject = Jarray.getJSONObject(i);
                int ProdID = Jobject.getInt("ProdID");

                String ProductName = AppUtils.CapsFirstLetterString(Jobject.getString("ProductName"));

                String NewMRP = Jobject.getString("NewMRP");
                String Discount = Jobject.getString("DiscountPer");
                String NDP = Jobject.getString("NewDP");
                String imgpath = Jobject.getString("NewImgPath");
                String BV = Jobject.getString("BV");
                String ImagePath = getResources().getString(R.string.productImageURL) + imgpath;

                boolean DiscDisp = Jobject.getBoolean("DiscDisp");

                FrameLayout FL = new FrameLayout(getApplicationContext());
                FL.setLayoutParams(layoutParams);

                final LinearLayout LL = new LinearLayout(getApplicationContext());
                LL.setOrientation(LinearLayout.VERTICAL);
                LL.setMinimumHeight((int) (200 * density));
                LL.setMinimumWidth((int) (200 * density));

                ImageView imageView = new ImageView(getApplicationContext());
                imageView.setBackground(getResources().getDrawable(R.drawable.bg_round_rectangle_gray));
                imageView.setLayoutParams(imageparams);
                imageView.setPadding((int) (10 * density), (int) (10 * density), (int) (10 * density), (int) (10 * density));


                loadProductImage(ImagePath, imageView);

                TextView tvproductname = new TextView(getApplicationContext());
                tvproductname.setLayoutParams(textparams);
                tvproductname.setMaxLines(1);
                tvproductname.setEllipsize(TextUtils.TruncateAt.END);
                tvproductname.setTypeface(typeface);
                tvproductname.setTextColor(getResources().getColor(android.R.color.black));
                tvproductname.setText(ProductName);


                textparams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                textparams.setMargins(0, (int) (5 * density), 0, 0);

                final LinearLayout LL_MRP = new LinearLayout(getApplicationContext());
                LL_MRP.setOrientation(LinearLayout.HORIZONTAL);
                LL_MRP.setLayoutParams(textparams);


                TextView tvproductmrp = new TextView(getApplicationContext());
                tvproductmrp.setTextSize(TypedValue.COMPLEX_UNIT_SP, 13);

                tvproductmrp.setTextColor(getResources().getColor(android.R.color.black));
                tvproductmrp.setTypeface(typeface);

                String NewDP = "₹ " + " " + NDP + "/-";
                String DiscountPer = Discount + "% off";
                Spannable spanString;

                if (DiscountPer.equalsIgnoreCase("0% off") || DiscountPer.equalsIgnoreCase("0.0% off")) {
                    spanString = new SpannableString("" + NewDP);
                    spanString.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.color_orange_text)), 0, NewDP.length(), 0);
                    spanString.setSpan(new RelativeSizeSpan(1.0f), 0, NewDP.length(), 0);
                } else {
                    spanString = new SpannableString("" + NewDP + "  " + NewMRP + "  ");
                    spanString.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.color_orange_text)), 0, NewDP.length(), 0);
                    spanString.setSpan(new RelativeSizeSpan(1.0f), 0, NewDP.length(), 0);
                    StyleSpan boldSpan = new StyleSpan(Typeface.BOLD);
                    spanString.setSpan(boldSpan, 0, NewDP.length(), 0);
                    spanString.setSpan(new StrikethroughSpan(), (NewDP.length() + 2), (((NewDP.length() + 2)) + (NewMRP.length())), 0);
                    spanString.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.app_color_grayicon)), (NewDP.length() + 2), (((NewDP.length() + 2)) + (NewMRP.length())), 0);
                    spanString.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.app_color_grayicon)), ((((NewDP.length() + 2)) + (NewMRP.length())) + 2), spanString.length(), 0);
                }
                tvproductmrp.setText(spanString);


                TextView tvBV = new TextView(getApplicationContext());
                tvBV.setLayoutParams(textparams2);
                tvBV.setMaxLines(1);
                tvBV.setTypeface(typeface);
                tvBV.setGravity(Gravity.END);
                tvBV.setTextColor(getResources().getColor(android.R.color.black));
                tvBV.setText("BV:" + BV);


                LL_MRP.addView(tvproductmrp);

                if (Usertype.equalsIgnoreCase("DISTRIBUTOR"))
                    LL_MRP.addView(tvBV);

                FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT, Gravity.TOP | Gravity.RIGHT);

                TextView newtag = new TextView(getApplicationContext());
                newtag.setBackgroundColor(getResources().getColor(R.color.color_orange_text));
                newtag.setPadding(paddingDp / 2, paddingDp / 2, paddingDp / 2, paddingDp / 2);
                newtag.setText(DiscountPer);
                newtag.setTextColor(Color.WHITE);
                newtag.setLayoutParams(params);
                newtag.setTypeface(typeface);

                LL.setId(ProdID);
                LL.addView(imageView);
                LL.addView(LL_MRP);
                LL.addView(tvproductname);

                LL.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(Home_Activity.this, ProductDetail_Activity.class);
                        intent.putExtra("productID", "" + LL.getId());
                        startActivity(intent);
                    }
                });

                FL.addView(LL);

                if (DiscDisp)
                    FL.addView(newtag);


                if (Type.equalsIgnoreCase("Newly"))
                    LLBottom_Newly.addView(FL);
                else
                    LLBottom.addView(FL);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public JSONArray shuffleJsonArray(JSONArray array) throws JSONException {
        // Implementing Fisher–Yates shuffle
        Random rnd = new Random();
        for (int i = array.length() - 1; i >= 0; i--) {
            int j = rnd.nextInt(i + 1);
            // Simple swap
            Object object = array.get(j);
            array.put(j, array.get(i));
            array.put(i, object);
        }
        return array;
    }

    /*Notification using popup screens*/
    private void executeGetPopUpPictureExit() {
        try {
            if (AppUtils.isNetworkAvailable(Home_Activity.this)) {
                new AsyncTask<Void, Void, String>() {
//                    protected void onPreExecute() {
//                        AppUtils.showProgressDialog(Home_Activity.this);
//                    }

                    @Override
                    protected String doInBackground(Void... params) {
                        String response = "";
                        try {
                            List<NameValuePair> postParameters = new ArrayList<>();
                            //  postParameters.add(new BasicNameValuePair("IDNo", AppController.getSpUserInfo().getString(SPUtils.USER_ID_NUMBER, "")));
                            response = AppUtils.callWebServiceWithMultiParam(Home_Activity.this, postParameters, QueryUtils.methodGetPopImage, "HomeActivity");
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        return response;
                    }

                    @Override
                    protected void onPostExecute(String resultData) {
                        try {
                           // AppController.getSpUserInfo().edit().putString(SPUtils.USER_PopupSts, "F").commit();

                            JSONObject jsonObject = new JSONObject(resultData);
                            if (jsonObject.getString("Status").equalsIgnoreCase("True")) {
                                JSONArray jsonArrayData = jsonObject.getJSONArray("Data");
                                if (jsonArrayData.length() > 0) {
                                    for (int i = 0; i < jsonArrayData.length(); i++) {
                                        showDialog(jsonArrayData.getJSONObject(1).getString("ImagePath"));
                                    }
                                }
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            //   AppUtils.showExceptionDialog(Home_Activity.this);
                        }
                    }
                }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            }
        } catch (Exception e) {
            e.printStackTrace();
            AppUtils.showExceptionDialog(Home_Activity.this);
        }
    }

    public void showDialog(String Str_img_url) {
        try {
            AppController.getSpUserInfo().edit().putString(SPUtils.USER_PopupSts, "F").commit();

            final Dialog dialog = new Dialog(Home_Activity.this, R.style.ImageZoomDialog);
            dialog.getWindow().getAttributes().windowAnimations = R.style.dialog_animation;
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setCancelable(true);
            dialog.setContentView(R.layout.custom_popup);

            ImageView iv_popup = dialog.findViewById(R.id.iv_popup);

            Str_img_url = Str_img_url.replace(" ", "%20");
            AppUtils.loadHomePageImage(Home_Activity.this, Str_img_url, iv_popup);

            final ImageView iv_popup_close = (ImageView) dialog.findViewById(R.id.iv_popup_close);
            iv_popup_close.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    try {
                        dialog.dismiss();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });

            dialog.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}