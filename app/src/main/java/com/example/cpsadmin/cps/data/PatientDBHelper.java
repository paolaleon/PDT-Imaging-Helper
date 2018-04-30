package com.example.cpsadmin.cps.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import com.example.cpsadmin.cps.data.PatientContract.PatientEntry;

/**
 * Created by Paola Leon on 3/5/2018.
 * PatientDBHelper class creates the database for storing patient records
 */

public class PatientDBHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "pdt.db";
    private static final int DATABASE_VERSION = 1;

    public PatientDBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String SQL_CREATE_PATIENT_TABLE = "CREATE TABLE " + PatientEntry.TABLE_NAME + "("
                + PatientEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + PatientEntry.COLUMN_PATIENT_NAME + " TEXT NOT NULL, "
                + PatientEntry.COLUMN_PATIENT_BIRTHDATE + " TEXT NOT NULL, "
                + PatientEntry.COLUMN_PATIENT_SEX + " INTEGER NOT NULL, "
                + PatientEntry.COLUMN_PATIENT_COMMENTS + " TEXT, "
                + PatientEntry.COLUMN_PATIENT_IMAGES + " BLOB);";

        db.execSQL(SQL_CREATE_PATIENT_TABLE);

    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }
}
