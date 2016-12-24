package com.example.kusha.schoolbus.fragments.parent;


import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.example.kusha.schoolbus.R;
import com.example.kusha.schoolbus.services.GPSTracker;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

/**
 * A simple {@link Fragment} subclass.
 */
public class ChildLocationFragment extends Fragment implements OnMapReadyCallback {

    Button btnSetLocation;
    GoogleMap googleMap;
    private double stuLati, stuLongi;
    private String stuLoc;
    List<Address> address = null;
    public static String buttonIdentifier = "";


    public ChildLocationFragment() {}


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View stuLocFragment = inflater.inflate(R.layout.fragment_student_location, container, false);
        PlaceAutocompleteFragment placeAutocompleteFragment = (PlaceAutocompleteFragment) this.getChildFragmentManager().findFragmentById(R.id.place_autocomplete_fragment);

        btnSetLocation = (Button) stuLocFragment.findViewById(R.id.btnSet);
        btnSetLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (stuLongi == 0 && stuLati == 0) {
                    Toast.makeText(getActivity(), "Please Select a Location", Toast.LENGTH_SHORT).show();
                } else {
                    if (buttonIdentifier.equals("pick")) {
                        AddNewChildFragment.stuPickLatitude = stuLati;
                        AddNewChildFragment.stuPickLongitude = stuLongi;
                        AddNewChildFragment.stuPickLoc = stuLoc;
                        buttonIdentifier = "";
                    }
                    else if(buttonIdentifier.equals("drop")){
                        AddNewChildFragment.stuDropLatitude = stuLati;
                        AddNewChildFragment.stuDropLongitude = stuLongi;
                        AddNewChildFragment.stuDropLoc = stuLoc;
                        buttonIdentifier = "";
                    }
                    getFragmentManager().popBackStack();
                }

            }
        });
        placeAutocompleteFragment.setHint("Search Location");

        placeAutocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {
                googleMap.clear();
                stuLoc = place.getName().toString();
                LatLng childLatlng = place.getLatLng();
                stuLati = childLatlng.latitude;
                stuLongi = childLatlng.longitude;
                MarkerOptions markerOptions = new MarkerOptions();
                markerOptions.position(childLatlng);
                markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.child_on_map));
                googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(childLatlng, 15));
                markerOptions.draggable(true);
                googleMap.addMarker(markerOptions);
            }

            @Override
            public void onError(Status status) {
                Log.d("=====>",status.getStatusMessage());
            }
        });

//        AutocompleteFilter typeFilter = new AutocompleteFilter.Builder()
//                .setTypeFilter(AutocompleteFilter.TYPE_FILTER_NONE)
//                .build();
//        placeAutocompleteFragment.setFilter(typeFilter);
//
        MapFragment mapFragment = (MapFragment) this.getChildFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);


        return stuLocFragment;
    }

    @Override
    public void onMapReady(final GoogleMap googleMap) {
        this.googleMap = googleMap;
        this.googleMap.setMyLocationEnabled(true);
        this.googleMap.getUiSettings().setZoomControlsEnabled(true);
        this.googleMap.getUiSettings().setZoomGesturesEnabled(true);
        //this.googleMap.getUiSettings().setMyLocationButtonEnabled(true);
        final Geocoder geocoder = new Geocoder(getActivity(), Locale.getDefault());
        LatLng SriLanka = new LatLng(7, 81);
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(SriLanka, 7));

        this.googleMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                MarkerOptions markerOptions = new MarkerOptions();
                markerOptions.position(latLng);
                markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.child_on_map));
                googleMap.clear();
                googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15));
                markerOptions.draggable(true);
                googleMap.addMarker(markerOptions);
                stuLati = latLng.latitude;
                stuLongi = latLng.longitude;
                try {
                    address = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1);
                    stuLoc = address.get(0).getAddressLine(0);
                } catch (IOException e) {

                }


            }
        });

        this.googleMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                marker.setVisible(false);
                stuLati = 0;
                stuLongi = 0;
                stuLoc = "";

                return true;
            }
        });

        this.googleMap.setOnMyLocationButtonClickListener(new GoogleMap.OnMyLocationButtonClickListener() {
            @Override
            public boolean onMyLocationButtonClick() {
                setCurrentLocation();
                return false;
            }
        });
    }

    private void setCurrentLocation() {
        GPSTracker gps = new GPSTracker(getActivity());
        final Geocoder geocoder = new Geocoder(getActivity(), Locale.getDefault());
        this.googleMap.clear();
        if(gps.canGetLocation()){
            stuLati = gps.getLatitude();
            stuLongi= gps.getLongitude();
            MarkerOptions marker = new MarkerOptions().position(new LatLng(stuLati, stuLongi)).title("Your Location");
            marker.icon(BitmapDescriptorFactory.fromResource(R.drawable.child_on_map));
            this.googleMap.addMarker(marker);
            this.googleMap.animateCamera((CameraUpdateFactory.newLatLngZoom(new LatLng(stuLati, stuLongi),15)));
            try {
                address = geocoder.getFromLocation(stuLati, stuLongi, 1);
                stuLoc = address.get(0).getAddressLine(0);
            } catch (IOException e) {

            }
            //Toast.makeText(getActivity(), "Location Set to Your Current Location", Toast.LENGTH_SHORT).show();
        }else{
            gps.showSettingsAlert();
        }
        //gps.stopUsingGPS();
    }
}
