package com.samdunkley.android.popularmovies.background;

import android.os.Handler;
import android.os.Looper;

import androidx.annotation.NonNull;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class DBExecutors {

    private static final Object LOCK = new Object();
    private static DBExecutors instance;
    private final Executor diskIO;

    private DBExecutors(Executor diskIO) {
        this.diskIO = diskIO;

    }

    public static DBExecutors getInstance() {
        if (instance == null) {
            synchronized (LOCK) {
                instance = new DBExecutors(Executors.newSingleThreadExecutor());
            }
        }
        return instance;
    }

    public Executor diskIO() {
        return diskIO;
    }

}