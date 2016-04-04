package home.eduard.calendarappandroid;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.CalendarView;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

public class Move extends Activity {

    private SQLiteAdapter mySQLiteAdapter;
    ListView listContent;

    SimpleCursorAdapter cursorAdapter;
    Cursor cursor;

    Activity thisActivity = this;
    private String day, month, year;
    int _id;
    String data1,data2,data3,data4;

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

        Log.d("sql", SQLiteAdapter.KEY_CONTENT3.toString());

        String[] from = new String[]{SQLiteAdapter.KEY_ID, SQLiteAdapter.KEY_CONTENT3, SQLiteAdapter.KEY_CONTENT2, SQLiteAdapter.KEY_CONTENT4};
        int[] to = new int[]{R.id.selection_number, R.id.id, R.id.text1, R.id.text2};
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
        mySQLiteAdapter.close();

        cursorAdapter.setViewBinder(new SimpleCursorAdapter.ViewBinder() {

            public boolean setViewValue(View aView, Cursor aCursor, int aColumnIndex) {
                if (aColumnIndex == 0) {
                    TextView textView = (TextView) aView;
                    int CursorPos = aCursor.getPosition() + 1;
                    textView.setText(Integer.toString(CursorPos));

                    return true;
                }

                return false;
            }
        });
    }

    private void updateView(int index) {
        View v = listContent.getChildAt(index -
                listContent.getFirstVisiblePosition());

        if (v == null)
            return;

        TextView someText = (TextView) v.findViewById(R.id.id);
        Log.v("Ggg", someText.getText().toString());

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

            Log.v("<<< ID", Integer.toString(item_id));

            _id = item_id;
            data1 = item_content1;
            data2 = item_content2;
            data3 = item_content3;
            data4 = item_content4;


            DatePickerDialog myDialog = new DatePickerDialog(Move.this,
                    myDialogListener,
                    Integer.parseInt(year),
                    Integer.parseInt(month),
                    Integer.parseInt(day));

            myDialog.show();
        }
    };

    public DatePickerDialog.OnDateSetListener myDialogListener = new DatePickerDialog.OnDateSetListener() {

        public void onDateSet(DatePicker view, int selectedYear, int selectedMonth, int selectedDay) {

            data1 = selectedDay + "-" + selectedMonth + "-" + selectedYear;
            Log.v("Date selected: ", data1);

            mySQLiteAdapter.openToWrite();
            mySQLiteAdapter.update(Integer.toString(_id), data1, data2, data3, data4);
            updateList();
            if(listContent.getCount()==0) thisActivity.finish();
            mySQLiteAdapter.close();
            Toast.makeText(Move.this, "Appointment has been moved.", Toast.LENGTH_SHORT).show();
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    private void updateList() {
        cursor = mySQLiteAdapter.showDate(day + "-" + month + "-" + year);
        cursorAdapter.swapCursor(cursor);
    }

}