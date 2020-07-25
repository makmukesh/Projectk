package com.vpipl.kalpamrit;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;

import com.vpipl.kalpamrit.Utils.AppUtils;


public class Payment_Activity extends AppCompatActivity {

    private static final String TAG = "Payment_Activity";
    WebView web_view;
    String url = "";

    ImageView img_menu;
    ImageView img_cart;
    ImageView img_user;

    public void SetupToolbar() {
        img_menu = findViewById(R.id.img_nav_back);
        img_cart = findViewById(R.id.img_cart);
        img_user = findViewById(R.id.img_login_logout);

        img_cart.setVisibility(View.GONE);
        img_user.setVisibility(View.GONE);

        img_menu.setImageResource(R.drawable.icon_nav_bar_close);

        img_menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Payment_Activity.this, PaymentFailed_Activity.class));
                finish();
            }
        });
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);

        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
        SetupToolbar();


        url = getIntent().getStringExtra("URL");

        web_view = (WebView) findViewById(R.id.web_view);

        web_view.getSettings().setJavaScriptEnabled(true);
        web_view.getSettings().setBuiltInZoomControls(true);
        web_view.getSettings().setDisplayZoomControls(true);
        web_view.getSettings().setSupportZoom(true);
        web_view.getSettings().setAppCacheMaxSize(5 * 1024 * 1024); // 5MB
        web_view.getSettings().setAppCachePath(getApplicationContext().getCacheDir().getAbsolutePath());
        web_view.getSettings().setAllowFileAccess(true);
        web_view.getSettings().setAppCacheEnabled(true);
        web_view.getSettings().setCacheMode(WebSettings.LOAD_DEFAULT);
        web_view.getSettings().setLoadWithOverviewMode(true);
        web_view.getSettings().setUseWideViewPort(true);
        web_view.addJavascriptInterface(new MyJavaScriptInterface(this), "AndroidApplication");

        web_view.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                Log.e(TAG, url);
                if (url.contains("PaymentError.aspx")){
                                startActivity(new Intent(Payment_Activity.this, PaymentFailed_Activity.class));
                                finish();
                }
            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                Log.e(TAG, url);
                return true;
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                Log.e(TAG, url);
            }
        });

        if (AppUtils.isNetworkAvailable(Payment_Activity.this)) {
            web_view.loadUrl(url);
        } else {
            AppUtils.alertDialogWithFinish(Payment_Activity.this, getResources().getString(R.string.txt_networkAlert));
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
            AppUtils.dismissProgressDialog();
        } catch (Exception e) {
            e.printStackTrace();
            AppUtils.showExceptionDialog(Payment_Activity.this);
        }
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(Payment_Activity.this, PaymentFailed_Activity.class));
        finish();
    }

    public class MyJavaScriptInterface
    {
        Context mContext;

        MyJavaScriptInterface(Context c) {
            mContext = c;
        }

        @JavascriptInterface
        public void showToast(String toast)
        {
            startActivity(new Intent(Payment_Activity.this, ThanksScreen_Activity.class).putExtra("ORDERNUMBER",toast));
            finish();
        }
    }
}