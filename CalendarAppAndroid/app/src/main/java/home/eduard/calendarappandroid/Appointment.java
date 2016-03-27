package home.eduard.calendarappandroid;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

public class Appointment extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.appointment);

        String intentVal = getIntent().getStringExtra("DoNext");
        Log.v("intent: ---> ", intentVal);
    }

}
