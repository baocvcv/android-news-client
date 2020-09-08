package com.java.baohan.backend;

import android.app.Application;
import android.icu.util.Output;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.java.baohan.model.News;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.concurrent.ConcurrentHashMap;

public class NewsViewModel extends AndroidViewModel {

    private NewsRepository repo;
    private LiveData<List<News>> allNews;
    private LiveData<List<News>> allPapers;

    private Application app;

    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss");

    private static final String TAG_ID = "id";
    private static final String TAG_TIME = "date";
    private static final String TAG_TITLE = "title";
    private static final String TAG_CONTENT = "content";
    private static final String TAG_SOURCE = "source";
    private static final String TAG_TYPE = "type";
    private static final String TAG_LANG = "lang";

    private static final String URL_BASE = "https://covid-dashboard.aminer.cn/api/events/list?type=%s&page=%d&size=%d";
    private static final int UPDATE_SIZE = 50;
    private static final int RETRIEVE_OLD_SIZE = 37;
    private static int earliestPage = -1;

    private static final int SEARCH_RANGE = 1000;
    private static final int CACHE_STEP = 100;
    private static ConcurrentHashMap<String, News> recentNewsCache;

    private static final String SEARCH_HISTORY_FILE = "searchHistory.txt";
    private static List<String> searchHistory;

    /*
    Public methods
     */
    public NewsViewModel (Application app) {
        super(app);
        this.app = app;
        repo = new NewsRepository(app);
        allNews = repo.getAllNews();
        allPapers = repo.getAllPapers();

        recentNewsCache = new ConcurrentHashMap<>();
        // cache news for search
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    updateCache();
                } catch (Exception e) {}
            }
        }).start();

        // load search history
        searchHistory = new LinkedList<>();
        try (Scanner scanner = new Scanner(app.openFileInput(SEARCH_HISTORY_FILE))) {
            while (scanner.hasNextLine()) {
                searchHistory.add(scanner.nextLine());
            }
        } catch (Exception e) {}
    }

    public LiveData<List<News>> getAllPapers() {
        return allPapers;
    }

    public LiveData<List<News>> getAllNews() {
        return allNews;
    }

    public void updateNews() {
        updateNews(false);
    }

    public void updatePapers() {
        updateNews(true );
    }

    public void retrieveOldNews() { retrieveOld(false); }

    public void retrieveOldPapers() { retrieveOld(true); }

    public void saveNews(News news) {
        news.isRead = true;
        repo.insert(news);
    }

    public void markRead(News news) {
        news.isRead = true;
        repo.update(news);
    }

    public List<News> searchRecentNews(String keyword) {
        searchHistory.add(0, keyword);
        try (BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(app.openFileOutput(SEARCH_HISTORY_FILE, app.MODE_PRIVATE)))) {
            for (String s: searchHistory)
                writer.write(s + "\n");
        } catch (Exception e) {}

        List<News> result = new ArrayList<>();
        for (Map.Entry<String, News> entry: recentNewsCache.entrySet()) {
            if (entry.getValue().title.contains(keyword)) {
                result.add(entry.getValue());
            }
        }
        Collections.sort(result, (e1, e2) -> e2.compareTo(e1));
        return result;
    }

    public List<String> getSearchHistory() {
        return searchHistory;
    }

    /*
        Private methods
         */
    private void updateNews(boolean isPaper) {
        // retrieve news and save in database
        String type;
        if (isPaper) {
            type = "paper";
        } else {
            type = "news";
        }

        try {
            URL url = new URL(String.format(URL_BASE, type, 1, UPDATE_SIZE));
            List<News> newsList = parse(getInputStream(url));
            // TODO: currently simply insert 100 pieces of news, should change to
            // TODO: insert until an old news has been requested
            repo.insert(newsList);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void retrieveOld(boolean isPaper) {
        // retrieve old news and save in database
        String type;
        LiveData<List<News>> collection;
        if (isPaper) {
            collection = allPapers;
            type = "paper";
        } else {
            collection = allNews;
            type = "news";
        }

        int curSize = collection.getValue().size();
        int pageNum = (curSize + RETRIEVE_OLD_SIZE - 1) / RETRIEVE_OLD_SIZE;
        if (earliestPage < pageNum) {
            earliestPage = pageNum;
        }

        try {
            boolean hasNew = false;
            while(!hasNew) {
                URL url = new URL(String.format(URL_BASE, type, earliestPage, RETRIEVE_OLD_SIZE));
                List<News> newsList = parse(getInputStream(url));
                if (!collection.getValue().contains(newsList.get(newsList.size()-1))) {
                    // check if the earliest news is in the current collection
                    hasNew = true;
                }
                repo.insert(newsList);
                earliestPage++;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void updateCache() throws Exception {
        for (int i = 0; i < SEARCH_RANGE / CACHE_STEP; i++) {
            URL url = new URL(String.format(URL_BASE, "paper", i+1, CACHE_STEP));
            List<News> newsList = parse(getInputStream(url));
            for (News n: newsList) {
                recentNewsCache.putIfAbsent(n.id, n);
            }

            url = new URL(String.format(URL_BASE, "news", i+1, CACHE_STEP));
            newsList = parse(getInputStream(url));
            for (News n: newsList) {
                recentNewsCache.putIfAbsent(n.id, n);
            }
        }
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

    private List<News> parse(InputStream is) throws Exception {
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            sb.append(line);
        }
        reader.close();
        JSONObject jsonObject = new JSONObject(sb.toString());

        // parse jsonObject
        ArrayList<News> ret = new ArrayList<>();
        JSONArray data = jsonObject.getJSONArray("data");
        for(int i = 0; i < data.length(); i++) {
            JSONObject entry = data.getJSONObject(i);

            String id = entry.getString(TAG_ID);
            Date time;
            try {
                time = dateFormat.parse(entry.getString(TAG_TIME));
            } catch (Exception e) {
                time = new Date(12032093);
                System.out.println(entry);
            }
            String content = entry.getString(TAG_CONTENT);
            String title = entry.getString(TAG_TITLE);
            String source = entry.getString(TAG_SOURCE);
            String type = entry.getString(TAG_TYPE);
            boolean isPaper = type.equals("paper");
            String lang = entry.getString(TAG_LANG);
            int lid;
            if (lang.equals("zh")) {
                lid = News.ZH;
            } else {
                lid = News.EN;
            }

            ret.add(new News(id, time, title, content, source, isPaper, lid));
        }
        return ret;
    }
}
