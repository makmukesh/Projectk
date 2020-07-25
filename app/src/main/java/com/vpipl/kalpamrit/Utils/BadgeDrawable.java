package com.vpipl.kalpamrit.Utils;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.util.DisplayMetrics;

/**
 * Created by PC14 on 23-May-16.
 */
public class BadgeDrawable extends Drawable {
    int densityDpi;
    private Paint mBadgePaint;
    private Paint mBadgePaint1;
    private Paint mTextPaint;
    private Rect mTxtRect = new Rect();
    private String mCount = "";

    public BadgeDrawable(Context context) {

        DisplayMetrics metrics = context.getResources().getDisplayMetrics();
        densityDpi = (int) (metrics.density * 160f);

        float mTextSize = (densityDpi / 100) * 8;

        mBadgePaint = new Paint();
        mBadgePaint.setColor(Color.TRANSPARENT);
        mBadgePaint.setAntiAlias(true);
        mBadgePaint.setStyle(Paint.Style.FILL);
        mBadgePaint1 = new Paint();
        mBadgePaint1.setColor(Color.TRANSPARENT);
        mBadgePaint1.setAntiAlias(true);
        mBadgePaint1.setStyle(Paint.Style.FILL);

        mTextPaint = new Paint();
        mTextPaint.setColor(Color.WHITE);
        mTextPaint.setTypeface(Typeface.DEFAULT);
        mTextPaint.setTextSize(mTextSize);
        mTextPaint.setAntiAlias(true);
        mTextPaint.setTextAlign(Paint.Align.CENTER);
    }

    @Override
    public void draw(Canvas canvas) {
        Rect bounds = getBounds();
        float width = bounds.right - bounds.left;
        float height = bounds.bottom - bounds.top;
        // Position the badge in the top-right quadrant of the icon.

    /*Using Math.max rather than Math.min */
        float radius = ((Math.max(width, height) / 2)) / 2;
        float centerX = (width - radius) - radius + (densityDpi / 100);
        float centerY = (height - radius) - radius - (densityDpi / 100);

        if (mCount.length() <= 2) {
            // Draw badge bg_circle.
            canvas.drawCircle(centerX, centerY, radius + 3, mBadgePaint1);
            canvas.drawCircle(centerX, centerY, radius + 1, mBadgePaint);
        } else {
            canvas.drawCircle(centerX, centerY, radius + 4, mBadgePaint1);
            canvas.drawCircle(centerX, centerY, radius + 2, mBadgePaint);
        }
        // Draw badge count text inside the bg_circle.
        mTextPaint.getTextBounds(mCount, 0, mCount.length(), mTxtRect);
        float textHeight = mTxtRect.bottom - mTxtRect.top;
        float textY = 0;

        if (densityDpi <= 160)
            textY = centerY + (textHeight / (densityDpi / 10));
        else
            textY = centerY + (textHeight / (densityDpi / 20));

        if (mCount.length() > 2)
            canvas.drawText("99+", centerX, textY, mTextPaint);
        else
            canvas.drawText(mCount, centerX, textY, mTextPaint);
    }

    /*
     Sets the count (i.e notifications) to display.
      */
    public void setCount(String count) {
        mCount = count;
        // Only draw a badge if there are notifications.
        boolean mWillDraw = !count.equalsIgnoreCase("0");
        invalidateSelf();
    }

    @Override
    public void setAlpha(int alpha) {
        // do nothing
    }

    @Override
    public void setColorFilter(ColorFilter cf) {
        // do nothing
    }

    @Override
    public int getOpacity() {
        return PixelFormat.UNKNOWN;
    }
}