package com.jaeyang.imaginecanvas;
/*
 * The drawing Activity
 */


import android.app.Dialog;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.io.FileNotFoundException;

public class DrawingActivity extends AppCompatActivity implements View.OnClickListener{
    private DrawingView drawView;
    private ImageButton currPaint, brushBtn, eraseBtn; //the current paint view
    //three dimension values
    private float smallBrush, mediumBrush, largeBrush;


    private static int RESULT_LOAD_IMG = 1;
    String imgDecodableString;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drawing);

        drawView = (DrawingView) findViewById(R.id.drawing);
        brushBtn = (ImageButton) findViewById(R.id.brush_btn);
        brushBtn.setOnClickListener(this);
        eraseBtn = (ImageButton) findViewById(R.id.erase_btn);
        eraseBtn.setOnClickListener(this);

        //instantiate them
        smallBrush = getResources().getInteger(R.integer.small_size);
        mediumBrush = getResources().getInteger(R.integer.medium_size);
        largeBrush = getResources().getInteger(R.integer.large_size);


        //Retrieving the first paint color
        LinearLayout paintLayout = (LinearLayout) findViewById(R.id.paint_colors);
        //get the first button and store it as the instance variable
        currPaint = (ImageButton) paintLayout.getChildAt(0);
        //this will set the first button as pressed
        currPaint.setImageDrawable(getResources().getDrawable(R.drawable.paint_pressed));

        drawView.setBrushSize(smallBrush);

    }

    /*
     * This method runs when the paint pallet is pressed
     */
    public void paintClicked(View view){
        //check is the clicked paint is not the currently selected one
        if(view != currPaint){
            ImageButton imgView = (ImageButton) view;
            String color = view.getTag().toString(); //get the tag and convert it into string
            drawView.setColor(color); //set color

            //Updating UI to reflect new chosen pallet
            imgView.setImageDrawable(getResources().getDrawable(R.drawable.paint_pressed)); //set new pallet view to pressed
            currPaint.setImageDrawable(getResources().getDrawable(R.drawable.paint)); //set the old pallet view as not pressed
            currPaint = (ImageButton) view; //now set current view to new pressed view
        }
    }

    /*
     * Loads the image when you choose it
     */

    public void loadImagefromGallery(View view) {
        // Create intent to Open Image applications like Gallery, Google Photos
        Intent galleryIntent = new Intent(Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        // Start the Intent
        startActivityForResult(galleryIntent, RESULT_LOAD_IMG);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try {
            // When an Image is picked
            if (requestCode == RESULT_LOAD_IMG && resultCode == RESULT_OK
                    && null != data) {
                // Get the Image from data

                Uri selectedImage = data.getData();
                String[] filePathColumn = { MediaStore.Images.Media.DATA };

                // Get the cursor
                Cursor cursor = getContentResolver().query(selectedImage,
                        filePathColumn, null, null, null);
                // Move to first row
                cursor.moveToFirst();

                int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                imgDecodableString = cursor.getString(columnIndex);
                cursor.close();
                ImageView imgView = (ImageView) findViewById(R.id.imgView);
                // Set the Image in ImageView after decoding the String
                imgView.setImageBitmap(BitmapFactory
                        .decodeFile(imgDecodableString));

            } else {
                Toast.makeText(this, "You haven't picked Image",
                        Toast.LENGTH_LONG).show();
            }
        } catch (Exception e) {
            Toast.makeText(this, "Something went wrong", Toast.LENGTH_LONG)
                    .show();
        }

    }

    public void onClick(View view){
        //Which button is clicked
        if(view.getId() == R.id.brush_btn){
            //When user clicks the button, we will display with three options
            final Dialog brushDialog = new Dialog(this);

            brushDialog.setTitle("Brush size: ");
            brushDialog.setContentView(R.layout.brush_chooser); //choose the layout you want your dialog to be


            brushDialog.show();

            ImageButton smallBtn = (ImageButton)brushDialog.findViewById(R.id.small_brush);
            smallBtn.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v) {
                    drawView.setBrushSize(smallBrush);
                    drawView.setLastBrushSize(smallBrush);
                    brushDialog.dismiss();
                }
            });
            ImageButton mediumBtn = (ImageButton)brushDialog.findViewById(R.id.medium_brush);
            mediumBtn.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v) {
                    drawView.setBrushSize(mediumBrush);
                    drawView.setLastBrushSize(mediumBrush);
                    brushDialog.dismiss();
                }
            });

            ImageButton largeBtn = (ImageButton)brushDialog.findViewById(R.id.large_brush);
            largeBtn.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v) {
                    drawView.setBrushSize(largeBrush);
                    drawView.setLastBrushSize(largeBrush);
                    brushDialog.dismiss();
                }
            });

        }else if(view.getId()==R.id.erase_btn){
            final AlertDialog.Builder brushDialog = new AlertDialog.Builder(this);
            brushDialog.setTitle("Eraser size:");
            brushDialog.setView(R.layout.brush_chooser);
            brushDialog.show();
        }
    }

}
