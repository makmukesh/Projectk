package com.vpipl.kalpamrit;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.BottomSheetDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.vpipl.kalpamrit.Adapters.ProductListGrid_Adapter;
import com.vpipl.kalpamrit.Utils.AppUtils;
import com.vpipl.kalpamrit.Utils.BadgeDrawable;
import com.vpipl.kalpamrit.Utils.QueryUtils;
import com.vpipl.kalpamrit.Utils.SPUtils;
import com.vpipl.kalpamrit.model.FilterList2CheckBox;
import com.vpipl.kalpamrit.model.ProductsList;

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
public class SearchProducts_Activity extends AppCompatActivity {
    private static String TAG = "SearchProducts_Activity";

    int NoofProduct = 0;
    int NumOfRows = 16;
    private LinearLayout layout_convertIcon;
    private LinearLayout layout_productList;
    private LinearLayout layout_noData;
    private LinearLayout layout_sort;
    private LinearLayout layout_filter;
    private ImageView icon;
    private ListView list_products;
    private GridView gridView_products;
    private List<ProductsList> productList = new ArrayList<>();
    private ProductListGrid_Adapter adapter;
    private String isDisplayView = "Grid";
    private TextView txt_productHeading;
    private TextView txt_productTotal;
    private BottomSheetDialog mBottomSheetDialog = null;
    private int checkedRadioButton = 0;
    private int pageIndex = 1;
    private Boolean isLoadMore = false;
    private String Keyword = "";

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
                startActivity(new Intent(SearchProducts_Activity.this, AddCartCheckOut_Activity.class));

            }
        });

        img_user.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!AppController.getSpIsLogin().getBoolean(SPUtils.IS_LOGIN, false))
                    startActivity(new Intent(SearchProducts_Activity.this, Login_Activity.class));
                else
                    AppUtils.showDialogSignOut(SearchProducts_Activity.this);
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
        setContentView(R.layout.searchproducts_activity);

        try {
            AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);

            Toolbar toolbar = findViewById(R.id.toolbar);
            setSupportActionBar(toolbar);
            getSupportActionBar().setTitle("");
            SetupToolbar();

            AppController.FiltersCondition = "";
            AppController.priceFilterList.clear();
            AppController.discountFilterList.clear();
            AppController.filterList1.clear();
            AppController.comesFromFilter = false;

            layout_filter = findViewById(R.id.layout_filter);
            layout_sort = findViewById(R.id.layout_sort);
            layout_convertIcon = findViewById(R.id.layout_convertIcon);

            icon = findViewById(R.id.icon);
            layout_productList = findViewById(R.id.layout_productList);
            list_products = findViewById(R.id.list_products);
            gridView_products = findViewById(R.id.gridView_products);
            layout_noData = findViewById(R.id.layout_noData);

            txt_productHeading = findViewById(R.id.txt_productHeading);
            txt_productTotal = findViewById(R.id.txt_productTotal);


            if (getIntent().getExtras() != null) {
                Keyword = getIntent().getStringExtra("Keyword");
                performDirectSearch(Keyword);
            }

            layout_sort.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    try {
                        showBottomSheetDialog();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });

            layout_filter.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    try {
                        startActivity(new Intent(SearchProducts_Activity.this, FilterProductScreen_Activity.class).putExtra("COMESFROM", SearchProducts_Activity.class.getSimpleName()));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });


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
            AppUtils.showExceptionDialog(SearchProducts_Activity.this);
        }
    }

    private void showBottomSheetDialog() {
        try {
            mBottomSheetDialog = new BottomSheetDialog(SearchProducts_Activity.this);
            View view = getLayoutInflater().inflate(R.layout.bottomsheet_sort_layout, null);
            RadioGroup radiogroup = view.findViewById(R.id.radiogroup);
            RadioButton radioButton1 = view.findViewById(R.id.radioButton1);
            RadioButton radioButton2 = view.findViewById(R.id.radioButton2);
            RadioButton radioButton3 = view.findViewById(R.id.radioButton3);
            RadioButton radioButton4 = view.findViewById(R.id.radioButton4);
            radiogroup.check(radiogroup.getChildAt(checkedRadioButton).getId());

            if (Build.VERSION.SDK_INT >= 21) {
                ColorStateList colorStateList = new ColorStateList(
                        new int[][]{

                                new int[]{-android.R.attr.state_enabled}, //disabled
                                new int[]{android.R.attr.state_enabled} //enabled
                        },
                        new int[]{

                                getResources().getColor(R.color.color_666666) //disabled
                                , getResources().getColor(R.color.colorPrimary) //enabled

                        }
                );


                radioButton1.setButtonTintList(colorStateList);//set the color tint list
                radioButton1.invalidate(); //could not be necessary
                radioButton2.setButtonTintList(colorStateList);//set the color tint list
                radioButton2.invalidate(); //could not be necessary
                radioButton3.setButtonTintList(colorStateList);//set the color tint list
                radioButton3.invalidate(); //could not be necessary
                radioButton4.setButtonTintList(colorStateList);//set the color tint list
                radioButton4.invalidate(); //could not be necessary
            }

            radiogroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(RadioGroup rg, int i) {
                    if (rg.getCheckedRadioButtonId() == rg.getChildAt(0).getId()) {
                        checkedRadioButton = 0;
                    } else if (rg.getCheckedRadioButtonId() == rg.getChildAt(1).getId()) {
                        checkedRadioButton = 1;
                    } else if (rg.getCheckedRadioButtonId() == rg.getChildAt(2).getId()) {
                        checkedRadioButton = 2;
                    } else if (rg.getCheckedRadioButtonId() == rg.getChildAt(3).getId()) {
                        checkedRadioButton = 3;
                    }

                    if (mBottomSheetDialog != null) {
                        mBottomSheetDialog.dismiss();
                    }

                    if (AppUtils.isNetworkAvailable(SearchProducts_Activity.this)) {
                        isLoadMore = false;
                        pageIndex = 1;
                        performDirectSearch(Keyword);
                    } else {
                        AppUtils.alertDialog(SearchProducts_Activity.this, getResources().getString(R.string.txt_networkAlert));
                        noDataFound();
                    }
                }
            });

            mBottomSheetDialog.setContentView(view);
            mBottomSheetDialog.show();
            mBottomSheetDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                @Override
                public void onDismiss(DialogInterface dialog) {
                    mBottomSheetDialog = null;
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void performDirectSearch(String Keyword) {
        try {
            if (AppUtils.isNetworkAvailable(SearchProducts_Activity.this)) {
                isLoadMore = false;
                pageIndex = 1;
                executeToGetSearchedProductListRequest(Keyword);
            } else {
                AppUtils.alertDialog(SearchProducts_Activity.this, getResources().getString(R.string.txt_networkAlert));
                noDataFound();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void executeToGetSearchedProductListRequest(final String searchKeyword) {
        try {
            if (AppUtils.isNetworkAvailable(SearchProducts_Activity.this)) {
                new AsyncTask<Void, Void, String>() {
                    protected void onPreExecute() {
                        AppUtils.showProgressDialog(SearchProducts_Activity.this);
                    }

                    @Override
                    protected String doInBackground(Void... params) {
                        String response = "";
                        try {
                            List<NameValuePair> postParameters = new ArrayList<>();
                            postParameters.add(new BasicNameValuePair("SearchContent", searchKeyword));
                            int sort = checkedRadioButton + 1;
                            postParameters.add(new BasicNameValuePair("SortBy", "" + sort));

//                            postParameters.add(new BasicNameValuePair("PageIndex", ""+pageIndex));
//                            postParameters.add(new BasicNameValuePair("NumOfRows",""+NumOfRows));

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

                            response = AppUtils.callWebServiceWithMultiParam(SearchProducts_Activity.this, postParameters, QueryUtils.methodToGetSearchProductsList, TAG);
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
                                JSONArray jsonArrayProductList = jsonObject.getJSONArray("HeadingMenu");
                                if (jsonArrayProductList.length() > 0) {
//                                  NoofProduct= Integer.parseInt(jsonObject.getJSONArray("NoofProduct").getJSONObject(0).getString("DtNoofProduct"));
                                    txt_productHeading.setText(Keyword);
//                                  txt_productTotal.setText("Showing "+NoofProduct+" items ");

                                    saveProductsList(jsonObject);
                                } else {
                                    noDataFound();
                                }
                            } else {
                                noDataFound();
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            AppUtils.showExceptionDialog(SearchProducts_Activity.this);
                        }
                    }
                }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            }
        } catch (Exception e) {
            e.printStackTrace();
            AppUtils.showExceptionDialog(SearchProducts_Activity.this);
        }
    }

    private void saveProductsList(final JSONObject jsonObject) {
        try {
            JSONArray jsonArrayProductList = jsonObject.getJSONArray("HeadingMenu");

            if (pageIndex == 1) {
                productList.clear();
            }

            txt_productTotal.setText("Showing " + jsonArrayProductList.length() + " items ");

            for (int i = 0; i < jsonArrayProductList.length(); i++) {
                JSONObject jsonObjectProducts = jsonArrayProductList.getJSONObject(i);
                ProductsList products = new ProductsList();
                products.setID("" + jsonObjectProducts.getString("ProdID"));
                products.setcode("" + jsonObjectProducts.getString("ProductCode"));
                products.setName("" + jsonObjectProducts.getString("ProductName"));
                products.setAvailFor("" + jsonObjectProducts.getString("AvailFor"));
                products.setDiscountPer("" + jsonObjectProducts.getString("DiscountPer"));
                products.setIsDisplayDiscount(jsonObjectProducts.getBoolean("DiscDisp"));
                products.setImagePath("" + getResources().getString(R.string.productImageURL) + jsonObjectProducts.getString("NewImgPath"));
                products.setNewMRP("" + jsonObjectProducts.getString("NewMRP"));
                products.setNewDP("" + jsonObjectProducts.getString("NewDP"));
                products.setBV("" + jsonObjectProducts.getString("BV"));
                productList.add(products);
            }
            if (AppUtils.showLogs) Log.e(TAG, "productList..." + productList);

            if (isLoadMore) {
                if (adapter != null)
                    adapter.notifyDataSetChanged();
            } else {
                if (isDisplayView.equalsIgnoreCase("Grid")) {
                    setGridViewData();
                } else {
                    setListViewData();
                }
            }

            if (AppController.filterList1.size() == 0) {
                createFilterList(jsonObject);
            }
        } catch (Exception e) {
            e.printStackTrace();
            AppUtils.showExceptionDialog(SearchProducts_Activity.this);
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

            adapter = new ProductListGrid_Adapter(SearchProducts_Activity.this, productList, "Grid");
            gridView_products.setAdapter(adapter);

            gridView_products.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                    Intent intent = new Intent(SearchProducts_Activity.this, ProductDetail_Activity.class);
                    if (AppUtils.showLogs)
                        Log.e(TAG, "gridView_products.setOnItemClickListener productID.." + productList.get(position).getID());
                    intent.putExtra("productID", productList.get(position).getID());
                    startActivity(intent);
                    //overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                }
            });

//            gridView_products.setOnScrollListener(new AbsListView.OnScrollListener()
//            {
//
//                @Override
//                public void onScrollStateChanged(AbsListView view, int scrollState)
//                {
//                    if(AppUtils.showLogs) Log.e(TAG, "gridView_products onScrollStateChanged called");
//                    int threshold = 1;
//                    int count = gridView_products.getCount();
//                    if (scrollState == SCROLL_STATE_IDLE)
//                    {
//                        if (gridView_products.getLastVisiblePosition() >= count - threshold)
//                        {
//                            if(NoofProduct!=productList.size())
//                            {
//                                if (AppUtils.isNetworkAvailable(SearchProducts_Activity.this))
//                                {
//                                    if(AppUtils.showLogs) Log.v(TAG, "list onScrollStateChanged executeToGetSearchProductListRequest called");
//
//                                    performLoadMoreDirectSearch();
//                                }
//                                else
//                                {
//                                    AppUtils.alertDialog(SearchProducts_Activity.this, getResources().getString(R.string.txt_networkAlert));
//                                }
//                            }
//                        }
//                    }
//                }
//
//                @Override
//                public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount)
//                {
////                    int lastInScreen = firstVisibleItem + visibleItemCount;
////                    if((lastInScreen == totalItemCount))
////                    {
////                        if (AppUtils.isNetworkAvailable(ProductListGrid_Activity.this))
////                        {
////                            if(AppUtils.showLogs)Log.v(TAG, "Grid Scoll executeToGetProductListRequest called");
////                            isLoadMore=true;
////
////                            numOfRows += 5;
////
////                            if(numOfRows==5)
////                            {
////                                pageIndex=1;
////                            }
////                            else
////                            {
////                                pageIndex= numOfRows/5;
////                            }
////                            executeToGetProductListRequest();
////                        }
////                        else
////                        {
////                            AppUtils.alertDialog(ProductListGrid_Activity.this, getResources().getString(R.string.txt_networkAlert));
////                        }
////                    }
//                }
//            });
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
            adapter = new ProductListGrid_Adapter(SearchProducts_Activity.this, productList, "List");
            list_products.setAdapter(adapter);

            list_products.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {

                    Intent intent = new Intent(SearchProducts_Activity.this, ProductDetail_Activity.class);
                    intent.putExtra("productID", productList.get(position).getID());
                    startActivity(intent);
                    //overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                }
            });

//            list_products.setOnScrollListener(new AbsListView.OnScrollListener()
//            {
//
//                @Override
//                public void onScrollStateChanged(AbsListView view, int scrollState)
//                {
//                    if(AppUtils.showLogs) Log.e(TAG, "list_products onScrollStateChanged called");
//
//                    int threshold = 1;
//                    int count = list_products.getCount();
//                    if (scrollState == SCROLL_STATE_IDLE)
//                    {
//                        if (list_products.getLastVisiblePosition() >= count - threshold)
//                        {
//                            if(NoofProduct!=productList.size())
//                            {
//                                if (AppUtils.isNetworkAvailable(SearchProducts_Activity.this))
//                                {
//                                    if(AppUtils.showLogs) Log.v(TAG, "list onScrollStateChanged executeToGetSearchProductListRequest called");
//
//                                    performLoadMoreDirectSearch();
//                                }
//                                else
//                                {
//                                    AppUtils.alertDialog(SearchProducts_Activity.this, getResources().getString(R.string.txt_networkAlert));
//                                }
//                            }
//                        }
//                    }
//                }
//
//                @Override
//                public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount)
//                {
//                    if(AppUtils.showLogs) Log.v(TAG, "list Scoll called");
//
//
////                    int lastInScreen = firstVisibleItem + visibleItemCount;
////                    if((lastInScreen == totalItemCount))
////                    {
////                        if (AppUtils.isNetworkAvailable(ProductListGrid_Activity.this))
////                        {
////                            if(AppUtils.showLogs)Log.v(TAG, "list Scoll executeToGetProductListRequest called");
////                            isLoadMore=true;
////
////                            numOfRows += 5;
////                            if(numOfRows==5)
////                            {
////                                pageIndex=1;
////                            }
////                            else
////                            {
////                                pageIndex= numOfRows/5;
////                            }
////                            executeToGetProductListRequest();
////                        }
////                        else
////                        {
////                            AppUtils.alertDialog(ProductListGrid_Activity.this, getResources().getString(R.string.txt_networkAlert));
////                        }
////                    }
//                }
//            });
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

    private void createFilterList(final JSONObject jsonObject) {
        try {
            //Array list of Filter Options
            AppController.filterList1.clear();
            HashMap<String, String> map1 = new HashMap<>();
            map1.put("name", "PRICE");
            AppController.filterList1.add(map1);
            HashMap<String, String> map2 = new HashMap<>();
            map2.put("name", "DISCOUNT");
            AppController.filterList1.add(map2);

            int MinPrice, MaxPrice, MinDiscount, Maxdiscount, DiffPrice, PriceRangeGap, PriceStartRange, DiscStartRange;
            JSONArray jsonArrayPriceAndDisount = jsonObject.getJSONArray("CategroyMenu");

            MinPrice = Integer.parseInt(jsonArrayPriceAndDisount.getJSONObject(0).getString("MinPrice"));
            MaxPrice = Integer.parseInt(jsonArrayPriceAndDisount.getJSONObject(0).getString("MaxPrice"));
            MinDiscount = Integer.parseInt(jsonArrayPriceAndDisount.getJSONObject(0).getString("MinDiscount"));
            Maxdiscount = Integer.parseInt(jsonArrayPriceAndDisount.getJSONObject(0).getString("Maxdiscount"));

            DiffPrice = (MaxPrice - MinPrice);

            if (DiffPrice >= 20000) {
                PriceRangeGap = 2000;
            } else if (DiffPrice >= 5000) {
                PriceRangeGap = 1000;
            } else {
                PriceRangeGap = 500;
            }

            PriceStartRange = (MinPrice - (MinPrice % PriceRangeGap));


            String Usertype = (AppController.getSpUserInfo().getString(SPUtils.USER_TYPE, ""));

            AppController.priceFilterList.clear();
            for (int i = 0; i < 8; i++) {
                String text, id;

                if (PriceStartRange <= MaxPrice) {
                    if (i < 7) {
                        text = "₹ " + PriceStartRange + " - ₹ " + (PriceStartRange + PriceRangeGap - 1);

                        if (Usertype.equalsIgnoreCase("DISTRIBUTOR"))
                            id = "(Cast(a.DP as numeric(18,0))>=" + PriceStartRange + " AND Cast(a.DP as numeric(18,0))<=" + (PriceStartRange + PriceRangeGap - 1) + ")";
                        else
                            id = "(Cast(a.DP2 as numeric(18,0))>=" + PriceStartRange + " AND Cast(a.DP2 as numeric(18,0))<=" + (PriceStartRange + PriceRangeGap - 1) + ")";

                    } else {
                        text = "₹ " + PriceStartRange + " and Above";

                        if (Usertype.equalsIgnoreCase("DISTRIBUTOR"))
                            id = "(Cast(a.DP as numeric(18,0))>=" + PriceStartRange + ")";
                        else
                            id = "(Cast(a.DP2 as numeric(18,0))>=" + PriceStartRange + ")";
                    }

                    PriceStartRange = PriceStartRange + PriceRangeGap;

                    FilterList2CheckBox priceFilter = new FilterList2CheckBox(text, id, false);
                    AppController.priceFilterList.add(priceFilter);
                }
            }

            DiscStartRange = (MinDiscount - (MinDiscount % 15));
            AppController.discountFilterList.clear();
            for (int i = 0; i < 8; i++) {
                String text, id;

                if (DiscStartRange <= Maxdiscount) {

                    if (i == 0) {

                        if (Usertype.equalsIgnoreCase("DISTRIBUTOR"))
                            id = "(Cast((((MRP-DP)/MRP)*100) as numeric(18,0))>=" + DiscStartRange + " AND Cast((((MRP-DP)/MRP)*100) as numeric(18,0))<=" + (DiscStartRange + 15) + ")";
                        else
                            id = "(Cast((((MRP-DP2)/MRP)*100) as numeric(18,0))>=" + DiscStartRange + " AND Cast((((MRP-DP2)/MRP)*100) as numeric(18,0))<=" + (DiscStartRange + 15) + ")";

                        text = " Below " + (DiscStartRange + 15) + "% Discount";


                    } else if ((i > 0 && i < 6) && (DiscStartRange + 15 <= 100)) {
                        if (Usertype.equalsIgnoreCase("DISTRIBUTOR"))
                            id = "(Cast((((MRP-DP)/MRP)*100) as numeric(18,0))>=" + DiscStartRange + " AND Cast((((MRP-DP)/MRP)*100) as numeric(18,0))<=" + (DiscStartRange + 15) + ")";
                        else
                            id = "(Cast((((MRP-DP2)/MRP)*100) as numeric(18,0))>=" + DiscStartRange + " AND Cast((((MRP-DP2)/MRP)*100) as numeric(18,0))<=" + (DiscStartRange + 15) + ")";

                        text = "" + DiscStartRange + "% - " + (DiscStartRange + 15) + "%";
                    } else {

                        if (Usertype.equalsIgnoreCase("DISTRIBUTOR"))
                            id = "(Cast((((MRP-DP)/MRP)*100) as numeric(18,0))>=" + DiscStartRange + ")";
                        else
                            id = "(Cast((((MRP-DP2)/MRP)*100) as numeric(18,0))>=" + DiscStartRange + ")";

                        text = "" + DiscStartRange + "% and Above";

                        FilterList2CheckBox discountFilter = new FilterList2CheckBox(text, id, false);
                        AppController.discountFilterList.add(discountFilter);
                        break;
                    }

                    DiscStartRange = DiscStartRange + 15;

                    FilterList2CheckBox discountFilter = new FilterList2CheckBox(text, id, false);
                    AppController.discountFilterList.add(discountFilter);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setOptionMenu() {
        try {
            if (AppController.getSpIsLogin().getBoolean(SPUtils.IS_LOGIN, false)) {
                img_user.setImageDrawable(getResources().getDrawable(R.drawable.icon_logout_orange));
            } else {
                img_user.setImageDrawable(getResources().getDrawable(R.drawable.icon_distributor_login_orange));
            }
            setBadgeCount(SearchProducts_Activity.this, (AppController.selectedProductsList.size()));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Override
    protected void onRestart() {
        super.onRestart();
        try {
            if (AppController.comesFromFilter) {
                AppController.comesFromFilter = false;
                isLoadMore = false;
                pageIndex = 1;
                performDirectSearch(Keyword);
            }

            setOptionMenu();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        try {
            setOptionMenu();
            isLoadMore = false;
            pageIndex = 1;
            performDirectSearch(Keyword);

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
            //overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        } catch (Exception e) {
            e.printStackTrace();
            AppUtils.showExceptionDialog(SearchProducts_Activity.this);
        }
    }
}
