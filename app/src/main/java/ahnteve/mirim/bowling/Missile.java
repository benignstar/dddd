package ahnteve.mirim.bowling;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

public class Missile {
    public int x, y, dir;   // 좌표, 발사 방향
    public boolean isDead;  // 사망 여부
    public Bitmap imgMissile;   // 미사일 이미지

    private float sx, sy;

    public Missile(int x, int y, int dir){
        this.x=x;
        this.y=y;
        this.dir=dir;
        imgMissile= BitmapFactory.decodeResource(GameView.mContext.getResources(), R.drawable.missile0);

        sx=GameView.mMap.sx[dir];   // 방향에 따른 이동 속도 계산
        sy=GameView.mMap.sy[dir];
        Move();
    }

    public boolean Move(){
        x+=(int) (sx*10);
        y+=(int) (sy*10);   // 미사일 이동

        return (x<0 || x>GameView.width || y>GameView.height);
    }
}
