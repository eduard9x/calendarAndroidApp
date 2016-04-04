package home.eduard.calendarappandroid;

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
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class NewAct extends Activity {
    private EditText origText;
    private Handler guiThread;
    private ExecutorService suggThread;
    private Runnable updateTask;
    private Future<?> suggPending;
    private Button synonymButton, highlightButton;
    private SQLiteAdapter mySQLiteAdapter;
    private String day, month, year, title, time, details, _id;
    private boolean update;
    private String NEWAPPT = "NewAppointment";
    private final int HIGHLIGHT = 1;
    private final int SYNONYM = 2;
    private int option;
    private EditText titleEditText, timeEditText, detailsEditText;
    private TextView errorLabel;
    private String selection, original;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.create_appointment);
        initThreading();
        findViews();
        setListeners();

        doRest();

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
        synonymButton = (Button) findViewById(R.id.synonym_button);
        titleEditText = (EditText) findViewById(R.id.titleEditText);
        timeEditText = (EditText) findViewById(R.id.timeEditText);
        detailsEditText = (EditText) findViewById(R.id.detailsEditText);
        errorLabel = (TextView) findViewById(R.id.errorLabel);
        highlightButton = (Button) findViewById(R.id.highlight_button);
    }

    /**
     * Setup user interface event handlers
     */
    private void setListeners() {
        synonymButton.setOnClickListener(goButtonClickListener);
    }

    private Button.OnClickListener goButtonClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            option = SYNONYM;
            queueUpdate(1000 /* milliseconds */);
        }
    };

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
                switch (option) {
                    case HIGHLIGHT:
                        original = selection;
                        break;
                    default:
                        original = origText.getText().toString().trim();
                        break;
                }

                Log.d("Original", original);

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
                                NewAct.this, // reference to activity
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
        AlertDialog.Builder builderSingle = new AlertDialog.Builder(NewAct.this);
        builderSingle.setTitle("Please select one of the following synonyms:");

        final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(NewAct.this,
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

                        if(strName.contains("(similarterm)") || strName.contains("(relatedterm)")){
                            String[] splitString = strName.split("[(]");
                            strName = splitString[0];
                        }

                        switch (option) {
                            case HIGHLIGHT: {
                                String allText = detailsEditText.getText().toString();
                                String[] pieces = allText.split(" ");
                                allText = "";
                                for (int i = 0; i < pieces.length; i++) {
                                    if (pieces[i].equals(original)) pieces[i] = strName;
                                    allText += pieces[i] + " ";
                                }
                                detailsEditText.setText(allText);
                            }
                            break;
                            default:
                                origText.setText(strName);
                                break;
                        }

                        dialog.dismiss();
                    }
                });
        builderSingle.show();

    }

    public void hideKeyboard(View view) {
        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    public void doRest() {
        update = false;
        mySQLiteAdapter = new SQLiteAdapter(this);

        final Activity thisActivity = this;

        final String[] Months = {"January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December"};

        String intentVal = getIntent().getStringExtra("DoNext");

        final String[] data = intentVal.split(";;;");

        day = data[0];
        month = data[1];
        year = data[2];

        if (data.length == 7) {

            update = true;

            title = data[3];
            time = data[4];
            details = data[5];
            _id = data[6];

            titleEditText.setText(title);
            timeEditText.setText(time);
            detailsEditText.setText(details);
        }


        TextView dateLabel = (TextView) findViewById(R.id.dateLabel);
        dateLabel.setText(day + " " + Months[Integer.parseInt(month)] + " " + year);

        Button SaveButton = (Button) findViewById(R.id.saveButton);

        highlightButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("create", "onCreateActionMode");

                int startSelection = detailsEditText.getSelectionStart();
                int endSelection = detailsEditText.getSelectionEnd();

                String selectedText = detailsEditText.getText().toString().substring(startSelection, endSelection);
                Log.d("selected", selectedText);

                selection = selectedText;
                option = HIGHLIGHT;

                queueUpdate(1000 /* milliseconds */);
            }
        });


        titleEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    hideKeyboard(v);
                }
            }
        });

        detailsEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    hideKeyboard(v);
                }
            }
        });

        timeEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    hideKeyboard(v);
                }
            }
        });

        SaveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.v("Save button: >>> ", " clicked ");

                hideKeyboard(view);

                String title = titleEditText.getText().toString();
                String time = timeEditText.getText().toString();
                String details = detailsEditText.getText().toString();
                int hours = -1, minutes = -1;
                boolean timeValid = false;

                try {
                    String[] validator = time.split(":");

                    if (validator.length != 2)
                        throw new Exception("Please input both hours and minutes.");
                    else {
                        hours = Integer.parseInt(validator[0]);
                        minutes = Integer.parseInt(validator[1]);
                    }

                    if (hours >= 0 && minutes >= 0)
                        if (hours <= 23 && minutes <= 59)
                            timeValid = true;

                } catch (Exception ex) {
                    Log.v("<<< Exception time: ", ex.toString());
                }

                boolean allowTitle;
                if (update)
                    allowTitle = mySQLiteAdapter.allowTitle(title, day + "-" + month + "-" + year, _id);
                else
                    allowTitle = mySQLiteAdapter.allowTitle(title, day + "-" + month + "-" + year, NEWAPPT);

                Log.v("<<< allow Title: ", Boolean.toString(allowTitle));

                if (!allowTitle) {

                    AlertDialog.Builder myDialog
                            = new AlertDialog.Builder(NewAct.this);
                    myDialog.setTitle("Appointment already exists.");
                    myDialog.setMessage("Appointment " + title + " already exists, please choose a different event title.");

                    myDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        // do something when the button is clicked
                        public void onClick(DialogInterface arg0, int arg1) {
                            //doNothing
                        }
                    });

                    myDialog.show();
                } else if (title.equals("")) {
                    errorLabel.setText("Please enter a valid " + getResources().getString(R.string.titleLabel));
                } else if (!timeValid) {
                    errorLabel.setText("Please enter a valid " + getResources().getString(R.string.timeLabel));
                } else {

                    mySQLiteAdapter.openToWrite();

                    String data1 = day + "-" + month + "-" + year;
                    String data2 = titleEditText.getText().toString();
                    String data3 = timeEditText.getText().toString();
                    String data4 = detailsEditText.getText().toString();

                    String[] timeToCheck = data3.split(":");
                    int[] timeIntegers = {Integer.parseInt(timeToCheck[0]), Integer.parseInt(timeToCheck[1])};

                    for (int i = 0; i < timeToCheck.length; i++)
                        if (timeIntegers[i] < 10)
                            timeToCheck[i] = "0" + Integer.toString(timeIntegers[i]);

                    data3 = timeToCheck[0] + ":" + timeToCheck[1];

                    if (update) {
                        Log.v("<<< UPDATE DB: ", Boolean.toString(update));
                        mySQLiteAdapter.update(_id, data1, data2, data3, data4);
                    } else {
                        Log.v("<<< INSERT DB: ", Boolean.toString(!update));
                        mySQLiteAdapter.insert(data1, data2, data3, data4);
                    }
                    mySQLiteAdapter.close();

                    thisActivity.finish();
                    Toast.makeText(NewAct.this, "Saved", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

}
