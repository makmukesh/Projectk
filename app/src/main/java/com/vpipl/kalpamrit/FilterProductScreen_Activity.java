package com.vpipl.kalpamrit;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.vpipl.kalpamrit.R;
import com.vpipl.kalpamrit.Utils.AppUtils;
import com.vpipl.kalpamrit.Utils.SPUtils;
import com.vpipl.kalpamrit.model.FilterList2CheckBox;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by admin on 22-05-2017.
 */
public class FilterProductScreen_Activity extends AppCompatActivity {
    String TAG = "FilterProductScreen_Activity";
    private ListView list1ViewHeading;
    private ListView list2ViewHeadingItem;
    private PriceFilterListAdapter adapterListPrice;
    private DiscountFilterListAdapter adapterListDiscount;
    private FilterList1Adapter adapterList1;

    private String COMESFROM = "";

    private Button btn_clearFilter;
    private Button btn_apply;

    private int mSelectedItem;

    ImageView img_nav_back, img_login_logout;

    public void SetupToolbar() {

        img_nav_back = findViewById(R.id.img_nav_back);
        img_login_logout = findViewById(R.id.img_login_logout);
        img_nav_back.setImageDrawable(getResources().getDrawable(R.drawable.icon_nav_bar));

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
                    startActivity(new Intent(FilterProductScreen_Activity.this, Login_Activity.class));
                else
                    AppUtils.showDialogSignOut(FilterProductScreen_Activity.this);
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
        setContentView(R.layout.filterproductscreen_activity);

        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
        SetupToolbar();
        try {

            list1ViewHeading = findViewById(R.id.list1);
            list2ViewHeadingItem = findViewById(R.id.list2);
            btn_clearFilter = findViewById(R.id.btn_clearFilter);
            btn_apply = findViewById(R.id.btn_apply);

            adapterList1 = new FilterList1Adapter(FilterProductScreen_Activity.this, AppController.filterList1);
            list1ViewHeading.setAdapter(adapterList1);
            list1ViewHeading.setItemChecked(0, true);
            list1ViewHeading.setSelection(0);
            setPriceFilterAdapter();


            list1ViewHeading.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    mSelectedItem = i;
                    if (i == 0) {
                        setPriceFilterAdapter();
                    } else {
                        setDiscountFilterAdapter();
                    }

                    list1ViewHeading.setItemChecked(i, true);
                    list1ViewHeading.setSelection(i);
                }
            });

            COMESFROM = getIntent().getStringExtra("COMESFROM");

            btn_clearFilter.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    for (int i = 0; i < AppController.priceFilterList.size(); i++) {
                        AppController.priceFilterList.set(i, new FilterList2CheckBox(AppController.priceFilterList.get(i).getName(), AppController.priceFilterList.get(i).getId(), false));
                    }

                    for (int i = 0; i < AppController.discountFilterList.size(); i++) {
                        AppController.discountFilterList.set(i, new FilterList2CheckBox(AppController.discountFilterList.get(i).getName(), AppController.discountFilterList.get(i).getId(), false));
                    }

                    AppController.FiltersCondition = "";

                    String PriceCondition = "";

                    for (int i = 0; i < AppController.priceFilterList.size(); i++) {
                        if (AppController.priceFilterList.get(i).isSelected()) {
                            if (PriceCondition.equals("")) {
                                PriceCondition = PriceCondition + " (" + AppController.priceFilterList.get(i).getId() + ") ";
                            } else {
                                PriceCondition = PriceCondition + " OR (" + AppController.priceFilterList.get(i).getId() + ") ";
                            }
                        }
                    }

                    String DiscountCond = "";
                    for (int i = 0; i < AppController.discountFilterList.size(); i++) {
                        if (AppController.discountFilterList.get(i).isSelected()) {
                            if (DiscountCond.equals("")) {
                                DiscountCond = DiscountCond + " (" + AppController.discountFilterList.get(i).getId() + ") ";
                            } else {
                                DiscountCond = DiscountCond + " OR (" + AppController.discountFilterList.get(i).getId() + ") ";
                            }
                        }
                    }


                    if (!PriceCondition.equals("") && !DiscountCond.equals("")) {
                        AppController.FiltersCondition = "' AND (" + PriceCondition + ") AND (" + DiscountCond + ")'";
                    } else if (!PriceCondition.equals("") && DiscountCond.equals("")) {
                        AppController.FiltersCondition = "' AND (" + PriceCondition + ")'";
                    } else if (PriceCondition.equals("") && !DiscountCond.equals("")) {
                        AppController.FiltersCondition = "' AND (" + DiscountCond + ")'";
                    } else {
                        AppController.FiltersCondition = "";
                    }
                    AppController.comesFromFilter = true;
                    finish();
                    //overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                }
            });

            btn_apply.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    AppController.FiltersCondition = "";

                    String PriceCondition = "";

                    for (int i = 0; i < AppController.priceFilterList.size(); i++) {
                        if (AppController.priceFilterList.get(i).isSelected()) {
                            if (PriceCondition.equals("")) {
                                PriceCondition = PriceCondition + " (" + AppController.priceFilterList.get(i).getId() + ") ";
                            } else {
                                PriceCondition = PriceCondition + " OR (" + AppController.priceFilterList.get(i).getId() + ") ";
                            }
                        }
                    }

                    String DiscountCond = "";
                    for (int i = 0; i < AppController.discountFilterList.size(); i++) {
                        if (AppController.discountFilterList.get(i).isSelected()) {
                            if (DiscountCond.equals("")) {
                                DiscountCond = DiscountCond + " (" + AppController.discountFilterList.get(i).getId() + ") ";
                            } else {
                                DiscountCond = DiscountCond + " OR (" + AppController.discountFilterList.get(i).getId() + ") ";
                            }
                        }
                    }

                    if (COMESFROM.equalsIgnoreCase(SearchProducts_Activity.class.getSimpleName())) {
                        if (!PriceCondition.equals("") && !DiscountCond.equals("")) {
                            AppController.FiltersCondition = " AND (" + PriceCondition + ") AND (" + DiscountCond + ")";
                        } else if (!PriceCondition.equals("") && DiscountCond.equals("")) {
                            AppController.FiltersCondition = " AND (" + PriceCondition + ")";
                        } else if (PriceCondition.equals("") && !DiscountCond.equals("")) {
                            AppController.FiltersCondition = " AND (" + DiscountCond + ")";
                        } else {
                            AppController.FiltersCondition = "";
                        }
                    } else {
                        if (!PriceCondition.equals("") && !DiscountCond.equals("")) {
                            AppController.FiltersCondition = "' AND (" + PriceCondition + ") AND (" + DiscountCond + ")'";
                        } else if (!PriceCondition.equals("") && DiscountCond.equals("")) {
                            AppController.FiltersCondition = "' AND (" + PriceCondition + ")'";
                        } else if (PriceCondition.equals("") && !DiscountCond.equals("")) {
                            AppController.FiltersCondition = "' AND (" + DiscountCond + ")'";
                        } else {
                            AppController.FiltersCondition = "";
                        }
                    }

                    AppController.comesFromFilter = true;
                    finish();
                    //overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            AppUtils.showExceptionDialog(FilterProductScreen_Activity.this);
        }
    }

    private void setPriceFilterAdapter() {
        try {
            adapterListPrice = new PriceFilterListAdapter(FilterProductScreen_Activity.this);
            list2ViewHeadingItem.setAdapter(adapterListPrice);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setDiscountFilterAdapter() {
        try {
            adapterListDiscount = new DiscountFilterListAdapter(FilterProductScreen_Activity.this);
            list2ViewHeadingItem.setAdapter(adapterListDiscount);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();

        try {
            AppUtils.dismissProgressDialog();
            //overridePendingTransition(R.anim.fade_in, R.anim.fade_out);

            AppController.comesFromFilter = true;
        } catch (Exception e) {
            e.printStackTrace();
            AppUtils.showExceptionDialog(FilterProductScreen_Activity.this);
        }
    }

    private class FilterList1Adapter extends BaseAdapter {
        String TAG = "FilterList1Adapter";
        Context context;
        LayoutInflater inflater = null;
        ArrayList<HashMap<String, String>> filterList1 = new ArrayList<>();

        public FilterList1Adapter(Context context, ArrayList<HashMap<String, String>> list) {
            this.context = context;
            inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            this.filterList1 = list;
        }

        @Override
        public int getCount() {
            return filterList1.size();
        }

        @Override
        public Object getItem(int position) {
            return filterList1.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            try {
                final Holder holder;
                if (convertView == null) {
                    convertView = inflater.inflate(R.layout.filter_list1_item, parent, false);
                    holder = new Holder();

                    holder.txt_title = convertView.findViewById(R.id.txt_title);

                    convertView.setTag(holder);
                } else {
                    holder = (Holder) convertView.getTag();
                }

                holder.txt_title.setText("" + filterList1.get(position).get("name"));
                holder.txt_title.setBackgroundColor(getResources().getColor(R.color.color_eeeeee));
                if (position == mSelectedItem) {
                    holder.txt_title.setBackgroundColor(Color.WHITE);
                }
            } catch (Exception e) {
                e.printStackTrace();
                AppUtils.showExceptionDialog(context);
            }

            return convertView;
        }

        private class Holder {
            TextView txt_title;
        }
    }

    private class PriceFilterListAdapter extends BaseAdapter {
        String TAG = "PriceFilterListAdapter";
        Context context;
        LayoutInflater inflater = null;

        public PriceFilterListAdapter(Context context) {
            this.context = context;
            inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public int getCount() {
            return AppController.priceFilterList.size();
        }

        @Override
        public Object getItem(int position) {
            return AppController.priceFilterList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            try {
                final Holder holder;
                if (convertView == null) {
                    convertView = inflater.inflate(R.layout.filter_list2_item, parent, false);
                    holder = new Holder();

                    holder.checkbox = convertView.findViewById(R.id.checkbox);
                    holder.checkbox.setOnClickListener(new View.OnClickListener() {
                        public void onClick(View v) {
                            CheckBox cb = (CheckBox) v;
                            FilterList2CheckBox list2CheckBox = (FilterList2CheckBox) cb.getTag();
                            //Toast.makeText(getApplicationContext(),"Clicked on Checkbox: " + cb.getText() +" is " + cb.isChecked(), Toast.LENGTH_LONG).show();
                            list2CheckBox.setSelected(cb.isChecked());
                            notifyDataSetChanged();
                        }
                    });
                    convertView.setTag(holder);
                } else {
                    holder = (Holder) convertView.getTag();
                }

                holder.checkbox.setText(AppController.priceFilterList.get(position).getName());
                holder.checkbox.setChecked(AppController.priceFilterList.get(position).isSelected());
                holder.checkbox.setTag(AppController.priceFilterList.get(position));
                notifyDataSetChanged();
            } catch (Exception e) {
                e.printStackTrace();
                AppUtils.showExceptionDialog(context);
            }

            return convertView;
        }

        private class Holder {
            CheckBox checkbox;
        }
    }

    private class DiscountFilterListAdapter extends BaseAdapter {
        String TAG = "DiscountFilterListAdapter";
        Context context;
        LayoutInflater inflater = null;

        public DiscountFilterListAdapter(Context context) {
            this.context = context;
            inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public int getCount() {
            return AppController.discountFilterList.size();
        }

        @Override
        public Object getItem(int position) {
            return AppController.discountFilterList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            try {
                final Holder holder;
                if (convertView == null) {
                    convertView = inflater.inflate(R.layout.filter_list2_item, parent, false);
                    holder = new Holder();

                    holder.checkbox = convertView.findViewById(R.id.checkbox);
                    holder.checkbox.setOnClickListener(new View.OnClickListener() {
                        public void onClick(View v) {
                            CheckBox cb = (CheckBox) v;
                            FilterList2CheckBox list2CheckBox = (FilterList2CheckBox) cb.getTag();
                            //Toast.makeText(getApplicationContext(),"Clicked on Checkbox: " + cb.getText() +" is " + cb.isChecked(), Toast.LENGTH_LONG).show();
                            list2CheckBox.setSelected(cb.isChecked());
                            notifyDataSetChanged();
                        }
                    });
                    convertView.setTag(holder);
                } else {
                    holder = (Holder) convertView.getTag();
                }

                holder.checkbox.setText(AppController.discountFilterList.get(position).getName());
                holder.checkbox.setChecked(AppController.discountFilterList.get(position).isSelected());
                holder.checkbox.setTag(AppController.discountFilterList.get(position));
                notifyDataSetChanged();
            } catch (Exception e) {
                e.printStackTrace();
                AppUtils.showExceptionDialog(context);
            }

            return convertView;
        }

        private class Holder {
            CheckBox checkbox;
        }
    }
}
