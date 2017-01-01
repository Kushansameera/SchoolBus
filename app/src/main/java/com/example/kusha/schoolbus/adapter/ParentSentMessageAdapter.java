package com.example.kusha.schoolbus.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.TextView;

import com.example.kusha.schoolbus.R;
import com.example.kusha.schoolbus.models.ManageParents;
import com.example.kusha.schoolbus.models.Message;

import java.util.List;

/**
 * Created by kusha on 12/30/2016.
 */

public class ParentSentMessageAdapter extends RecyclerView.Adapter<ParentSentMessageAdapter.ParentSentMessageViewHolder> {
    List<Message> myMessages;
    Context context;
    LayoutInflater layoutInflater;

    public ParentSentMessageAdapter(Context context, List<Message> messages) {
        this.myMessages = messages;
        this.context = context;
        layoutInflater = LayoutInflater.from(context);
    }

    @Override
    public ParentSentMessageAdapter.ParentSentMessageViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = layoutInflater.inflate(R.layout.parent_sent_card, parent, false);
        return new ParentSentMessageAdapter.ParentSentMessageViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ParentSentMessageAdapter.ParentSentMessageViewHolder holder, int position) {
        Message message = myMessages.get(position);
        holder.childName.setText("Child Name: "+message.getChildName());
        holder.msgBody.setText(message.getMsgBody());
        holder.msgDate.setText(message.getMsgDate());
        holder.msgTime.setText(message.getMsgTime());
    }

    @Override
    public int getItemCount() {
        return myMessages.size();
    }

    public Message getItem(int position){
        return myMessages.get(position);
    }

    private OnItemLongClickListener listener;

    public interface OnItemLongClickListener {
        void onItemLongClick(View itemView, int position);
    }

    public void setOnItemLongClickListener(OnItemLongClickListener listener) {
        this.listener = listener;
    }

    public class ParentSentMessageViewHolder extends RecyclerView.ViewHolder {

        TextView childName,msgBody,msgDate,msgTime;

        public ParentSentMessageViewHolder(final View itemView) {
            super(itemView);

            childName = (TextView) itemView.findViewById(R.id.txtChildName);
            msgBody = (TextView) itemView.findViewById(R.id.txtMsgBody);
            msgDate = (TextView) itemView.findViewById(R.id.txtMsgDate);
            msgTime = (TextView) itemView.findViewById(R.id.txtMsgTime);
//            itemView.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    if (listener != null) {
//                        int position = getAdapterPosition();
//                        if (position != RecyclerView.NO_POSITION) {
//                            listener.onItemClick(itemView, position);
//                        }
//                    }
//                }
//            });

            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    if (listener != null) {
                        int position = getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION) {
                            listener.onItemLongClick(itemView, position);
                        }
                    }
                    return true;
                }
            });

        }

    }
}
