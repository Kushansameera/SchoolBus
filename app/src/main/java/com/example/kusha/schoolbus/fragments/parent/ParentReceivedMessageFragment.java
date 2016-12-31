package com.example.kusha.schoolbus.fragments.parent;


import android.os.Bundle;
import android.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.kusha.schoolbus.R;
import com.example.kusha.schoolbus.activities.parent.ParentActivity;
import com.example.kusha.schoolbus.adapter.ParentReceivedMessageAdapter;
import com.example.kusha.schoolbus.adapter.ParentSentMessageAdapter;
import com.example.kusha.schoolbus.models.Message;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.Query;
import com.firebase.client.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class ParentReceivedMessageFragment extends Fragment {
    View parentReceivedMessagesFragment;
    public static String msgDriverId,msgDriverName;
    ParentReceivedMessageAdapter adapter;
    private RecyclerView rcvParentReceivedMessages;
    private TextView txtMsgFrom;
    List<Message> messages = new ArrayList<>();
    private Firebase ref = new Firebase("https://schoolbus-708f4.firebaseio.com/");

    public ParentReceivedMessageFragment() {}


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        parentReceivedMessagesFragment = inflater.inflate(R.layout.fragment_parent_received_message, container, false);
        rcvParentReceivedMessages = (RecyclerView)parentReceivedMessagesFragment.findViewById(R.id.rcvParentReceivedMessages);
        txtMsgFrom = (TextView)parentReceivedMessagesFragment.findViewById(R.id.txtMsgFrom);
        txtMsgFrom.setText("From: "+msgDriverName);

        rcvParentReceivedMessages.setLayoutManager(new LinearLayoutManager(getActivity()));
        adapter = new ParentReceivedMessageAdapter(getActivity(), messages);
        showMessages();
        rcvParentReceivedMessages.setAdapter(adapter);
        return parentReceivedMessagesFragment;
    }

    private void showMessages(){
        Query queryRef;
        queryRef = ref.child("Parents").child(ParentActivity.userId).child("messages").child("received").child(msgDriverId).child("myMessages");
        messages.clear();

        queryRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.getChildren()==null){
                    Toast.makeText(getActivity(), "No Messages Found", Toast.LENGTH_SHORT).show();
                }else {
                    for (DataSnapshot data:dataSnapshot.getChildren()) {
                        Message m = data.getValue(Message.class);
                        messages.add(m);
                    }
                    adapter.notifyDataSetChanged();
                }

            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });

    }

}
