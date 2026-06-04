package com.ptithcm.newspaper.ui.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.ptithcm.newspaper.R;
import com.ptithcm.newspaper.data.model.Article;
import com.ptithcm.newspaper.ui.activity.DetailActivity;
import com.ptithcm.newspaper.util.PreferencesManager;

import org.jsoup.Jsoup;

import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ArticleAdapter extends RecyclerView.Adapter<ArticleAdapter.ViewHolder> {
    private Context context;
    private List<Article> articleList;

    public ArticleAdapter(Context context, List<Article> articleList) {
        this.context = context;
        this.articleList = articleList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_article, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Article article = articleList.get(position);

        String decodedTitle = article.getTitle();
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            decodedTitle = android.text.Html.fromHtml(decodedTitle, android.text.Html.FROM_HTML_MODE_COMPACT).toString();
            decodedTitle = android.text.Html.fromHtml(decodedTitle, android.text.Html.FROM_HTML_MODE_COMPACT).toString();
        } else {
            decodedTitle = android.text.Html.fromHtml(decodedTitle).toString();
            decodedTitle = android.text.Html.fromHtml(decodedTitle).toString();
        }
        holder.tvTitle.setText(decodedTitle);

        String rawDate = article.getPubDate();
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
            Date dateObj = sdf.parse(rawDate);

            SimpleDateFormat dateOnly = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            String articleDateStr = dateOnly.format(dateObj);
            String todayStr = dateOnly.format(new Date());

            SimpleDateFormat timeOnly = new SimpleDateFormat("HH:mm", Locale.getDefault());
            SimpleDateFormat fullFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());

            if (articleDateStr.equals(todayStr)) {
                holder.tvDate.setText(context.getString(R.string.today) + ", " + timeOnly.format(dateObj));
                holder.tvDate.setTextColor(Color.parseColor("#E53935"));
                holder.tvDate.setTypeface(null, Typeface.BOLD);
            } else {
                holder.tvDate.setText(fullFormat.format(dateObj));
                holder.tvDate.setTextColor(Color.parseColor("#757575"));
                holder.tvDate.setTypeface(null, Typeface.NORMAL);
            }
        } catch (Exception e) {
            holder.tvDate.setText(rawDate);
        }

        String imageUrl = article.getThumbnail();
        if (imageUrl == null || imageUrl.isEmpty()) {
            if (article.getDescription() != null) {
                org.jsoup.nodes.Document doc = Jsoup.parse(article.getDescription());
                org.jsoup.nodes.Element img = doc.selectFirst("img");
                if (img != null) {
                    imageUrl = img.attr("src");
                }
            }
        }

        Glide.with(context).load(imageUrl).placeholder(R.drawable.ic_launcher_background).error(R.drawable.ic_launcher_background).into(holder.imgThumbnail);

        holder.itemView.setOnClickListener(view -> {
            Intent intent = new Intent(context, DetailActivity.class);
            intent.putExtra("ARTICLE_LINK", article.getLink());
            intent.putExtra("ARTICLE_TITLE", article.getTitle());
            intent.putExtra("ARTICLE_THUMBNAIL", article.getThumbnail());
            context.startActivity(intent);
        });

        holder.itemView.setOnLongClickListener(view -> {
            PreferencesManager prefs = new PreferencesManager(context);
            prefs.addFavorite(article);
            Toast.makeText(context, context.getString(R.string.saved_success), Toast.LENGTH_SHORT).show();
            return true;
        });
    }

    @Override
    public int getItemCount() {
        return articleList == null ? 0 : articleList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imgThumbnail;
        TextView tvTitle, tvDate;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imgThumbnail = itemView.findViewById(R.id.imgThumbnail);
            tvTitle = itemView.findViewById(R.id.tvTitle);
            tvDate = itemView.findViewById(R.id.tvDate);
        }
    }
}

