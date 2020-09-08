package com.java.baohan.model;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Index;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

@Dao
public interface NewsDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insert(News news);

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insertAll(List<News> news);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void update(News news);

    @Delete
    void delete(News news);

    @Query("delete from news_table")
    void deleteAll();

    //TODO: remove rowid if News model is confirmed
    @Query("select rowid, id, time, title, content, source, isPaper, isRead, lid " +
            "from news_table " +
            "order by time desc")
    LiveData<List<News>> getAll();

    @Query("select rowid, id, time, title, content, source, isPaper, isRead, lid " +
            "from news_table " +
            "where isPaper = 0 and isRead = :isRead " +
            "order by time desc")
    LiveData<List<News>> getAllNews(boolean isRead);

    @Query("select rowid, id, time, title, content, source, isPaper, isRead, lid " +
            "from news_table " +
            "where isPaper = 0 " +
            "order by time desc")
    LiveData<List<News>> getAllNews();

    @Query("select rowid, id, time, title, content, source, isPaper, isRead, lid " +
            "from news_table " +
            "where isPaper = 1 and isRead = :isRead " +
            "order by time desc")
    LiveData<List<News>> getAllPapers(boolean isRead);

    @Query("select rowid, id, time, title, content, source, isPaper, isRead, lid " +
            "from news_table " +
            "where isPaper = 1 " +
            "order by time desc")
    LiveData<List<News>> getAllPapers();

    @Query("select id, time, title, content, source, isPaper, isRead, lid " +
            "from news_table " +
            "where title match :keyword or content match :keyword " +
            "order by time desc")
    LiveData<List<News>> searchWord(String keyword);

    @Query("select id, time, title, content, source, isPaper, isRead, lid " +
            "from news_table " +
            "where title match :keyword or content match :keyword " +
            "order by time desc")
    List<News> searchWordSync(String keyword);
}
