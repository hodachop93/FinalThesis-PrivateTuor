package com.hodachop93.hohoda.utils;

import com.hodachop93.hohoda.common.ApplicationConstants;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by hopho on 13/04/2016.
 */
public class DateUtils {

    public static String formatDate(SimpleDateFormat simpleDateFormat, Date date) {
        return simpleDateFormat.format(date);
    }

    /**
     * Format "MMM dd, yyyy, hh:mm a"
     *
     * @param date
     * @return
     */
    public static String formatDateCourse(Date date) {
        SimpleDateFormat sdf = new SimpleDateFormat(ApplicationConstants.DATE_FORMAT_COURSE);
        return sdf.format(date);
    }

    /**
     * Format "MMM dd, yyyy, hh:mm a"
     *
     * @param timeStamp
     * @return
     */
    public static String formatDateCourse(long timeStamp) {
        if (timeStamp == 0) {
            return null;
        }
        Date date = new Date(timeStamp);
        SimpleDateFormat sdf = new SimpleDateFormat(ApplicationConstants.DATE_FORMAT_COURSE);
        return sdf.format(date);
    }

    public static String formatDateMessage(long timeStamp) {
        Date date = new Date(timeStamp);
        SimpleDateFormat sdf = new SimpleDateFormat(ApplicationConstants.DATE_FORMAT_MESSAGE);
        return sdf.format(date);
    }

    public static String getTimeAgo(long timeStamp) {
        // Converting timestamp into x ago format
        return android.text.format.DateUtils.getRelativeTimeSpanString(timeStamp, System.currentTimeMillis(), 0).toString();
    }
}
