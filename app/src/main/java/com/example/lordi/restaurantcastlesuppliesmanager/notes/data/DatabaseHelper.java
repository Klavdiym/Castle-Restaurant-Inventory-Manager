package com.example.lordi.restaurantcastlesuppliesmanager.notes.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

class DatabaseHelper extends SQLiteOpenHelper {
    private static final String DBNAME = "notes.db";
    private static final int DBVERSION = 1;

    /**
     * Create a new SQLiteOpenHelper object for this database.
     * @param context the application context
     */
    DatabaseHelper(Context context) {
        super(context, DBNAME, null, DBVERSION);
    }

    /**
     * Called when the database needs to be created
     * @param db the database handle
     */
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(NotesContentContract.Notes.CREATE_SQLITE_TABLE);
    }

    /**
     * Called when the database needs to be updated
     * @param db the database handle
     * @param oldVersion the old database version
     * @param newVersion the new database version
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + DBNAME);
        onCreate(db);
    }
}
