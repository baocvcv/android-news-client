package com.java.baohan.backend;

import android.app.Application;

import com.java.baohan.R;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class CovidEvent {

    public int classNum;
    public Date date;
    public String title;
    public String url;

    public CovidEvent() {}

    public CovidEvent(int classNum, Date date, String title, String url) {
        this.classNum = classNum;
        this.date = date;
        this.title = title;
        this.url = url;
    }

    private static Map<Integer, List<CovidEvent>> eventList = null;
    private static List<String> classNames;
    private static int numClasses;
//    private static final DateTimeFormatter dateFormat = DateTimeFormatter.ISO_LOCAL_DATE;
    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss");

    public static List<CovidEvent> getEventList(int classNum) {
        return eventList.getOrDefault(classNum, null);
    }

    public static List<String> getClassNames() {
        return classNames;
    }

    public static void loadEventList(Application app) {
        try {
            InputStream is = app.getResources().openRawResource(R.raw.clustering_result);
            BufferedReader reader = new BufferedReader(new InputStreamReader(is));
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
            reader.close();
            JSONObject jsonObject = new JSONObject(sb.toString());

            // parse jsonObject
            JSONArray rawClasses = jsonObject.getJSONArray("classes");
            classNames = new ArrayList<>();
            for (int i = 0; i < rawClasses.length(); i++)
                classNames.add(rawClasses.getString(i));

            JSONArray rawData = jsonObject.getJSONArray("data");
            eventList = new HashMap<>();
            for (int i = 0; i < rawData.length(); i++) {
                JSONObject obj = rawData.getJSONObject(i);
                int classNum = obj.getInt("class");
                Date date = dateFormat.parse(obj.getString("date"));
                String title = obj.getString("title");
                String url = obj.getJSONArray("urls").getString(0);

                eventList.putIfAbsent(classNum, new LinkedList<>());
                eventList.get(classNum).add(new CovidEvent(classNum, date, title, url));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public String toString() {
        return title;
    }
}
