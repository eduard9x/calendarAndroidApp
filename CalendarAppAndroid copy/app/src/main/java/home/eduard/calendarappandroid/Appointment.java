package home.eduard.calendarappandroid;

import android.app.ListActivity;
import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import static android.provider.BaseColumns._ID;
import static home.eduard.calendarappandroid.Constants.TIME;
import static home.eduard.calendarappandroid.Constants.TITLE;
import static home.eduard.calendarappandroid.Constants.CONTENT_URI;
import static home.eduard.calendarappandroid.Constants.DETAILS;


public class Appointment extends ListActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private static String[] FROM = { _ID, TIME, TITLE, DETAILS};
    private static int[] TO = { R.id.rowid, R.id.time, R.id.title, R.id.details};
    private static String ORDER_BY = TIME + " DESC";

    // The loader's unique id (within this activity)
    private final static int LOADER_ID = 1;

    // The adapter that binds our data to the ListView
    private SimpleCursorAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.appointment);

        String intentVal = getIntent().getStringExtra("DoNext");
        Log.v("intent: ---> ", intentVal);

        // Initialize the adapter. It starts off empty.
        mAdapter = new SimpleCursorAdapter(this, R.layout.item, null, FROM, TO, 0);

        // Associate the adapter with the ListView
        setListAdapter(mAdapter);

        // Initialize the loader
        LoaderManager lm = getLoaderManager();
        lm.initLoader(LOADER_ID, null, this);

        ListView lv = (ListView) findViewById(android.R.id.list);
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.v(" ITEM ", " TO BE OPENED/DELETED " + " " + id);

                deleteEvent(CONTENT_URI, "_ID=?", new String[]{Long.toString(id)});
            }
        });

        Button newButton = (Button) findViewById(R.id.button2);
        newButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addEvent("Hello, Android!");
            }
        });

    }


    private void addEvent(String string) {
        // Insert a new record into the Events data source.
        // You would do something similar for delete and update.
        ContentValues values = new ContentValues();
        values.put(TIME, System.currentTimeMillis());
        values.put(TITLE, string);
        values.put(DETAILS, "I will have to meet with MR.Wood to discuss some business plans. I will need to give him some moneey. Looots and loooots of monaaeeyyy.");
        getContentResolver().insert(CONTENT_URI, values);
    }

    private void deleteEvent(Uri uri, String selection, String[] selectionArgs) {
        getContentResolver().delete(uri, selection, selectionArgs);

    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        // Create a new CursorLoader
        return new CursorLoader(this, CONTENT_URI, FROM, null, null, ORDER_BY);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        switch (loader.getId()) {
            case LOADER_ID:
                // The data is now available to use
                mAdapter.swapCursor(cursor);
                break;
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        // The loader's data is unavailable
        mAdapter.swapCursor(null);
    }
}
