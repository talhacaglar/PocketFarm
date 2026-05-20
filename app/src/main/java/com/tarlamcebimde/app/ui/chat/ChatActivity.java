package com.tarlamcebimde.app.ui.chat;

import android.os.Bundle;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.tarlamcebimde.app.R;
import com.tarlamcebimde.app.adapter.MessageAdapter;
import com.tarlamcebimde.app.model.Message;
import com.tarlamcebimde.app.repository.ChatRepository;
import com.tarlamcebimde.app.util.Constants;
import com.tarlamcebimde.app.util.FirebaseHelper;

import java.util.ArrayList;

public class ChatActivity extends AppCompatActivity {
    private RecyclerView rvMessages;
    private EditText etMessage;
    private ImageButton btnSend;
    private Toolbar toolbar;
    private TextView tvChatTitle;
    private MessageAdapter adapter;
    private ChatRepository chatRepository;
    private String chatId, currentUserId, currentUserName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        chatRepository = new ChatRepository();
        chatId = getIntent().getStringExtra(Constants.EXTRA_CHAT_ID);
        currentUserId = FirebaseHelper.getInstance().getCurrentUserId();
        if (chatId == null || currentUserId == null) { finish(); return; }

        rvMessages = findViewById(R.id.rv_messages);
        etMessage = findViewById(R.id.et_message);
        btnSend = findViewById(R.id.btn_send);
        toolbar = findViewById(R.id.toolbar);
        tvChatTitle = findViewById(R.id.tv_chat_title);

        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("");
        }
        toolbar.setNavigationOnClickListener(v -> onBackPressed());

        adapter = new MessageAdapter(new ArrayList<>(), currentUserId);
        LinearLayoutManager lm = new LinearLayoutManager(this);
        lm.setStackFromEnd(true);
        rvMessages.setLayoutManager(lm);
        rvMessages.setAdapter(adapter);

        FirebaseHelper.getInstance().getDb().collection(Constants.COLLECTION_USERS)
                .document(currentUserId).get()
                .addOnSuccessListener(doc -> { if (doc.exists()) currentUserName = doc.getString("fullName"); });

        FirebaseHelper.getInstance().getDb().collection(Constants.COLLECTION_CHATS)
                .document(chatId).get()
                .addOnSuccessListener(doc -> {
                    if (doc.exists()) tvChatTitle.setText(doc.getString("productTitle"));
                });

        chatRepository.getMessages(chatId).observe(this, messages -> {
            if (messages == null) return;
            adapter.updateMessages(messages);
            if (!messages.isEmpty()) rvMessages.scrollToPosition(messages.size() - 1);
        });

        btnSend.setOnClickListener(v -> {
            String text = etMessage.getText().toString().trim();
            if (text.isEmpty() || currentUserName == null) return;
            chatRepository.sendMessage(chatId, Message.createTextMessage(currentUserId, currentUserName, text));
            etMessage.setText("");
        });
    }
}
