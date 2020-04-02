package fi.lmarkk.litepaint;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.os.Bundle;
import android.widget.Toast;

import java.util.UUID;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    static final int MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE = 1;
    private PaintingView paintingView;
    private ImageButton currentPaint,  drawButton, eraseButton, newButton, saveButton;
    private float smallBrush, mediumBrush, largeBrush;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        paintingView= (PaintingView) findViewById(R.id.painting_view);
        LinearLayout paintLayout = (LinearLayout)findViewById(R.id.paint_colors);
        currentPaint = (ImageButton)paintLayout.getChildAt(0);
        currentPaint.setImageDrawable(getResources().getDrawable(R.drawable.paint_pressed));
        smallBrush = getResources().getInteger(R.integer.small_size);
        mediumBrush = getResources().getInteger(R.integer.medium_size);
        largeBrush = getResources().getInteger(R.integer.large_size);
        newButton = (ImageButton)findViewById(R.id.new_drawing_button);
        newButton.setOnClickListener(this);
        drawButton = (ImageButton)findViewById(R.id.draw_button);
        drawButton.setOnClickListener(this);
        eraseButton = (ImageButton)findViewById(R.id.erase_button);
        eraseButton.setOnClickListener(this);
        saveButton = (ImageButton)findViewById(R.id.save_button);
        saveButton.setOnClickListener(this);
        paintingView.setBrushSize(mediumBrush);
    }

    public void paintClicked(View view) {
        paintingView.setErase(false);
        paintingView.setBrushSize(paintingView.getLastBrushSize());
        if(view != currentPaint){
            ImageButton imageView = (ImageButton)view;
            String color = view.getTag().toString();
            paintingView.setColor(color);
            imageView.setImageDrawable(getResources().getDrawable(R.drawable.paint_pressed));
            currentPaint.setImageDrawable(getResources().getDrawable(R.drawable.paint));
            currentPaint=(ImageButton)view;
        }
    }

    @Override
    public void onClick(View view) {
        if(view.getId() == R.id.draw_button) {
            final Dialog brushDialog = new Dialog(this);
            brushDialog.setTitle("Brush size:");
            brushDialog.setContentView(R.layout.brush_chooser);

            ImageButton smallButton = (ImageButton)brushDialog.findViewById(R.id.small_brush);
            smallButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    paintingView.setErase(false);
                    paintingView.setBrushSize(smallBrush);
                    paintingView.setLastBrushSize(smallBrush);
                    brushDialog.dismiss();
                }
            });

            ImageButton mediumButton = (ImageButton)brushDialog.findViewById(R.id.medium_brush);
            mediumButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    paintingView.setErase(false);
                    paintingView.setBrushSize(mediumBrush);
                    paintingView.setLastBrushSize(mediumBrush);
                    brushDialog.dismiss();
                }
            });

            ImageButton largeButton = (ImageButton)brushDialog.findViewById(R.id.large_brush);
            largeButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    paintingView.setErase(false);
                    paintingView.setBrushSize(largeBrush);
                    paintingView.setLastBrushSize(largeBrush);
                    brushDialog.dismiss();
                }
            });

            brushDialog.show();
        } else if(view.getId() == R.id.erase_button) {
            final Dialog brushDialog = new Dialog(this);
            brushDialog.setTitle("Eraser size:");
            brushDialog.setContentView(R.layout.brush_chooser);

            ImageButton smallBtn = (ImageButton)brushDialog.findViewById(R.id.small_brush);
            smallBtn.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v) {
                    paintingView.setErase(true);
                    paintingView.setBrushSize(smallBrush);
                    brushDialog.dismiss();
                }
            });

            ImageButton mediumBtn = (ImageButton)brushDialog.findViewById(R.id.medium_brush);
            mediumBtn.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v) {
                    paintingView.setErase(true);
                    paintingView.setBrushSize(mediumBrush);
                    brushDialog.dismiss();
                }
            });

            ImageButton largeBtn = (ImageButton)brushDialog.findViewById(R.id.large_brush);
            largeBtn.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v) {
                    paintingView.setErase(true);
                    paintingView.setBrushSize(largeBrush);
                    brushDialog.dismiss();
                }
            });

            brushDialog.show();
        } else if(view.getId() == R.id.new_drawing_button) {
            AlertDialog.Builder newDialog = new AlertDialog.Builder(this);
            newDialog.setTitle("New drawing");
            newDialog.setMessage("Start new drawing (you will lose the current drawing)?");
            newDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener(){
                public void onClick(DialogInterface dialog, int which){
                    paintingView.startNew();
                    dialog.dismiss();
                }
            });
            newDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener(){
                public void onClick(DialogInterface dialog, int which){
                    dialog.cancel();
                }
            });
            newDialog.show();
        } else if(view.getId() == R.id.save_button) {
            if(ContextCompat.checkSelfPermission(this,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {

                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE);
            } else {
                makeSaveDialog();
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    makeSaveDialog();
                }
                return;
            }
        }
    }

    private void makeSaveDialog() {
        if(ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_GRANTED) {
            AlertDialog.Builder saveDialog = new AlertDialog.Builder(this);
            saveDialog.setTitle("Save drawing");
            saveDialog.setMessage("Save drawing to device Gallery?");
            saveDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which){
                    paintingView.setDrawingCacheEnabled(true);
                    String imgSaved = MediaStore.Images.Media.insertImage(
                            getContentResolver(), paintingView.getDrawingCache(),
                            UUID.randomUUID().toString()+".png", "drawing");
                    if(imgSaved!=null){
                        Toast savedToast = Toast.makeText(getApplicationContext(),
                                "Drawing saved to Gallery!", Toast.LENGTH_SHORT);
                        savedToast.show();
                    }
                    else{
                        Toast unsavedToast = Toast.makeText(getApplicationContext(),
                                "Oops! Image could not be saved.", Toast.LENGTH_SHORT);
                        unsavedToast.show();
                    }
                    paintingView.destroyDrawingCache();
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
