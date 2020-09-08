package com.java.baohan.model;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Fts4;
import androidx.room.Ignore;
import androidx.room.Index;
import androidx.room.PrimaryKey;

import java.util.Date;

//@Fts4(languageId = "lid")
@Entity(tableName = "news_table")
//        indices = {@Index(value = {"rowid", "id"}, unique = true)})
public class News implements Comparable<News> {

    public static final int EN = 0;
    public static final int ZH = 1;

//    @PrimaryKey(autoGenerate = true)
//    @ColumnInfo(name = "rowid")
//    public int uid;

    @PrimaryKey
    @NonNull
    public String id;

    public Date time;

    public String title;

    public String content;

    public String source;

    public boolean isPaper;

    public boolean isRead = false;

    @ColumnInfo(name = "lid")
    public int languageId; // 0 for en, 1 for zh

    public News(String id, Date time, String title, String content, String source, boolean isPaper, int languageId, boolean isRead) {
        this.id = id;
        this.time = time;
        this.title = title;
        this.content = content;
        this.source = source;
        this.isPaper = isPaper;
        this.languageId = languageId;
        this.isRead = isRead;
    }

    @Ignore
    public News(String id, Date time, String title, String content, String source, boolean isPaper, int languageId) {
        this.id = id;
        this.time = time;
        this.title = title;
        this.content = content;
        this.source = source;
        this.isPaper = isPaper;
        this.languageId = languageId;
        this.isRead = false;
    }

    public boolean equals(News rhs) {
        return this.id.equals(rhs.id);
    }

    @Override
    public int compareTo(News rhs) {
        if (this.equals(rhs)) {
            return 0;
        }
        return time.compareTo(rhs.time);
    }

    @Override
    public String toString() {
        return title + " " + time + " " + id;
    }
}
