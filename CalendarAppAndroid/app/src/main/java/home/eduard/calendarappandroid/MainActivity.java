package home.eduard.calendarappandroid;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.ImageButton;

import java.util.Calendar;
import java.util.GregorianCalendar;

public class MainActivity extends Activity {

    int yearToSet, monthToSet, dayToSet;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);

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

                createIntent(date, "home.eduard.calendarappandroid.NewAppt");
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

    void createIntent(String doNext, String className) {
        try {
            Intent whatToDoNext = new Intent(this, Class.forName(className));
            whatToDoNext.putExtra("DoNext", doNext);
            this.startActivity(whatToDoNext);
        } catch (Exception ex) {
            Log.v("Class error", ex.toString());
        }
    }

    public void setTodaysDate() {
        GregorianCalendar cal = new GregorianCalendar();
        yearToSet = cal.get(Calendar.YEAR);
        monthToSet = cal.get(Calendar.MONTH);
        dayToSet = cal.get(Calendar.DAY_OF_MONTH);
    }
}
