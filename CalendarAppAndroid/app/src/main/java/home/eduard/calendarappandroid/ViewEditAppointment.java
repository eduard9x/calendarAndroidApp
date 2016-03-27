package home.eduard.calendarappandroid;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class ViewEditAppointment extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        final String day,month,year;
        final String[] Months= {"January","February","March","April","May","June","July","August","September","October","November","December"};

        super.onCreate(savedInstanceState);
        setContentView(R.layout.create_appointment);

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);

        String intentVal = getIntent().getStringExtra("DoNext");

        String[] date = intentVal.split(" ");

        day = date[0];
        month = date[1];
        year = date[2];

        TextView dateLabel = (TextView) findViewById(R.id.dateLabel);
        dateLabel.setText(day + " " + Months[Integer.parseInt(month)] + " " + year);

        Button SaveButton = (Button) findViewById(R.id.saveButton);

        SaveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                EditText titleEditText = (EditText) findViewById(R.id.titleEditText);
                EditText timeEditText = (EditText) findViewById(R.id.timeEditText);
                EditText detailsEditText = (EditText) findViewById(R.id.detailsEditText);
                TextView errorLabel = (TextView) findViewById(R.id.errorLabel);

                String title = titleEditText.getText().toString();
                String time = timeEditText.getText().toString();
                String details = detailsEditText.getText().toString();

                if(title.equals("")) Log.v(" <<<< error", "empty title");
                else if(time.equals("")) Log.v(" <<<< error", "empty time");

                String date = day + " " + month + " " + year;
                createIntent(date);
            }
        });
    }

    void createIntent(String doNext) {
        Intent whatToDoNext = new Intent(this, ViewEditAppointment.class);
        whatToDoNext.putExtra("DoNext", doNext);
        this.startActivity(whatToDoNext);
    }
}
