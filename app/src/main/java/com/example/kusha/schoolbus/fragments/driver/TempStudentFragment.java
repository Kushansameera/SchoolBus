package com.example.kusha.schoolbus.fragments.driver;


import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.kusha.schoolbus.R;
import com.example.kusha.schoolbus.activities.driver.DriverActivity;
import com.example.kusha.schoolbus.services.GeoDistance;
import com.example.kusha.schoolbus.models.Children;
import com.example.kusha.schoolbus.models.DriverRoute;
import com.example.kusha.schoolbus.models.PaymentList;
import com.example.kusha.schoolbus.models.Schools;
import com.example.kusha.schoolbus.models.Student;
import com.example.kusha.schoolbus.models.StudentPayment;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.Query;
import com.firebase.client.ValueEventListener;
import com.parse.ParseInstallation;
import com.parse.ParsePush;
import com.parse.ParseQuery;

import java.util.Calendar;

public class TempStudentFragment extends Fragment {
    public static String tempStudentId = "";
    public static String driverId = "";
    public static String studistance = "";
    private static String latestStudentID;

    private TextView txtTempStuType, txtTempStuName, txtTempStuSchool, txtTempStuGrade, txtTempStuClass, txtTempStuPickup, txtTempStuDrop, txtTempStuPickupTime, txtTempStuMonthlyFee;
    private Button btnAccept, btnReject, btnMonthlyFee;
    private ImageView tempStuImageView;
    Firebase ref = new Firebase("https://schoolbus-708f4.firebaseio.com/");
    private ProgressDialog progressDialog;
    private Student student;
    private Schools school;
    private Double pricePerKm;
    boolean flag = false;
    private ProgressDialog mProgressDialog;
    View stuRequestFragment;
    String stuType;
    String morningUrl = "", eveningUrl = "";
    Double morningDis = 0.0, eveningDis = 0.0;
    int newMorningDis, newEveningDis;
    String[] monthArray;
    int fullSeats=0, reservedSeatsMorning =0,reservedSeatsAfternoon=0;

    public TempStudentFragment() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        stuRequestFragment = inflater.inflate(R.layout.fragment_temp_student, container, false);
        progressDialog = new ProgressDialog(getActivity());
        txtTempStuName = (TextView) stuRequestFragment.findViewById(R.id.txtTempStuName);
        txtTempStuSchool = (TextView) stuRequestFragment.findViewById(R.id.txtTempStuSchool);
        txtTempStuGrade = (TextView) stuRequestFragment.findViewById(R.id.txtTempStuGrade);
        txtTempStuClass = (TextView) stuRequestFragment.findViewById(R.id.txtTempStuClass);
        txtTempStuPickup = (TextView) stuRequestFragment.findViewById(R.id.txtTempStuPickup);
        txtTempStuDrop = (TextView) stuRequestFragment.findViewById(R.id.txtTempStuDrop);
        txtTempStuPickupTime = (TextView) stuRequestFragment.findViewById(R.id.txtTempStuPickupTime);
        txtTempStuMonthlyFee = (TextView) stuRequestFragment.findViewById(R.id.txtTempStuMonthlyFee);
        txtTempStuType = (TextView) stuRequestFragment.findViewById(R.id.txtTempStuType);
        btnAccept = (Button) stuRequestFragment.findViewById(R.id.btnAccept);
        btnReject = (Button) stuRequestFragment.findViewById(R.id.btnReject);
        btnMonthlyFee = (Button) stuRequestFragment.findViewById(R.id.btnMonthlyFee);
        tempStuImageView = (ImageView) stuRequestFragment.findViewById(R.id.tempStuImageView);
        mProgressDialog = new ProgressDialog(getActivity());
        monthArray = getResources().getStringArray(R.array.months);
        getTempStudentData();
        getPricePerKm();
        getSeatData();

