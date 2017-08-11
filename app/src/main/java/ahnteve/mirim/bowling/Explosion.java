package ahnteve.mirim.bowling;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

public class Explosion {
    final static int BIG =0;    // 폭파 불꽃의 종류를 지정하는 상수
    final static int SMALL=1;
    final static int MYSHIP=2;
    final static int BOSS=3;

    public int x, y;
    public int w, h;
    public boolean isDead;
    public Bitmap imgExp;

    private Bitmap imgTemp[]=new Bitmap[6];
    private int kind;       // 폭파 종류(1:작은것 0:큰것 2:아군기 3:보스)
    private int expCnt=-1;  // 폭파 진행 카운터
    private int delay=15;   // 아군기 폭파 후 지연시간

    public Explosion(int x, int y, int kind) {
        this.x=x;
        this.y=y;
        this.kind=kind;

        int n=kind;
        if(n==BOSS) n=BIG;

        for(int i=0; i<6; i++)
            imgTemp[i]= BitmapFactory.decodeResource(GameView.mContext.getResources(), R.drawable.exp00+n*6+i);

        w=imgTemp[0].getWidth()/2;
        h=imgTemp[0].getHeight()/2;
    }

    public boolean Explode(){
        expCnt++;
        int num=expCnt;

        if(kind==MYSHIP || kind==BOSS){
            num=expCnt/3;   // 세 배 천천히 폭파
            if(num>5)num=5;
        }

        if(expCnt==1){
            switch (kind){
                case SMALL :
                    if(GameView.isSound)
                        GameView.sdPool.play(GameView.sdExp0, 1, 1, 9, 0, 1);
                    if(GameView.isVibe)
                        GameView.vibe.vibrate(50);
                    break;
                case BIG :
                    if(GameView.isSound)
                        GameView.sdPool.play(GameView.sdExp1, 1, 1, 9, 0, 1);
                    if(GameView.isVibe)
                        GameView.vibe.vibrate(100);
                    break;
                case MYSHIP :
                    if(GameView.isSound)
                        GameView.sdPool.play(GameView.sdExp2, 1, 1, 9, 0, 1);
                    if(GameView.isVibe)
                        GameView.vibe.vibrate(100);
                    break;
                case BOSS :
                    if(GameView.isSound)
                        GameView.sdPool.play(GameView.sdExp3, 1, 1, 9, 0, 1);
                    if(GameView.isVibe)
                        GameView.vibe.vibrate(100);
                    break;
            }
        }
        imgExp=imgTemp[num];
        if(num<5) return false;     // 폭파 진행중

        switch (kind){
            case SMALL :
                return true;
            case MYSHIP:
                return ResetGunShip();
            default:
                return CheckClear();    // 적군 파괴, 스테이지 클리어 조사
        }
    }

    public static boolean CheckClear(){
        if(GameView.mMap.enemyCnt>0 || GameView.mExp.size()>1)
            return true;

        if(GameView.stageNum%GameView.BOSS_COUNT >0){
            GameView.status=GameView.STAGE_CLEAR;
            return true;
        }

        if(GameView.mBoss.shield[EnemyBoss.CENTER]>0)
            return true;

        if(GameView.mBoss.shield[EnemyBoss.CENTER]<0){
            GameView.mBoss.y=-60;
            GameView.mBoss.shield[EnemyBoss.CENTER]=0;
            GameView.isBoss=false;
            GameView.status=GameView.STAGE_CLEAR;
            return true;
        }

        GameView.MakeBossStage();
        return true;
    }

    public boolean ResetGunShip(){
        if(--delay > 0) return false; // 지연시간

        if(GameView.shipCnt>=0){
            GameView.mShip.ResetShip();
            GameView.isPower=GameView.isDouble=false;
            GameView.gunDelay=15;
        } else {
            GameView.mShip.y=-40;   // GameOver
            GameView.status=GameView.GAMEOVER;
        }
        return true;
    }

}
