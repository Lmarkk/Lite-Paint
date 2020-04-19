package fi.lmarkk.litepaint;

import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.view.MotionEvent;

/**
 * Class which holds instances of Canvas, Bitmap, Paint and other related classes to facilitate
 * the user painting on a canvas with various brush sizes and colors.
 *
 * @author Lassi Markkinen
 * @version 2020.0419
 */
public class PaintingView extends View {
    private Path drawPath;
    private Paint drawPaint, canvasPaint;
    private int paintColor = 0xFF000000;
    private Canvas drawCanvas;
    private Bitmap canvasBitmap;
    private float brushSize, lastBrushSize;

    /**
     * Public constructor which sets layer type for erasing functionality and calls setupDrawing.
     *
     * @author Lassi Markkinen
     * @param context App context.
     * @param attrs AttributeSet, not used.
     */
    public PaintingView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setLayerType(LAYER_TYPE_SOFTWARE, null);
        setupDrawing();
    }
    /**
     * Utility method which instantiates the class attributes.
     *
     * @author Lassi Markkinen
     */
    private void setupDrawing() {
        brushSize = getResources().getInteger(R.integer.medium_size);
        lastBrushSize = brushSize;
        drawPath = new Path();
        drawPaint = new Paint();
        drawPaint.setColor(paintColor);
        drawPaint.setAntiAlias(true);
        drawPaint.setStrokeWidth(brushSize);
        drawPaint.setStyle(Paint.Style.STROKE);
        drawPaint.setStrokeJoin(Paint.Join.ROUND);
        drawPaint.setStrokeCap(Paint.Cap.ROUND);
        canvasPaint = new Paint(Paint.DITHER_FLAG);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        canvasBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        drawCanvas = new Canvas(canvasBitmap);
    }

    /**
     * Called when user draws a path on the canvas. calls Canvas drawBitmap and drawPath methods.
     *
     * @author Lassi Markkinen
     * @param canvas Canvas used for drawing.
     */
    @Override
    protected void onDraw(Canvas canvas) {
        canvas.drawBitmap(canvasBitmap, 0, 0, canvasPaint);
        canvas.drawPath(drawPath, drawPaint);
    }

    /**
     * Method which handles user touch events. Updates drawPath according to user motion and calls
     * Canvas.drawPath when finger is lifted from screen.
     *
     * @author Lassi Markkinen
     * @param event The motion event registered when user presses on the screen.
     * @return true if the event action matches the given options in the switch case, defaults to false.
     */
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float touchX = event.getX();
        float touchY = event.getY();

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                drawPath.moveTo(touchX, touchY);
                break;
            case MotionEvent.ACTION_MOVE:
                drawPath.lineTo(touchX, touchY);
                break;
            case MotionEvent.ACTION_UP:
                drawCanvas.drawPath(drawPath, drawPaint);
                drawPath.reset();
                break;
            default:
                return false;
        }
        invalidate();
        return true;
    }

    /**
     * Method which parses and sets the color for the Paint instance.
     *
     * @author Lassi Markkinen
     * @param newColor hex string received from MainActivity.showColorPickerDialog.
     */
    public void setColor(String newColor) {
        invalidate();
        paintColor = Color.parseColor(newColor);
        drawPaint.setColor(paintColor);
    }

    /**
     * Method which sets the stroke width for the Paint instance.
     *
     * The given float value is modified into suitable form using TypedValue.applyDimension and then
     * passed to Paint.setStrokeWidth method.
     *
     * @author Lassi Markkinen
     * @param newSize Size variable received from MainActivity brush size dialog.
     */
    public void setBrushSize(float newSize) {
        brushSize = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                newSize, getResources().getDisplayMetrics());
        drawPaint.setStrokeWidth(brushSize);
    }

    public void setLastBrushSize(float lastSize) {
        lastBrushSize=  lastSize;
    }

    public float getLastBrushSize() {
        return lastBrushSize;
    }

    /**
     * Method which toggles the erase functionality.
     *
     * if isErase is found true, drawPaint.setXfermode is used with PorterDuff.Mode.CLEAR to apply
     * erasing effect to brush. Otherwise mode is set to null and Paint function will be normal to
     * user.
     *
     * @author Lassi Markkinen
     * @param isErase Determines whether the Paint instance is set to erase or not.
     */
    public void setErase(boolean isErase) {
        if(isErase) {
            drawPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
        } else {
            drawPaint.setXfermode(null);
        }
    }

    /**
     * Method which starts a new painting. calls the Canvas.DrawColor method with
     * PorterDuff.Mode.CLEAR and Canvas.invalidate methods which effectively erases the whole canvas
     * and refreshes the screen so user can see the change immediately.
     *
     * @author Lassi Markkinen
     */
    public void startNew(){
        drawCanvas.drawColor(0, PorterDuff.Mode.CLEAR);
        invalidate();
    }

    public int getPaintColor() {
        return paintColor;
    }
}
