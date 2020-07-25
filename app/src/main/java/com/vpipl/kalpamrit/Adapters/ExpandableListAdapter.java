package com.vpipl.kalpamrit.Adapters;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.vpipl.kalpamrit.R;

import java.util.HashMap;
import java.util.List;

/**
 * Created by admin on 13-05-2017.
 */

public class ExpandableListAdapter extends BaseExpandableListAdapter {

    private Context _context;
    private List<String> _listDataHeader;

    // header titles
    // child data in format of header title, child title

    private HashMap<String, List<String>> _listDataChild;

    public ExpandableListAdapter(Context context, List<String> listDataHeader, HashMap<String, List<String>> listChildData) {
        this._context = context;
        this._listDataHeader = listDataHeader;
        this._listDataChild = listChildData;
    }

    @Override
    public Object getChild(int groupPosition, int childPosititon) {
        return this._listDataChild.get(this._listDataHeader.get(groupPosition)).get(childPosititon);
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public View getChildView(int groupPosition, final int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {

        final String childText = (String) getChild(groupPosition, childPosition);

        if (convertView == null) {
            LayoutInflater infalInflater = (LayoutInflater) this._context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.explist_child_item, null);
        }

        TextView txtListChild = convertView.findViewById(R.id.lblListItem);
        txtListChild.setText(childText);
        return convertView;
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return this._listDataChild.get(this._listDataHeader.get(groupPosition)).size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return this._listDataHeader.get(groupPosition);
    }

    @Override
    public int getGroupCount() {
        return this._listDataHeader.size();
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded,
                             View convertView, ViewGroup parent) {
        String headerTitle = (String) getGroup(groupPosition);

        if (convertView == null) {
            LayoutInflater infalInflater = (LayoutInflater) this._context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.explist_group_item, null);
        }

        TextView lblListHeader = convertView.findViewById(R.id.lblListHeader);
        lblListHeader.setText(headerTitle);

//        headerTitle = lblListHeader.getText().toString().trim();

        ImageView imageView4 = convertView.findViewById(R.id.imageView4);

        if (headerTitle.equalsIgnoreCase("Dashboard"))
            imageView4.setImageDrawable(_context.getResources().getDrawable(R.drawable.nav_icon_dashboard));
        else if (headerTitle.equalsIgnoreCase("New Joining"))
            imageView4.setImageDrawable(_context.getResources().getDrawable(R.drawable.nav_icon_distributor_registration));
        else if (headerTitle.equalsIgnoreCase("My Profile"))
            imageView4.setImageDrawable(_context.getResources().getDrawable(R.drawable.nav_icon_profile));
        else if (headerTitle.equalsIgnoreCase("KYC Documents"))
            imageView4.setImageDrawable(_context.getResources().getDrawable(R.drawable.nav_icon_kyc));
        else if (headerTitle.equalsIgnoreCase("My Network"))
            imageView4.setImageDrawable(_context.getResources().getDrawable(R.drawable.nav_icon_network));
        else if (headerTitle.equalsIgnoreCase("Documents"))
            imageView4.setImageDrawable(_context.getResources().getDrawable(R.drawable.nav_icon_document));
        else if (headerTitle.equalsIgnoreCase("My Incentives"))
            imageView4.setImageDrawable(_context.getResources().getDrawable(R.drawable.nav_icon_incentive));
        else if (headerTitle.equalsIgnoreCase("Account"))
            imageView4.setImageDrawable(_context.getResources().getDrawable(R.drawable.nav_icon_account));
        else if (headerTitle.equalsIgnoreCase("Logout"))
            imageView4.setImageDrawable(_context.getResources().getDrawable(R.drawable.nav_icon_logout));
        else if (headerTitle.equalsIgnoreCase("Repurchase Section"))
            imageView4.setImageDrawable(_context.getResources().getDrawable(R.drawable.nav_icon_purchase_summary));
        else if (headerTitle.equalsIgnoreCase("Wallet Section"))
            imageView4.setImageDrawable(_context.getResources().getDrawable(R.drawable.nav_icon_wallet));
        else if (headerTitle.equalsIgnoreCase("Shop Product"))
            imageView4.setImageDrawable(_context.getResources().getDrawable(R.drawable.nav_icon_shop));
        else if (headerTitle.equalsIgnoreCase("My Orders"))
            imageView4.setImageDrawable(_context.getResources().getDrawable(R.drawable.nav_icon_order_list));
        else if (headerTitle.equalsIgnoreCase("Home"))
            imageView4.setImageDrawable(_context.getResources().getDrawable(R.drawable.nav_icon_home));
        else if (headerTitle.equalsIgnoreCase("Enquiry/Complaint"))
            imageView4.setImageDrawable(_context.getResources().getDrawable(R.drawable.nav_icon_enquiry));

        else if (headerTitle.equalsIgnoreCase("Achievers Gallery"))
            imageView4.setImageDrawable(_context.getResources().getDrawable(R.drawable.nav_icon_kyc));

        else if (headerTitle.equalsIgnoreCase("Downloads"))
            imageView4.setImageDrawable(_context.getResources().getDrawable(R.drawable.icons_download_24));

        ImageView imageView3 = convertView.findViewById(R.id.imageView3);

         if (getChildrenCount(groupPosition) > 0) {
            imageView3.setImageResource(R.drawable.ic_expand_more_white);
            imageView3.setVisibility(View.VISIBLE);
        } else
            imageView3.setVisibility(View.GONE);


        if (isExpanded) {
            imageView3.setImageResource(R.drawable.ic_expand_less_white);
        } else {
        }

        return convertView;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }
}