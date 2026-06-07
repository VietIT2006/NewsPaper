package com.ptithcm.newspaper.ui.activity;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.ptithcm.newspaper.R;
import com.ptithcm.newspaper.data.model.AuthResponse;
import com.ptithcm.newspaper.data.model.RssSource;
import com.ptithcm.newspaper.data.remote.BackendApiClient;
import com.ptithcm.newspaper.data.remote.UserApi;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SourceManagerActivity extends AppCompatActivity {

    private RecyclerView recyclerSources;
    private SourceAdapter adapter;
    private List<RssSource> sourceList = new ArrayList<>();
    private UserApi userApi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_source_manager);

        Toolbar toolbar = findViewById(R.id.toolbarSources);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("📡 Quản lý nguồn tin (Global)");
        }

        userApi = BackendApiClient.getClient().create(UserApi.class);

        recyclerSources = findViewById(R.id.recyclerSources);
        recyclerSources.setLayoutManager(new LinearLayoutManager(this));
        adapter = new SourceAdapter();
        recyclerSources.setAdapter(adapter);

        Button btnAddSource = findViewById(R.id.btnAddSource);
        btnAddSource.setOnClickListener(v -> showAddSourceDialog());

        loadSources();
    }

    private void loadSources() {
        userApi.getSources().enqueue(new Callback<UserApi.SourceResponse>() {
            @Override
            public void onResponse(Call<UserApi.SourceResponse> call, Response<UserApi.SourceResponse> response) {
                if (response.isSuccessful() && response.body() != null && response.body().success) {
                    sourceList.clear();
                    if (response.body().sources != null) {
                        sourceList.addAll(response.body().sources);
                    }
                    adapter.notifyDataSetChanged();
                } else {
                    Toast.makeText(SourceManagerActivity.this, "Lỗi lấy dữ liệu từ server", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<UserApi.SourceResponse> call, Throwable t) {
                Toast.makeText(SourceManagerActivity.this, "Lỗi kết nối", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showAddSourceDialog() {
        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        int padding = (int) (16 * getResources().getDisplayMetrics().density);
        layout.setPadding(padding, padding, padding, padding);

        EditText etName = new EditText(this);
        etName.setHint("Tên nguồn tin (VD: Thanh Niên)");
        layout.addView(etName);

        EditText etUrl = new EditText(this);
        etUrl.setHint("URL nguồn RSS (VD: https://...)");
        layout.addView(etUrl);

        new AlertDialog.Builder(this)
                .setTitle("Thêm nguồn tin mới")
                .setView(layout)
                .setPositiveButton("Thêm", (dialog, which) -> {
                    String name = etName.getText().toString().trim();
                    String url = etUrl.getText().toString().trim();
                    if (TextUtils.isEmpty(name) || TextUtils.isEmpty(url)) {
                        Toast.makeText(this, "Vui lòng nhập đầy đủ thông tin", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    
                    UserApi.SourceRequest req = new UserApi.SourceRequest(name, url);
                    userApi.addSource(req).enqueue(new Callback<AuthResponse>() {
                        @Override
                        public void onResponse(Call<AuthResponse> call, Response<AuthResponse> response) {
                            if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                                Toast.makeText(SourceManagerActivity.this, "Đã thêm thành công", Toast.LENGTH_SHORT).show();
                                loadSources(); // Tải lại danh sách
                            } else {
                                Toast.makeText(SourceManagerActivity.this, "Thất bại", Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onFailure(Call<AuthResponse> call, Throwable t) {
                            Toast.makeText(SourceManagerActivity.this, "Lỗi mạng", Toast.LENGTH_SHORT).show();
                        }
                    });
                })
                .setNegativeButton("Hủy", null)
                .show();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    // --- Inline Adapter ---
    private class SourceAdapter extends RecyclerView.Adapter<SourceAdapter.SourceViewHolder> {

        @Override
        public SourceViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_rss_source, parent, false);
            return new SourceViewHolder(view);
        }

        @Override
        public void onBindViewHolder(SourceViewHolder holder, int position) {
            RssSource source = sourceList.get(position);
            holder.tvSourceName.setText(source.getName());
            holder.tvSourceUrl.setText(source.getUrl());

            holder.switchSource.setOnCheckedChangeListener(null);
            holder.switchSource.setChecked(source.isEnabled());
            holder.switchSource.setOnCheckedChangeListener((buttonView, isChecked) -> {
                UserApi.SourceToggleRequest req = new UserApi.SourceToggleRequest(isChecked);
                userApi.toggleSource(source.getId(), req).enqueue(new Callback<AuthResponse>() {
                    @Override
                    public void onResponse(Call<AuthResponse> call, Response<AuthResponse> response) {
                        if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                            source.setEnabled(isChecked);
                        } else {
                            holder.switchSource.setChecked(!isChecked); // revert
                            Toast.makeText(SourceManagerActivity.this, "Lỗi cập nhật", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<AuthResponse> call, Throwable t) {
                        holder.switchSource.setChecked(!isChecked); // revert
                        Toast.makeText(SourceManagerActivity.this, "Lỗi mạng", Toast.LENGTH_SHORT).show();
                    }
                });
            });
        }

        @Override
        public int getItemCount() {
            return sourceList.size();
        }

        class SourceViewHolder extends RecyclerView.ViewHolder {
            TextView tvSourceName;
            TextView tvSourceUrl;
            Switch switchSource;

            SourceViewHolder(View itemView) {
                super(itemView);
                tvSourceName = itemView.findViewById(R.id.tvSourceName);
                tvSourceUrl = itemView.findViewById(R.id.tvSourceUrl);
                switchSource = itemView.findViewById(R.id.switchSource);
            }
        }
    }
}
