package com.example.kusha.schoolbus.activities.driver;

import android.support.design.widget.TabLayout;
import android.support.v4.app.NavUtils;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;

import com.example.kusha.schoolbus.R;
import com.example.kusha.schoolbus.adapter.ViewPagerAdapter;
import com.example.kusha.schoolbus.fragments.driver.CurrentStudentHolderFragment;
import com.example.kusha.schoolbus.fragments.driver.CurrentStudentsFragment;
import com.example.kusha.schoolbus.fragments.driver.RouteFeesFragment;
import com.example.kusha.schoolbus.fragments.driver.RouteLocationFragment;
import com.example.kusha.schoolbus.fragments.driver.RouteSchoolsFragment;
import com.example.kusha.schoolbus.fragments.driver.StudentRequestsFragment;
import com.example.kusha.schoolbus.fragments.driver.TempStudentFragment;
import com.example.kusha.schoolbus.fragments.driver.TempStudentHolderFragment;

public class StudentActivity extends AppCompatActivity {
    private TabLayout routeTabLayout;
    private ViewPager routeViewPager;
    ActionBar actionBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student);
        setTitle("Students");
        actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        routeTabLayout = (TabLayout) findViewById(R.id.routeTabs);
        routeViewPager = (ViewPager)findViewById(R.id.routeViewpager);
        setupViewPager(routeViewPager);
        routeTabLayout.setupWithViewPager(routeViewPager);
    }

    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new TempStudentHolderFragment(), "Requests");
        adapter.addFragment(new CurrentStudentHolderFragment(), "Current");
        viewPager.setAdapter(adapter);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
