package com.jaeyang.imaginecanvas;
/*
 * The drawing Activity
 */


import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.io.FileNotFoundException;
import java.util.UUID;

public class DrawingActivity extends AppCompatActivity implements View.OnClickListener{
    private DrawingView drawView;
    private ImageButton currPaint, brushBtn, eraseBtn, newBtn, saveBtn; //the current paint view
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
        newBtn = (ImageButton) findViewById(R.id.new_btn);
        newBtn.setOnClickListener(this);
        saveBtn = (ImageButton) findViewById(R.id.save_btn);
        saveBtn.setOnClickListener(this);

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
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
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

                Uri selectedImage = data.getData(); //gets the data from the intent
                String[] filePathColumn = { MediaStore.Images.Media.DATA }; //also file name

                // Get the cursor
                Cursor cursor = getContentResolver().query(selectedImage,
                        filePathColumn, null, null, null); //Query the given URI, returning a Cursor over the result set.
                // Move to first row
                cursor.moveToFirst();

                //Gets the first item that is saved in the cursor
                int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                imgDecodableString = cursor.getString(columnIndex); // gets the filepath of it as a string
                cursor.close();

                Bitmap imageBitmap = BitmapFactory.decodeFile(imgDecodableString);//decode a filePath into a bitmap
                drawView = (DrawingView) findViewById(R.id.drawing);
                //Resize the bitmap
                imageBitmap = Bitmap.createScaledBitmap(imageBitmap, drawView.getWidth(), drawView.getHeight(), false);
                // Set the Image in ImageView after decoding the String
                drawView.setCanvasBitmap(imageBitmap);//BitmapFactory: Creates Bitmap objects from various sources, including files, streams, and byte-arrays.


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
            brushDialog.show();

        }else if(view.getId()==R.id.erase_btn){
            final Dialog brushDialog = new Dialog(this);

            brushDialog.setTitle("Brush size: ");
            brushDialog.setContentView(R.layout.brush_chooser); //choose the layout you want your dialog to be

            brushDialog.show();
        }else if(view.getId() == R.id.new_btn){
            //happens when the new button is pressed
            AlertDialog.Builder newDialog = new AlertDialog.Builder(this);
            newDialog.setTitle("New drawing");
            newDialog.setMessage("Start new drawing \n(you will lose the current drawing)?");
            newDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener(){
                public void onClick(DialogInterface dialog, int which){
                    drawView.startNew();
                    dialog.dismiss();
                }
            });
            newDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener(){
                public void onClick(DialogInterface dialog, int which){
                    dialog.cancel();
                }
            });
            newDialog.show();
        }else if(view.getId() == R.id.save_btn){
            AlertDialog.Builder saveDialog = new AlertDialog.Builder(this);
            saveDialog.setTitle("Save drawing");
            saveDialog.setMessage("Save drawing to device Gallery?");
            saveDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener(){
                public void onClick(DialogInterface dialog, int which){
                    //save drawing
                    //enable the view to be saved as a bitmap in a cache
                    drawView.setDrawingCacheEnabled(true);
                    //Write the image to a file
                    Bitmap viewBitMap = drawView.getDrawingCache();
                    //MediaStore is the provider
                    //Saves the images in the gallery, this is set to imgSaved
                    String imgSaved = MediaStore.Images.Media.insertImage(
                            getContentResolver(), viewBitMap, UUID.randomUUID().toString()+".png", "drawing");

                    //Toast that gives user feedback on opening the app
                    if(imgSaved!=null){ //if img is saved
                        Toast savedToast = Toast.makeText(getApplicationContext(),
                                "Drawing saved to Gallery!", Toast.LENGTH_SHORT);
                        savedToast.show();
                    }
                    else{
                        Toast unsavedToast = Toast.makeText(getApplicationContext(),
                                "Oops! Image could not be saved.", Toast.LENGTH_SHORT);
                        unsavedToast.show();
                    }

                    //Destroy the cache so future drawings saved wont use existing cache
                    drawView.destroyDrawingCache();

                }
            });
            saveDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener(){
                public void onClick(DialogInterface dialog, int which){
                    dialog.cancel();
                }
            });
            saveDialog.show();
        }
    }

}
