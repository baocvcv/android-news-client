package com.java.baohan.backend;


import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import org.json.JSONArray;
import org.json.JSONObject;

import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class KnowledgeNode {

    @NonNull
    public String label;

    @NonNull
    public String url;

    @Nullable
    public String intro; // from "enwiki", "baidu" or "zhwiki", could be null

    public Map<String, String> properties; // "COVID": "Properties" { ... }

    public List<KnowledgeRelation> relations;

    public Bitmap img;

    public int hot;

    public  KnowledgeNode() {}

    public KnowledgeNode(String label, String url, String intro, Map<String, String> properties, List<KnowledgeRelation> relations, Bitmap img) {
        this.label = label;
        this.url = url;
        this.intro = intro;
        this.properties = properties;
        this.relations = relations;
        this.img = img;
    }

    public static KnowledgeNode parse(JSONObject jsonObject) {
        KnowledgeNode ret = new KnowledgeNode();

        try {
            ret.label = jsonObject.getString("label");
            ret.url = jsonObject.getString("url");

            double hot = jsonObject.getDouble("hot");
            if(hot > 0.7)
                ret.hot = 3;
            else if(hot > 0.4)
                ret.hot = 2;
            else
                ret.hot = 1;

            JSONObject info = jsonObject.getJSONObject("abstractInfo");
            // parse wiki
            String wiki;
            if (!(wiki = info.getString("enwiki")).isEmpty()) {
                ret.intro = wiki;
            } else if (!(wiki = info.getString("baidu")).isEmpty()) {
                ret.intro = wiki;
            } else if (!(wiki = info.getString("zhwiki")).isEmpty()) {
                ret.intro = wiki;
            }

            Thread t = null;
            if(!info.isNull("img")) {
                // load image async
                String imgURL = info.getString("img");
                t = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            ret.img = BitmapFactory.decodeStream(new URL(imgURL).openConnection().getInputStream());
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
                t.start();
            }

            // parse COVID
            JSONObject covid = info.getJSONObject("COVID");
            JSONObject rawProperties; // = covid.getJSONObject("properties");
//            if (!covid.isNull("properties")) {
            if((rawProperties = covid.getJSONObject("properties")).length() > 0) {
                ret.properties = new HashMap<>();
                for(Iterator<String> it = rawProperties.keys(); it.hasNext(); ) {
                    String key = it.next();
                    ret.properties.put(key, rawProperties.getString(key));
                }
            }

//            if (!covid.isNull("relations")) {
//                JSONArray rawRelations = covid.getJSONArray("relations");
            JSONArray rawRelations;
            if((rawRelations = covid.getJSONArray("relations")).length() > 0) {
                ret.relations = new LinkedList<>();
                for(int i = 0; i < rawRelations.length(); i++) {
                    JSONObject r = rawRelations.getJSONObject(i);
                    ret.relations.add(new KnowledgeRelation(r.getString("relation"), r.getString("url"), r.getString("label"), r.getBoolean("forward")));
                }
            }

            if (t != null) // wait for img to load
                t.join();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return ret;
    }

    @Override
    public String toString() {
        String ret = "Entity: " + label + " " + url + "\n";
        if (intro != null)
            ret += intro + "\n";
        if (properties != null)
            ret += "properties: " + properties.size() + "\n";
        if (relations != null)
            ret += "relations: " + relations.size() + "\n";
        return  ret;
    }
}
