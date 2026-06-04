# 📊 Kiến Trúc Phân Tầng (Layered Architecture)

## 📁 Cấu Trúc Dự Án Mới

```
com/ptithcm/newspaper/
│
├── 📱 ui/                          (TẦNG PRESENTATION - UI)
│   ├── activity/                   (Activities)
│   │   ├── MainActivity.java       - Màn hình chính
│   │   ├── DetailActivity.java     - Chi tiết bài viết
│   │   ├── FavoritesActivity.java  - Bài viết yêu thích
│   │   ├── ReadingHistoryActivity.java - Lịch sử đọc
│   │   ├── SettingsActivity.java   - Cài đặt
│   │   ├── StatsActivity.java      - Thống kê
│   │   └── AboutActivity.java      - Về ứng dụng
│   │
│   ├── adapter/                    (RecyclerView Adapters)
│   │   └── ArticleAdapter.java    - Adapter cho bài viết
│   │
│   └── fragment/                   (Fragments - nếu cần)
│
├── 💾 data/                        (TẦNG DATA - Lưu Trữ & API)
│   ├── local/                      (Local Storage)
│   │   └── [PreferencesManager -> util/]
│   │
│   ├── remote/                     (Remote API - Retrofit)
│   │   ├── NewsApi.java           - Giao diện API
│   │   └── ApiClient.java         - Khởi tạo Retrofit
│   │
│   ├── repository/                 (Repository Pattern - tương lai)
│   │   ├── ArticleRepository.java
│   │   └── PreferencesRepository.java
│   │
│   └── model/                      (Data Models)
│       ├── Article.java           - Model bài viết
│       └── RssResponse.java        - Model RSS response
│
├── 💼 domain/                      (TẦNG DOMAIN - Business Logic)
│   └── usecase/                    (Use Cases - tương lai)
│       └── GetArticlesUseCase.java
│       └── SaveFavoriteUseCase.java
│
├── 🛠️ util/                        (TẦNG UTILITY - Helper)
│   ├── PreferencesManager.java    - Quản lý SharedPreferences
│   └── Constants.java             - Hằng số
│
└── NewsPaperApp.java              (Application class)
```

---

## 🎯 Mô Tả Từng Tầng

### 1. **UI Layer (tầng Presentation)**
```
Chịu trách nhiệm: Hiển thị UI và xử lý tương tác người dùng

Chứa:
├── Activities
│   └── Quản lý màn hình, xử lý click, navigate
├── Adapters
│   └── Hiển thị dữ liệu trong RecyclerView
└── Fragments
    └── Các phần UI có thể tái sử dụng

Không nên:
❌ Chứa business logic phức tạp
❌ Gọi database trực tiếp
❌ Xử lý network requests
```

### 2. **Data Layer (tầng Dữ Liệu)**
```
Chịu trách nhiệm: Lấy/lưu trữ dữ liệu

Chứa:
├── Remote
│   └── API calls (NewsApi, ApiClient)
├── Local
│   └── SharedPreferences, Database
├── Repository
│   └── Trung gian giữa UI và Data
└── Models
    └── Các class POJO

Không nên:
❌ Chứa UI code
❌ Gọi Activity/Fragment
❌ Hiển thị Toast/Dialog
```

### 3. **Domain Layer (tầng Miền)**
```
Chịu trách nhiệm: Business logic, use cases

Chứa:
├── UseCase classes
│   └── Mỗi use case là 1 action (tương lai)
└── Entities
    └── Pure business models

Không nên:
❌ Phụ thuộc vào UI
❌ Phụ thuộc vào framework
❌ Gọi API/Database trực tiếp
```

### 4. **Util Layer (tầng Tiện Ích)**
```
Chịu trách nhiệm: Các hàm helper, constants

Chứa:
├── PreferencesManager
│   └── Quản lý SharedPreferences
├── Constants
│   └── Các hằng số
├── Validators
│   └── Kiểm tra dữ liệu
└── Utils
    └── Các hàm tiện ích
```

---

## 🔄 Luồng Dữ Liệu

```
User Action (Click)
        ↓
   UI Layer (Activity/Adapter)
        ↓
Data Layer (Repository/API/LocalStorage)
        ↓
   Remote/Local (Retrofit/SharedPreferences)
        ↓
   Trả về dữ liệu
        ↓
   Domain Layer (xử lý business logic - tương lai)
        ↓
   UI Layer (cập nhật UI)
        ↓
   Hiển thị cho người dùng
```

---

## ✅ Lợi Ích Của Kiến Trúc Phân Tầng

