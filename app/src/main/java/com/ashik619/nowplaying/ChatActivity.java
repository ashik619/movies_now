package com.ashik619.nowplaying;


import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.Dialog;
import android.content.Intent;
import android.database.DataSetObserver;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.RelativeLayout;

import com.ashik619.nowplaying.adapters.MessageAdapter;
import com.ashik619.nowplaying.models.Message;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.splunk.mint.Mint;

import java.util.LinkedList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ChatActivity extends AppCompatActivity {
    private DatabaseReference mDatabase;
    private DatabaseReference moviesEndpoint;
    String m_id;
    @BindView(R.id.sendButton)
    ImageButton sendButton;
    @BindView(R.id.listViewChat)
    ListView listViewChat;
    @BindView(R.id.commentText)
    EditText commentText;
    MessageAdapter messageAdapter;
    String name = NowPlayingApplication.getLocalPrefInstance().getName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Mint.initAndStartSession(this.getApplication(), "5c1dbf76");
        setContentView(R.layout.activity_chat);
        ButterKnife.bind(this);
        Intent intent = getIntent();
        m_id = intent.getStringExtra("m_id");
        mDatabase =  FirebaseDatabase.getInstance().getReference();
        moviesEndpoint = mDatabase.child(m_id);
        messageAdapter = new MessageAdapter(ChatActivity.this,Message.class,R.layout.message_list_item,moviesEndpoint);
        listViewChat.setAdapter(messageAdapter);
        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String text = commentText.getText().toString();
                if(!text.equals("")){
                    pushMessage(text);
                    commentText.setText("");
                }
            }
        });
        messageAdapter.registerDataSetObserver(new DataSetObserver() {
            @Override
            public void onChanged() {
                super.onChanged();
                listViewChat.post(new Runnable(){
                    public void run() {
                        listViewChat.setSelection(listViewChat.getCount() - 1);
                    }});
            }
        });
        if (name == null){
            showUsernameDialog();
        }
    }
    void pushMessage(String msg){
        Message message = new Message();
        message.setUsername(name);
        message.setText(msg);
        moviesEndpoint.push().setValue(message);
    }

    void showUsernameDialog(){
        final Dialog dialog = new Dialog(this);
        dialog.setCancelable(false);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.username_dialog);
        dialog.show();
        Button okbutton = (Button) dialog.findViewById(R.id.okbutton);
        final EditText userName = (EditText) dialog.findViewById(R.id.userName);
        okbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                name = userName.getText().toString();
                if (!name.equals("")){
                    NowPlayingApplication.getLocalPrefInstance().setName(name);
                    dialog.dismiss();
                }
            }
        });
    }
}
