package com.example.kusha.schoolbus.fragments.driver;


import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
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
import com.example.kusha.schoolbus.activities.driver.DriverActivity;
import com.example.kusha.schoolbus.activities.parent.ParentActivity;
import com.example.kusha.schoolbus.application.ApplicationClass;
import com.example.kusha.schoolbus.models.ManageParents;
import com.example.kusha.schoolbus.models.Message;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.Query;
import com.firebase.client.ValueEventListener;
import com.parse.ParseInstallation;
import com.parse.ParsePush;
import com.parse.ParseQuery;
import com.squareup.otto.Subscribe;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;


public class DriverMessageFragment extends Fragment {

    private Button btnSentMessages, btnReceivedMessages;
    private FloatingActionButton floatingBtnNewMessage;
    private EditText txtMessage;
    Fragment fragment;
    Firebase ref = new Firebase("https://schoolbus-708f4.firebaseio.com/");
    private String latestSentId = "";
    private String latestReceivedId = "";
    List<ManageParents> parents = new ArrayList<>();
    List<ManageParents> selectedParents = new ArrayList<>();
    private ProgressDialog progressDialog, mProgressDialog;
    int con = 0;

    public DriverMessageFragment() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View messageFragment = inflater.inflate(R.layout.fragment_message_driver, container, false);
        btnSentMessages = (Button) messageFragment.findViewById(R.id.btnSentMessages);
        btnReceivedMessages = (Button) messageFragment.findViewById(R.id.btnReceivedMessages);
        floatingBtnNewMessage = (FloatingActionButton) messageFragment.findViewById(R.id.floatingBtnNewMessage);
        progressDialog = new ProgressDialog(getActivity());
        mProgressDialog = new ProgressDialog(getActivity());
        progressDialog.setCancelable(false);
        mProgressDialog.setCancelable(false);
        //mProgressDialog.setMessage("Wait...");
        //ApplicationClass.bus.register(this);
        getParentList();
        btnSentMessages.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                try {
//                    fragment = new ParentSentContactsFragment();
//                    FragmentManager fragmentManager = getFragmentManager();
//                    fragmentManager.beginTransaction().replace(R.id.main_frame_container, fragment).commit();
//                } catch (Exception e) {
//                    Log.d("Sent Contacts", e.getMessage());
//                }
            }
        });

        btnReceivedMessages.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        floatingBtnNewMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectContactList();
            }
        });

        return messageFragment;
    }

    private void selectContactList() {
        selectedParents.clear();
        CharSequence[] items = new CharSequence[parents.size()];
        for (int i = 0; i < parents.size(); i++) {
            items[i] = parents.get(i).getParentName();
        }

        AlertDialog dialog = new AlertDialog.Builder(getActivity())
                .setTitle("Select Contacts")
                .setMultiChoiceItems(items, null, new DialogInterface.OnMultiChoiceClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int indexSelected, boolean isChecked) {
                        if (isChecked) {
                            selectedParents.add(parents.get(indexSelected));
                        } else if (selectedParents.contains(parents.get(indexSelected))) {
                            selectedParents.remove(parents.get(indexSelected));
                        }
                    }
                }).setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        if (selectedParents.size() == 0) {
                            Toast.makeText(getActivity(), "Please Select Atleast One Contact", Toast.LENGTH_SHORT).show();
                        } else {
                            dialog.dismiss();
                            newMessage();
                        }

                    }
                }).setNeutralButton("Send to All", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        selectedParents.clear();
                        for (int i = 0; i < parents.size(); i++) {
                            selectedParents.add(parents.get(i));
                        }
                        dialog.dismiss();
                        newMessage();

                    }
                }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        selectedParents.clear();
                        dialog.dismiss();
                    }
                }).create();
        dialog.setCancelable(false);
        dialog.show();
    }

    private void getParentList() {
        progressDialog.setMessage("Loading...");
        progressDialog.show();
        Query queryRef;
        queryRef = ref.child("Drivers").child(DriverActivity.userId).child("parent").orderByChild("regParents");

        parents.clear();

        queryRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot data : dataSnapshot.getChildren()) {
                    for (DataSnapshot d : data.getChildren()) {
                        ManageParents m = d.getValue(ManageParents.class);
                        parents.add(m);
                    }
                }
                progressDialog.dismiss();
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });
    }

    public void newMessage() {
        mProgressDialog.dismiss();
        Log.d("========>", "newMessage");
        ApplicationClass.bus.unregister(this);
        final Dialog newMessageDialog = new Dialog(getActivity());
        newMessageDialog.setContentView(R.layout.new_message_driver);
        Window window = newMessageDialog.getWindow();
        window.setLayout(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        newMessageDialog.setCancelable(false);

        txtMessage = (EditText) newMessageDialog.findViewById(R.id.txtMessage);
        ImageButton btnSendMessage = (ImageButton) newMessageDialog.findViewById(R.id.btnSendMessage);
        Button btnCloseMessage = (Button) newMessageDialog.findViewById(R.id.btnCloseMessage);

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
        final Message newMessage = new Message();
        newMessage.setMsgFrom(DriverActivity.driverName);
        newMessage.setMsgBody(txtMessage.getText().toString());
        newMessage.setMsgDate(String.valueOf(calendar.get(Calendar.YEAR)) + "-" + String.valueOf(calendar.get(Calendar.MONTH) + 1) + "-" + String.valueOf(calendar.get(Calendar.DATE)));
        newMessage.setMsgTime(time.format(currentTime));

        for (int i = 0; i < selectedParents.size(); i++) {
            mProgressDialog.setMessage("Sending...");
            mProgressDialog.show();
            final String parentID = selectedParents.get(i).getParentId();
            final int finalI = i;
            newMessage.setMsgTo(selectedParents.get(finalI).getParentName());
            ref.child("Parents").child(parentID).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.hasChild("messages")) {
                        if (dataSnapshot.child("messages").child("received").hasChild(ParentActivity.selectedDriverID)) {
                            latestReceivedId = dataSnapshot.child("messages").child("received").child(DriverActivity.userId).child("messageCounter").getValue().toString();
                        } else {
                            latestReceivedId = "0";
                        }
                    } else {
                        latestReceivedId = "0";
                    }
                    int receivedMessageId = Integer.parseInt(latestReceivedId) + 1;
                    String receivedMyMessageID = String.valueOf(receivedMessageId);
                    ref.child("Parents").child(selectedParents.get(finalI).getParentId()).child("messages").child("received").child(DriverActivity.userId).child("myMessages").child(receivedMyMessageID).setValue(newMessage);
                    ref.child("Parents").child(selectedParents.get(finalI).getParentId()).child("messages").child("received").child(DriverActivity.userId).child("messageCounter").setValue(receivedMyMessageID);
                    sendPush(selectedParents.get(finalI).getParentEmail());
                }

                @Override
                public void onCancelled(FirebaseError firebaseError) {

                }
            });

            ref.child("Drivers").child(DriverActivity.userId).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.hasChild("messages")) {
                        if (dataSnapshot.child("messages").child("sent").hasChild(parentID)) {
                            latestSentId = dataSnapshot.child("messages").child("sent").child(parentID).child("messageCounter").getValue().toString();
                        } else {
                            latestSentId = "0";
                        }
                    } else {
                        latestSentId = "0";
                    }
                    int sentMessageId = Integer.parseInt(latestSentId) + 1;
                    String sentMyMessageID = String.valueOf(sentMessageId);
                    ref.child("Drivers").child(DriverActivity.userId).child("messages").child("sent").child(selectedParents.get(finalI).getParentId()).child("myMessages").child(sentMyMessageID).setValue(newMessage);
                    ref.child("Drivers").child(DriverActivity.userId).child("messages").child("sent").child(selectedParents.get(finalI).getParentId()).child("messageCounter").setValue(sentMyMessageID);
                    mProgressDialog.dismiss();
                }

                @Override
                public void onCancelled(FirebaseError firebaseError) {

                }
            });
        }

        //Toast.makeText(getActivity(), "Messages Sent", Toast.LENGTH_SHORT).show();
    }

    private void sendPush(String to){
        ParseQuery pushQuery = ParseInstallation.getQuery();
        pushQuery.whereEqualTo("email", to);

        // Send push notification to query
        ParsePush push = new ParsePush();
        push.setQuery(pushQuery); // Set our Installation query
        push.setMessage("You Got New Message From "+DriverActivity.driverName);
        push.sendInBackground();
        Log.d("PUSH MESSAGE", "SENT "+to);
    }


}