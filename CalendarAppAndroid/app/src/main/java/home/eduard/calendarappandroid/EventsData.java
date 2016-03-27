package home.eduard.calendarappandroid;

import static android.provider.BaseColumns._ID;
import static home.eduard.calendarappandroid.Constants.DETAILS;
import static home.eduard.calendarappandroid.Constants.TIME;
import static home.eduard.calendarappandroid.Constants.TITLE;
import static home.eduard.calendarappandroid.Constants.TABLE_NAME;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class EventsData extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "appointmentt.db";
    private static final int DATABASE_VERSION = 1;

    /** Create a helper object for the MainActivity database */
    public EventsData(Context ctx) {
        super(ctx, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + TABLE_NAME + " (" + _ID
                + " INTEGER PRIMARY KEY AUTOINCREMENT, " + TIME
                + " INTEGER," + TITLE + " TEXT NOT NULL, " + DETAILS + " TEXT);");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion,
                          int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }
}