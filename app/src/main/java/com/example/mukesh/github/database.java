package com.example.mukesh.github;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by MUKESH on 10-Oct-15.
 */
public class database extends SQLiteOpenHelper {

    final static String Table_name = "Follow_table";
    final static String database_name = "Follow_database";
    final static String col1 = "Serial Number";
    final static String col2 = "User ID" ;

    public database(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, Table_name, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
