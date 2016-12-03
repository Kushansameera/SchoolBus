package com.example.kusha.schoolbus.fragments.parent;


import android.app.FragmentManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.app.Fragment;
import android.os.Handler;
import android.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.kusha.schoolbus.R;
import com.example.kusha.schoolbus.activities.parent.ManageDriversActivity;
import com.example.kusha.schoolbus.activities.parent.ParentActivity;
import com.example.kusha.schoolbus.models.DriverLocation;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.Query;
import com.firebase.client.ValueEventListener;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class BusLocationFragment extends Fragment implements OnMapReadyCallback {

    GoogleMap busMap;

    Firebase ref = new Firebase("https://schoolbus-708f4.firebaseio.com/");
    public FirebaseAuth mFirebaseAuth;
    private String userId;
    private String driverID;

    public BusLocationFragment() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        final View busLocationFragment = inflater.inflate(R.layout.fragment_bus_location, container, false);

        mFirebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mFirebaseAuth.getCurrentUser();
        userId = user.getUid().toString().trim();

        MapFragment mapFragment = (MapFragment) this.getChildFragmentManager().findFragmentById(R.id.busMap);
        mapFragment.getMapAsync(this);

        if (!ParentActivity.selectedDriverEmail.equals("")) {
            getDriverID();
            final Handler lco = new Handler();
            lco.postDelayed(new Runnable() {
                @Override
                public void run() {
                    updateBusLocation();
                }
            }, 2000);

        } else {
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
            alertDialogBuilder.setMessage("Please Select a Driver");

            alertDialogBuilder.setPositiveButton("Select", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface arg0, int arg1) {
                    Intent intent = new Intent(getActivity(), ManageDriversActivity.class);
                    startActivity(intent);
                }
            });

            AlertDialog alertDialog = alertDialogBuilder.create();
            alertDialog.show();
        }

        return busLocationFragment;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.busMap = googleMap;
        this.busMap.getUiSettings().setZoomControlsEnabled(true);
        this.busMap.getUiSettings().setZoomGesturesEnabled(true);
        LatLng SriLanka = new LatLng(7, 81);
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(SriLanka, 7));
    }

    private void getDriverID() {
        Query queryRef;
        String driverEmail = ParentActivity.selectedDriverEmail;
        queryRef = ref.child("Drivers").orderByChild("email").equalTo(driverEmail);

        queryRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot data : dataSnapshot.getChildren()) {
                    driverID = data.getKey();
                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });
    }

    private void updateBusLocation() {
        ref.child("Drivers").child(driverID).child("myLocation").child("location").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                DriverLocation driverLocation = new DriverLocation();
                driverLocation = dataSnapshot.getValue(DriverLocation.class);
                createMarker(driverLocation);

                //Toast.makeText(getActivity(),String.valueOf(driverLocation.getDriverLatitude()),Toast.LENGTH_SHORT);
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });
    }

    private void createMarker(DriverLocation location){
        this.busMap.clear();
        MarkerOptions marker = new MarkerOptions().position(new LatLng(location.getDriverLatitude(), location.getDriverLongitude())).title("Your Location");
        marker.icon(BitmapDescriptorFactory.fromResource(R.drawable.bus_map));
        marker.title("School Bus");
//        marker.icon(BitmapDescriptorFactory
//                .defaultMarker(BitmapDescriptorFactory.HUE_YELLOW));
        marker.getSnippet();
        this.busMap.addMarker(marker);
        this.busMap.animateCamera((CameraUpdateFactory.newLatLngZoom(new LatLng(location.getDriverLatitude(), location.getDriverLongitude()),15)));
    }
}
