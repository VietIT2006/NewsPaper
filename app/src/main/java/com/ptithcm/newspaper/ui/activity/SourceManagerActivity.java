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
import com.ptithcm.newspaper.data.model.RssSource;
import com.ptithcm.newspaper.util.PreferencesManager;

import java.util.ArrayList;
import java.util.List;

public class SourceManagerActivity extends AppCompatActivity {

    private PreferencesManager preferencesManager;
    private RecyclerView recyclerSources;
    private SourceAdapter adapter;
    private List<RssSource> sourceList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_source_manager);

        Toolbar toolbar = findViewById(R.id.toolbarSources);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("📡 Nguồn tin tức");
        }

        preferencesManager = new PreferencesManager(this);

        // Load sources or initialize defaults
        sourceList = preferencesManager.getRssSources();
        if (sourceList == null || sourceList.isEmpty()) {
            sourceList = getDefaultSources();
            preferencesManager.saveRssSources(sourceList);
        }

        recyclerSources = findViewById(R.id.recyclerSources);
        recyclerSources.setLayoutManager(new LinearLayoutManager(this));
        adapter = new SourceAdapter();
        recyclerSources.setAdapter(adapter);

        Button btnAddSource = findViewById(R.id.btnAddSource);
        btnAddSource.setOnClickListener(v -> showAddSourceDialog());
    }

    private List<RssSource> getDefaultSources() {
        List<RssSource> defaults = new ArrayList<>();
        defaults.add(new RssSource("Thanh Niên", "https://thanhnien.vn/rss/home.rss", true));
        defaults.add(new RssSource("VnExpress", "https://vnexpress.net/rss/tin-moi-nhat.rss", false));
        defaults.add(new RssSource("Tuổi Trẻ", "https://tuoitre.vn/rss/tin-moi-nhat.rss", false));
        defaults.add(new RssSource("Dân Trí", "https://dantri.com.vn/rss/home.rss", false));
        defaults.add(new RssSource("Zing News", "https://zingnews.vn/rss/tin-moi.rss", false));
        return defaults;
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
                    sourceList.add(new RssSource(name, url, true));
                    adapter.notifyItemInserted(sourceList.size() - 1);
                    saveSources();
                    Toast.makeText(this, "Đã thêm nguồn tin: " + name, Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton("Hủy", null)
                .show();
    }

    private void saveSources() {
        preferencesManager.saveRssSources(sourceList);
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

            // Remove listener before setting checked to avoid triggering
            holder.switchSource.setOnCheckedChangeListener(null);
            holder.switchSource.setChecked(source.isEnabled());
            holder.switchSource.setOnCheckedChangeListener((buttonView, isChecked) -> {
                source.setEnabled(isChecked);
                saveSources();
            });
        }

        @Override
        public int getItemCount() {
            return sourceList != null ? sourceList.size() : 0;
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
