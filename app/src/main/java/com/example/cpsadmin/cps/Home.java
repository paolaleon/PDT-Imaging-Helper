package com.example.cpsadmin.cps;

import android.app.LoaderManager;
import android.content.ContentUris;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.example.cpsadmin.cps.data.PatientContract.PatientEntry;

public class Home extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final int PATIENT_LOADER = 0;
    private PatientCursorAdapter cursorAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        setTitle("Patient List");

        // initialize a floating action button that allows to add patients to the database
        FloatingActionButton add = (FloatingActionButton) findViewById(R.id.add_BT);
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent openRecord = new Intent(Home.this, Record.class);
                startActivity(openRecord);
            }
        });

        ListView patientListView = (ListView) findViewById(R.id.patientList);
        View emptyView = findViewById(R.id.emptyView);
        patientListView.setEmptyView(emptyView);

        // use a cursor adapter to open a patient record when selected in the list view
        cursorAdapter = new PatientCursorAdapter(this, null);
        patientListView.setAdapter(cursorAdapter);
        patientListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                Intent openRecord = new Intent(Home.this, Record.class);
                Uri currentPatientUri = ContentUris.withAppendedId(PatientEntry.CONTENT_URI, id);
                openRecord.setData(currentPatientUri);
                startActivity(openRecord);
            }
        });
        getLoaderManager().initLoader(PATIENT_LOADER, null, this);
    }

    /* onCreateLoader creates a loader that displays an overview of the patient in the home screen in the
       form of a ListView
     */
    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        String[] projection = {
                PatientEntry._ID,
                PatientEntry.COLUMN_PATIENT_NAME,
        };

        return new CursorLoader(this,
                PatientEntry.CONTENT_URI,
                projection,
                null,
                null,
                null);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        cursorAdapter.swapCursor(null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        cursorAdapter.swapCursor(data);
    }


}