| Lợi Ích | Mô Tả |
|---------|-------|
| 📚 **Dễ bảo trì** | Code được tổ chức rõ ràng, dễ tìm bug |
| 🔧 **Dễ mở rộng** | Thêm tính năng mới mà không ảnh hưởng existing code |
| 🧪 **Dễ test** | Tách biệt concerns, dễ viết unit tests |
| ♻️ **Tái sử dụng** | Dùng lại code từ các tầng khác |
| 👥 **Team work** | Nhiều dev có thể làm việc độc lập |
| 🚀 **Performance** | Dễ optimize từng tầng riêng |

---

## 📋 Ví Dụ: Lấy Danh Sách Tin Tức

### Trước (Code lộn xộn):
```java
// MainActivity.java - Chứa tất cả logic
public class MainActivity extends Activity {
    private void fetchNews() {
        // Setup Retrofit
        Retrofit retrofit = new Retrofit.Builder()
            .baseUrl("https://...")
            .addConverterFactory(GsonConverterFactory.create())
            .build();
        
        // Call API
        NewsService service = retrofit.create(NewsService.class);
        service.getNews().enqueue(new Callback<List<Article>>() {
            // Handle response, save to SharedPreferences, update UI...
        });
    }
}
```

### Sau (Kiến trúc phân tầng):
```
1. UI Layer:
   MainActivity.java → gọi Repository

2. Data Layer:
   ArticleRepository.java → chọn local hoặc remote
   → NewsApi.java (call API)
   → ApiClient.java (setup Retrofit)

3. Models:
   Article.java → POJO
   RssResponse.java → API response

4. Util:
   PreferencesManager.java → lưu local
```

---

## 🔄 Cách Thêm Tính Năng Mới

**Ví dụ: Thêm tính năng "Bookmark bài viết"**

### Bước 1: Tạo Model (data/model/)
```java
public class Bookmark {
    private String articleId;
    private String note;
    private long timestamp;
    // ... getters/setters
}
```

### Bước 2: Tạo Local Storage (util/)
```java
// Thêm vào PreferencesManager
public void addBookmark(Bookmark bookmark) { }
public List<Bookmark> getBookmarks() { }
```

### Bước 3: Tạo Repository (data/repository/)
```java
public class BookmarkRepository {
    public void saveBookmark(Bookmark bookmark) { }
    public List<Bookmark> getAllBookmarks() { }
}
```

### Bước 4: Tạo UI (ui/activity/)
```java
public class BookmarkActivity extends Activity {
    private BookmarkRepository repository;
    
    private void loadBookmarks() {
        List<Bookmark> bookmarks = repository.getAllBookmarks();
        // Update UI
    }
}
```

---

## 📝 Quy Tắc Thiết Kế

```
✅ DO:
  • Dependency flows downward (UI → Data → Model)
  • Mỗi file có một trách nhiệm
  • Tách biệt concerns rõ ràng
  • Import từ các tầng dưới
  • Sử dụng interfaces/abstract classes

❌ DON'T:
  • Gọi Activity từ Data layer
  • Lưu trữ logic phức tạp trong Adapter
  • Import từ các tầng trên
  • Mix business logic với UI code
  • Gọi API từ UI trực tiếp
```

---

## 🚀 Tương Lai (Có Thể Mở Rộng)

### Thêm Domain Layer (Clean Architecture):
```java
// domain/usecase/GetArticlesUseCase.java
public class GetArticlesUseCase {
    private ArticleRepository repository;
    
    public Observable<List<Article>> execute() {
        return repository.getArticles();
    }
}
```

### Thêm Dependency Injection (Hilt/Dagger):
```java
@Module
public class RepositoryModule {
    @Provides
    ArticleRepository provideArticleRepository(
        NewsApi api,
        PreferencesManager prefs
    ) {
        return new ArticleRepository(api, prefs);
    }
}
```

### Reactive Programming (RxJava):
```java
repository.getArticles()
    .subscribeOn(Schedulers.io())
    .observeOn(AndroidSchedulers.mainThread())
    .subscribe(articles -> updateUI(articles));
```

---

## 📊 Tóm Tắt

| Tầng | Trách Nhiệm | Ví Dụ |
|-----|-----------|-------|
| **UI** | Hiển thị & tương tác | Activity, Adapter, Fragment |
| **Data** | Lấy/lưu dữ liệu | Repository, API, DB |
| **Domain** | Business logic | UseCase, Entity |
| **Util** | Helper functions | Preferences, Constants |

---

**Kiến trúc phân tầng giúp dự án bạn ngăn nắp, dễ bảo trì và dễ mở rộng!** 🎉

