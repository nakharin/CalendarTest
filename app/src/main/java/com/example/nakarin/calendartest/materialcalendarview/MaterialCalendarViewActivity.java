package com.example.nakarin.calendartest.materialcalendarview;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.Toast;

import com.example.nakarin.calendartest.R;
import com.example.nakarin.calendartest.materialcalendarview.decorators.EventDecorator;
import com.example.nakarin.calendartest.materialcalendarview.decorators.HighlightWeekendsDecorator;
import com.example.nakarin.calendartest.materialcalendarview.decorators.OneDayDecorator;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.OnDateChangedListener;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.Executors;

public class MaterialCalendarViewActivity extends AppCompatActivity implements
        OnDateChangedListener, SwipeRefreshLayout.OnRefreshListener {

    private static final DateFormat FORMATTER = SimpleDateFormat.getDateInstance();

    private final OneDayDecorator oneDayDecorator = new OneDayDecorator();

    private SwipeRefreshLayout swipe_refresh_layout;

    private ScrollView scrollView;

    private MaterialCalendarView materialCalendarView;

    private ListView lv_date;

    String[] values = new String[] { "Android List View",
            "Adapter implementation",
            "Simple List View In Android",
            "Create List View Android",
            "Android Example",
            "List View Source Code",
            "List View Array Adapter",
            "Android Example List View"
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_meteria_calendar_view);

        android.support.v7.app.ActionBar bar = getSupportActionBar();
        bar.setTitle("ตารางปฎิทินนัดหมาย");
        bar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#00B977")));

        // Method from this class
        setUpWidget();

        // Method from this class
        setUpCalendar();

        // Method from this class
        setUpListView();

        swipe_refresh_layout.setOnRefreshListener(this);
        materialCalendarView.setOnDateChangedListener(this);

        new ApiSimulator().executeOnExecutor(Executors.newSingleThreadExecutor());
    }

    private void setUpWidget() {

        swipe_refresh_layout = (SwipeRefreshLayout) this.findViewById(R.id.swipe_refresh_layout);
        swipe_refresh_layout.setEnabled(false);
        swipe_refresh_layout.setColorSchemeResources(R.color.Green, R.color.Blue,
                R.color.Yellow, R.color.Red);

        scrollView = (ScrollView) this.findViewById(R.id.scrollView);

        materialCalendarView = (MaterialCalendarView) this.findViewById(R.id.materialCalendarView);

        lv_date = (ListView) this.findViewById(R.id.lv_date);
    }

    private void setUpCalendar() {

        materialCalendarView.setShowOtherDates(true);

        Calendar calendar = Calendar.getInstance();
        materialCalendarView.setSelectedDate(calendar.getTime());

        materialCalendarView.setFirstDayOfWeek(Calendar.SUNDAY);

        materialCalendarView.setHeaderTextAppearance(R.style.TextAppearance_AppCompat_Large);
        materialCalendarView.setDateTextAppearance(R.style.TextAppearance_AppCompat_Medium);
        materialCalendarView.setWeekDayTextAppearance(R.style.TextAppearance_AppCompat_Medium);

        HashSet<CalendarDay> dayHashSet = new HashSet<>();
        dayHashSet.add(CalendarDay.from(2015, Calendar.AUGUST, 14));
        dayHashSet.add(CalendarDay.from(2015, Calendar.AUGUST, 13));
        dayHashSet.add(CalendarDay.from(2015, Calendar.AUGUST, 12));

        materialCalendarView.addDecorators(oneDayDecorator,
                new HighlightWeekendsDecorator(),
                new EventDecorator(Color.BLUE, dayHashSet));
    }

    private void setUpListView() {

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_list_item_1, android.R.id.text1, values);

        // Assign adapter to ListView
        lv_date.setAdapter(adapter);

        setListViewHeightBasedOnChildren(lv_date);
    }

    /*
    * Method for Setting the Height of the ListView dynamically. Hack to fix
    * the issue of not showing all the items of the ListView when placed inside
    * a ScrollView
    */
    private void setListViewHeightBasedOnChildren(ListView listView) {
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null)
            return;

        int desiredWidth = View.MeasureSpec.makeMeasureSpec(listView.getWidth(),
                View.MeasureSpec.UNSPECIFIED);
        int totalHeight = 0;
        View view = null;
        for (int i = 0; i < listAdapter.getCount(); i++) {
            view = listAdapter.getView(i, view, listView);
            if (i == 0)
                view.setLayoutParams(new ViewGroup.LayoutParams(desiredWidth,
                        android.view.ViewGroup.LayoutParams.WRAP_CONTENT));

            view.measure(desiredWidth, View.MeasureSpec.UNSPECIFIED);
            totalHeight += view.getMeasuredHeight();
        }
        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight
                + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
        listView.setLayoutParams(params);
        listView.requestLayout();
    }

    @Override
    public void onRefresh() {

        new ApiSimulator().executeOnExecutor(Executors.newSingleThreadExecutor());
    }

    @Override
    public void onDateChanged(@NonNull MaterialCalendarView materialCalendarView, CalendarDay calendarDay) {
        //If you change a decorate, you need to invalidate decorators
        oneDayDecorator.setDate(calendarDay.getDate());
        materialCalendarView.invalidateDecorators();

        Toast.makeText(getApplicationContext(),
                FORMATTER.format(calendarDay.getDate()), Toast.LENGTH_LONG).show();

        scrollView.fullScroll(View.FOCUS_DOWN);
    }

    /**
     * Simulate an API call to show how to add decorators
     */
    private class ApiSimulator extends AsyncTask<Void, Void, List<CalendarDay>> {

        @Override
        protected List<CalendarDay> doInBackground(@NonNull Void... voids) {

            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            Calendar calendar = Calendar.getInstance();
            calendar.add(Calendar.MONTH, -2);
            ArrayList<CalendarDay> dates = new ArrayList<>();
            for (int i = 0; i < 30; i++) {
                CalendarDay day = CalendarDay.from(calendar);
                dates.add(day);
                calendar.add(Calendar.DATE, 5);
            }

            return dates;
        }

        @Override
        protected void onPostExecute(@NonNull List<CalendarDay> calendarDays) {
            super.onPostExecute(calendarDays);

            if(isFinishing()) {
                return;
            }

            materialCalendarView.addDecorator(new EventDecorator(Color.RED, calendarDays));

            swipe_refresh_layout.setRefreshing(false);
            scrollView.smoothScrollTo(0, 0);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu items for use in the action bar
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_calendar, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle presses on the action bar items
        switch (item.getItemId()) {
            case R.id.action_add:
                Log.i("action_add", "Start");
                Toast.makeText(this, "Create Event in Day", Toast.LENGTH_LONG).show();
                Log.i("action_add", "End");
                return true;
            case R.id.action_day:
                Log.i("action_day", "Start");
                materialCalendarView.setSelectedDate(CalendarDay.today());
                Log.i("action_day", "End");
                return true;
            case R.id.action_refresh:
                Log.i("action_refresh", "Start");
                swipe_refresh_layout.setRefreshing(true);
                new ApiSimulator().executeOnExecutor(Executors.newSingleThreadExecutor());

                Log.i("action_refresh", "End");
                return true;
            default:
                return false;
        }
    }
}
