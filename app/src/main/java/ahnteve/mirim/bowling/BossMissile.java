package ahnteve.mirim.bowling;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

public class BossMissile {
    public int x, y;
    public int w, h;
    public boolean isDead;
    public Bitmap imgMissile;

    private int sx, sy;

    public BossMissile(int x, int y, int dir){
        this.x=x;
        this.y=y;

        imgMissile= BitmapFactory.decodeResource(GameView.mContext.getResources(), R.drawable.boss_missile);
        w=imgMissile.getWidth()/2;
        h=imgMissile.getHeight()/2;

        sy=10;
        sx=0;
        if(dir==EnemyBoss.LEFT) // 1
            sx=-2;
        if(dir==EnemyBoss.RIGHT) // 2
            sx=2;
    }

    public boolean Move(){
        x+=sx;
        y+=sy;

        return (x<w || x>GameView.width+w || y>GameView.height+h);
    }
}
