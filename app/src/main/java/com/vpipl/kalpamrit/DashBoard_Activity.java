package com.vpipl.kalpamrit;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.vpipl.kalpamrit.Adapters.ExpandableListAdapter;
import com.vpipl.kalpamrit.Utils.AppUtils;
import com.vpipl.kalpamrit.Utils.CircularImageView;
import com.vpipl.kalpamrit.Utils.QueryUtils;
import com.vpipl.kalpamrit.Utils.SPUtils;

import org.apache.commons.lang3.text.WordUtils;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DashBoard_Activity extends AppCompatActivity {

    TextView txt_joining_Date, txt_user_id, txt_Status, txt_ActivationDate, txt_ActivationPoint,txt_Proposer_ID, txt_LastProfileUpdate;

    TextView txt_total_joinings, txt_total_active_joinings, txt_cm_bv, txt_uptodate_bv, txt_cum_self_pur_bv, txt_total_self_pur_bv;
    TextView txt_uptodatejoin_self, txt_uptodatejoin_downline;

    private static final String TAG = "DashBoard_Activity";

    private static DrawerLayout drawer;
    private static NavigationView navigationView;

    private TextView txt_welcome_name;
    private TextView txt_id_number;
    private TextView txt_available_wb;
    private ArrayList<String> listDataHeader;
    private HashMap<String, List<String>> listDataChild;
    private int lastExpandedPosition = -1;
    private ExpandableListView expListView;
    private CircularImageView profileImage;
    LinearLayout ll_bottom_rep;
    private JSONArray HeadingJarray;
    String refer_url = "";

    ImageView img_nav_back, img_login_logout;

    public void SetupToolbar() {

        img_nav_back = findViewById(R.id.img_nav_back);
        img_login_logout = findViewById(R.id.img_login_logout);

        img_nav_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (drawer.isDrawerOpen(navigationView)) {
                    img_nav_back.setImageDrawable(getResources().getDrawable(R.drawable.icon_nav_bar));
                    drawer.closeDrawer(navigationView);
                } else {
                    img_nav_back.setImageDrawable(getResources().getDrawable(R.drawable.icon_nav_bar_close));
                    drawer.openDrawer(navigationView);
                }
            }
        });

        img_login_logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!AppController.getSpIsLogin().getBoolean(SPUtils.IS_LOGIN, false))
                    startActivity(new Intent(DashBoard_Activity.this, Login_Activity.class));
                else
                    AppUtils.showDialogSignOut(DashBoard_Activity.this);
            }
        });

        if (AppController.getSpIsLogin().getBoolean(SPUtils.IS_LOGIN, false))
            img_login_logout.setImageDrawable(getResources().getDrawable(R.drawable.icon_logout_orange));
        else
            img_login_logout.setImageDrawable(getResources().getDrawable(R.drawable.icon_distributor_login_orange));
    }

    TextView txt_product_bonus, txt_rank_name, txt_refer_url;
    ImageView new_icon;
    FloatingActionButton fab;
    AdView mAdView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);
        try {
            AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);

            Toolbar toolbar = findViewById(R.id.toolbar);
            setSupportActionBar(toolbar);
            getSupportActionBar().setTitle("");
            SetupToolbar();

            if (AppController.getSpIsLogin().getBoolean(SPUtils.IS_LOGIN, false)) {

                txt_joining_Date = findViewById(R.id.txt_joining_Date);
                txt_user_id = findViewById(R.id.txt_user_id);

                txt_Status = findViewById(R.id.txt_Status);
                txt_ActivationDate = findViewById(R.id.txt_ActivationDate);
                txt_ActivationPoint = findViewById(R.id.txt_ActivationPoint);
                txt_Proposer_ID = findViewById(R.id.txt_Proposer_ID);
                txt_LastProfileUpdate = findViewById(R.id.txt_LastProfileUpdate);
                ll_bottom_rep = findViewById(R.id.ll_bottom_rep);

//                txt_Total_Direct_Incentive = (TextView) findViewById(R.id.txt_Total_Direct_Incentive);
                txt_total_joinings = findViewById(R.id.txt_total_joinings);
                txt_total_active_joinings = findViewById(R.id.txt_total_active_joinings);
                txt_cm_bv = findViewById(R.id.txt_cm_bv);
                txt_uptodate_bv = findViewById(R.id.txt_uptodate_bv);

                txt_cum_self_pur_bv = findViewById(R.id.txt_cum_self_pur_bv);
                txt_total_self_pur_bv = findViewById(R.id.txt_total_self_pur_bv);
                txt_product_bonus = findViewById(R.id.txt_product_bonus);
                txt_rank_name = findViewById(R.id.txt_rank_name);
                txt_uptodatejoin_self = findViewById(R.id.txt_uptodatejoin_self);
                txt_uptodatejoin_downline = findViewById(R.id.txt_uptodatejoin_downline);
                txt_refer_url = findViewById(R.id.txt_refer_url);

                txt_joining_Date.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                            startActivity(new Intent(DashBoard_Activity.this , Change_Proposer_Activity.class));
                    }
                });
                txt_user_id.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                            startActivity(new Intent(DashBoard_Activity.this , ProposerGenealogyReport_Activity.class));
                    }
                });

                drawer = findViewById(R.id.drawer_layout);
                navigationView = findViewById(R.id.nav_view);

                View navHeaderView = navigationView.getHeaderView(0);
                txt_welcome_name = navHeaderView.findViewById(R.id.txt_welcome_name);
                txt_available_wb = navHeaderView.findViewById(R.id.txt_available_wb);
                txt_id_number = navHeaderView.findViewById(R.id.txt_id_number);
                profileImage = navHeaderView.findViewById(R.id.iv_Profile_Pic);
                LinearLayout LL_Nav = navHeaderView.findViewById(R.id.LL_Nav);
                fab = findViewById(R.id.fab);

                expListView = findViewById(R.id.left_drawer);

                listDataHeader = new ArrayList<>();
                listDataChild = new HashMap<>();

                HeadingJarray = Splash_Activity.HeadingJarray;


                LL_Nav.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String Usertype = (AppController.getSpUserInfo().getString(SPUtils.USER_TYPE, ""));
                        if (Usertype.equalsIgnoreCase("DISTRIBUTOR")) {
                            if (AppController.getSpIsLogin().getBoolean(SPUtils.IS_LOGIN, false)) {
                                startActivity(new Intent(DashBoard_Activity.this, Profile_View_Activity.class));
                            } else {
                                startActivity(new Intent(DashBoard_Activity.this, Login_Activity.class));
                            }

                            if (drawer.isDrawerOpen(GravityCompat.START)) {
                                drawer.closeDrawer(GravityCompat.START);
                            }
                        }
                    }
                });

                continueapp();

                drawer.addDrawerListener(new DrawerLayout.DrawerListener() {
                    @Override
                    public void onDrawerSlide(@NonNull View drawerView, float slideOffset) {

                    }

                    @Override
                    public void onDrawerOpened(@NonNull View drawerView) {
                        img_nav_back.setImageDrawable(getResources().getDrawable(R.drawable.icon_nav_bar_close));
                    }

                    @Override
                    public void onDrawerClosed(@NonNull View drawerView) {
                        img_nav_back.setImageDrawable(getResources().getDrawable(R.drawable.icon_nav_bar));
                    }

                    @Override
                    public void onDrawerStateChanged(int newState) {

                    }
                });

            } else {
                startActivity(new Intent(DashBoard_Activity.this, Login_Activity.class).putExtra("SendToHome", true));
            }
            txt_refer_url.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ShareApp();
                }
            });
            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ShareApp();
                }
            });

            /*Admob ads added by mukesh 04-12-2019 11:00 AM*/

            MobileAds.initialize(this, getString(R.string.ads_app_id));
            mAdView = (AdView) findViewById(R.id.adView);

            AdRequest adRequest = new AdRequest.Builder()
                    .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
                    // Check the LogCat to get your test device ID
                    // .addTestDevice("B3EEABB8EE11C2BE770B684D95219ECB")
                    .build();

            mAdView.setAdListener(new AdListener() {
                @Override
                public void onAdLoaded() {
                }

                @Override
                public void onAdClosed() {

                    Log.e("log1", "Ad is closed!");
                }

                @Override
                public void onAdFailedToLoad(int errorCode) {

                    Log.e("log2", "Ad failed to load! error code: " + errorCode);
                    //  Toast.makeText(getApplicationContext(), "Ad failed to load! error code: "+errorCode, Toast.LENGTH_LONG).show();
                }

                @Override
                public void onAdLeftApplication() {
                    Log.e("log2", "Ad left application!");

                }

                @Override
                public void onAdOpened() {
                    super.onAdOpened();
                }
            });

            mAdView.loadAd(adRequest);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void ShareApp() {
        try {
            final String appPackageName = getPackageName();
            Intent i = new Intent(Intent.ACTION_SEND);
            i.setType("text/plain");
            i.putExtra(Intent.EXTRA_SUBJECT, R.string.app_name);
            String sAux = "Join our refer program and share it with your friends";
            sAux = sAux + "\n\n" + "Kalpamrit Marketing Private Limited";

            if (refer_url.equalsIgnoreCase("")) {
                refer_url = "https://play.google.com/store/apps/details?id=com.vpipl.kalpamrit";
            }
            sAux = sAux + "\n\n" + refer_url;
            //  sAux = sAux + "\n\n https://play.google.com/store/apps/details?id=" + appPackageName;
            i.putExtra(Intent.EXTRA_TEXT, sAux);
            startActivity(Intent.createChooser(i, "choose one"));
        } catch (Exception e) {
        }
    }

    public void continueapp() {
        executeGetDashBoardDetails();
        executeGetDashBoardNew19052020();

        enableExpandableList();
        LoadNavigationHeaderItems();
        //  executeGetDashBoardDetailsNew();
        //  executeGetReferaFriend();
    }

    @Override
    public void onBackPressed() {
        try {
            if (drawer.isDrawerOpen(navigationView)) {
                drawer.closeDrawer(navigationView);
            } else {
                showExitDialog();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void showExitDialog() {
        try {
            final Dialog dialog = AppUtils.createDialog(DashBoard_Activity.this, false);
            dialog.setCancelable(false);

            TextView txt_DialogTitle = dialog.findViewById(R.id.txt_DialogTitle);
            //txt_DialogTitle.setText(Html.fromHtml("Are you sure!!! Do you want to Exit from Dashboard?"));
            txt_DialogTitle.setText(Html.fromHtml("Do you want to exit from this Dashboard!"));

            TextView txt_submit = dialog.findViewById(R.id.txt_submit);
            txt_submit.setText("Yes");
            txt_submit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    dialog.dismiss();

                    Intent intent = new Intent(DashBoard_Activity.this, Home_Activity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                }
            });

            TextView txt_cancel = dialog.findViewById(R.id.txt_cancel);
            txt_cancel.setText(getResources().getString(R.string.txt_signout_no));
            txt_cancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        dialog.dismiss();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
            dialog.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        try {
            enableExpandableList();
            LoadNavigationHeaderItems();
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
        }
    }


    private void executeGetDashBoardDetails() {
        try {
            if (AppUtils.isNetworkAvailable(DashBoard_Activity.this)) {
                new AsyncTask<Void, Void, String>() {
                    @Override
                    protected void onPreExecute() {
                        super.onPreExecute();
                        AppUtils.showProgressDialog(DashBoard_Activity.this);
                    }

                    @Override
                    protected String doInBackground(Void... params) {
                        String response = "";
                        try {
                            Thread.currentThread().setPriority(Thread.MAX_PRIORITY);

                            List<NameValuePair> postParameters = new ArrayList<>();
                            postParameters.add(new BasicNameValuePair("FormNo", AppController.getSpUserInfo().getString(SPUtils.USER_FORM_NUMBER, "")));

                            response = AppUtils.callWebServiceWithMultiParam(DashBoard_Activity.this, postParameters, QueryUtils.methodToGetDashboardDetail, TAG);
                        } catch (Exception e) {
                            e.printStackTrace();
                            AppUtils.showExceptionDialog(DashBoard_Activity.this);
                        }

                        return response;
                    }

                    @Override
                    protected void onPostExecute(String resultData) {
                        AppUtils.dismissProgressDialog();
                        try {
                            JSONObject jsonObject = new JSONObject(resultData);

                            JSONArray jsonArrayMembers = jsonObject.getJSONArray("Members");
                            JSONArray jsonArrayPayoutDetail = jsonObject.getJSONArray("PayoutDetail");
                            JSONArray jsonArrayRefDownlineDetail = jsonObject.getJSONArray("RefDownlineDetail");
                            JSONArray jsonArrayRefActiveDownlineDetail = jsonObject.getJSONArray("RefActiveDownlineDetail");
                            JSONArray jsonArrayTotalBVDetail = jsonObject.getJSONArray("GetTotalBVDetail");
                            JSONArray jsonArrayMemberRank = jsonObject.getJSONArray("MemberRank");

                            if (jsonObject.getString("Status").equalsIgnoreCase("True")) {
                                WriteValues(jsonArrayMembers,jsonArrayPayoutDetail, jsonArrayTotalBVDetail, jsonArrayRefDownlineDetail, jsonArrayRefActiveDownlineDetail, jsonArrayMemberRank);
                            } else {
                                AppUtils.alertDialog(DashBoard_Activity.this, jsonObject.getString("Message"));
                                if (AppUtils.showLogs)
                                    Log.v(TAG, "executeGetKYCUploadRequest executed...Failed... called");
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            AppUtils.showExceptionDialog(DashBoard_Activity.this);
                        }
                    }
                }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            }
        } catch (Exception e) {
            e.printStackTrace();
            AppUtils.showExceptionDialog(DashBoard_Activity.this);
        }
    }

    private void WriteValues(JSONArray jsonArrayMembers,JSONArray jsonArrayPayoutDetail, JSONArray jsonArrayTotalBVDetail, JSONArray jsonArrayRefDownlineDetail, JSONArray jsonArrayRefActiveDownlineDetail
            , JSONArray jsonArrayMemberRank) {

        String str_sts = "Inactive";

        try {

            txt_joining_Date.setText("Joining Date : " + jsonArrayMembers.getJSONObject(0).getString("JoiningDt"));
            txt_user_id.setText("ID : " + jsonArrayMembers.getJSONObject(0).getString("IdNo"));
            txt_Status.setText(jsonArrayMembers.getJSONObject(0).getString("Status"));

            txt_Proposer_ID.setText(jsonArrayMembers.getJSONObject(0).getString("ProposerIDNo"));

            txt_LastProfileUpdate.setText(jsonArrayMembers.getJSONObject(0).getString("LastUpdateTime"));
            txt_ActivationDate.setText(jsonArrayMembers.getJSONObject(0).getString("PayDate"));
            txt_ActivationPoint.setText(jsonArrayMembers.getJSONObject(0).getString("BV"));

            if(jsonArrayPayoutDetail.length() > 0) {
                txt_total_self_pur_bv.setText("" + jsonArrayPayoutDetail.getJSONObject(0).getString("TotalBV"));
            }

            if (jsonArrayMembers.length() > 0) {
                // txt_rank_name.setText("Rank : " + jsonArrayMemberRank.getJSONObject(0).getString("MaxPBSlab"));
                str_sts = jsonArrayMembers.getJSONObject(0).getString("Status");
            }


            if (jsonArrayMemberRank.length() > 0) {
                // txt_rank_name.setText("Rank : " + jsonArrayMemberRank.getJSONObject(0).getString("MaxPBSlab"));
                txt_rank_name.setText("" + jsonArrayMemberRank.getJSONObject(0).getString("MaxPBSlab"));
            } else {
                txt_rank_name.setText("NA");
            }

            if (jsonArrayRefDownlineDetail.length() > 0) {
                findViewById(R.id.view_self_downline_join).setVisibility(View.VISIBLE);
                txt_uptodatejoin_self.setText("Self : " + jsonArrayRefDownlineDetail.getJSONObject(0).getInt("SelfJoin"));
                txt_uptodatejoin_downline.setText("Downline : " + jsonArrayRefDownlineDetail.getJSONObject(0).getInt("DownlineJoin"));

                int total = jsonArrayRefDownlineDetail.getJSONObject(0).getInt("DownlineJoin") + jsonArrayRefDownlineDetail.getJSONObject(0).getInt("SelfJoin");
                txt_total_joinings.setText("" + total);
            }

            if (jsonArrayRefActiveDownlineDetail.length() > 0) {
                String str_uptodateactivejoin_self = "0";
                findViewById(R.id.view_self_downline_active_join).setVisibility(View.VISIBLE);

                TextView txt_uptodatejoinactive_downline, txt_uptodateactivejoin_self;
                txt_uptodatejoinactive_downline = findViewById(R.id.txt_uptodatejoinactive_downline);
                txt_uptodateactivejoin_self = findViewById(R.id.txt_uptodateactivejoin_self);

                if (str_sts.equalsIgnoreCase("Active")) {
                    str_uptodateactivejoin_self = "1";
                } else {
                    str_uptodateactivejoin_self = "0";
                }
                txt_uptodateactivejoin_self.setText("Self : " + str_uptodateactivejoin_self);
                txt_uptodatejoinactive_downline.setText("Downline : " + jsonArrayRefActiveDownlineDetail.getJSONObject(0).getString("DownlineActiveJoin"));
                int downline = Integer.parseInt(str_uptodateactivejoin_self) + Integer.parseInt(jsonArrayRefActiveDownlineDetail.getJSONObject(0).getString("DownlineActiveJoin"));
                txt_total_active_joinings.setText("" + downline);
            }

            if (jsonArrayTotalBVDetail.length() > 0) {
                TextView txt_curr_month_bv_self, txt_curr_month_bv_downline;
                TextView txt_uptodatebv_self, txt_uptodatebv_downline;
                txt_curr_month_bv_self = findViewById(R.id.txt_curr_month_bv_self);
                txt_curr_month_bv_downline = findViewById(R.id.txt_curr_month_bv_downline);
                txt_uptodatebv_self = findViewById(R.id.txt_uptodatebv_self);
                txt_uptodatebv_downline = findViewById(R.id.txt_uptodatebv_downline);

                findViewById(R.id.view_self_downline_curr_month_bv).setVisibility(View.VISIBLE);
                findViewById(R.id.view_self_downline_bv).setVisibility(View.VISIBLE);

                /*Current Month BV Detail Start*/
                txt_curr_month_bv_self.setText("Self : " + jsonArrayTotalBVDetail.getJSONObject(0).getString("SelfBV"));
                txt_curr_month_bv_downline.setText("Downline : " + jsonArrayTotalBVDetail.getJSONObject(0).getString("GBV"));
                txt_cm_bv.setText("" + jsonArrayTotalBVDetail.getJSONObject(0).getString("CurrentTotal"));
                /*Current Month BV Detail End*/

                /*Up-to-Date BV Detail*/
                txt_uptodatebv_self.setText("Self :" + jsonArrayTotalBVDetail.getJSONObject(0).getString("TotalSelfBV"));
                txt_uptodatebv_downline.setText("Downline : " + jsonArrayTotalBVDetail.getJSONObject(0).getInt("TotalGBV"));
                int TotalBV = jsonArrayTotalBVDetail.getJSONObject(0).getInt("TotalBV");
                txt_uptodate_bv.setText("" + TotalBV);
                /*Up-to-Date BV Detail End*/

                //*************************************************************************************************
                txt_cum_self_pur_bv.setText("" + jsonArrayTotalBVDetail.getJSONObject(0).getString("SelfBV"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void enableExpandableList() {

        listDataHeader = new ArrayList<>();
        listDataChild = new HashMap<>();

        if (AppController.getSpIsLogin().getBoolean(SPUtils.IS_LOGIN, false)) {
            if (HeadingJarray != null && HeadingJarray.length() > 0)
                prepareListDataDistributor(listDataHeader, listDataChild, HeadingJarray);
            else
                executeTogetDrawerMenuItems();
        }

        ExpandableListAdapter listAdapter = new ExpandableListAdapter(this, listDataHeader, listDataChild);
        expListView.setAdapter(listAdapter);

        expListView.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {

            @Override
            public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id) {

                String GroupTitle = listDataHeader.get(groupPosition);

                if (GroupTitle.trim().equalsIgnoreCase(getResources().getString(R.string.dashboard))) {
                    startActivity(new Intent(DashBoard_Activity.this, DashBoard_Activity.class));
                    if (drawer.isDrawerOpen(GravityCompat.START)) {
                        drawer.closeDrawer(GravityCompat.START);
                    }
                } else if (GroupTitle.trim().equalsIgnoreCase("Home")) {
                    startActivity(new Intent(DashBoard_Activity.this, Home_Activity.class));
                    if (drawer.isDrawerOpen(GravityCompat.START)) {
                        drawer.closeDrawer(GravityCompat.START);
                    }
                } else if (GroupTitle.trim().equalsIgnoreCase("Logout")) {
                    AppUtils.showDialogSignOut(DashBoard_Activity.this);
                } else if (GroupTitle.trim().equalsIgnoreCase("New Joining")) {
                    startActivity(new Intent(DashBoard_Activity.this, Register_Activity.class));
                    if (drawer.isDrawerOpen(GravityCompat.START)) {
                        drawer.closeDrawer(GravityCompat.START);
                    }
                }
                return false;
            }
        });

        expListView.setOnGroupExpandListener(new ExpandableListView.OnGroupExpandListener() {

            @Override
            public void onGroupExpand(int groupPosition) {

                if (lastExpandedPosition != -1
                        && groupPosition != lastExpandedPosition) {
                    expListView.collapseGroup(lastExpandedPosition);
                }
                lastExpandedPosition = groupPosition;
            }

        });

        expListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {

            @Override
            public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
                String ChildItemTitle = listDataChild.get(listDataHeader.get(groupPosition)).get(childPosition);

                if (ChildItemTitle.trim().equalsIgnoreCase(getResources().getString(R.string.view_profile))) {
                    startActivity(new Intent(DashBoard_Activity.this, Profile_View_Activity.class));
                } else if (ChildItemTitle.trim().equalsIgnoreCase(getResources().getString(R.string.change_password))) {
                    startActivity(new Intent(DashBoard_Activity.this, Change_Password_Activity.class));
                } else if (ChildItemTitle.trim().equalsIgnoreCase(getResources().getString(R.string.new_joining))) {
                    startActivity(new Intent(DashBoard_Activity.this, Register_Activity.class));
                } else if (ChildItemTitle.trim().equalsIgnoreCase(getResources().getString(R.string.upload_kyc))) {
                    startActivity(new Intent(DashBoard_Activity.this, KYCUploadDocument_Activity.class).putExtra("HEADING", "Update"));
                } else if (ChildItemTitle.trim().equalsIgnoreCase(getResources().getString(R.string.generation_structure))) {
                    startActivity(new Intent(DashBoard_Activity.this, Sponsor_genealogy_Activity.class));
                } else if (ChildItemTitle.trim().equalsIgnoreCase(getResources().getString(R.string.sponsor_downline))) {
                    startActivity(new Intent(DashBoard_Activity.this, Sponsor_team_details_Activity.class).putExtra("Action", "Sponsor"));
                } else if (ChildItemTitle.trim().equalsIgnoreCase(getResources().getString(R.string.bv_detail_report))) {
                    startActivity(new Intent(DashBoard_Activity.this, Sponsor_team_details_Activity.class).putExtra("Action", "Direct"));
                } else if (ChildItemTitle.trim().equalsIgnoreCase(getResources().getString(R.string.welcome_letter))) {
                    startActivity(new Intent(DashBoard_Activity.this, WelcomeLetter_Activity.class));
                } else if (ChildItemTitle.trim().equalsIgnoreCase(getResources().getString(R.string.purchase_reports))) {
                    startActivity(new Intent(DashBoard_Activity.this, Repurchase_BV_Detail.class));
                } else if (ChildItemTitle.trim().equalsIgnoreCase("Team Repurchase BV Summary")) {
                    startActivity(new Intent(DashBoard_Activity.this, Repurchase_BV_Summary_Team_Activity.class));
                } else if (ChildItemTitle.trim().equalsIgnoreCase("Repurchase Bill Summary")) {
                    startActivity(new Intent(DashBoard_Activity.this, Repurchase_Bill_Summary.class));
                } else if (ChildItemTitle.trim().equalsIgnoreCase(getResources().getString(R.string.wallet_detail))) {
                    startActivity(new Intent(DashBoard_Activity.this, Wallet_Transaction_Report_Activity.class));
                } else if (ChildItemTitle.trim().equalsIgnoreCase("Wallet Request Report")) {
                    startActivity(new Intent(DashBoard_Activity.this, Wallet_Request_Status_Report_Activity.class));
                } else if (ChildItemTitle.trim().equalsIgnoreCase("Request For Wallet Amount")) {
                    startActivity(new Intent(DashBoard_Activity.this, Wallet_Request_Amount_Activity.class));
                } else if (ChildItemTitle.trim().equalsIgnoreCase(getResources().getString(R.string.monthly_incentive))) {
                    startActivity(new Intent(DashBoard_Activity.this, Monthly_Incentive_Activity.class));
                } else if (ChildItemTitle.trim().equalsIgnoreCase(getResources().getString(R.string.monthly_incentive_detail_report))) {
                    startActivity(new Intent(DashBoard_Activity.this, Monthly_Incentive_Detail_Activity.class));
                } else if (ChildItemTitle.trim().equalsIgnoreCase("TDS Detail Report")) {
                    startActivity(new Intent(DashBoard_Activity.this, TDS_detail_report_Activity.class));
                } else if (ChildItemTitle.trim().equalsIgnoreCase("Update Profile")) {
                    startActivity(new Intent(DashBoard_Activity.this, Profile_Update_Activity.class));
                } else if (ChildItemTitle.trim().equalsIgnoreCase("View KYC Documents")) {
                    startActivity(new Intent(DashBoard_Activity.this, KYCUploadDocument_Activity.class).putExtra("HEADING", "View"));
                } else if (ChildItemTitle.trim().equalsIgnoreCase("Register Enquiry/Complaint")) {
                    startActivity(new Intent(DashBoard_Activity.this, Register_Complaint_Activity.class));
                } else if (ChildItemTitle.trim().equalsIgnoreCase("View Open/Pending Queries")) {
                    startActivity(new Intent(DashBoard_Activity.this, ViewPendingQueries_Activity.class).putExtra("HEADING", "Open"));
                } else if (ChildItemTitle.trim().equalsIgnoreCase("View Closed Queries")) {
                    startActivity(new Intent(DashBoard_Activity.this, ViewPendingQueries_Activity.class).putExtra("HEADING", "Closed"));
                } else if (ChildItemTitle.trim().equalsIgnoreCase("Product Wallet Detail")) {
                    startActivity(new Intent(DashBoard_Activity.this, ProductWallet_Transaction_Report_Activity.class));
                } else if (ChildItemTitle.trim().equalsIgnoreCase("Wallet To Bank Transfer Detail")) {
                    startActivity(new Intent(DashBoard_Activity.this, Wallet_Bank_Transfer_Report_Activity.class));
                } else if (ChildItemTitle.trim().equalsIgnoreCase("ID Card")) {
                    startActivity(new Intent(DashBoard_Activity.this, ID_card_Activity.class));
                } else if (ChildItemTitle.trim().equalsIgnoreCase("New Sponsor Genealogy")) {
                    startActivity(new Intent(DashBoard_Activity.this, NewSponsorGenealogyReport_Activity.class));
                }else if (ChildItemTitle.trim().equalsIgnoreCase("Change Proposer")) {
                    startActivity(new Intent(DashBoard_Activity.this, Change_Proposer_Activity.class));
                }else if (ChildItemTitle.trim().equalsIgnoreCase("Proposer Genealogy")) {
                    startActivity(new Intent(DashBoard_Activity.this, ProposerGenealogyReport_Activity.class));
                }
                if (drawer.isDrawerOpen(GravityCompat.START)) {
                    drawer.closeDrawer(GravityCompat.START);
                }
                return false;
            }
        });
    }

    private void LoadNavigationHeaderItems() {
        txt_id_number.setText("");
        txt_id_number.setVisibility(View.GONE);

        txt_available_wb.setText("");
        txt_available_wb.setVisibility(View.GONE);
        txt_welcome_name.setText("Guest");

        if (AppController.getSpIsLogin().getBoolean(SPUtils.IS_LOGIN, false)) {
            String welcome_text = WordUtils.capitalizeFully(AppController.getSpUserInfo().getString(SPUtils.USER_FIRST_NAME, ""));
            txt_welcome_name.setText(welcome_text);

            txt_user_id.setText("ID : " + AppController.getSpUserInfo().getString(SPUtils.USER_ID_NUMBER, ""));

            String userid = AppController.getSpUserInfo().getString(SPUtils.USER_ID_NUMBER, "");
            txt_id_number.setText(userid);
            txt_id_number.setVisibility(View.VISIBLE);

            // executeWalletBalanceRequest();

            String bytecode = AppController.getSpUserInfo().getString(SPUtils.USER_profile_pic_byte_code, "");

            if (!bytecode.equalsIgnoreCase(""))
                profileImage.setImageBitmap(AppUtils.getBitmapFromString(bytecode));
        }
    }

    private void prepareListDataDistributor(List<String> listDataHeader, Map<String, List<String>> listDataChild, JSONArray HeadingJarray) {

        List<String> Empty = new ArrayList<>();
        try {
            listDataHeader.add("Home");
            listDataChild.put(listDataHeader.get(listDataHeader.size() - 1), Empty);

            ArrayList<String> MenuAl = new ArrayList<>();
            for (int i = 0; i < HeadingJarray.length(); i++) {
                if (HeadingJarray.getJSONObject(i).getInt("ParentId") == 0)
                    MenuAl.add(HeadingJarray.getJSONObject(i).getString("MenuName"));
            }

            for (int aa = 0; aa < MenuAl.size(); aa++) {
                ArrayList<String> SubMenuAl = new ArrayList<>();

                for (int bb = 0; bb < HeadingJarray.length(); bb++) {
                    if (HeadingJarray.getJSONObject(aa).getInt("MenuId") == HeadingJarray.getJSONObject(bb).getInt("ParentId")) {
                        SubMenuAl.add(AppUtils.CapsFirstLetterString(HeadingJarray.getJSONObject(bb).getString("MenuName")));
                    }
                }
                listDataHeader.add(AppUtils.CapsFirstLetterString(MenuAl.get(aa)));
                listDataChild.put(listDataHeader.get(listDataHeader.size() - 1), SubMenuAl);
            }

            listDataHeader.add("Logout");
            listDataChild.put(listDataHeader.get(listDataHeader.size() - 1), Empty);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /*Code added by mukesh 28-01-2019 07:36 PM*/

    public void WriteValuesTables(JSONArray jsonArrayDownLineDetail) {
        TableLayout ll = (TableLayout) findViewById(R.id.displayLinear_two);
        ll.removeAllViews();

        try {
            float sp = 8;
            int px = (int) (sp * getResources().getDisplayMetrics().scaledDensity);

            TableRow.LayoutParams params = new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT);
            params.setMargins(px / 2, px / 2, px / 2, px / 2);
            params.weight = 1;

            if (jsonArrayDownLineDetail.length() > 0) {
                TableRow row1 = new TableRow(this);

                TableRow.LayoutParams lp1 = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT);
                row1.setLayoutParams(lp1);
                row1.setBackgroundColor(getResources().getColor(R.color.table_Heading_Columns));

                Typeface typeface = ResourcesCompat.getFont(this, R.font.gisha_0);
// Level No.	Member ID	Member Name	Self Repurchase BV	Team Repurchase BV	Total BV
                TextView A1 = new TextView(this);
                TextView B1 = new TextView(this);
                TextView C1 = new TextView(this);
                TextView D1 = new TextView(this);
                TextView E1 = new TextView(this);
                TextView F1 = new TextView(this);

                A1.setText("Level No");
                B1.setText("Member ID");
                C1.setText("Member Name");
                D1.setText("Self Repurchase BV");
                E1.setText("Team Repurchase BV");
                F1.setText("Total BV");

                A1.setPadding(px, px, px, px);
                B1.setPadding(px, px, px, px);
                C1.setPadding(px, px, px, px);
                D1.setPadding(px, px, px, px);
                E1.setPadding(px, px, px, px);
                F1.setPadding(px, px, px, px);

                A1.setTypeface(typeface);
                B1.setTypeface(typeface);
                C1.setTypeface(typeface);
                D1.setTypeface(typeface);
                E1.setTypeface(typeface);
                F1.setTypeface(typeface);

                A1.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
                B1.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
                C1.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
                D1.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
                E1.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
                F1.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);

                A1.setGravity(Gravity.CENTER);
                B1.setGravity(Gravity.CENTER);
                C1.setGravity(Gravity.CENTER);
                D1.setGravity(Gravity.CENTER);
                E1.setGravity(Gravity.CENTER);
                F1.setGravity(Gravity.CENTER);

                A1.setTextColor(Color.BLACK);
                B1.setTextColor(Color.BLACK);
                C1.setTextColor(Color.BLACK);
                D1.setTextColor(Color.BLACK);
                E1.setTextColor(Color.BLACK);
                F1.setTextColor(Color.BLACK);

                A1.setLayoutParams(params);
                B1.setLayoutParams(params);
                C1.setLayoutParams(params);
                D1.setLayoutParams(params);
                E1.setLayoutParams(params);
                F1.setLayoutParams(params);

                row1.addView(A1);
                row1.addView(B1);
                row1.addView(C1);
                row1.addView(D1);
                row1.addView(E1);
                row1.addView(F1);

                ll.addView(row1);


                for (int i = 0; i < jsonArrayDownLineDetail.length(); i++) {
                    try {
                        JSONObject jobject = jsonArrayDownLineDetail.getJSONObject(i);

                        String LevelNo = jobject.getString("MLevel");
                        String IdNo = jobject.getString("IdNo");
                        String MemName = jobject.getString("MemName");
                        String SRepurchase = jobject.getString("SRepurchase");
                        String DRepurchase = jobject.getString("DRepurchase");
                        String TotalBV = jobject.getString("TotalBV");


                        TableRow row = new TableRow(this);
                        TableRow.LayoutParams lp = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT);
                        row.setLayoutParams(lp);

                        if (i % 2 == 0)
                            row.setBackgroundColor(getResources().getColor(R.color.table_row_one));
                        else
                            row.setBackgroundColor(getResources().getColor(R.color.table_row_two));

                        TextView A = new TextView(this);
                        TextView B = new TextView(this);
                        TextView C = new TextView(this);
                        TextView D = new TextView(this);
                        TextView E = new TextView(this);
                        TextView F = new TextView(this);

                        float self_a = Float.parseFloat(SRepurchase);
                        float team_a = Float.parseFloat(DRepurchase);
                        float ttl_a = Float.parseFloat(TotalBV);

                        A.setText(LevelNo);
                        B.setText(IdNo);
                        C.setText(MemName);
                        D.setText("" + Math.round(self_a));
                        E.setText("" + Math.round(team_a));
                        F.setText("" + Math.round(ttl_a));

                        A.setGravity(Gravity.CENTER);
                        B.setGravity(Gravity.CENTER);
                        C.setGravity(Gravity.CENTER);
                        D.setGravity(Gravity.CENTER);
                        E.setGravity(Gravity.CENTER);
                        F.setGravity(Gravity.CENTER);

                        A.setPadding(px, px, px, px);
                        B.setPadding(px, px, px, px);
                        C.setPadding(px, px, px, px);
                        D.setPadding(px, px, px, px);
                        E.setPadding(px, px, px, px);
                        F.setPadding(px, px, px, px);

                        A.setTypeface(typeface);
                        B.setTypeface(typeface);
                        C.setTypeface(typeface);
                        D.setTypeface(typeface);
                        E.setTypeface(typeface);
                        F.setTypeface(typeface);

                        A.setLayoutParams(params);
                        B.setLayoutParams(params);
                        C.setLayoutParams(params);
                        D.setLayoutParams(params);
                        E.setLayoutParams(params);
                        F.setLayoutParams(params);

                        A.setTextSize(TypedValue.COMPLEX_UNIT_SP, 13);
                        B.setTextSize(TypedValue.COMPLEX_UNIT_SP, 13);
                        C.setTextSize(TypedValue.COMPLEX_UNIT_SP, 13);
                        D.setTextSize(TypedValue.COMPLEX_UNIT_SP, 13);
                        E.setTextSize(TypedValue.COMPLEX_UNIT_SP, 13);
                        F.setTextSize(TypedValue.COMPLEX_UNIT_SP, 13);

                        row.addView(A);
                        row.addView(B);
                        row.addView(C);
                        row.addView(D);
                        row.addView(E);
                        row.addView(F);

                        ll.addView(row);

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void WriteValuesTablesTwo(JSONArray jsonArrayDownLineDetail) {
        TableLayout ll = (TableLayout) findViewById(R.id.displayLinear);
        ll.removeAllViews();

        try {
            float sp = 8;
            int px = (int) (sp * getResources().getDisplayMetrics().scaledDensity);

            TableRow.LayoutParams params = new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT);
            params.setMargins(px / 2, px / 2, px / 2, px / 2);
            params.weight = 1;

            if (jsonArrayDownLineDetail.length() > 0) {
                TableRow row1 = new TableRow(this);

                TableRow.LayoutParams lp1 = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT);
                row1.setLayoutParams(lp1);
                row1.setBackgroundColor(getResources().getColor(R.color.table_Heading_Columns));

                Typeface typeface = ResourcesCompat.getFont(this, R.font.gisha_0);
// Level No.	Member ID	Member Name	Self Repurchase BV	Team Repurchase BV	Total BV
                TextView A1 = new TextView(this);
                TextView B1 = new TextView(this);
                TextView C1 = new TextView(this);
                TextView D1 = new TextView(this);
                TextView E1 = new TextView(this);
                TextView F1 = new TextView(this);

                A1.setText("Level No");
                B1.setText("Member ID");
                C1.setText("Member Name");
                D1.setText("Self Repurchase BV");
                E1.setText("Team Repurchase BV");
                F1.setText("Total BV");

                A1.setPadding(px, px, px, px);
                B1.setPadding(px, px, px, px);
                C1.setPadding(px, px, px, px);
                D1.setPadding(px, px, px, px);
                E1.setPadding(px, px, px, px);
                F1.setPadding(px, px, px, px);

                A1.setTypeface(typeface);
                B1.setTypeface(typeface);
                C1.setTypeface(typeface);
                D1.setTypeface(typeface);
                E1.setTypeface(typeface);
                F1.setTypeface(typeface);

                A1.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
                B1.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
                C1.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
                D1.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
                E1.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
                F1.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);

                A1.setGravity(Gravity.CENTER);
                B1.setGravity(Gravity.CENTER);
                C1.setGravity(Gravity.CENTER);
                D1.setGravity(Gravity.CENTER);
                E1.setGravity(Gravity.CENTER);
                F1.setGravity(Gravity.CENTER);

                A1.setTextColor(Color.BLACK);
                B1.setTextColor(Color.BLACK);
                C1.setTextColor(Color.BLACK);
                D1.setTextColor(Color.BLACK);
                E1.setTextColor(Color.BLACK);
                F1.setTextColor(Color.BLACK);

                A1.setLayoutParams(params);
                B1.setLayoutParams(params);
                C1.setLayoutParams(params);
                D1.setLayoutParams(params);
                E1.setLayoutParams(params);
                F1.setLayoutParams(params);

                row1.addView(A1);
                row1.addView(B1);
                row1.addView(C1);
                row1.addView(D1);
                row1.addView(E1);
                row1.addView(F1);

                ll.addView(row1);


                for (int i = 0; i < jsonArrayDownLineDetail.length(); i++) {
                    try {
                        JSONObject jobject = jsonArrayDownLineDetail.getJSONObject(i);

                        String LevelNo = jobject.getString("MLevel");
                        String IdNo = jobject.getString("IdNo");
                        String MemName = jobject.getString("MemName");
                        String SRepurchase = jobject.getString("SRepurchase");
                        String DRepurchase = jobject.getString("DRepurchase");
                        String TotalBV = jobject.getString("TotalBV");

                        TableRow row = new TableRow(this);
                        TableRow.LayoutParams lp = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT);
                        row.setLayoutParams(lp);

                        if (i % 2 == 0)
                            row.setBackgroundColor(getResources().getColor(R.color.table_row_one));
                        else
                            row.setBackgroundColor(getResources().getColor(R.color.table_row_two));

                        TextView A = new TextView(this);
                        TextView B = new TextView(this);
                        TextView C = new TextView(this);
                        TextView D = new TextView(this);
                        TextView E = new TextView(this);
                        TextView F = new TextView(this);

                        /*Double team_a  = 0.0 ;
                        team_a = Double.parseDouble(DRepurchase);
                        Double ttl_a  = 0.0 ;
                        ttl_a = Double.parseDouble(TotalBV);
                        */

                        float self_a = Float.parseFloat(SRepurchase);
                        float team_a = Float.parseFloat(DRepurchase);
                        float ttl_a = Float.parseFloat(TotalBV);

                        A.setText(LevelNo);
                        B.setText(IdNo);
                        C.setText(MemName);
                        D.setText("" + Math.round(self_a));
                        E.setText("" + Math.round(team_a));
                        F.setText("" + Math.round(ttl_a));

                        A.setGravity(Gravity.CENTER);
                        B.setGravity(Gravity.CENTER);
                        C.setGravity(Gravity.CENTER);
                        D.setGravity(Gravity.CENTER);
                        E.setGravity(Gravity.CENTER);
                        F.setGravity(Gravity.CENTER);

                        A.setPadding(px, px, px, px);
                        B.setPadding(px, px, px, px);
                        C.setPadding(px, px, px, px);
                        D.setPadding(px, px, px, px);
                        E.setPadding(px, px, px, px);
                        F.setPadding(px, px, px, px);

                        A.setTypeface(typeface);
                        B.setTypeface(typeface);
                        C.setTypeface(typeface);
                        D.setTypeface(typeface);
                        E.setTypeface(typeface);
                        F.setTypeface(typeface);

                        A.setLayoutParams(params);
                        B.setLayoutParams(params);
                        C.setLayoutParams(params);
                        D.setLayoutParams(params);
                        E.setLayoutParams(params);
                        F.setLayoutParams(params);

                        A.setTextSize(TypedValue.COMPLEX_UNIT_SP, 13);
                        B.setTextSize(TypedValue.COMPLEX_UNIT_SP, 13);
                        C.setTextSize(TypedValue.COMPLEX_UNIT_SP, 13);
                        D.setTextSize(TypedValue.COMPLEX_UNIT_SP, 13);
                        E.setTextSize(TypedValue.COMPLEX_UNIT_SP, 13);
                        F.setTextSize(TypedValue.COMPLEX_UNIT_SP, 13);

                        row.addView(A);
                        row.addView(B);
                        row.addView(C);
                        row.addView(D);
                        row.addView(E);
                        row.addView(F);

                        ll.addView(row);

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /*Refer a friend added by mukesh on 02-11-2019 11:19 AM*/
    private void executeGetReferaFriend() {
        try {
            if (AppUtils.isNetworkAvailable(DashBoard_Activity.this)) {
                new AsyncTask<Void, Void, String>() {
                    @Override
                    protected void onPreExecute() {
                        super.onPreExecute();
                        //     AppUtils.showProgressDialog(DashBoard_Activity.this);
                    }

                    @Override
                    protected String doInBackground(Void... params) {
                        String response = "";
                        try {
                            List<NameValuePair> postParameters = new ArrayList<>();
                            postParameters.add(new BasicNameValuePair("FormNo", AppController.getSpUserInfo().getString(SPUtils.USER_ID_NUMBER, "")));
                            response = AppUtils.callWebServiceWithMultiParam(DashBoard_Activity.this, postParameters, QueryUtils.methodToReferFriend, TAG);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        return response;
                    }

                    @Override
                    protected void onPostExecute(String resultData) {
                        AppUtils.dismissProgressDialog();
                        try {
                            JSONObject jsonObject = new JSONObject(resultData);

                            if (jsonObject.getString("Status").equalsIgnoreCase("True")) {
                                JSONArray jsonArrayData = jsonObject.getJSONArray("Data");
                                if (jsonObject.getString("Message").equalsIgnoreCase("Successfully.!")) {
                                    refer_url = "" + jsonArrayData.getJSONObject(0).getString("Url");
                                    //  txt_refer_url.setText("" +refer_url);
                                    txt_refer_url.setText("Refer a friend");
                                }
                            } else {
                                refer_url = "";
                                //  AppUtils.alertDialog(DashBoard_Activity.this, jsonObject.getString("Message"));
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            //   AppUtils.showExceptionDialog(DashBoard_Activity.this);
                        }
                    }
                }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            }
        } catch (Exception e) {
            e.printStackTrace();
            //  AppUtils.showExceptionDialog(DashBoard_Activity.this);
        }
    }

    private void executeWalletBalanceRequest() {
        try {
            if (AppUtils.isNetworkAvailable(DashBoard_Activity.this)) {
                new AsyncTask<Void, Void, String>() {
                    @Override
                    protected String doInBackground(Void... params) {
                        String response = "";
                        try {
                            Thread.currentThread().setPriority(Thread.MAX_PRIORITY);

                            List<NameValuePair> postParameters = new ArrayList<>();
                            postParameters.add(new BasicNameValuePair("Formno", AppController.getSpUserInfo().getString(SPUtils.USER_FORM_NUMBER, "")));
                            response = AppUtils.callWebServiceWithMultiParam(DashBoard_Activity.this,
                                    postParameters, QueryUtils.methodToGetWalletBalance, TAG);

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        return response;
                    }

                    @Override
                    protected void onPostExecute(String resultData) {
                        try {
                            JSONObject jsonObject = new JSONObject(resultData);
                            JSONArray jsonArrayData = jsonObject.getJSONArray("Data");

                            if (jsonObject.getString("Status").equalsIgnoreCase("True")) {
                                if (jsonObject.getString("Message").equalsIgnoreCase("Successfully.!")) {
                                    String count_text = "Wallet Balance : \u20B9 " + jsonArrayData.getJSONObject(0).getString("WBalance");
                                    txt_available_wb.setText(count_text);
                                    txt_available_wb.setVisibility(View.VISIBLE);
                                }
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void executeProductWalletBalanceRequest() {
        try {
            if (AppUtils.isNetworkAvailable(DashBoard_Activity.this)) {
                new AsyncTask<Void, Void, String>() {
                    @Override
                    protected String doInBackground(Void... params) {
                        String response = "";
                        try {
                            List<NameValuePair> postParameters = new ArrayList<>();
                            postParameters.add(new BasicNameValuePair("Formno", AppController.getSpUserInfo().getString(SPUtils.USER_FORM_NUMBER, "")));
                            response = AppUtils.callWebServiceWithMultiParam(DashBoard_Activity.this,
                                    postParameters, QueryUtils.methodToProductWalletBalance, TAG);

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        return response;
                    }

                    @Override
                    protected void onPostExecute(String resultData) {
                        try {
                            JSONObject jsonObject = new JSONObject(resultData);
                            JSONArray jsonArrayData = jsonObject.getJSONArray("Data");

                            if (jsonObject.getString("Status").equalsIgnoreCase("True")) {
                                if (jsonObject.getString("Message").equalsIgnoreCase("Successfully.!")) {
                                    //String count_text = "Product Wallet Bal. : ₹ " + jsonArrayData.getJSONObject(0).getString("ProductWalletBal");
                                    String count_text = "₹ " + jsonArrayData.getJSONObject(0).getString("ProductWalletBal");
                                    txt_product_bonus.setText(count_text);
                                } else {
                                    AppUtils.alertDialog(DashBoard_Activity.this, jsonObject.getString("Message"));
                                    txt_product_bonus.setVisibility(View.GONE);
                                }
                            } else {
                                AppUtils.alertDialog(DashBoard_Activity.this, jsonObject.getString("Message"));
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            AppUtils.showExceptionDialog(DashBoard_Activity.this);
                        }
                    }
                }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            }
        } catch (Exception e) {
            e.printStackTrace();
            AppUtils.showExceptionDialog(DashBoard_Activity.this);
        }
    }

    private void executeTogetDrawerMenuItems() {
        new AsyncTask<Void, Void, String>() {

            @Override
            protected void onPreExecute() {
                AppUtils.showProgressDialog(DashBoard_Activity.this);
            }

            @Override
            protected String doInBackground(Void... params) {

                Thread.currentThread().setPriority(Thread.MAX_PRIORITY);

                String response = "";
                try {
                    Thread.currentThread().setPriority(Thread.MAX_PRIORITY);

                    List<NameValuePair> postParameters = new ArrayList<>();
                    response = AppUtils.callWebServiceWithMultiParam(DashBoard_Activity.this, postParameters, QueryUtils.methodtoGetDrawerMenuItems, TAG);

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
                        HeadingJarray = jsonObject.getJSONArray("Data");
                        prepareListDataDistributor(listDataHeader, listDataChild, HeadingJarray);
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    private void executeGetDashBoardDetailsNew() {
        try {
            if (AppUtils.isNetworkAvailable(DashBoard_Activity.this)) {
                new AsyncTask<Void, Void, String>() {
                    @Override
                    protected void onPreExecute() {
                        super.onPreExecute();
                        AppUtils.showProgressDialog(DashBoard_Activity.this);
                    }

                    @Override
                    protected String doInBackground(Void... params) {
                        String response = "";
                        try {
                            List<NameValuePair> postParameters = new ArrayList<>();
                            postParameters.add(new BasicNameValuePair("FormNo", AppController.getSpUserInfo().getString(SPUtils.USER_FORM_NUMBER, "")));
                            response = AppUtils.callWebServiceWithMultiParam(DashBoard_Activity.this, postParameters, QueryUtils.methodToDashboardrepurchaseBVDetail, TAG);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        return response;
                    }

                    @Override
                    protected void onPostExecute(String resultData) {
                        AppUtils.dismissProgressDialog();
                        try {
                            JSONObject jsonObject = new JSONObject(resultData);

                            if (jsonObject.getString("Status").equalsIgnoreCase("True")) {
                                ll_bottom_rep.setVisibility(View.VISIBLE);
                                WriteValuesTables(jsonObject.getJSONArray("CurrentRepurchaseBVDetail"));
                                WriteValuesTablesTwo(jsonObject.getJSONArray("UptodateRepurchaseBVDetail"));
                            } else {
                                ll_bottom_rep.setVisibility(View.GONE);
                                AppUtils.alertDialog(DashBoard_Activity.this, jsonObject.getString("Message"));
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            AppUtils.showExceptionDialog(DashBoard_Activity.this);
                        }
                    }
                }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            }
        } catch (Exception e) {
            e.printStackTrace();
            AppUtils.showExceptionDialog(DashBoard_Activity.this);
        }
    }

    private void executeGetDashBoardNew19052020() {
        try {
            if (AppUtils.isNetworkAvailable(DashBoard_Activity.this)) {
                new AsyncTask<Void, Void, String>() {
                    @Override
                    protected void onPreExecute() {
                        super.onPreExecute();
                        AppUtils.showProgressDialog(DashBoard_Activity.this);
                    }

                    @Override
                    protected String doInBackground(Void... params) {
                        String response = "";
                        try {
                            List<NameValuePair> postParameters = new ArrayList<>();
                            postParameters.add(new BasicNameValuePair("FormNo", AppController.getSpUserInfo().getString(SPUtils.USER_FORM_NUMBER, "")));
                            response = AppUtils.callWebServiceWithMultiParam(DashBoard_Activity.this, postParameters, QueryUtils.methodtoDasboard_New, TAG);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        return response;
                    }

                    @Override
                    protected void onPostExecute(String resultData) {
                        AppUtils.dismissProgressDialog();
                        try {
                            JSONObject jsonObject = new JSONObject(resultData);

                            if (jsonObject.getString("Status").equalsIgnoreCase("True")) {
                                /*"ProductWalletBalance":[],"GetAvailableBalance":[],"DashBoardMenu":[],
                                "CurrentRepurchase":[],"UptodateRepurchase":[],"ShareURl":[]*/
                                JSONArray ProductWalletBalance = jsonObject.getJSONArray("ProductWalletBalance");
                                JSONArray GetAvailableBalance = jsonObject.getJSONArray("GetAvailableBalance");
                                JSONArray HeadingJarray = jsonObject.getJSONArray("DashBoardMenu");
                                JSONArray CurrentRepurchase = jsonObject.getJSONArray("CurrentRepurchase");
                                JSONArray UptodateRepurchase = jsonObject.getJSONArray("UptodateRepurchase");
                                JSONArray ShareURl = jsonObject.getJSONArray("ShareURl");

                                if (ProductWalletBalance.length() > 0) {
                                    String count_text = "₹ " + ProductWalletBalance.getJSONObject(0).getString("ProductWalletBal");
                                    txt_product_bonus.setText(count_text);
                                } else {
                                    txt_product_bonus.setVisibility(View.GONE);
                                }

                                if (GetAvailableBalance.length() > 0) {
                                    String count_text = "Wallet Balance : \u20B9 " + GetAvailableBalance.getJSONObject(0).getString("WBalance");
                                    txt_available_wb.setText(count_text);
                                    txt_available_wb.setVisibility(View.VISIBLE);
                                } else {
                                    txt_available_wb.setVisibility(View.GONE);
                                }
                                /*if (HeadingJarray.length() > 0) {
                                    HeadingJarray = jsonObject.getJSONArray("DashBoardMenu");
                                    prepareListDataDistributor(listDataHeader, listDataChild, HeadingJarray);
                                }*/
                                if (CurrentRepurchase.length() > 0) {
                                    ll_bottom_rep.setVisibility(View.VISIBLE);
                                    WriteValuesTables(CurrentRepurchase);
                                } else {

                                }
                                if (UptodateRepurchase.length() > 0) {
                                    WriteValuesTablesTwo(UptodateRepurchase);
                                } else {
                                    // ll_bottom_rep.setVisibility(View.GONE);
                                }
                                if (ShareURl.length() > 0) {
                                    refer_url = "" + ShareURl.getJSONObject(0).getString("Url");
                                    txt_refer_url.setText("Refer a friend");
                                } else {
                                    refer_url = "";
                                }

                            } else {
                                AppUtils.alertDialog(DashBoard_Activity.this, jsonObject.getString("Message"));
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            AppUtils.showExceptionDialog(DashBoard_Activity.this);
                        }
                    }
                }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            }
        } catch (Exception e) {
            e.printStackTrace();
            AppUtils.showExceptionDialog(DashBoard_Activity.this);
        }
    }
}