package home.eduard.calendarappandroid;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.LinkedList;
import java.util.List;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import android.util.Log;
import android.util.Xml;

public class SuggestTask implements Runnable {
    private static final String TAG = "SuggestTask";
    private final NewAct suggest;
    private final String original;

    SuggestTask(NewAct context, String original) {
        this.suggest = context;
        this.original = original;
    }

    public void run() {
        // Get suggestions for the original text
        List<String> suggestions = doSuggest(original);
        suggest.setSuggestions(suggestions);
    }

    private List<String> doSuggest(String original) {
        List<String> messages = new LinkedList<String>();
        String error = null;
        HttpURLConnection con = null;
        Log.d(TAG, "doSuggest(" + original + ")");

        try {
            // Check if task has been interrupted
            if (Thread.interrupted())
                throw new InterruptedException();

            // Build RESTful query for Thesaurus API
            String q = URLEncoder.encode(original, "UTF-8");

            URL url = new URL(
                    "http://thesaurus.altervista.org/thesaurus/v1?word=" + q + "&language=en_US&key=pBnxuiTWACxzPqMYWoxy&output=xml");
            con = (HttpURLConnection) url.openConnection();
            con.setReadTimeout(10000 /* milliseconds */);
            con.setConnectTimeout(15000 /* milliseconds */);
            con.setRequestMethod("GET");
            con.setDoInput(true);

            // Start the query
            con.connect();

            // Check if task has been interrupted
            if (Thread.interrupted())
                throw new InterruptedException();

            // Read results from the query
            XmlPullParser parser = Xml.newPullParser();
            parser.setInput(con.getInputStream(), null);
            int eventType = parser.getEventType();
            while (eventType != XmlPullParser.END_DOCUMENT) {
                if (eventType == XmlPullParser.TEXT) {
                    String text = parser.getText();
                    if (!(text.contains("noun"))) {
                        String toAdd = text;
                        toAdd = toAdd.replaceAll("[\n\r\\s]", "");
                        String[] str1 = toAdd.split("[|]");
                        for (int i = 0; i < str1.length; i++)
                            if (!str1[i].contains("antonym") && !str1[i].contains("verb") && !str1[i].contains("adj") && !str1[i].contains("adv") && str1[i].length() > 0)
                                messages.add(str1[i]);
                    }
                }
                eventType = parser.next();
            }

            // Check if task has been interrupted
            if (Thread.interrupted())
                throw new InterruptedException();

        } catch (IOException e) {
            Log.e(TAG, "IOException", e);
            error = suggest.getResources().getString(R.string.error)
                    + " " + e.toString();
        } catch (XmlPullParserException e) {
            Log.e(TAG, "XmlPullParserException", e);
            error = suggest.getResources().getString(R.string.error)
                    + " " + e.toString();
        } catch (InterruptedException e) {
            Log.d(TAG, "InterruptedException", e);
            error = suggest.getResources().getString(
                    R.string.interrupted);
        } finally {
            if (con != null) {
                con.disconnect();
            }
        }

        // If there was an error, return the error by itself
        if (error != null) {
            messages.clear();
            messages.add(error);
        }

        // Print something if we got nothing
        if (messages.size() == 0) {
            messages.add(suggest.getResources().getString(
                    R.string.no_results));
        }

        // All done
        Log.d(TAG, "   -> returned " + messages);
        return messages;
    }

}