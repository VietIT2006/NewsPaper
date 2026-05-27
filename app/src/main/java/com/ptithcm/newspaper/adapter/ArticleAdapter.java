package com.ptithcm.newspaper.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.ptithcm.newspaper.R;
import com.ptithcm.newspaper.model.Article;
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

        holder.tvTitle.setText(article.getTitle());
        holder.tvDate.setText(article.getPubDate());

        // Dùng Glide để tự động tải hình ảnh từ URL và đưa vào ImageView
        Glide.with(context)
                .load(article.getThumbnail())
                .placeholder(R.drawable.ic_launcher_background) // Ảnh chờ khi đang tải
                .into(holder.imgThumbnail);

        // Lắng nghe sự kiện click vào bài báo (sau này sẽ dùng Jsoup mở chi tiết ở đây)
        holder.itemView.setOnClickListener(v -> {
            // Lắng nghe sự kiện click vào bài báo
            holder.itemView.setOnClickListener(view -> {
                android.content.Intent intent = new android.content.Intent(context, com.ptithcm.newspaper.DetailActivity.class);
                // Gói đường link của bài báo để gửi sang màn hình Detail
                intent.putExtra("ARTICLE_LINK", article.getLink());
                context.startActivity(intent);
            });
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