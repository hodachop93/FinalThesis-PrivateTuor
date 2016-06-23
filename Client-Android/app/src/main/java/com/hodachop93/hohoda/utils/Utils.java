package com.hodachop93.hohoda.utils;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.v4.content.ContextCompat;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.util.Base64;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

import com.hodachop93.hohoda.R;
import com.hodachop93.hohoda.api.ApiClient;
import com.hodachop93.hohoda.model.Course;
import com.hodachop93.hohoda.model.HashTag;
import com.hodachop93.hohoda.view.RoundedBackgroundSpan;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.Locale;

/**
 * Created by Hop on 07/03/2016.
 */
public final class Utils {
    private static String BASE_URL = "http://192.168.1.10:8080/";

    public static String getUserCountry(Context context) {
        Locale locale = context.getResources().getConfiguration().locale;
        return locale.toString();
    }

    public static String convertUrlIfUsingLocalhost(String url) {
        String result = null;
        if (url != null && url.contains("http://localhost:8080/")) {
            result = url.replace("http://localhost:8080/", BASE_URL);
        }
        return (result != null) ? result : url;
    }


    public static void hideSoftKeyboard(Activity activity) {
        if (activity != null) {
            InputMethodManager inputMethodManager = (InputMethodManager) activity
                    .getSystemService(Activity.INPUT_METHOD_SERVICE);
            if (activity.getCurrentFocus() != null
                    && activity.getCurrentFocus().getWindowToken() != null) {
                inputMethodManager.hideSoftInputFromWindow(activity.getCurrentFocus()
                        .getWindowToken(), 0);
                // activity.getCurrentFocus().clearFocus();
            }
        }
    }

    public static void setupUIForAutoHideKeyboard(final Activity activity, View view) {
        //Set up touch listener for non-text box views to hide keyboard.
        if (!(view instanceof EditText)) {
            view.setOnTouchListener(new View.OnTouchListener() {
                public boolean onTouch(View v, MotionEvent event) {
                    hideSoftKeyboard(activity);
                    return false;
                }
            });
        }

        //If a layout container, iterate over children and seed recursion.
        if (view instanceof ViewGroup) {
            for (int i = 0; i < ((ViewGroup) view).getChildCount(); i++) {
                View innerView = ((ViewGroup) view).getChildAt(i);
                setupUIForAutoHideKeyboard(activity, innerView);
            }
        }
    }

    /**
     * Convert a hashtags list to a rounded hashtags list
     *
     * @param textView The TextView to be filled
     * @param hashTags The string hashtags list
     * @param color    The color of hashtags
     * @return A rounded hashtags text
     */
    public static void fillHashtagList(TextView textView, List<HashTag> hashTags, int color) {

        if (hashTags.isEmpty()) {
            textView.setVisibility(View.GONE);
            return;
        } else {
            textView.setVisibility(View.VISIBLE);
        }

        SpannableStringBuilder stringBuilder = new SpannableStringBuilder();

        String between = ""; //spacing between two hash tag
        String paddingHorizontal = "   "; //padding horizontal each hashtag

        for (HashTag tag : hashTags) {
            stringBuilder.append(between);
            if (between.length() == 0) between = "  ";
            String thisTag = paddingHorizontal + "#" + tag.getName() + paddingHorizontal;
            stringBuilder.append(thisTag);
            stringBuilder.setSpan(new RoundedBackgroundSpan(textView.getContext(), color), stringBuilder.length() - thisTag.length(),
                    stringBuilder.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        }

        textView.setText(stringBuilder);
        if (hashTags.size() == 1)
            textView.append(" ");

        if (Utils.isLollipopOrHigher()) {
            textView.append("\n ");
            textView.setPadding(textView.getPaddingLeft(), textView.getPaddingTop(), textView.getPaddingRight(), -textView.getPaddingTop());
        }
    }

    public static boolean isLollipopOrHigher() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP;
    }

    /**
     * Change background course status
     *
     * @param tvCourseStatus  The course status text view
     * @param courseStatusInt The job status string
     */
    public static void changeBackgroundCourseStatus(Context context, TextView tvCourseStatus, int courseStatusInt) {

        Drawable drawable = tvCourseStatus.getBackground();
        int color = 0;

        switch (courseStatusInt) {
            case Course.COURSE_PENDING:
                color = ContextCompat.getColor(context, R.color.bg_course_status_pending);
                break;
            case Course.COURSE_OPENED:
                color = ContextCompat.getColor(context, R.color.bg_course_status_opened);
                break;
            case Course.COURSE_CLOSED:
                color = ContextCompat.getColor(context, R.color.bg_course_status_closed);
        }
        drawable.setColorFilter(color, PorterDuff.Mode.MULTIPLY);
    }

    public static boolean checkEditTextNotEmpty(EditText edt) {
        boolean result;
        if (edt.getText() == null || edt.getText().toString().isEmpty()) {
            result = false;
        } else {
            result = true;
        }
        return result;
    }

    public static void appendColoredText(TextView tv, String text, int color) {
        int start = tv.getText().length();
        tv.append(text);
        int end = tv.getText().length();

        Spannable spannableText = (Spannable) tv.getText();
        spannableText.setSpan(new ForegroundColorSpan(color), start, end, 0);
    }

    public static void printKeyHashes(Context context) {
        PackageInfo info;
        try {
            info = context.getPackageManager().getPackageInfo("com.hodachop93.hohoda", PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md;
                md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                String something = new String(Base64.encode(md.digest(), 0));
                Log.d("KEY_HASH", something);
            }
        } catch (PackageManager.NameNotFoundException e1) {
            Log.e("name not found", e1.toString());
        } catch (NoSuchAlgorithmException e) {
            Log.e("no such an algorithm", e.toString());
        } catch (Exception e) {
            Log.e("exception", e.toString());
        }
    }
}
