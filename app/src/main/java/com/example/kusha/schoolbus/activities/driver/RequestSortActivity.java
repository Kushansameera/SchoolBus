package com.example.kusha.schoolbus.activities.driver;

import android.app.ProgressDialog;
import android.location.Location;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.example.kusha.schoolbus.R;
import com.example.kusha.schoolbus.activities.parent.ParentActivity;
import com.example.kusha.schoolbus.adapter.CustomAdapter;
import com.example.kusha.schoolbus.models.Schools;
import com.example.kusha.schoolbus.models.Student;
import com.example.kusha.schoolbus.models.StudentReport;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.Query;
import com.firebase.client.ValueEventListener;
import com.mapbox.directions.DirectionsCriteria;
import com.mapbox.directions.MapboxDirections;
import com.mapbox.directions.service.models.DirectionsResponse;
import com.mapbox.directions.service.models.DirectionsRoute;
import com.mapbox.directions.service.models.Waypoint;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import retrofit.Response;

public class RequestSortActivity extends AppCompatActivity {

    public static List<Student> myStudents;
    private List<Schools> mySchools = new ArrayList<>();
    private List<StudentReport> sortStudents = new ArrayList<>();
    Firebase ref = new Firebase("https://schoolbus-708f4.firebaseio.com/");
    String serverKey = "pk.eyJ1Ijoia3VzaGFuc2FtZWUiLCJhIjoiY2l4aHY3ZjUxMDA4ajJvbGFoMWg4dmxmbCJ9.jzN6WUj1PmSsCAKbPuzRmQ";
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_request_sort);
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Loading...");
        progressDialog.setCancelable(false);
        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }
        setTitle("Find Best");
        getSchools();

//        Waypoint origin = new Waypoint(80.11699788272381, 6.953339647653217);
//
//// Santa Monica Pier
//        Waypoint destination = new Waypoint(79.859608, 6.8916701);
//
//// Build the client object
//        MapboxDirections client = new MapboxDirections.Builder()
//                .setAccessToken(serverKey)
//                .setOrigin(origin)
//                .setDestination(destination)
//                .setProfile(DirectionsCriteria.PROFILE_DRIVING)
//                .build();
//
//// Execute the request
//        try {
//            Response<DirectionsResponse> response = client.execute();
//            DirectionsRoute route = response.body().getRoutes().get(0);
//            int distance = route.getDistance();
//            Toast.makeText(this, String.valueOf(distance), Toast.LENGTH_SHORT).show();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }

    }

    private void getSchools() {
        progressDialog.show();
        Query queryRef;
        queryRef = ref.child("Drivers").child(DriverActivity.userId).child("schools").orderByChild("routeSchools");
        mySchools.clear();

        queryRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot data : dataSnapshot.getChildren()) {
                    for (DataSnapshot d : data.getChildren()) {
                        Schools s = d.getValue(Schools.class);
                        mySchools.add(s);
                    }
                }
                calculateDistance();
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });

    }

    private void calculateDistance() {
        //Waypoint destination=null,origin=null;
        Location destination = new Location("");
        Location origin = new Location("");
        StudentReport stuReport = new StudentReport();
        for (int i = 0; i < myStudents.size(); i++) {
            for (int j = 0; j < mySchools.size(); j++) {
                if (mySchools.get(j).getSchoolName().equals(myStudents.get(i).getStuSchool())) {
                    //destination = new Waypoint(Double.parseDouble(mySchools.get(j).getSchoolLatitude()), Double.parseDouble(mySchools.get(j).getSchoolLongitude()));
                    destination.setLatitude(Double.parseDouble(mySchools.get(j).getSchoolLatitude()));
                    destination.setLongitude(Double.parseDouble(mySchools.get(j).getSchoolLongitude()));
                }
            }
            if (myStudents.get(i).getStuType().equals("Morning Only")) {
                //origin = new Waypoint(Double.parseDouble(myStudents.get(i).getStuPickLongitude()), Double.parseDouble(myStudents.get(i).getStuPickLatitude()));
                origin.setLatitude(Double.parseDouble(myStudents.get(i).getStuPickLatitude()));
                origin.setLongitude(Double.parseDouble(myStudents.get(i).getStuPickLongitude()));
            } else if (myStudents.get(i).getStuType().equals("Afternoon Only")) {
                //origin = new Waypoint(Double.parseDouble(myStudents.get(i).getStuDropLongitude()), Double.parseDouble(myStudents.get(i).getStuDropLatitude()));
                origin.setLatitude(Double.parseDouble(myStudents.get(i).getStuDropLatitude()));
                origin.setLongitude(Double.parseDouble(myStudents.get(i).getStuDropLongitude()));
            } else if (myStudents.get(i).getStuType().equals("Both")) {
                //origin = new Waypoint(Double.parseDouble(myStudents.get(i).getStuPickLongitude()), Double.parseDouble(myStudents.get(i).getStuPickLatitude()));
                origin.setLatitude(Double.parseDouble(myStudents.get(i).getStuPickLatitude()));
                origin.setLongitude(Double.parseDouble(myStudents.get(i).getStuPickLongitude()));
            }
            Float distance = origin.distanceTo(destination);
            stuReport.setStuName(myStudents.get(i).getStuName());
            stuReport.setStuSchool(myStudents.get(i).getStuSchool());
            stuReport.setStuType(myStudents.get(i).getStuType());
            stuReport.setDistance((double) distance);
            Toast.makeText(this, "Distance===========>"+myStudents.get(i).getStuName()+","+ String.valueOf(distance), Toast.LENGTH_SHORT).show();
            sortStudents.add(stuReport);
//            MapboxDirections client = new MapboxDirections.Builder()
//                    .setAccessToken(serverKey)
//                    .setOrigin(origin)
//                    .setDestination(destination)
//                    .setProfile(DirectionsCriteria.PROFILE_DRIVING)
//                    .build();
//
//            try {
//                Response<DirectionsResponse> response = client.execute();
//                DirectionsRoute route = response.body().getRoutes().get(0);
//                int distance = route.getDistance();
//                stuReport.setStuName(myStudents.get(i).getStuName());
//                stuReport.setStuSchool(myStudents.get(i).getStuSchool());
//                stuReport.setStuType(myStudents.get(i).getStuType());
//                stuReport.setDistance((double) distance);
//                Log.d("Distance===========>",myStudents.get(i).getStuName()+","+ String.valueOf(distance));
//                Toast.makeText(this, "Distance===========>"+myStudents.get(i).getStuName()+","+ String.valueOf(distance), Toast.LENGTH_SHORT).show();
//                sortStudents.add(stuReport);
//            } catch (IOException e) {
//                e.printStackTrace();
//            }


        }
        progressDialog.dismiss();
        Toast.makeText(this, "Completed", Toast.LENGTH_SHORT).show();
    }
}
