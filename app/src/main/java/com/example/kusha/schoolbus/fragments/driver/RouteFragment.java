package com.example.kusha.schoolbus.fragments.driver;


import android.graphics.Color;
import android.os.Bundle;
import android.app.Fragment;
import android.support.design.widget.FloatingActionButton;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.akexorcist.googledirection.DirectionCallback;
import com.akexorcist.googledirection.GoogleDirection;
import com.akexorcist.googledirection.constant.TransportMode;
import com.akexorcist.googledirection.model.Direction;
import com.akexorcist.googledirection.util.DirectionConverter;
import com.example.kusha.schoolbus.R;
import com.example.kusha.schoolbus.activities.driver.DriverActivity;
import com.example.kusha.schoolbus.models.CustomLatLng;
import com.example.kusha.schoolbus.models.DriverLocation;
import com.example.kusha.schoolbus.models.DriverRoute;
import com.example.kusha.schoolbus.models.Schools;
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
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.List;

public class RouteFragment extends Fragment implements OnMapReadyCallback, DirectionCallback {

    View routeFragment;
    GoogleMap driverMap;
    String serverKey = "AIzaSyDb5C2cX_fb7XLzq0AjaD2TjXzULYYmEsc";
    private Firebase ref = new Firebase("https://schoolbus-708f4.firebaseio.com/");
    private FloatingActionButton eveningButton, morningButton;
    private List<Schools> schools = new ArrayList<>();
    private List<DriverRoute> driverEveningRoute = new ArrayList<>();
    private List<DriverRoute> driverMorningRoute = new ArrayList<>();
    private ArrayList<CustomLatLng> myPlaces = new ArrayList<>();
    private int flag = 0;
    MarkerOptions busMarker;
    Marker myMarker;
    int i = 0;

