package com.example.kusha.schoolbus.application;

import android.app.Application;

import com.firebase.client.Firebase;
import com.parse.Parse;
import com.squareup.otto.Bus;
import com.squareup.otto.ThreadEnforcer;

/**
 * Created by kusha on 11/3/2016.
 */

public class ApplicationClass extends Application {
    public static Bus bus;
    @Override
    public void onCreate() {
        super.onCreate();
        Firebase.setAndroidContext(this);
        //Firebase.getDefaultConfig().setPersistenceEnabled(true);
        bus = new Bus(ThreadEnforcer.MAIN);
        Parse.enableLocalDatastore(this);
        Parse.initialize(this);
    }
}
