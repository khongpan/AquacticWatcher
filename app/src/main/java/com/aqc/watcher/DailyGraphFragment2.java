package com.aqc.watcher;

import android.support.v4.app.Fragment;
//import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.LegendRenderer;
import com.jjoe64.graphview.helper.StaticLabelsFormatter;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DailyGraphFragment2 extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    Date date = new Date();
    String mStrSelectedDay = new SimpleDateFormat("yyyy-MM-dd").format(date);

    //private String mBaseUrl;
    //private int mSelectedPond;
    //private String mStrGraphGroup;
    private String[] mUrls;
    private int mPlotsNum = 0;

    private DailyGraphFragment2.OnFragmentInteractionListener mListener;

    private final Handler mHandler = new Handler();
    private Runnable mTimer1;
    private Runnable mTimer2;

    private GraphView mGraphView;
    //private LineGraphSeries<DataPoint> mSeries1;
    //private LineGraphSeries<DataPoint> mSeries2;
    //private LineGraphSeries<DataPoint> mSeries3;
    private LineGraphSeries<DataPoint>[] mSeries;

    //private SensorDailyDataXML sensorXml1;
    //private SensorDailyDataXML sensorXml2;
    //private SensorDailyDataXML sensorXml3;
    private SensorDailyDataXML[] mSensorsXml;

    private int[] mColorsList = {Color.BLUE,Color.GREEN,Color.RED,Color.MAGENTA,Color.YELLOW,Color.CYAN};


    public DailyGraphFragment2() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment DailyGraphFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static DailyGraphFragment2 newInstance(String param1, String param2) {
        DailyGraphFragment2 fragment = new DailyGraphFragment2();
        Bundle args = new Bundle();
        //args.putString(ARG_PARAM1, param1);
        //args.putString(ARG_PARAM2, param2);
        //fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            //mParam1 = getArguments().getString(ARG_PARAM1);
            //mParam2 = getArguments().getString(ARG_PARAM2);
            //mBaseUrl = getArguments().getString("BASE_URL");
            //mSelectedPond = getArguments().getInt("SELECTED_POND", 0);
            //mStrGraphGroup = getArguments().getString("GRAPH_GROUP");
            mPlotsNum = getArguments().getInt("plots_num");
            mUrls = new String[mPlotsNum];
            mSeries = new LineGraphSeries[mPlotsNum];
            for (int i = 0; i < mPlotsNum; i++) {
                mUrls[i] = getArguments().getString(String.format("url%d", i));
            }

        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_daily_graph, container, false);

        mGraphView = (GraphView) rootView.findViewById(R.id.graph);

        //setGraphFormat();
        //updateGraph();

        // Inflate the layout for this fragment
        return rootView;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof DailyGraphFragment2.OnFragmentInteractionListener) {
            mListener = (DailyGraphFragment2.OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    public void formatPlotArea() {
        //mGraphView.getLegendRenderer().setVisible(true);
        //mGraphView.getLegendRenderer().setBackgroundColor(Color.WHITE);
        //mGraphView.getLegendRenderer().setAlign(LegendRenderer.LegendAlign.TOP);

        mGraphView.getGridLabelRenderer().setGridColor(Color.DKGRAY);

        mGraphView.getViewport().setXAxisBoundsManual(true);
        //mGraphView.getViewport().setMinX(0);
        //mGraphView.getViewport().setMaxX(288);

        // set manual Y bounds
        mGraphView.getViewport().setYAxisBoundsManual(true);
        mGraphView.getViewport().setMinY(0);
        mGraphView.getViewport().setMaxY(20);


        //mGraphView.getViewport().setScrollable(true);

        StaticLabelsFormatter staticLabelsFormatter = new StaticLabelsFormatter(mGraphView);


        staticLabelsFormatter.setHorizontalLabels(new String[]{"", "", "03", "", "", "06", "", "", "09", "", "", "12", "", "", "15", "", "", "18", "", "", "21", "", "", "", ""});
        mGraphView.getGridLabelRenderer().setLabelFormatter(staticLabelsFormatter);
        mGraphView.getGridLabelRenderer().setHumanRounding(false);

        mGraphView.getGridLabelRenderer().setNumVerticalLabels(9);
        //mGraphView.getGridLabelRenderer().setNumHorizontalLabels(2);
/*
        // set date label formatter
        DateFormat df = DateFormat.getTimeInstance(DateFormat.SHORT);
        mGraphView.getGridLabelRenderer().setLabelFormatter(new DateAsXAxisLabelFormatter(getActivity(), df));
        mGraphView.getGridLabelRenderer().setNumHorizontalLabels(4); // only 4 because of the space
*/

        // set manual x bounds to have nice steps
        SimpleDateFormat date_formatter = new SimpleDateFormat("yyyy-MM-dd");
        Date start_time = new Date();
        Date end_time = new Date();
        try {
            start_time = date_formatter.parse(mStrSelectedDay);
            end_time.setTime(start_time.getTime() + 1000 * 60 * 60 * 24);
        } catch (Exception e) {
            e.printStackTrace();
        }

        mGraphView.getViewport().setMinX(start_time.getTime());
        mGraphView.getViewport().setMaxX(end_time.getTime());
        mGraphView.getViewport().setXAxisBoundsManual(true);

        for (int i = 0; i < mPlotsNum; i++) {
            mSeries[i] = new LineGraphSeries<DataPoint>(new DataPoint[]{new DataPoint(0, 0), new DataPoint(0, 0)});

            //mSeries[i].setTitle("series 1");
            mSeries[i].setColor(mColorsList[i%mColorsList.length]);
            mSeries[i].setThickness(2);

            mGraphView.addSeries(mSeries[i]);
        }
    }

    public void updateSeriesData() {
        int p;
        DataPoint[] dataPoint;

        for (p = 0; p < mPlotsNum; p++) {
            int data_count = mSensorsXml[p].getCountRecord();

            if (data_count > 0) {
                dataPoint = new DataPoint[mSensorsXml[p].getCountRecord()];
                for (int i = 0; i < mSensorsXml[p].getCountRecord(); i++) {
                    DataPoint v = new DataPoint(mSensorsXml[p].getDataTimeStamp(i), mSensorsXml[p].getDataValue(i));
                    dataPoint[i] = v;
                }
                if (dataPoint == null)
                    dataPoint = new DataPoint[]{new DataPoint(0, 0), new DataPoint(0, 0)};

            } else {
                dataPoint = new DataPoint[]{new DataPoint(0, 0)};
            }
            mSeries[p].resetData(dataPoint);
            mSeries[p].setTitle(mSensorsXml[p].getIoName());
        }
        //mGraphView.removeAllSeries();
        //mGraphView.addSeries(mSeries1);
        //mGraphView.addSeries(mSeries2);


        mGraphView.getLegendRenderer().setVisible(true);
        mGraphView.getLegendRenderer().setBackgroundColor(Color.WHITE);
        mGraphView.getLegendRenderer().setAlign(LegendRenderer.LegendAlign.TOP);

/*
        // set date label formatter
        DateFormat df = DateFormat.getTimeInstance(DateFormat.SHORT);
        mGraphView.getGridLabelRenderer().setLabelFormatter(new DateAsXAxisLabelFormatter(getActivity(), df));
        mGraphView.getGridLabelRenderer().setNumHorizontalLabels(4); // only 4 because of the space
*/

        // set manual x bounds to have nice steps
        SimpleDateFormat date_formatter = new SimpleDateFormat("yyyy-MM-dd");
        Date start_time = new Date();
        Date end_time = new Date();
        try {
            start_time = date_formatter.parse(mStrSelectedDay);
            end_time.setTime(start_time.getTime() + 1000 * 60 * 60 * 24 + 1);
        } catch (Exception e) {
            e.printStackTrace();
        }

        mGraphView.getViewport().setMinX(start_time.getTime());
        mGraphView.getViewport().setMaxX(end_time.getTime());
        mGraphView.getViewport().setXAxisBoundsManual(true);
        if(mSensorsXml[0].getCountRecord()>0) {

            if (mSensorsXml[0].getIoType().contains("Temp")) {
                mGraphView.getViewport().setMinY(15);
                mGraphView.getViewport().setMaxY(45);
                mGraphView.getGridLabelRenderer().setNumVerticalLabels(16);
                mGraphView.getGridLabelRenderer().setHumanRounding(false, true);
            } else if (mSensorsXml[0].getIoType().contains("Oxygen")) {
                mGraphView.getViewport().setMinY(0);
                mGraphView.getViewport().setMaxY(16);
                mGraphView.getGridLabelRenderer().setNumVerticalLabels(9);
                mGraphView.getGridLabelRenderer().setHumanRounding(false, true);
            } else if (mSensorsXml[0].getIoType().contains("Alkalinity")) {
                mGraphView.getViewport().setMinY(3);
                mGraphView.getViewport().setMaxY(11);
                mGraphView.getGridLabelRenderer().setNumVerticalLabels(9);
                mGraphView.getGridLabelRenderer().setHumanRounding(false, true);
            }
        }


        //mGraphView.getViewport().setScalable(true);
        //mGraphView.getViewport().setScrollable(true);


    }

    void setDate(String date_str) {
        mStrSelectedDay = date_str;
    }


    public boolean checkInternetConnection() {

        boolean have_connection = false;

        ConnectivityManager cManager = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo nInfo = cManager.getActiveNetworkInfo();

        if (nInfo == null) {
            Toast.makeText(getActivity(), "No Internet connection! Please connect to the Internet.", Toast.LENGTH_LONG).show();
        } else {
            have_connection = true;
        }
        return have_connection;
    }

    void updateGraph() {

        if (checkInternetConnection() == false) return;

        DailyGraphFragment2.DownloadFromInternet Downloader = new DailyGraphFragment2.DownloadFromInternet();
        Downloader.execute("100", mStrSelectedDay);
    }

    private void lockScreenOrientation() {
        int currentOrientation = getResources().getConfiguration().orientation;
        if (currentOrientation == Configuration.ORIENTATION_PORTRAIT) {
            getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        } else {
            getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        }
    }

    private void unlockScreenOrientation() {
        //getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);
        getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
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
            //showDialog(progress_bar_type);
            cancel = false;

            progressDialog = ProgressDialog.show(getActivity(),
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
            try {
                DataPoint[] dataPoint;
                String finalUrl1, finalUrl2;
                String io_number = str[0];
                String date_str = str[1];

                mSensorsXml = new SensorDailyDataXML[mPlotsNum];

                for (i = 0; i < mPlotsNum; i++) {
                    //mUrls[i] = mUrls[i] + "," + date_str;
                    mSensorsXml[i] = new SensorDailyDataXML(mUrls[i]+","+date_str);
                    mSensorsXml[i].fetchXML(mUrls[i]+","+date_str);
                }
                boolean fetch_complete = false;
                while (!fetch_complete) {
                    fetch_complete = true;
                    for (i = 0; i < mPlotsNum; i++) {
                        if (!mSensorsXml[i].isFetchComplete()) {
                            fetch_complete = false;
                        }
                        Thread.sleep(1000);
                        publishProgress("" + count++);
                        if (cancel) break;
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
            if (cancel) {
                unlockScreenOrientation();
                return;
            }
            updateSeriesData();
            unlockScreenOrientation();
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("STR_SELECTED_DAY", mStrSelectedDay);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if (savedInstanceState != null) {
            mStrSelectedDay = savedInstanceState.getString("STR_SELECTED_DAY");
        }
        if (mStrSelectedDay == null) {
            mStrSelectedDay = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
        }
        formatPlotArea();
        updateGraph();
    }

    public static Date convertStringToDate(String date) {
        SimpleDateFormat FORMATTER = new SimpleDateFormat("yyyy-MM-dd");
        if (date != null) {
            try {
                return FORMATTER.parse(date);
            } catch (ParseException e) {
                // nothing we can do if the input is invalid
                throw new RuntimeException(e);
            }
        }
        return null;
    }

}
