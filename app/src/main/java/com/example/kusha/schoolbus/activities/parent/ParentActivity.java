package com.example.kusha.schoolbus.activities.parent;

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
import android.widget.Toast;

import com.example.kusha.schoolbus.R;
import com.example.kusha.schoolbus.activities.LoginActivity;
import com.example.kusha.schoolbus.fragments.parent.AddNewChildFragment;
import com.example.kusha.schoolbus.fragments.parent.AttendanceFragment;
import com.example.kusha.schoolbus.fragments.parent.BusLocationFragment;
import com.example.kusha.schoolbus.fragments.parent.DriveMangeFragment;
import com.example.kusha.schoolbus.fragments.parent.MessageFragment;
import com.example.kusha.schoolbus.fragments.parent.ParentAccessFragment;
import com.example.kusha.schoolbus.fragments.parent.ParentPaymentragment;
import com.example.kusha.schoolbus.fragments.parent.SettingsParentFragment;
import com.example.kusha.schoolbus.models.User;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class ParentActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    Fragment fragment = null;
    public static String selectedDriverID="";
    public static String selectedDriverEmail = "";
    public static String selectedDriverName = "";
    public static String selectedChildId = "";
    public static String selectedChildName = "";

    public FirebaseAuth mFirebaseAuth;
    private Firebase ref = new Firebase("https://schoolbus-708f4.firebaseio.com/");
    public static String userId;
    private static String userEmail;
    User mUser;
    TextView navName;
    TextView navEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_parent);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

//        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
//            }
//        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        mFirebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mFirebaseAuth.getCurrentUser();
        userId = user.getUid().toString().trim();
        userEmail = user.getEmail().toString().trim();

        ref.child("Users").child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // for (DataSnapshot data:dataSnapshot.getChildren()) {
                mUser = dataSnapshot.getValue(User.class);
                // }

            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });

        final View view = navigationView.getHeaderView(0);
        final Handler key = new Handler();
        key.postDelayed(new Runnable() {
            @Override
            public void run() {
                navName = (TextView)view.findViewById(R.id.textParentName);
                navEmail = (TextView)view.findViewById(R.id.textParentEmail);
                navName.setText(mUser.getName());
                navEmail.setText(userEmail);
            }
        }, 2000);
        Log.d("=======>Child Name: ",selectedChildName);

        try {
            getSupportActionBar().setTitle("Bus Location");
            fragment = new BusLocationFragment();
            FragmentManager fragmentManager = getFragmentManager();
            fragmentManager.beginTransaction().replace(R.id.main_frame_container_parent, fragment).commit();
        } catch (Exception e) {
            Log.d("Bus Location", e.getMessage());
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
        getMenuInflater().inflate(R.menu.parent, menu);
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

        if (id == R.id.nav_bus_location) {
            changeFragmentBusLocation();
        } else if (id == R.id.nav_attendance) {
            changeFragmentAttendance();
        } else if (id == R.id.nav_message) {
            changeFragmentMessage();
        } else if (id == R.id.nav_children) {
            changeFragmentChildren();
        } else if (id == R.id.nav_payment) {
            changeFragmentPayments();
        } else if (id == R.id.nav_settings) {
            changeFragmentSettings();
        } else if (id == R.id.nav_driver) {
            changeFragmentDriver();
        } else if (id == R.id.nav_logout) {

            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
            alertDialogBuilder.setMessage("Are you sure,You wanted to Sign out");

            alertDialogBuilder.setPositiveButton("yes", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface arg0, int arg1) {
                    LoginActivity loginActivity = new LoginActivity();
                    loginActivity.mFirebaseAuth.getInstance().signOut();
                    Intent intent = new Intent(ParentActivity.this, LoginActivity.class);
                    startActivity(intent);
                }
            });

            alertDialogBuilder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });

            AlertDialog alertDialog = alertDialogBuilder.create();
            alertDialog.show();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void changeFragmentBusLocation() {
        try {
            getSupportActionBar().setTitle("Bus Location");
            fragment = new BusLocationFragment();
            FragmentManager fragmentManager = getFragmentManager();
            fragmentManager.beginTransaction().replace(R.id.main_frame_container_parent, fragment).commit();
        } catch (Exception e) {
            Log.d("Bus Location", e.getMessage());
        }
    }

    public void changeFragmentChildren() {
        try {
            getSupportActionBar().setTitle("Children");
            fragment = new ParentAccessFragment();
            FragmentManager fragmentManager = getFragmentManager();
            fragmentManager.beginTransaction().replace(R.id.main_frame_container_parent, fragment).commit();
        } catch (Exception e) {
            Log.d("Access Key", e.getMessage());
        }
    }

    public void changeFragmentAttendance() {
        try {
            getSupportActionBar().setTitle("Attendance");
            fragment = new AttendanceFragment();
            FragmentManager fragmentManager = getFragmentManager();
            fragmentManager.beginTransaction().replace(R.id.main_frame_container_parent, fragment).commit();
        } catch (Exception e) {
            Log.d("Attendance", e.getMessage());
        }
    }

    public void changeFragmentDriver() {
        try {
            Intent intent = new Intent(ParentActivity.this, ManageDriversActivity.class);
            startActivity(intent);
        } catch (Exception e) {
            Log.d("Driver", e.getMessage());
        }
    }

    public void changeFragmentMessage() {
        try {
            getSupportActionBar().setTitle("Messages");
            fragment = new MessageFragment();
            FragmentManager fragmentManager = getFragmentManager();
            fragmentManager.beginTransaction().replace(R.id.main_frame_container_parent, fragment).commit();
        } catch (Exception e) {
            Log.d("Messages", e.getMessage());
        }
    }

    public void changeFragmentPayments() {
        try {
            getSupportActionBar().setTitle("Payments");
            fragment = new ParentPaymentragment();
            FragmentManager fragmentManager = getFragmentManager();
            fragmentManager.beginTransaction().replace(R.id.main_frame_container_parent, fragment).commit();
        } catch (Exception e) {
            Log.d("Payments", e.getMessage());
        }
    }

    public void changeFragmentSettings() {
        try {
            getSupportActionBar().setTitle("Settings");
            fragment = new SettingsParentFragment();
            FragmentManager fragmentManager = getFragmentManager();
            fragmentManager.beginTransaction().replace(R.id.main_frame_container_parent, fragment).commit();
        } catch (Exception e) {
            Log.d("Settings", e.getMessage());
        }
    }

}
