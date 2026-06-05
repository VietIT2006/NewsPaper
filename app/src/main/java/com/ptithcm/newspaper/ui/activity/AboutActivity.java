package com.ptithcm.newspaper.ui.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.TextView;

import com.ptithcm.newspaper.R;

public class AboutActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        Toolbar toolbar = findViewById(R.id.toolbarAbout);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle(getString(R.string.about_title));
        }

        TextView tvAppVersion = findViewById(R.id.tvAppVersion);
        TextView tvAppDescription = findViewById(R.id.tvAppDescription);
        TextView tvDeveloper = findViewById(R.id.tvDeveloper);

        tvAppVersion.setText(getString(R.string.about_version));
        tvAppDescription.setText(getString(R.string.about_description));
        tvDeveloper.setText(getString(R.string.about_developer));
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

