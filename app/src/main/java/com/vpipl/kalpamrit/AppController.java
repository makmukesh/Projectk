package com.vpipl.kalpamrit;

import android.app.Activity;
import android.app.Application;
import android.arch.lifecycle.Lifecycle;
import android.arch.lifecycle.LifecycleObserver;
import android.arch.lifecycle.OnLifecycleEvent;
import android.arch.lifecycle.ProcessLifecycleOwner;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.util.Log;

import com.vpipl.kalpamrit.SMS.AppSignatureHelper;
import com.vpipl.kalpamrit.Utils.SPUtils;
import com.vpipl.kalpamrit.model.ExpandList;
import com.vpipl.kalpamrit.model.FilterList2CheckBox;
import com.vpipl.kalpamrit.model.ProductsList;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by admin on 01-05-2017.
 */
public class AppController extends Application implements LifecycleObserver {

    private Timer timer;

    /**
     * for State Listing
     */
    public static ArrayList<HashMap<String, String>> stateList = new ArrayList<>();
    public static ArrayList<HashMap<String, String>> RewardList = new ArrayList<>();
    public static ArrayList<HashMap<String, String>> bankList = new ArrayList<>();

    public static List<ProductsList> selectedProductsList = new ArrayList<>();
    public static List<ProductsList> selectedProductsListUpdated = new ArrayList<>();

    public static ArrayList<HashMap<String, String>> category1 = new ArrayList<>();
    public static ArrayList<HashMap<String, String>> category2 = new ArrayList<>();
    public static ArrayList<HashMap<String, String>> category3 = new ArrayList<>();
    public static ArrayList<HashMap<String, String>> category4 = new ArrayList<>();

    public static ArrayList<FilterList2CheckBox> priceFilterList = new ArrayList<>();
    public static ArrayList<FilterList2CheckBox> discountFilterList = new ArrayList<>();
    public static ArrayList<HashMap<String, String>> filterList1 = new ArrayList<>();


    public static List<ExpandList> navigationDrawerList = new ArrayList<>();


    public static boolean comesFromFilter = false;

    public static String FiltersCondition = "";

    private static AppController mInstance;
    private static SharedPreferences sp_userinfo;
    private static SharedPreferences sp_rememberuserinfo;
    private static SharedPreferences sp_isLogin;

    private static SharedPreferences sp_isInstall;

    /**
     * used to get instance globally
     */
    public static synchronized AppController getInstance() {
        return mInstance;
    }

    /**
     * used to get instance globally of SharedPreferences
     */
    public static synchronized SharedPreferences getSpUserInfo() {
        return sp_userinfo;
    }

    public static synchronized SharedPreferences getSpRememberUserInfo() {
        return sp_rememberuserinfo;
    }

    public static synchronized SharedPreferences getSpIsInstall() {
        return sp_isInstall;
    }


    /**
     * used to get instance globally of SharedPreferences
     */
    public static synchronized SharedPreferences getSpIsLogin() {
        return sp_isLogin;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        try {
            mInstance = this;

            initSharedPreferences();

            AppSignatureHelper appSignature = new AppSignatureHelper((Context) this);
            appSignature.getAppSignatures();

            ProcessLifecycleOwner.get().getLifecycle().addObserver(this);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * used to initialize instance globally of SharedPreferences
     */
    private void initSharedPreferences() {
        try {
            sp_userinfo = getApplicationContext().getSharedPreferences(SPUtils.USER_INFO, Context.MODE_PRIVATE);
            sp_rememberuserinfo = getApplicationContext().getSharedPreferences(SPUtils.REMEMBER_USER_INFO, Context.MODE_PRIVATE);
            sp_isLogin = getApplicationContext().getSharedPreferences(SPUtils.IS_LOGIN, Context.MODE_PRIVATE);
            sp_isInstall = getApplicationContext().getSharedPreferences(SPUtils.IS_INSTALL, Context.MODE_PRIVATE);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

   /* @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    private void onAppBackgrounded() {
        Log.d("MyApp", "App in background");

        timer = new Timer();
        Log.i("Main timer", "Invoking logout timer");
        LogOutTimerTask logoutTimeTask = new LogOutTimerTask();
        //    timer.schedule(logoutTimeTask, 300000); //auto logout in 5 minutes
        timer.schedule(logoutTimeTask, 30000); //auto logout in 30 seconds
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    private void onAppForegrounded() {
        if(timer != null) {
            timer.cancel();
            timer = null;

            Log.i("Main timer", "Timer Cancel Logout");
        }
        Log.d("MyApp", "App in foreground");
    }*/

    private class LogOutTimerTask extends TimerTask {

        @Override
        public void run() {

            if(timer == null) {
                Log.i("Main timer", "Failed Timer Logout");
            }
            else {
                AppController.getSpUserInfo().edit().clear().commit();
                AppController.getSpIsLogin().edit().clear().commit();
                AppController.selectedProductsList.clear();

                //  AppController.finishAffinity();

                System.exit(0);

                Intent a = new Intent(Intent.ACTION_MAIN);
                a.addCategory(Intent.CATEGORY_HOME);
                a.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(a);

                android.os.Process.killProcess(android.os.Process.myPid());

                Log.i("Main timer", "Success Timer Logout");
            }
        }
    }
}