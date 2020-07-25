package com.vpipl.kalpamrit.Adapters;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;
import android.widget.TextView;

import com.vpipl.kalpamrit.AppController;
import com.vpipl.kalpamrit.Change_Password_Activity;
import com.vpipl.kalpamrit.Home_Activity;
import com.vpipl.kalpamrit.KYCUploadDocument_Activity;
import com.vpipl.kalpamrit.Monthly_Incentive_Activity;
import com.vpipl.kalpamrit.Monthly_Incentive_Detail_Activity;
import com.vpipl.kalpamrit.ProductExpand_Activity;
import com.vpipl.kalpamrit.ProductListGrid_Activity;
import com.vpipl.kalpamrit.Profile_View_Activity;
import com.vpipl.kalpamrit.R;
import com.vpipl.kalpamrit.Register_Activity;
import com.vpipl.kalpamrit.Repurchase_BV_Detail;
import com.vpipl.kalpamrit.Repurchase_BV_Summary_Team_Activity;
import com.vpipl.kalpamrit.Sponsor_genealogy_Activity;
import com.vpipl.kalpamrit.Sponsor_team_details_Activity;
import com.vpipl.kalpamrit.Utils.AnimatedExpandableListView;
import com.vpipl.kalpamrit.Utils.AppUtils;
import com.vpipl.kalpamrit.Wallet_Request_Amount_Activity;
import com.vpipl.kalpamrit.Wallet_Request_Status_Report_Activity;
import com.vpipl.kalpamrit.Wallet_Transaction_Report_Activity;
import com.vpipl.kalpamrit.WelcomeLetter_Activity;
import com.vpipl.kalpamrit.model.ExpandList;
import com.vpipl.kalpamrit.model.SecondLevelExpandableListView;

import java.util.List;

/**
 * Created by PC14 on 21-May-16.
 */

public class ExpandList_Parent_Adapter extends AnimatedExpandableListView.AnimatedExpandableListAdapter {
    String TAG = "ExpandList_Parent_Adapter";

    private Context context;
    private List<ExpandList> expandList;
    private LayoutInflater inflater;
    private String comesFrom = "";

    public ExpandList_Parent_Adapter(Context context, List<ExpandList> expandList) {
        this.context = context;
        this.expandList = expandList;
        this.inflater = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.comesFrom = "ProductExpand";
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return expandList.get(groupPosition).getExpandList().get(childPosition);
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public View getRealChildView(final int mainGroupPosition, final int mainchildPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        if (AppUtils.showLogs)
            Log.v("TAG", "mainGroupPosition : " + mainGroupPosition + " childPosition : " + mainchildPosition);

        ExpandList expandChildList = (ExpandList) getChild(mainGroupPosition, mainchildPosition);
        final SecondLevelExpandableListView secondLevelELV = new SecondLevelExpandableListView(context);
        secondLevelELV.setAdapter(new ExpandList_Child_Adapter(context, expandChildList, comesFrom));
        secondLevelELV.setGroupIndicator(null);

        secondLevelELV.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {
            @Override
            public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id) {
                if (expandList.get(mainGroupPosition).getExpandList() != null && expandList.get(mainGroupPosition).getExpandList().size() > 0) {
                    if (expandList.get(mainGroupPosition).getExpandList().get(mainchildPosition).getExpandList() != null && expandList.get(mainGroupPosition).getExpandList().get(mainchildPosition).getExpandList().size() == 0) {
                        moveToGroup(mainGroupPosition, mainchildPosition);
                    }
                }

                return false;
            }
        });

        secondLevelELV.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
                moveToGroupChild(mainGroupPosition, mainchildPosition, childPosition, expandList.get(mainGroupPosition).getExpandList().get(mainchildPosition).getExpandList().get(childPosition).getName());

