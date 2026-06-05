package com.ptithcm.newspaper.ui.adapter;

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.ptithcm.newspaper.R;
import com.ptithcm.newspaper.data.model.ChatMessage;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ChatViewHolder> {

    private static final int VIEW_TYPE_USER = 1;
    private static final int VIEW_TYPE_AI = 2;

    private Context context;
    private List<ChatMessage> messageList;

    public ChatAdapter(Context context, List<ChatMessage> messageList) {
        this.context = context;
        this.messageList = messageList;
    }

    @Override
    public int getItemViewType(int position) {
        return messageList.get(position).isUser() ? VIEW_TYPE_USER : VIEW_TYPE_AI;
    }

    @NonNull
    @Override
    public ChatViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_chat_message, parent, false);
        return new ChatViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ChatViewHolder holder, int position) {
        ChatMessage message = messageList.get(position);

        holder.tvMessage.setText(message.getMessage());

        // Format timestamp
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm", Locale.getDefault());
        holder.tvTimestamp.setText(sdf.format(new Date(message.getTimestamp())));

        // Configure alignment and bubble style based on message type
        FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) holder.bubbleContainer.getLayoutParams();

        if (message.isUser()) {
            // User message: right-aligned, blue bubble
            params.gravity = Gravity.END;
            holder.bubbleContainer.setLayoutParams(params);
            holder.bubbleContainer.setBackgroundResource(R.drawable.bg_chat_bubble_user);
            holder.tvEmoji.setText("👤");
            holder.tvMessage.setTextColor(context.getResources().getColor(R.color.textPrimary));
        } else {
            // AI message: left-aligned, gray bubble
            params.gravity = Gravity.START;
            holder.bubbleContainer.setLayoutParams(params);
            holder.bubbleContainer.setBackgroundResource(R.drawable.bg_chat_bubble_ai);
            holder.tvEmoji.setText("🤖");
            holder.tvMessage.setTextColor(context.getResources().getColor(R.color.textPrimary));
        }
    }

    @Override
    public int getItemCount() {
        return messageList == null ? 0 : messageList.size();
    }

    public static class ChatViewHolder extends RecyclerView.ViewHolder {
        LinearLayout bubbleContainer;
        TextView tvEmoji, tvMessage, tvTimestamp;

        public ChatViewHolder(@NonNull View itemView) {
            super(itemView);
            bubbleContainer = itemView.findViewById(R.id.bubbleContainer);
            tvEmoji = itemView.findViewById(R.id.tvEmoji);
            tvMessage = itemView.findViewById(R.id.tvMessage);
            tvTimestamp = itemView.findViewById(R.id.tvTimestamp);
        }
    }
}
