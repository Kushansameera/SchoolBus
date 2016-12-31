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
public class ParentSentMessagesFragment extends Fragment {

    View parentSentMessagesFragment;
    public static String msgDriverId,msgDriverName;
    ParentSentMessageAdapter adapter;
    private RecyclerView rcvParentSentMessages;
    private TextView txtMsgTo;
    List<Message> messages = new ArrayList<>();
    private Firebase ref = new Firebase("https://schoolbus-708f4.firebaseio.com/");

    public ParentSentMessagesFragment() {}


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        parentSentMessagesFragment = inflater.inflate(R.layout.fragment_parent_sent_messages, container, false);
        rcvParentSentMessages = (RecyclerView)parentSentMessagesFragment.findViewById(R.id.rcvParentSentMessages);
        txtMsgTo = (TextView)parentSentMessagesFragment.findViewById(R.id.txtMsgTo);
        txtMsgTo.setText("To: "+msgDriverName);

        rcvParentSentMessages.setLayoutManager(new LinearLayoutManager(getActivity()));
        adapter = new ParentSentMessageAdapter(getActivity(), messages);
        showMessages();
        rcvParentSentMessages.setAdapter(adapter);

        return parentSentMessagesFragment;
    }

    private void showMessages(){
        Query queryRef;
        queryRef = ref.child("Parents").child(ParentActivity.userId).child("messages").child("sent").child(msgDriverId).child("myMessages");
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
