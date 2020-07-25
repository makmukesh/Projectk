package com.vpipl.kalpamrit;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.vpipl.kalpamrit.Adapters.AddCartCheckout_Adapter;
import com.vpipl.kalpamrit.Utils.AppUtils;
import com.vpipl.kalpamrit.Utils.SPUtils;

/**
 * Created by PC14 on 08-Apr-16.
 */
public class AddCartCheckOutOld_Activity extends AppCompatActivity {
    private static FrameLayout layout_cartProductList;
    private static LinearLayout layout_noData;
    private static ListView list_cartProducts;
    private static TextView txt_totalRs;
    private static TextView txt_total;
    private static Button btn_checkout;
    private static Button btn_startShopping;

    private String TAG = "AddCartCheckOut_Activity";
    private AddCartCheckout_Adapter adapter;

    ImageView img_menu;
    static ImageView img_cart;
    ImageView img_user;

    private static String calculateSelectedProductTotalAmount() {
        double amount = 0.0d;
        try {

//            String Usertype = (AppController.getSpUserInfo().getString(SPUtils.USER_TYPE, ""));
//            if (Usertype.equalsIgnoreCase("DISTRIBUTOR")) {
            for (int i = 0; i < AppController.selectedProductsList.size(); i++) {
                double countAmount;
                countAmount = ((Double.parseDouble(AppController.selectedProductsList.get(i).getNewDP())) * (Double.parseDouble(AppController.selectedProductsList.get(i).getQty())));
                amount = amount + countAmount;
            }
//            }
//            else
//            {
//                for (int i = 0; i < AppController.selectedProductsList.size(); i++) {
//                    double countAmount = 0.0d;
//                    countAmount = ((Double.parseDouble(AppController.selectedProductsList.get(i).getNewMRP())) * (Double.parseDouble(AppController.selectedProductsList.get(i).getQty())));
//                    amount = amount + countAmount;
//                }
//            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return ((int) amount) + "";
    }

    public static void setNetPaybleValue() {
        try {
            txt_total.setText(Html.fromHtml("Total Items (" + AppController.selectedProductsList.size() + ")"));
            txt_totalRs.setText(Html.fromHtml("₹ " + calculateSelectedProductTotalAmount() + "/-"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void showEmptyCart() {
        try {
            layout_cartProductList.setVisibility(View.GONE);
            list_cartProducts.setVisibility(View.GONE);
            layout_noData.setVisibility(View.VISIBLE);

            if (AppController.selectedProductsList.size() > 0) {
                if (img_cart != null)
                    img_cart.setVisibility(View.VISIBLE);
            } else {
                if (img_cart != null)
                    img_cart.setVisibility(View.GONE);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void SetupToolbar() {

        img_menu = findViewById(R.id.img_nav_back);

        img_cart = findViewById(R.id.img_cart);
        img_user = findViewById(R.id.img_login_logout);


        img_cart.setVisibility(View.VISIBLE);

        img_menu.setImageResource(R.drawable.icon_nav_bar_close);

        img_cart.setImageDrawable(getResources().getDrawable(R.drawable.ic_icon_cart_empty));

        img_menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        img_cart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cartClearAll();
            }
        });

        img_user.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!AppController.getSpIsLogin().getBoolean(SPUtils.IS_LOGIN, false))
                    startActivity(new Intent(AddCartCheckOutOld_Activity.this, Login_Activity.class));
                else
                    AppUtils.showDialogSignOut(AddCartCheckOutOld_Activity.this);
            }
        });

        if (AppController.getSpIsLogin().getBoolean(SPUtils.IS_LOGIN, false))
            img_user.setImageDrawable(getResources().getDrawable(R.drawable.icon_logout_orange));
        else
            img_user.setImageDrawable(getResources().getDrawable(R.drawable.icon_distributor_login_orange));
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.addcartcheckout_activity);

        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);

        try {

            Toolbar toolbar = findViewById(R.id.toolbar);
            setSupportActionBar(toolbar);
            getSupportActionBar().setTitle("");
            SetupToolbar();

            txt_totalRs = findViewById(R.id.txt_totalRs);
            txt_total = findViewById(R.id.txt_total);
            layout_cartProductList = findViewById(R.id.layout_cartProductList);
            layout_noData = findViewById(R.id.layout_noData);
            list_cartProducts = findViewById(R.id.list_cartProducts);
            btn_checkout = findViewById(R.id.btn_checkout);
            btn_startShopping = findViewById(R.id.btn_startShopping);

            btn_startShopping.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    finish();
                    Intent intent = new Intent(AddCartCheckOutOld_Activity.this, Home_Activity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                }
            });

            btn_checkout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (AppController.getSpIsLogin().getBoolean(SPUtils.IS_LOGIN, false)) {
                        startActivity(new Intent(AddCartCheckOutOld_Activity.this, CheckoutToPay_Activity.class));
                    } else {
                        startActivity(new Intent(AddCartCheckOutOld_Activity.this, Login_Checkout_Activity.class));
                    }
                }
            });

            if (AppController.selectedProductsList.size() > 0) {
                setProductSelectedCartList();
            } else {
                showEmptyCart();
            }

        } catch (Exception e) {
            e.printStackTrace();
            AppUtils.showExceptionDialog(AddCartCheckOutOld_Activity.this);
        }
    }

    private void setProductSelectedCartList() {
        try {
            if (AppController.selectedProductsList.size() > 0) {
                adapter = new AddCartCheckout_Adapter(AddCartCheckOutOld_Activity.this);
                list_cartProducts.setAdapter(adapter);
                adapter.notifyDataSetChanged();

                setNetPaybleValue();

                layout_cartProductList.setVisibility(View.VISIBLE);
                list_cartProducts.setVisibility(View.VISIBLE);
                layout_noData.setVisibility(View.GONE);

                list_cartProducts.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    }
                });

                if (AppController.selectedProductsList.size() > 0) {
                    if (img_cart != null)
                        img_cart.setVisibility(View.VISIBLE);
                } else {
                    if (img_cart != null)
                        img_cart.setVisibility(View.GONE);
                }

                showCartData();
            } else {
                showEmptyCart();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void showCartData() {
        try {
            layout_cartProductList.setVisibility(View.VISIBLE);
            list_cartProducts.setVisibility(View.VISIBLE);
            layout_noData.setVisibility(View.GONE);

            if (AppController.selectedProductsList.size() > 0) {
                if (img_cart != null)
                    img_cart.setVisibility(View.VISIBLE);
            } else {
                if (img_cart != null)
                    img_cart.setVisibility(View.GONE);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Override
    protected void onRestart() {
        super.onRestart();

        try {

            if (AppController.selectedProductsList.size() > 0) {
                setProductSelectedCartList();
            } else {
                showEmptyCart();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        try {

            if (AppController.selectedProductsList.size() > 0) {
                setProductSelectedCartList();
            } else {
                showEmptyCart();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void cartClearAll() {
        try {
            AppController.selectedProductsList.clear();
            showEmptyCart();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}