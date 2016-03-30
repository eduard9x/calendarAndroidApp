package home.eduard.calendarappandroid;

import android.net.Uri;
import android.provider.BaseColumns;

public interface Constants extends BaseColumns {
    public static final String TABLE_NAME = "appointmentt";

    public static final String AUTHORITY = "home.eduard.calendarappandroid";
    public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/" + TABLE_NAME);

    // Columns in the MainActivity database
    public static final String TIME = "time";
    public static final String TITLE = "title";
    public static final String DETAILS = "details";

}
