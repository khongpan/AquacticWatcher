package com.aqc.watcher;

/**
 * Created by Mink on 12/10/2015.
 */

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Date;


public class IOlistXML {
    private int MAX_IO = 200;
    private String strUrl = null;
    private String[] strNetworkId = new String[MAX_IO];
    private String[] strRegion = new String[MAX_IO];
    private Date[] mDateTime = new Date[MAX_IO];
    private String mIoName;
    private int iRecordCount;

    private XmlPullParserFactory xmlFactoryObject;
    private volatile boolean parsingComplete = false;


    public IOlistXML(String url) {
        this.strUrl = url;
    }

    public boolean isFetchComplete() {
        return parsingComplete;
    }

    public int getCountRecord() {
        return iRecordCount;
    }

    public String getNetworkId(int index) {
        return strNetworkId[index];
    }

    public String getSiteName(int index) {
        return strRegion[index];
    }

    public String[] getNetworkId() {
        String[] NetworkIdLists = new String[iRecordCount];
        for (int i = 0; i < iRecordCount; i++) {
            NetworkIdLists[i] = strNetworkId[i];
        }
        return NetworkIdLists;

    }

    public String[] getSiteName() {
        String[] siteNameLists = new String[iRecordCount];
        for (int i = 0; i < iRecordCount; i++) {
            siteNameLists[i] = strRegion[i];
        }
        return siteNameLists;
    }


    private void parseXML(XmlPullParser myParser) {
        int event, i = 0;
        String text = null;
        try {
            event = myParser.getEventType();
            while (event != XmlPullParser.END_DOCUMENT) {
                String name = myParser.getName();
                switch (event) {
                    case XmlPullParser.START_TAG:
                        break;
                    case XmlPullParser.TEXT:
                        text = myParser.getText();
                        break;
                    case XmlPullParser.END_TAG:
                        switch (name) {
                            case "IONumber":
                                if (text.equals("300")) iRecordCount++;
                                break;
                            case "Region":
                                strRegion[i] = text;
                                break;
                            case "NetworkID":
                                strNetworkId[i] = text;
                                break;
                            case "IO":
                                i = iRecordCount;
                                break;
                        }
                        break;
                }
                event = myParser.next();
            }
            parsingComplete = true;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void fetchXML(String reqUrl) {

        strUrl = reqUrl;

        parsingComplete = false;
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    URL url = new URL(strUrl);
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setReadTimeout(10000 /* milliseconds */);
                    conn.setConnectTimeout(15000 /* milliseconds */);
                    conn.setRequestMethod("GET");
                    conn.setDoInput(true);
                    conn.connect();

                    InputStream stream = conn.getInputStream();
                    xmlFactoryObject = XmlPullParserFactory.newInstance();
                    XmlPullParser myparser = xmlFactoryObject.newPullParser();

                    myparser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
                    myparser.setInput(stream, null);
                    parseXML(myparser);
                    stream.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        thread.start();
    }


}
