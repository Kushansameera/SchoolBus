package com.example.kusha.schoolbus.activities.driver;

import android.app.Fragment;
import android.app.FragmentManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.example.kusha.schoolbus.R;
import com.example.kusha.schoolbus.activities.LoginActivity;
import com.example.kusha.schoolbus.services.DriverGPS;
import com.example.kusha.schoolbus.fragments.driver.ManageParentsFragment;
import com.example.kusha.schoolbus.fragments.driver.DriverMessageFragment;
import com.example.kusha.schoolbus.fragments.driver.PaymentDriverFragment;
import com.example.kusha.schoolbus.fragments.driver.RouteFragment;
import com.example.kusha.schoolbus.fragments.driver.SettingFragment;
import com.example.kusha.schoolbus.models.DriverLocation;
import com.example.kusha.schoolbus.models.User;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Calendar;

public class DriverActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    public FirebaseAuth mFirebaseAuth;
    private Firebase ref = new Firebase("https://schoolbus-708f4.firebaseio.com/");
    public static String userId;
    private static String userEmail;
    public static String driverName="";
    User mUser;
    Fragment fragment = null;
    TextView navName;
    TextView navEmail;
    String[] monthArray;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driver);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        final NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        mFirebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mFirebaseAuth.getCurrentUser();
        userId = user.getUid().toString().trim();
        userEmail = user.getEmail().toString().trim();
        monthArray = getResources().getStringArray(R.array.months);

        ref.child("Users").child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                mUser = dataSnapshot.getValue(User.class);

            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });
        updateLocation();
        final View view = navigationView.getHeaderView(0);
        final Handler key = new Handler();
        key.postDelayed(new Runnable() {
            @Override
            public void run() {
                navName = (TextView) view.findViewById(R.id.textDriverName);
                navEmail = (TextView) view.findViewById(R.id.textDriverEmail);
                navName.setText(mUser.getName());
                navEmail.setText(userEmail);
                driverName = mUser.getName();
            }
        }, 2000);

        setPaymentSummery();

        try {
            getSupportActionBar().setTitle("Route");
            fragment = new RouteFragment();
            FragmentManager fragmentManager = getFragmentManager();
            fragmentManager.beginTransaction().replace(R.id.main_frame_container, fragment).commit();
        } catch (Exception e) {
            Log.d("Route", e.getMessage());
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.driver, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_route) {
            changeFragmentRoute();
        } else if (id == R.id.nav_student) {
            changeFragmentStudent();
        } else if (id == R.id.nav_payment) {
            changeFragmentPayment();
        } else if (id == R.id.nav_message) {
            changeFragmentMessage();
        } else if (id == R.id.nav_parents) {
            changeFragmentParent();
        } else if (id == R.id.nav_route_setting) {
            changeFragmentRouteSetting();
        } else if (id == R.id.nav_setting) {
            changeFragmentSettings();
        } else if (id == R.id.nav_logout) {

            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
            alertDialogBuilder.setMessage("Are you sure,You wanted to Sign out");

            alertDialogBuilder.setPositiveButton("yes", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface arg0, int arg1) {
                    LoginActivity loginActivity = new LoginActivity();
                    loginActivity.mFirebaseAuth.getInstance().signOut();
                    Intent intent = new Intent(DriverActivity.this, LoginActivity.class);
                    startActivity(intent);
                }
            });

            alertDialogBuilder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    finish();
                }
            });

            AlertDialog alertDialog = alertDialogBuilder.create();
            alertDialog.show();

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void changeFragmentRoute() {
        try {
            getSupportActionBar().setTitle("Route");
            fragment = new RouteFragment();
            FragmentManager fragmentManager = getFragmentManager();
            fragmentManager.beginTransaction().replace(R.id.main_frame_container, fragment).commit();
        } catch (Exception e) {
            Log.d("Route", e.getMessage());
        }
    }

    public void changeFragmentPayment() {
        try {
            getSupportActionBar().setTitle("Payment");
            fragment = new PaymentDriverFragment();
            FragmentManager fragmentManager = getFragmentManager();
            fragmentManager.beginTransaction().replace(R.id.main_frame_container, fragment).commit();
        } catch (Exception e) {
            Log.d("Payment", e.getMessage());
        }
    }

    public void changeFragmentStudent() {

        try {
            Intent intent = new Intent(DriverActivity.this, StudentActivity.class);
            startActivity(intent);
        } catch (Exception e) {
            Log.d("Student", e.getMessage());
        }
//        try{
//            getSupportActionBar().setTitle("View Students");
//            fragment = new ViewStudentFragment();
//            FragmentManager fragmentManager = getFragmentManager();
//            fragmentManager.beginTransaction().replace(R.id.main_frame_container, fragment).commit();
//        }catch(Exception e){
//            Log.d("Student", e.getMessage());
//        }
    }

    public void changeFragmentParent() {
        try {
            getSupportActionBar().setTitle("Manage Parents");
            fragment = new ManageParentsFragment();
            FragmentManager fragmentManager = getFragmentManager();
            fragmentManager.beginTransaction().replace(R.id.main_frame_container, fragment).commit();
        } catch (Exception e) {
            Log.d("Message", e.getMessage());
        }
    }

    public void changeFragmentMessage() {
        try {
            getSupportActionBar().setTitle("Message");
            fragment = new DriverMessageFragment();
            FragmentManager fragmentManager = getFragmentManager();
            fragmentManager.beginTransaction().replace(R.id.main_frame_container, fragment).commit();
        } catch (Exception e) {
            Log.d("Message", e.getMessage());
        }
    }

    public void changeFragmentRouteSetting() {
        try {
            Intent intent = new Intent(DriverActivity.this, RouteSettingActivity.class);
            startActivity(intent);
        } catch (Exception e) {
            Log.d("Route Setting", e.getMessage());
        }
    }

    public void changeFragmentSettings() {
        try {
            getSupportActionBar().setTitle("Settings");
            fragment = new SettingFragment();
            FragmentManager fragmentManager = getFragmentManager();
            fragmentManager.beginTransaction().replace(R.id.main_frame_container, fragment).commit();
        } catch (Exception e) {
            Log.d("Settings", e.getMessage());
        }
    }

    private void updateLocation() {
        DriverGPS driverGPS = new DriverGPS(this);
        DriverLocation driverLocation = new DriverLocation();

        if (driverGPS.canGetLocation()) {
            driverLocation.setDriverLatitude(driverGPS.getLatitude());
            driverLocation.setDriverLongitude(driverGPS.getLongitude());
            setDriverLocation(driverLocation);

        }

    }

    public void setDriverLocation(DriverLocation driverLocation) {
        ref.child("Drivers").child(userId).child("myLocation").child("location").setValue(driverLocation);
    }

    private void setPaymentSummery() {
        Calendar calendar = Calendar.getInstance();
        final int year = calendar.get(Calendar.YEAR);
        final int month = calendar.get(Calendar.MONTH);
        final String mYear = String.valueOf(year);
        final String mMonth = String.valueOf(month);
        ref.child("Drivers").child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (!dataSnapshot.hasChild("budget")) {
                    Firebase mref = ref.child("Drivers").child(userId).child("budget").child("summery").child(String.valueOf(year)).child(monthArray[month]);
                    mref.child("totalIncome").setValue("0");
                    mref.child("currentIncome").setValue("0");
                    ref.child("Drivers").child(userId).child("budget").child("total").setValue("0");
                    ref.child("Drivers").child(userId).child("budget").child("lastMonth").setValue(String.valueOf(month));
                    ref.child("Drivers").child(userId).child("budget").child("lastYear").setValue(String.valueOf(year));

                } else {
                    int lastMonth = 0, total = 0;
                    String lastYear = "";
                    lastMonth = Integer.parseInt((String) dataSnapshot.child("budget").child("lastMonth").getValue());
                    String mTotal = dataSnapshot.child("budget").child("total").getValue().toString();
                    total = Integer.parseInt(mTotal);
                    lastYear = dataSnapshot.child("budget").child("lastYear").getValue().toString();

                    if(dataSnapshot.child("budget").child("summery").hasChild(mYear)){
                        if(!dataSnapshot.child("budget").child("summery").child(mYear).hasChild(monthArray[month])){
                            ref.child("Drivers").child(userId).child("budget").child("summery").child(mYear).child(monthArray[month]).child("totalIncome").setValue(mTotal);
                            ref.child("Drivers").child(userId).child("budget").child("summery").child(mYear).child(monthArray[month]).child("currentIncome").setValue("0");
                            ref.child("Drivers").child(userId).child("budget").child("lastMonth").setValue(String.valueOf(month));
                            ref.child("Drivers").child(userId).child("budget").child("lastYear").setValue(String.valueOf(year));
                        }
                    }else {
                        ref.child("Drivers").child(userId).child("budget").child("summery").child(mYear).child(monthArray[month]).child("totalIncome").setValue(mTotal);
                        ref.child("Drivers").child(userId).child("budget").child("summery").child(mYear).child(monthArray[month]).child("currentIncome").setValue("0");
                        ref.child("Drivers").child(userId).child("budget").child("lastMonth").setValue(String.valueOf(month));
                        ref.child("Drivers").child(userId).child("budget").child("lastYear").setValue(String.valueOf(year));
                    }
                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });
    }
}
