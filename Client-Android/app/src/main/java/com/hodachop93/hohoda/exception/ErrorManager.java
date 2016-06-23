package com.hodachop93.hohoda.exception;

import android.content.Context;

import com.hodachop93.hohoda.R;
import com.hodachop93.hohoda.model.HohodaResponse;
import com.hodachop93.hohoda.utils.MessageUtils;

import java.util.Arrays;
import java.util.List;

import retrofit2.Response;

/**
 * Created by Hop on 14/03/2016.
 */
public class ErrorManager {
/*    public static final List<String> USER_EXCEPTION_LIST;
    public static String USER_EXCEPTIONS[] = {ErrorFactory.CODE_USER_NOT_FOUND, ErrorFactory.CODE_USER_BLOCKED,
            ErrorFactory.CODE_USER_TERMINATED, ErrorFactory.CODE_USER_CREATED_NOT_ACTIVATED};

    static {
        USER_EXCEPTION_LIST = Arrays.asList(USER_EXCEPTIONS);
    }*/

    public static void handleApplicationException(Context context, Response<?> response) {
        HohodaResponse<?> hohodaResponse;
        if (!(response.body() instanceof HohodaResponse<?>))
            return;

        hohodaResponse = (HohodaResponse<?>) response.body();

        MessageUtils.showInformationMessage(context, context.getString(R.string.label_error), hohodaResponse.getMessageInfo());
    }

    public static void handleErroneousException(Context context, Response<?> response) {
        MessageUtils.showInformationMessage(context, context.getString(R.string.label_error), response.message());
    }

    public static void handleNetworkException(Context context) {
        MessageUtils.showInformationMessage(context, context.getString(R.string.label_error), context.getString(R.string.label_network_error));
    }
}
