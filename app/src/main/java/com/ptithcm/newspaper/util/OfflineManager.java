package com.ptithcm.newspaper.util;

import android.content.Context;
import android.content.SharedPreferences;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Quản lý lưu bài viết offline vào bộ nhớ trong.
 * Sử dụng SharedPreferences "OFFLINE_INDEX" để theo dõi các link đã lưu,
 * và lưu nội dung HTML vào thư mục offline_articles/ trong filesDir.
 */
public class OfflineManager {

    private static final String PREFS_NAME = "OFFLINE_INDEX";
    private static final String KEY_SAVED_LINKS = "saved_links";
    private static final String OFFLINE_DIR = "offline_articles";

    private final Context context;
    private final SharedPreferences prefs;
    private final File offlineDir;

    public OfflineManager(Context context) {
        this.context = context;
        this.prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        this.offlineDir = new File(context.getFilesDir(), OFFLINE_DIR);
        if (!offlineDir.exists()) {
            offlineDir.mkdirs();
        }
    }

    /**
     * Lưu nội dung HTML của bài viết vào bộ nhớ trong.
     */
    public void saveArticle(String articleLink, String htmlContent) {
        try {
            String fileName = String.valueOf(articleLink.hashCode());
            File file = new File(offlineDir, fileName);
            FileOutputStream fos = new FileOutputStream(file);
            fos.write(htmlContent.getBytes("UTF-8"));
            fos.close();

            // Cập nhật index
            Set<String> links = new HashSet<>(getSavedLinksSet());
            links.add(articleLink);
            prefs.edit().putStringSet(KEY_SAVED_LINKS, links).apply();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Đọc nội dung HTML đã lưu của bài viết.
     * @return HTML string hoặc null nếu chưa lưu.
     */
    public String getArticle(String articleLink) {
        try {
            String fileName = String.valueOf(articleLink.hashCode());
            File file = new File(offlineDir, fileName);
            if (!file.exists()) {
                return null;
            }
            FileInputStream fis = new FileInputStream(file);
            BufferedReader reader = new BufferedReader(new InputStreamReader(fis, "UTF-8"));
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line).append("\n");
            }
            reader.close();
            return sb.toString();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Xoá bài viết đã lưu offline.
     */
    public void deleteArticle(String articleLink) {
        String fileName = String.valueOf(articleLink.hashCode());
        File file = new File(offlineDir, fileName);
        if (file.exists()) {
            file.delete();
        }

        // Cập nhật index
        Set<String> links = new HashSet<>(getSavedLinksSet());
        links.remove(articleLink);
        prefs.edit().putStringSet(KEY_SAVED_LINKS, links).apply();
    }

    /**
     * Kiểm tra bài viết đã được lưu offline chưa.
     */
    public boolean isArticleSaved(String articleLink) {
        String fileName = String.valueOf(articleLink.hashCode());
        File file = new File(offlineDir, fileName);
        return file.exists();
    }

    /**
     * Lấy danh sách tất cả link bài viết đã lưu offline.
     */
    public List<String> getSavedArticleLinks() {
        return new ArrayList<>(getSavedLinksSet());
    }

    /**
     * Xoá tất cả bài viết offline đã lưu.
     */
    public void clearAll() {
        // Xoá tất cả file
        File[] files = offlineDir.listFiles();
        if (files != null) {
            for (File file : files) {
                file.delete();
            }
        }
        // Xoá index
        prefs.edit().remove(KEY_SAVED_LINKS).apply();
    }

    private Set<String> getSavedLinksSet() {
        return prefs.getStringSet(KEY_SAVED_LINKS, new HashSet<>());
    }
}
