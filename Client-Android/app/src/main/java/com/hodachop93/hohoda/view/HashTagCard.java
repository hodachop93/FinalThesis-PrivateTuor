package com.hodachop93.hohoda.view;

import android.content.Context;
import android.graphics.drawable.GradientDrawable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.AppCompatTextView;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.hodachop93.hohoda.R;

/**
 * Created by Hop on 13/04/2016.
 */
public class HashTagCard extends AppCompatTextView {

    public HashTagCard(Context context) {
        super(context);
        init();
    }

    public HashTagCard(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public HashTagCard(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {

        int padding = getResources().getDimensionPixelSize(R.dimen.padding_vertical_rounded_hashtag);

        setPadding(padding * 2, padding, padding * 2, padding);

        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        setLayoutParams(params);

    }

    @Override
    public void setTextColor(int color) {
        super.setTextColor(color);

        GradientDrawable border = (GradientDrawable) ContextCompat.getDrawable(getContext(), R.drawable.border_hash_tag_card);
        border.setStroke(getResources().getDimensionPixelOffset(R.dimen._1sdp), color);
        setBackground(border);
    }
}
