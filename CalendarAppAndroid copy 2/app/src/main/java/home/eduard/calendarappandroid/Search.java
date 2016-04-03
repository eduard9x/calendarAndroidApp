package home.eduard.calendarappandroid;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

import java.util.List;

public class Search extends Activity {

    EditText searchField;
    Button buttonSearch;

    private SQLiteAdapter mySQLiteAdapter;
    ListView listContent;

    SimpleCursorAdapter cursorAdapter;
    Cursor cursor;
    List<String> results_array_list;

    Activity thisActivity = this;
    private String day, month, year;
    final String[] Months = {"January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December"};


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.search);

        searchField = (EditText) findViewById(R.id.searchField);
        buttonSearch = (Button) findViewById(R.id.searchButtonDatabase);

        listContent = (ListView) findViewById(R.id.searchList);

        mySQLiteAdapter = new SQLiteAdapter(this);
        mySQLiteAdapter.openToWrite();

        buttonSearchOnClickListener.onClick(buttonSearch);

        listContent.setOnItemClickListener(listContentOnItemClickListener);
        buttonSearch.setOnClickListener(buttonSearchOnClickListener);

        mySQLiteAdapter.close();
    }

    Button.OnClickListener buttonSearchOnClickListener
            = new Button.OnClickListener() {

        @Override
        public void onClick(View arg0) {
            String toSearch = searchField.getText().toString();

            results_array_list = mySQLiteAdapter.searchTitle(toSearch);

            ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(
                    Search.this,
                    R.layout.row,
                    R.id.id,
                    results_array_list);

            listContent.setAdapter(arrayAdapter);
        }
    };

    private ListView.OnItemClickListener listContentOnItemClickListener
            = new ListView.OnItemClickListener() {

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position,
                                long id) {

            Log.v("RESULTS:", results_array_list.get(position));

            String selected = results_array_list.get(position);
            String[] splitSelected = selected.split(" >> ");
            String date, title;

            date = splitSelected[0];
            title = splitSelected[1];

            String[] breakDownDate = date.split("-");
            day = breakDownDate[0];

            for (int i = 0; i < Months.length; i++)
                if (Months[i].equals(breakDownDate[1])) {
                    month = Integer.toString(i);
                    break;
                }

            year = breakDownDate[2];

            Log.v("RESULTS2:", day+"-"+Months[Integer.parseInt(month)]+"-"+year + " >> " + title);





        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
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