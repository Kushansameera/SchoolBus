package com.example.kusha.schoolbus.fragments.parent;


import android.app.FragmentManager;
import android.content.DialogInterface;
import android.os.Bundle;
import android.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.example.kusha.schoolbus.R;
import com.example.kusha.schoolbus.activities.parent.ParentActivity;
import com.example.kusha.schoolbus.adapter.ParentSentMessageAdapter;
import com.example.kusha.schoolbus.fragments.driver.StudentRequestsFragment;
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
    ImageButton btnBack;
    private Firebase ref = new Firebase("https://schoolbus-708f4.firebaseio.com/");

    public ParentSentMessagesFragment() {}


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        parentSentMessagesFragment = inflater.inflate(R.layout.fragment_parent_sent_messages, container, false);
        rcvParentSentMessages = (RecyclerView)parentSentMessagesFragment.findViewById(R.id.rcvParentSentMessages);
        btnBack = (ImageButton)parentSentMessagesFragment.findViewById(R.id.imageButtonBack);
        txtMsgTo = (TextView)parentSentMessagesFragment.findViewById(R.id.txtMsgTo);
        txtMsgTo.setText("To: "+msgDriverName);

        rcvParentSentMessages.setLayoutManager(new LinearLayoutManager(getActivity()));
        adapter = new ParentSentMessageAdapter(getActivity(), messages);
        showMessages();
        rcvParentSentMessages.setAdapter(adapter);

        adapter.setOnItemLongClickListener(new ParentSentMessageAdapter.OnItemLongClickListener() {
            @Override
            public void onItemLongClick(View itemView, int position) {
                final String msgId = messages.get(position).getMsgID();
                final PopupMenu popup = new PopupMenu(getActivity(), itemView);
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.delete_item:
                                deleteMessage(msgId);
                                return true;
                            case R.id.delete_all:
                                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
                                alertDialogBuilder.setMessage("Are you sure,You Want to Delete All Messages");

                                alertDialogBuilder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface arg0, int arg1) {
                                        deleteAllMessage();
                                    }
                                });

                                alertDialogBuilder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.cancel();
                                    }
                                });
                                AlertDialog alertDialog = alertDialogBuilder.create();
                                alertDialog.show();
                                return true;
                            default:
                                return false;
                        }
                    }
                });
                MenuInflater inflater = popup.getMenuInflater();
                inflater.inflate(R.menu.delete_all, popup.getMenu());
                popup.show();
            }
        });

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    Fragment fragment = new ParentSentContactsFragment();
                    FragmentManager fragmentManager = getFragmentManager();
                    fragmentManager.beginTransaction().replace(R.id.main_frame_container_parent, fragment).commit();
                } catch (Exception e) {
                    Log.d("Sent Contacts", e.getMessage());
                }
            }
        });

        return parentSentMessagesFragment;
    }

    private void deleteMessage(String msgID){
        ref.child("Parents").child(ParentActivity.userId).child("messages").child("sent").child(msgDriverId).child("myMessages").child(msgID).removeValue();
        Toast.makeText(getActivity(), "Message Deleted", Toast.LENGTH_SHORT).show();
        adapter.notifyDataSetChanged();
    }

    private void deleteAllMessage(){
        for(int i=0;i<messages.size();i++){
            ref.child("Parents").child(ParentActivity.userId).child("messages").child("sent").child(msgDriverId).child("myMessages").child(messages.get(i).getMsgID()).removeValue();

        }
        Toast.makeText(getActivity(), "Messages Deleted", Toast.LENGTH_SHORT).show();
    }

    private void showMessages(){
        Query queryRef;
        queryRef = ref.child("Parents").child(ParentActivity.userId).child("messages").child("sent").child(msgDriverId).child("myMessages");
        messages.clear();

        queryRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                messages.clear();
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
