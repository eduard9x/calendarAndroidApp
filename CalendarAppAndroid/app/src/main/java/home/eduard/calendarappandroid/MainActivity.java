package home.eduard.calendarappandroid;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.ImageButton;

import java.util.Calendar;
import java.util.GregorianCalendar;

public class MainActivity extends AppCompatActivity {

    int yearToSet, monthToSet, dayToSet;
    private SQLiteAdapter mySQLiteAdapter;
    Cursor cursor;
    Activity thisActivity = this;

    private final String THESAURUS_KEY = "pBnxuiTWACxzPqMYWoxy";
    private final String THESAURUS_URL = "http://thesaurus.altervista.org/thesaurus/v1?word=peace&language=en_US&key=pBnxuiTWACxzPqMYWoxy&output=xml";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //save the today's date first
        setTodaysDate();

        Button newButton = (Button) findViewById(R.id.newButton);
        Button ViewEditButton = (Button) findViewById(R.id.viewButton);
        Button deleteButton = (Button) findViewById(R.id.deleteButton);
        ImageButton searchButton = (ImageButton) findViewById(R.id.searchButton);
        Button moveButton = (Button) findViewById(R.id.moveButton);

        final CalendarView myCalendar = (CalendarView) findViewById(R.id.calendarView);

        ViewEditButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String date = Integer.toString(dayToSet) + ";;;" + Integer.toString(monthToSet) + ";;;" + Integer.toString(yearToSet);

                createIntent(date, "home.eduard.calendarappandroid.Picker");
            }
        });

        newButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String date = Integer.toString(dayToSet) + ";;;" + Integer.toString(monthToSet) + ";;;" + Integer.toString(yearToSet);
                Log.v("new appt straight", date);

                createIntent(date, "home.eduard.calendarappandroid.ViewEditAppointment");
            }
        });

        moveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String date = Integer.toString(dayToSet) + ";;;" + Integer.toString(monthToSet) + ";;;" + Integer.toString(yearToSet);

                createIntent(date, "home.eduard.calendarappandroid.Move");
            }
        });

        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String date = Integer.toString(dayToSet) + ";;;" + Integer.toString(monthToSet) + ";;;" + Integer.toString(yearToSet);
                Log.v("new appt straight", date);

                createIntent(date, "home.eduard.calendarappandroid.Search");
            }
        });

        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String date = Integer.toString(dayToSet) + ";;;" + Integer.toString(monthToSet) + ";;;" + Integer.toString(yearToSet);
                Log.v("new appt straight", date);

                createIntent(date, "home.eduard.calendarappandroid.Delete");
            }
        });

        //region Saving the selected date
        myCalendar.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {

            @Override
            public void onSelectedDayChange(CalendarView view, int year, int month, int dayOfMonth) {
                yearToSet = year;
                monthToSet = month;
                dayToSet = dayOfMonth;
            }
        });
        //endregion
    }


    public void setTodaysDate() {
        GregorianCalendar cal = new GregorianCalendar();
        yearToSet = cal.get(Calendar.YEAR);
        monthToSet = cal.get(Calendar.MONTH);
        dayToSet = cal.get(Calendar.DAY_OF_MONTH);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
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


    void createIntent(String doNext, String className) {
        try {
            Intent whatToDoNext = new Intent(this, Class.forName(className));
            whatToDoNext.putExtra("DoNext", doNext);
            this.startActivity(whatToDoNext);
        } catch (Exception ex) {
            Log.v("Class error", ex.toString());
        }
    }
}
