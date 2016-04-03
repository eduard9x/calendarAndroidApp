package org.example.suggest;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.RejectedExecutionException;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class MainActivity extends Activity {
    private EditText origText;
    private Handler guiThread;
    private ExecutorService suggThread;
    private Runnable updateTask;
    private Future<?> suggPending;
    private Button goButton;
    private TextView chosenOne;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.suggest_layout);
        initThreading();
        findViews();
        setListeners();
    }

    @Override
    protected void onDestroy() {
        // Terminate extra threads here
        suggThread.shutdownNow();
        super.onDestroy();
    }

    /**
     * Get a handle to all user interface elements
     */
    private void findViews() {
        origText = (EditText) findViewById(R.id.original_text);
        goButton = (Button) findViewById(R.id.go_button);
        chosenOne = (TextView) findViewById(R.id.chosen_one);
    }

    /**
     * Setup user interface event handlers
     */
    private void setListeners() {

        Button.OnClickListener goButtonClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                queueUpdate(1000 /* milliseconds */);
            }
        };

        goButton.setOnClickListener(goButtonClickListener);
    }

    /**
     * Initialize multi-threading. There are two threads: 1) The main
     * graphical user interface thread already started by Android,
     * and 2) The suggest thread, which we start using an executor.
     */
    private void initThreading() {
        guiThread = new Handler();
        suggThread = Executors.newSingleThreadExecutor();

        // This task gets suggestions and updates the screen
        updateTask = new Runnable() {
            public void run() {
                // Get text to suggest
                String original = origText.getText().toString().trim();

                // Cancel previous suggestion if there was one
                if (suggPending != null)
                    suggPending.cancel(true);

                // Check to make sure there is text to work on
                if (original.length() != 0) {
                    // Let user know we're doing something
                    Log.v("Message", getResources().getString(R.string.working));

                    // Begin suggestion now but don't wait for it
                    try {
                        SuggestTask suggestTask = new SuggestTask(
                                MainActivity.this, // reference to activity
                                original // original text
                        );
                        suggPending = suggThread.submit(suggestTask);
                    } catch (RejectedExecutionException e) {
                        // Unable to start new task
                        Log.v("Message", getResources().getString(R.string.error));

                    }
                }
            }
        };
    }

    /**
     * Request an update to start after a short delay
     */
    private void queueUpdate(long delayMillis) {
        // Cancel previous update if it hasn't started yet
        guiThread.removeCallbacks(updateTask);
        // Start an update if nothing happens after a few milliseconds
        guiThread.postDelayed(updateTask, delayMillis);
    }

    /**
     * Modify list on the screen (called from another thread)
     */
    public void setSuggestions(List<String> suggestions) {
        guiSetList(suggestions);
    }

    /**
     * All changes to the GUI must be done in the GUI thread
     */
    private void guiSetList(final List<String> list) {
        guiThread.post(new Runnable() {
            public void run() {
                createDialog(list);
            }

        });
    }

    private void createDialog(List<String> list) {
        AlertDialog.Builder builderSingle = new AlertDialog.Builder(MainActivity.this);
        builderSingle.setTitle("Please select one of the following synonyms:");

        final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(MainActivity.this,
                android.R.layout.select_dialog_singlechoice, list);

        builderSingle.setNegativeButton(
                "cancel",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

        builderSingle.setAdapter(
                arrayAdapter,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String strName = arrayAdapter.getItem(which);
                        chosenOne.setText(strName);
                        dialog.dismiss();
                    }
                });
        builderSingle.show();

    }

}
