package ahnteve.mirim.bowling;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

public class GunShip {
    public int x, y;            // 위치
    public int w, h;            // 폭과 높이
    public int shield;          // 보호막
    public int dir;             // 이동방향 (1:왼쪽, 2:오른쪽, 3:위쪽, 0:정지)
    public boolean isDead;      // 사망
    public boolean undead;      // 무적모드
    public int undeadCnt;       // 무적모드 지속시간
    public Bitmap imgShip;      // 우주선 이미지

    private Bitmap imgTemp[] =new Bitmap[8];
    private int sx[]={0, -8, 8, 0};
    private int sy[]={0, 0, 0, -8};

    private int imgNum=0;

    public GunShip(int x, int y){
        this.x=x;
        this.y=y;
        for(int i=0; i<8; i++){
            imgTemp[i]= BitmapFactory.decodeResource(GameView.mContext.getResources(), R.drawable.gunship0+i);
        }

        w=imgTemp[0].getWidth()/2;
        h=imgTemp[0].getHeight()/2;

        ResetShip();
    }

    // 초기화
    public void ResetShip(){
        x=GameView.width/2;
        y=GameView.height-36;
        shield=3;
        isDead=false;
        undeadCnt=50;
        undead=true;
        dir=0;
        imgShip=imgTemp[0];
    }

    public boolean Move(){
        imgNum++;
        if(imgNum > 3) imgNum=0;

        if(undead){
            imgShip=imgTemp[imgNum+4];
            undeadCnt--;
            if(undeadCnt<0) undead=false;
        } else
            imgShip=imgTemp[imgNum];

        x+=sx[dir];
        y+=sy[dir];
        if(x<w){
            x=w;
            dir=0;
        } else if(x>GameView.width-w){
            x=GameView.width-w;
            dir=0;
        }
        return (y<-32);     // Stage Clear 용용
    }
}
