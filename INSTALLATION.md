# 🚀 Hướng Dẫn Cài Đặt & Chạy Ứng Dụng

## 📋 Yêu Cầu Hệ Thống

- **Android Studio** phiên bản mới nhất
- **Android SDK** 24 trở lên (API 24+)
- **Gradle** 8.0 hoặc mới hơn
- **Java Development Kit (JDK)** 11 trở lên

## 🛠️ Hướng Dẫn Cài Đặt

### 1. Mở Dự Án
```bash
# Sao chép repository hoặc mở thư mục NewsPaper2
cd D:\NewsPaper2

# Mở Android Studio
# File → Open → Chọn thư mục NewsPaper2
```

### 2. Đợi Gradle Sync
- Android Studio sẽ tự động tải dependencies
- Chờ cho đến khi "Build Gradle" hoàn thành

### 3. Chạy Ứng Dụng

#### Cách 1: Trên Thiết Bị Thật
```bash
# 1. Kết nối thiết bị Android qua USB
# 2. Bật Developer Mode trên thiết bị
# 3. Trong Android Studio:
#    - Nhấn Shift + F10 hoặc
#    - Run → Run 'app'
```

#### Cách 2: Trên Emulator
```bash
# 1. Mở AVD Manager (Virtual Device)
# 2. Tạo hoặc chọn một virtual device
# 3. Nhấn Play để khởi động emulator
# 4. Trong Android Studio:
#    - Nhấn Shift + F10 hoặc
#    - Run → Run 'app'
```

## 🔧 Cấu Hình Gradle

### Thêm Dependencies (Đã được làm)

File: `app/build.gradle`

```groovy
dependencies {
    // ... các dependencies khác ...
    
    // CardView cho Stats Activity
    implementation 'androidx.cardview:cardview:1.0.0'
}
```

## 📂 Cấu Trúc Dự Án

```
NewsPaper2/
├── app/
│   ├── src/
│   │   └── main/
│   │       ├── AndroidManifest.xml (Đã cập nhật)
│   │       ├── java/com/ptithcm/newspaper/
│   │       │   ├── AboutActivity.java ✨
│   │       │   ├── DetailActivity.java (Cập nhật)
│   │       │   ├── FavoritesActivity.java ✨
│   │       │   ├── MainActivity.java (Cập nhật)
│   │       │   ├── NewsPaperApp.java ✨
│   │       │   ├── PreferencesManager.java ✨
│   │       │   ├── ReadingHistoryActivity.java ✨
│   │       │   ├── SettingsActivity.java ✨
│   │       │   ├── StatsActivity.java ✨
│   │       │   ├── adapter/
│   │       │   ├── api/
│   │       │   └── model/
│   │       └── res/
│   │           ├── layout/
│   │           │   ├── activity_about.xml ✨
│   │           │   ├── activity_favorites.xml ✨
│   │           │   ├── activity_reading_history.xml ✨
│   │           │   ├── activity_settings.xml ✨
│   │           │   └── activity_stats.xml ✨
│   │           └── values/
│   │               └── strings.xml (Cập nhật)
│   └── build.gradle (Cập nhật)
│
├── build.gradle
├── settings.gradle
├── FEATURES.md ✨ (File tài liệu mới)
└── CHANGES_SUMMARY.md ✨ (File tài liệu mới)
```

**✨** = File mới  
**(Cập nhật)** = File được chỉnh sửa

## 🎯 Kiểm Tra sau Khi Cài Đặt

### 1. Chạy Ứng Dụng
```
✓ Ứng dụng khởi động thành công
✓ Không có lỗi crash
✓ Hiển thị danh sách tin tức
```

### 2. Kiểm Tra Menu
```
✓ Menu "Yêu thích" hoạt động
✓ Menu "Lịch sử" hoạt động
✓ Menu "Thống kê" hoạt động
✓ Menu "Cài đặt" hoạt động
✓ Menu "Giới thiệu" hoạt động
✓ Tìm kiếm hoạt động
✓ Đổi ngôn ngữ hoạt động
```

### 3. Kiểm Tra Tính Năng
```
✓ Yêu thích: Nhấn giữ bài viết → Lưu
✓ Lịch sử: Xem bài viết → Lưu tự động
✓ Dark Mode: Bật từ Cài đặt
✓ Thống kê: Xem số lượng bài
✓ Giới thiệu: Xem thông tin app
```

## 🐛 Khắc Phục Sự Cố

### Lỗi: "Gradle sync failed"
```
Giải pháp:
1. File → Invalidate Caches / Restart
2. Chọn "Invalidate and Restart"
3. Đợi Android Studio khởi động lại
```

### Lỗi: "Cannot resolve symbol"
```
Giải pháp:
1. Build → Clean Project
2. Build → Rebuild Project
3. Nếu vẫn lỗi: File → Sync Now
```

### Ứng dụng crash khi chạy
```
Giải pháp:
1. Kiểm tra Logcat (View → Tool Windows → Logcat)
2. Tìm dòng "Exception" để xem lỗi chi tiết
3. Cập nhật dependencies nếu cần
```

### Dark Mode không hoạt động
```
Giải pháp:
1. Kiểm tra API level (tối thiểu 24)
2. Xóa app → Cài lại
3. Kiểm tra cấu hình trong SettingsActivity
```

## 📱 Kiểm Tra Trên Thiết Bị Thật

### Yêu Cầu
```
- Android 7.0 (API 24) trở lên
- RAM tối thiểu 2GB
- Dung lượng ứng dụng ~20MB
```

### Bước Thực Hiện
```
1. Kết nối thiết bị qua USB
2. Bật USB Debugging trên thiết bị:
   Settings → Developer Options → USB Debugging → ON
3. Trong Android Studio:
   Run → Select Device → Chọn thiết bị → Run
```

## 🎓 Tài Nguyên Thêm

- **Android Documentation**: https://developer.android.com
- **Retrofit**: https://square.github.io/retrofit/
- **RecyclerView**: https://developer.android.com/guide/topics/ui/layout/recyclerview
- **SharedPreferences**: https://developer.android.com/training/data-storage/shared-preferences

## ❓ Câu Hỏi Thường Gặp

### Q: Dữ liệu được lưu ở đâu?
A: Dữ liệu được lưu trong SharedPreferences:
- Yêu thích: "FAVORITES" preference
- Lịch sử: "READING_HISTORY" preference
- Cài đặt: "SETTINGS" preference

### Q: Có thể đồng bộ với cloud không?
A: Hiện tại không hỗ trợ, nhưng có thể thêm Firebase Firestore trong tương lai.

### Q: Ứng dụng yêu cầu internet không?
A: Có, ứng dụng cần internet để tải tin tức từ RSS feed. Nhưng đã lưu bài viết có thể xem offline (trong phiên bản tương lai).

### Q: Làm sao để xóa tất cả dữ liệu?
A: Trong Android:
1. Settings → Apps → Siêu Báo 24h
2. Storage → Clear Data
3. Hoặc gỡ cài đặt ứng dụng

---

**Bạn đã sẵn sàng! Hãy thử cài đặt và sử dụng ứng dụng Siêu Báo 24h.** 🎉

Nếu gặp vấn đề, vui lòng kiểm tra Logcat hoặc liên hệ hỗ trợ.

