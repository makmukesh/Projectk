package com.vpipl.kalpamrit.Utils;

import android.content.Context;

import com.vpipl.kalpamrit.AppController;
import com.vpipl.kalpamrit.R;

/**
 * Created by admin on 01-05-2017.
 */
public class QueryUtils {

    public static String methodMemberLoginOnPortal = "MemberLoginOnPortal";
    public static String methodUserLoginOnPortal = "UserLoginOnPortal";
    public static String methodCheckSponsor = "CheckSponsor";
    public static String methodToChangePassword = "ChangePassword";
    public static String methodToGetUserProfile = "ViewProfile";
    public static String methodToUpdateUserProfile = "UpdateProfile";
    public static String methodMaster_FillState = "Master_FillState";
    public static String methodMaster_FillBank = "Master_FillBank";
    public static String methodGet_BankDetail = "Get_BankDetail";

    public static String methodUploadImages = "UploadKYCImages";
    public static String methodGetImages = "ReadKYCImages";
    public static String methodWelcomeLetter = "WelcomeLetter";
    public static String methodToGetSponsorTeamDetail = "SponsorTeamDetail";
    public static String methodToGetMyDirectMembers = "MyDirectMembers";
    public static String methodToGetMonthlyIncentive = "MonthlyIncentive";
    public static String methodToGetMonthlyIncentiveDetailReport = "MonthlyIncentiveDetailReport";
    public static String methodToGetWalletTransactionReport = "WalletTransactionDetail";
    public static String methodToGetProductWalletTransactionDetail = "ProductWalletTransactionDetail";
    public static String methodToGetWalletBalance = "GetAvailableWalletBalance";
    public static String methodToRequestWalletAmount = "RequestForWalletAmount";
    public static String methodToGetWallet_Request_Status_Report = "WalletRequestReport";
    public static String methodToGetTeamRepurchaseSummary = "TeamRepurchaseBVSummary";
    public static String methodToGetMyRepurchaseBVDetail = "MyRepurchaseBVDetail";
    public static String methodToGetRepurchaseBVSummaryDetail = "RepurchaseBVSummaryDetail";
    public static String methodToGetDashboardDetail = "Dashboard";
    public static String methodToForgetPasswordMember = "ForgotPassword";
    public static String methodToForgetPasswordUser = "UserForgotPassword";
    public static String methodToApplicationStatus = "ApplictionStatus";
    public static String methodVerificationMobileNoJson = "VerificationMobileNo";
    public static String methodtoGuestUserReg = "GuestUserReg";
    ////////////////////////////////////////
    public static String methodtoSendOTP = "SendGoogleOTP";
    public static String methodHomePageSlider = "HomePageSlider";
    public static String methodToGetProductDetail = "Item_Details";
    public static String methodHotSellingProducts = "HotSellingProducts";
    public static String methodtoGetDrawerMenuItems = "DashBoardMenu";
    public static String methodToGetCheckOutDeliveryAddress = "CheckOutDeliveryAddress";
    public static String methodToGetSearchProductsList = "SearchProductListKeyworks";
    public static String methodToAddCheckOutDeliveryAddress = "CheckOutAddNewAddress";
    public static String methodToGetViewOrdersList = "MyOrders";
    public static String methodToGetViewOrdersDetails = "MyOrderDetils";
    public static String methodToGetVersion = "CheckVersionInfo";
    public static String methodAppAllCategory = "AppMenuCategory_AllCategory";
    public static String methodToGetProductList = "ProductView_ProductCount";
    public static String methodToAddOrder_OnlinePayment = "AddOrder_OnlinePayment";
    public static String methodToWalletToBankTransferDetail = "WalletToBankTransferDetail";

