package ahnteve.mirim.bowling;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

public class FireGun {
    public int x, y;
    public int w, h;
    public boolean isDead;
    public Bitmap imgGun;

    public int kind;        // 미사일 종류 (0: 보통, 1: 강화)
    private float sy;       // 이동 속도

    public FireGun(int x, int y){
        this.x=x;
        this.y=y;

        kind=(GameView.isPower)? 1: 0; // 미사일 종류
        imgGun= BitmapFactory.decodeResource(GameView.mContext.getResources(), R.drawable.missile1+kind);

        w=imgGun.getWidth()/2;
        h=imgGun.getHeight()/2;
        sy=-10;
        Move();
    }

    public boolean Move(){
        y+=sy;
        return (y<0);
    }
}
