package com.java.baohan.model;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.util.Date;

public class Paper {
    @PrimaryKey
    public int uid;

    @ColumnInfo(name = "id")
    public String id;

    @ColumnInfo(name = "time")
    public Date time;

    @ColumnInfo(name = "title")
    public String title;

    @ColumnInfo(name = "content")
    public String content;

    @ColumnInfo(name = "source")
    public String source;
}
