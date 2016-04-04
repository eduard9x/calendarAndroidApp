package home.eduard.calendarappandroid;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import java.util.List;

public class Search extends Activity {

    EditText searchField;
    Button buttonSearch;

    private SQLiteAdapter mySQLiteAdapter;
    ListView listContent;

    List<String> results_array_list;

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

        buttonSearch.setOnClickListener(buttonSearchOnClickListener);

        mySQLiteAdapter.close();
    }

    Button.OnClickListener buttonSearchOnClickListener
            = new Button.OnClickListener() {

        @Override
        public void onClick(View arg0) {
            String toSearch = searchField.getText().toString();

            results_array_list = mySQLiteAdapter.searchForTitleAndDetails(toSearch);

            ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(
                    Search.this,
                    R.layout.search_results,
                    R.id.results,
                    results_array_list);

            listContent.setAdapter(arrayAdapter);
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

}