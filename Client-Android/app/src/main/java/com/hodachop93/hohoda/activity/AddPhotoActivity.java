package com.hodachop93.hohoda.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.Window;


import com.hodachop93.hohoda.R;
import com.hodachop93.hohoda.eventbus.PhotoIntentEventBus;
import com.hodachop93.hohoda.utils.ImageHelper;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class AddPhotoActivity extends Activity {

    private static final int CAMERA_CAPTURE_IMAGE_REQUEST_CODE = 101;
    private static final int GALLERY_PICKER_REQUEST_CODE = 102;
    private static final int CROP_IMAGE_REQUEST_CODE = 103;

    private Uri captureUri;
    private boolean isDialogShowing = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState != null) {

            if (savedInstanceState.containsKey("URI_CAPTURE"))
                captureUri = savedInstanceState.getParcelable("URI_CAPTURE");

            isDialogShowing = savedInstanceState.getBoolean("DIALOG_SHOW",
                    false);
        }

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setBackgroundDrawable(new ColorDrawable(0));

        if (!isDialogShowing)
            addImage();

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        System.out.println("onSaveInstanceState onSaveInstanceState");

        outState.putParcelable("URI_CAPTURE", captureUri);
        outState.putBoolean("DIALOG_SHOW", isDialogShowing);
    }

    public static void copyStream(InputStream input, OutputStream output)
            throws IOException {

        byte[] buffer = new byte[1024];
        int bytesRead;
        while ((bytesRead = input.read(buffer)) != -1) {
            output.write(buffer, 0, bytesRead);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode != RESULT_OK) {

            System.out.println("Result cancelled " + resultCode);

            finish();
            return;
        }

        Bitmap photo = null;

        if (data != null) {
            Bundle extras = data.getExtras();
            if (extras != null) {
                photo = extras.getParcelable("data");

            }
        }

        switch (requestCode) {

            case GALLERY_PICKER_REQUEST_CODE:

                try {
                    photo = ImageHelper.decodeUri(this, captureUri, 256, 256);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }

                PhotoIntentEventBus.getInstance().postSticky(new PhotoIntentEventBus.Event(photo));
                finish();

                break;

            case CAMERA_CAPTURE_IMAGE_REQUEST_CODE:

                System.out.println("Photo taken " + photo);

                if (!performCrop()) {
                    try {
                        photo = ImageHelper.decodeUri(this, captureUri, 256, 256);
                    } catch (FileNotFoundException e) {
                        photo = null;
                    }

                    PhotoIntentEventBus.getInstance().postSticky(new PhotoIntentEventBus.Event(photo));
                    finish();
                }

                break;

            case CROP_IMAGE_REQUEST_CODE:

                System.out.println("Cropped " + photo);

                PhotoIntentEventBus.getInstance().postSticky(new PhotoIntentEventBus.Event(photo));
                finish();

                break;
        }

    }

    @Override
    public void finish() {
        super.finish();

        File file = new File(captureUri.getPath());
        if (file.delete()) {
            System.out.println("temp file deleted");
        } else {
            System.out.println("temp file not deleted");
        }
    }

    private boolean performCrop() {
        try {

            Intent cropIntent = getCropImageIntent();

            cropIntent.setDataAndType(captureUri, "image/*");
            cropIntent.putExtra("crop", "true");
            cropIntent.putExtra("aspectX", 1);
            cropIntent.putExtra("aspectY", 1);

            cropIntent.putExtra("outputX", 256);
            cropIntent.putExtra("outputY", 256);

            cropIntent.putExtra("return-data", true);

            startActivityForResult(cropIntent, CROP_IMAGE_REQUEST_CODE);

            return true;
        } catch (ActivityNotFoundException anfe) {
            return false;
        }
    }

    /******************
     * Add photo
     **********************/

    private void addImage() {

        isDialogShowing = true;

        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setCancelable(false);
        builder.setTitle(R.string.alert_addphoto);
        builder.setItems(R.array.dialog_camera,
                new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int item) {

                        captureUri = ImageHelper
                                .getOutputMediaFileUri();

                        switch (item) {

                            case 0:// take photo

                                startCameraIntent();
                                break;

                            case 1: // choose from gallery

                                startGalleryIntent();
                                break;

                            default:// cancel

                                dialog.dismiss();
                                finish();
                                break;
                        }
                    }

                });

        builder.show();
    }

    private void startGalleryIntent() {
        Intent pickImageIntent = new Intent();
        // call android default gallery
        pickImageIntent.setType("image/*");
        pickImageIntent.setAction(Intent.ACTION_GET_CONTENT);

        pickImageIntent.putExtra("crop", "true");
        pickImageIntent.putExtra("outputX", 256);
        pickImageIntent.putExtra("outputY", 256);
        pickImageIntent.putExtra("aspectX", 1);
        pickImageIntent.putExtra("aspectY", 1);
        pickImageIntent.putExtra(MediaStore.EXTRA_OUTPUT, captureUri);
        pickImageIntent.putExtra("scale", true);
        pickImageIntent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());

        startActivityForResult(pickImageIntent, GALLERY_PICKER_REQUEST_CODE);
    }

    private void startCameraIntent() {

        Intent takePicIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        takePicIntent.putExtra("outputX", 256);
        takePicIntent.putExtra("outputY", 256);
        takePicIntent.putExtra("return-data", true);
        takePicIntent.putExtra(MediaStore.EXTRA_OUTPUT, captureUri);
        takePicIntent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());

        startActivityForResult(takePicIntent, CAMERA_CAPTURE_IMAGE_REQUEST_CODE);
    }

    public Intent getCropImageIntent() {
        return new Intent("com.android.camera.action.CROP");
    }
}
