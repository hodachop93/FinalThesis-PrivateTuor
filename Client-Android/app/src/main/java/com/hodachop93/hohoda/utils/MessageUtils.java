package com.hodachop93.hohoda.utils;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;

import com.afollestad.materialdialogs.MaterialDialog;
import com.hodachop93.hohoda.R;


public class MessageUtils {

    public static void showInformationMessage(Context context, @StringRes int titleId, @StringRes int messageId) {
        showMessageWithoutCallback(context, titleId, messageId, R.string.label_ok);
    }

    public static void showInformationMessage(Context context, String title, String message) {
        showMessageWithoutCallback(context, title, message, context.getString(R.string.label_ok));
    }

    public static void showConfirmMessage(Context context, String title, String message,
                                          @Nullable MaterialDialog.SingleButtonCallback callback) {
        showMessageWithCallback(context, title, message, context.getString(R.string.label_ok),
                context.getString(R.string.label_cancel), callback);
    }

    public static void showConfirmMessage(Context context, @StringRes int titleId, @StringRes int messageId,
                                          @Nullable MaterialDialog.SingleButtonCallback callback) {
        showMessageWithCallback(context, titleId, messageId, R.string.label_ok,
                R.string.label_cancel, callback);
    }

    public static void showMessageWithCallback(Context context, @StringRes int titleId, @StringRes int messageId, @StringRes int positiveTextId,
                                               @StringRes int negativeTextId, @Nullable MaterialDialog.SingleButtonCallback callback) {
        String title = context.getString(titleId);
        String message = context.getString(messageId);
        String positiveText = context.getString(positiveTextId);
        String negativeText = context.getString(negativeTextId);

        showMessageWithCallback(context, title, message, positiveText, negativeText, callback);
    }

    public static void showMessageWithCallback(Context context, String title, String message, String positiveText,
                                               String negativeText, @Nullable MaterialDialog.SingleButtonCallback callback) {
        MaterialDialog.Builder builder = new MaterialDialog.Builder(context)
                .title(title)
                .content(message)
                .positiveText(positiveText)
                .negativeText(negativeText);
        if (callback != null) {
            builder.onPositive(callback);
        }
        builder.build().show();
    }

    public static void showMessageWithoutCallback(Context context, String title, String message, String positiveText) {
        MaterialDialog.Builder builder = new MaterialDialog.Builder(context)
                .title(title)
                .content(message)
                .positiveText(positiveText);

        builder.build().show();
    }

    public static void showMessageWithoutCallback(Context context, @StringRes int titleId, @StringRes int messageId,
                                                  @StringRes int positiveTextId) {
        MaterialDialog.Builder builder = new MaterialDialog.Builder(context)
                .title(titleId)
                .content(messageId)
                .positiveText(positiveTextId);

        builder.build().show();
    }
}
