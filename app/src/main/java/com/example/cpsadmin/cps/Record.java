package com.example.cpsadmin.cps;

import android.app.AlertDialog;
import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.view.View;
import android.widget.PopupMenu;
import android.widget.Spinner;
import android.widget.Toast;
import com.example.cpsadmin.cps.data.PatientContract.PatientEntry;
import java.io.IOException;

public class Record extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor>, PopupMenu.OnMenuItemClickListener {

    private EditText ET_name, ET_birthdate, ET_comments;
    private Spinner SP_sex;
    private int sex;

    private static final int EXISTING_PATIENT_LOADER = 0;
    private final int PICK_IMAGE = 1;
    private final int CAPTURE_IMAGE = 2;
    private boolean gotPicture = false;

    private Uri currentPatientUri, uri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record);

        if (gotPicture) {
            Intent openAnalysis = new Intent(this, Analysis.class);
            openAnalysis.addCategory("capture");
            gotPicture = false;
            startActivity(openAnalysis);
        }

        Intent intent = getIntent();
        currentPatientUri = intent.getData();

        Button BT_delete = (Button) findViewById(R.id.delete_BT);
        Button BT_diagnose = (Button) findViewById(R.id.diagnose_BT);
        Button BT_treat = (Button) findViewById(R.id.treat_BT);

        if (currentPatientUri == null) {
            setTitle("Add Patient");
            BT_delete.setVisibility(View.INVISIBLE);
            BT_diagnose.setVisibility(View.INVISIBLE);
            BT_treat.setVisibility(View.INVISIBLE);
        } else {
            setTitle("Edit Patient");
            getLoaderManager().initLoader(EXISTING_PATIENT_LOADER, null, this);
            BT_delete.setVisibility(View.VISIBLE);
            BT_diagnose.setVisibility(View.VISIBLE);
            BT_treat.setVisibility(View.VISIBLE);

        }

        ET_name = (EditText) findViewById(R.id.name_ET);
        ET_birthdate = (EditText) findViewById(R.id.birthdate_ET);
        ET_comments = (EditText) findViewById(R.id.medicalHistory_ET);
        SP_sex = (Spinner) findViewById(R.id.sex_SP);
        SP_sex.setSelection(PatientEntry.SEX_UNDEFINED);

        setupSpinner();

    }

    /* setupSpinner initializes the spinner to select the patient's sex
    @params: none
    @return: void
     */
    public void setupSpinner() {
        ArrayAdapter sexSpinnerAdapter = ArrayAdapter.createFromResource(this, R.array.sex_array,
                android.R.layout.simple_spinner_item);
        sexSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);

        SP_sex.setAdapter(sexSpinnerAdapter);
        SP_sex.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selection = (String) parent.getItemAtPosition(position);
                if (!TextUtils.isEmpty(selection)) {
                    if (selection.equals(getString(R.string.sex_male))) {
                        sex = PatientEntry.SEX_MALE;
                    } else if (selection.equals(getString(R.string.sex_female))) {
                        sex = PatientEntry.SEX_FEMALE;
                    } else {
                        sex = PatientEntry.SEX_UNDEFINED;
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                sex = PatientEntry.SEX_UNDEFINED;
            }
        });
    }

    /* save saves the information currently displayed on screen
    @params: the current view
    @return: void
     */
    public void save(View view) {
        savePatient();
        finish();
    }

    /* savePatient is a helper method that saves the current state of the EditText bodies into the database
    @params: none
    @return: void
    */
    public void savePatient() {
        String name = ET_name.getText().toString().trim();
        String birthdate = ET_birthdate.getText().toString().trim();
        String comments = ET_comments.getText().toString().trim();

        if (currentPatientUri == null && TextUtils.isEmpty(name) && TextUtils.isEmpty(birthdate)
                && TextUtils.isEmpty(comments) && sex == PatientEntry.SEX_UNDEFINED) {
            return;
        }

        ContentValues values = new ContentValues();
        values.put(PatientEntry.COLUMN_PATIENT_NAME, name);
        values.put(PatientEntry.COLUMN_PATIENT_BIRTHDATE, birthdate);
        values.put(PatientEntry.COLUMN_PATIENT_SEX, sex);
        values.put(PatientEntry.COLUMN_PATIENT_COMMENTS, comments);


        if (currentPatientUri == null) {
            Uri newUri = getContentResolver().insert(PatientEntry.CONTENT_URI, values);

            if (newUri == null) {
                Toast.makeText(this, "Error while adding patient", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Patient added successfully", Toast.LENGTH_SHORT).show();
            }
        } else {
            int rowsAffected = getContentResolver().update(currentPatientUri, values, null, null);

            if (rowsAffected == 0) {
                Toast.makeText(this, "Error while updating patient", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Information updated successfully", Toast.LENGTH_SHORT).show();
            }
        }

    }

    /* delete deletes the current record from the database permanently
    @params: the current view
    @return: void
     */
    public void delete(View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Delete patient permanently?");
        builder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                deletePatient();
            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (dialogInterface != null) {
                    dialogInterface.dismiss();
                }
            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    /* deletePatient is a helper method that deletes the current record permanently
    @params: none
    @return: void
     */
    private void deletePatient() {
        if (currentPatientUri != null) {
            int rowsDeleted = getContentResolver().delete(currentPatientUri, null, null);

            if (rowsDeleted == 0) {
                Toast.makeText(this, "Error while deleting patient", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Patient deleted successfully", Toast.LENGTH_SHORT).show();
            }
        }
        finish();
    }

    /* OnCreateLoader selects the data from the database that will be displayed in the EditText bodies
    @params: cursor position and the bundle
    @return: a loader of cursors
     */
    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        String[] projection = {
                PatientEntry._ID,
                PatientEntry.COLUMN_PATIENT_NAME,
                PatientEntry.COLUMN_PATIENT_BIRTHDATE,
                PatientEntry.COLUMN_PATIENT_SEX,
                PatientEntry.COLUMN_PATIENT_COMMENTS,
                //TODO: ADD FIELD FOR IMAGES
        };

        return new CursorLoader(this,
                currentPatientUri,
                projection,
                null,
                null,
                null);
    }

    /* onLoadFinished displays the information from the database into the EditText bodies
    @params: a cursor loader and the cursor
    @return: void
     */
    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        if (cursor == null || cursor.getCount() < 1) {
            return;
        }

        if (cursor.moveToFirst()) {
            int nameColumnIndex = cursor.getColumnIndex(PatientEntry.COLUMN_PATIENT_NAME);
            int birthdateColumnIndex = cursor.getColumnIndex(PatientEntry.COLUMN_PATIENT_BIRTHDATE);
            int sexColumnIndex = cursor.getColumnIndex(PatientEntry.COLUMN_PATIENT_SEX);
            int commentsColumnIndex = cursor.getColumnIndex(PatientEntry.COLUMN_PATIENT_COMMENTS);
            //TODO: MOVE CURSOR TO IMAGES GRID
            //  int imagesColumnIndex = cursor.getColumnIndex(PatientEntry.COLUMN_PATIENT_IMAGES);

            String name = cursor.getString(nameColumnIndex);
            String birthdate = cursor.getString(birthdateColumnIndex);
            int sex = cursor.getInt(sexColumnIndex);
            String comments = cursor.getString(commentsColumnIndex);
            //TODO: GET IMAGES
            //add a field for images

            ET_name.setText(name);
            ET_birthdate.setText(birthdate);
            ET_comments.setText(comments);

            switch (sex) {
                case PatientEntry.SEX_MALE:
                    SP_sex.setSelection(1);
                    break;
                case PatientEntry.SEX_FEMALE:
                    SP_sex.setSelection(2);
                    break;
                default:
                    SP_sex.setSelection(0);
                    break;
            }
        }
    }

    /* onLoaderReset resets the loader to a default position
    @params: a cursor loader
    @return: void
     */
    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        ET_name.setText("");
        ET_birthdate.setText("");
        ET_comments.setText("");
        SP_sex.setSelection(0);
    }

    /* openTreat sends the user to the main treatment window
    @params: the current view
    @return: void
     */
    public void openTreat(View view) {
        Intent openTreat = new Intent(this, Treat.class);
        openTreat.setData(currentPatientUri);
        startActivity(openTreat);
    }

    /* openDiagnoseMenu shows a popup with the available diagnose options
    @params: the current view
    @return: void
     */
    public void openDiagnoseMenu(View view) {
        PopupMenu popup = new PopupMenu(this, view);
        popup.setOnMenuItemClickListener(this);
        popup.inflate(R.menu.options);
        popup.show();
    }

    /* onMenuItemClick initializes the possible diagnose options: by importing or by capturing
    @params: desired menu item
    @return: boolean
     */
    public boolean onMenuItemClick(MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case R.id.importing:
                // Diagnose by importing image to the application
                Intent importImage = new Intent();
                importImage.setData(currentPatientUri);
                importImage.setType("image/*");
                importImage.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(importImage, "Select Picture"), PICK_IMAGE);
                return true;
            case R.id.capturing:
                // Diagnose by capturing image with the endoscopy camera. Opens usbcameratest4, an helper app that previews the camera
                PackageManager manager = this.getPackageManager();
                Intent captureImage = manager.getLaunchIntentForPackage("com.serenegiant.usbcameratest4");
                captureImage.addCategory(Intent.CATEGORY_LAUNCHER);
                startActivityForResult(captureImage, CAPTURE_IMAGE);
                return true;
            default:
                return false;
        }
    }

    /* onActivityResult performs an action after a result from the intent has been obtained
    @params: the request code, the result code and the intent
    @return: void
     */
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        // pick an image from the device's gallery and display it in the Analysis window
        if (requestCode == PICK_IMAGE && resultCode == RESULT_OK) {// && data != null && data.getData() != null) {
            uri = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
                Intent openAnalysis = new Intent(this, Analysis.class);
                openAnalysis.setData(uri);
                openAnalysis.addCategory("import");
                openAnalysis.putExtra("imageUri", uri.toString());
                startActivity(openAnalysis);
            } catch (IOException e) {
                e.printStackTrace();
            }

        //  take an image with the endoscopy camera and display in the Analysis window
        } //else if (requestCode == CAPTURE_IMAGE && resultCode == RESULT_OK) { // && data == null) {
            //gotPicture = true;
        //}
    }
}