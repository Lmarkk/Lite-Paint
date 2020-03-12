package fi.lmarkk.litepaint;

import androidx.appcompat.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.os.Bundle;

public class MainActivity extends AppCompatActivity {
    private PaintingView paintingView;
    private ImageButton currentPaint;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        paintingView= (PaintingView) findViewById(R.id.painting_view);
        LinearLayout paintLayout = (LinearLayout)findViewById(R.id.paint_colors);
        currentPaint = (ImageButton)paintLayout.getChildAt(0);
        currentPaint.setImageDrawable(getResources().getDrawable(R.drawable.paint_pressed));
    }
}
