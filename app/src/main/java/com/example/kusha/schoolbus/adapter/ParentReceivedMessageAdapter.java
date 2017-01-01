package com.example.kusha.schoolbus.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.kusha.schoolbus.R;
import com.example.kusha.schoolbus.models.Message;

import java.util.List;

/**
 * Created by kusha on 1/1/2017.
 */

public class ParentReceivedMessageAdapter extends RecyclerView.Adapter<ParentReceivedMessageAdapter.ParentReceivedMessageViewHolder> {
    List<Message> myMessages;
    Context context;
    LayoutInflater layoutInflater;

    public ParentReceivedMessageAdapter(Context context, List<Message> messages) {
        this.myMessages = messages;
        this.context = context;
        layoutInflater = LayoutInflater.from(context);
    }

    @Override
    public ParentReceivedMessageAdapter.ParentReceivedMessageViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = layoutInflater.inflate(R.layout.parent_received_card, parent, false);
        return new ParentReceivedMessageAdapter.ParentReceivedMessageViewHolder (v);
    }

    @Override
    public void onBindViewHolder(ParentReceivedMessageAdapter.ParentReceivedMessageViewHolder  holder, int position) {
        Message message = myMessages.get(position);
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

    public class ParentReceivedMessageViewHolder extends RecyclerView.ViewHolder {

        TextView msgBody,msgDate,msgTime;

        public ParentReceivedMessageViewHolder(final View itemView) {
            super(itemView);

            msgBody = (TextView) itemView.findViewById(R.id.txtMsgBody);
            msgDate = (TextView) itemView.findViewById(R.id.txtMsgDate);
            msgTime = (TextView) itemView.findViewById(R.id.txtMsgTime);
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
