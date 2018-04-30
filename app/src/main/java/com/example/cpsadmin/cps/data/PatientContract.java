package com.example.cpsadmin.cps.data;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by Paola Leon on 3/5/2018.
 * PatientContract class defines all constants for correct database functionality
 */

public final class PatientContract {

    public static final String CONTENT_AUTHORITY = "com.example.cpsadmin.cps";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);
    public static final String PATH_PATIENTS = "patients";

    public static abstract class PatientEntry implements BaseColumns {

        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_PATIENTS);
        public static final String TABLE_NAME = "patients";

        public static final String _ID = BaseColumns._ID;
        public static final String COLUMN_PATIENT_NAME = "name";
        public static final String COLUMN_PATIENT_BIRTHDATE = "birthdate";
        public static final String COLUMN_PATIENT_SEX = "sex";
        public static final String COLUMN_PATIENT_COMMENTS = "comments";
        public static final String COLUMN_PATIENT_IMAGES = "images";

        // definition of valid types
        public  static final String CONTENT_LIST_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PatientContract.PATH_PATIENTS;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PatientContract.PATH_PATIENTS;

        // possible values for sex
        public static final int SEX_UNDEFINED = 0;
        public static final int SEX_MALE = 1;
        public static final int SEX_FEMALE = 2;

        /* isValidSex checks for valid sex input
        @param: constant that defines sex
        @return: if the input is valid or not
         */
        public static boolean isValidSex(int sex) {
            if (sex == SEX_UNDEFINED || sex == SEX_MALE || sex == SEX_FEMALE)
                return true;
            else
                return false;
        }
    }
}
