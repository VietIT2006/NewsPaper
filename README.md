# 📰 Siêu Báo 24h - Ứng Dụng Đọc Tin Tức Android

> **Một ứng dụng NEWS hiện đại với các tính năng thú vị: Yêu thích, Lịch sử đọc, Dark Mode, và nhiều hơn nữa!**

![Version Badge](https://img.shields.io/badge/version-1.0-blue)
![Android Badge](https://img.shields.io/badge/android-24%2B-green)
![Status Badge](https://img.shields.io/badge/status-active-brightgreen)

---

## 🎯 Tính Năng Chính

### ⭐ Yêu Thích
- Lưu bài viết yêu thích bằng cách nhấn giữ
- Xem tất cả bài yêu thích từ menu riêng
- Lưu trữ vô hạn

### 📚 Lịch Sử Đọc
- Tự động ghi nhận bài viết đã xem
- Lưu tối đa 100 bài gần nhất
- Xóa lịch sử với một nút bấm

### 🌙 Chế Độ Tối (Dark Mode)
- Bật/tắt từ cài đặt
- Áp dụng tự động khi khởi động lại
- Hỗ trợ toàn bộ ứng dụng

### 📊 Thống Kê
- Xem số bài đã đọc
- Xem số bài yêu thích
- UI đẹp với CardView

### ⚙️ Cài Đặt
- Quản lý Dark Mode
- Bật/tắt Thông báo
- Lưu cấu hình tự động

### 🌐 Đổi Ngôn Ngữ
- Hỗ trợ Tiếng Việt & Tiếng Anh
- Chuyển đổi tức thời

### 🔍 Tìm Kiếm Nâng Cao
- Tìm kiếm theo tiêu đề bài viết
- Kết quả hiển thị thời gian thực

### 📤 Chia Sẻ
- Chia sẻ bài viết qua các ứng dụng khác
- Hỗ trợ tất cả mạng xã hội

### ℹ️ Giới Thiệu
- Xem thông tin ứng dụng
- Liệt kê tất cả tính năng
- Thông tin nhà phát triển

---

## 📊 Danh Sách Thay Đổi

### ✨ Tạo Mới (12 File)
- **7 Activity** Java mới
- **5 Layout** XML mới

### ✏️ Cập Nhật (6 File)
- `AndroidManifest.xml` - Đăng ký Activity mới
- `MainActivity.java` - Thêm menu & PreferencesManager
- `DetailActivity.java` - Thêm nút Yêu thích & lưu lịch sử
- `ArticleAdapter.java` - Truyền dữ liệu cho Detail
- `app/build.gradle` - Thêm CardView dependency
- `values/strings.xml` - Thêm kiểu mới

### 📚 Tài Liệu (4 File)
- `FEATURES.md` - Chi tiết tính năng
- `CHANGES_SUMMARY.md` - Tóm tắt thay đổi
- `INSTALLATION.md` - Hướng dẫn cài đặt
- `FILES_MODIFIED.md` - Danh sách file xem chi tiết

---

## 🚀 Bắt Đầu Nhanh

### Yêu Cầu
- Android Studio (phiên bản mới)
- Android SDK 24+
- JDK 11+

### Cài Đặt
```bash
# 1. Mở Android Studio
# 2. File → Open → Chọn thư mục NewsPaper2
# 3. Đợi Gradle Sync hoàn thành
# 4. Run → Run 'app'
```

### Chạy Ứng Dụng
```bash
# Trên thiết bị thật:
Shift + F10  # Hoặc Run → Run 'app'

# Trên Emulator:
Chọn device → Shift + F10
```

---

## 📱 Giao Diện

### Màu Sắc
- **Chính**: #007BFF (Blue)
- **Accent**: #E53935 (Red)
- **Nền**: #F5F5F5 (Light Gray)

### Layout
- RecyclerView cho danh sách
- CardView cho thống kê
- Toolbar với menu
- SwipeRefreshLayout để làm mới

---

## 📁 Cấu Trúc Dự Án

```
NewsPaper2/
├── app/
│   ├── src/main/
│   │   ├── java/com/ptithcm/newspaper/
│   │   │   ├── AboutActivity.java
│   │   │   ├── DetailActivity.java
│   │   │   ├── FavoritesActivity.java
│   │   │   ├── MainActivity.java
│   │   │   ├── NewsPaperApp.java
│   │   │   ├── PreferencesManager.java
│   │   │   ├── ReadingHistoryActivity.java
│   │   │   ├── SettingsActivity.java
│   │   │   ├── StatsActivity.java
│   │   │   └── adapter/, api/, model/
│   │   │
│   │   └── res/
│   │       ├── layout/
│   │       │   ├── activity_about.xml
│   │       │   ├── activity_favorites.xml
│   │       │   ├── activity_reading_history.xml
│   │       │   ├── activity_settings.xml
│   │       │   └── activity_stats.xml
│   │       │
│   │       └── values/
│   │           ├── strings.xml
│   │           ├── colors.xml
│   │           └── themes.xml
│   │
│   └── build.gradle
│
├── FEATURES.md
├── CHANGES_SUMMARY.md
├── INSTALLATION.md
├── FILES_MODIFIED.md
└── README.md (file này)
```

---

## 🎨 Hướng Dẫn Sử Dụng

### 1. Yêu Thích Bài Viết
```
Danh sách tin → Nhấn giữ bài viết → Tự động lưu
Hoặc:
Mở bài viết → Nhấn ⭐ Yêu Thích → Lưu
```

### 2. Xem Lịch Sử
```
Menu → Lịch sử → Xem bài đã đọc
Menu → Lịch sử → (menu) → Xóa lịch sử
```

### 3. Bật Dark Mode
```
Menu → Cài đặt → Bật "Chế độ tối" → Xong
```

### 4. Xem Thống Kê
```
Menu → Thống kê → Xem số bài đã đọc & yêu thích
```

---

## 🔧 Cấu Hình Trong Code

### PreferencesManager
```java
// Lưu bài viết yêu thích
PreferencesManager prefs = new PreferencesManager(context);
prefs.addFavorite(article);
prefs.removeFavorite(articleLink);
List<Article> favorites = prefs.getFavorites();

// Lưu lịch sử đọc
prefs.addToHistory(article);
List<Article> history = prefs.getHistory();
prefs.clearHistory();

// Dark Mode
prefs.setDarkMode(true);
boolean isDarkEnabled = prefs.isDarkModeEnabled();
```

### Dark Mode Implementation
```java
// Trong NewsPaperApp.onCreate()
PreferencesManager prefs = new PreferencesManager(this);
if (prefs.isDarkModeEnabled()) {
    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
} else {
    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
}
```

---

## 📊 Thống Kê Dự Án

| Thống Kê | Số Lượng |
|----------|----------|
| Java Classes | 7 mới + 3 cập nhật |
| XML Layouts | 5 mới |
| Database | SharedPreferences |
| API Calls | Retrofit2 |
| Permissions | INTERNET only |
| Min SDK | 24 (Android 7.0) |
| Target SDK | 36 (Android 14) |

---

## 🐛 Khắc Phục Sự Cố

### Gradle Sync Failed
```
→ File → Invalidate Caches / Restart
→ Chọn "Invalidate and Restart"
```

### Cannot Resolve Symbol
```
→ Build → Clean Project
→ Build → Rebuild Project
→ File → Sync Now
```

### App Crashes
```
→ Xem Logcat: View → Tool Windows → Logcat
→ Tìm "Exception" để xem lỗi chi tiết
```

Xem **INSTALLATION.md** để biết chi tiết hơn.

---

## 📚 Dependencies

```groovy
// Core
implementation 'androidx.appcompat:appcompat:1.x.x'
implementation 'androidx.constraintlayout:constraintlayout:2.x.x'

// Networking
implementation 'com.squareup.retrofit2:retrofit:2.9.0'
implementation 'com.squareup.retrofit2:converter-gson:2.9.0'

// Image Loading
implementation 'com.github.bumptech.glide:glide:4.16.0'

// HTML Parsing
implementation 'org.jsoup:jsoup:1.17.2'

// UI
implementation 'com.google.android.material:material:1.x.x'
implementation 'androidx.swiperefreshlayout:swiperefreshlayout:1.1.0'
implementation 'androidx.cardview:cardview:1.0.0'

// JSON
implementation 'com.google.code.gson:gson:2.10.1'
```

---

## 📖 Tài Liệu Thêm

- **FEATURES.md** - Danh sách chi tiết tất cả tính năng
- **CHANGES_SUMMARY.md** - Tóm tắt những gì đã thay đổi
- **INSTALLATION.md** - Hướng dẫn chi tiết cài đặt
- **FILES_MODIFIED.md** - Liệt kê tất cả file thay đổi

---

## 🤝 Đóng Góp

Để đóng góp cho dự án:
1. Fork repository
2. Tạo branch tính năng mới
3. Commit thay đổi
4. Push đến branch
5. Tạo Pull Request

---

## 📝 License

MIT License - Tự do sử dụng và sửa đổi

---

## 👥 Nhà Phát Triển

**PTIT HCM Development Team**
- Phiên bản: 1.0
- Ngày cập nhật: 2026-06-04
- Status: ✅ Sản xuất

---

## ❓ Câu Hỏi Thường Gặp

### Cần internet không?
**Có**, để tải tin tức từ RSS feed.

### Dữ liệu được lưu ở đâu?
**SharedPreferences** - Lưu local trên thiết bị.

### Có thể đồng bộ cloud không?
**Hiện tại không**, nhưng có thể thêm Firebase sau.

### Ứng dụng chiếm bao nhiêu dung lượng?
**~20MB** (tùy thiết bị).

---

## 🎉 Cảm Ơn

Cảm ơn bạn đã sử dụng **Siêu Báo 24h**! Chúc bạn có trải nghiệm tuyệt vời. 📰✨

---

**Bạn đã sẵn sàng? Hãy tải xuống và sử dụng ngay!** 🚀

