package home.eduard.calendarappandroid;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.util.Log;

import java.util.ArrayList;

public class SQLiteAdapter {

    public static final String MYDATABASE_NAME = "MY_DATABASE2";
    public static final String MYDATABASE_TABLE = "MY_TABLE2";
    public static final int MYDATABASE_VERSION = 1;
    public static final String KEY_ID = "_id";
    public static final String KEY_CONTENT1 = "Date";
    public static final String KEY_CONTENT2 = "Title";
    public static final String KEY_CONTENT3 = "Time";
    public static final String KEY_CONTENT4 = "Details";
    private String NEWAPPT = "NewAppointment";


    //create table MY_DATABASE (ID integer primary key, Content text not null);
    private static final String SCRIPT_CREATE_DATABASE =
            "create table " + MYDATABASE_TABLE + " ("
                    + KEY_ID + " integer primary key autoincrement, "
                    + KEY_CONTENT1 + " text not null, "
                    + KEY_CONTENT2 + " text not null, "
                    + KEY_CONTENT3 + " text not null, "
                    + KEY_CONTENT4 + " text );";

    private SQLiteHelper sqLiteHelper;
    private SQLiteDatabase sqLiteDatabase;

    private Context context;

    public SQLiteAdapter(Context c) {
        context = c;
    }

    public SQLiteAdapter openToRead() throws android.database.SQLException {
        sqLiteHelper = new SQLiteHelper(context, MYDATABASE_NAME, null, MYDATABASE_VERSION);
        sqLiteDatabase = sqLiteHelper.getReadableDatabase();
        return this;
    }

    public SQLiteAdapter openToWrite() throws android.database.SQLException {
        sqLiteHelper = new SQLiteHelper(context, MYDATABASE_NAME, null, MYDATABASE_VERSION);
        sqLiteDatabase = sqLiteHelper.getWritableDatabase();
        return this;
    }

    public void close() {
        sqLiteHelper.close();
    }

    public long insert(String content1, String content2, String content3, String content4) {

        ContentValues contentValues = new ContentValues();
        contentValues.put(KEY_CONTENT1, content1);
        contentValues.put(KEY_CONTENT2, content2);
        contentValues.put(KEY_CONTENT3, content3);
        contentValues.put(KEY_CONTENT4, content4);
        return sqLiteDatabase.insert(MYDATABASE_TABLE, null, contentValues);
    }

    public long update(String _id, String content1, String content2, String content3, String content4) {

        ContentValues contentValues = new ContentValues();
        contentValues.put(KEY_CONTENT1, content1);
        contentValues.put(KEY_CONTENT2, content2);
        contentValues.put(KEY_CONTENT3, content3);
        contentValues.put(KEY_CONTENT4, content4);
        return sqLiteDatabase.update(MYDATABASE_TABLE, contentValues, KEY_ID + " = ?", new String[]{_id});
    }

    public int deleteAll(String date) {
        return sqLiteDatabase.delete(MYDATABASE_TABLE, KEY_CONTENT1 + " = ?", new String[]{date});
    }

    public void delete_byID(int id) {
        sqLiteDatabase.delete(MYDATABASE_TABLE, KEY_ID + "=" + id, null);
    }

    public Cursor queueAll() {
        String[] columns = new String[]{KEY_ID, KEY_CONTENT1, KEY_CONTENT2, KEY_CONTENT3, KEY_CONTENT4};
        Cursor cursor = sqLiteDatabase.query(MYDATABASE_TABLE, columns,
                null, null, null, null, null);

        return cursor;
    }

    public Cursor resetCursor() {
        Cursor cursor = sqLiteDatabase.query(MYDATABASE_TABLE, null,
                null, null, null, null, null);

        return cursor;
    }

    public Cursor queueFew(int i) {
        String[] columns = new String[]{KEY_ID, KEY_CONTENT1, KEY_CONTENT2, KEY_CONTENT3, KEY_CONTENT4};
        Cursor cursor = sqLiteDatabase.query(MYDATABASE_TABLE, columns,
                KEY_ID + " < ?", new String[]{String.valueOf(i)}, null, null, null);

        return cursor;
    }

    public Cursor showDate(String date) {
        String[] columns = new String[]{KEY_ID, KEY_CONTENT1, KEY_CONTENT2, KEY_CONTENT3, KEY_CONTENT4};
        Cursor cursor = sqLiteDatabase.query(MYDATABASE_TABLE, columns,
                KEY_CONTENT1 + " = ?", new String[]{date}, null, null, KEY_CONTENT3 + " DESC");

        return cursor;
    }

    public boolean allowTitle(String title, String date, String _id) {

        openToRead();
        Cursor cursor = showDate(date);

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            if (title.equalsIgnoreCase(cursor.getString(cursor.getColumnIndex(KEY_CONTENT2))))
                if (_id == NEWAPPT) return false;
                else if (!(_id.equals(cursor.getString(cursor.getColumnIndex(KEY_ID))))) return false;

            cursor.moveToNext();
        }

        close();

        return true;
    }


    public class SQLiteHelper extends SQLiteOpenHelper {

        public SQLiteHelper(Context context, String name,
                            CursorFactory factory, int version) {
            super(context, name, factory, version);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL(SCRIPT_CREATE_DATABASE);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        }

    }

}
