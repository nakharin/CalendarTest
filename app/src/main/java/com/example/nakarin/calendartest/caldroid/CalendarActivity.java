package com.example.nakarin.calendartest.caldroid;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.Toast;

import com.example.nakarin.calendartest.R;
import com.roomorama.caldroid.CaldroidFragment;
import com.roomorama.caldroid.CaldroidListener;

import java.util.Calendar;
import java.util.Date;

public class CalendarActivity extends FragmentActivity {

    private CaldroidFragment caldroidFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendar);

        setUpCalender();

        setUpCalendarListener();
    }

    private void setUpCalender() {

        caldroidFragment = new CaldroidFragment();
        Bundle args = new Bundle();
        Calendar cal = Calendar.getInstance();
        args.putInt(CaldroidFragment.START_DAY_OF_WEEK, CaldroidFragment.MONDAY);
        args.putInt(CaldroidFragment.MONTH, cal.get(Calendar.MONTH) + 1);
        args.putInt(CaldroidFragment.YEAR, cal.get(Calendar.YEAR));
        caldroidFragment.setArguments(args);

        android.support.v4.app.FragmentTransaction t = getSupportFragmentManager().beginTransaction();
        t.replace(R.id.calendar, caldroidFragment);
        t.commit();
    }

    private void setUpCalendarListener() {

        CaldroidListener caldroidListener = new CaldroidListener() {

            @Override
            public void onSelectDate(Date date, View view) {
                Toast.makeText(getApplicationContext(),
                        "Long click " + date.toString(),
                        Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onLongClickDate(Date date, View view) {
                setUpCalenderEvent(date);
            }
        };

        caldroidFragment.setCaldroidListener(caldroidListener);
    }

    private void setUpCalenderEvent(Date date) {
        caldroidFragment.setBackgroundResourceForDate(R.drawable.ic_event2, date);
        caldroidFragment.refreshView();
    }
}
