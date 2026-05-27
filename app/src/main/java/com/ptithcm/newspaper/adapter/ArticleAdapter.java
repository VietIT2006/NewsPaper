package com.ptithcm.newspaper.adapter;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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
import java.util.ArrayList;
import java.util.List;

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

        // 1. Sửa lỗi font chữ tiêu đề (Double Decoding)
        String decodedTitle = article.getTitle();
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            decodedTitle = android.text.Html.fromHtml(decodedTitle, android.text.Html.FROM_HTML_MODE_COMPACT).toString();
            decodedTitle = android.text.Html.fromHtml(decodedTitle, android.text.Html.FROM_HTML_MODE_COMPACT).toString();
        } else {
            decodedTitle = android.text.Html.fromHtml(decodedTitle).toString();
            decodedTitle = android.text.Html.fromHtml(decodedTitle).toString();
        }
        holder.tvTitle.setText(decodedTitle);
        holder.tvDate.setText(article.getPubDate());

        // 2. Lấy ảnh, nếu rỗng thì bóc tách từ Description (Tab Thể thao)
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

        // 3. Sự kiện bấm 1 lần: Mở DetailActivity
        holder.itemView.setOnClickListener(view -> {
            Intent intent = new Intent(context, DetailActivity.class);
            intent.putExtra("ARTICLE_LINK", article.getLink());
            context.startActivity(intent);
        });

        // 4. Sự kiện nhấn giữ (Long Click): Lưu bài viết (Bookmark) bằng Gson
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

    // Hàm Lọc danh sách cho tính năng Tìm kiếm
    public void filterList(List<Article> filteredList) {
        this.articleList = filteredList;
        notifyDataSetChanged();
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