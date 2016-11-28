package com.example.kusha.schoolbus.fragments.parent;

import android.app.Dialog;
import android.app.DialogFragment;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.widget.TimePicker;
import android.widget.Toast;

import java.util.Calendar;

/**
 * Created by kusha on 11/20/2016.
 */

public class TimePickerFragment extends DialogFragment implements TimePickerDialog.OnTimeSetListener {

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final Calendar c = Calendar.getInstance();
        int hour = c.get(Calendar.HOUR_OF_DAY);
        int minute = c.get(Calendar.MINUTE);

        //Create and return a new instance of TimePickerDialog
        return new TimePickerDialog(getActivity(),this, hour, minute,
                DateFormat.is24HourFormat(getActivity()));
    }

    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        String newMinute = "00";
        if(minute<10){
            newMinute = "0"+String.valueOf(minute);
        }
        else {
            newMinute = String.valueOf(minute);
        }
        if(hourOfDay<5 || hourOfDay>7){
            Toast.makeText(getActivity(), "Pickup Time is Not Valid", Toast.LENGTH_SHORT).show();
        }else {
            if(hourOfDay==7 && minute>15){
                Toast.makeText(getActivity(), "Pickup Time is Not Valid", Toast.LENGTH_SHORT).show();
            }
            else {
                AddNewChildFragment.txtPickTime.setText(String.valueOf(hourOfDay)+":"+newMinute+" AM");
            }
        }
    }
}