                if (AppUtils.showLogs)
                    Log.v("TAG", "groupPosition : " + groupPosition + " childPosition : " + childPosition);
                return false;
            }
        });

        return secondLevelELV;
    }

    @Override
    public int getRealChildrenCount(int groupPosition) {
        return expandList.get(groupPosition).getExpandList().size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return expandList.get(groupPosition);
    }

    @Override
    public int getGroupCount() {
        return expandList.size();
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        ExpandList expandList1 = (ExpandList) getGroup(groupPosition);
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.expand_row_first, null);
        }

        TextView title = convertView.findViewById(R.id.title);
        TextView icon_right = convertView.findViewById(R.id.icon_right);

        if (expandList.get(groupPosition).getExpandList().size() == 0) {
            icon_right.setText("");
        } else {
            if (isExpanded) {
                icon_right.setText("-");
            } else {
                icon_right.setText("+");
            }
        }

        title.setText(expandList1.getName());
        return convertView;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }

    private void moveToGroup(int mainGroupPosition, int groupPosition) {
        try {
            // this case is used for both comes user like navigation and product sxpand page because it
            // only major to move both on product list page. but only check that it will shop page.

            if (!expandList.get(mainGroupPosition).getExpandList().get(groupPosition).getId().equals("")) {
                Home_Activity.drawer.closeDrawer(Home_Activity.navigationView);
                Intent intent = new Intent(context, ProductListGrid_Activity.class);
                intent.putExtra("Type", "" + expandList.get(mainGroupPosition).getExpandList().get(groupPosition).getType());
                intent.putExtra("categoryID", "" + expandList.get(mainGroupPosition).getExpandList().get(groupPosition).getId());
                context.startActivity(intent);
                // (Activity)context).//overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
            } else {
                AppUtils.showExceptionDialog(context);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void moveToGroupChild(int mainGroupPosition, int groupPosition, int groupChildPosition, String pageName) {
        try {
            if (comesFrom.equals("Drawer")) {
                switch (pageName) {
                    case "Sponsor Genealogy":
                        Home_Activity.drawer.closeDrawer(Home_Activity.navigationView);
                        context.startActivity(new Intent(context, Sponsor_genealogy_Activity.class));
                        //((Activity) context).//overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                        break;
                    case "Sponsor Team Details":
                        Home_Activity.drawer.closeDrawer(Home_Activity.navigationView);
                        context.startActivity(new Intent(context, Sponsor_team_details_Activity.class).putExtra("Action", "Sponsor"));
                        //((Activity) context).//overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                        break;
                    case "My Direct Members":
                        Home_Activity.drawer.closeDrawer(Home_Activity.navigationView);
                        context.startActivity(new Intent(context, Sponsor_team_details_Activity.class).putExtra("Action", "Direct"));
                        //((Activity) context).//overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                        break;
                    case "Welcome Letter":
                        Home_Activity.drawer.closeDrawer(Home_Activity.navigationView);
                        context.startActivity(new Intent(context, WelcomeLetter_Activity.class));
                        //((Activity) context).//overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                        break;
                    case "My Repurchase BV Detail":
                        Home_Activity.drawer.closeDrawer(Home_Activity.navigationView);
                        context.startActivity(new Intent(context, Repurchase_BV_Detail.class));
                        //((Activity) context).//overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                        break;
                    case "Team Repurchase BV Summary":
                        Home_Activity.drawer.closeDrawer(Home_Activity.navigationView);
                        context.startActivity(new Intent(context, Repurchase_BV_Summary_Team_Activity.class));
                        //((Activity) context).//overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                        break;
                    case "Request For Wallet Amount":
                        Home_Activity.drawer.closeDrawer(Home_Activity.navigationView);
                        context.startActivity(new Intent(context, Wallet_Request_Amount_Activity.class));
                        //((Activity) context).//overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                        break;
                    case "Wallet Request Report":
                        Home_Activity.drawer.closeDrawer(Home_Activity.navigationView);
                        context.startActivity(new Intent(context, Wallet_Request_Status_Report_Activity.class));
                        //((Activity) context).//overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                        break;
                    case "Wallet Transaction Report":
                        Home_Activity.drawer.closeDrawer(Home_Activity.navigationView);
                        context.startActivity(new Intent(context, Wallet_Transaction_Report_Activity.class));
                        //((Activity) context).//overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                        break;
                    case "Change Password":
                        Home_Activity.drawer.closeDrawer(Home_Activity.navigationView);
                        context.startActivity(new Intent(context, Change_Password_Activity.class));
                        // ((Activity) context).//overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                        break;
                    case "Upload KYC Documents":
                        Home_Activity.drawer.closeDrawer(Home_Activity.navigationView);
                        context.startActivity(new Intent(context, KYCUploadDocument_Activity.class));
                        //((Activity) context).//overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                        break;
                    case "View Profile":
                        Home_Activity.drawer.closeDrawer(Home_Activity.navigationView);
                        context.startActivity(new Intent(context, Profile_View_Activity.class));
                        //((Activity) context).//overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                        break;
                    case "New Joining":
                        Home_Activity.drawer.closeDrawer(Home_Activity.navigationView);
                        context.startActivity(new Intent(context, Register_Activity.class));
                        //((Activity) context).//overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                        break;
                    case "Monthly Incentive":
                        Home_Activity.drawer.closeDrawer(Home_Activity.navigationView);
                        context.startActivity(new Intent(context, Monthly_Incentive_Activity.class));
                        //((Activity) context).//overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                        break;
                    case "Monthly Incentive Detail Report":
                        Home_Activity.drawer.closeDrawer(Home_Activity.navigationView);
                        context.startActivity(new Intent(context, Monthly_Incentive_Detail_Activity.class));
                        //((Activity) context).//overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                        break;
                    default:
                        Boolean isExpands = false;

                        for (int j = 0; j < AppController.category3.size(); j++) {
                            if (expandList.get(mainGroupPosition).getExpandList().get(groupPosition).getExpandList().get(groupChildPosition).getId().equals(AppController.category3.get(j).get("CID"))) {
                                isExpands = true;
                            }
                        }

                        if (isExpands) {

                            Home_Activity.drawer.closeDrawer(Home_Activity.navigationView);
                            Intent intent = new Intent(context, ProductExpand_Activity.class);
                            intent.putExtra("HID", "" + expandList.get(mainGroupPosition).getExpandList().get(groupPosition).getId());
                            intent.putExtra("CID", "" + expandList.get(mainGroupPosition).getExpandList().get(groupPosition).getExpandList().get(groupChildPosition).getId());
                            context.startActivity(intent);
                            // ((Activity) context).//overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                        } else {
                            Home_Activity.drawer.closeDrawer(Home_Activity.navigationView);
                            Intent intent = new Intent(context, ProductListGrid_Activity.class);
                            intent.putExtra("Type", "" + expandList.get(mainGroupPosition).getExpandList().get(groupPosition).getExpandList().get(groupChildPosition).getType());
                            intent.putExtra("categoryID", "" + expandList.get(mainGroupPosition).getExpandList().get(groupPosition).getExpandList().get(groupChildPosition).getId());
                            context.startActivity(intent);
                            //  ((Activity)context).//overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                        }
                        break;
                }
            } else {

                if (!expandList.get(mainGroupPosition).getExpandList().get(groupPosition).getExpandList().get(groupChildPosition).getId().equals("")) {

                    Home_Activity.drawer.closeDrawer(Home_Activity.navigationView);
                    Intent intent = new Intent(context, ProductListGrid_Activity.class);
                    intent.putExtra("Type", "" + expandList.get(mainGroupPosition).getExpandList().get(groupPosition).getExpandList().get(groupChildPosition).getType());
                    intent.putExtra("categoryID", "" + expandList.get(mainGroupPosition).getExpandList().get(groupPosition).getExpandList().get(groupChildPosition).getId());
                    context.startActivity(intent);
                    // ((Activity)context).//overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                } else {
                    AppUtils.showExceptionDialog(context);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}