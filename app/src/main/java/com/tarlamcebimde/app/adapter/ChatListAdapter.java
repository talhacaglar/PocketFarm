package com.tarlamcebimde.app.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.tarlamcebimde.app.R;
import com.tarlamcebimde.app.model.Chat;
import de.hdodenhof.circleimageview.CircleImageView;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class ChatListAdapter extends RecyclerView.Adapter<ChatListAdapter.ViewHolder> {
    private List<Chat> chats;
    private final String currentUserId;
    private final OnChatClickListener listener;

    public interface OnChatClickListener { void onChatClick(Chat chat); }

    public ChatListAdapter(List<Chat> chats, String currentUserId, OnChatClickListener listener) {
        this.chats = chats; this.currentUserId = currentUserId; this.listener = listener;
    }

    public void updateChats(List<Chat> chats) { this.chats = chats; notifyDataSetChanged(); }

    @NonNull @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_chat, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Chat chat = chats.get(position);
        holder.tvName.setText(chat.getOtherParticipantName(currentUserId));
        holder.tvProductTitle.setText(chat.getProductTitle());
        holder.tvLastMessage.setText(chat.getLastMessage() != null ? chat.getLastMessage() : "");

        if (chat.getLastMessageTime() != null) {
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM HH:mm", Locale.getDefault());
            holder.tvTime.setText(sdf.format(chat.getLastMessageTime().toDate()));
        }

        String imageUrl = chat.getOtherParticipantImage(currentUserId);
        if (imageUrl != null && !imageUrl.isEmpty()) {
            Glide.with(holder.itemView.getContext()).load(imageUrl)
                    .placeholder(R.drawable.ic_person).into(holder.ivPhoto);
        }

        holder.itemView.setOnClickListener(v -> listener.onChatClick(chat));
    }

    @Override public int getItemCount() { return chats.size(); }

    static class ViewHolder extends RecyclerView.ViewHolder {
        CircleImageView ivPhoto;
        TextView tvName, tvProductTitle, tvLastMessage, tvTime;
        ViewHolder(View v) {
            super(v);
            ivPhoto = v.findViewById(R.id.iv_chat_photo);
            tvName = v.findViewById(R.id.tv_chat_name);
            tvProductTitle = v.findViewById(R.id.tv_chat_product);
            tvLastMessage = v.findViewById(R.id.tv_last_message);
            tvTime = v.findViewById(R.id.tv_chat_time);
        }
    }
}
