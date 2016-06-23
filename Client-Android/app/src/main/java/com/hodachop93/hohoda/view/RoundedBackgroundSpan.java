package com.hodachop93.hohoda.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.GradientDrawable;
import android.support.v4.content.ContextCompat;
import android.text.style.ReplacementSpan;

import com.hodachop93.hohoda.HohodaApplication;
import com.hodachop93.hohoda.R;


/**
 * Created by hodachop93 on 20/01/2016.
 */
public class RoundedBackgroundSpan extends ReplacementSpan {
    private static final int DEFAULT_COLOR = ContextCompat.getColor(HohodaApplication.getInstance(), R.color.white);

    private int color = DEFAULT_COLOR;
    private static GradientDrawable drawable;
    private int mWidth;
    private int paddingVertical;
    private int lineSpacingExtra;
    private Context mContext;

    public RoundedBackgroundSpan(Context context, int color) {
        super();
        mContext = context;
        if (drawable == null)
            drawable = (GradientDrawable) ContextCompat.getDrawable(context, R.drawable.border_hashtag);
        paddingVertical = context.getResources().getDimensionPixelSize(R.dimen.padding_vertical_rounded_hashtag);
        lineSpacingExtra = context.getResources().getDimensionPixelSize(R.dimen.line_spacing_extra_rounded_hashtag);
        this.color = color;
    }

    @Override
    public void draw(Canvas canvas, CharSequence text, int start, int end, float x, int top, int y, int bottom, Paint paint) {
        drawable.setBounds((int) x, top - paddingVertical, (int) (x + mWidth), bottom + paddingVertical - lineSpacingExtra );
        drawable.setStroke(mContext.getResources().getDimensionPixelSize(R.dimen._1sdp), color);
        drawable.draw(canvas);
        paint.setColor(color);
        canvas.drawText(text, start, end, x, y, paint);
    }

    @Override
    public int getSize(Paint paint, CharSequence text, int start, int end, Paint.FontMetricsInt fm) {
        mWidth = (int) paint.measureText(text, start, end);
        return mWidth;
    }

}
