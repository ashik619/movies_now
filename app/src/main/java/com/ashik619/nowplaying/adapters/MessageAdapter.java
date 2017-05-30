package com.ashik619.nowplaying.adapters;


import android.view.View;

import com.ashik619.nowplaying.ChatActivity;
import com.ashik619.nowplaying.R;
import com.ashik619.nowplaying.custom_view.CustomTextView;
import com.ashik619.nowplaying.models.Message;
import com.firebase.ui.database.FirebaseListAdapter;
import com.google.firebase.database.DatabaseReference;

/**
 * Created by ashik619 on 25-05-2017.
 */
public class MessageAdapter extends FirebaseListAdapter<Message> {
    ChatActivity activity;

    public MessageAdapter(ChatActivity activity, Class<Message> modelClass, int modelLayout, DatabaseReference ref) {
        super(activity, modelClass, modelLayout, ref);
        this.activity = activity;
    }

    @Override
    protected void populateView(View v, Message model, int position) {
        CustomTextView nameText = (CustomTextView) v.findViewById(R.id.nameText);
        CustomTextView commentText = (CustomTextView) v.findViewById(R.id.commentText);
        nameText.setText(model.getUsername());
        commentText.setText(model.getText());

    }
}
