package com.aqc.watcher;


import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

public class MainActivity extends AppCompatActivity
        implements PondLayoutFragment.OnFragmentInteractionListener {

    final int PH_NUM = 4;
    final int DO_NUM = 4;
    final int T_NUM = 4;
    final int SENSOR_NUM = PH_NUM + DO_NUM + T_NUM;




    Spinner mSpnSitesList;
    //Button mBtnDoLevel;
    Button mBtnRefresh;
    //Button[] mBtnPh = new Button[PH_NUM];
    //Button[] mBtnDo = new Button[DO_NUM];
    Button[] mBtnSensor = new Button[SENSOR_NUM];
    //Button mBtnTemperature;
    TextView mTvDateTime;
    String strWebGetKey;
    List<String> mSitesList;
    static boolean mSitesListReady = false;
    private String[] strSitesName;
    int SelectedPond;
    private String[] strNetworkIDs;
    AgritronicsWebHelper AgWeb = AgritronicsWebHelper.getInstance();
    AppPreferences mPref;

    SensorInfoXML[] mXmlSensors = new SensorInfoXML[SENSOR_NUM];
    IOlistXML xmlIoList;
    boolean mUpdating=false;

    //username: aqc000
    // password: aqc000

    String[] BaseURL = {"http://agritronics.nstda.or.th/ws/get.php?appkey=0c5a295bd8c07a0809511130b0a3&p=AQUATIC-CONTROL-01",
            "http://agritronics.nstda.or.th/ws/get.php?appkey=0c5a295bd8c07a0809511130b0a3&p=AQUATIC-CONTROL-02",
            "http://agritronics.nstda.or.th/ws/get.php?appkey=0c5a295bd8c07a0809511130b0a3&p=AQUATIC-CONTROL-03",
            "http://agritronics.nstda.or.th/ws/get.php?appkey=0c5a295bd8c07a0809511130b0a3&p=AQUATIC-CONTROL-04",
            "http://agritronics.nstda.or.th/ws/get.php?appkey=0c5a295bd8c07a080d5306&p=AQUATIC-CONTROL-05",
            "http://agritronics.nstda.or.th/ws/get.php?appkey=0c5a295bd8c07a080b450069e3f2&p=PAN-01",
            "http://agritronics.nstda.or.th/ws/get.php?appkey=0c5a295bd8c07a080b450069e3f2&p=TEST-POND-CONTROL-1",
            "http://agritronics.nstda.or.th/ws/get.php?appkey=0c5a295bd8c07a080b450069e3f2&p=TEST-POND-CONTROL-2",
            "http://agritronics.nstda.or.th/ws/get.php?appkey=0c5a295bd8c07a080b450069e3f2&p=TEST-POND-CONTROL-3",
            "http://agritronics.nstda.or.th/ws/get.php?appkey=0c5a295bd8c07a080b450069e3f2&p=TEST-POND-CONTROL-4",
            "http://agritronics.nstda.or.th/ws/get.php?appkey=0c5a295bd8c07a080b450069e3f2&p=AQUATIC-CONTROL",};
    private ArrayAdapter<String> mSitesListAdapter;
    //"http://203.185.131.92/ws/get.php?appkey=0c5a295bd8c07a080b450069e3f2&p=AQUATIC-CONTROL",};


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        StartApp();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            //Toast.makeText(this, "setting", Toast.LENGTH_LONG).show();
            launchSetting();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void StartApp() {
        mPref = AppPreferences.getInstance(this);
        mTvDateTime = (TextView) findViewById(R.id.txtDate);
        mBtnRefresh = (Button) findViewById(R.id.btnRefresh);

        mSitesListReady=false;
        mSpnSitesList = findViewById(R.id.spnPond);
        strSitesName = getResources().getStringArray(R.array.type);
        mSitesList = new ArrayList<>(Arrays.asList(strSitesName));

        ArrayList list = mPref.getStringArrayPref("SitesList");
        if (list != null) mSitesList = list;

        //ArrayAdapter<String> objAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, mSitesList);
        mSitesListAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, mSitesList);
        mSpnSitesList.setAdapter(mSitesListAdapter);

        SharedPreferences sp = getSharedPreferences("PondWatcher", MODE_PRIVATE);
        int selected_pond = sp.getInt("SelectedPond", 1);
        strWebGetKey = sp.getString("WebGetKey", "no_key");
        if (selected_pond >= mSitesList.size()) selected_pond = 0;
        mSpnSitesList.setSelection(selected_pond);
        //spnSites.setSelection(objAdapter.getPosition("AQUA DEMO1"));
        mSpnSitesList.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
                int i;
                SharedPreferences sp = getSharedPreferences("PondWatcher", MODE_PRIVATE);
                SharedPreferences.Editor sp_editor = sp.edit();
                sp_editor.putInt("SelectedPond", pos);
                sp_editor.apply();
                SelectedPond = pos;
                updateView();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                //SelectedPond=0;
            }
        });

        mBtnRefresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateView();
            }
        });
        int i = 0;
        mBtnSensor[i++] = findViewById(R.id.btnDo0);
        mBtnSensor[i++] = findViewById(R.id.btnDo1);
        mBtnSensor[i++] = findViewById(R.id.btnDo2);
        mBtnSensor[i++] = findViewById(R.id.btnDo3);
        mBtnSensor[i++] = findViewById(R.id.btnPh0);
        mBtnSensor[i++] = findViewById(R.id.btnPh1);
        mBtnSensor[i++] = findViewById(R.id.btnPh2);
        mBtnSensor[i++] = findViewById(R.id.btnPh3);
        mBtnSensor[i++] = findViewById(R.id.btnTemperature0);
        mBtnSensor[i++] = findViewById(R.id.btnTemperature1);
        mBtnSensor[i++] = findViewById(R.id.btnTemperature2);
        mBtnSensor[i++] = findViewById(R.id.btnTemperature3);
        //mBtnSensor[i++]=findViewById(getResources().getIdentifier("btnTemperature" , "id", getPackageName()));

        for (i = 0; i < SENSOR_NUM; i++) {
            mBtnSensor[i].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //launchTemperatureDailyGraph(view);
                    String btn_name;
                    String[] urls=new String[1];
                    Button Btn;

                    Btn = findViewById(view.getId());
                    if (Btn.getText().equals("-")) return;

                    btn_name = getResources().getResourceEntryName(view.getId());
                    switch (btn_name) {
                        case "btnDo0":
                            urls[0]=AgWeb.getDoUrl(strWebGetKey,strNetworkIDs[SelectedPond],0);
                            break;
                        case "btnDo1":
                            urls[0]=AgWeb.getDoUrl(strWebGetKey,strNetworkIDs[SelectedPond],1);
                            break;
                        case "btnDo2":
                            urls[0]=AgWeb.getDoUrl(strWebGetKey,strNetworkIDs[SelectedPond],2);
                            break;
                        case "btnDo3":
                            urls[0]=AgWeb.getDoUrl(strWebGetKey,strNetworkIDs[SelectedPond],3);
                            break;
                        case "btnPh0":
                            urls[0]=AgWeb.getPhUrl(strWebGetKey,strNetworkIDs[SelectedPond],0);
                            break;
                        case "btnPh1":
                            urls[0]=AgWeb.getPhUrl(strWebGetKey,strNetworkIDs[SelectedPond],1);
                            break;
                        case "btnPh2":
                            urls[0]=AgWeb.getPhUrl(strWebGetKey,strNetworkIDs[SelectedPond],2);
                            break;
                        case "btnPh3":
                            urls[0]=AgWeb.getPhUrl(strWebGetKey,strNetworkIDs[SelectedPond],3);
                            break;
                        case "btnTemperature0":
                            urls[0]=AgWeb.getTemperatureUrl(strWebGetKey,strNetworkIDs[SelectedPond],0);
                            break;
                        case "btnTemperature1":
                            urls[0]=AgWeb.getTemperatureUrl(strWebGetKey,strNetworkIDs[SelectedPond],1);
                            break;
                        case "btnTemperature2":
                            urls[0]=AgWeb.getTemperatureUrl(strWebGetKey,strNetworkIDs[SelectedPond],2);
                            break;
                        case "btnTemperature3":
                            urls[0]=AgWeb.getTemperatureUrl(strWebGetKey,strNetworkIDs[SelectedPond],3);
                            break;




                    }
                    launchDailySingleGraph(view,urls);
/*
                    String[] urls = new String[1];
                    urls[0] = AgWeb.getTemperatureUrl(strWebGetKey,strNetworkIDs[SelectedPond],0);
                    launchDailySingleGraph(view,urls);
*/
                }
            });

            mBtnSensor[i].setOnLongClickListener(new View.OnLongClickListener() {

                @Override
                public  boolean onLongClick(View view) {
                    String btn_name;
                    String[] urls;
                    Button Btn;

                    Btn = findViewById(view.getId());
                    if (Btn.getText().equals("-")) return false;

                    btn_name = getResources().getResourceEntryName(view.getId());

                    if(btn_name.contains("Do")) {
                        urls = new String[3];
                        urls[0] = AgWeb.getDoUrl(strWebGetKey, strNetworkIDs[SelectedPond], 0);
                        urls[1] = AgWeb.getDoUrl(strWebGetKey, strNetworkIDs[SelectedPond], 1);
                        urls[2] = AgWeb.getDoUrl(strWebGetKey, strNetworkIDs[SelectedPond], 2);
                    }
                    else if(btn_name.contains("Ph")) {
                        urls = new String[3];
                        urls[0] = AgWeb.getPhUrl(strWebGetKey, strNetworkIDs[SelectedPond], 0);
                        urls[1] = AgWeb.getPhUrl(strWebGetKey, strNetworkIDs[SelectedPond], 1);
                        urls[2] = AgWeb.getPhUrl(strWebGetKey, strNetworkIDs[SelectedPond], 2);
                    }
                    else if(btn_name.contains("Temp")) {
                        urls = new String[1];
                        urls[0] = AgWeb.getTemperatureUrl(strWebGetKey, strNetworkIDs[SelectedPond], 0);
                    }else {
                        urls = new String[1];
                        urls[0] = AgWeb.getTemperatureUrl(strWebGetKey, strNetworkIDs[SelectedPond], 0);
                    }

                    launchDailySingleGraph(view,urls);
                    return true;

                }
            });




        }

    }

    @Override
    protected void onResume() {
        super.onPostResume();
        if(mSitesListReady==false) {
            updateView();
        }
    }

    void updateView() {

        if(mUpdating) return;
        SharedPreferences sp = getSharedPreferences("PondWatcher", MODE_PRIVATE);

        strWebGetKey = sp.getString("WebGetKey", "no_key");
        if (checkInternetConnection() == true && strWebGetKey!="no_key") {

            mUpdating=true;
            DownloadFromInternet downloader = new DownloadFromInternet();
            downloader.execute();
        }
    }

    public void launchSetting() {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
    }


    public void launchGraph(View view) {
        String message = BaseURL[SelectedPond];
        Intent intent = new Intent(this, DailyGraphActivity.class);
        intent.putExtra("BASE_URL", message);
        intent.putExtra("SELECTED_POND", SelectedPond);
        intent.putExtra("GRAPH_GROUP", "DO_LEVEL");
        startActivity(intent);
    }

    public void launchTemperatureDailyGraph(View view) {
        String message = BaseURL[SelectedPond];
        Intent intent = new Intent(this, DailyGraphActivity2.class);
        intent.putExtra("BASE_URL", message);
        intent.putExtra("SELECTED_POND", SelectedPond);
        intent.putExtra("GRAPH_GROUP", "TEMPERATURE");
        startActivity(intent);
    }

    public void launchDailySingleGraph(View view, String[] urls) {

        int num = urls.length;
        String strUrlX;

        //String message = BaseURL[SelectedPond];
        Intent intent = new Intent(this, DailyGraphActivity2.class);
        intent.putExtra("plots_num", num);
        for (int i = 0; i < num; i++) {
            strUrlX = String.format("url%d", i);
            intent.putExtra(strUrlX, urls[i]);
        }
        startActivity(intent);
    }


    public void launchGrid(View view) {
        String message = BaseURL[SelectedPond];
        Intent intent = new Intent(this, SyslogActivity.class);
        intent.putExtra("BASE_URL", message);
        intent.putExtra("SELECTED_POND", SelectedPond);
        startActivity(intent);
    }

    public void launchDailyGraph(View view) {
        String message = BaseURL[SelectedPond];
        String graphGroup = new String("DO_PROBE");

        Intent intent = new Intent(this, DailyGraphActivity.class);
        intent.putExtra("BASE_URL", message);
        intent.putExtra("SELECTED_POND", SelectedPond);
        intent.putExtra("GRAPH_GROUP", new String("DO_PROBE"));
        startActivity(intent);
    }

    public void launchAeratorGraph(View view) {
        String message = BaseURL[SelectedPond];
        String graphGroup = new String("DO_PROBE");

        Intent intent = new Intent(this, DailyGraphActivity.class);
        intent.putExtra("BASE_URL", message);
        intent.putExtra("SELECTED_POND", SelectedPond);
        intent.putExtra("GRAPH_GROUP", new String("AERATOR_STATUS"));
        startActivity(intent);
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
        savedInstanceState.putInt("SelectedPond", SelectedPond);
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
        SelectedPond = savedInstanceState.getInt("SelectedPond");
        if (SelectedPond >= BaseURL.length)
            SelectedPond = 0;
    }

    public boolean checkInternetConnection() {

        boolean have_connection = false;

        ConnectivityManager cManager = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
        NetworkInfo nInfo = cManager.getActiveNetworkInfo();

        if (nInfo == null) {
            Toast.makeText(this, "No Internet connection! Please connect to the Internet.", Toast.LENGTH_LONG).show();
        } else {
            have_connection = true;
        }

        return have_connection;
    }


    private void lockScreenOrientation() {
        int currentOrientation = getResources().getConfiguration().orientation;
        if (currentOrientation == Configuration.ORIENTATION_PORTRAIT) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        } else {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        }
    }

    private void unlockScreenOrientation() {
        //setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);

    }

    // Async Task Class
    class DownloadFromInternet extends AsyncTask<String, String, String> {
        ProgressDialog progressDialog;
        boolean cancel;

        // Show Progress bar before downloading Music
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Shows Progress Bar Dialog and then call doInBackground method

            cancel = false;

            progressDialog = ProgressDialog.show(MainActivity.this,
                    "Downloading Data",
                    "Please Wait!");

            progressDialog.setCanceledOnTouchOutside(true);
            progressDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                @Override
                public void onCancel(DialogInterface dialog) {
                    cancel = true;
                }
            });

            lockScreenOrientation();

            //Toast.makeText(getActivity(),"Progress Start",Toast.LENGTH_LONG).show();
        }

        // Download xml from Internet
        @Override
        protected String doInBackground(String... str) {
            int count = 1;
            int i;
            String url;
            try {

                if (!mSitesListReady) {

                    SharedPreferences sp = getSharedPreferences("PondWatcher", MODE_PRIVATE);
                    strWebGetKey = sp.getString("WebGetKey", "no_key");
                    String ioListUrl = "http://agritronics.nstda.or.th/ws/get.php?appkey=" + strWebGetKey;
                    xmlIoList = new IOlistXML(ioListUrl);
                    xmlIoList.fetchXML(ioListUrl);
                    //progressDialog.setMessage("download sites list");

                    while (!xmlIoList.isFetchComplete()) {
                        publishProgress("" + count++);
                        Thread.sleep(1000);

                        if (cancel)
                            mUpdating=false;
                            break;
                    }
                    strNetworkIDs = xmlIoList.getNetworkId();

                }

                int s=0;
                for (i = 0; i < DO_NUM; i++,s++) {
                    url = AgWeb.getDoUrl(strWebGetKey, strNetworkIDs[SelectedPond], i);
                    mXmlSensors[s] = new SensorInfoXML(url);
                    mXmlSensors[s].fetchXML();
                }
                for (i = 0; i < PH_NUM; i++,s++) {
                    url = AgWeb.getPhUrl(strWebGetKey, strNetworkIDs[SelectedPond], i);
                    mXmlSensors[s] = new SensorInfoXML(url);
                    mXmlSensors[s].fetchXML();
                }
                for (i = 0; i < T_NUM; i++,s++) {
                    url = AgWeb.getTemperatureUrl(strWebGetKey, strNetworkIDs[SelectedPond], i);
                    mXmlSensors[s] = new SensorInfoXML(url);
                    mXmlSensors[s].fetchXML();
                };


                for (i = 0; i < SENSOR_NUM; i++) {
                    while (!mXmlSensors[i].isFetchComplete()) {
                        Thread.sleep(1000);
                        publishProgress("" + count++);
                        if (cancel)
                            mUpdating=false;
                            break;
                    }
                }

            } catch (Exception e) {
                Log.e("Error: ", e.getMessage());
            }
            return null;
        }

        // While Downloading Music File
        @Override
        protected void onProgressUpdate(String... progress) {
            // Set progress percentage
            progressDialog.setMessage("Please wait... " + String.valueOf(progress[0]) + " sec");
        }

        // Once XML is downloaded
        @Override
        protected void onPostExecute(String file_url) {
            // Dismiss the dialog after the Xml file was downloaded
            //Toast.makeText(getActivity(),"Progress Ended",Toast.LENGTH_LONG).show();

            progressDialog.dismiss();
            mUpdating = false;

            // Play the music
            //updateSeriesData();

            if (cancel) {
                unlockScreenOrientation();
                return;
            }

            if (!mSitesListReady) {
                strNetworkIDs = xmlIoList.getNetworkId();
                strSitesName = xmlIoList.getSiteName();
                if (strSitesName.length > 0) {
                    mSitesList.clear();
                    mSitesList.addAll(new ArrayList<>(Arrays.asList(strSitesName)));
                    mSitesListAdapter.notifyDataSetChanged();
                    if(SelectedPond>=strSitesName.length) {
                        SelectedPond=0;
                        mSpnSitesList.setSelection(SelectedPond);
                    }


                    ArrayList<String> list = new ArrayList<>(Arrays.asList(strSitesName));
                    mPref.setStringArrayPref("SitesList", list);

                    mSitesListReady = true;
                }
            }

            if(mXmlSensors[0]==null) return;

            for(int i =0;i< SENSOR_NUM;i++) {
                mBtnSensor[i].setText(mXmlSensors[i].getLastValue());
            }

            SimpleDateFormat date_formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date io_time = new Date();
            Date cur_time = new Date();
            try {
                io_time = date_formatter.parse(mXmlSensors[0].getIoDateTime());
                if (cur_time.getTime() - io_time.getTime() > 1000 * 60 * 60) ;
            } catch (Exception e) {
                e.printStackTrace();
            }
            mTvDateTime.setText(mXmlSensors[0].getIoDateTime());
            if (cur_time.getTime() - io_time.getTime() > 1000 * 60 * 60) {
                mTvDateTime.setTextColor(Color.RED);
                Toast.makeText(getBaseContext(), "Data too old !!! ", Toast.LENGTH_LONG).show();
            } else {
                mTvDateTime.setTextColor(Color.BLACK);
            }

            unlockScreenOrientation();

        }
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    public static void onChangeUser() {
        mSitesListReady=false;
    }



}