        btnMonthlyFee.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkStuId();
                calculateMonthlyFee();

            }
        });

        btnAccept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!txtTempStuMonthlyFee.getText().equals("")) {
                    if (flag) {
                        if(checkSeats()){
                            if (latestStudentID.equals("1")) {
                                ref.child("Drivers").child(driverId).child("permanent").child("studentCounter").setValue(1);
                                setSeatData();
                                addStudent();
                                saveStudentWithParent();
                                addStudentToPayments();
                                addStuPickLocation();
                                sendAcceptPush(student.getParentEmail());
                                flag = false;
                            } else {
                                setSeatData();
                                addStudent();
                                saveStudentWithParent();
                                addStudentToPayments();
                                addStuPickLocation();
                                sendAcceptPush(student.getParentEmail());
                                flag = false;
                            }
                        }else {
                            Toast.makeText(getActivity(), "Your Vehicle Don't Have Enough Seats", Toast.LENGTH_SHORT).show();
                        }

                    }
                } else {
                    Toast.makeText(getActivity(), "Please Add Monthly Fee", Toast.LENGTH_SHORT).show();
                }
            }
        });

        btnReject.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
                alertDialogBuilder.setMessage("Are you sure,You wanted to Reject This Student ");

                alertDialogBuilder.setPositiveButton("yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                        deleteData();
                        sendRejectPush(student.getParentEmail());
                        Toast.makeText(getActivity(), "Rejected", Toast.LENGTH_SHORT).show();
                        FragmentTransaction ft = getFragmentManager().beginTransaction();
                        ft.replace(R.id.temp_student_frame_container, new StudentRequestsFragment());
                        ft.commit();
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
        });


        return stuRequestFragment;
    }

    private boolean checkSeats(){
        if(student.getStuType().equals("Morning Only")){
            if(fullSeats>reservedSeatsMorning){
                return true;
            }else {
                return false;
            }
        }else if(student.getStuType().equals("Afternoon Only")){
            if(fullSeats>reservedSeatsAfternoon){
                return true;
            }else {
                return false;
            }

        }else if(student.getStuType().equals("Both")){
            if(fullSeats>reservedSeatsAfternoon && fullSeats>reservedSeatsMorning){
                return true;
            }else {
                return false;
            }
        }else {
            return false;
        }
    }

    private void setSeatData(){
        if(student.getStuType().equals("Morning Only")){
            ref.child("Drivers").child(driverId).child("reservedSeatsMorning").setValue(String.valueOf(reservedSeatsMorning +1));
        }else if(student.getStuType().equals("Afternoon Only")){
            ref.child("Drivers").child(driverId).child("reservedSeatsAfternoon").setValue(String.valueOf(reservedSeatsAfternoon +1));
        }else if(student.getStuType().equals("Both")){
            ref.child("Drivers").child(driverId).child("reservedSeatsMorning").setValue(String.valueOf(reservedSeatsMorning +1));
            ref.child("Drivers").child(driverId).child("reservedSeatsAfternoon").setValue(String.valueOf(reservedSeatsAfternoon +1));
        }

    }

    private void getSeatData() {
        ref.child("Drivers").child(driverId).child("allSeats").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                fullSeats = Integer.parseInt(dataSnapshot.getValue().toString());
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });

        ref.child("Drivers").child(driverId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.hasChild("reservedSeatsMorning")){
                    reservedSeatsMorning = Integer.parseInt(dataSnapshot.child("reservedSeatsMorning").getValue().toString());
                }else {
                    reservedSeatsMorning = 0;
                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });

        ref.child("Drivers").child(driverId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.hasChild("reservedSeatsAfternoon")){
                    reservedSeatsAfternoon = Integer.parseInt(dataSnapshot.child("reservedSeatsAfternoon").getValue().toString());
                }else {
                    reservedSeatsAfternoon = 0;
                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });
    }

    private void addStudentToPayments() {
        Calendar calendar = Calendar.getInstance();
        final int month = calendar.get(Calendar.MONTH);
        final int year = calendar.get(Calendar.YEAR);
        final StudentPayment studentPayment = new StudentPayment();
        PaymentList paymentList = new PaymentList();
        studentPayment.setStuId(student.getStuID());
        studentPayment.setStuName(student.getStuName());
        studentPayment.setStuMonthlyFee(student.getStuMonthlyFee());
        studentPayment.setStuLastPaidMonth("");
        studentPayment.setStuLastPaidYear("");
        studentPayment.setStuReceivables("");
        studentPayment.setStuRegMonth(String.valueOf(month));
        studentPayment.setStuRegYear(String.valueOf(year));
        paymentList.setStuID(student.getStuID());
        paymentList.setStuName(student.getStuName());
        ref.child("Drivers").child(driverId).child("permanent").child("permanentStudent").child(latestStudentID).child("paymentInfo").setValue(studentPayment);
        for (int i = 11; i >= month; i--) {
            ref.child("Drivers").child(driverId).child("permanent").child("permanentStudent").child(latestStudentID).child("payments").child(String.valueOf(year)).child(monthArray[i]).child("status").setValue("Not Paid");
            ref.child("Drivers").child(driverId).child("budget").child("paymentList").child(String.valueOf(year)).child(monthArray[i]).child("notPaid").child(latestStudentID).setValue(paymentList);
            //ref.child("Drivers").child(driverId).child("budget").child("paymentList").child(String.valueOf(year)).child(monthArray[i]).child("notPaid").child(latestStudentID).child("StuName").setValue(student.getStuName());
        }

        ref.child("Drivers").child(driverId).child("budget").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                int totalIncome = Integer.parseInt((String) dataSnapshot.child("summery").child(String.valueOf(year)).child(monthArray[month]).child("totalIncome").getValue());
                int total = Integer.parseInt(dataSnapshot.child("total").getValue().toString());

                int mTotalIncome = totalIncome + Integer.parseInt(studentPayment.getStuMonthlyFee());
                int mTotal = total + Integer.parseInt(studentPayment.getStuMonthlyFee());

                ref.child("Drivers").child(driverId).child("budget").child("summery").child(String.valueOf(year)).child(monthArray[month]).child("totalIncome").setValue(String.valueOf(mTotalIncome));
                ref.child("Drivers").child(driverId).child("budget").child("total").setValue(String.valueOf(mTotal));
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });

    }

    private void getTempStudentData() {
        progressDialog.setMessage("Loading Data...");
        progressDialog.show();
        ref.child("Drivers").child(driverId).child("temp").child("tempStudent").child(tempStudentId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                student = dataSnapshot.getValue(Student.class);
                txtTempStuType.setText(student.getStuType());
                stuType = student.getStuType();
                txtTempStuName.setText(student.getStuName());
                txtTempStuSchool.setText(student.getStuSchool());
                txtTempStuGrade.setText(student.getStuGrade());
                txtTempStuClass.setText(student.getStuClass());
                if (stuType.equals("Afternoon Only")) {
                    txtTempStuDrop.setText(student.getStuDropLocation());
                    txtTempStuPickup.setText("-");
                }
                if (stuType.equals("Morning Only")) {
                    txtTempStuPickup.setText(student.getStuPickLocation());
                    txtTempStuDrop.setText("-");
                }
                else {
                    txtTempStuPickup.setText(student.getStuPickLocation());
                    txtTempStuDrop.setText(student.getStuDropLocation());
                }
                txtTempStuPickupTime.setText(student.getStuPickTime());
                getSchoolDetails();
                getImage(student.getStuImage());
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });
    }

    private void getImage(String url){
        try {
            byte [] encodeByte= Base64.decode(url,Base64.DEFAULT);
            Bitmap bitmap= BitmapFactory.decodeByteArray(encodeByte, 0, encodeByte.length);
            tempStuImageView.setImageBitmap(bitmap);
            progressDialog.dismiss();
        } catch(Exception e) {
            e.getMessage();
        }
    }

    private void calculateMonthlyFee() {
        mProgressDialog.setMessage("Calculating");
        mProgressDialog.show();
        mProgressDialog.setCancelable(false);

            if (!stuType.equals("Afternoon Only")) {
            morningUrl = "https://maps.googleapis.com/maps/api/distancematrix/json?origins=" + student.getStuPickLatitude() + "," + student.getStuPickLongitude() + "&destinations=" + school.getSchoolLatitude() + "," + school.getSchoolLongitude() + "&mode=driving&language=fr-FR&avoid=tolls&key=AIzaSyDb5C2cX_fb7XLzq0AjaD2TjXzULYYmEsc";
            new GeoDistance(stuRequestFragment.getContext()).execute(morningUrl);
            Log.d("========<>", "eveningOnlyFalse");
        }
        if (!stuType.equals("Morning Only")) {
            eveningUrl = "https://maps.googleapis.com/maps/api/distancematrix/json?origins=" + school.getSchoolLatitude() + "," + school.getSchoolLongitude() + "&destinations=" + student.getStuDropLatitude() + "," + student.getStuDropLongitude() + "&mode=driving&language=fr-FR&avoid=tolls&key=AIzaSyDb5C2cX_fb7XLzq0AjaD2TjXzULYYmEsc";
            new GeoDistance(stuRequestFragment.getContext()).execute(eveningUrl);
            Log.d("========<>", "MorningOnlyFalse");
        }
        final Handler key = new Handler();
        key.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (!stuType.equals("Afternoon Only")) {
                    morningDis = Double.valueOf(studistance) / 1000;
                    newMorningDis = (int) Math.round(morningDis);
                    Log.d("========<>", "EveningOnlyFalse  " + String.valueOf(newMorningDis));
                }
                if (!stuType.equals("Morning Only")) {
                    eveningDis = Double.valueOf(studistance) / 1000;
                    newEveningDis = (int) Math.round(eveningDis);
                    Log.d("========<>", "MorningOnlyFalse  " + String.valueOf(newEveningDis));
                }

                int monthlyFee = (newMorningDis + newEveningDis) * ((int) Math.round(pricePerKm));
                Log.d("========<>", "MorningOnlyFalse  " + String.valueOf(monthlyFee));
                txtTempStuMonthlyFee.setText(String.valueOf(monthlyFee));
                student.setStuMonthlyFee(String.valueOf(monthlyFee));
                mProgressDialog.dismiss();
            }
        }, 2000);


    }

    private void checkStuId() {
        mProgressDialog.setMessage("Calculating");
        mProgressDialog.show();
        mProgressDialog.setCancelable(false);
        ref.child("Drivers").child(driverId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChild("permanent")) {
                    getLatestStuID();

                } else {
                    latestStudentID = "1";
                    flag = true;
                    mProgressDialog.dismiss();
                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
            }
        });
    }

    private void getLatestStuID() {
        ref.child("Drivers").child(driverId).child("permanent").child("studentCounter").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Integer currentValue = Integer.parseInt(dataSnapshot.getValue().toString());
                Integer newValue = currentValue + 1;
                latestStudentID = newValue.toString();
                ref.child("Drivers").child(driverId).child("permanent").child("studentCounter").setValue(latestStudentID);
                //mProgressDialog.dismiss();
                flag = true;

            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });
    }

    private void addStudent() {
        progressDialog.setMessage("Saving Data...");
        progressDialog.show();
        student.setStuID(latestStudentID);
        ref.child("Drivers").child(driverId).child("permanent").child("permanentStudent").child(latestStudentID).child("info").setValue(student, new Firebase.CompletionListener() {
            @Override
            public void onComplete(FirebaseError firebaseError, Firebase firebase) {
                if (firebaseError == null) {
                    deleteData();
                    progressDialog.dismiss();
                    Toast.makeText(getActivity(), "Student Accepted", Toast.LENGTH_SHORT).show();
                    FragmentTransaction ft = getFragmentManager().beginTransaction();
                    ft.replace(R.id.temp_student_frame_container, new StudentRequestsFragment());
                    ft.commit();

                } else {
                    progressDialog.dismiss();
                    Toast.makeText(getActivity(), "Cannot Accept Student", Toast.LENGTH_SHORT).show();
                }
            }


        });

    }

    private void saveStudentWithParent() {
        Children children = new Children();
        children.setStuId(latestStudentID);
        children.setStuName(student.getStuName());
        ref.child("Parents").child(student.getParentID()).child("children").child(driverId).child(latestStudentID).setValue(children);

    }

    private void deleteData() {
        ref.child("Drivers").child(driverId).child("temp").child("tempStudent").child(tempStudentId).removeValue();
    }

    private void getSchoolDetails() {
        Query queryRef;
        queryRef = ref.child("Drivers").child(driverId).child("schools").child("routeSchools");
        queryRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot data : dataSnapshot.getChildren()) {
                    //for (DataSnapshot d : data.getChildren()) {
                        school = data.getValue(Schools.class);
                        if (school.getSchoolName().equals(student.getStuSchool())) {
                            break;
                        }
                    //}

                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });
    }

    private void getPricePerKm() {
        ref.child("Drivers").child(driverId).child("pricePerKm").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                pricePerKm = Double.valueOf(dataSnapshot.getValue().toString());
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });
    }

    private void addStuPickLocation(){
        DriverRoute morningDriverRoute = new DriverRoute();
        DriverRoute eveningDriverRoute = new DriverRoute();
        if(student.getStuType().equals("Morning Only")){
            morningDriverRoute.setStuID(latestStudentID);
            morningDriverRoute.setPlaceLatitude(student.getStuPickLatitude());
            morningDriverRoute.setPlaceLongitude(student.getStuPickLongitude());
            morningDriverRoute.setPlaceName(student.getStuName());
            morningDriverRoute.setAttend("Yes");
        }
        else if(student.getStuType().equals("Afternoon Only")){
            eveningDriverRoute.setStuID(latestStudentID);
            eveningDriverRoute.setPlaceLatitude(student.getStuDropLatitude());
            eveningDriverRoute.setPlaceLongitude(student.getStuDropLongitude());
            eveningDriverRoute.setPlaceName(student.getStuName());
            eveningDriverRoute.setAttend("Yes");
        }
        else if(student.getStuType().equals("Both")){
            morningDriverRoute.setStuID(latestStudentID);
            morningDriverRoute.setPlaceLatitude(student.getStuPickLatitude());
            morningDriverRoute.setPlaceLongitude(student.getStuPickLongitude());
            morningDriverRoute.setPlaceName(student.getStuName());
            morningDriverRoute.setAttend("Yes");

            eveningDriverRoute.setStuID(latestStudentID);
            eveningDriverRoute.setPlaceLatitude(student.getStuDropLatitude());
            eveningDriverRoute.setPlaceLongitude(student.getStuDropLongitude());
            eveningDriverRoute.setPlaceName(student.getStuName());
            eveningDriverRoute.setAttend("Yes");
        }

        if(student.getStuType().equals("Morning Only")){
            ref.child("Drivers").child(DriverActivity.userId).child("route").child("morningRoute").child(student.getStuID()).setValue(morningDriverRoute);
        }
        else if(student.getStuType().equals("Afternoon Only")){
            ref.child("Drivers").child(DriverActivity.userId).child("route").child("eveningRoute").child(student.getStuID()).setValue(eveningDriverRoute);
        }
        else if(student.getStuType().equals("Both")){
            ref.child("Drivers").child(DriverActivity.userId).child("route").child("morningRoute").child(student.getStuID()).setValue(morningDriverRoute);
            ref.child("Drivers").child(DriverActivity.userId).child("route").child("eveningRoute").child(student.getStuID()).setValue(eveningDriverRoute);
        }


    }

    private void sendAcceptPush(String to){
        ParseQuery pushQuery = ParseInstallation.getQuery();
        pushQuery.whereEqualTo("email", to);

        // Send push notification to query
        ParsePush push = new ParsePush();
        push.setQuery(pushQuery); // Set our Installation query
        push.setMessage("Your Child Request About "+student.getStuName()+" Accepted");
        push.sendInBackground();
        Log.d("PUSH MESSAGE", "SENT "+to);
    }

    private void sendRejectPush(String to){
        ParseQuery pushQuery = ParseInstallation.getQuery();
        pushQuery.whereEqualTo("email", to);

        // Send push notification to query
        ParsePush push = new ParsePush();
        push.setQuery(pushQuery); // Set our Installation query
        push.setMessage("Your Child Request About "+student.getStuName()+" Rejected");
        push.sendInBackground();
        Log.d("PUSH MESSAGE", "SENT "+to);
    }

}

