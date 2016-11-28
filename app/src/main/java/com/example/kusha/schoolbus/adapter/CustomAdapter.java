package com.example.kusha.schoolbus.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

import static android.R.attr.resource;

/**
 * Created by kusha on 11/20/2016.
 */

public class CustomAdapter extends ArrayAdapter<String> {

    private int hidingItemIndex;

    public CustomAdapter(Context context, int textViewResourceId, List<String> objects, int hidingItemIndex ) {
        super(context, textViewResourceId, objects);
        this.hidingItemIndex = hidingItemIndex;
    }
    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        View v = null;
        if (position == hidingItemIndex) {
            TextView tv = new TextView(getContext());
            tv.setVisibility(View.GONE);
            v = tv;
        } else {
            v = super.getDropDownView(position, null, parent);
        }
        return v;
    }
}
