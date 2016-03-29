package home.eduard.calendarappandroid;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.CalendarView;

import java.util.Calendar;
import java.util.GregorianCalendar;

public class MainActivity extends AppCompatActivity {

    int yearToSet, monthToSet, dayToSet;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

       FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Snackbar.make(view, "put your own stuff", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        //save the today's date first
        setTodaysDate();

        Button myButton = (Button) findViewById(R.id.button);
//        final CalendarView myCalendar = (CalendarView) findViewById(R.id.calendarView);

        myButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String date = Integer.toString(dayToSet) + ";;;" + Integer.toString(monthToSet) + ";;;" + Integer.toString(yearToSet);
                Log.v("new appt straight", date);

                createIntent(date);

            }
        });


//        //region Saving the selected date
//        myCalendar.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
//
//            @Override
//            public void onSelectedDayChange(CalendarView view, int year, int month, int dayOfMonth) {
//                yearToSet = year;
//                monthToSet = month;
//                dayToSet = dayOfMonth;
//            }
//        });
//        //endregion
    }


    public void setTodaysDate(){
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

    void createIntent(String doNext) {
        Intent whatToDoNext = new Intent(this, ViewEditAppointment.class);
        whatToDoNext.putExtra("DoNext", doNext);
        this.startActivity(whatToDoNext);
    }
}
