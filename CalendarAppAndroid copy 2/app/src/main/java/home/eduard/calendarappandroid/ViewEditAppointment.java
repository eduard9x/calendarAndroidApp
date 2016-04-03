package home.eduard.calendarappandroid;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class ViewEditAppointment extends Activity {

    private SQLiteAdapter mySQLiteAdapter;
    private String day, month, year, title, time, details, _id;
    private boolean update;
    private String NEWAPPT = "NewAppointment";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.create_appointment);

        update = false;
        mySQLiteAdapter = new SQLiteAdapter(this);

        final Activity thisActivity = this;

        final String[] Months = {"January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December"};

        final EditText titleEditText = (EditText) findViewById(R.id.titleEditText);
        final EditText timeEditText = (EditText) findViewById(R.id.timeEditText);
        final EditText detailsEditText = (EditText) findViewById(R.id.detailsEditText);
        final TextView errorLabel = (TextView) findViewById(R.id.errorLabel);

        String intentVal = getIntent().getStringExtra("DoNext");

        final String[] data = intentVal.split(";;;");

        day = data[0];
        month = data[1];
        year = data[2];

        if (data.length == 7) {

            update = true;

            title = data[3];
            time = data[4];
            details = data[5];
            _id = data[6];

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

                boolean allowTitle;
                if (update)
                    allowTitle = mySQLiteAdapter.allowTitle(title, day + "-" + month + "-" + year, _id);
                else
                    allowTitle = mySQLiteAdapter.allowTitle(title, day + "-" + month + "-" + year, NEWAPPT);

                Log.v("<<< allow Title: ", Boolean.toString(allowTitle));

                if (!allowTitle) {

                    AlertDialog.Builder myDialog
                            = new AlertDialog.Builder(ViewEditAppointment.this);
                    myDialog.setTitle("Appointment already exists.");
                    myDialog.setMessage("Appointment " + title + " already exists, please choose a different event title.");

                    myDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        // do something when the button is clicked
                        public void onClick(DialogInterface arg0, int arg1) {
                            //doNothing
                        }
                    });

                    myDialog.show();
                } else if (title.equals("")) {
                    errorLabel.setText("Please enter a valid " + getResources().getString(R.string.titleLabel));
                } else if (!timeValid) {
                    errorLabel.setText("Please enter a valid " + getResources().getString(R.string.timeLabel));
                } else {

                    mySQLiteAdapter.openToWrite();

                    String data1 = day + "-" + month + "-" + year;
                    String data2 = titleEditText.getText().toString();
                    String data3 = timeEditText.getText().toString();
                    String data4 = detailsEditText.getText().toString();

                    String[] timeToCheck = data3.split(":");
                    int[] timeIntegers = {Integer.parseInt(timeToCheck[0]), Integer.parseInt(timeToCheck[1])};

                    for (int i = 0; i < timeToCheck.length; i++)
                        if (timeIntegers[i] < 10)
                            timeToCheck[i] = "0" + Integer.toString(timeIntegers[i]);

                    data3 = timeToCheck[0] + ":" + timeToCheck[1];

                    if (update) {
                        Log.v("<<< UPDATE DB: ", Boolean.toString(update));
                        mySQLiteAdapter.update(_id, data1, data2, data3, data4);
                    } else {
                        Log.v("<<< INSERT DB: ", Boolean.toString(!update));
                        mySQLiteAdapter.insert(data1, data2, data3, data4);
                    }
                    mySQLiteAdapter.close();

                    thisActivity.finish();
                    Toast.makeText(ViewEditAppointment.this, "Saved", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    public void hideKeyboard(View view) {
        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }
}
