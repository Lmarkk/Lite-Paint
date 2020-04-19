package fi.lmarkk.litepaint;

import androidx.annotation.NonNull;
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
import android.os.Bundle;
import android.widget.Toast;

import com.flask.colorpicker.ColorPickerView;
import com.flask.colorpicker.OnColorSelectedListener;
import com.flask.colorpicker.builder.ColorPickerClickListener;
import com.flask.colorpicker.builder.ColorPickerDialogBuilder;

import java.util.UUID;


/**
 * The main activity class which holds a PaintingView instance and methods for handling ui button clicks.
 *
 * @author Lassi Markkinen
 * @version 2020.0419
 */
public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    static final int MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE = 1;
    private PaintingView paintingView;
    private float smallBrush, mediumBrush, largeBrush;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        paintingView= findViewById(R.id.painting_view);
        smallBrush = getResources().getInteger(R.integer.small_size);
        mediumBrush = getResources().getInteger(R.integer.medium_size);
        largeBrush = getResources().getInteger(R.integer.large_size);
        ImageButton newButton = findViewById(R.id.new_drawing_button);
        newButton.setOnClickListener(this);
        ImageButton drawButton = findViewById(R.id.draw_button);
        drawButton.setOnClickListener(this);
        ImageButton eraseButton = findViewById(R.id.erase_button);
        eraseButton.setOnClickListener(this);
        ImageButton saveButton = findViewById(R.id.save_button);
        saveButton.setOnClickListener(this);
        ImageButton colorPickerButton = findViewById(R.id.color_picker_button);
        colorPickerButton.setOnClickListener(this);
        paintingView.setBrushSize(mediumBrush);
    }

    /**
     * OnClick method for the main activity which uses a switch case to determine if an imagebutton
     * is clicked.
     *
     * Depending on the button PaintingView methods can be called or in the case of the
     * new drawing and save buttons, the user will be prompted with dialogs and if the color picker
     * button is clicked the user will be presented with a color selector.
     *
     *
     * @author Lassi Markkinen
     * @param view The view which was clicked.
     */

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.draw_button: {
                final Dialog brushDialog = new Dialog(this);
                brushDialog.setTitle("Brush size:");
                brushDialog.setContentView(R.layout.brush_chooser);

                ImageButton smallButton = brushDialog.findViewById(R.id.small_brush);
                smallButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        paintingView.setErase(false);
                        paintingView.setBrushSize(smallBrush);
                        paintingView.setLastBrushSize(smallBrush);
                        brushDialog.dismiss();
                    }
                });

                ImageButton mediumButton = brushDialog.findViewById(R.id.medium_brush);
                mediumButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        paintingView.setErase(false);
                        paintingView.setBrushSize(mediumBrush);
                        paintingView.setLastBrushSize(mediumBrush);
                        brushDialog.dismiss();
                    }
                });

                ImageButton largeButton = brushDialog.findViewById(R.id.large_brush);
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
                break;
            }
            case R.id.erase_button: {
                final Dialog brushDialog = new Dialog(this);
                brushDialog.setTitle("Eraser size:");
                brushDialog.setContentView(R.layout.brush_chooser);

                ImageButton smallBtn = brushDialog.findViewById(R.id.small_brush);
                smallBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        paintingView.setErase(true);
                        paintingView.setBrushSize(smallBrush);
                        brushDialog.dismiss();
                    }
                });

                ImageButton mediumBtn = brushDialog.findViewById(R.id.medium_brush);
                mediumBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        paintingView.setErase(true);
                        paintingView.setBrushSize(mediumBrush);
                        brushDialog.dismiss();
                    }
                });

                ImageButton largeBtn = brushDialog.findViewById(R.id.large_brush);
                largeBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        paintingView.setErase(true);
                        paintingView.setBrushSize(largeBrush);
                        brushDialog.dismiss();
                    }
                });

                brushDialog.show();
                break;
            }
            case R.id.new_drawing_button:
                AlertDialog.Builder newDialog = new AlertDialog.Builder(this);
                newDialog.setTitle("New drawing");
                newDialog.setMessage("Start new drawing (you will lose the current drawing)?");
                newDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        paintingView.startNew();
                        dialog.dismiss();
                    }
                });
                newDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                newDialog.show();
                break;
            case R.id.save_button:
                if (ContextCompat.checkSelfPermission(this,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED) {

                    ActivityCompat.requestPermissions(this,
                            new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                            MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE);
                } else {
                    makeSaveDialog();
                }
                break;
            case R.id.color_picker_button:
                paintingView.setErase(false);
                paintingView.setBrushSize(paintingView.getLastBrushSize());
                showColorPickerDialog(view);
                break;
        }
    }

    /**
     * Method that is called when the app request local storage permissions from the user.
     *
     * Simple if-clauses are used to check if permission was granted if and if affirmative a method
     * is called prompting the user with a save dialog.
     *
     *
     * @author Lassi Markkinen
     * @param requestCode Code for the type of permission requested.
     * @param permissions The requested permissions.
     * @param grantResults The results for permissions being granted or denied.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                makeSaveDialog();
            }
        }
    }

    /**
     * Method which writes the current image into local storage after checking permissions.
     *
     * First the method will check if the user has enabled storage writing permissions. The user
     * is the prompted with a dialog to confirm whether they want to save the image. If the user
     * affirms, PaintingView's current drawing cache is saved into local storage with a randomly
     * name and png media type. If the returned image is not null, the user is notified with a toast
     * and likewise if the operation fails the user is also notified of this.
     *
     *
     * @author Lassi Markkinen
     */
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
                                "Painting saved to gallery!",
                                Toast.LENGTH_SHORT);
                        savedToast.show();
                    }
                    else{
                        Toast unsavedToast = Toast.makeText(getApplicationContext(),
                                "Something went wrong, image could not be saved.",
                                Toast.LENGTH_SHORT);
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

    /**
     * Method which displays a dialog for the user to select a paint color with.
     *
     * A ColorPickerDialog is built with a positive and negative button. when the "ok" button is
     * clicked, the PaintingView.setColor method is called. the selectedColor field is converted
     * from an int to a hexstring for this purpose. The negative button simply closes the dialog.
     *
     *
     * @author Lassi Markkinen
     * @param v the imagebutton used for accessing the color picker.
     */
    public void showColorPickerDialog(View v) {
        ColorPickerDialogBuilder
                .with(this)
                .setTitle("Choose color")
                .initialColor(paintingView.getPaintColor())
                .wheelType(ColorPickerView.WHEEL_TYPE.FLOWER)
                .density(12)
                .setOnColorSelectedListener(new OnColorSelectedListener() {
                    @Override
                    public void onColorSelected(int selectedColor) {}
                })
                .setPositiveButton("ok", new ColorPickerClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int selectedColor, Integer[] allColors) {
                        paintingView.setColor("#" + Integer.toHexString(selectedColor).toUpperCase());
                    }
                })
                .setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {}
                })
                .build()
                .show();
    }
}
