package com.hodachop93.hohoda.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import com.hodachop93.hohoda.R;
import com.hodachop93.hohoda.model.HashTag;
import com.hodachop93.hohoda.utils.ValidateDataUtils;
import com.tokenautocomplete.TokenCompleteTextView;

/**
 * Created by Hop on 13/04/2016.
 */
public class HashTagEditText extends TokenCompleteTextView<HashTag> {
    public static final int DEFAULT_HASHTAG_TEXT_COLOR = 0xffffffff;

    private int mHashtagTextColor;

    //    private static final
    public HashTagEditText(Context context) {
        super(context);
        init();
    }

    public HashTagEditText(Context context, AttributeSet attrs) {
        super(context, attrs);

        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.HashTagEditText);

        mHashtagTextColor = a.getColor(R.styleable.HashTagEditText_hashtag_text_color, DEFAULT_HASHTAG_TEXT_COLOR);
        Log.d(TAG, "mHashtagTextColor: "+mHashtagTextColor);
        a.recycle();

        init();
    }

    public HashTagEditText(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.HashTagEditText, defStyle, 0);

        mHashtagTextColor = a.getColor(R.styleable.HashTagEditText_hashtag_text_color, DEFAULT_HASHTAG_TEXT_COLOR);
        Log.d(TAG, "mHashtagTextColor: "+mHashtagTextColor);
        a.recycle();

        init();
    }

    private void init() {
        setSplitChar(' ');
    }

    @Override
    protected View getViewForObject(HashTag object) {
        HashTagCard card = new HashTagCard(getContext());
        card.setTextColor(mHashtagTextColor);
        card.setText("#" + object.getName());


        return card;
    }

    @Override
    protected HashTag defaultObject(String completionText) {

        while (completionText.startsWith("#") && completionText.length() > 1) {
            completionText = completionText.substring(1, completionText.length());
        }

        if (ValidateDataUtils.isValidHashTag(completionText)) {
            return new HashTag(completionText);
        } else {
            return null;
        }
    }
}
