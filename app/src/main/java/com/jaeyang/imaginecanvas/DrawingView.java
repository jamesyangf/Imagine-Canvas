package com.jaeyang.imaginecanvas;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.net.Uri;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;

/**
 * Created by Jae Yang on 8/11/2016.
 *
 * Custom View class in which the drawing will take place
 */
public class DrawingView extends View {

    //drawing path
    private Path drawPath;
    //drawing and canvas paint, defines the attributes
    private Paint drawPaint, canvasPaint; //two types of drawing, the user and whats on the canvas
    //initial paint color
    private int paintColor = 0xFF000000;
    //the canvas,defines the shape
    private Canvas drawCanvas;
    //canvas bitmap, the actual canvas to be drawn on
    private Bitmap canvasBitmap;
    //for brush sizes
    private float brushSize, lastBrushSize;
    //flag if erasing
    private boolean erase = false;





    //the constructor of view
    public DrawingView(Context context, AttributeSet attrs){
        super(context,attrs);
        setupDrawing();
    }

    private void setupDrawing(){
        //instantiate the drawing Path and Paint Object
        drawPath = new Path();
        drawPaint = new Paint();

        //initiates brush size with medium size
        brushSize = getResources().getInteger(R.integer.medium_size);
        lastBrushSize = brushSize;


        //Set paints initial color
        drawPaint.setColor(paintColor);

        //Set Up initial paint properties
        drawPaint.setAntiAlias(true);
        drawPaint.setStrokeWidth(brushSize); //width of stroke
        drawPaint.setStyle(Paint.Style.STROKE);
        drawPaint.setStrokeJoin(Paint.Join.ROUND);
        drawPaint.setStrokeCap(Paint.Cap.ROUND);

        //instantiating canvas Paint Object
        canvasPaint = new Paint(Paint.DITHER_FLAG);


    }

    /*
     * sizing the canvas
     */
    protected void onSizeChanged(int w, int h, int oldw,int oldh){
        super.onSizeChanged(w, h, oldw, oldh);

        //instantiate the drawing canvas and bitmap using the width and height values
        canvasBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888); //creates the image bitmap
        drawCanvas = new Canvas(canvasBitmap);

    }

    /*
     * Draws the stuff
     */
    protected void onDraw(Canvas canvas){
        //draw View, call to update the view and is called withinvalidate
        canvas.drawBitmap(canvasBitmap, 0, 0, canvasPaint);
        canvas.drawPath(drawPath, drawPaint); //draws the path the user took
    }

    /*
     * Listen for touch events
     */
    public boolean onTouchEvent(MotionEvent event){
        //detech user touch
        //retrieve the X and Y positions of the user touch
        float touchX = event.getX();
        float touchY = event.getY();

        //switch statement that responds to the motion
        switch (event.getAction()) {
            //When user touches the screen
            case MotionEvent.ACTION_DOWN:
                drawPath.moveTo(touchX,touchY);
                break;
            //when user moves around the screen
            case MotionEvent.ACTION_MOVE:
                drawPath.lineTo(touchX, touchY);
                break;
            //when user lets go of screen
            case MotionEvent.ACTION_UP:
                drawCanvas.drawPath(drawPath, drawPaint);
                drawPath.reset();
                break;
            default:
                return false;
        }

        //calling invalidate will cause the onDraw method to execute
        invalidate(); //if view is visible, onDraw will be called
        return true;

    }

    /*
     * Sets the color for the paint
     */
    public void setColor(String newColor){
        //invalidate the view
        invalidate();

        //parse and set the color for drawing
        paintColor = Color.parseColor(newColor);
        drawPaint.setColor(paintColor);
    }

    /*
     * this method sets the brush size
     */
    public void setBrushSize(float newSize){
        //update size
        float pixelAmount = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                newSize, getResources().getDisplayMetrics());
        brushSize = pixelAmount;
        drawPaint.setStrokeWidth(brushSize);
    }
    public void setLastBrushSize(float lastSize){
        lastBrushSize=lastSize;
    }
    public float getLastBrushSize(){
        return lastBrushSize;
    }

    /*
     * Sets the erase status
     */
    public void setErase(boolean isErase){
        erase = isErase;
        if(erase) drawPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
        else drawPaint.setXfermode(null);
    }
    /*
     * Starts a new drawing
     */
    public void startNew(){
        drawCanvas.drawColor(0, PorterDuff.Mode.CLEAR);//(color, mode)
        //forces a view to draw
        invalidate(); //updates it
    }


}
