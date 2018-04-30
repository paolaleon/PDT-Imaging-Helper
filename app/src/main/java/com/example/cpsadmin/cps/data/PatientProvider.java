package com.example.cpsadmin.cps.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.util.Log;

import com.example.cpsadmin.cps.data.PatientContract.PatientEntry;

/**
 * Created by Paola Leon on 3/10/2018.
 * PatientProvider class is the bridge between patient database and UI designed
 */

public class PatientProvider extends ContentProvider {

    private PatientDBHelper dbHelper;
    public static final String LOG_TAG = PatientProvider.class.getSimpleName();
    private static final int PATIENTS = 0;
    private static final int PATIENT_ID = 1;
    private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    static {
        sUriMatcher.addURI(PatientContract.CONTENT_AUTHORITY, "patients", PATIENTS);
        sUriMatcher.addURI(PatientContract.CONTENT_AUTHORITY, "patients/#", PATIENT_ID);
    }

    @Override
    public boolean onCreate() {
        dbHelper = new PatientDBHelper(getContext());
        return true;
    }

    /* query reads the information from the database
    @params: desired uri, desired fields, string selection, arguments and the sort order
    @return: cursor that contains all the information
     */
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor;

        int match = sUriMatcher.match(uri);
        switch (match) {
            case PATIENTS:
                cursor = db.query(PatientEntry.TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder);
                break;
            case PATIENT_ID:
                selection = PatientEntry._ID + "=?";
                selectionArgs = new String[] { String.valueOf(ContentUris.parseId(uri))};
                cursor = db.query(PatientEntry.TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder);
                break;
            default:
                throw new IllegalArgumentException("Cannot query unknown URI " + uri);
        }

        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }

    /* getType provides if the content is list or item type
    @params: desired uri
    @return: String that specifies either list or item type
     */
    @Override
    public String getType(Uri uri) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case PATIENTS:
                return PatientEntry.CONTENT_LIST_TYPE;
            case PATIENT_ID:
                return PatientEntry.CONTENT_ITEM_TYPE;
            default:
                throw new IllegalStateException("Unknown URI " + uri + " with match " + match);
        }
    }

    /* insert method allows to populate the database one record at a time
    @params: uri and content values that will populate the record
    @return: uri in which the content was inserted
     */
    @Override
    public Uri insert(Uri uri, ContentValues contentValues) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case PATIENTS:
                return insertPatient(uri, contentValues);
            default:
                throw new IllegalArgumentException("Insertion is not supported for " + uri);
        }
    }

    /* insertPatient method is a helper method that allows to populate patient database
    @params: uri and content values that will populate the record
    @return: uri in which the content was stored
     */
    private Uri insertPatient(Uri uri, ContentValues contentValues) {
        String name = contentValues.getAsString(PatientEntry.COLUMN_PATIENT_NAME);
        if (name == null) {
            throw new IllegalArgumentException("Patient requires a name");
        }

        Integer sex = contentValues.getAsInteger(PatientEntry.COLUMN_PATIENT_SEX);
        if (sex == null || !PatientEntry.isValidSex(sex)) {
            throw new IllegalArgumentException("Patient requires a valid sex");
        }

        String birthdate = contentValues.getAsString(PatientEntry.COLUMN_PATIENT_BIRTHDATE);
            if (!birthdate.matches("[/-9]+")) {
                throw new IllegalArgumentException("Patient requires a valid birthdate");
        }

        SQLiteDatabase db = dbHelper.getWritableDatabase();
        long id = db.insert(PatientEntry.TABLE_NAME, null, contentValues);

        if (id == -1) {
            Log.e(LOG_TAG, "Failed to insert row for " + uri);
            return null;
        }

        getContext().getContentResolver().notifyChange(uri, null);
        return ContentUris.withAppendedId(uri, id);
    }

    /* delete allows to erase records from the database permanently
    @params: uri, selection string and the arguments
    @return: integer that defines the position of the cursor after erasing
     */
    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        getContext().getContentResolver().notifyChange(uri, null);
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case PATIENTS:
                return db.delete(PatientEntry.TABLE_NAME, selection, selectionArgs);
            case PATIENT_ID:
                selection = PatientEntry._ID + "=?";
                selectionArgs = new String[] { String.valueOf(ContentUris.parseId(uri)) };
                return db.delete(PatientEntry.TABLE_NAME, selection, selectionArgs);
            default:
                throw new IllegalArgumentException("Deletion is not supported for " + uri);
        }
    }

    /* update allows to modify records that already exist in the database
    @params: uri, content values that are being modified, selection and arguments
    @return: integer that defines the position of the cursor after modifying
     */
    @Override
    public int update(Uri uri, ContentValues contentValues, String selection, String[] selectionArgs) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case PATIENTS:
                return updatePatient(uri, contentValues, selection, selectionArgs);
            case PATIENT_ID:
                selection = PatientEntry._ID + "=?"; //change later to name
                selectionArgs = new String[] { String.valueOf(ContentUris.parseId(uri)) };
                return updatePatient(uri, contentValues, selection, selectionArgs);
            default:
                throw new IllegalArgumentException("Update is not supported for " + uri);
        }
    }

    /*  updatePatient is a helper method that allows to modify information already present in the database
    @params: current uri, modified content values, selection and arguments
    @return: integer that defines the position of the cursor
     */
    private int updatePatient(Uri uri, ContentValues contentValues, String selection, String[] selectionArgs) {
        if (contentValues.containsKey(PatientEntry.COLUMN_PATIENT_NAME)) {
            String name = contentValues.getAsString(PatientEntry.COLUMN_PATIENT_NAME);
            if (name == null) {
                throw new IllegalArgumentException("Patient requires a name");
            }
        }

        if (contentValues.containsKey(PatientEntry.COLUMN_PATIENT_SEX)) {
            Integer sex = contentValues.getAsInteger(PatientEntry.COLUMN_PATIENT_SEX);
            if (sex == null || !PatientEntry.isValidSex(sex)) {
                throw new IllegalArgumentException("Patient requires a valid sex");
            }
        }

        if (contentValues.containsKey(PatientEntry.COLUMN_PATIENT_BIRTHDATE)) {
            String birthdate = contentValues.getAsString(PatientEntry.COLUMN_PATIENT_BIRTHDATE);
            if (!birthdate.matches("[/-9]+")) {
                throw new IllegalArgumentException("Patient requires a valid birthdate");
            }
        }

        if (contentValues.size() == 0) {
            return 0;
        }

        getContext().getContentResolver().notifyChange(uri, null);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        return db.update(PatientEntry.TABLE_NAME, contentValues, selection, selectionArgs);
    }
}
