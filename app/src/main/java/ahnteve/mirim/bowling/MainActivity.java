package ahnteve.mirim.bowling;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.view.WindowManager;

public class MainActivity extends AppCompatActivity {
    GameView mGameView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);        // fullscreen

        setContentView(R.layout.activity_main);

        mGameView=(GameView)findViewById(R.id.mGameView);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) { // Option Menu
        menu.add(0, 1, 0, "Quit Game");
        menu.add(0, 2, 0, "Pause Game");
        menu.add(0, 3, 0, "Auto Fire On");
        menu.add(0, 4, 0, "Music Off");
        menu.add(0, 5, 0, "Sound Off");
        menu.add(0, 6, 0, "Vibrator Off");

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case 1:
                mGameView.StopGame();
                finish();
                break;
            case 2:
                break;
            case 3:
                break;
            case 4:
                break;
            case 5:
                break;
            case 6:
                break;
        }
        return true;
    }
}
