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
import java.util.Date;

public class MainActivity extends AppCompatActivity
        implements PondLayoutFragment.OnFragmentInteractionListener{

    final int AERATOR_NUM=20;
    boolean mDownloadCancel;
    Button mBtnDoLevel, mBtnOnMotorCount, mBtnRefresh, mBtnUsableMotor;
    Button mBtnTemperature;
    TextView tv;
    TextView[] mAerator =new TextView[AERATOR_NUM+1];
    String url1,url2;
    String[] sMotorUrl= new String[AERATOR_NUM+1];
    String sAvlMotorUrl;
    String sTemperatureUrl;
    private String chk;
    String[] BaseURL = {"http://agritronics.nstda.or.th/ws/get.php?appkey=0c5a295bd8c07a080d5306&p=AQUATIC-CONTROL-01",
            "http://agritronics.nstda.or.th/ws/get.php?appkey=0c5a295bd8c07a080d5306&p=AQUATIC-CONTROL-02",
            "http://agritronics.nstda.or.th/ws/get.php?appkey=0c5a295bd8c07a080d5306&p=AQUATIC-CONTROL-03",
            "http://agritronics.nstda.or.th/ws/get.php?appkey=0c5a295bd8c07a080d5306&p=AQUATIC-CONTROL-04",
            "http://agritronics.nstda.or.th/ws/get.php?appkey=0c5a295bd8c07a080d5306&p=AQUATIC-CONTROL-05",
            "http://agritronics.nstda.or.th/ws/get.php?appkey=0c5a295bd8c07a080b450069e3f2&p=PAN-01",
            "http://agritronics.nstda.or.th/ws/get.php?appkey=0c5a295bd8c07a080b450069e3f2&p=TEST-POND-CONTROL-1",
            "http://agritronics.nstda.or.th/ws/get.php?appkey=0c5a295bd8c07a080b450069e3f2&p=TEST-POND-CONTROL-2",
            "http://agritronics.nstda.or.th/ws/get.php?appkey=0c5a295bd8c07a080b450069e3f2&p=TEST-POND-CONTROL-3",
            "http://agritronics.nstda.or.th/ws/get.php?appkey=0c5a295bd8c07a080b450069e3f2&p=TEST-POND-CONTROL-4",
            "http://agritronics.nstda.or.th/ws/get.php?appkey=0c5a295bd8c07a080b450069e3f2&p=AQUATIC-CONTROL",};
    //"http://203.185.131.92/ws/get.php?appkey=0c5a295bd8c07a080b450069e3f2&p=AQUATIC-CONTROL",};
    SensorInfoXML xmlDoLevel, xmlOnMotorCount, xmlTemperature;
    SensorInfoXML[] xmlMotor=new SensorInfoXML[AERATOR_NUM+1];
    SensorInfoXML xmlUsableMotorCount;

    private String[] strPondsName;
    int SelectedPond;

    PondLayoutFragment pondLayoutFragment;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (findViewById(R.id.layoutPond) != null) {

            // Create a new Fragment to be placed in the activity layout
            //DailyGraphFragment dailyGraphFragment = new DailyGraphFragment();



            pondLayoutFragment = new PondLayoutFragment();


            // In case this activity was started with special instructions from an
            // Intent, pass the Intent's extras to the fragment as arguments


            //pondLayoutFragment.setArguments(getIntent().getExtras());
            Bundle arg= new Bundle();

            arg.putString("BASE_URL",BaseURL[SelectedPond]);
            arg.putInt("SELECTED_POND",SelectedPond);
            arg.putString("GRAPH_GROUP", new String("AERATOR_STATUS"));


            pondLayoutFragment.setArguments(arg);

            // Add the fragment to the 'fragment_container' FrameLayout
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.layoutPond, pondLayoutFragment).commit();
        }

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
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void StartApp() {
        tv = (TextView) findViewById(R.id.txtDate);
        mBtnRefresh = (Button) findViewById(R.id.btnRefresh);
        mBtnDoLevel = (Button) findViewById(R.id.btnDOL);
        mBtnTemperature = (Button) findViewById(R.id.btnTemperature);
        mBtnOnMotorCount = (Button) findViewById(R.id.btnOMC);

        mBtnUsableMotor = (Button) findViewById(R.id.btUsableAerator);
        mAerator[1] = (TextView) findViewById(R.id.btM1);
        mAerator[2] = (TextView) findViewById(R.id.btM2);
        mAerator[3] = (TextView) findViewById(R.id.btM3);
        mAerator[4] = (TextView) findViewById(R.id.btM4);
        mAerator[5] = (TextView) findViewById(R.id.btM5);
        mAerator[6] = (TextView) findViewById(R.id.btM6);
        mAerator[7] = (TextView) findViewById(R.id.btM7);
        mAerator[8] = (TextView) findViewById(R.id.btM8);
        mAerator[9] = (TextView) findViewById(R.id.btM9);
        mAerator[10] = (TextView) findViewById(R.id.btM10);
        mAerator[11] = (TextView) findViewById(R.id.btM11);
        mAerator[12] = (TextView) findViewById(R.id.btM12);
        mAerator[13] = (TextView) findViewById(R.id.btM13);
        mAerator[14] = (TextView) findViewById(R.id.btM14);
        mAerator[15] = (TextView) findViewById(R.id.btM15);
        mAerator[16] = (TextView) findViewById(R.id.btM16);
        mAerator[17] = (TextView) findViewById(R.id.btM17);
        mAerator[18] = (TextView) findViewById(R.id.btM18);
        mAerator[19] = (TextView) findViewById(R.id.btM19);
        mAerator[20] = (TextView) findViewById(R.id.btM20);

        //ConnectivityManager cManager = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
        //NetworkInfo nInfo = cManager.getActiveNetworkInfo();

        //if(nInfo==null) {
        //   Toast.makeText(this, "No Internet connection! Please connect to the Internet.", Toast.LENGTH_LONG).show();
        //}else
        {
            Spinner spntype = (Spinner) findViewById(R.id.spnPond);
            strPondsName = getResources().getStringArray(R.array.type);
            ArrayAdapter<String> objAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, strPondsName);
            spntype.setAdapter(objAdapter);

            SharedPreferences sp = getSharedPreferences("PondWatcher", MODE_PRIVATE);
            int selected_pond = sp.getInt("SelectedPond",1);
            spntype.setSelection(selected_pond);
            //spntype.setSelection(objAdapter.getPosition("AQUA DEMO1"));
            spntype.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
                    int i;


                    SharedPreferences sp = getSharedPreferences("PondWatcher", MODE_PRIVATE);
                    SharedPreferences.Editor sp_editor = sp.edit();
                    sp_editor.putInt("SelectedPond", pos);
                    sp_editor.apply();

                    url1 = BaseURL[pos] + ",4096,1505";
                    url2 = BaseURL[pos] + ",4096,1506";

                    if(BaseURL[pos].contains("AQUATIC-CONTROL"))
                    {
                        url1 = BaseURL[pos] + ",4096,300";
                        sTemperatureUrl = BaseURL[pos] + ",4096,304";
                    }
                    sAvlMotorUrl = BaseURL[pos] + ",4096,1507";

                    for (i = 1; i <= AERATOR_NUM; i++) {
                        sMotorUrl[i] = BaseURL[pos];

                    }

                    SelectedPond = pos;
                    int ionumber = 1520;

                    for (i = 1; i <= AERATOR_NUM; i++) {
                        sMotorUrl[i] = BaseURL[pos] + ",4096," + String.valueOf(ionumber);
                        ionumber++;
                    }

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

            mBtnDoLevel.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    launchDailyGraph(v);
                    return true;
                }
            });

            mBtnTemperature.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    launchTemperatureDailyGraph(view);

                }
            });

            mBtnOnMotorCount.setOnClickListener( new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    launchAeratorGraph(v);
                }

            });

            mBtnUsableMotor.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    launchGrid(v);
                }


            });




            mAerator[1].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ShowMotorState(1);
                }
            });
            mAerator[2].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ShowMotorState(2);
                }
            });
            mAerator[3].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ShowMotorState(3);
                }
            });
            mAerator[4].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ShowMotorState(4);
                }
            });
            mAerator[5].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ShowMotorState(5);
                }
            });
            mAerator[6].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ShowMotorState(6);
                }
            });
            mAerator[7].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ShowMotorState(7);
                }
            });
            mAerator[8].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ShowMotorState(8);
                }
            });
            mAerator[9].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ShowMotorState(9);
                }
            });
            mAerator[10].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ShowMotorState(10);
                }
            });
            mAerator[11].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ShowMotorState(11);
                }
            });
            mAerator[12].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ShowMotorState(12);
                }
            });
            mAerator[13].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ShowMotorState(13);
                }
            });
            mAerator[14].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ShowMotorState(14);
                }
            });
            mAerator[15].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ShowMotorState(15);
                }
            });
            mAerator[16].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ShowMotorState(16);
                }
            });
            mAerator[17].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ShowMotorState(17);
                }
            });
            mAerator[18].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ShowMotorState(18);
                }
            });
            mAerator[19].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ShowMotorState(19);
                }
            });
            mAerator[20].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ShowMotorState(20);
                }
            });

        }
    }

    public void DisplayMotorStatus(){

        int relay_state;
        int decision_state;
        int profile_state;
        int mode_state;

        String str_value;
        int value;
        for(int i=1;i<=AERATOR_NUM;i++) {

            str_value = xmlMotor[i].getLastValue();

            if (!xmlMotor[i].getDetails().equals("no"))
            {
                //mAerator[i].setText(xmlMotor[i].getDetails());
            }
            /*{
                RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(
                        RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);


   ;
                layoutParams.setMargins(i*10,i*10, 0, 0);
                mAerator[i].setWidth(20);
                mAerator[i].setHeight(20);
                mAerator[i].setLayoutParams(layoutParams);
                //text.setText("Result ");
            }*/



            if(str_value.equals("-")){
                mAerator[i].setBackgroundColor(Color.WHITE);
                mAerator[i].setTextColor(Color.WHITE);
            }else {
                value = (int) Float.parseFloat(str_value);
                relay_state = value%100;
                value/=100;
                decision_state = value%10;
                value/=10;
                profile_state = value%10;
                value/=10;
                mode_state = value %10;

                str_value = xmlMotor[i].getLastValue();
                value = (int) Float.parseFloat(str_value);
                relay_state = value%100;
                value/=100;
                decision_state = value%10;
                value/=10;
                profile_state = value%10;
                value/=10;
                mode_state = value %10;

                mAerator[i].setTextColor(Color.WHITE);

                if (relay_state == 3) { //relay on
                    mAerator[i].setBackgroundColor(Color.argb(255, 0, 192, 0));
                    if (profile_state == 1) {// profile MustOn
                        mAerator[i].setBackgroundColor(Color.argb(255, 0, 112, 0));
                    }
                } else if (relay_state == 5) { //relay off
                    if (profile_state == 2) // profile MustOff
                        mAerator[i].setBackgroundColor(Color.argb(255, 204, 204, 204));
                    else if (profile_state == 3) // profile Rest
                        mAerator[i].setBackgroundColor(Color.argb(255, 64, 64, 128));
                    else
                        mAerator[i].setBackgroundColor(Color.BLACK);
                } else if (str_value.equals("-")) {
                    mAerator[i].setBackgroundColor(Color.WHITE);
                } else if (relay_state == 10) { //relay manual on
                    mAerator[i].setBackgroundColor(Color.YELLOW);
                    //mAerator[i].setBackgroundColor(Color.argb(255,192,192,192));
                    //mAerator[i].setTextColor(Color.argb(255,0,192,0));
                } else if (relay_state == 11) { // relay manual off
                    mAerator[i].setBackgroundColor(Color.BLUE);
                    //mAerator[i].setBackgroundColor(Color.argb(255,0,0,192));
                    //mAerator[i].setBackgroundColor(Color.argb(255,192,192,192));
                    //mAerator[i].setTextColor(Color.BLACK);

                }else {
                    mAerator[i].setBackgroundColor(Color.RED);

                }


            }


        }

    }

    public void ShowMotorState(int mNo){

        int relay_state;
        int decision_state;
        int profile_state;
        int mode_state;
        int value;
        int state;
        String str_value;

        String[] strRelayState = {
                "Null","Unknow","Activate","On","Deactivate","Off",
                "OverCurrent","UnderCurrent","InternalErr","CommError",
                "ManualOn","ManualOff","mOverCurrent","mUnderCurrent","mInternalError"
        };

        String[] strDecisionState = {
                "On","Off","Defer"
        };

        String[] strProfileState = {
                "OnDemand","MustOn","MustOff","Rest"
        };

        String[] strModeState = {
                "Control","ForceOn","ForceOff","UnCare"
        };

        if (xmlMotor[mNo]==null) return;

        str_value = xmlMotor[mNo].getLastValue();

        if (str_value.equals("-")) return;

        value = (int) Float.parseFloat(str_value);
        state = value;
        relay_state = value%100;
        value/=100;
        decision_state = value%10;
        value/=10;
        profile_state = value%10;
        value/=10;
        mode_state = value %10;

        String str_show;

        /*if (SelectedPond==2) {
            str_show = "Motor"+ mNo + " S" + state + " "
                    + strRelayState[relay_state];
        } else*/ {
            str_show = "Index"+ (mNo-1) + " S" + state + " m"
                    + strModeState[mode_state] + " p"
                    + strProfileState[profile_state] + " d"
                    + strDecisionState[decision_state] + " r"
                    + strRelayState[relay_state] + " x"
                    + xmlMotor[mNo].getPosX() + " y"
                    + xmlMotor[mNo].getPosY() + " ";



        }


        Toast.makeText(this,str_show,Toast.LENGTH_SHORT).show();

    }

    void updateView() {

        if (checkInternetConnection()==true) {

            DownloadFromInternet downloader = new DownloadFromInternet();
            downloader.execute();
            if ( pondLayoutFragment!= null) {
                //disable for Aquatic Control Company::Mink
                pondLayoutFragment.setPondUrl(BaseURL[SelectedPond]);
                pondLayoutFragment.updateGraph();

            }

        }
    }

    public void launchGraph(View view) {
        String message = BaseURL[SelectedPond];
        Intent intent = new Intent(this, DailyGraphActivity.class);
        intent.putExtra("BASE_URL", message);
        intent.putExtra("SELECTED_POND", SelectedPond);
        intent.putExtra("GRAPH_GROUP" , "DO_LEVEL");
        startActivity(intent);
    }

    public void launchTemperatureDailyGraph(View view) {
        String message = BaseURL[SelectedPond];
        Intent intent = new Intent(this, DailyGraphActivity.class);
        intent.putExtra("BASE_URL", message);
        intent.putExtra("SELECTED_POND", SelectedPond);
        intent.putExtra("GRAPH_GROUP" , "TEMPERATURE");
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
        intent.putExtra("GRAPH_GROUP" , new String("DO_PROBE"));
        startActivity(intent);
    }

    public void launchAeratorGraph(View view) {
        String message = BaseURL[SelectedPond];
        String graphGroup = new String("DO_PROBE");

        Intent intent = new Intent(this, DailyGraphActivity.class);
        intent.putExtra("BASE_URL", message);
        intent.putExtra("SELECTED_POND", SelectedPond);
        intent.putExtra("GRAPH_GROUP" , new String("AERATOR_STATUS"));
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
        if (SelectedPond>=BaseURL.length)
            SelectedPond=0;
    }

    public boolean checkInternetConnection() {

        boolean have_connection = false;

        ConnectivityManager cManager = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
        NetworkInfo nInfo = cManager.getActiveNetworkInfo();

        if(nInfo==null) {
            Toast.makeText(this, "No Internet connection! Please connect to the Internet.", Toast.LENGTH_LONG).show();
        } else {
            have_connection=true;
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
            int count=1;
            try {
                xmlDoLevel = new SensorInfoXML(url1);
                xmlDoLevel.fetchXML();
                xmlOnMotorCount = new SensorInfoXML(url2);
                xmlOnMotorCount.fetchXML();

                for (int i = 1; i <= AERATOR_NUM; i++) {
                    xmlMotor[i] = new SensorInfoXML(sMotorUrl[i]);
                    xmlMotor[i].fetchXML();
                }

                xmlTemperature = new SensorInfoXML(sTemperatureUrl);
                xmlTemperature.fetchXML();

                xmlUsableMotorCount = new SensorInfoXML(sAvlMotorUrl);
                xmlUsableMotorCount.fetchXML();



                while (!xmlDoLevel.isFetchComplete()) {
                    Thread.sleep(1000);
                    publishProgress("" + count++);
                    if(cancel)
                        break;
                }
                while (!xmlOnMotorCount.isFetchComplete())  {
                    if (cancel)
                        break;
                }

                for (int i = 1; i <= AERATOR_NUM; i++) {
                    while (!xmlMotor[i].isFetchComplete()) {
                        if (cancel)
                            break;
                    }
                }
                while (!xmlUsableMotorCount.isFetchComplete()) {
                    if (cancel)
                        break;
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
            progressDialog.setMessage("Please wait... "+String.valueOf(progress[0])+" sec");
        }

        // Once XML is downloaded
        @Override
        protected void onPostExecute(String file_url) {
            // Dismiss the dialog after the Xml file was downloaded
            //Toast.makeText(getActivity(),"Progress Ended",Toast.LENGTH_LONG).show();

            progressDialog.dismiss();
            // Play the music
            //updateSeriesData();

            if (cancel) {
                unlockScreenOrientation();
                return;
            }

            mBtnDoLevel.setText(xmlDoLevel.getLastValue());
            mBtnOnMotorCount.setText(xmlOnMotorCount.getLastValue());
            mBtnTemperature.setText(xmlTemperature.getLastValue());

            SimpleDateFormat date_formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date io_time = new Date();
            Date cur_time = new Date();
            try {
                io_time = date_formatter.parse(xmlDoLevel.getIoDateTime());
                if (cur_time.getTime()-io_time.getTime() > 1000*60*60);
            }    catch (Exception e)
            {
                e.printStackTrace();
            }
            tv.setText(xmlDoLevel.getIoDateTime());
            if (cur_time.getTime()-io_time.getTime() > 1000*60*60) {
                tv.setTextColor(Color.RED);
                Toast.makeText(getBaseContext(), "Data too old !!! " ,Toast.LENGTH_LONG).show();
            } else {
                tv.setTextColor(Color.BLACK);
            }


            String str = mBtnDoLevel.getText().toString();
            //float f = Float.parseFloat(str);
            //if (f < 1) {
            //    f = f * 20;
            //}
            //str = String.format("%.2f", f);
            //mBtnDoLevel.setText(str);

            for (int i = 1; i <= AERATOR_NUM; i++) {
                mAerator[i].setText(String.valueOf(i));
            }


            mBtnUsableMotor.setText(xmlUsableMotorCount.getLastValue());

            DisplayMotorStatus();

            unlockScreenOrientation();

        }
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }

}

