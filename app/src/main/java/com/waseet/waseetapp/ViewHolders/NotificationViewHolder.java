package com.waseet.waseetapp.ViewHolders;

import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.waseet.waseetapp.R;

public class NotificationViewHolder extends RecyclerView.ViewHolder {

    public TextView content;
    public TextView views;
    public CardView notice_Card;

    public NotificationViewHolder(@NonNull View itemView) {
        super(itemView);

        content = itemView.findViewById(R.id.content);
        views = itemView.findViewById(R.id.views);
        notice_Card = (CardView) itemView.findViewById(R.id.notice_card);

    }
}
