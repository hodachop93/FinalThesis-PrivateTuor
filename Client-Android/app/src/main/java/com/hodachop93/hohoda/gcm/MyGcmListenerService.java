package com.hodachop93.hohoda.gcm;

/**
 * Created by Hop on 12/04/2016.
 */

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.google.android.gms.gcm.GcmListenerService;
import com.hodachop93.hohoda.R;
import com.hodachop93.hohoda.activity.ChatActivity;
import com.hodachop93.hohoda.activity.CourseDetailActivity;
import com.hodachop93.hohoda.common.AppReferences;
import com.hodachop93.hohoda.model.Message;
import com.hodachop93.hohoda.utils.NotificationUtils;


/**
 * Created by Khanh Nguyen on 2/16/16.
 */
public class MyGcmListenerService extends GcmListenerService {
    private static final String TAG = "MyGcmListenerService";

    private static final int REQUEST_CODE_NOTIFICATION = 2000;

    private static final String FIELD_PUSH_TYPE = "push_type";
    private static final String FIELD_COURSE_ID = "course_id";


    private static final int PUSH_TYPE_POST_A_COURSE = 0;
    private static final int PUSH_TYPE_JOIN_A_COURSE = 1;
    public static final int PUSH_TYPE_COURSE_BE_CLOSED = 2;
    private static final int PUSH_TYPE_CHAT = 10;

    public static final String NOTIFICATION_CHAT_FOREGROUND = "NOTIFICATION_CHAT_FOREGROUND";

    private static int notificationId = 0;

    /**
     * Called when message is received.
     *
     * @param from SenderID of the sender.
     * @param data Data bundle containing message data as key/value pairs.
     *             For Set of keys use data.keySet().
     */
    @Override
    public void onMessageReceived(String from, Bundle data) {

        Log.d(TAG, data.toString());
        Log.d(TAG, from);
        int typePush = -1;
        try {
            typePush = Integer.parseInt(data.getString(FIELD_PUSH_TYPE));
        } catch (NumberFormatException ex) {
            Log.e(TAG, ex.getMessage());
        }
        if (typePush == PUSH_TYPE_CHAT) {
            processChatNotification(data);
        } else {
            showNotificationCourse(data);
        }

    }

    private void processChatNotification(Bundle data) {
        String conversationId = data.getString("conversation_id");
        String body = data.getString("body");
        String title = data.getString("title");
        String createdBy = data.getString("created_by");
        String content = data.getString("content");
        long createdAt = Long.parseLong(data.getString("created_at"));

        Message message = new Message(conversationId, createdBy, content, createdAt);
        if (NotificationUtils.isAppIsInBackground(getApplicationContext())) {
            //App is in background => show notification
            showNotificationMessage(title, body, createdBy);
        } else {
            //App is in foreground => broadcast the message
            Intent pushNotification = new Intent(NOTIFICATION_CHAT_FOREGROUND);
            pushNotification.putExtra("message", message);
            LocalBroadcastManager.getInstance(this).sendBroadcast(pushNotification);
        }

    }

    private void showNotificationMessage(String title, String body, String createdBy) {
        Intent intent = ChatActivity.getIntent(this, createdBy);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, notificationId, intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_ONE_SHOT);


        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(getNotificationIcon())
                .setContentTitle(title)
                .setContentText(body)
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent);

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(notificationId++, notificationBuilder.build());
    }

    /**
     * Create and show a simple notification containing the received GCM message.
     *
     * @param data GCM message received.
     */
    private void showNotificationCourse(Bundle data) {

/*        try {
            int unreadNotificationCount = Integer.valueOf(data.getString("total_unread_notification"));
            NotificationCountEventBus.Event event = new NotificationCountEventBus.Event(unreadNotificationCount);
            NotificationCountEventBus.getInstance().post(event);
        } catch (Exception ignored) {

        }*/

        // check the type_push from data and navigate to activity
        Intent intent = navigateNotification(data);

        PendingIntent pendingIntent = PendingIntent.getActivity(this, notificationId, intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_ONE_SHOT);


        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(getNotificationIcon())
                .setContentTitle(data.getString("title"))
                .setContentText(data.getString("body"))
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent);

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(notificationId++, notificationBuilder.build());
    }

    private Intent navigateNotification(Bundle data) {
        int pushType = Integer.parseInt(data.getString(FIELD_PUSH_TYPE));

        switch (pushType) {
            case PUSH_TYPE_POST_A_COURSE:
            case PUSH_TYPE_JOIN_A_COURSE:
            case PUSH_TYPE_COURSE_BE_CLOSED:
                return getCourseDetailActivityIntent(data.getString(FIELD_COURSE_ID));
            default:
                return null;
        }
    }

    private Intent getCourseDetailActivityIntent(String courseId) {
        Intent intent = CourseDetailActivity.getIntent(this, courseId);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        return intent;
    }

    private int getNotificationIcon() {
        boolean useWhiteIcon = (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP);
        return useWhiteIcon ? R.mipmap.ic_launcher : R.mipmap.ic_launcher;
    }
}