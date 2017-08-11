package ahnteve.mirim.bowling;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

public class EnemyBoss {
    final static int CENTER=0;
    final static int LEFT=1;
    final static int RIGHT=2;
    final static int BOTH=3;

    public int x, y;
    public int w, h;
    public int imgNum;
    public int shield[]={0, 0, 0}; // 보호막
    public Bitmap imgBoss;
    public Bitmap imgSpt[];

    private int sx, sy;
    private int dir;
    private int loop;
    private int arShield[]={20, 30, 40}; // 난이도에 따른 보호막

    public EnemyBoss(){
        shield=new int[3];
        imgSpt=new Bitmap[4];

        for(int i=0; i<4; i++)
            imgSpt[i]= BitmapFactory.decodeResource(GameView.mContext.getResources(), R.drawable.boss0+i);

        x=imgSpt[3].getWidth()/2;
        h=imgSpt[3].getHeight()/2;
    }

    public void InitBoss(){
        shield[LEFT]=shield[RIGHT]=arShield[GameView.difficult];
        shield[CENTER]=shield[LEFT]*2;

        x=GameView.width/2;
        y=-60;

        sy=4;
        sx=4;
        dir=0;
        loop=0;
        imgBoss=imgSpt[BOTH];
    }

    public void Move(){
        x+=sx*dir;
        y+=sy;
        if(y>100){
            sy=0;
            if(dir==0) dir=1;
        }

        if(x<100 || x>GameView.width-100)
            dir=-dir;

        loop++;
        if(loop%50>0) return;
        GameView.mBsMissile.add(new BossMissile(x, y, CENTER));
        if(shield[LEFT]>0)
            GameView.mBsMissile.add(new BossMissile(x-w/2, y, LEFT));
        if(shield[RIGHT]>0)
            GameView.mBsMissile.add(new BossMissile(x+w/2, y, RIGHT));
    }
}
