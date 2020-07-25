package com.vpipl.kalpamrit;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.text.style.StrikethroughSpan;
import android.text.style.StyleSpan;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.vpipl.kalpamrit.Adapters.ProductDetailImageSliderViewPagerAdapter;
import com.vpipl.kalpamrit.Utils.AppUtils;
import com.vpipl.kalpamrit.Utils.BadgeDrawable;
import com.vpipl.kalpamrit.Utils.CirclePageIndicator;
import com.vpipl.kalpamrit.Utils.QueryUtils;
import com.vpipl.kalpamrit.Utils.SPUtils;
import com.vpipl.kalpamrit.model.ProductsList;

import org.apache.commons.lang3.text.WordUtils;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by PC14 on 08-Apr-16.
 */
public class ProductDetail_Activity extends AppCompatActivity {


    private static String TAG = "ProductDetail_Activity";
    private static HashMap<String, String> ProductDetails = new HashMap<>();

    TextView txt_productCode;

    private TextView txt_specification;

    private Button btn_addToCart;
    private Button btn_buyNow;
    private ViewPager image_viewPager;
    private CirclePageIndicator imagePageIndicator;
    private TextView txt_productName;
    private TextView txt_productDetail;
    private TextView txt_productAmount;
    private TextView txt_productDiscount;
    private TextView txt_productBV;
    private LinearLayout LLBottom;
    public static JSONObject hotsellingJObject;
    public LinearLayout ll_simila_products;

    boolean DiscDisp = false;

    private WebView webView_specification;

    private ArrayList<HashMap<String, String>> imageList = new ArrayList<>();
    private Boolean isBuyClick = true;

    private int currentPage = 0;
    private Timer timer;

    private int CartCount = 0;

    TextView txt_view_stock;

    JSONArray jsonArrayProductStockDetail;

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

    ImageView img_menu, img_cart, img_user;

    public void SetupToolbar() {
        img_menu = findViewById(R.id.img_nav_back);

        img_cart = findViewById(R.id.img_cart);
        img_user = findViewById(R.id.img_login_logout);

        img_cart.setVisibility(View.VISIBLE);

        img_menu.setImageDrawable(getResources().getDrawable(R.drawable.icon_nav_bar_close));
        img_menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        img_cart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //  startActivity(new Intent(ProductDetail_Activity.this, AddCartCheckOutOld_Activity.class));
                startActivity(new Intent(ProductDetail_Activity.this, AddCartCheckOut_Activity.class));
            }
        });

        img_user.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!AppController.getSpIsLogin().getBoolean(SPUtils.IS_LOGIN, false))
                    startActivity(new Intent(ProductDetail_Activity.this, Login_Activity.class));
                else
                    AppUtils.showDialogSignOut(ProductDetail_Activity.this);
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
        setContentView(R.layout.productdetail_activity);

        try {
            AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);

            Toolbar toolbar = findViewById(R.id.toolbar);
            setSupportActionBar(toolbar);
            getSupportActionBar().setTitle("");
            SetupToolbar();

            txt_productName = findViewById(R.id.txt_productName);
//          txt_productCode =(TextView)findViewById(R.id.txt_productCode);
            txt_productDetail = findViewById(R.id.txt_productDetail);
            txt_productAmount = findViewById(R.id.txt_productAmount);
            txt_productDiscount = findViewById(R.id.txt_productDiscount);
            txt_productBV = findViewById(R.id.txt_productBV);

            txt_view_stock = findViewById(R.id.txt_view_stock);

            webView_specification = findViewById(R.id.webView_specification);

            image_viewPager = findViewById(R.id.image_viewPager);
            imagePageIndicator = findViewById(R.id.imagePageIndicator);

            txt_specification = findViewById(R.id.txt_specification);

            btn_addToCart = findViewById(R.id.btn_addToCart);
            btn_buyNow = findViewById(R.id.btn_buyNow);
            LLBottom = findViewById(R.id.LLBottom);
            ll_simila_products = findViewById(R.id.ll_simila_products);

