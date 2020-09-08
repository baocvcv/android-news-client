package com.java.baohan.model;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;
import androidx.sqlite.db.SupportSQLiteDatabase;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Database(entities = {News.class}, version = 3, exportSchema = false)
@TypeConverters({NewsConverters.class})
public abstract class NewsDatabase extends RoomDatabase {

    public abstract NewsDao newsDao();

    private static volatile NewsDatabase INSTANCE;
    private static final int NUMBER_OF_THREADS = 4;
    public static final ExecutorService databaseWriteExecutor =
            Executors.newFixedThreadPool(NUMBER_OF_THREADS);

    public static NewsDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (NewsDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                            NewsDatabase.class, "news_database")
                            .addCallback(sNewsDatabaseCallback)
                            .fallbackToDestructiveMigration()
                            .build();
                }
            }
        }
        return INSTANCE;
    }

    private static NewsDatabase.Callback sNewsDatabaseCallback = new RoomDatabase.Callback() {

        @Override
        public void onOpen(@NonNull SupportSQLiteDatabase db) {
            super.onOpen(db);
            databaseWriteExecutor.execute(() -> {
                //TODO: change start up code
                System.out.println("*************************************reached!");
                NewsDao dao = INSTANCE.newsDao();
//                dao.deleteAll();
            });
        }
    };
}
