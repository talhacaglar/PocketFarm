package com.tarlamcebimde.app.ui.main;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.tarlamcebimde.app.R;
import com.tarlamcebimde.app.adapter.ChatListAdapter;
import com.tarlamcebimde.app.model.Chat;
import com.tarlamcebimde.app.repository.ChatRepository;
import com.tarlamcebimde.app.ui.chat.ChatActivity;
import com.tarlamcebimde.app.util.Constants;
import com.tarlamcebimde.app.util.FirebaseHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * Sohbet listesi fragment'ı
 */
public class ChatListFragment extends Fragment implements ChatListAdapter.OnChatClickListener {

    private RecyclerView rvChats;
    private TextView tvEmpty;
    private ChatListAdapter adapter;
    private ChatRepository chatRepository;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_chat_list, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        chatRepository = new ChatRepository();
        initViews(view);
        setupRecyclerView();
        loadChats();
    }

    private void initViews(View view) {
        rvChats = view.findViewById(R.id.rv_chats);
        tvEmpty = view.findViewById(R.id.tv_empty);
    }

    private void setupRecyclerView() {
        String currentUserId = FirebaseHelper.getInstance().getCurrentUserId();
        adapter = new ChatListAdapter(new ArrayList<>(), currentUserId, this);
        rvChats.setLayoutManager(new LinearLayoutManager(requireContext()));
        rvChats.setAdapter(adapter);
    }

    private void loadChats() {
        String userId = FirebaseHelper.getInstance().getCurrentUserId();
        if (userId == null) return;

        chatRepository.getUserChats(userId).observe(getViewLifecycleOwner(), chats -> {
            if (chats == null || chats.isEmpty()) {
                tvEmpty.setVisibility(View.VISIBLE);
                rvChats.setVisibility(View.GONE);
            } else {
                tvEmpty.setVisibility(View.GONE);
                rvChats.setVisibility(View.VISIBLE);
                adapter.updateChats(chats);
            }
        });
    }

    @Override
    public void onChatClick(Chat chat) {
        Intent intent = new Intent(requireContext(), ChatActivity.class);
        intent.putExtra(Constants.EXTRA_CHAT_ID, chat.getChatId());
        startActivity(intent);
    }
}
