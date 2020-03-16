package fi.lmarkk.litepaint;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.os.Bundle;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private PaintingView paintingView;
    private ImageButton currentPaint,  drawButton;
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
        drawButton = (ImageButton)findViewById(R.id.draw_button);
    }

    public void paintClicked(View view) {
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
        }
    }
}