    public static String getViewgenealogyURL(Context con) {
        String url = "";
        try {
            url = AppUtils.ViewgenealogyURL() + AppController.getSpUserInfo().getString(SPUtils.USER_FORM_NUMBER, "");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return url;
    }

    public static String methodToWebEncryption = "EncryptData";


    public static String methodToGetRepurchaseBillSummary = "RepurchaseBillSummary";
    public static String methodToGetNewlyProducts = "NewlyProducts";
    public static String methodToGetTDSReport = "TDSReport";

    public static String methodHomePageSectionjustdowntoSlider = "HomePageSectionjustdowntoSlider";


    public static String methodtoSendJoiningOTP = "SendGoogleOTP";
    public static String methodToCheck_OTPPermission = "CheckJoiningOTPPremission";


    public static String methodMaster_FillDepartmentQuery = "EnquiryComplaintDepartment";
    public static String methodToSubmitQuery = "RegisterEnquiryComplaint";
    public static String methodToOpenQueriesReport = "EnquiryComplaintDetails";
    public static String methodToClosedQueriesReport = "ViewClosedQueries";
    public static String methodToOpenQuerieConversation = "EnquiryComplaintQuerieDetails";

    public static String methodToThanksPage = "ThanksPage";
    public static String methodToSendSMSForOrder = "SendSMSForOrder";
    public static String methodToSendMailForOrder = "SendMailForOrder";

    public static String methodToWeightConfig = "WeightConfig";

    //Dummy...//
    public static String methodToPurchaseProducts = "RequestEPin";
    public static String methodToLoadProducts = "PinRequestPackage";
    public static String methodMaster_FillCompany = "Master_FillState";
    public static String methodMaster_FillFranchisee = "Master_FillState";
    //...Dummy//

    public static String methodToGetProductStock = "NearestProductStockDetails";

    public static String methodToProductWalletBalance = "ProductWalletBalance";
    public static String methodToBuniessCard = "BuniessCard";

    public static String methodToGetViewachiversList = "AllAchievers";
    public static String methodToGetRewardsNameList = "RewardsNameList";
    public static String methodToLoad_ThoughtOfTheDay = "Load_ThoughtOfTheDay";
    public static String methodToSendOTPOnUpdateProfile = "SendGoogleOTP";
    public static String methodToNewJoiningNew = "NewJoiningNew";

    // Code added by mukesh 28-01-2019 07:38 PM
    public static String methodToDashboardrepurchaseBVDetail = "DashboardrepurchaseBVDetail";

    // Paytm Payment gateway 29-06-2019 02:48 PM
    public static String methodToAddOrder_OnlinePaymentPayTM = "AddOrder_OnlinePaymentPayTM";
    public static String methodToCartProduct = "CartProduct";

    //Api Added 24-10-2019 07:20 PM
    public static String methodToCheckPincode = "CheckPincode";
    // Code added by mukesh 02-11-2019 02:20 PM
    public static String methodToReferFriend = "ShareUrl";
    public static String methodToNewJoiningNew2 = "NewJoiningNew2";
    // Code added by mukesh 08-11-2019 05:54 PM
    public static String methodToAddOrder_Wallet = "AddOrder_Wallet";

    public static String methodCheckUniqueMobile = "CheckUnique_ValidateMobileNo";
    public static String methodCheckUniquePan = "CheckUnique_ValidatePanNo";
    public static String methodToGetEpinRequestReport = "PinRequestDetailsDateWise";
    public static String methodToAllProductImages = "ProductImages";
    public static String methodtoNewJoining = "NewJoining";

    // Code added by mukesh 21-01-2019 05:42 PM
    public static String methodtoNewSponsorGenealogy = "NewSponsorGenealogy";

//    public static String methodToUpdateToDBCart = "UpdateToDBCart";
//    public static String methodToGetCartTotalCount = "GetCartTotalCount";
//    public static String methodToAddToDBCart = "AddToDBCart";
//    public static String methodToGetBothCartDetail = "GetBothCartDetail";
//    public static String methodToGetClearAllCart = "ClearAllCart";
//    public static String methodToGetDeleteFromDBCart = "DeleteFromDBCart";
    // Code added by mukesh 19-05-2020 12:33 PM
    public static String methodtoDasboard_New = "Dasboard_New";

    // Code added by mukesh 02-07-2020 11:14 PM for Proposer Id system
    public static String methodtoNewJoiningNew3 = "NewJoiningNew3";

    // Code added by mukesh 03-07-2020 05:09 PM for Website Download section
    public static String methodtoSelectDownLoadFileList = "SelectDownLoadFileList";

    // Code added by mukesh 02-07-2020 11:14 PM for Reconfig Proposer Id system
    public static String methodtoCheckProposer = "CheckProposer";
    public static String methodtoNewJoiningNew4 = "NewJoiningNew4";
    // Code added by mukesh 14-07-2020 12:45 PM for popup system
    public static String methodGetPopImage = "";

    // Code added by mukesh 23-07-2020 05:47 PM PM for change proposer system
    public static String methodCheckProposerWithOTP = "CheckProposerWithOTP";
    public static String methodChangeProposer = "ChangeProposer";
    public static String methodProposerGenealogy = "ProposerGenealogy";
}