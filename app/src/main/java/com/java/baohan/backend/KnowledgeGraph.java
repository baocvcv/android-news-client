package com.java.baohan.backend;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.LinkedList;
import java.util.List;

public class KnowledgeGraph {

    private static final String URL_BASE = "https://innovaapi.aminer.cn/covid/api/v1/pneumonia/entityquery?entity=%s";

    /*
    Returns null if no search result
     */
    public static List<KnowledgeNode> search(String entity) {
        List<KnowledgeNode> ret = null;
        try {
            URL url = new URL(String.format(URL_BASE, entity));
            InputStream is = getInputStream(url);
            BufferedReader reader = new BufferedReader(new InputStreamReader(is));
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
            reader.close();
            JSONObject jsonObject = new JSONObject(sb.toString());

            String msg = jsonObject.getString("msg");
            if (!msg.equals("success")) {
                return null;
            }

            ret = new LinkedList<>();
            JSONArray rawDataArray = jsonObject.getJSONArray("data");
            for (int i = 0; i < rawDataArray.length(); i++) {
                JSONObject rawData = rawDataArray.getJSONObject(i);
                ret.add(KnowledgeNode.parse(rawData));
            }
        } catch (Exception e) {
            return null;
        }

        return ret;
    }

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

}
