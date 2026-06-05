package com.ptithcm.newspaper.ui.activity;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.ptithcm.newspaper.R;
import com.ptithcm.newspaper.data.model.Article;
import com.ptithcm.newspaper.ui.adapter.ArticleAdapter;
import com.ptithcm.newspaper.util.PreferencesManager;

import java.util.List;

public class FavoritesActivity extends AppCompatActivity {

    private RecyclerView recyclerFavorites;
    private ArticleAdapter adapter;
    private TextView tvEmptyMessage;
    private PreferencesManager preferencesManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorites);

        Toolbar toolbar = findViewById(R.id.toolbarFavorites);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle(getString(R.string.favorites_title));
        }

        recyclerFavorites = findViewById(R.id.recyclerFavorites);
        tvEmptyMessage = findViewById(R.id.tvEmptyMessage);
        preferencesManager = new PreferencesManager(this);

        recyclerFavorites.setLayoutManager(new LinearLayoutManager(this));
        loadFavorites();
    }

    private void loadFavorites() {
        List<Article> favorites = preferencesManager.getFavorites();

        if (favorites.isEmpty()) {
            tvEmptyMessage.setText(getString(R.string.empty_favorites));
            recyclerFavorites.setVisibility(View.GONE);
            tvEmptyMessage.setVisibility(View.VISIBLE);
        } else {
            tvEmptyMessage.setVisibility(View.GONE);
            recyclerFavorites.setVisibility(View.VISIBLE);
            adapter = new ArticleAdapter(this, favorites);
            adapter.setOnItemLongClickListener(article -> showNoteDialog(article));
            recyclerFavorites.setAdapter(adapter);
        }
    }

    private void showNoteDialog(Article article) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View view = getLayoutInflater().inflate(R.layout.dialog_bookmark_note, null);
        builder.setView(view);
        AlertDialog dialog = builder.create();

        EditText etNote = view.findViewById(R.id.etNote);
        EditText etTags = view.findViewById(R.id.etTags);
        Button btnCancel = view.findViewById(R.id.btnCancelNote);
        Button btnSave = view.findViewById(R.id.btnSaveNote);

        if (article.getNote() != null) etNote.setText(article.getNote());
        if (article.getTags() != null) etTags.setText(article.getTags());

        btnCancel.setOnClickListener(v -> dialog.dismiss());
        btnSave.setOnClickListener(v -> {
            article.setNote(etNote.getText().toString().trim());
            article.setTags(etTags.getText().toString().trim());
            preferencesManager.updateFavorite(article);
            Toast.makeText(this, getString(R.string.bookmark_saved), Toast.LENGTH_SHORT).show();
            dialog.dismiss();
            loadFavorites(); // Refresh list to show note/tag UI
        });

        dialog.show();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadFavorites();
    }
}
