package com.vpipl.kalpamrit.Utils;

import android.content.Context;

/**
 * Created by admin on 01-05-2017.
 */
public class SPUtils {

    public static String IS_INSTALL = "sp_isInstall";
    public static String IS_INSTALL_MobileNo = "sp_isInstall_MobileNo";
    public static String IS_INSTALL_DeviceModel = "sp_isInstall_DeviceModel";
    public static String IS_INSTALL_DeviceName = "sp_isInstall_DeviceName";
    public static String IS_REMEMBER_User = "sp_isRemember";
    public static String IS_REMEMBER_ID_User = "sp_isRemember_id";
    public static String IS_REMEMBER_ID_Member = "sp_isRemember_id";
    public static String USER_INFO = "sp_userinfo";
    public static String REMEMBER_USER_INFO = "sp_remember_userinfo";
    public static String IS_LOGIN = "sp_isLogin";
    public static String USER_ID_NUMBER = "sp_user_id";
    public static String USER_TYPE = "sp_user_type";
    public static String USER_FORM_NUMBER = "sp_user_form_id";
    public static String USER_WALLET_BALANCE = "sp_user_form_id";
    public static String USER_FIRST_NAME = "sp_user_first_name";
    public static String USER_LAST_NAME = "sp_user_last_name";
    public static String USER_KIT_ID = "sp_user_kit_id";
    public static String USER_STATE_CODE = "sp_user_state_code";
    public static String USER_ACTIVE_STATUS = "active_status";
    public static String USER_UPGRADE_DATE = "UpGradeDate";
    public static String USER_DOJ = "doj";
    public static String USER_DOB = "dob";
    public static String USER_ADDRESS = "address";
    public static String USER_ADDRESS2 = "address";
    public static String USER_CITY = "city";
    public static String USER_DISTRICT = "district";
    public static String USER_PAN = "PanNo";
    public static String USER_USER_NM = "UserNm";
    public static String USER_KIT_NAME = "KitName";
    public static String USER_JOIN_COLOR = "JoinColor";
    public static String USER_IS_PORTAL = "isPortal";
    public static String USER_profile_pic_byte_code = "bytecode";
    //Keys of profileDetailsList
    public static String USER_CoSponsorID = "cosponsor_id";
    public static String USER_NAME = "name";
    public static String USER_Relation_Prefix = "name_relation";
    public static String USER_FATHER_NAME = "fname";
    public static String USER_PASSWORD = "password";
    public static String USER_MOBILE_NO = "mobileNo";
    public static String USER_Phone_NO = "phoneNo";
    public static String USER_GENDER = "gender";
    public static String USER_STATE = "state";
    public static String USER_PINCODE = "pin";
    public static String USER_EMAIL = "email";
    public static String USER_AADHAAR = "aadhaar";
    public static String USER_CATEGORY = "category";
    public static String USER_BANKNAME = "bank_name";
    public static String USER_BANKACNTNUM = "bank_acuntnumber";
    public static String USER_BANKIFSC = "bank_ifsc";
    public static String USER_BANKBRANCH = "bank_branch";
    public static String USER_NOMINEE_NAME = "nominee_name";
    public static String USER_NOMINEE_RELATION = "nominee_relation";
    public static String USER_NOMINEE_DOB = "nominee_dob";
    public static String USER_SPONSOR_ID = "sponsor_id";
    public static String USER_SPONSOR_NAME = "sponsor_name";
    public static String USER_ProposeID = "proposeID";
    public static String USER_ProposeName = "ProposeName";
    public static String USER_PopupSts = "PopupSts";
    private static Context context;
    private static SPUtils instance = new SPUtils();

    public SPUtils getInstance(Context con) {
        context = con;
        return instance;
    }
}