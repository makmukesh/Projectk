package com.vpipl.kalpamrit;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.util.TypedValue;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.vpipl.kalpamrit.Adapters.ExpandList_Parent_Adapter;
import com.vpipl.kalpamrit.Adapters.ImageSliderViewPagerAdapter;
import com.vpipl.kalpamrit.Utils.AnimatedExpandableListView;
import com.vpipl.kalpamrit.Utils.AppUtils;
import com.vpipl.kalpamrit.Utils.BadgeDrawable;
import com.vpipl.kalpamrit.Utils.CirclePageIndicator;
import com.vpipl.kalpamrit.Utils.SPUtils;
import com.vpipl.kalpamrit.model.ExpandList;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by PC14 on 08-Apr-16.
 */
public class ProductExpand_Activity extends AppCompatActivity {
    private static String TAG = "ProductExpand_Activity";
    private static ViewGroup FooterView = null;
    private static ViewGroup HeaderView = null;

    LinearLayout LLOTherCategories;
    private CirclePageIndicator imagePageIndicator;
    private ViewPager image_viewPager;
    private List<ExpandList> expandListing = new ArrayList<>();
    private AnimatedExpandableListView expandSubCatListView;
    private TextView txt_Heading;
    private EditText et_search;
    private int currentPage = 0;
    private Timer timer;


    String Heading_CID = "0", Heading_type = "";


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
                startActivity(new Intent(ProductExpand_Activity.this, AddCartCheckOut_Activity.class));

            }
        });

        img_user.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!AppController.getSpIsLogin().getBoolean(SPUtils.IS_LOGIN, false))
                    startActivity(new Intent(ProductExpand_Activity.this, Login_Activity.class));
                else
                    AppUtils.showDialogSignOut(ProductExpand_Activity.this);
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
        setContentView(R.layout.productexpand_activity);

        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
        SetupToolbar();

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        try {

            et_search = findViewById(R.id.et_search);

            et_search.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                @Override
                public boolean onEditorAction(TextView view, int actionId, KeyEvent event) {
                    if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                        AppUtils.hideKeyboardOnClick(ProductExpand_Activity.this, view);
                        performSearch();
                        return true;
                    }
                    return false;
                }
            });

            expandSubCatListView = findViewById(R.id.expandSubCatListView);

            setExpandListData();
        } catch (Exception e) {
            e.printStackTrace();
            AppUtils.showExceptionDialog(ProductExpand_Activity.this);
        }
    }

    private void performSearch() {
        if (et_search.getText().toString().isEmpty()) {
            AppUtils.alertDialog(ProductExpand_Activity.this, "Please Enter search keyword.");
            et_search.requestFocus();
        } else {
            startActivity(new Intent(this, SearchProducts_Activity.class).putExtra("Keyword", et_search.getText().toString()));
        }

    }

    private void setExpandListData() {
        try {
            expandListing.clear();

            LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            FooterView = (ViewGroup) inflater.inflate(R.layout.productexpandlist_footer, expandSubCatListView, false);
            HeaderView = (ViewGroup) inflater.inflate(R.layout.productexpand_header, expandSubCatListView, false);
            expandSubCatListView.addHeaderView(HeaderView, null, false);
            expandSubCatListView.addFooterView(FooterView, null, false);

            setImageSlider();

            txt_Heading = HeaderView.findViewById(R.id.txt_Heading);


            if (getIntent().getExtras().getString("CID").equals("")) {
                for (int i = 0; i < AppController.category1.size(); i++) {
                    if (getIntent().getExtras().getString("HID").equals(AppController.category1.get(i).get("HID"))) {
                        txt_Heading.setText("In " + AppController.category1.get(i).get("Heading"));

                        Heading_CID = AppController.category1.get(i).get("HID");
                        Heading_type = AppController.category1.get(i).get("Type");

                        for (int j = 0; j < AppController.category2.size(); j++) {
                            if (AppController.category1.get(i).get("HID").equals(AppController.category2.get(j).get("HID"))) {
                                List<ExpandList> cat2 = new ArrayList<>();

                                for (int k = 0; k < AppController.category3.size(); k++) {
                                    List<ExpandList> cat3 = new ArrayList<>();

                                    if (AppController.category2.get(j).get("CID").equals(AppController.category3.get(k).get("CID"))) {
                                        for (int l = 0; l < AppController.category4.size(); l++) {
                                            if (AppController.category3.get(k).get("SCID").equals(AppController.category4.get(l).get("SCID"))) {
                                                cat3.add(new ExpandList(AppController.category4.get(l).get("SubCat"), AppController.category4.get(l).get("SCID2"), AppController.category4.get(l).get("Type"), new ArrayList<ExpandList>()));
                                            }
                                        }
                                        cat2.add(new ExpandList(AppController.category3.get(k).get("SubCategory"), AppController.category3.get(k).get("SCID"), AppController.category3.get(k).get("Type"), cat3));
                                    }
                                }
                                expandListing.add(new ExpandList(AppController.category2.get(j).get("Category"), AppController.category2.get(j).get("CID"), AppController.category2.get(j).get("Type"), cat2));
                            }
                        }
                    }
                }
            } else {
                for (int i = 0; i < AppController.category2.size(); i++) {
                    if (getIntent().getExtras().getString("CID").equals(AppController.category2.get(i).get("CID"))) {
                        for (int a = 0; a < AppController.category1.size(); a++) {
                            if (getIntent().getExtras().getString("HID").equals(AppController.category1.get(a).get("HID"))) {
//                              txt_Heading.setText("In "+AppController.category1.get(a).get("Heading")+" > "+AppController.category2.get(i).get("Category"));
                                txt_Heading.setText("In " + AppController.category2.get(i).get("Category"));

                                Heading_CID = AppController.category2.get(i).get("CID");
                                Heading_type = AppController.category2.get(i).get("Type");
                            }
                        }

                        for (int j = 0; j < AppController.category3.size(); j++) {
                            if (getIntent().getExtras().getString("CID").equals(AppController.category3.get(j).get("CID"))) {
                                List<ExpandList> cat1 = new ArrayList<>();
                                for (int k = 0; k < AppController.category4.size(); k++) {
                                    if (AppController.category3.get(j).get("SCID").equals(AppController.category4.get(k).get("SCID"))) {
                                        cat1.add(new ExpandList(AppController.category4.get(k).get("SubCat"), AppController.category4.get(k).get("SCID2"), AppController.category4.get(k).get("Type"), new ArrayList<ExpandList>()));
                                    }
                                }
                                expandListing.add(new ExpandList(AppController.category3.get(j).get("SubCategory"), AppController.category3.get(j).get("SCID"), AppController.category3.get(j).get("Type"), cat1));
                            }
                        }
                    }
                }
            }

            txt_Heading.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!Heading_CID.equalsIgnoreCase("0") && !TextUtils.isEmpty(Heading_type)) {
                        Intent intent = new Intent(ProductExpand_Activity.this, ProductListGrid_Activity.class);
                        intent.putExtra("Type", "" + Heading_type);
                        intent.putExtra("categoryID", "" + Heading_CID);
                        startActivity(intent);
                    }
                }
            });

            expandSubCatListView.setAdapter(new ExpandList_Parent_Adapter(ProductExpand_Activity.this, expandListing));
            expandSubCatListView.setGroupIndicator(null);
            expandSubCatListView.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {
                @Override
                public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id) {
                    if (expandSubCatListView.isGroupExpanded(groupPosition)) {
                        expandSubCatListView.collapseGroupWithAnimation(groupPosition);
                    } else {
                        expandSubCatListView.expandGroupWithAnimation(groupPosition);
                    }

                    if (expandListing.get(groupPosition).getExpandList() != null && expandListing.get(groupPosition).getExpandList().size() == 0) {
                        try {
                            for (int j = 0; j < AppController.category1.size(); j++) {
                                if (getIntent().getExtras().getString("HID").equals(AppController.category1.get(j).get("HID"))) {

                                    if (AppUtils.showLogs) Log.e(TAG, "in if called....");
                                    Intent intent = new Intent(ProductExpand_Activity.this, ProductListGrid_Activity.class);
                                    intent.putExtra("Type", "" + expandListing.get(groupPosition).getType());
                                    intent.putExtra("categoryID", "" + expandListing.get(groupPosition).getId());
                                    startActivity(intent);
                                }
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    return true;
                }
            });


            TableLayout ll = FooterView.findViewById(R.id.displayLinear);

            float sp = 5;
            int px = (int) (sp * getResources().getDisplayMetrics().scaledDensity);
            int px_right = (int) (10 * getResources().getDisplayMetrics().scaledDensity);

            Typeface typeface = ResourcesCompat.getFont(this, R.font.gisha_0);

            for (int aaa = 0; aaa < AppController.category2.size(); aaa++) {
                String category_name = AppController.category2.get(aaa).get("Category");
                String CID = AppController.category2.get(aaa).get("CID");

                final TableRow row1 = new TableRow(this);

                TableRow.LayoutParams lp1 = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT);
                row1.setLayoutParams(lp1);
                row1.setBackgroundColor(Color.WHITE);

                row1.setId(Integer.parseInt(CID));

                TextView A1 = new TextView(this);
                TextView B1 = new TextView(this);

                A1.setTypeface(typeface);
                B1.setTypeface(typeface);

                A1.setText("-");
                B1.setText(category_name);

                A1.setPadding(px, px_right, px_right, px_right);

                A1.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
                B1.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);

