package com.java.baohan.backend;

import android.provider.ContactsContract;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

public class PandemicData {

    private static ConcurrentHashMap<String, List<DataEntry>> countryData;
    private static ConcurrentHashMap<String, List<DataEntry>> provinceData;
    private static ConcurrentHashMap<String, ConcurrentHashMap<String, List<DataEntry>>> provinceDetails;
    private static boolean finishedUpdating = false;

    private static final String DATA_URL = "https://covid-dashboard.aminer.cn/api/dist/epidemic.json";
    private static final DateTimeFormatter dateFormat = DateTimeFormatter.ISO_LOCAL_DATE;

    public PandemicData() {
        countryData = new ConcurrentHashMap<>();
        provinceData = new ConcurrentHashMap<>();
        provinceDetails = new ConcurrentHashMap<>();

        // parse data
        new Thread(new Runnable() {
            @Override
            public void run() {
                updateDataCache();
            }
        }).start();

        // TODO: remove this
        new Thread(new Runnable() {
            @Override
            public void run() {
                while(!finishedUpdating) ;

                System.out.println("Finished updating!");
            }
        }).start();
    }

    public ConcurrentHashMap<String, List<DataEntry>> getCountryData() {
        return countryData;
    }

    public ConcurrentHashMap<String, List<DataEntry>> getProvinceData() {
        return provinceData;
    }

    public ConcurrentHashMap<String, ConcurrentHashMap<String, List<DataEntry>>> getProvinceDetails() {
        return provinceDetails;
    }

    public boolean isDataReady() { return finishedUpdating; }

    private void updateDataCache() {
        finishedUpdating = false;
        try {
            URL url = new URL(DATA_URL);
            parse(getInputStream(url));
        } catch (Exception e) {}
        finishedUpdating = true;
    }

    private InputStream getInputStream(URL url) throws Exception {
        HttpURLConnection conn;
        conn = (HttpURLConnection) url.openConnection();
        conn.setConnectTimeout(60 * 1000);
        conn.setReadTimeout(30 * 1000);
        conn.setRequestMethod("GET");
        if (conn.getResponseCode() != 200) {
            //TODO: add toast: "bad internet connection"
            return null;
        }
        return conn.getInputStream();
    }

    private void parse(InputStream is) throws Exception {
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            sb.append(line);
        }
        reader.close();
        JSONObject jsonObject = new JSONObject(sb.toString());

        // parse JSON
        for (Iterator<String > it = jsonObject.keys(); it.hasNext(); ) {
            String loc = it.next();
            String[] split = loc.split("\\|");
            JSONObject rawData = jsonObject.getJSONObject(loc);
            if (split.length == 1) { // country data
                countryData.put(split[0], parseData(rawData));
            } else if (split.length == 2 && split[0].equals("China")) { // province data
                provinceData.put(split[1], parseData(rawData));
            } else if (split.length == 3 && split[0].equals("China")) { // province details
                provinceDetails.putIfAbsent(split[1], new ConcurrentHashMap<>());
                provinceDetails.get(split[1]).put(split[2], parseData(rawData));
            }
        }
    }

    private List<DataEntry> parseData(JSONObject rawData) {
        List<DataEntry> ret = new ArrayList<>();
        try {
            LocalDate beginDate = LocalDate.parse(rawData.getString("begin"), dateFormat);

            JSONArray rawDataArray = rawData.getJSONArray("data");
            for(int i = 0; i < rawDataArray.length(); i++) {
                JSONArray rawEntry = rawDataArray.getJSONArray(i);
                DataEntry entry = new DataEntry(
                    rawEntry.getInt(0),
                    rawEntry.getInt(2),
                    rawEntry.getInt(3),
                    beginDate.plusDays(i)
                );
                ret.add(entry);
            }
        } catch (Exception e) {}

        return ret;
    }
}
