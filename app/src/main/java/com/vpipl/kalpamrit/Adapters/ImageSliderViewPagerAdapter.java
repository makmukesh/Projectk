package com.vpipl.kalpamrit.Adapters;

import android.content.Context;
import android.content.Intent;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.vpipl.kalpamrit.Home_Activity;
import com.vpipl.kalpamrit.Home_Slider_Web_Activity;
import com.vpipl.kalpamrit.R;
import com.vpipl.kalpamrit.Sponsor_genealogy_Activity_New;
import com.vpipl.kalpamrit.Utils.AppUtils;
import com.vpipl.kalpamrit.ViewPdf;


/**
 * Specifies the WelcomeScreen Item List functionality
 */
public class ImageSliderViewPagerAdapter extends PagerAdapter {
    private Context context;
    private LayoutInflater inflater;

    public ImageSliderViewPagerAdapter(Context con) {
        this.context = con;
    }

    @Override
    public int getCount() {
        return Home_Activity.imageSlider.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public Object instantiateItem(final ViewGroup container, final int position) {
        final ImageView swipeImageView;

        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View itemView = inflater.inflate(R.layout.home_swipeimage_layout, container, false);

        swipeImageView = itemView.findViewById(R.id.swipeImageView);

        try {
          AppUtils.loadSlidingImage(context, Home_Activity.imageSlider.get(position).get("Images"), swipeImageView);
            container.addView(itemView);

            swipeImageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //NavigateURL Home_Activity.imageSlider.get(position).get("NavigateURL")
                   // Intent intent = new Intent(context , Sponsor_genealogy_Activity_New.class);
                    if(Home_Activity.imageSlider.get(position).get("NavigateURL").equalsIgnoreCase("#")){
                    }
                    else {
                      //  Intent intent = new Intent(context, Home_Slider_Web_Activity.class);
                        Intent intent = new Intent(context, ViewPdf.class);
                        intent.putExtra("URL", Home_Activity.imageSlider.get(position).get("NavigateURL"));
                        context.startActivity(intent);
                    }
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
            AppUtils.showExceptionDialog(context);
        }

        return itemView;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((LinearLayout) object);
    }
}