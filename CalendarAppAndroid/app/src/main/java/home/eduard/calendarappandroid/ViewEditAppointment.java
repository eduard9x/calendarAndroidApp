package home.eduard.calendarappandroid;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class ViewEditAppointment extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.create_appointment);

        final Activity thisActivity = this;

        final String day, month, year;
        final String[] Months = {"January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December"};


        final EditText titleEditText = (EditText) findViewById(R.id.titleEditText);
        final EditText timeEditText = (EditText) findViewById(R.id.timeEditText);
        final EditText detailsEditText = (EditText) findViewById(R.id.detailsEditText);
        final TextView errorLabel = (TextView) findViewById(R.id.errorLabel);

        String intentVal = getIntent().getStringExtra("DoNext");

        String[] date = intentVal.split(";;;");

        day = date[0];
        month = date[1];
        year = date[2];

        TextView dateLabel = (TextView) findViewById(R.id.dateLabel);
        dateLabel.setText(day + " " + Months[Integer.parseInt(month)] + " " + year);

        Button SaveButton = (Button) findViewById(R.id.saveButton);

        SaveButton.setFocusable(true);
        SaveButton.setFocusableInTouchMode(true);///add this line
        SaveButton.requestFocus();


        SaveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String title = titleEditText.getText().toString();
                String time = timeEditText.getText().toString();
                String details = detailsEditText.getText().toString();

                Log.v("Save button: >>> ", " clicked ");

                if (title.equals("")) {
                    errorLabel.setText("Please enter a valid title.");
                } else if (time.equals("")) {
                    errorLabel.setText("Please enter a valid time.");
                } else {
                    thisActivity.finish();
                }
                String date = day + ";;;" + month + ";;;" + year;
//                createIntent(date);
            }
        });


    }

    void createIntent(String doNext) {
        Intent whatToDoNext = new Intent(this, ViewEditAppointment.class);
        whatToDoNext.putExtra("DoNext", doNext);
        this.startActivity(whatToDoNext);
    }
}
