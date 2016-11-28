package com.example.kusha.schoolbus.fragments.driver;



import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.PopupMenu;
import android.widget.Toast;

import com.example.kusha.schoolbus.R;
import com.example.kusha.schoolbus.models.RouteLocations;
import com.example.kusha.schoolbus.adapter.RouteLocationAdapter;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.MutableData;
import com.firebase.client.Query;
import com.firebase.client.Transaction;
import com.firebase.client.ValueEventListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class RouteLocationFragment extends Fragment {

    RecyclerView rcvLocation;
    RouteLocationAdapter adapter;
    List<RouteLocations> locations = new ArrayList<>();
    private View routeLocationFragment;
    public FirebaseAuth mFirebaseAuth;
    private FirebaseDatabase mDatabase;
    private Firebase ref = new Firebase("https://schoolbus-708f4.firebaseio.com/");
    private String userId;
    private String latestRouteID = "";
    private boolean flag = false;
    private String newLocName = "";

    EditText txtRouteLocationName;
    ImageButton btnAddRouteLocation;

    public RouteLocationFragment() {

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        routeLocationFragment = inflater.inflate(R.layout.fragment_route_location, container, false);
        rcvLocation = (RecyclerView) routeLocationFragment.findViewById(R.id.rcvLocation);
        txtRouteLocationName = (EditText) routeLocationFragment.findViewById(R.id.txtRouteLocationName);
        btnAddRouteLocation = (ImageButton) routeLocationFragment.findViewById(R.id.btnAddRouteLocation);

        mFirebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mFirebaseAuth.getCurrentUser();
        userId = user.getUid().toString().trim();

        rcvLocation.setLayoutManager(new LinearLayoutManager(getActivity()));
        adapter = new RouteLocationAdapter(getActivity(), locations);
        showRouteLocations();
        rcvLocation.setAdapter(adapter);

        btnAddRouteLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (txtRouteLocationName.getText().length() == 0) {
                    Toast.makeText(getActivity(), "Please Enter Pickup Location Name", Toast.LENGTH_SHORT).show();
                } else {
                    checkLocExists();
                    flag = false;
                }

            }
        });

        adapter.setOnItemClickListener(new RouteLocationAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                final String locId = locations.get(position).getLocId();
                final String locName = locations.get(position).getLocName();
                final PopupMenu popup = new PopupMenu(getActivity(), view);
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.editRoute:
                                updateData(locId, locName);
                                return true;
                            case R.id.deleteRoute:
                                deleteData(locId, locName);
                                return true;
                            default:
                                return false;
                        }
                    }
                });
                MenuInflater inflater = popup.getMenuInflater();
                inflater.inflate(R.menu.edit_delete, popup.getMenu());
                popup.show();
            }
        });


        return routeLocationFragment;
    }

//    @Override
//    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
//        super.onCreateContextMenu(menu, v, menuInfo);
//        MenuInflater inflater = getActivity().getMenuInflater();
//        inflater.inflate(R.menu.edit_delete, menu);
//
//    }
//
//    @Override
//    public boolean onContextItemSelected(MenuItem item) {
//        return super.onContextItemSelected(item);
//    }

    private void deleteData(final String locId, final String locName) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
        alertDialogBuilder.setMessage("Are you sure,You wanted to Delete " + locName);

        alertDialogBuilder.setPositiveButton("yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface arg0, int arg1) {
                ref.child("Drivers").child(userId).child("route").child("routeLocations").child(locId).removeValue();
                locations.clear();
                adapter.notifyDataSetChanged();
                Toast.makeText(getActivity(), locName + " Deleted From Pickup Locations", Toast.LENGTH_SHORT).show();
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


    }

    private void updateData(final String locId, final String locName) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
        alertDialogBuilder.setMessage("Enter New Location Name");
        final EditText newLocationName = new EditText(getActivity());
        newLocationName.setInputType(InputType.TYPE_CLASS_TEXT);
        newLocationName.setText(locName);
        alertDialogBuilder.setView(newLocationName);

        alertDialogBuilder.setPositiveButton("Update", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                newLocName = newLocationName.getText().toString();
                if (newLocationName.getText().toString().equals("")) {
                    Toast.makeText(getActivity(), "Please Enter Location Name", Toast.LENGTH_SHORT).show();
                } else {
                    ref.child("Drivers").child(userId).child("route").child("routeLocations").child(locId).child("locName").setValue(newLocName);
                    locations.clear();
                    adapter.notifyDataSetChanged();
                    Toast.makeText(getActivity(), locName + " Changed as " + newLocName, Toast.LENGTH_SHORT).show();
                }
            }
        });
        alertDialogBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        alertDialogBuilder.show();
    }

    private void addLocation() {
        final RouteLocations routeLocations = new RouteLocations();
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                routeLocations.setLocId(latestRouteID);
                routeLocations.setLocName(txtRouteLocationName.getText().toString().trim());
                ref.child("Drivers").child(userId).child("route").child("routeLocations").child(latestRouteID).setValue(routeLocations);
                Toast.makeText(getActivity(), "Successfully Added Location", Toast.LENGTH_SHORT).show();
                txtRouteLocationName.setText("");
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });
    }

    private void getLatestLocationID() {
        ref.child("Drivers").child(userId).child("route").child("locationCounter").runTransaction(new Transaction.Handler() {
            @Override
            public Transaction.Result doTransaction(MutableData mutableData) {
                Integer currentValue = mutableData.getValue(Integer.class) + 1;
                latestRouteID = currentValue.toString();
                mutableData.setValue(currentValue);
                return Transaction.success(mutableData);
            }

            @Override
            public void onComplete(FirebaseError firebaseError, boolean b, DataSnapshot dataSnapshot) {
                addLocation();
                locations.clear();
                adapter.notifyDataSetChanged();
                //txtRouteLocationName.setText("");
            }
        });
    }

    private void checkLocationId() {
        ref.child("Drivers").child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChild("route")) {
                    getLatestLocationID();
                } else {
                    latestRouteID = "1";
                    ref.child("Drivers").child(userId).child("route").child("locationCounter").setValue(1);
                    addLocation();

                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });
    }

    private void showRouteLocations() {
        Query queryRef;
        queryRef = ref.child("Drivers").child(userId).child("route").orderByChild("routeLocations");

        locations.clear();
        queryRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot data : dataSnapshot.getChildren()) {
                    for (DataSnapshot d : data.getChildren()) {
                        RouteLocations r = d.getValue(RouteLocations.class);
                        locations.add(r);
                    }
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });
    }

    private void checkLocExists() {
        Query queryRef;
        queryRef = ref.child("Drivers").child(userId).child("route").child("routeLocations").orderByValue();
        final String oldLocName = txtRouteLocationName.getText().toString();
        queryRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() != null) {
                    for (DataSnapshot data : dataSnapshot.getChildren()) {
                        RouteLocations routeLoc = data.getValue(RouteLocations.class);
                        if (routeLoc.getLocName().equalsIgnoreCase(oldLocName)) {
                            flag = true;
                            break;
                        }
                    }
                    if (flag) {
                        Toast.makeText(getActivity(), "Location is Already Exists", Toast.LENGTH_SHORT).show();
                        txtRouteLocationName.setText("");
                    } else {
                        checkLocationId();
                    }

                } else {
                    checkLocationId();
                }

            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });
    }


}
