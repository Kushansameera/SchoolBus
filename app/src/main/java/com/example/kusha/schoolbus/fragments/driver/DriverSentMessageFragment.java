package com.example.kusha.schoolbus.fragments.driver;


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
import com.example.kusha.schoolbus.activities.driver.DriverActivity;
import com.example.kusha.schoolbus.adapter.ParentReceivedMessageAdapter;
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
public class DriverSentMessageFragment extends Fragment {

    View driverSentMessagesFragment;
    public static String msgParentId,msgParentName;
    ParentReceivedMessageAdapter adapter;
    private RecyclerView rcvDriverSentdMessages;
    private TextView txtMsgTo;
    List<Message> messages = new ArrayList<>();
    private Firebase ref = new Firebase("https://schoolbus-708f4.firebaseio.com/");


    public DriverSentMessageFragment() {}


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        driverSentMessagesFragment = inflater.inflate(R.layout.fragment_driver_sent_message, container, false);
        rcvDriverSentdMessages = (RecyclerView)driverSentMessagesFragment.findViewById(R.id.rcvDriverSentMessages);
        txtMsgTo = (TextView)driverSentMessagesFragment.findViewById(R.id.txtMsgTo);
        txtMsgTo.setText("To: "+msgParentName);

        rcvDriverSentdMessages.setLayoutManager(new LinearLayoutManager(getActivity()));
        adapter = new ParentReceivedMessageAdapter(getActivity(), messages);
        showMessages();
        rcvDriverSentdMessages.setAdapter(adapter);
        return  driverSentMessagesFragment;
    }

    private void showMessages(){
        Query queryRef;
        queryRef = ref.child("Drivers").child(DriverActivity.userId).child("messages").child("sent").child(msgParentId).child("myMessages");
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
