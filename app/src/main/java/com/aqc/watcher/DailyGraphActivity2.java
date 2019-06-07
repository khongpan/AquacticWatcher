package com.aqc.watcher;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;

public class DailyGraphActivity2 extends AppCompatActivity
        implements DailyGraphFragment2.OnFragmentInteractionListener,
        DateNavigateFragment.OnDateNavigateFragmentInteractionListener

{

    private String mBaseUrl;
    private int mSelectedPond;
    private String mCurrentDate;
    private String mGraphGroup;

    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    // private GoogleApiClient client;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_daily_graph);

        Intent intent = getIntent();
        int plots_num = intent.getIntExtra("plots_num", 0);
        String[] Urls = new String[plots_num];

        Urls[0] = intent.getStringExtra("url0");

        // However, if we're being restored from a previous state,
        // then we don't need to do anything and should return or else
        // we could end up with overlapping fragments.
        if (savedInstanceState != null) {
            return;
        }

        // Check that the activity is using the layout version with
        // the fragment_container FrameLayout
        if (findViewById(R.id.frDailyGraph) != null) {

            // Create a new Fragment to be placed in the activity layout
            Fragment dailyGraphFragment;

            dailyGraphFragment = new DailyGraphFragment2();


            // In case this activity was started with special instructions from an
            // Intent, pass the Intent's extras to the fragment as arguments


            dailyGraphFragment.setArguments(getIntent().getExtras());

            // Add the fragment to the 'fragment_container' FrameLayout
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.frDailyGraph, dailyGraphFragment).commit();
        }
        if (findViewById(R.id.frameDateNavigate) != null) {

            // Create a new Fragment to be placed in the activity layout
            DateNavigateFragment dateNavigateFragment = new DateNavigateFragment();

            // In case this activity was started with special instructions from an
            // Intent, pass the Intent's extras to the fragment as arguments
            dateNavigateFragment.setArguments(getIntent().getExtras());

            // Add the fragment to the 'fragment_container' FrameLayout
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.frameDateNavigate, dateNavigateFragment).commit();
        }
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        //client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    @Override
    public void onDateNavigateFragmentInteraction(String date) {

        mCurrentDate = date;


            DailyGraphFragment2 dailyGraphFragment = (DailyGraphFragment2)
                    getSupportFragmentManager().findFragmentById(R.id.frDailyGraph);

            if (dailyGraphFragment != null) {


                // If article frag is available, we're in two-pane layout...
                // Call a method in the ArticleFragment to update its content
                dailyGraphFragment.setDate(mCurrentDate);
                //dailyGraphFragment.setGraphGroup(mGraphGroup);
                dailyGraphFragment.updateGraph();
            }
    }


    @Override
    public void onStart() {
        super.onStart();
        /*
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.connect();
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "DailyGraph Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app deep link URI is correct.
                Uri.parse("android-app://com.smn.cpfmaeklong/http/host/path")
        );
        AppIndex.AppIndexApi.start(client, viewAction);
        */
    }

    @Override
    public void onStop() {
        super.onStop();
        /*

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "DailyGraph Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app deep link URI is correct.
                Uri.parse("android-app://com.smn.cpfmaeklong/http/host/path")
        );
        AppIndex.AppIndexApi.end(client, viewAction);
        client.disconnect();
        */
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        // Save UI state changes to the savedInstanceState.
        // This bundle will be passed to onCreate if the process is
        // killed and restarted.
        savedInstanceState.putBoolean("MyBoolean", true);
        savedInstanceState.putDouble("myDouble", 1.9);
        savedInstanceState.putInt("MyInt", 1);
        savedInstanceState.putString("MyString", "Welcome back to Android");
        //savedInstanceState.putInt("SelectedPond", SelectedPond);
        // etc.
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        // Restore UI state from the savedInstanceState.
        // This bundle has also been passed to onCreate.
        boolean myBoolean = savedInstanceState.getBoolean("MyBoolean");
        double myDouble = savedInstanceState.getDouble("myDouble");
        int myInt = savedInstanceState.getInt("MyInt");
        String myString = savedInstanceState.getString("MyString");
        //SelectedPond = savedInstanceState.getInt("SelectedPond");
        //if (SelectedPond>=BaseURL.length)
        //    SelectedPond=0;
    }
}
