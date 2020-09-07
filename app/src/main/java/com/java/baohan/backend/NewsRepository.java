package com.java.baohan.backend;

import android.app.Application;

import androidx.lifecycle.LiveData;

import com.java.baohan.model.News;
import com.java.baohan.model.NewsDao;
import com.java.baohan.model.NewsDatabase;

import java.util.List;

public class NewsRepository {

    private NewsDao newsDao;
    private LiveData<List<News>> allNews;
    private LiveData<List<News>> allPapers;

    public NewsRepository(Application app) {
        NewsDatabase db = NewsDatabase.getDatabase(app);
        newsDao = db.newsDao();
        allNews = newsDao.getAllNews();
        allPapers = newsDao.getAllPapers();
    }

    public LiveData<List<News>> getAllNews() {
        return allNews;
    }

    public LiveData<List<News>> getAllPapers() {
        return allPapers;
    }

    void insert(News news) {
        NewsDatabase.databaseWriteExecutor.execute(() -> {
            newsDao.insert(news);
        });
    }

//    void insert(News... news) {
    void insert(List<News> news) {
        NewsDatabase.databaseWriteExecutor.execute(() -> {
            newsDao.insertAll(news);
        });
    }
}
