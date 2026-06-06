package com.ptithcm.newspaper.ui.activity;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.speech.tts.TextToSpeech;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.ValueCallback;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.ptithcm.newspaper.R;
import com.ptithcm.newspaper.data.model.Article;
import com.ptithcm.newspaper.util.OfflineManager;
import com.ptithcm.newspaper.util.PreferencesManager;

import org.json.JSONArray;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class DetailActivity extends AppCompatActivity implements TextToSpeech.OnInitListener {

    private WebView webViewContent;
    private ProgressBar progressBarDetail;
    private PreferencesManager preferencesManager;
    private OfflineManager offlineManager;
    private String currentArticleLink;
    private String currentArticleTitle;
    private String currentArticleThumbnail;
    private String currentArticleHtml;
    private String plainTextContent = "";

    // TTS
    private TextToSpeech textToSpeech;
    private boolean isTtsReady = false;
    private boolean isPlayingTts = false;
    private float ttsSpeed = 1.0f;
    private LinearLayout layoutTtsControls;
    private ImageView btnTtsPlay, btnTtsStop;
    private TextView tvTtsSpeed;

    // Reader Mode
    private ImageView btnReaderMode;

    private static final String GEMINI_API_KEY = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        
        preferencesManager = new PreferencesManager(this);
        offlineManager = new OfflineManager(this);
        textToSpeech = new TextToSpeech(this, this);

        Toolbar toolbar = findViewById(R.id.toolbarDetail);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle(getString(R.string.detail_title));
        }

        webViewContent = findViewById(R.id.webViewContent);
        progressBarDetail = findViewById(R.id.progressBarDetail);
        layoutTtsControls = findViewById(R.id.layoutTtsControls);
        btnTtsPlay = findViewById(R.id.btnTtsPlay);
        btnTtsStop = findViewById(R.id.btnTtsStop);
        tvTtsSpeed = findViewById(R.id.tvTtsSpeed);
        btnReaderMode = findViewById(R.id.btnReaderMode);

        webViewContent.getSettings().setJavaScriptEnabled(true);
        webViewContent.getSettings().setDefaultTextEncodingName("utf-8");

        currentArticleLink = getIntent().getStringExtra("ARTICLE_LINK");
        currentArticleTitle = getIntent().getStringExtra("ARTICLE_TITLE");
        currentArticleThumbnail = getIntent().getStringExtra("ARTICLE_THUMBNAIL");
        
        setupTtsControls();
        setupReaderMode();

        if (currentArticleLink != null) {
            if (offlineManager.isArticleSaved(currentArticleLink)) {
                String savedHtml = offlineManager.getArticle(currentArticleLink);
                if (savedHtml != null) {
                    currentArticleHtml = savedHtml;
                    extractPlainText(savedHtml);
                    renderWebView(savedHtml);
                } else {
                    scrapeArticleContent(currentArticleLink);
                }
            } else {
                scrapeArticleContent(currentArticleLink);
            }
            
            Article article = new Article();
            article.setLink(currentArticleLink);
            article.setTitle(currentArticleTitle != null ? currentArticleTitle : getString(R.string.untitled_article));
            article.setThumbnail(currentArticleThumbnail);
            preferencesManager.addToHistory(article);
        }
    }

    private void setupTtsControls() {
        btnTtsPlay.setOnClickListener(v -> {
            if (!isTtsReady) {
                Toast.makeText(this, getString(R.string.tts_not_available), Toast.LENGTH_SHORT).show();
                return;
            }
            if (plainTextContent.isEmpty()) {
                Toast.makeText(this, getString(R.string.tts_no_content), Toast.LENGTH_SHORT).show();
                return;
            }

            if (isPlayingTts) {
                textToSpeech.stop();
                isPlayingTts = false;
                btnTtsPlay.setImageResource(android.R.drawable.ic_media_play);
            } else {
                textToSpeech.setSpeechRate(ttsSpeed);
                // Split text into chunks to avoid TTS limits
                int chunkSize = 3000;
                for (int i = 0; i < plainTextContent.length(); i += chunkSize) {
                    String chunk = plainTextContent.substring(i, Math.min(plainTextContent.length(), i + chunkSize));
                    if (i == 0) {
                        textToSpeech.speak(chunk, TextToSpeech.QUEUE_FLUSH, null, "TTS_ID");
                    } else {
                        textToSpeech.speak(chunk, TextToSpeech.QUEUE_ADD, null, "TTS_ID");
                    }
                }
                isPlayingTts = true;
                btnTtsPlay.setImageResource(android.R.drawable.ic_media_pause);
            }
        });

        btnTtsStop.setOnClickListener(v -> {
            if (textToSpeech != null) {
                textToSpeech.stop();
                isPlayingTts = false;
                btnTtsPlay.setImageResource(android.R.drawable.ic_media_play);
            }
        });

        tvTtsSpeed.setOnClickListener(v -> {
            if (ttsSpeed == 1.0f) ttsSpeed = 1.25f;
            else if (ttsSpeed == 1.25f) ttsSpeed = 1.5f;
            else if (ttsSpeed == 1.5f) ttsSpeed = 2.0f;
            else if (ttsSpeed == 2.0f) ttsSpeed = 0.75f;
            else ttsSpeed = 1.0f;
            tvTtsSpeed.setText(ttsSpeed + "x");
            if (isPlayingTts) {
                textToSpeech.setSpeechRate(ttsSpeed);
            }
        });
    }

    private void setupReaderMode() {
        btnReaderMode.setOnClickListener(v -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            View view = getLayoutInflater().inflate(R.layout.dialog_reader_settings, null);
            builder.setView(view);
            AlertDialog dialog = builder.create();

            Button btnClose = view.findViewById(R.id.btnCloseReaderSettings);
            btnClose.setOnClickListener(view1 -> dialog.dismiss());

            Button btnSizeS = view.findViewById(R.id.btnSizeSmall);
            Button btnSizeM = view.findViewById(R.id.btnSizeMedium);
            Button btnSizeL = view.findViewById(R.id.btnSizeLarge);
            Button btnSizeXL = view.findViewById(R.id.btnSizeXLarge);

            View.OnClickListener sizeListener = v1 -> {
                int size = 18;
                if (v1 == btnSizeS) size = 14;
                if (v1 == btnSizeM) size = 18;
                if (v1 == btnSizeL) size = 22;
                if (v1 == btnSizeXL) size = 26;
                preferencesManager.setFontSize(size);
                if (currentArticleHtml != null) renderWebView(currentArticleHtml);
            };
            btnSizeS.setOnClickListener(sizeListener);
            btnSizeM.setOnClickListener(sizeListener);
            btnSizeL.setOnClickListener(sizeListener);
            btnSizeXL.setOnClickListener(sizeListener);

            Button btnFontSans = view.findViewById(R.id.btnFontSans);
            Button btnFontSerif = view.findViewById(R.id.btnFontSerif);
            Button btnFontMono = view.findViewById(R.id.btnFontMono);

            View.OnClickListener fontListener = v1 -> {
                String font = "sans-serif";
                if (v1 == btnFontSans) font = "sans-serif";
                if (v1 == btnFontSerif) font = "serif";
                if (v1 == btnFontMono) font = "monospace";
                preferencesManager.setFontFamily(font);
                if (currentArticleHtml != null) renderWebView(currentArticleHtml);
            };
            btnFontSans.setOnClickListener(fontListener);
            btnFontSerif.setOnClickListener(fontListener);
            btnFontMono.setOnClickListener(fontListener);

            Button btnBgWhite = view.findViewById(R.id.btnBgWhite);
            Button btnBgSepia = view.findViewById(R.id.btnBgSepia);
            Button btnBgDark = view.findViewById(R.id.btnBgDark);

            View.OnClickListener bgListener = v1 -> {
                String bg = "white";
                if (v1 == btnBgWhite) bg = "white";
                if (v1 == btnBgSepia) bg = "sepia";
                if (v1 == btnBgDark) bg = "dark";
                preferencesManager.setReaderBackground(bg);
                if (currentArticleHtml != null) renderWebView(currentArticleHtml);
            };
            btnBgWhite.setOnClickListener(bgListener);
            btnBgSepia.setOnClickListener(bgListener);
            btnBgDark.setOnClickListener(bgListener);

            dialog.show();
        });
    }

    @Override
    public void onInit(int status) {
        if (status == TextToSpeech.SUCCESS) {
            int result = textToSpeech.setLanguage(new Locale("vi", "VN"));
            if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                isTtsReady = false;
            } else {
                isTtsReady = true;
            }
        } else {
            isTtsReady = false;
        }
    }

    @Override
    public void onActionModeStarted(ActionMode mode) {
        super.onActionModeStarted(mode);
        Menu menu = mode.getMenu();

        if (menu.findItem(101) == null) {
            MenuItem translateItem = menu.add(Menu.NONE, 101, 100, getString(R.string.action_translate));
            translateItem.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
            translateItem.setOnMenuItemClickListener(item -> {
                handleCustomSelection(true);
                mode.finish();
                return true;
            });
        }

        if (menu.findItem(102) == null) {
            MenuItem askAiItem = menu.add(Menu.NONE, 102, 101, getString(R.string.action_ask_ai));
            askAiItem.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
            askAiItem.setOnMenuItemClickListener(item -> {
                handleCustomSelection(false);
                mode.finish();
                return true;
            });
        }
    }

    private void handleCustomSelection(boolean isTranslate) {
        webViewContent.evaluateJavascript("(function(){return window.getSelection().toString()})()", new ValueCallback<String>() {
            @Override
            public void onReceiveValue(String value) {
                if (value != null && !value.equals("null") && value.length() >= 2) {
                    String selectedText = value;
                    if (value.startsWith("\"") && value.endsWith("\"")) {
                        selectedText = value.substring(1, value.length() - 1);
                    }
                    selectedText = selectedText.trim().replace("\\\"", "\"").replace("\\n", "\n").replace("\\u003C", "<");
                    if (!selectedText.isEmpty()) {
                        showActionPopup(isTranslate, selectedText);
                    }
                }
            }
        });
    }

    private void showActionPopup(boolean isTranslate, String selectedText) {
        BottomSheetDialog dialog = new BottomSheetDialog(this);
        View view = getLayoutInflater().inflate(R.layout.layout_bottom_sheet_result, null);
        dialog.setContentView(view);

        TextView tvTitle = view.findViewById(R.id.tvActionTitle);
        ImageView imgIcon = view.findViewById(R.id.imgActionIcon);
        TextView tvSelectedText = view.findViewById(R.id.tvSelectedText);
        TextView tvResultText = view.findViewById(R.id.tvResultText);
        ProgressBar progressBar = view.findViewById(R.id.progressBarAction);
        Button btnClose = view.findViewById(R.id.btnClose);

        tvSelectedText.setText(selectedText);
        btnClose.setOnClickListener(v -> dialog.dismiss());

        if (isTranslate) {
            tvTitle.setText(getString(R.string.action_translate));
            imgIcon.setImageResource(android.R.drawable.ic_menu_sort_alphabetically);
            performTranslation(selectedText, tvResultText, progressBar);
        } else {
            tvTitle.setText(getString(R.string.action_ask_ai));
            imgIcon.setImageResource(android.R.drawable.ic_dialog_info);
            performAskAI(selectedText, tvResultText, progressBar);
        }

        dialog.show();
    }

    private void performTranslation(String text, TextView tvResult, ProgressBar progressBar) {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());
        executor.execute(() -> {
            try {
                String urlStr = "https://translate.googleapis.com/translate_a/single?client=gtx&sl=auto&tl=en&dt=t&q=" + URLEncoder.encode(text, "UTF-8");
                URL url = new URL(urlStr);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");

                BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                StringBuilder response = new StringBuilder();
                String inputLine;
                while ((inputLine = in.readLine()) != null) response.append(inputLine);
                in.close();

                JSONArray jsonArray = new JSONArray(response.toString());
                JSONArray parts = jsonArray.getJSONArray(0);
                StringBuilder translatedText = new StringBuilder();
                for (int i = 0; i < parts.length(); i++) translatedText.append(parts.getJSONArray(i).getString(0));

                handler.post(() -> {
                    progressBar.setVisibility(View.GONE);
                    tvResult.setVisibility(View.VISIBLE);
                    tvResult.setText(translatedText.toString());
                });

            } catch (Exception e) {
                handler.post(() -> {
                    progressBar.setVisibility(View.GONE);
                    tvResult.setVisibility(View.VISIBLE);
                    tvResult.setText(getString(R.string.api_error));
                });
            }
        });
    }

    private void performAskAI(String text, TextView tvResult, ProgressBar progressBar) {
        if (GEMINI_API_KEY.isEmpty()) {
            progressBar.setVisibility(View.GONE);
            tvResult.setVisibility(View.VISIBLE);
            tvResult.setText("Vui lòng thêm GEMINI_API_KEY");
            return;
        }

        ExecutorService executor = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());

        executor.execute(() -> {
            try {
                String urlStr = "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.5-flash:generateContent?key=" + GEMINI_API_KEY;
                URL url = new URL(urlStr);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Content-Type", "application/json");
                conn.setDoOutput(true);

                String prompt = "Giải thích ngắn gọn nghĩa của từ/cụm từ sau trong ngữ cảnh đọc báo: \"" + text + "\"";
                
                JSONObject part = new JSONObject();
                part.put("text", prompt);
                JSONArray partsArray = new JSONArray();
                partsArray.put(part);
                JSONObject content = new JSONObject();
                content.put("parts", partsArray);
                JSONArray contentsArray = new JSONArray();
                contentsArray.put(content);
                JSONObject requestBody = new JSONObject();
                requestBody.put("contents", contentsArray);

                OutputStream os = conn.getOutputStream();
                os.write(requestBody.toString().getBytes("UTF-8"));
                os.close();

                int responseCode = conn.getResponseCode();
                if (responseCode == 200) {
                    BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                    StringBuilder response = new StringBuilder();
                    String inputLine;
                    while ((inputLine = in.readLine()) != null) response.append(inputLine);
                    in.close();

                    JSONObject jsonResponse = new JSONObject(response.toString());
                    String aiText = jsonResponse.getJSONArray("candidates")
                            .getJSONObject(0).getJSONObject("content").getJSONArray("parts")
                            .getJSONObject(0).getString("text");

                    handler.post(() -> {
                        progressBar.setVisibility(View.GONE);
                        tvResult.setVisibility(View.VISIBLE);
                        tvResult.setText(aiText);
                    });
                } else {
                    BufferedReader in = new BufferedReader(new InputStreamReader(conn.getErrorStream()));
                    StringBuilder errorResponse = new StringBuilder();
                    String inputLine;
                    while ((inputLine = in.readLine()) != null) errorResponse.append(inputLine);
                    in.close();
                    
                    final String errorMsg = "Lỗi API (" + responseCode + "): " + errorResponse.toString();
                    handler.post(() -> {
                        progressBar.setVisibility(View.GONE);
                        tvResult.setVisibility(View.VISIBLE);
                        tvResult.setText(errorMsg);
                    });
                }

            } catch (Exception e) {
                final String exMsg = "Lỗi Exception: " + e.getMessage();
                handler.post(() -> {
                    progressBar.setVisibility(View.GONE);
                    tvResult.setVisibility(View.VISIBLE);
                    tvResult.setText(exMsg);
                });
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add(0, 1, 0, getString(R.string.favorites)).setIcon(android.R.drawable.ic_menu_add).setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
        menu.add(0, 2, 0, getString(R.string.share)).setIcon(android.R.drawable.ic_menu_share).setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
        
        // Advanced Features
        menu.add(0, 3, 0, getString(R.string.ai_summary)).setIcon(android.R.drawable.ic_menu_sort_by_size).setShowAsAction(MenuItem.SHOW_AS_ACTION_NEVER);
        menu.add(0, 4, 0, getString(R.string.ai_chat_title)).setIcon(android.R.drawable.ic_dialog_dialer).setShowAsAction(MenuItem.SHOW_AS_ACTION_NEVER);
        menu.add(0, 5, 0, getString(R.string.offline_save)).setIcon(android.R.drawable.ic_menu_save).setShowAsAction(MenuItem.SHOW_AS_ACTION_NEVER);
        menu.add(0, 6, 0, getString(R.string.tts_play)).setIcon(android.R.drawable.ic_media_play).setShowAsAction(MenuItem.SHOW_AS_ACTION_NEVER);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        } else if (item.getItemId() == 1) { // Favorites
            if (currentArticleLink != null) {
                Article article = new Article();
                article.setLink(currentArticleLink);
                article.setTitle(currentArticleTitle);
                article.setThumbnail(currentArticleThumbnail);
                article.setSavedHtml(currentArticleHtml);
                preferencesManager.addFavorite(article);
                Toast.makeText(this, getString(R.string.added_to_favorites), Toast.LENGTH_SHORT).show();
            }
            return true;
        } else if (item.getItemId() == 2) { // Share
            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.setType("text/plain");
            shareIntent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.share_subject));
            shareIntent.putExtra(Intent.EXTRA_TEXT, currentArticleLink);
            startActivity(Intent.createChooser(shareIntent, getString(R.string.share_via)));
            return true;
        } else if (item.getItemId() == 3) { // AI Summary
            showAiSummary();
            return true;
        } else if (item.getItemId() == 4) { // Chat AI
            if (plainTextContent != null && !plainTextContent.isEmpty()) {
                Intent chatIntent = new Intent(this, AiChatActivity.class);
                chatIntent.putExtra("ARTICLE_CONTENT", plainTextContent);
                chatIntent.putExtra("ARTICLE_TITLE", currentArticleTitle);
                startActivity(chatIntent);
            } else {
                Toast.makeText(this, getString(R.string.ai_chat_no_content), Toast.LENGTH_SHORT).show();
            }
            return true;
        } else if (item.getItemId() == 5) { // Save Offline
            if (currentArticleLink != null && currentArticleHtml != null) {
                if (offlineManager.isArticleSaved(currentArticleLink)) {
                    Toast.makeText(this, getString(R.string.offline_already_saved), Toast.LENGTH_SHORT).show();
                } else {
                    offlineManager.saveArticle(currentArticleLink, currentArticleHtml);
                    Toast.makeText(this, getString(R.string.offline_saved), Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(this, getString(R.string.error_data), Toast.LENGTH_SHORT).show();
            }
            return true;
        } else if (item.getItemId() == 6) { // Toggle TTS controls
            if (layoutTtsControls.getVisibility() == View.VISIBLE) {
                layoutTtsControls.setVisibility(View.GONE);
            } else {
                layoutTtsControls.setVisibility(View.VISIBLE);
            }
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    
    private void showAiSummary() {
        if (plainTextContent.isEmpty()) return;
        
        BottomSheetDialog dialog = new BottomSheetDialog(this);
        View view = getLayoutInflater().inflate(R.layout.layout_bottom_sheet_result, null);
        dialog.setContentView(view);

        TextView tvTitle = view.findViewById(R.id.tvActionTitle);
        ImageView imgIcon = view.findViewById(R.id.imgActionIcon);
        TextView tvSelectedText = view.findViewById(R.id.tvSelectedText);
        TextView tvResultText = view.findViewById(R.id.tvResultText);
        ProgressBar progressBar = view.findViewById(R.id.progressBarAction);
        Button btnClose = view.findViewById(R.id.btnClose);

        tvTitle.setText(getString(R.string.ai_summary));
        imgIcon.setImageResource(android.R.drawable.ic_menu_sort_by_size);
        tvSelectedText.setText(currentArticleTitle);
        btnClose.setOnClickListener(v -> dialog.dismiss());

        ExecutorService executor = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());

        executor.execute(() -> {
            try {
                String urlStr = "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.5-flash:generateContent?key=" + GEMINI_API_KEY;
                URL url = new URL(urlStr);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Content-Type", "application/json");
                conn.setDoOutput(true);

                String prompt = "Tóm tắt bài báo sau thành 3-5 câu ngắn gọn và dễ hiểu:\n\n" + plainTextContent;
                
                JSONObject part = new JSONObject();
                part.put("text", prompt);
                JSONArray partsArray = new JSONArray();
                partsArray.put(part);
                JSONObject content = new JSONObject();
                content.put("parts", partsArray);
                JSONArray contentsArray = new JSONArray();
                contentsArray.put(content);
                JSONObject requestBody = new JSONObject();
                requestBody.put("contents", contentsArray);

                OutputStream os = conn.getOutputStream();
                os.write(requestBody.toString().getBytes("UTF-8"));
                os.close();

                int responseCode = conn.getResponseCode();
                if (responseCode == 200) {
                    BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                    StringBuilder response = new StringBuilder();
                    String inputLine;
                    while ((inputLine = in.readLine()) != null) response.append(inputLine);
                    in.close();

                    JSONObject jsonResponse = new JSONObject(response.toString());
                    String aiText = jsonResponse.getJSONArray("candidates")
                            .getJSONObject(0).getJSONObject("content").getJSONArray("parts")
                            .getJSONObject(0).getString("text");

                    handler.post(() -> {
                        progressBar.setVisibility(View.GONE);
                        tvResultText.setVisibility(View.VISIBLE);
                        tvResultText.setText(aiText);
                    });
                } else {
                    BufferedReader in = new BufferedReader(new InputStreamReader(conn.getErrorStream()));
                    StringBuilder errorResponse = new StringBuilder();
                    String inputLine;
                    while ((inputLine = in.readLine()) != null) errorResponse.append(inputLine);
                    in.close();
                    
                    final String errorMsg = "Lỗi API (" + responseCode + "): " + errorResponse.toString();
                    handler.post(() -> {
                        progressBar.setVisibility(View.GONE);
                        tvResultText.setVisibility(View.VISIBLE);
                        tvResultText.setText(errorMsg);
                    });
                }

            } catch (Exception e) {
                final String exMsg = "Lỗi Exception: " + e.getMessage();
                handler.post(() -> {
                    progressBar.setVisibility(View.GONE);
                    tvResultText.setVisibility(View.VISIBLE);
                    tvResultText.setText(exMsg);
                });
            }
        });

        dialog.show();
    }

    private void extractPlainText(String html) {
        try {
            Document doc = Jsoup.parse(html);
            plainTextContent = doc.text();
        } catch (Exception e) {
            plainTextContent = "";
        }
    }

    private void renderWebView(String finalHtml) {
        currentArticleHtml = finalHtml;
        extractPlainText(finalHtml);
        
        int fontSize = preferencesManager.getFontSize();
        String fontFamily = preferencesManager.getFontFamily();
        String bgColor = preferencesManager.getReaderBackground();
        
        String bgHex = "#FFFFFF";
        String textHex = "#333333";
        if ("dark".equals(bgColor)) {
            bgHex = "#121212";
            textHex = "#E0E0E0";
        } else if ("sepia".equals(bgColor)) {
            bgHex = "#F5E6CC";
            textHex = "#433422";
        }
        
        String styledHtml = "<html><head><meta charset=\"UTF-8\"><style>" +
                "img{max-width: 100%; height: auto;} " +
                "body{padding: 16px; font-size: " + fontSize + "px; font-family: " + fontFamily + "; background-color: " + bgHex + "; color: " + textHex + "; line-height: 1.6;}" +
                "</style></head><body>" + finalHtml + "</body></html>";
                
        webViewContent.loadDataWithBaseURL(currentArticleLink, styledHtml, "text/html; charset=utf-8", "UTF-8", null);
        webViewContent.setVisibility(View.VISIBLE);
        layoutTtsControls.setVisibility(View.VISIBLE);
    }

    private void scrapeArticleContent(String url) {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());
        executor.execute(() -> {
            try {
                Document document = Jsoup.connect(url)
                        .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/100.0.4896.127 Safari/537.36")
                        .get();

                Element contentElement = document.selectFirst(".detail-cmain");
                if (contentElement == null) {
                    contentElement = document.selectFirst(".detail-content");
                }

                final String finalHtml = (contentElement != null) ? contentElement.html() : getString(R.string.no_content);
                handler.post(() -> {
                    progressBarDetail.setVisibility(View.GONE);
                    if (!finalHtml.equals(getString(R.string.no_content))) {
                        renderWebView(finalHtml);
                    } else {
                        Toast.makeText(DetailActivity.this, finalHtml, Toast.LENGTH_SHORT).show();
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
                handler.post(() -> {
                    progressBarDetail.setVisibility(View.GONE);
                    Toast.makeText(DetailActivity.this, getString(R.string.connection_error), Toast.LENGTH_SHORT).show();
                });
            }
        });
    }

    @Override
    protected void onDestroy() {
        if (textToSpeech != null) {
            textToSpeech.stop();
            textToSpeech.shutdown();
        }
        super.onDestroy();
    }
}
