package com.vpipl.kalpamrit;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.util.Log;
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
import com.vpipl.kalpamrit.Utils.QueryUtils;
import com.vpipl.kalpamrit.Utils.SPUtils;
import com.vpipl.kalpamrit.model.ProductsList;

import org.apache.commons.lang3.text.WordUtils;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by PC14 on 08-Apr-16.
 */
public class AddCartCheckOut_Activity extends AppCompatActivity {
    private static FrameLayout layout_cartProductList;
    private static LinearLayout layout_noData;
    private static ListView list_cartProducts;
    private static TextView txt_totalRs;
    private static TextView txt_total;
    private static Button btn_checkout;
    private static Button btn_startShopping;
    private List<ProductsList> productListupdate = new ArrayList<>();
    private ArrayList<HashMap<String, String>> imageList = new ArrayList<>();

    private String TAG = "AddCartCheckOutOld_Activity";
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
                    startActivity(new Intent(AddCartCheckOut_Activity.this, Login_Activity.class));
                else
                    AppUtils.showDialogSignOut(AddCartCheckOut_Activity.this);
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
                    Intent intent = new Intent(AddCartCheckOut_Activity.this, Home_Activity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                }
            });

            btn_checkout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (AppController.getSpIsLogin().getBoolean(SPUtils.IS_LOGIN, false)) {
                        startActivity(new Intent(AddCartCheckOut_Activity.this, CheckoutToPay_Activity.class));
                    } else {
                        startActivity(new Intent(AddCartCheckOut_Activity.this, Login_Checkout_Activity.class));
                    }
                }
            });



            if (AppController.selectedProductsList.size() > 0) {
                executeToGetProductLoadListRequest();
                // setProductSelectedCartList() ;
            } else {
                showEmptyCart();
            }

        } catch (Exception e) {
            e.printStackTrace();
            AppUtils.showExceptionDialog(AddCartCheckOut_Activity.this);
        }
    }

    private void setProductSelectedCartList() {
        try {
            Log.e("7" , "" + AppController.selectedProductsList.size()) ;
            if (AppController.selectedProductsList.size() > 0) {
                adapter = new AddCartCheckout_Adapter(AddCartCheckOut_Activity.this);
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

           /* if (AppController.selectedProductsList.size() > 0) {
                setProductSelectedCartList();
            } else {
                showEmptyCart();
            }
          */
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        try {

           /* if (AppController.selectedProductsList.size() > 0) {
                setProductSelectedCartList();
            } else {
                showEmptyCart();
            }*/

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
    private void executeToGetProductLoadListRequest() {
        try {
            if (AppUtils.isNetworkAvailable(AddCartCheckOut_Activity.this)) {
                new AsyncTask<Void, Void, String>() {
                    protected void onPreExecute() {
                        AppUtils.showProgressDialog(AddCartCheckOut_Activity.this);
                    }

                    @Override
                    protected String doInBackground(Void... params) {
                        String response = "";
                        try {
                            List<NameValuePair> postParameters = new ArrayList<>();
                            postParameters.add(new BasicNameValuePair("Type", "H"));
                            postParameters.add(new BasicNameValuePair("CategoryID", "1"));
                            // int sort = checkedRadioButton + 1;
                            postParameters.add(new BasicNameValuePair("Sort", ""));
                            postParameters.add(new BasicNameValuePair("PageIndex", "1"));
                            postParameters.add(new BasicNameValuePair("NumOfRows", "500"));

                            String productids = "0" ;
                            for (int i = 0; i < AppController.selectedProductsList.size(); i++) {
                                productids =  productids + AppController.selectedProductsList.get(i).getID()  + "," ;
                            }
                            //System.out.println("last char = " + productids.charAt(productids.length() - 1));

                            if(productids.endsWith(","))
                            {
                                productids = productids.substring(0,productids.length() - 1);
                            }

                            postParameters.add(new BasicNameValuePair("ProductIDs", "" + productids));

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
                            response = AppUtils.callWebServiceWithMultiParam(AddCartCheckOut_Activity.this, postParameters, QueryUtils.methodToCartProduct, TAG);
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
                                JSONArray jsonArrayProductList = jsonObject.getJSONArray("Data");
                                if (jsonArrayProductList.length() > 0) {
                                    Log.e("1" , "1") ;
                                    saveProductsList(jsonObject);
                                }
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            AppUtils.showExceptionDialog(AddCartCheckOut_Activity.this);
                        }
                    }
                }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            }
        } catch (Exception e) {
            e.printStackTrace();
            AppUtils.showExceptionDialog(AddCartCheckOut_Activity.this);
        }
    }

    private void saveProductsList(final JSONObject jsonObject) {
        try {
            Log.e("2" , "2") ;
            AppController.selectedProductsListUpdated.clear();
            JSONArray jsonArrayProductList = jsonObject.getJSONArray("Data");

            if(jsonArrayProductList.length() > 0) {
                for (int i = 0; i < jsonArrayProductList.length(); i++) {
                    JSONObject jsonObjectProducts = jsonArrayProductList.getJSONObject(i);

                    ProductsList products = new ProductsList();

                    products.setID("" + jsonObjectProducts.getString("ProductID"));
                    products.setUID("" + jsonObjectProducts.getString("UserProdID"));
                    products.setWeight("" + jsonObjectProducts.getString("Weight"));
                    products.setNewMRP("" + jsonObjectProducts.getString("NewMRP"));
                    products.setNewDP("" + jsonObjectProducts.getString("NewDP"));
                    products.setBV("" + jsonObjectProducts.getString("BV"));
                    products.setDiscountPer("" + jsonObjectProducts.getString("DiscountPer"));
                    products.setDescription("" + jsonObjectProducts.getString("ProductDesc"));
                    products.setsellerCode("" + jsonObjectProducts.getString("SellerCode"));
                    products.setDetail("" + jsonObjectProducts.getString("ProdDetail"));
                    products.setKeyFeature("" + jsonObjectProducts.getString("KeyFeature"));
                    products.setCatID("" + jsonObjectProducts.getString("CatID"));
                    products.setIsshipChrg("" + jsonObjectProducts.getString("IsshipChrg"));
                    products.setShipCharge("" + jsonObjectProducts.getString("ShipCharge"));

                    products.setcode("" + jsonObjectProducts.getString("ProductCode"));
                    products.setName("" + WordUtils.capitalizeFully(jsonObjectProducts.getString("ProductName")));
                    products.setDiscount("" + jsonObjectProducts.getString("Discount"));
                    products.setAvailFor("" + jsonObjectProducts.getString("AvailFor"));
                    products.setIsDisplayDiscount(true);
                    products.setImagePath("" + getResources().getString(R.string.productImageURL) + jsonObjectProducts.getString("ImgPath"));

                    productListupdate.add(products);
                }
            }
            if(productListupdate.size() > 0) {
                Log.e("3" , " " + productListupdate.size()) ;
                UpdateData();
            }

        } catch (Exception e) {
            e.printStackTrace();
            AppUtils.showExceptionDialog(AddCartCheckOut_Activity.this);
        }
    }

    public void UpdateData(){

        try{

            Log.e("4", " " + productListupdate.size());
// for(int i = 0; i < stringArray.length; i++){
            //   for (int i = 0; i < AppController.selectedProductsList.size(); i++) {

            for (int j = 0; j < productListupdate.size(); j++) {

                //    if (AppController.selectedProductsList.get(i).getID().equalsIgnoreCase(productListupdate.get(j).getID())) {
/*                      AppController.selectedProductsList.add(productListupdate.get(j).getNewMRP());
                        AppController.selectedProductsList.add(productListupdate.get(j).getNewDP());
*/
                ProductsList selectedProduct = new ProductsList();

                String randomNo = AppUtils.generateRandomAlphaNumeric();
                selectedProduct.setProductType("P");//“K” In case of Combo Product else “P” in Main Cart.
                selectedProduct.setOrderFor("WR");//this will be static WR stands for WareHouse
                selectedProduct.setRandomNo("" + randomNo.trim().replace(",", " "));
                selectedProduct.setParentProductID("0");//In Case of Main Cart it would be 0. In case of Subcart it would be Combo package ID.
                selectedProduct.setUID("0");//UID save only in case of combo package else value would be 0.
                selectedProduct.setID("" + productListupdate.get(j).getID());
                selectedProduct.setcode("" + productListupdate.get(j).getcode());
                selectedProduct.setName("" + productListupdate.get(j).getName());
                selectedProduct.setWeight("" + productListupdate.get(j).getWeight());
                selectedProduct.setNewMRP("" + productListupdate.get(j).getNewMRP());
                selectedProduct.setNewDP("" + productListupdate.get(j).getNewDP());
                selectedProduct.setBV("" + productListupdate.get(j).getBV());
                selectedProduct.setDiscountPer("" + productListupdate.get(j).getDiscountPer());
                selectedProduct.setQty("" + productListupdate.get(j).getQty());
                //    selectedProduct.setQty("" + AppController.selectedProductsList.get(i).getQty());
                selectedProduct.setBaseQty("1");
                selectedProduct.setsellerCode("" + productListupdate.get(j).getsellerCode());
                selectedProduct.setCatID("" + productListupdate.get(j).getCatID());
                selectedProduct.setIsshipChrg("" + productListupdate.get(j).getIsshipChrg());
                selectedProduct.setShipCharge("" + productListupdate.get(j).getShipCharge());


             //   if (imageList.size() > 0) {
                    selectedProduct.setImagePath("" + productListupdate.get(j).getImagePath());
              //  }
                AppController.selectedProductsListUpdated.add(selectedProduct);
                //        }
                //       }
            }
            Log.e("5", "" + AppController.selectedProductsListUpdated.size());
            Log.e("6", "" + AppController.selectedProductsList.size());

            if (AppController.selectedProductsListUpdated.size() > 0) {
                //  AppController.selectedProductsList.clear();
                AppController.selectedProductsList = AppController.selectedProductsListUpdated;
            }
            setProductSelectedCartList();

        }
        catch (Exception e) {
            e.getMessage();
            Log.e("6", "" + e.getMessage() );
        }
    }
}