    public RouteFragment() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        routeFragment = inflater.inflate(R.layout.fragment_route, container, false);
        MapFragment mapFragment = (MapFragment) this.getChildFragmentManager().findFragmentById(R.id.driverRouteMap);
        eveningButton = (FloatingActionButton) routeFragment.findViewById(R.id.floatingActionButtonEvening);
        morningButton = (FloatingActionButton) routeFragment.findViewById(R.id.floatingActionButtonMorning);
        getSchoolData();
        busMarker = new MarkerOptions();
        mapFragment.getMapAsync(this);
        //schools.clear();
        driverEveningRoute.clear();
        driverMorningRoute.clear();
        morningButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                flag = 1;
                driverMap.clear();
                getMorningRouteData();
            }
        });

        eveningButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                flag = 2;
                driverMap.clear();
                getEveningRouteData();
            }
        });

        return routeFragment;
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.driverMap = googleMap;
        this.driverMap.getUiSettings().setZoomControlsEnabled(true);
        this.driverMap.getUiSettings().setZoomGesturesEnabled(true);
        LatLng SriLanka = new LatLng(7, 81);
        // LatLng SriLanka1 = new LatLng(6.975210, 79.920622);
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(SriLanka, 7));
        updateBusLocation();

    }

    private void updateBusLocation() {
        ref.child("Drivers").child(DriverActivity.userId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.hasChild("myLocation")){
                    if (i != 0){
                        myMarker.remove();
                    }
                    i++;
                    DriverLocation driverLocation = new DriverLocation();
                    driverLocation = dataSnapshot.child("myLocation").child("location").getValue(DriverLocation.class);
                    createMarker(driverLocation);
                }


                //Toast.makeText(getActivity(),String.valueOf(driverLocation.getDriverLatitude()),Toast.LENGTH_SHORT);
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });
    }

    public void createMarker(DriverLocation location) {
        busMarker.position(new LatLng(location.getDriverLatitude(), location.getDriverLongitude()));
        busMarker.icon(BitmapDescriptorFactory.fromResource(R.drawable.my_bus));
        busMarker.title("School Bus");
        busMarker.getSnippet();
        myMarker = this.driverMap.addMarker(busMarker);
        this.driverMap.animateCamera((CameraUpdateFactory.newLatLngZoom(new LatLng(location.getDriverLatitude(), location.getDriverLongitude()), 15)));
    }

    public void requestDirection(LatLng origin, LatLng destination) {
        GoogleDirection.withServerKey(serverKey)
                .from(origin)
                .to(destination)
                .transportMode(TransportMode.DRIVING)
                .execute(this);
    }

    @Override
    public void onDirectionSuccess(Direction direction, String rawBody) {
        //Toast.makeText(getActivity(), "Success with status : " + direction.getStatus(), Toast.LENGTH_SHORT).show();
        if (direction.isOK()) {
            for (int i = 0; i < myPlaces.size(); i++) {
                if (myPlaces.get(i).getType().equals("school")) {
                    driverMap.addMarker(new MarkerOptions().position(myPlaces.get(i).getLatLng()).icon(BitmapDescriptorFactory.fromResource(R.drawable.school_on_map)).title(myPlaces.get(i).getName()));
                } else if (myPlaces.get(i).getType().equals("student")) {
                    driverMap.addMarker(new MarkerOptions().position(myPlaces.get(i).getLatLng()).icon(BitmapDescriptorFactory.fromResource(R.drawable.child_on_map)).title(myPlaces.get(i).getName()));
                }

            }

            ArrayList<LatLng> directionPositionList = direction.getRouteList().get(0).getLegList().get(0).getDirectionPoint();
            driverMap.addPolyline(DirectionConverter.createPolyline(getActivity(), directionPositionList, 5, Color.RED));

        }
    }

    @Override
    public void onDirectionFailure(Throwable t) {
        Toast.makeText(getActivity(), t.getMessage(), Toast.LENGTH_SHORT).show();
    }


    private void getSchoolData() {
        Query query;
        query = ref.child("Drivers").child(DriverActivity.userId).child("schools").orderByChild("routeSchools");
        schools.clear();
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot data : dataSnapshot.getChildren()) {
                    for (DataSnapshot d : data.getChildren()) {
                        Schools s = d.getValue(Schools.class);
                        schools.add(s);
                    }
                }

            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });
    }

    private void getMorningRouteData() {
        Query query;
        query = ref.child("Drivers").child(DriverActivity.userId).child("route").child("morningRoute");
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                driverMorningRoute.clear();
                for (DataSnapshot data : dataSnapshot.getChildren()) {
                    DriverRoute dr = data.getValue(DriverRoute.class);
                    if (dr.getAttend().equals("Yes")) {
                        driverMorningRoute.add(dr);
                    }
                }
                if (flag == 1) {
                    driverMap.clear();
                    setMorningRoute();
                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });
    }

    private void getEveningRouteData() {
        Query query;
        query = ref.child("Drivers").child(DriverActivity.userId).child("route").child("eveningRoute");
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                driverEveningRoute.clear();
                for (DataSnapshot data : dataSnapshot.getChildren()) {
                    DriverRoute dr = data.getValue(DriverRoute.class);
                    if (dr.getAttend().equals("Yes")) {
                        driverEveningRoute.add(dr);
                    }

                }
                if (flag == 2) {
                    driverMap.clear();
                    setEveningRoute();
                }

            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });
    }

    private void setMorningRoute() {
        myPlaces.clear();
        for (int i = 0; i < schools.size(); i++) {
            double lati = Double.parseDouble(schools.get(i).getSchoolLatitude());
            double longi = Double.parseDouble(schools.get(i).getSchoolLongitude());
            LatLng schoolLatLang = new LatLng(lati, longi);
            CustomLatLng customLatLng = new CustomLatLng();
            customLatLng.setLatLng(schoolLatLang);
            customLatLng.setType("school");
            customLatLng.setName(schools.get(i).getSchoolName());
            myPlaces.add(customLatLng);
        }
        for (int i = 0; i < driverMorningRoute.size(); i++) {
            double lati = Double.parseDouble(driverMorningRoute.get(i).getPlaceLatitude());
            double longi = Double.parseDouble(driverMorningRoute.get(i).getPlaceLongitude());
            LatLng stuLatLang = new LatLng(lati, longi);
            CustomLatLng customLatLng = new CustomLatLng();
            customLatLng.setLatLng(stuLatLang);
            customLatLng.setType("student");
            customLatLng.setName(driverMorningRoute.get(i).getPlaceName());
            myPlaces.add(customLatLng);
        }

        for (int i = 0; i < myPlaces.size() - 1; i++) {
            requestDirection(myPlaces.get(i).getLatLng(), myPlaces.get(i + 1).getLatLng());
        }
        updateBusLocation();
    }

    private void setEveningRoute() {
        myPlaces.clear();
        for (int i = 0; i < driverEveningRoute.size(); i++) {
            double lati = Double.parseDouble(driverEveningRoute.get(i).getPlaceLatitude());
            double longi = Double.parseDouble(driverEveningRoute.get(i).getPlaceLongitude());
            LatLng stuLatLang = new LatLng(lati, longi);
            CustomLatLng customLatLng = new CustomLatLng();
            customLatLng.setLatLng(stuLatLang);
            customLatLng.setType("student");
            customLatLng.setName(driverEveningRoute.get(i).getPlaceName());
            myPlaces.add(customLatLng);
        }

        for (int i = 0; i < schools.size(); i++) {
            double lati = Double.parseDouble(schools.get(i).getSchoolLatitude());
            double longi = Double.parseDouble(schools.get(i).getSchoolLongitude());
            LatLng schoolLatLang = new LatLng(lati, longi);
            CustomLatLng customLatLng = new CustomLatLng();
            customLatLng.setLatLng(schoolLatLang);
            customLatLng.setType("school");
            customLatLng.setName(schools.get(i).getSchoolName());
            myPlaces.add(customLatLng);
        }

        for (int i = 0; i < myPlaces.size() - 1; i++) {
            requestDirection(myPlaces.get(i).getLatLng(), myPlaces.get(i + 1).getLatLng());
        }
        updateBusLocation();
    }
}
