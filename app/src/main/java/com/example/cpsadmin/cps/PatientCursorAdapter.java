package com.example.cpsadmin.cps;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;
import com.example.cpsadmin.cps.data.PatientContract.PatientEntry;

/**
 * Created by Paola Leon on 3/17/2018.
 * PatientCursorAdapter parses the information from the patient database and displays it in the Record window
 */

public class PatientCursorAdapter extends CursorAdapter {

    public PatientCursorAdapter(Context context, Cursor cursor) {
        super(context, cursor, 0);
    }

    /* newView inflates a new view once it has been created in the home window
    @params: current context, the cursor and the parent view group
    @return: a new entry in the ListView
     */
    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.list_item, parent, false);
    }

    /* bindView displays the patient information into the Record window EditText bodies
    @params: current view, context and the cursor
    @return: void
     */
    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        TextView TV_name = (TextView) view.findViewById(R.id.name);
        TextView TV_id = (TextView) view.findViewById(R.id.id);

        int nameColumnIndex = cursor.getColumnIndex(PatientEntry.COLUMN_PATIENT_NAME);
        int idColumnIndex = cursor.getColumnIndex(PatientEntry._ID);

        String patientName = cursor.getString(nameColumnIndex);
        String patientId = cursor.getString(idColumnIndex);

        TV_name.setText(patientName);
        TV_id.setText("ID: " + patientId);
    }

}
