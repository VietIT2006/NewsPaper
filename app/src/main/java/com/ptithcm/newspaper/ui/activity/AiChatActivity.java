package com.ptithcm.newspaper.ui.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;

import com.ptithcm.newspaper.R;
import com.ptithcm.newspaper.data.model.ChatMessage;
import com.ptithcm.newspaper.ui.adapter.ChatAdapter;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class AiChatActivity extends AppCompatActivity {

    private static final String GEMINI_API_KEY = "REMOVED";

    private RecyclerView recyclerChat;
    private EditText etChatInput;
    private Button btnSend;
    private ProgressBar progressBarChat;

    private ChatAdapter chatAdapter;
    private List<ChatMessage> chatMessages;

    private String articleContent;
    private String articleTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ai_chat);

        // Setup toolbar
        Toolbar toolbar = findViewById(R.id.toolbarChat);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("\uD83D\uDCAC Chat AI");
        }

        // Get article data from intent
        articleContent = getIntent().getStringExtra("ARTICLE_CONTENT");
        articleTitle = getIntent().getStringExtra("ARTICLE_TITLE");

        if (articleContent == null) articleContent = "";
        if (articleTitle == null) articleTitle = "Bài viết";

        // Init views
        recyclerChat = findViewById(R.id.recyclerChat);
        etChatInput = findViewById(R.id.etChatInput);
        btnSend = findViewById(R.id.btnSend);
        progressBarChat = findViewById(R.id.progressBarChat);

        // Setup RecyclerView
        chatMessages = new ArrayList<>();
        chatAdapter = new ChatAdapter(this, chatMessages);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setStackFromEnd(true);
        recyclerChat.setLayoutManager(layoutManager);
        recyclerChat.setAdapter(chatAdapter);

        // Add welcome message from AI
        String welcomeMessage = "Xin chào! Tôi đã đọc bài viết \"" + articleTitle + "\". Bạn muốn hỏi gì về bài viết này?";
        addMessage(welcomeMessage, false);

        // Send button click
        btnSend.setOnClickListener(v -> {
            String userInput = etChatInput.getText().toString().trim();
            if (!userInput.isEmpty()) {
                addMessage(userInput, true);
                etChatInput.setText("");
                sendToGemini(userInput);
            }
        });
    }

    private void addMessage(String message, boolean isUser) {
        chatMessages.add(new ChatMessage(message, isUser, System.currentTimeMillis()));
        chatAdapter.notifyItemInserted(chatMessages.size() - 1);
        recyclerChat.scrollToPosition(chatMessages.size() - 1);
    }

    private void sendToGemini(String userQuestion) {
        // Show loading, disable send
        progressBarChat.setVisibility(View.VISIBLE);
        btnSend.setEnabled(false);

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

                // Build prompt with article context
                String prompt = "Dựa trên nội dung bài viết sau:\n\n" + articleContent + "\n\nCâu hỏi của người dùng: " + userQuestion;

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
                    while ((inputLine = in.readLine()) != null) {
                        response.append(inputLine);
                    }
                    in.close();

                    JSONObject jsonResponse = new JSONObject(response.toString());
                    String aiText = jsonResponse.getJSONArray("candidates")
                            .getJSONObject(0)
                            .getJSONObject("content")
                            .getJSONArray("parts")
                            .getJSONObject(0)
                            .getString("text");

                    handler.post(() -> {
                        progressBarChat.setVisibility(View.GONE);
                        btnSend.setEnabled(true);
                        addMessage(aiText, false);
                    });
                } else {
                    BufferedReader in = new BufferedReader(new InputStreamReader(conn.getErrorStream()));
                    StringBuilder errorResponse = new StringBuilder();
                    String inputLine;
                    while ((inputLine = in.readLine()) != null) {
                        errorResponse.append(inputLine);
                    }
                    in.close();

                    final String errorMsg = "Lỗi API (" + responseCode + "): " + errorResponse.toString();
                    handler.post(() -> {
                        progressBarChat.setVisibility(View.GONE);
                        btnSend.setEnabled(true);
                        addMessage(errorMsg, false);
                    });
                }

            } catch (Exception e) {
                e.printStackTrace();
                final String exMsg = "Lỗi Exception: " + e.getMessage();
                handler.post(() -> {
                    progressBarChat.setVisibility(View.GONE);
                    btnSend.setEnabled(true);
                    addMessage(exMsg, false);
                });
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
