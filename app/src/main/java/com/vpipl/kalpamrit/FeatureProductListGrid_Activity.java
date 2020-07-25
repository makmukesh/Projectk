package com.vpipl.kalpamrit;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.vpipl.kalpamrit.Adapters.ProductListGrid_Adapter;
import com.vpipl.kalpamrit.Utils.AppUtils;
import com.vpipl.kalpamrit.Utils.BadgeDrawable;
import com.vpipl.kalpamrit.Utils.SPUtils;
import com.vpipl.kalpamrit.model.ProductsList;

import org.apache.commons.lang3.text.WordUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by PC14 on 08-Apr-16.
 */
public class FeatureProductListGrid_Activity extends AppCompatActivity {
    private static String TAG = "ProductListGrid_Activity";

    private LinearLayout layout_convertIcon;
    private LinearLayout layout_productList;
    private LinearLayout layout_noData;
    private ImageView icon;
    private ListView list_products;
    private GridView gridView_products;
    private List<ProductsList> productList = new ArrayList<>();
    private ProductListGrid_Adapter adapter;
    private String isDisplayView = "Grid";
    private TextView txt_productHeading;
    private TextView txt_productTotal;

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
                startActivity(new Intent(FeatureProductListGrid_Activity.this, AddCartCheckOut_Activity.class));

            }
        });

        img_user.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!AppController.getSpIsLogin().getBoolean(SPUtils.IS_LOGIN, false))
                    startActivity(new Intent(FeatureProductListGrid_Activity.this, Login_Activity.class));
                else
                    AppUtils.showDialogSignOut(FeatureProductListGrid_Activity.this);
            }
        });

        if (AppController.getSpIsLogin().getBoolean(SPUtils.IS_LOGIN, false))
            img_user.setImageDrawable(getResources().getDrawable(R.drawable.icon_logout_orange));
        else
            img_user.setImageDrawable(getResources().getDrawable(R.drawable.icon_distributor_login_orange));
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.featureproductlistgrid_activity);

        try {
            AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);

            Toolbar toolbar = findViewById(R.id.toolbar);
            setSupportActionBar(toolbar);
            getSupportActionBar().setTitle("");
            SetupToolbar();

            layout_convertIcon = findViewById(R.id.layout_convertIcon);
            icon = findViewById(R.id.icon);
            layout_productList = findViewById(R.id.layout_productList);
            list_products = findViewById(R.id.list_products);
            gridView_products = findViewById(R.id.gridView_products);
            layout_noData = findViewById(R.id.layout_noData);

            txt_productHeading = findViewById(R.id.txt_productHeading);
            txt_productTotal = findViewById(R.id.txt_productTotal);

            if (AppUtils.isNetworkAvailable(FeatureProductListGrid_Activity.this)) {
              //  saveProductsList(Home_Activity.hotsellingJObject);
                saveAllProductsList(Home_Activity.allproductsJObject);
            } else {
                AppUtils.alertDialog(FeatureProductListGrid_Activity.this, getResources().getString(R.string.txt_networkAlert));
                noDataFound();
            }

            layout_convertIcon.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    try {
                        if (icon.getTag().equals("list")) {
                            isDisplayView = "list";
                            setListViewData();
                        } else {
                            isDisplayView = "Grid";
                            setGridViewData();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            AppUtils.showExceptionDialog(FeatureProductListGrid_Activity.this);
        }
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        try {
            if (AppUtils.isNetworkAvailable(FeatureProductListGrid_Activity.this)) {
              //  saveProductsList(Home_Activity.hotsellingJObject);
                saveAllProductsList(Home_Activity.allproductsJObject);
            } else {
                AppUtils.alertDialog(FeatureProductListGrid_Activity.this, getResources().getString(R.string.txt_networkAlert));
            }

            setOptionMenu();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void saveProductsList(final JSONObject jsonObject) {
        try {
            JSONArray jsonArrayProductList = jsonObject.getJSONArray("Data");

            productList.clear();

            txt_productHeading.setText("Best Sellers");
            txt_productTotal.setText("Showing " + jsonArrayProductList.length() + " items ");

            for (int i = 0; i < jsonArrayProductList.length(); i++) {
                JSONObject jsonObjectProducts = jsonArrayProductList.getJSONObject(i);

                ProductsList products = new ProductsList();
                products.setID("" + jsonObjectProducts.getString("ProdID"));
                products.setcode("" + jsonObjectProducts.getString("ProductCode"));
                products.setName("" + WordUtils.capitalizeFully(jsonObjectProducts.getString("ProductName")));
                products.setDiscount("" + jsonObjectProducts.getString("Discount"));
                products.setAvailFor("" + jsonObjectProducts.getString("AvailFor"));
                products.setBV("" + jsonObjectProducts.getString("BV"));
                products.setDiscountPer("" + jsonObjectProducts.getString("DiscountPer"));
                products.setIsDisplayDiscount( jsonObjectProducts.getBoolean("DiscDisp"));
                products.setImagePath("" + getResources().getString(R.string.productImageURL) + jsonObjectProducts.getString("NewImgPath"));
                products.setNewMRP("" + jsonObjectProducts.getString("NewMRP"));
                products.setNewDP("" + jsonObjectProducts.getString("NewDP"));
                productList.add(products);

            }

            if (isDisplayView.equalsIgnoreCase("Grid")) {
                setGridViewData();
            } else {
                setListViewData();
            }

        } catch (Exception e) {
            e.printStackTrace();
            AppUtils.showExceptionDialog(FeatureProductListGrid_Activity.this);
        }
    }

    private void saveAllProductsList(final JSONObject jsonObject) {
        try {
            JSONArray jsonArrayProductList = jsonObject.getJSONArray("ProductList");

            productList.clear();

            txt_productHeading.setText("All Produts");

            for (int i = 0; i < jsonArrayProductList.length(); i++) {
                JSONObject jsonObjectProducts = jsonArrayProductList.getJSONObject(i);

                ProductsList products = new ProductsList();
                products.setID("" + jsonObjectProducts.getString("ProdID"));
                products.setcode("" + jsonObjectProducts.getString("ProductCode"));
                products.setName("" + WordUtils.capitalizeFully(jsonObjectProducts.getString("ProductName")));
                products.setDiscount("" + jsonObjectProducts.getString("Discount"));
                products.setAvailFor("" + jsonObjectProducts.getString("AvailFor"));
                products.setBV("" + jsonObjectProducts.getString("BV"));
                products.setDiscountPer("" + jsonObjectProducts.getString("DiscountPer"));
                products.setIsDisplayDiscount( jsonObjectProducts.getBoolean("DiscDisp"));
                products.setImagePath("" + getResources().getString(R.string.productImageURL) + jsonObjectProducts.getString("NewImgPath"));
                products.setNewMRP("" + jsonObjectProducts.getString("NewMRP"));
                products.setNewDP("" + jsonObjectProducts.getString("NewDP"));
                productList.add(products);

            }

            if (isDisplayView.equalsIgnoreCase("Grid")) {
                setGridViewData();
            } else {
                setListViewData();
            }

        } catch (Exception e) {
            e.printStackTrace();
            AppUtils.showExceptionDialog(FeatureProductListGrid_Activity.this);
        }
    }

    private void setGridViewData() {
        try {
            icon.setTag("list");
            icon.setBackgroundDrawable(getResources().getDrawable(R.drawable.ic_icon_list));
            layout_noData.setVisibility(View.GONE);
            gridView_products.setVisibility(View.VISIBLE);
            list_products.setVisibility(View.GONE);
            layout_productList.setVisibility(View.VISIBLE);

            adapter = new ProductListGrid_Adapter(FeatureProductListGrid_Activity.this, productList, "Grid");
            gridView_products.setAdapter(adapter);

            gridView_products.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                    Intent intent = new Intent(FeatureProductListGrid_Activity.this, ProductDetail_Activity.class);
                    intent.putExtra("productID", productList.get(position).getID());
                    startActivity(intent);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setListViewData() {
        try {
            icon.setTag("grid");
            icon.setBackgroundDrawable(getResources().getDrawable(R.drawable.ic_icon_grid));

            layout_noData.setVisibility(View.GONE);
            gridView_products.setVisibility(View.GONE);
            list_products.setVisibility(View.VISIBLE);
            layout_productList.setVisibility(View.VISIBLE);
            adapter = new ProductListGrid_Adapter(FeatureProductListGrid_Activity.this, productList, "List");
            list_products.setAdapter(adapter);

            list_products.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                    Intent intent = new Intent(FeatureProductListGrid_Activity.this, ProductDetail_Activity.class);
                    intent.putExtra("productID", productList.get(position).getID());
                    startActivity(intent);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void noDataFound() {
        try {
            layout_noData.setVisibility(View.VISIBLE);
            gridView_products.setVisibility(View.GONE);
            list_products.setVisibility(View.GONE);
            layout_productList.setVisibility(View.GONE);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setOptionMenu() {
        try {

            setBadgeCount(FeatureProductListGrid_Activity.this, (AppController.selectedProductsList.size()));

            if (AppController.getSpIsLogin().getBoolean(SPUtils.IS_LOGIN, false)) {
                img_user.setImageDrawable(getResources().getDrawable(R.drawable.icon_logout_orange));
            } else {
                img_user.setImageDrawable(getResources().getDrawable(R.drawable.icon_distributor_login_orange));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        try {
            setOptionMenu();
          //  saveProductsList(Home_Activity.hotsellingJObject);
            saveAllProductsList(Home_Activity.allproductsJObject);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        try {
            AppController.FiltersCondition = "";
            AppController.priceFilterList.clear();
            AppController.discountFilterList.clear();
            AppController.filterList1.clear();
            AppController.comesFromFilter = false;

            AppUtils.dismissProgressDialog();
        } catch (Exception e) {
            e.printStackTrace();
            AppUtils.showExceptionDialog(FeatureProductListGrid_Activity.this);
        }
    }
}
