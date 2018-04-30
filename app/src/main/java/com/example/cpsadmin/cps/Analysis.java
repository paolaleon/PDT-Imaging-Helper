package com.example.cpsadmin.cps;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.SeekBar;
import android.widget.Toast;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import com.example.cpsadmin.cps.image_processing.Image;

import org.opencv.android.OpenCVLoader;
import org.opencv.core.Mat;

public class Analysis extends AppCompatActivity implements PopupMenu.OnMenuItemClickListener {

    private ImageView image;
    private Uri currentPatientUri;
    private static final int PERMISSION_REQUEST_CODE = 1;
    private SeekBar contrastSeekBar, brightnessSeekBar;


    /* DEVICE METHODS
     * on create
     * save image to device */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_analysis);

        image = findViewById(R.id.rawImage);
        contrastSeekBar = findViewById(R.id.contrastSeekBar);
        brightnessSeekBar = findViewById(R.id.brightnessSeekBar);

        if (!OpenCVLoader.initDebug())
            Log.e(this.getClass().getSimpleName(), "  OpenCVLoader.initDebug(), not working.");
        else
            Log.i(this.getClass().getSimpleName(), "  OpenCVLoader.initDebug(), working.");

        Intent intent = getIntent();
        currentPatientUri = intent.getData();
        Bundle extra = intent.getExtras();
        Uri imageUri;

        if (extra != null) {
            if (intent.hasCategory("import")) {
                imageUri = Uri.parse(getIntent().getStringExtra("imageUri"));
                image.setImageURI(imageUri);
                image.setRotation(90);
            }
        }

        setTitle("Image Analysis"); //+ currentPatientUri);
    }


    /* saveImageToDevice saves the current image displayed to a directory in the internal memory
    @params: the image's bitmap, the name of the file that will be saved
    @return: void
     */

    public void saveImageToDevice(Bitmap bitmap, String filename) {
        File sdCard = Environment.getExternalStorageDirectory();
        File dir = new File(sdCard.getAbsolutePath()+ "/PDT Imaging Helper");
        dir.mkdirs();
        File outFile = new File(dir, filename);
        boolean success = false;
        FileOutputStream outStream;
        try {
            outStream = new FileOutputStream(outFile);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outStream);
            outStream.flush();
            outStream.close();
            success = true;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (success) {
            Toast.makeText(getApplicationContext(), "Image Saved Successfully", Toast.LENGTH_LONG).show();
            Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
            intent.setData(Uri.fromFile(outFile));
            sendBroadcast(intent);

        } else {
            Toast.makeText(getApplicationContext(),
                    "Error While Saving Image", Toast.LENGTH_LONG).show();
        }
    }



    /* POPUP MENU HANDLERS
     * customize options button
     * save image options
     * switch statement to generate desired result */


    /* customizeOptions shows a popup of the available image processing functions
    @params: the current view
    @return void
     */
    public void customizeOptions(View view) {
        contrastSeekBar.setVisibility(View.INVISIBLE);
        brightnessSeekBar.setVisibility(View.INVISIBLE);
        PopupMenu popup = new PopupMenu(this, view);
        popup.setOnMenuItemClickListener(this);
        popup.inflate(R.menu.lookup_tables);
        popup.show();
    }

    /* saveImageOptions shows a popup of the available saving options
    @params: the current view
    @return: void
     */
    public void saveImageOptions(View view) {
        PopupMenu popup = new PopupMenu(this, view);
        popup.setOnMenuItemClickListener(this);
        popup.inflate(R.menu.save_options);
        popup.show();
    }


    @Override
    public boolean onMenuItemClick(MenuItem menuItem) {
        Mat rawMat;
        Mat processedMat;
        switch (menuItem.getItemId()) {
            case R.id.redChannel:
                rawMat = Image.imageToMat(image);
                processedMat = Image.getChannel(rawMat, 0);
                Image.matToImage(processedMat, image);
                return true;
            case R.id.sixteenColors:
                rawMat = Image.imageToMat(this.image);
                Mat redMat = Image.getChannel(rawMat, 0);
                processedMat = Image.sixteenColors(redMat);
                Image.matToImage(processedMat, image);
                return true;
            case R.id.contrast:
                contrastSeekBar.setVisibility(View.VISIBLE);
                contrastSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                    Mat rawMat = Image.imageToMat(image);
                    @Override
                    public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                        Mat processedMat = Image.adjustContrast(rawMat, Image.getAlpha(i));
                        Image.matToImage(processedMat, image);
                    }
                    @Override
                    public void onStartTrackingTouch(SeekBar seekBar) {

                    }

                    @Override
                    public void onStopTrackingTouch(SeekBar seekBar) {

                    }
                });
                return true;
            case R.id.brightness:
                brightnessSeekBar.setVisibility(View.VISIBLE);
                brightnessSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                    Mat rawMat = Image.imageToMat(image);
                    @Override
                    public void onProgressChanged(SeekBar seekBar, int progress, boolean b) {
                        Log.e("value", "Progress is " + progress);
                        Mat processedMat = Image.adjustBrightness(rawMat, progress);
                        Image.matToImage(processedMat, image);
                    }
                    @Override
                    public void onStartTrackingTouch(SeekBar seekBar) {

                    }

                    @Override
                    public void onStopTrackingTouch(SeekBar seekBar) {

                    }
                });
                return true;
            case R.id.toDevice:
                if (Build.VERSION.SDK_INT >= 23) {
                    if (checkPermission()) {
                        Log.e("value", "Permission already Granted, Now you can save image.");
                    } else {
                        requestPermission();
                    }
                } else {
                    Log.e("value", "Not required for requesting runtime permission");
                }
                BitmapDrawable drawable = (BitmapDrawable) image.getDrawable();
                Bitmap finalBitmap = drawable.getBitmap();
                String filename = System.currentTimeMillis() + "_img.jpg";
                saveImageToDevice(finalBitmap, filename);
                return true;
            //case R.id.toPatientRecord:
              //  return true;
            default:
                return false;

        }
    }

    /* PERMISSIONS FOR WRITE EXTERNAL STORAGE
     * check permission
     * request permission
     * report result */
    private boolean checkPermission() {
        int result = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (result == PackageManager.PERMISSION_GRANTED) {
            return true;
        } else {
            return false;
        }
    }

    private void requestPermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            Toast.makeText(this, "Write External Storage permission allows us to do store images. Please allow this permission in App Settings.", Toast.LENGTH_LONG).show();
        } else {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSION_REQUEST_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_REQUEST_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.e("value", "Permission Granted, Now you can save image .");
                } else {
                    Log.e("value", "Permission Denied, You cannot save image.");
                }
                break;
        }
    }
}

