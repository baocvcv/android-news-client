package com.java.baohan.backend;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLStreamHandler;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class Scholar {

    /* public fields */
    public Bitmap avatar;
    public String name_en;
    public String name_zh;

    public double activity;
    public int citations;
    public double diversity;
    public double gindex;
    public double hindex;
    public double sociability;
    public int publications;

    public String[] affiliation;
    public String[] position;
    public String homepage;
    public String bio;
    public String edu;
    public String work;

    public Map<String, Integer> tags;

    private boolean isAlive;
    private String id;

    /* private fields */
    private static final ConcurrentHashMap<String, Scholar> aliveScholars = new ConcurrentHashMap<>();
    private static final ConcurrentHashMap<String, Scholar> deadScholars = new ConcurrentHashMap<>();

    /* constants */
    private static final String URL_SCHOLAR = "https://innovaapi.aminer.cn/predictor/api/v1/valhalla/highlight/get_ncov_expers_list?v=2";


    /* public methods */
    public static void cacheScholars() {
        try {
            URL url = new URL(URL_SCHOLAR);
            parse(getInputStream(url));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static ConcurrentHashMap<String, Scholar> getAliveScholars() {
        return aliveScholars;
    }

    public static ConcurrentHashMap<String, Scholar> getDeadScholars() {
        return deadScholars;
    }

    /* private methods */
    private static InputStream getInputStream(URL url) throws Exception {
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

    private static void parse(InputStream is) throws Exception {
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            sb.append(line);
        }
        reader.close();
        JSONObject jsonObject = new JSONObject(sb.toString());

        // parse JSON
        int status = jsonObject.getInt("status");
        if (status != 0) {
            return;
        }

        JSONArray rawDataArray = jsonObject.getJSONArray("data");
        for (int i = 0; i < rawDataArray.length(); i++) {
            JSONObject rawData = rawDataArray.getJSONObject(i);
            Scholar s = new Scholar();

            // load avatar
            String avatarURL = rawData.getString("avatar");
            Thread t = null;
            //TODO: create a task queue for retrieving images, avoid having to wait for image to download
            if (!avatarURL.isEmpty()) {
                // load image async
                t = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            s.avatar = BitmapFactory.decodeStream(new URL(avatarURL).openConnection().getInputStream());
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
                t.start();
            }

            // parse basic info
            s.id = rawData.getString("id");
            s.name_en = rawData.getString("name");
            s.name_zh = rawData.getString("name_zh");
            s.isAlive = !rawData.getBoolean("is_passedaway");

            JSONObject indices = rawData.getJSONObject("indices");
            s.activity = indices.getDouble("activity");
            s.citations = indices.getInt("citations");
            s.diversity = indices.getDouble("diversity");
            s.gindex = indices.getDouble("gindex");
            s.hindex = indices.getDouble("hindex");
            s.sociability = indices.getDouble("sociability");
            s.publications = indices.getInt("pubs");

            // parse profile
            JSONObject profile = rawData.getJSONObject("profile");
            s.affiliation = profile.optString("affiliation", "").split("\\/");
            s.bio = profile.optString("bio", null);
            s.position = profile.optString("position", "").split("、");
            s.edu = profile.optString("edu", null);
            s.work = profile.optString("work", null);
            s.homepage = profile.optString("homepage", null);

            // parse tags
            if(!rawData.isNull("tags")) {
                s.tags = new HashMap<>();
                JSONArray tagList = rawData.getJSONArray("tags");
                JSONArray tagScore = rawData.getJSONArray("tags_score");
                for (int j = 0; j < tagList.length(); j++) {
                    s.tags.put(tagList.getString(j), tagScore.getInt(j));
                }
            }

            if (t != null)
                t.join();
            if (s.isAlive) {
                aliveScholars.put(s.id, s);
            } else {
                deadScholars.put(s.id, s);
            }
        }
    }
}
