package com.aqc.watcher;

public class AgritronicsWebHelper {
    private static final AgritronicsWebHelper ourInstance = new AgritronicsWebHelper();

    private static final String mBaseURL = "http://agritronics.nstda.or.th/ws/get.php?appkey="; //0c5a295bd8c07a0809511130b0a3&p=AQUATIC-CONTROL-01",

    public static AgritronicsWebHelper getInstance() {
        return ourInstance;
    }

    private AgritronicsWebHelper() {

    }


    public String getDoUrl(String key, String NetworkID, int index) {
        String url;
        int io_number = 300;
        io_number += index;

        if(index>2) io_number=0;

        url = mBaseURL + key + "&p=" + NetworkID + ",4096," + String.valueOf(io_number);

        return url;
    }


    public String getTemperatureUrl(String key, String NetworkID, int index) {
        String url;
        int io_number = 304;
        io_number += index;

        if(index>1) io_number=0;

        url = mBaseURL + key + "&p=" + NetworkID + ",4096," + String.valueOf(io_number);
        return url;
    }

    public String getPhUrl(String key, String NetworkID, int index) {
        String url;
        int io_number = 303;

        switch (index) {
            case 0:
                io_number += index;
                break;
            case 1:
                io_number += 3;
                break;
            case 2:
                io_number += 4;
                break;

            default:
                io_number = 0;
                break;
        }

        url = mBaseURL + key + "&p=" + NetworkID + ",4096," + String.valueOf(io_number);

        return url;
    }


}
