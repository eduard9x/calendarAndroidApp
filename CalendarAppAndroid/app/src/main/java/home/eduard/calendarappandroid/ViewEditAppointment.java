package home.eduard.calendarappandroid;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class ViewEditAppointment extends Activity {

    private SQLiteAdapter mySQLiteAdapter;
    private String day, month, year, title, time, details, _id;
    private boolean update;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.create_appointment);

        update=false;
        mySQLiteAdapter = new SQLiteAdapter(this);

        final Activity thisActivity = this;

        final String[] Months = {"January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December"};

        final EditText titleEditText = (EditText) findViewById(R.id.titleEditText);
        final EditText timeEditText = (EditText) findViewById(R.id.timeEditText);
        final EditText detailsEditText = (EditText) findViewById(R.id.detailsEditText);
        final TextView errorLabel = (TextView) findViewById(R.id.errorLabel);

        String intentVal = getIntent().getStringExtra("DoNext");

        final String[] date = intentVal.split(";;;");

        day = date[0];
        month = date[1];
        year = date[2];

        if(date.length==7){

            update=true;

            title = date[3];
            time = date[4];
            details = date[5];
            _id = date[6];

            titleEditText.setText(title);
            timeEditText.setText(time);
            detailsEditText.setText(details);
        }


        TextView dateLabel = (TextView) findViewById(R.id.dateLabel);
        dateLabel.setText(day + " " + Months[Integer.parseInt(month)] + " " + year);

        Button SaveButton = (Button) findViewById(R.id.saveButton);

        titleEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    hideKeyboard(v);
                }
            }
        });

        detailsEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    hideKeyboard(v);
                }
            }
        });

        timeEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    hideKeyboard(v);
                }
            }
        });

        SaveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.v("Save button: >>> ", " clicked ");

                hideKeyboard(view);

                String title = titleEditText.getText().toString();
                String time = timeEditText.getText().toString();
                String details = detailsEditText.getText().toString();
                int hours = -1, minutes = -1;
                boolean timeValid = false;

                try {
                    String[] validator = time.split(":");

                    if (validator.length != 2)
                        throw new Exception("Please input both hours and minutes.");
                    else {
                        hours = Integer.parseInt(validator[0]);
                        minutes = Integer.parseInt(validator[1]);
                    }

                    if (hours >= 0 && minutes >= 0)
                        if (hours <= 23 && minutes <= 59)
                            timeValid = true;

                } catch (Exception ex) {
                    Log.v("<<< Exception time: ", ex.toString());
                }


                if (title.equals("")) {
                    errorLabel.setText("Please enter a valid " + getResources().getString(R.string.titleLabel));
                } else if (!timeValid) {
                    errorLabel.setText("Please enter a valid " + getResources().getString(R.string.timeLabel));
                } else {

                    mySQLiteAdapter.openToWrite();

                    String data1 = day + "-" + month + "-" + year;
                    String data2 = titleEditText.getText().toString();
                    String data3 = timeEditText.getText().toString();
                    String data4 = detailsEditText.getText().toString();


                    if(update){
                        mySQLiteAdapter.update(_id, data1, data2, data3, data4);
                    }else{
                        mySQLiteAdapter.insert(data1, data2, data3, data4);
                    }
                    mySQLiteAdapter.close();
                    thisActivity.finish();
                }
            }
        });

    }

    public void hideKeyboard(View view) {
        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }
}
