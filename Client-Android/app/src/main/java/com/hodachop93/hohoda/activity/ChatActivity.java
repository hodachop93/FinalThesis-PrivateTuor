package com.hodachop93.hohoda.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.hodachop93.hohoda.R;
import com.hodachop93.hohoda.adapter.MessageAdapter;
import com.hodachop93.hohoda.api.chat.ChatApi;
import com.hodachop93.hohoda.common.AppReferences;
import com.hodachop93.hohoda.gcm.MyGcmListenerService;
import com.hodachop93.hohoda.model.Conversation;
import com.hodachop93.hohoda.model.HohodaResponse;
import com.hodachop93.hohoda.model.Message;
import com.hodachop93.hohoda.utils.ClickGuard;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ChatActivity extends BaseActivity {
    @Bind(R.id.toolbar)
    Toolbar toolbar;
    @Bind(R.id.message)
    TextView tvMessage;
    @Bind(R.id.recycler_view)
    RecyclerView recyclerView;
    @Bind(R.id.btn_send)
    ImageView btnSend;

    private Conversation mConversation;
    private MessageAdapter mAdapter;
    private String mUserId;
    private boolean mIsReload;
    private BroadcastReceiver mChatBroadcastReceiver;
    private static ChatActivity instance;

    private Callback<HohodaResponse<Conversation>> mCallbackStartChat = new Callback<HohodaResponse<Conversation>>() {
        @Override
        public void onResponse(Call<HohodaResponse<Conversation>> call, Response<HohodaResponse<Conversation>> response) {
            if (response.isSuccess()) {
                String status = response.body().getStatus();
                if ("0".equals(status)) {
                    mConversation = response.body().getBody();
                    if (mConversation.getMemberOne().equals(mUserId)) {
                        getSupportActionBar().setTitle(mConversation.getPersonTwo().getName());
                    } else {
                        getSupportActionBar().setTitle(mConversation.getPersonOne().getName());
                    }

                    if (mConversation != null) {
                        makeGetAllMessagesRequest(mConversation.getId(), true);
                        mAdapter.setConversation(mConversation);
                    }
                } else {
                    Toast.makeText(ChatActivity.this, getString(R.string.start_chat_fail), Toast.LENGTH_SHORT).show();
                    finish();
                }
            } else {
                Toast.makeText(ChatActivity.this, getString(R.string.start_chat_fail), Toast.LENGTH_SHORT).show();
                finish();
            }
        }

        @Override
        public void onFailure(Call<HohodaResponse<Conversation>> call, Throwable t) {
            Toast.makeText(ChatActivity.this, getString(R.string.start_chat_fail), Toast.LENGTH_SHORT).show();
        }
    };

    private Callback<HohodaResponse<List<Message>>> mCallbackGetAllMessages = new Callback<HohodaResponse<List<Message>>>() {
        @Override
        public void onResponse(Call<HohodaResponse<List<Message>>> call, Response<HohodaResponse<List<Message>>> response) {
            mAdapter.finishLoadMore();
            if (response.isSuccess()) {
                String status = response.body().getStatus();
                if ("0".equals(status)) {
                    processReturnedData(response.body().getBody());
                }
            }
        }

        @Override
        public void onFailure(Call<HohodaResponse<List<Message>>> call, Throwable t) {
            mAdapter.finishLoadMore();
        }
    };

    private Callback<HohodaResponse<Message>> mCallbackAddNewMessage = new Callback<HohodaResponse<Message>>() {
        @Override
        public void onResponse(Call<HohodaResponse<Message>> call, Response<HohodaResponse<Message>> response) {
            if (response.isSuccess()) {
                String status = response.body().getStatus();
                if ("0".equals(status)) {
                    Message message = response.body().getBody();
                    mAdapter.addData(0, message);
                    tvMessage.setText(null);
                }
            } else {
                Toast.makeText(ChatActivity.this, getString(R.string.send_message_error), Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        public void onFailure(Call<HohodaResponse<Message>> call, Throwable t) {
            Toast.makeText(ChatActivity.this, getString(R.string.send_message_error), Toast.LENGTH_SHORT).show();
        }
    };


    public static Intent getIntent(Context context, String toUserId) {
        Intent intent = new Intent(context, ChatActivity.class);
        intent.putExtra("TO_USER_ID", toUserId);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        instance = this;
        setContentView(R.layout.activity_chat);
        ButterKnife.bind(this);

        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, true));
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        mUserId = AppReferences.getUserID();
        mAdapter = new MessageAdapter(this, null, mUserId, recyclerView);
        mAdapter.setLoadMoreEnabled(true);
        mAdapter.setLoadMoreListener(new MessageAdapter.LoadMoreListener() {
            @Override
            public void onLoadMore() {
                Handler handler = new Handler();
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        makeGetAllMessagesRequest(mConversation.getId(), false);
                    }
                });
            }
        });

        String toUserId = getIntent().getStringExtra("TO_USER_ID");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        ClickGuard.guard(btnSend);

        mChatBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent.getAction().equals(MyGcmListenerService.NOTIFICATION_CHAT_FOREGROUND)) {
                    handleReceivedMessage(intent);
                }
            }
        };

        makeStartChatRequest(toUserId);
    }

    @Override
    protected void onResume() {
        super.onResume();
        // registering the receiver for new notification
        LocalBroadcastManager.getInstance(this).registerReceiver(mChatBroadcastReceiver,
                new IntentFilter(MyGcmListenerService.NOTIFICATION_CHAT_FOREGROUND));
    }

    @Override
    protected void onPause() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mChatBroadcastReceiver);
        super.onPause();
    }

    public static ChatActivity getInstance() {
        return instance;
    }

    private void handleReceivedMessage(Intent intent) {
        Message message = intent.getParcelableExtra("message");
        if (message.getConversationId().equals(mConversation.getId())) {
            //add message to chat screen
            mAdapter.addData(0, message);
        }
    }

    @SuppressWarnings("unused")
    @OnClick(R.id.btn_send)
    public void sendMessage() {
        String mess = tvMessage.getText().toString().trim();

        if (!mess.isEmpty()) {
            long currentTimeStamp = System.currentTimeMillis();
            Message message = new Message(mConversation.getId(), mUserId, mess, currentTimeStamp);
            makeAddNewMessageRequest(message);
        }
    }

    private void makeAddNewMessageRequest(Message message) {
        Call<HohodaResponse<Message>> call = ChatApi.getInstance().addNewMessage(message.getConversationId(),
                message.getContent(), message.getCreatedAt());
        call.enqueue(mCallbackAddNewMessage);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void makeStartChatRequest(String toUserId) {
        Call<HohodaResponse<Conversation>> call = ChatApi.getInstance().startChat(toUserId);
        call.enqueue(mCallbackStartChat);
    }

    /**
     * Get messages
     *
     * @param conversationId The conversation id
     * @param reload         true if reload or first load
     */
    private void makeGetAllMessagesRequest(String conversationId, boolean reload) {
        long timeNext;
        mIsReload = reload;

        if (reload) {
            // reload == true: first load or refreshing
            timeNext = System.currentTimeMillis();
        } else {
            // load more
            timeNext = mAdapter.getLastItem().getCreatedAt();
        }

        Call<HohodaResponse<List<Message>>> call = ChatApi.getInstance().getAllMessages(conversationId, timeNext);
        call.enqueue(mCallbackGetAllMessages);
    }

    private void processReturnedData(List<Message> data) {
        if (mIsReload) {
            mAdapter.addData(data);
        } else {
            mAdapter.addData(data);
            if (data.isEmpty()) {
                mAdapter.setLoadMoreEnabled(false);
            }
        }
    }

}
