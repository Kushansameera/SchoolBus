package com.example.kusha.schoolbus.fragments.parent;


import android.app.Dialog;
import android.app.FragmentManager;
import android.os.Bundle;
import android.app.Fragment;
import android.support.design.widget.FloatingActionButton;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.kusha.schoolbus.R;
import com.example.kusha.schoolbus.activities.parent.ParentActivity;
import com.example.kusha.schoolbus.models.Message;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.parse.ParseInstallation;
import com.parse.ParsePush;
import com.parse.ParseQuery;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * A simple {@link Fragment} subclass.
 */
public class ParentMessageFragment extends Fragment {

    private Button btnSentMessages, btnReceivedMessages;
    private FloatingActionButton floatingBtnNewMessage;
    private EditText txtMessage;
    Fragment fragment;
    Firebase ref = new Firebase("https://schoolbus-708f4.firebaseio.com/");
    private String latestSentId = "0", latestReceivedId = "0";

    public ParentMessageFragment() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View messageFragment = inflater.inflate(R.layout.fragment_message_parent, container, false);
        btnSentMessages = (Button) messageFragment.findViewById(R.id.btnSentMessages);
        btnReceivedMessages = (Button) messageFragment.findViewById(R.id.btnReceivedMessages);
        floatingBtnNewMessage = (FloatingActionButton) messageFragment.findViewById(R.id.floatingBtnNewMessage);

        btnSentMessages.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    fragment = new ParentSentContactsFragment();
                    FragmentManager fragmentManager = getFragmentManager();
                    fragmentManager.beginTransaction().replace(R.id.main_frame_container_parent, fragment).commit();
                } catch (Exception e) {
                    Log.d("Sent Contacts", e.getMessage());
                }
            }
        });

        btnReceivedMessages.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    fragment = new ParentReceivedContactsFragment();
                    FragmentManager fragmentManager = getFragmentManager();
                    fragmentManager.beginTransaction().replace(R.id.main_frame_container_parent, fragment).commit();
                } catch (Exception e) {
                    Log.d("Sent Contacts", e.getMessage());
                }
            }
        });

        floatingBtnNewMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                newMessage();
            }
        });
        return messageFragment;
    }

    private void newMessage() {
        final Dialog newMessageDialog = new Dialog(getActivity());
        newMessageDialog.setContentView(R.layout.new_message_parent);
        Window window = newMessageDialog.getWindow();
        window.setLayout(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        newMessageDialog.setCancelable(false);

        TextView txtMessageTo = (TextView) newMessageDialog.findViewById(R.id.txtMessageTo);
        TextView txtMessageChildName = (TextView) newMessageDialog.findViewById(R.id.txtMessageChildName);
        txtMessage = (EditText) newMessageDialog.findViewById(R.id.txtMessage);
        ImageButton btnSendMessage = (ImageButton) newMessageDialog.findViewById(R.id.btnSendMessage);
        Button btnCloseMessage = (Button) newMessageDialog.findViewById(R.id.btnCloseMessage);
        txtMessageTo.setText(ParentActivity.selectedDriverName);
        txtMessageChildName.setText(ParentActivity.selectedChildName);

        getSentID();
        getReceivedID();

        btnSendMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Toast.makeText(getActivity(), "Sent = "+latestSentId+"  received = "+latestReceivedId, Toast.LENGTH_SHORT).show();
                sendMessage();
                newMessageDialog.dismiss();
            }
        });

        btnCloseMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                newMessageDialog.dismiss();
            }
        });
        newMessageDialog.show();
    }

    private void sendMessage() {
        Calendar calendar = Calendar.getInstance();
        Date currentTime = calendar.getTime();
        DateFormat time = new SimpleDateFormat("hh:mm a");
        Message newMessage = new Message();
        newMessage.setMsgFrom(ParentActivity.parentName);
        newMessage.setMsgTo(ParentActivity.selectedDriverName);
        newMessage.setMsgBody(txtMessage.getText().toString());
        newMessage.setMsgDate(String.valueOf(calendar.get(Calendar.YEAR))+"-"+String.valueOf(calendar.get(Calendar.MONTH)+1)+"-"+String.valueOf(calendar.get(Calendar.DATE)));
        newMessage.setMsgTime(time.format(currentTime));
        newMessage.setChildName(ParentActivity.selectedChildName);
        int sentMessageId = Integer.parseInt(latestSentId)+1;
        String sentMyMessageID = String.valueOf(sentMessageId);
        int receivedMessageId = Integer.parseInt(latestReceivedId)+1;
        String receivedMyMessageID = String.valueOf(receivedMessageId);
        newMessage.setMsgID(sentMyMessageID);

        ref.child("Parents").child(ParentActivity.userId).child("messages").child("sent").child(ParentActivity.selectedDriverID).child("myMessages").child(sentMyMessageID).setValue(newMessage);
        ref.child("Parents").child(ParentActivity.userId).child("messages").child("sent").child(ParentActivity.selectedDriverID).child("messageCounter").setValue(sentMyMessageID);

        newMessage.setMsgID(receivedMyMessageID);
        ref.child("Drivers").child(ParentActivity.selectedDriverID).child("messages").child("received").child(ParentActivity.userId).child("myMessages").child(receivedMyMessageID).setValue(newMessage);
        ref.child("Drivers").child(ParentActivity.selectedDriverID).child("messages").child("received").child(ParentActivity.userId).child("messageCounter").setValue(receivedMyMessageID);
        sendPush(ParentActivity.selectedDriverEmail);
        Toast.makeText(getActivity(), "Message Sent to "+ParentActivity.selectedDriverName, Toast.LENGTH_SHORT).show();
    }

    private void getSentID() {
        ref.child("Parents").child(ParentActivity.userId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.hasChild("messages")){
                    if(dataSnapshot.child("messages").child("sent").hasChild(ParentActivity.selectedDriverID)){
                        latestSentId = dataSnapshot.child("messages").child("sent").child(ParentActivity.selectedDriverID).child("messageCounter").getValue().toString();
                    }else {
                        latestSentId = "0";
                    }
                }else {
                    latestSentId = "0";
                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });
    }

    private void getReceivedID() {
        ref.child("Drivers").child(ParentActivity.selectedDriverID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.hasChild("messages")){
                    if(dataSnapshot.child("messages").child("received").hasChild(ParentActivity.userId)){
                        latestReceivedId = dataSnapshot.child("messages").child("received").child(ParentActivity.userId).child("messageCounter").getValue().toString();
                    }else {
                        latestReceivedId = "0";
                    }
                }else {
                    latestReceivedId = "0";
                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });
    }

    private void sendPush(String to){
        ParseQuery pushQuery = ParseInstallation.getQuery();
        pushQuery.whereEqualTo("email", to);

        // Send push notification to query
        ParsePush push = new ParsePush();
        push.setQuery(pushQuery); // Set our Installation query
        push.setMessage("You Got New Message From "+ParentActivity.parentName);
        push.sendInBackground();
        Log.d("PUSH MESSAGE", "SENT "+to);
    }

}
