package com.vpipl.kalpamrit;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;

import com.vpipl.kalpamrit.Utils.AppUtils;
import com.vpipl.kalpamrit.Utils.SPUtils;

/**
 * Created by PC14 on 3/21/2016.
 */
public class Incentive_Statement_Activity extends AppCompatActivity {

    private String TAG = "Generation_Structure";
    private WebView webView_viewGenealogy;
    String URL = "";


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
                    startActivity(new Intent(Incentive_Statement_Activity.this, Login_Activity.class));
                else
                    AppUtils.showDialogSignOut(Incentive_Statement_Activity.this);
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
        setContentView(R.layout.activity_sponsor_genealogy);

        try {

            AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);

            Toolbar toolbar = findViewById(R.id.toolbar);
            setSupportActionBar(toolbar);
            getSupportActionBar().setTitle("");
            SetupToolbar();

            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

            webView_viewGenealogy = findViewById(R.id.webView_viewGenealogy);
            webView_viewGenealogy.getSettings().setJavaScriptEnabled(true);
            webView_viewGenealogy.getSettings().setBuiltInZoomControls(true);
            webView_viewGenealogy.getSettings().setDisplayZoomControls(true);
            webView_viewGenealogy.getSettings().setSupportZoom(true);

            webView_viewGenealogy.getSettings().setAppCacheMaxSize(5 * 1024 * 1024); // 5MB
            webView_viewGenealogy.getSettings().setAppCachePath(getApplicationContext().getCacheDir().getAbsolutePath());
            webView_viewGenealogy.getSettings().setAllowFileAccess(true);
            webView_viewGenealogy.getSettings().setAppCacheEnabled(true);
            webView_viewGenealogy.getSettings().setCacheMode(WebSettings.LOAD_DEFAULT); // load online by default

            webView_viewGenealogy.getSettings().setLoadWithOverviewMode(true);
            webView_viewGenealogy.getSettings().setUseWideViewPort(true);

            webView_viewGenealogy.setWebViewClient(new WebViewClient() {
                public void onPageStarted(WebView view, String url, Bitmap favicon) {
                    AppUtils.showProgressDialog(Incentive_Statement_Activity.this);
                }

                public boolean shouldOverrideUrlLoading(WebView view, String url) {

                    if (AppUtils.showLogs) Log.v(TAG, "shouldOverrideUrlLoading.....url..." + url);
                    view.loadUrl(url);

                    return true;
                }

                public void onPageFinished(WebView view, String url) {
                    AppUtils.dismissProgressDialog();
                }
            });

            if (AppUtils.isNetworkAvailable(Incentive_Statement_Activity.this)) {
                URL = getIntent().getStringExtra("URL");
                webView_viewGenealogy.loadUrl(URL);
            } else {
                AppUtils.alertDialog(Incentive_Statement_Activity.this, getResources().getString(R.string.txt_networkAlert));
            }

        } catch (Exception e) {
            e.printStackTrace();
            AppUtils.showExceptionDialog(Incentive_Statement_Activity.this);
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (event.getAction() == KeyEvent.ACTION_DOWN) {
            switch (keyCode) {
                case KeyEvent.KEYCODE_BACK:
                    if (webView_viewGenealogy.canGoBack()) {
                        webView_viewGenealogy.goBack();
                    } else {
                        finish();
                    }
                    return true;
            }
        }
        return super.onKeyDown(keyCode, event);
    }

}
