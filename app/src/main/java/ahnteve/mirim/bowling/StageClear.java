package ahnteve.mirim.bowling;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;

public class StageClear {
    private int mx, my; // 메세지 좌표
    private int mw;     // 메세지 폭
    private Bitmap imgMsg;
    private int loop;

    public StageClear(){
        imgMsg= BitmapFactory.decodeResource(GameView.mContext.getResources(), R.drawable.msg_clear);
        mw=imgMsg.getWidth();

        mx=(GameView.width-mw)/2;
        my=300;
        loop=0;
    }

    public void SetClear(Canvas canvas){
        int x, y, w, h;
        boolean isFinish;

        canvas.drawBitmap(GameView.imgBack, 0, 0, null);
        GameView.mThread.DrawScore(canvas);

        GameView.mShip.dir=3;
        isFinish=GameView.mShip.Move();
        x=GameView.mShip.x;
        y=GameView.mShip.y;
        w=GameView.mShip.w;
        h=GameView.mShip.h;

        canvas.drawBitmap(GameView.mShip.imgShip, x-w, y-h, null);

        loop++;

        if(loop%12 / 6 == 0)
            canvas.drawBitmap(imgMsg, mx, my, null);

        if(isFinish){
            canvas.drawBitmap(imgMsg, mx, my, null);
            GameView.mShip.dir=0;
            loop=0;
            setNextStage();
        }
    }

    public void setNextStage() {
        // 화면 위의 모든 객체 제거
        GameView.mMissile.clear();
        GameView.mGun.clear();
        GameView.mBonus.clear();
        GameView.mExp.clear();

        GameView.stageNum++;
        if (GameView.stageNum > GameView.MAX_STAGE) {
            GameView.status = GameView.ALL_CLEAR;
            GameView.stageNum = GameView.MAX_STAGE;
        } else {
            GameView.MakeStage();
            GameView.status=GameView.PROCESS;
        }
    }
}