//            if (getIntent().getExtras() != null) {
//                if (AppUtils.isNetworkAvailable(ProductDetail_Activity.this)) {
//                    executeToGetProductDetailRequest();
//                } else {
//                    AppUtils.alertDialog(ProductDetail_Activity.this, getResources().getString(R.string.txt_networkAlert));
//                }
//            } else {
//                AppUtils.alertDialog(ProductDetail_Activity.this, getResources().getString(R.string.txt_networkAlert));
//            }

            final String pincode_sts = "N";
            btn_addToCart.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                 //   if (pincode_sts.equalsIgnoreCase("Y")) {
                        isBuyClick = false;
                        goToAddProductInCart();
                   /* } else {
                        AppUtils.alertDialog(ProductDetail_Activity.this, "Member pincode not exist in our service area. We don't provide service on this pincode area");
                    }*/
                }
            });

            btn_buyNow.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                 //   if (pincode_sts.equalsIgnoreCase("Y")) {
                        isBuyClick = true;
                        goToAddProductInCart();
                    /*} else {
                        AppUtils.alertDialog(ProductDetail_Activity.this, "Member pincode not exist in our service area. We don't provide service on this pincode area");
                    }*/
                }
            });

           /* txt_view_stock.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {

                        if (jsonArrayProductStockDetail != null && jsonArrayProductStockDetail.length() > 0) {
                            showViewStockDailog(jsonArrayProductStockDetail);
                        } else
                            executeToGetProductStock();

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });*/

        } catch (Exception e) {
            e.printStackTrace();
            AppUtils.showExceptionDialog(ProductDetail_Activity.this);
        }
    }

    private void goToAddProductInCart() {
        try {

            addItemInSelectedProductList();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void addItemInSelectedProductList() {
        try {
            boolean already_exists = false;
            for (int i = 0; i < AppController.selectedProductsList.size(); i++) {
                if (AppController.selectedProductsList.get(i).getName().equalsIgnoreCase(ProductDetails.get("ProductName"))) {
                    already_exists = true;
                }
            }

            if (already_exists) {
                if (isBuyClick) {
                    startActivity(new Intent(ProductDetail_Activity.this, AddCartCheckOut_Activity.class));
                } else
                    AppUtils.alertDialog(ProductDetail_Activity.this, "Selected Product alrady exist in Cart. Please update quantity in Cart.");
            } else {
                ProductsList selectedProduct = new ProductsList();

                String randomNo = AppUtils.generateRandomAlphaNumeric();

                selectedProduct.setProductType("P");//“K” In case of Combo Product else “P” in Main Cart.
                selectedProduct.setOrderFor("WR");//this will be static WR stands for WareHouse
                selectedProduct.setRandomNo("" + randomNo.trim().replace(",", " "));
                selectedProduct.setParentProductID("0");//In Case of Main Cart it would be 0. In case of Subcart it would be Combo package ID.
                selectedProduct.setUID("0");//UID save only in case of combo package else value would be 0.
                selectedProduct.setID("" + ProductDetails.get("ProductID"));
                selectedProduct.setcode("" + ProductDetails.get("UserProdID"));
                selectedProduct.setName("" + ProductDetails.get("ProductName"));
                selectedProduct.setWeight("" + ProductDetails.get("ProductWeight"));
                selectedProduct.setNewMRP("" + ProductDetails.get("NewMRP"));
                selectedProduct.setNewDP("" + ProductDetails.get("NewDP"));
                selectedProduct.setBV("" + ProductDetails.get("BV"));
                selectedProduct.setDiscountPer("" + ProductDetails.get("DiscountPer"));
                selectedProduct.setQty("1");
                selectedProduct.setBaseQty("1");
                selectedProduct.setsellerCode("" + ProductDetails.get("SellerCode"));
                selectedProduct.setCatID("" + ProductDetails.get("CatID"));
                selectedProduct.setIsshipChrg("" + ProductDetails.get("IsshipChrg"));
                selectedProduct.setShipCharge("" + ProductDetails.get("ShipCharge"));

                if (imageList.size() > 0) {
                    selectedProduct.setImagePath("" + imageList.get(0).get("image"));
                }

                AppController.selectedProductsList.add(selectedProduct);

                setBadgeCount(ProductDetail_Activity.this, (AppController.selectedProductsList.size()));

                if (isBuyClick) {
                    startActivity(new Intent(ProductDetail_Activity.this, AddCartCheckOut_Activity.class));
                } else {
                    AppUtils.alertDialog(ProductDetail_Activity.this, "Success: You have added " + ProductDetails.get("ProductName") + " to your shopping cart!");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void executeToGetProductDetailRequest() {
        try {
            if (AppUtils.isNetworkAvailable(ProductDetail_Activity.this)) {
                new AsyncTask<Void, Void, String>() {
                    protected void onPreExecute() {
                        AppUtils.showProgressDialog(ProductDetail_Activity.this);
                    }

                    @Override
                    protected String doInBackground(Void... params) {
                        String response = "";
                        try {
                            List<NameValuePair> postParameters = new ArrayList<>();
                            postParameters.add(new BasicNameValuePair("ProductID", getIntent().getExtras().getString("productID")));

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

                            response = AppUtils.callWebServiceWithMultiParam(ProductDetail_Activity.this, postParameters, QueryUtils.methodToGetProductDetail, TAG);
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
                                JSONArray jsonArrayProductDetail = jsonObject.getJSONArray("Data");
                                if (jsonArrayProductDetail.length() > 0) {
                                    saveProductDetails(jsonArrayProductDetail.getJSONObject(0));
                                } else {
                                    AppUtils.alertDialog(ProductDetail_Activity.this, jsonObject.getString("Message"));
                                }
                            } else {
                                AppUtils.alertDialog(ProductDetail_Activity.this, jsonObject.getString("Message"));
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            AppUtils.showExceptionDialog(ProductDetail_Activity.this);
                        }
                    }
                }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            }
        } catch (Exception e) {
            e.printStackTrace();
            AppUtils.showExceptionDialog(ProductDetail_Activity.this);
        }
    }

    private void saveProductDetails(final JSONObject jsonObjectProductDetails) {
        try {
            ProductDetails.clear();
            ProductDetails.put("ProductID", "" + jsonObjectProductDetails.getString("ProductID"));
            ProductDetails.put("UserProdID", "" + jsonObjectProductDetails.getString("UserProdID"));
            ProductDetails.put("ProductName", "" + WordUtils.capitalizeFully(jsonObjectProductDetails.getString("ProductName").trim()));

            ProductDetails.put("ProductWeight", "" + jsonObjectProductDetails.getString("Weight"));
            ProductDetails.put("NewMRP", "" + jsonObjectProductDetails.getString("NewMRP"));
            ProductDetails.put("NewDP", "" + jsonObjectProductDetails.getString("NewDP"));
            ProductDetails.put("BV", "" + jsonObjectProductDetails.getString("BV"));
            ProductDetails.put("DiscountPer", "" + jsonObjectProductDetails.getString("DiscountPer"));
            ProductDetails.put("ProductDesc", "" + WordUtils.capitalizeFully(jsonObjectProductDetails.getString("ProductDesc")));
            ProductDetails.put("SellerCode", "" + jsonObjectProductDetails.getString("SellerCode"));
            ProductDetails.put("ProdDetail", "" + WordUtils.capitalizeFully(jsonObjectProductDetails.getString("ProdDetail")));
            ProductDetails.put("KeyFeature", "" + WordUtils.capitalizeFully(jsonObjectProductDetails.getString("KeyFeature")));
            ProductDetails.put("CatID", "" + jsonObjectProductDetails.getString("CatID"));
            ProductDetails.put("IsshipChrg", "" + jsonObjectProductDetails.getString("IsshipChrg"));
            ProductDetails.put("ShipCharge", "" + jsonObjectProductDetails.getString("ShipCharge"));

            if (jsonObjectProductDetails.getString("IsDiscount").trim().equalsIgnoreCase("Y")) ;
            DiscDisp = true;

            setProductDetails();

            imageList.clear();

            if (!jsonObjectProductDetails.getString("ImagePath").equals("")) {
                HashMap<String, String> map = new HashMap<>();
                map.put("image", "" + getResources().getString(R.string.productImageURL) + jsonObjectProductDetails.getString("ImagePath"));
                imageList.add(map);
            }

            if (!jsonObjectProductDetails.getString("ImgPath1").equals("")) {
                HashMap<String, String> map = new HashMap<>();
                map.put("image", "" + getResources().getString(R.string.productImageURL) + jsonObjectProductDetails.getString("ImgPath1"));
                imageList.add(map);
            }

            if (!jsonObjectProductDetails.getString("ImgPath2").equals("")) {
                HashMap<String, String> map = new HashMap<>();
                map.put("image", "" + getResources().getString(R.string.productImageURL) + jsonObjectProductDetails.getString("ImgPath2"));
                imageList.add(map);
            }

            if (!jsonObjectProductDetails.getString("ImgPath3").equals("")) {
                HashMap<String, String> map = new HashMap<>();
                map.put("image", "" + getResources().getString(R.string.productImageURL) + jsonObjectProductDetails.getString("ImgPath3"));
                imageList.add(map);
            }

            if (!jsonObjectProductDetails.getString("Imgpath4").equals("")) {
                HashMap<String, String> map = new HashMap<>();
                map.put("image", "" + getResources().getString(R.string.productImageURL) + jsonObjectProductDetails.getString("Imgpath4"));
                imageList.add(map);
            }

            if (AppUtils.showLogs) Log.e(TAG, "imageList..." + imageList);

            if (imageList.size() == 0) {
                HashMap<String, String> map = new HashMap<>();
                map.put("image", "" + getResources().getString(R.string.productImageURL) + jsonObjectProductDetails.getString("ImgPath"));
                imageList.add(map);
            }

            if (imageList.size() > 0) {
                setImageSlider();
            }

            if (!getIntent().getExtras().getString("categoryID").equals(null)) {
                executeToGetSimilerProducts(jsonObjectProductDetails.getString("CatId1"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setProductDetails() {
        try {
            txt_productName.setText(ProductDetails.get("ProductName"));
//          txt_productCode.setText("Product Code: "+ProductDetails.get("UserProdID"));
            txt_productDetail.setText(Html.fromHtml(ProductDetails.get("ProdDetail")).toString().trim());

            txt_productBV.setText("");
            txt_productBV.setVisibility(View.GONE);

            String Usertype = (AppController.getSpUserInfo().getString(SPUtils.USER_TYPE, ""));
            if (Usertype.equalsIgnoreCase("DISTRIBUTOR")) {
                txt_productBV.setText("BV : " + ProductDetails.get("BV"));
                txt_productBV.setVisibility(View.VISIBLE);
            }

            String NewDP = "₹ " + ProductDetails.get("NewDP") + "/-";
            String NewMRP = ProductDetails.get("NewMRP");
            String DiscountPer = ProductDetails.get("DiscountPer") + "% off";
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
            txt_productAmount.setText(spanString);
            txt_productDiscount.setText(DiscountPer);

            if (DiscDisp)
                txt_productDiscount.setVisibility(View.VISIBLE);

            webView_specification.loadDataWithBaseURL(null, customFont(ProductDetails.get("ProductDesc")), "text/html", "utf-8", null);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setImageSlider() {
        try {
            image_viewPager.setAdapter(new ProductDetailImageSliderViewPagerAdapter(ProductDetail_Activity.this, imageList, ProductDetails.get("ProductID")));

            final float density = getResources().getDisplayMetrics().density;
            imagePageIndicator.setFillColor(getResources().getColor(R.color.colorPrimaryDark));
            imagePageIndicator.setStrokeColor(getResources().getColor(R.color.app_color_gry));
            imagePageIndicator.setPageColor(getResources().getColor(R.color.app_color_gry));
            imagePageIndicator.setStrokeWidth();
            imagePageIndicator.setRadius(4 * density);
            imagePageIndicator.setViewPager(image_viewPager);

            final Handler handler = new Handler();

            final Runnable Update = new Runnable() {
                public void run() {
                    if (currentPage == imageList.size()) {
                        currentPage = 0;
                    }
                    image_viewPager.setCurrentItem(currentPage++, true);
                }
            };

            timer = new Timer(); // This will create a new Thread
            timer.schedule(new TimerTask() { // task to be scheduled

                @Override
                public void run() {
                    handler.post(Update);
                }
            }, 500, 3000);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void setOptionMenu() {
        try {

            setBadgeCount(ProductDetail_Activity.this, (AppController.selectedProductsList.size()));
            if (AppController.getSpIsLogin().getBoolean(SPUtils.IS_LOGIN, false)) {
                img_user.setImageDrawable(getResources().getDrawable(R.drawable.icon_logout_orange));
            } else {
                img_user.setImageDrawable(getResources().getDrawable(R.drawable.icon_distributor_login_orange));
            }

            String Usertype = (AppController.getSpUserInfo().getString(SPUtils.USER_TYPE, ""));
            if (Usertype.equalsIgnoreCase("DISTRIBUTOR"))
                txt_view_stock.setVisibility(View.VISIBLE);
            else
                txt_view_stock.setVisibility(View.GONE);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onRestart() {
        super.onRestart();

        try {
            setOptionMenu();
            executeToGetProductDetailRequest();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        try {
            setOptionMenu();
            executeToGetProductDetailRequest();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
            AppUtils.dismissProgressDialog();
        } catch (Exception e) {
            e.printStackTrace();
            AppUtils.showExceptionDialog(ProductDetail_Activity.this);
        }
    }

    public String customFont(String content) {
        String header = "<html>\n" +
                "<head>\n" +
                "<style type=\"text/css\">\n" +
                "@font-face {\n" +
                "    font-family: MyFont;\n" +
                "    src: url(\"file:///android_asset/fonts/gisha_0.ttf\")\n" +
                "}\n" +
                "body {\n" +
                "    font-family: gisha_0;\n" +
                "    text-align: justify;\n" +
                "}\n" +
                "</style>\n" +
                "</head>\n" +
                "<body>";

        String footer = "</body>\n" +
                "</html>";

        String text = header + content + footer;

        return text;
    }


    private void executeToGetProductStock() {
        try {
            if (AppUtils.isNetworkAvailable(ProductDetail_Activity.this)) {
                new AsyncTask<Void, Void, String>() {
                    protected void onPreExecute() {
                        AppUtils.showProgressDialog(ProductDetail_Activity.this);
                    }

                    @Override
                    protected String doInBackground(Void... params) {
                        String response = "";
                        try {
                            List<NameValuePair> postParameters = new ArrayList<>();

                            postParameters.add(new BasicNameValuePair("Prodid", getIntent().getExtras().getString("productID")));
                            postParameters.add(new BasicNameValuePair("Pincode", AppController.getSpUserInfo().getString(SPUtils.USER_PINCODE, "0")));
                            postParameters.add(new BasicNameValuePair("CityName", AppController.getSpUserInfo().getString(SPUtils.USER_CITY, "")));
                            postParameters.add(new BasicNameValuePair("StateCode", AppController.getSpUserInfo().getString(SPUtils.USER_STATE_CODE, "0")));

                            response = AppUtils.callWebServiceWithMultiParam(ProductDetail_Activity.this, postParameters, QueryUtils.methodToGetProductStock, TAG);
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
                                jsonArrayProductStockDetail = jsonObject.getJSONArray("Data");

                                if (jsonArrayProductStockDetail.length() > 0) {
                                    showViewStockDailog(jsonArrayProductStockDetail);
                                } else {
                                    AppUtils.alertDialog(ProductDetail_Activity.this, jsonObject.getString("Message"));
                                }

                            } else {
                                AppUtils.alertDialog(ProductDetail_Activity.this, jsonObject.getString("Message"));
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            AppUtils.showExceptionDialog(ProductDetail_Activity.this);
                        }
                    }
                }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            }
        } catch (Exception e) {
            e.printStackTrace();
            AppUtils.showExceptionDialog(ProductDetail_Activity.this);
        }
    }

    private void showViewStockDailog(JSONArray jsonArrayProductStockDetail) {
        try {
            final Dialog dialog = new Dialog(this, R.style.ThemeDialogCustom);
            dialog.getWindow().getAttributes().windowAnimations = R.style.dialog_animation;
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setCancelable(false);
            dialog.setContentView(R.layout.custom_dialog_view_product_stock);

            float sp = 10;
            int px = (int) (sp * getResources().getDisplayMetrics().scaledDensity);

            TableRow.LayoutParams params = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT);
//            params.setMargins(px / 2, px / 2, px / 2, px / 2);
            params.weight = 1;

            if (jsonArrayProductStockDetail.length() > 0) {
                TableLayout ll = dialog.findViewById(R.id.displayLinear);

                Typeface typeface = ResourcesCompat.getFont(this, R.font.gisha_0);

                TableRow row1 = new TableRow(this);

                TableRow.LayoutParams lp1 = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT);
                row1.setLayoutParams(lp1);
                row1.setBackgroundColor(getResources().getColor(R.color.color_green_text));

                TextView A1 = new TextView(this);
                TextView B1 = new TextView(this);
                TextView C1 = new TextView(this);
                TextView D1 = new TextView(this);
                TextView E1 = new TextView(this);

                A1.setText("Name");
                B1.setText("Stock Qty");
                C1.setText("Type");
                D1.setText("City");
                E1.setText("Pin Code");

                A1.setPadding(px, px, px, px);
                B1.setPadding(px, px, px, px);
                C1.setPadding(px, px, px, px);
                D1.setPadding(px, px, px, px);
                E1.setPadding(px, px, px, px);

                A1.setTypeface(typeface);
                B1.setTypeface(typeface);
                C1.setTypeface(typeface);
                D1.setTypeface(typeface);
                E1.setTypeface(typeface);

                A1.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
                B1.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
                C1.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
                D1.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
                E1.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);

                A1.setGravity(Gravity.CENTER_VERTICAL);
                B1.setGravity(Gravity.CENTER);
                C1.setGravity(Gravity.CENTER_VERTICAL);
                D1.setGravity(Gravity.CENTER_VERTICAL);
                E1.setGravity(Gravity.CENTER_VERTICAL);

                A1.setTextColor(Color.WHITE);
                B1.setTextColor(Color.WHITE);
                C1.setTextColor(Color.WHITE);
                D1.setTextColor(Color.WHITE);
                E1.setTextColor(Color.WHITE);

                A1.setLayoutParams(params);
                B1.setLayoutParams(params);
                C1.setLayoutParams(params);
                D1.setLayoutParams(params);
                E1.setLayoutParams(params);

                row1.addView(C1);
                row1.addView(D1);
                row1.addView(E1);
                row1.addView(A1);
                row1.addView(B1);

                View view = new View(this);
                view.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 1));
                view.setBackgroundColor(Color.parseColor("#cccccc"));

                ll.addView(row1);
                ll.addView(view);

                for (int i = 0; i < jsonArrayProductStockDetail.length(); i++) {
                    JSONObject jobject = jsonArrayProductStockDetail.getJSONObject(i);

                    final String franchisee_name = WordUtils.capitalizeFully(jobject.getString("PartyName"));
                    final String CityName = WordUtils.capitalizeFully(jobject.getString("CityName"));
                    final String stock = jobject.getString("TotalQty");
                    final String PinCode = jobject.getString("PinCode");

                    final String GroupName = jobject.getString("GroupName");
                    StringBuilder sb = new StringBuilder(franchisee_name);


                    if (!GroupName.equalsIgnoreCase("Depot")) {

                        int ii = 0;
                        while ((ii = sb.indexOf(" ", ii + 25)) != -1) {
                            sb.replace(ii, ii + 1, "\n");
                        }

                        TableRow row = new TableRow(this);
                        TableRow.LayoutParams lp = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT);
                        row.setLayoutParams(lp);

                        if (i % 2 == 0)
                            row.setBackgroundColor(Color.WHITE);
                        else
                            row.setBackgroundColor(Color.parseColor("#dddddd"));


                        TextView A = new TextView(this);
                        TextView B = new TextView(this);
                        TextView C = new TextView(this);
                        TextView D = new TextView(this);
                        TextView E = new TextView(this);

                        A.setText(sb.toString());
                        B.setText("" + stock.replaceAll(".0", ""));
                        C.setText(GroupName);
                        D.setText(CityName);
                        E.setText(PinCode);

                        A.setPadding(px, px, px, px);
                        B.setPadding(px, px, px, px);
                        C.setPadding(px, px, px, px);
                        D.setPadding(px, px, px, px);
                        E.setPadding(px, px, px, px);

                        A.setTypeface(typeface);
                        B.setTypeface(typeface);
                        C.setTypeface(typeface);
                        D.setTypeface(typeface);
                        E.setTypeface(typeface);

                        A.setTextSize(TypedValue.COMPLEX_UNIT_SP, 13);
                        B.setTextSize(TypedValue.COMPLEX_UNIT_SP, 13);
                        C.setTextSize(TypedValue.COMPLEX_UNIT_SP, 13);
                        D.setTextSize(TypedValue.COMPLEX_UNIT_SP, 13);
                        E.setTextSize(TypedValue.COMPLEX_UNIT_SP, 13);

                        A.setGravity(Gravity.CENTER_VERTICAL);
                        B.setGravity(Gravity.CENTER);
                        C.setGravity(Gravity.CENTER_VERTICAL);
                        D.setGravity(Gravity.CENTER_VERTICAL);
                        E.setGravity(Gravity.CENTER_VERTICAL);

                        A.setLayoutParams(params);
                        B.setLayoutParams(params);
                        C.setLayoutParams(params);
                        D.setLayoutParams(params);
                        E.setLayoutParams(params);

                        row.addView(C);
                        row.addView(D);
                        row.addView(E);
                        row.addView(A);
                        row.addView(B);

                        View view_one = new View(this);
                        view_one.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 1));
                        view.setBackgroundColor(Color.parseColor("#cccccc"));

                        ll.addView(row);
                        ll.addView(view_one);
                    }
                }
            }
            dialog.findViewById(R.id.txt_submit).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                }
            });

            dialog.findViewById(R.id.textView3).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                }
            });

            dialog.show();

        } catch (Exception e) {
            e.printStackTrace();
            AppUtils.showExceptionDialog(this);
        }
    }

    /*Similer products*/
    private void executeToGetSimilerProducts(final String cateId) {
        new AsyncTask<Void, Void, String>() {
            protected void onPreExecute() {
                AppUtils.showProgressDialog(ProductDetail_Activity.this);
            }

            @Override
            protected String doInBackground(Void... params) {
                String response = "";
                try {
                    List<NameValuePair> postParameters = new ArrayList<>();
                    postParameters.add(new BasicNameValuePair("Type", "C"));
                    //  postParameters.add(new BasicNameValuePair("CategoryID", "" + cateId));
                    postParameters.add(new BasicNameValuePair("CategoryID", "" + getIntent().getExtras().getString("categoryID")));
                    int sort = 1;
                    postParameters.add(new BasicNameValuePair("Sort", "" + sort));
                    postParameters.add(new BasicNameValuePair("PageIndex", "1"));
                    postParameters.add(new BasicNameValuePair("NumOfRows", "50"));
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

                    response = AppUtils.callWebServiceWithMultiParam(ProductDetail_Activity.this, postParameters, QueryUtils.methodToGetProductList, TAG);
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
                    if (jsonObject.getString("Status").equalsIgnoreCase("True")) {
                        JSONArray jsonArrayProductList = jsonObject.getJSONArray("ProductList");
                        if (jsonArrayProductList.length() > 0) {
                            DrawNewlyProducts(jsonArrayProductList);
                        }
                    } else {
                        AppUtils.alertDialog(ProductDetail_Activity.this, "Sorry Seems to be a server error. Please try again!!!");
                    }
                } catch (Exception ignored) {

                }
            }
        }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    private void DrawNewlyProducts(JSONArray Jarray) throws JSONException {

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

        ArrayList<HashMap<String, String>> mylist = new ArrayList<>();
        if (Jarray.length() > 0) {
            for (int i = 0; i < Jarray.length(); i++) {
                JSONObject jsonObject = null;
                try {
                    jsonObject = Jarray.getJSONObject(i);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                HashMap<String, String> map = new HashMap<>();
                map.put("ProdID", jsonObject.getString("ProdID"));
                map.put("ProductName", AppUtils.CapsFirstLetterString(jsonObject.getString("ProductName").trim().toUpperCase()));
                map.put("NewMRP", jsonObject.getString("NewMRP"));
                map.put("DiscountPer", jsonObject.getString("DiscountPer"));
                map.put("NewDP", jsonObject.getString("NewDP"));
                String imgpath = jsonObject.getString("NewImgPath");
                String ImagePath = getResources().getString(R.string.productImageURL) + imgpath;
                map.put("ImagePath", "" + ImagePath);
                map.put("BV", jsonObject.getString("BV"));
                map.put("DiscDisp", jsonObject.getString("DiscDisp"));

                mylist.add(map);
            }
        }

        for (int i = 0; i < Jarray.length(); i++) {
            try {
                Collections.shuffle(mylist);

                ll_simila_products.setVisibility(View.VISIBLE);

                JSONObject Jobject = Jarray.getJSONObject(i);
                int ProdID = Integer.parseInt(mylist.get(i).get("ProdID"));

                String ProductName = mylist.get(i).get("ProductName");

                String NewMRP = mylist.get(i).get("NewMRP");
                String Discount = mylist.get(i).get("DiscountPer");
                String NDP = mylist.get(i).get("NewDP");
                String imgpath = mylist.get(i).get("ImagePath");
                String BV = mylist.get(i).get("BV");
                //   String ImagePath = getResources().getString(R.string.productImageURL) + imgpath;
                String ImagePath = imgpath;

                boolean DiscDisp = Boolean.parseBoolean(mylist.get(i).get("DiscDisp"));

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
                        Intent intent = new Intent(ProductDetail_Activity.this, ProductDetail_Activity.class);
                        intent.putExtra("productID", "" + LL.getId());
                        startActivity(intent);
                        finish();
                    }
                });

                FL.addView(LL);

                if (DiscDisp)
                    FL.addView(newtag);

                LLBottom.addView(FL);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void loadProductImage(String imageURL, ImageView imageView) {
        try {
            if (!ProductDetail_Activity.this.isFinishing()) {
                Glide.with(ProductDetail_Activity.this)
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

}