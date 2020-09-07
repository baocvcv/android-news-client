package com.java.baohan.model;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

@Dao
public interface NewsDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(News news);

    @Insert
    void insertAll(List<News> news);
//    void insertAll(News... news);

    @Delete
    void delete(News news);

    @Query("delete from news_table")
    void deleteAll();

    @Query("select rowid, id, time, title, content, source, isPaper, isRead, lid" +
            " from news_table order by time desc")
    LiveData<List<News>> getAll();

    @Query("select rowid, id, time, title, content, source, isPaper, isRead, lid" +
            " from news_table where isPaper = 0 and isRead = :isRead order by time desc")
    LiveData<List<News>> getAllNews(boolean isRead);

    @Query("select rowid, id, time, title, content, source, isPaper, isRead, lid" +
            " from news_table where isPaper = 0 order by time desc")
    LiveData<List<News>> getAllNews();

    @Query("select rowid, id, time, title, content, source, isPaper, isRead, lid" +
            " from news_table where isPaper = 1 and isRead = :isRead order by time desc")
    LiveData<List<News>> getAllPapers(boolean isRead);

    @Query("select rowid, id, time, title, content, source, isPaper, isRead, lid" +
            " from news_table where isPaper = 1 order by time desc")
    LiveData<List<News>> getAllPapers();

}
