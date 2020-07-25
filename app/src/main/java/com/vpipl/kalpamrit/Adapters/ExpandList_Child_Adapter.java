package com.vpipl.kalpamrit.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.vpipl.kalpamrit.R;
import com.vpipl.kalpamrit.Utils.AnimatedExpandableListView;
import com.vpipl.kalpamrit.model.ExpandList;

/**
 * Created by PC14 on 21-May-16.
 */
public class ExpandList_Child_Adapter extends AnimatedExpandableListView.AnimatedExpandableListAdapter {

    private ExpandList expandList;
    private LayoutInflater inflater;
    private String comesFrom = "";

    public ExpandList_Child_Adapter(Context context, ExpandList expandList, String comesFrom) {
        Context context1 = context;
        this.expandList = expandList;
        this.inflater = (LayoutInflater) context1.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.comesFrom = comesFrom;
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return expandList.getExpandList().get(childPosition);
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public View getRealChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        ExpandList expandList = (ExpandList) getChild(0, childPosition);
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.expand_row_second, null);
            TextView title = convertView.findViewById(R.id.title);
            title.setText(expandList.getName());
        }
        return convertView;
    }

    @Override
    public int getRealChildrenCount(int groupPosition) {
        return expandList.getExpandList().size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return expandList;
    }

    @Override
    public int getGroupCount() {
        return 1;
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.expand_row_third, null);
        }

        TextView title = convertView.findViewById(R.id.title);
        TextView icon_right = convertView.findViewById(R.id.icon_right);
//        ImageView icon = (ImageView) convertView.findViewById(R.id.icon);

        title.setText(expandList.getName());

        try {
            if (!comesFrom.equals("Drawer")) {
//                   icon.setVisibility(View.VISIBLE);
//
//                   AppUtils.loadRoundAppImage(context,expandList.getIcon(), icon);
                //AppController.getDrawerImageLoader().displayImage(expandList.getIcon(), icon, AppController.getDrawerImageLoaderOptions());
//            } else {
//                   icon.setVisibility(View.GONE);
                RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT));
                params.setMargins(65, 0, 0, 0);
                title.setLayoutParams(params);
            }

            if (expandList.getExpandList() == null || expandList.getExpandList().size() == 0) {
                icon_right.setText("");
            } else {
                if (isExpanded) {
                    icon_right.setText("-");
                } else {
                    icon_right.setText("+");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

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
}