package home.eduard.calendarappandroid;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

public class Delete extends Activity {

    EditText deleteSelection;
    Button buttonDeleteAll, deleteSelected;
    private String day, month, year;


    private SQLiteAdapter mySQLiteAdapter;
    ListView listContent;

    SimpleCursorAdapter cursorAdapter;
    Cursor cursor;

    Activity thisActivity = this;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.delete);

        String intentVal = getIntent().getStringExtra("DoNext");
        final String[] date = intentVal.split(";;;");

        day = date[0];
        month = date[1];
        year = date[2];

        deleteSelection = (EditText) findViewById(R.id.deleteSelection);
        deleteSelected = (Button) findViewById(R.id.deleteSelected);
        buttonDeleteAll = (Button) findViewById(R.id.deleteAll);

        listContent = (ListView) findViewById(R.id.deleteList);

        mySQLiteAdapter = new SQLiteAdapter(this);
        mySQLiteAdapter.openToWrite();

        cursor = mySQLiteAdapter.showDate(day + "-" + month + "-" + year);
        String[] from = new String[]{SQLiteAdapter.KEY_ID, SQLiteAdapter.KEY_CONTENT1, SQLiteAdapter.KEY_CONTENT2};
        int[] to = new int[]{R.id.id, R.id.text1, R.id.text2};
        cursorAdapter =
                new SimpleCursorAdapter(thisActivity, R.layout.row, cursor, from, to, 0);
        listContent.setAdapter(cursorAdapter);

        listContent.setOnItemClickListener(listContentOnItemClickListener);
        buttonDeleteAll.setOnClickListener(buttonDeleteAllOnClickListener);
        deleteSelected.setOnClickListener(buttonDeleteSelectedOnClickListener);

    }

    Button.OnClickListener buttonDeleteSelectedOnClickListener
            = new Button.OnClickListener() {

        @Override
        public void onClick(View arg0) {
            String text = deleteSelection.getText().toString();

            try {
                int number = Integer.parseInt(text);

                if (number > 0 && number < listContent.getCount())
                    listContentOnItemClickListener.onItemClick(listContent, null, number - 1, 203);
                else throw new Exception();
            } catch (Exception ex) {
                Log.v("Exception delete:", ex.toString());

                AlertDialog.Builder myDialog
                        = new AlertDialog.Builder(Delete.this);
                myDialog.setTitle("Error position.");
                myDialog.setMessage("The " + text + " is not a valid position for an appointment to delete.");

                myDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    // do something when the button is clicked
                    public void onClick(DialogInterface arg0, int arg1) {
                        //doNothing
                    }
                });

                myDialog.show();
            }

        }

    };


    Button.OnClickListener buttonDeleteAllOnClickListener
            = new Button.OnClickListener() {

        @Override
        public void onClick(View arg0) {

            AlertDialog.Builder myDialog
                    = new AlertDialog.Builder(Delete.this);
            myDialog.setTitle("Delete all");
            myDialog.setMessage("Are you sure you want to delete all the appointments?");

            myDialog.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                // do something when the button is clicked
                public void onClick(DialogInterface arg0, int arg1) {

                    mySQLiteAdapter.deleteAll(day + "-" + month + "-" + year);
                    updateList();
                }
            });

            myDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                // do something when the button is clicked
                public void onClick(DialogInterface arg0, int arg1) {
                    //doNothing
                }
            });

            myDialog.show();


        }

    };

    private ListView.OnItemClickListener listContentOnItemClickListener
            = new ListView.OnItemClickListener() {

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position,
                                long id) {


            Log.v("long ", Long.toString(id));


            Cursor cursor = (Cursor) parent.getItemAtPosition(position);
            final int item_id = cursor.getInt(cursor.getColumnIndex(SQLiteAdapter.KEY_ID));
            final String item_content1 = cursor.getString(cursor.getColumnIndex(SQLiteAdapter.KEY_CONTENT1));
            final String item_content2 = cursor.getString(cursor.getColumnIndex(SQLiteAdapter.KEY_CONTENT2));
            final String item_content3 = cursor.getString(cursor.getColumnIndex(SQLiteAdapter.KEY_CONTENT3));
            final String item_content4 = cursor.getString(cursor.getColumnIndex(SQLiteAdapter.KEY_CONTENT4));

            AlertDialog.Builder myDialog
                    = new AlertDialog.Builder(Delete.this);

            myDialog.setTitle(item_content2);

            TextView dialogTxt_id = new TextView(Delete.this);
            LayoutParams dialogTxt_idLayoutParams
                    = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
            dialogTxt_id.setLayoutParams(dialogTxt_idLayoutParams);
            dialogTxt_id.setText("When: " + String.valueOf(item_content1));

            TextView dialogC1_id = new TextView(Delete.this);
            LayoutParams dialogC1_idLayoutParams
                    = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
            dialogC1_id.setLayoutParams(dialogC1_idLayoutParams);
            dialogC1_id.setText("Time: " + String.valueOf(item_content3));

            TextView dialogC2_id = new TextView(Delete.this);
            LayoutParams dialogC2_idLayoutParams
                    = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
            dialogC2_id.setLayoutParams(dialogC2_idLayoutParams);
            dialogC2_id.setText("Details: " + String.valueOf(item_content4));

            LinearLayout layout = new LinearLayout(Delete.this);
            layout.setOrientation(LinearLayout.VERTICAL);
            layout.addView(dialogTxt_id);
            layout.addView(dialogC1_id);
            layout.addView(dialogC2_id);
            myDialog.setView(layout);


            myDialog.setPositiveButton("DELETE", new DialogInterface.OnClickListener() {
                // do something when the button is clicked
                public void onClick(DialogInterface arg0, int arg1) {
                    mySQLiteAdapter.delete_byID(item_id);
                    updateList();
                }
            });

            myDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                // do something when the button is clicked
                public void onClick(DialogInterface arg0, int arg1) {

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


}