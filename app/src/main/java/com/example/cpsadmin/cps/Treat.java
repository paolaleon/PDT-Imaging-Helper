package com.example.cpsadmin.cps;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class Treat extends AppCompatActivity {

    private Uri currentPatientUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_treat);

        Intent intent = getIntent();
        currentPatientUri = intent.getData();
        setTitle("Treatment");
    }

    /* treatDetail takes the user to the main treatment window.
       @params: the current View
       @return: void
    */
    public void treatDetail(View view) {
        Intent treatDetail = new Intent(this, treatDetail.class);
        treatDetail.setData(currentPatientUri);
        startActivity(treatDetail);
    }
}
