package com.vpipl.kalpamrit;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.vpipl.kalpamrit.Utils.AppUtils;


public class PaymentFailed_Activity extends AppCompatActivity {
    public TextView txt_name, txt_address, txt_mobNo, txt_email;
    String TAG = "PaymentFailed_Activity";
    Button btn_startShopping;



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
               moveToHome();
            }
        });
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.payment_failed_activity);

        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);

        try {

            Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
            setSupportActionBar(toolbar);
            getSupportActionBar().setTitle("");
            SetupToolbar();

            btn_startShopping = (Button) findViewById(R.id.btn_startShopping);

            btn_startShopping.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    moveToHome();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            AppUtils.showExceptionDialog(PaymentFailed_Activity.this);
        }
    }

    private void moveToHome() {
        try {
            Intent intent = new Intent(PaymentFailed_Activity.this, Home_Activity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onBackPressed() {
        moveToHome();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            moveToHome();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        try {
            if (AppUtils.showLogs) Log.v(TAG, "onDestroy() called.....");
            AppUtils.dismissProgressDialog();
        } catch (Exception e) {
            e.printStackTrace();
            AppUtils.showExceptionDialog(PaymentFailed_Activity.this);
        }
    }
}

