package com.ptithcm.newspaper.adapter;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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
import com.ptithcm.newspaper.DetailActivity;
import com.ptithcm.newspaper.R;
import com.ptithcm.newspaper.model.Article;

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
            // Định dạng gốc từ rss2json trả về
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
            Date dateObj = sdf.parse(rawDate);

            SimpleDateFormat dateOnly = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            String articleDateStr = dateOnly.format(dateObj);
            String todayStr = dateOnly.format(new Date());

            SimpleDateFormat timeOnly = new SimpleDateFormat("HH:mm", Locale.getDefault());
            SimpleDateFormat fullFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());

            if (articleDateStr.equals(todayStr)) {
                holder.tvDate.setText("Hôm nay, " + timeOnly.format(dateObj));
                holder.tvDate.setTextColor(Color.parseColor("#E53935"));
                holder.tvDate.setTypeface(null, Typeface.BOLD);
            } else {
                // Ngày cũ: Hiển thị bình thường màu xám
                holder.tvDate.setText(fullFormat.format(dateObj));
                holder.tvDate.setTextColor(Color.parseColor("#757575"));
                holder.tvDate.setTypeface(null, Typeface.NORMAL);
            }
        } catch (Exception e) {
            holder.tvDate.setText(rawDate);
        }

        // 3. Lấy ảnh, bóc tách ảnh bằng Jsoup cho Tab Thể thao
        String imageUrl = article.getThumbnail();
        if (imageUrl == null || imageUrl.isEmpty()) {
            if (article.getDescription() != null) {
                org.jsoup.nodes.Document doc = org.jsoup.Jsoup.parse(article.getDescription());
                org.jsoup.nodes.Element img = doc.selectFirst("img");
                if (img != null) {
                    imageUrl = img.attr("src");
                }
            }
        }

        Glide.with(context).load(imageUrl).placeholder(R.drawable.ic_launcher_background).error(R.drawable.ic_launcher_background).into(holder.imgThumbnail);

        // 4. Sự kiện bấm 1 lần: Mở DetailActivity
        holder.itemView.setOnClickListener(view -> {
            Intent intent = new Intent(context, DetailActivity.class);
            intent.putExtra("ARTICLE_LINK", article.getLink());
            context.startActivity(intent);
        });

        // 5. Sự kiện nhấn giữ (Long Click): Lưu bài báo
        holder.itemView.setOnLongClickListener(view -> {
            SharedPreferences prefs = context.getSharedPreferences("FAVORITES", Context.MODE_PRIVATE);
            String savedArticlesJson = prefs.getString("articles", "[]");

            Type type = new TypeToken<ArrayList<Article>>(){}.getType();
            ArrayList<Article> savedList = new Gson().fromJson(savedArticlesJson, type);

            savedList.add(article);
            prefs.edit().putString("articles", new Gson().toJson(savedList)).apply();

            Toast.makeText(context, "Đã lưu bài viết vào mục Yêu thích!", Toast.LENGTH_SHORT).show();
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