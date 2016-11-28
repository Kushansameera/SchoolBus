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
import com.example.kusha.schoolbus.fragments.driver.AddNewPaymentFragment;
import com.example.kusha.schoolbus.fragments.driver.ViewPaymentFragment;

public class PaymentActivity extends AppCompatActivity {

    private TabLayout paymentTabLayout;
    private ViewPager paymentViewPager;
    ActionBar actionBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);
        setTitle("Payments");
        actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        paymentTabLayout = (TabLayout) findViewById(R.id.paymentTabs);
        paymentViewPager = (ViewPager)findViewById(R.id.paymentViewpager);
        setupViewPager(paymentViewPager);
        paymentTabLayout.setupWithViewPager(paymentViewPager);
    }

    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new AddNewPaymentFragment(), "Add New");

        adapter.addFragment(new ViewPaymentFragment(), "View");
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
