package com.tarlamcebimde.app.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.tarlamcebimde.app.R;
import com.tarlamcebimde.app.model.Message;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.ViewHolder> {
    private static final int TYPE_SENT = 0;
    private static final int TYPE_RECEIVED = 1;
    private static final int TYPE_SYSTEM = 2;
    private List<Message> messages;
    private final String currentUserId;

    public MessageAdapter(List<Message> messages, String currentUserId) {
        this.messages = messages; this.currentUserId = currentUserId;
    }

    public void updateMessages(List<Message> messages) { this.messages = messages; notifyDataSetChanged(); }

    @Override
    public int getItemViewType(int position) {
        Message msg = messages.get(position);
        if (msg.isSystemMessage()) return TYPE_SYSTEM;
        return msg.isSentBy(currentUserId) ? TYPE_SENT : TYPE_RECEIVED;
    }

    @NonNull @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        int layout;
        if (viewType == TYPE_SENT) layout = R.layout.item_message_sent;
        else if (viewType == TYPE_RECEIVED) layout = R.layout.item_message_received;
        else layout = R.layout.item_message_system;
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(layout, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Message msg = messages.get(position);
        holder.tvMessage.setText(msg.getText());
        if (holder.tvTime != null && msg.getTimestamp() != null) {
            SimpleDateFormat sdf = new SimpleDateFormat("HH:mm", Locale.getDefault());
            holder.tvTime.setText(sdf.format(msg.getTimestamp().toDate()));
        }
        if (holder.tvSender != null) holder.tvSender.setText(msg.getSenderName());
    }

    @Override public int getItemCount() { return messages.size(); }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvMessage, tvTime, tvSender;
        ViewHolder(View v) {
            super(v);
            tvMessage = v.findViewById(R.id.tv_message);
            tvTime = v.findViewById(R.id.tv_time);
            tvSender = v.findViewById(R.id.tv_sender);
        }
    }
}
