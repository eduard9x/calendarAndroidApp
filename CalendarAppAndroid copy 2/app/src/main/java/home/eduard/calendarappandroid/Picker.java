package home.eduard.calendarappandroid;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

public class Picker extends Activity {

    private SQLiteAdapter mySQLiteAdapter;
    ListView listContent;

    SimpleCursorAdapter cursorAdapter;
    Cursor cursor;

    Activity thisActivity = this;
    private String day, month, year;
    final String[] Months = {"January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December"};

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.picker);

        String intentVal = getIntent().getStringExtra("DoNext");
        final String[] date = intentVal.split(";;;");

        day = date[0];
        month = date[1];
        year = date[2];

        listContent = (ListView) findViewById(R.id.searchList);

        mySQLiteAdapter = new SQLiteAdapter(this);
        mySQLiteAdapter.openToWrite();

        cursor = mySQLiteAdapter.showDate(day + "-" + month + "-" + year);

        String[] from = new String[]{SQLiteAdapter.KEY_ID, SQLiteAdapter.KEY_CONTENT3, SQLiteAdapter.KEY_CONTENT2};
        int[] to = new int[]{R.id.id, R.id.text1, R.id.text2};
        cursorAdapter =
                new SimpleCursorAdapter(thisActivity, R.layout.row, cursor, from, to, 0);
        listContent.setAdapter(cursorAdapter);

        listContent.setOnItemClickListener(listContentOnItemClickListener);

        TextView emptyList = (TextView) findViewById(R.id.emptyList);
        if (listContent.getCount() != 0) {
            emptyList.setVisibility(View.INVISIBLE);
            updateView(listContent.getCount());
        } else {
            emptyList.setVisibility(View.VISIBLE);
        }

    }

    private void updateView(int index) {
        View v = listContent.getChildAt(index -
                listContent.getFirstVisiblePosition());

        if (v == null)
            return;

        TextView someText = (TextView) v.findViewById(R.id.id);
        Log.v("Ggg" , someText.getText().toString());

//        someText.setText("Hi! I updated you manually!");
    }

    private ListView.OnItemClickListener listContentOnItemClickListener
            = new ListView.OnItemClickListener() {

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position,
                                long id) {

            Log.v("<<< Position", Integer.toString(position));


            Cursor cursor = (Cursor) parent.getItemAtPosition(position);
            final int item_id = cursor.getInt(cursor.getColumnIndex(SQLiteAdapter.KEY_ID));
            final String item_content1 = cursor.getString(cursor.getColumnIndex(SQLiteAdapter.KEY_CONTENT1));
            final String item_content2 = cursor.getString(cursor.getColumnIndex(SQLiteAdapter.KEY_CONTENT2));
            final String item_content3 = cursor.getString(cursor.getColumnIndex(SQLiteAdapter.KEY_CONTENT3));
            final String item_content4 = cursor.getString(cursor.getColumnIndex(SQLiteAdapter.KEY_CONTENT4));

            AlertDialog.Builder myDialog
                    = new AlertDialog.Builder(Picker.this);

            myDialog.setTitle(item_content2);

            TextView dialogTxt_id = new TextView(Picker.this);
            LayoutParams dialogTxt_idLayoutParams
                    = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
            dialogTxt_id.setLayoutParams(dialogTxt_idLayoutParams);
            dialogTxt_id.setText("When: " + day+ "-" + Months[Integer.parseInt(month)] + "-" + year);

            TextView dialogC1_id = new TextView(Picker.this);
            LayoutParams dialogC1_idLayoutParams
                    = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
            dialogC1_id.setLayoutParams(dialogC1_idLayoutParams);
            dialogC1_id.setText("Time: " + String.valueOf(item_content3));

            TextView dialogC2_id = new TextView(Picker.this);
            LayoutParams dialogC2_idLayoutParams
                    = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
            dialogC2_id.setLayoutParams(dialogC2_idLayoutParams);
            dialogC2_id.setText("Details: " + String.valueOf(item_content4));

            LinearLayout layout = new LinearLayout(Picker.this);
            layout.setOrientation(LinearLayout.VERTICAL);
            layout.addView(dialogTxt_id);
            layout.addView(dialogC1_id);
            layout.addView(dialogC2_id);
            myDialog.setView(layout);

            myDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                // do something when the button is clicked
                public void onClick(DialogInterface arg0, int arg1) {

                }
            });

            myDialog.setPositiveButton("Edit", new DialogInterface.OnClickListener() {
                // do something when the button is clicked
                public void onClick(DialogInterface arg0, int arg1) {

                    String date = day + ";;;" + month + ";;;" + year + ";;;" + item_content2 + ";;;" + item_content3 + ";;;" + item_content4 + ";;;" + item_id;
                    Log.v("new appt straight", date);

                    thisActivity.finish();
                    createIntent(date, "home.eduard.calendarappandroid.ViewEditAppointment");
                }
            });

            myDialog.show();

        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mySQLiteAdapter.close();
    }


    private void updateList() {
        cursor = mySQLiteAdapter.resetCursor();
        cursorAdapter.swapCursor(cursor);
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