//                A1.setGravity(Gravity.CENTER);
//                B1.setGravity(Gravity.CENTER);

                A1.setTextColor(getResources().getColor(android.R.color.darker_gray));
                B1.setTextColor(getResources().getColor(R.color.app_color_grayicon));

                row1.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(ProductExpand_Activity.this, ProductExpand_Activity.class);
                        intent.putExtra("HID", "1");
                        intent.putExtra("CID", "" + row1.getId());
                        startActivity(intent);
                        finish();
                    }
                });

                row1.addView(A1);
                row1.addView(B1);

                View view = new View(this);
                view.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 1));
                view.setBackgroundColor(getResources().getColor(R.color.color_eeeeee));

                ll.addView(row1);
                ll.addView(view);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setImageSlider() {
        try {
            image_viewPager = HeaderView.findViewById(R.id.pager_slider);
            imagePageIndicator = HeaderView.findViewById(R.id.imagePageIndicator);

            image_viewPager.setAdapter(new ImageSliderViewPagerAdapter(ProductExpand_Activity.this));

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
                    if (currentPage == Home_Activity.imageSlider.size()) {
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

            setBadgeCount(ProductExpand_Activity.this, (AppController.selectedProductsList.size()));

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
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onRestart() {
        super.onRestart();

        try {
            setOptionMenu();
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
            AppUtils.showExceptionDialog(ProductExpand_Activity.this);
        }
    }
}
