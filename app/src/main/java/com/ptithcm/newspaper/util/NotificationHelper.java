package com.ptithcm.newspaper.util;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import androidx.core.app.NotificationCompat;

import com.ptithcm.newspaper.ui.activity.DetailActivity;

/**
 * Helper tạo thông báo cho tin tức mới.
 * Tạo notification channel "NEWS_CHANNEL" trên API 26+.
 */
public class NotificationHelper {

    private static final String CHANNEL_ID = "NEWS_CHANNEL";
    private static final String CHANNEL_NAME = "Tin tức mới";

    /**
     * Tạo notification channel (chỉ cần gọi 1 lần, gọi lại cũng không sao).
     */
    private static void createNotificationChannel(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    CHANNEL_NAME,
                    NotificationManager.IMPORTANCE_DEFAULT
            );
            channel.setDescription("Thông báo khi có tin tức mới");
            NotificationManager manager = context.getSystemService(NotificationManager.class);
            if (manager != null) {
                manager.createNotificationChannel(channel);
            }
        }
    }

    /**
     * Hiển thị thông báo tin tức mới.
     *
     * @param context Context ứng dụng
     * @param title   Tiêu đề bài viết
     * @param link    Link bài viết (dùng để mở DetailActivity)
     */
    public static void showNewsNotification(Context context, String title, String link) {
        createNotificationChannel(context);

        Intent intent = new Intent(context, DetailActivity.class);
        intent.putExtra("ARTICLE_LINK", link);
        intent.putExtra("ARTICLE_TITLE", title);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);

        PendingIntent pendingIntent = PendingIntent.getActivity(
                context,
                link.hashCode(),
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(android.R.drawable.ic_dialog_info)
                .setContentTitle("📰 Tin mới")
                .setContentText(title)
                .setAutoCancel(true)
                .setContentIntent(pendingIntent)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        if (manager != null) {
            manager.notify(link.hashCode(), builder.build());
        }
    }
}
