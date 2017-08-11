package ahnteve.mirim.bowling;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.view.Display;

public class GameOver {
    final int WAIT=1;   // 버튼입력 대기상태
    final int TOUCH=2;  // 버튼 선택
    final int BTN_YES=1;// 버튼 상태
    final int BTN_NO=2;

    private int btnWhich;
    private int status= WAIT;

    private int mx1, my1, mx2, my2; // 메세지를 표시할 위치
    private int mw1, mw2;           // 메세지 폭
    private int x1, y1, x2, w, h;
    private Bitmap imgOver, imgAgain;
    private Bitmap imgYes, imgNo;
    private Bitmap imgCong;
    private int loop;
    private Rect rectYes, rectNo;   // 버튼의 좌표

    public GameOver(){
        imgYes= BitmapFactory.decodeResource(GameView.mContext.getResources(), R.drawable.btn_yes);
        imgNo=BitmapFactory.decodeResource(GameView.mContext.getResources(), R.drawable.btn_no);

        imgOver=BitmapFactory.decodeResource(GameView.mContext.getResources(), R.drawable.msg_over);
        imgCong=BitmapFactory.decodeResource(GameView.mContext.getResources(), R.drawable.msg_all);
        imgAgain=BitmapFactory.decodeResource(GameView.mContext.getResources(), R.drawable.msg_again);

        my1=260;    // 메세지 표시 위치

        // Try again
        mw2=imgAgain.getWidth();
        mx2=(GameView.width-mw2)/2;
        my2=550;

        y1=630;
        w=imgYes.getWidth();
        h=imgYes.getHeight();

        x1=100;
        x2=GameView.width-100-w;

        rectYes=new Rect(x1, y1, x1+w, y1+h);
        rectNo=new Rect(x2, y1, x2+w, y1+h);
        loop=0;
    }

    public void SetOver(Canvas canvas){
        if(GameView.status==GameView.GAMEOVER)
            mw1=imgOver.getWidth();
        else mw1=imgCong.getWidth();
        mx1=(GameView.width-mw1)/2;

        switch (status){
            case WAIT:
                DisplayAll(canvas);
                break;
            case TOUCH :
                CheckButton();
        }
    }

    public void DisplayAll(Canvas canvas){
        canvas.drawBitmap(GameView.imgBack, 0, 0, null);

        GameView.mThread.MoveAll();
        GameView.mThread.AttackSprite();
        GameView.mThread.DrawAll(canvas);

        loop++;
        if(loop%12 / 6==0){
            if(GameView.status==GameView.GAMEOVER)
                canvas.drawBitmap(imgOver, mx1, my1, null);
            else
                canvas.drawBitmap(imgCong, mx1, my1, null);
        }

        canvas.drawBitmap(imgAgain, mx2, my2, null);
        canvas.drawBitmap(imgYes, x1, y1, null);
        canvas.drawBitmap(imgNo, x2, y1, null);
    }

    public void CheckButton(){
        if(btnWhich==BTN_NO){
            GameView.GameOver();
            return;
        }

        status=WAIT;
        btnWhich=0;

        GameView.mMissile.clear();
        GameView.mGun.clear();
        GameView.mBonus.clear();
        GameView.mExp.clear();
        GameView.mBoss.InitBoss();

        GameView.score=0;

        if(GameView.stageNum>GameView.MAX_STAGE)
            GameView.stageNum=GameView.MAX_STAGE;

        GameView.shipCnt=3;
        GameView.MakeStage();

        GameView.mShip.ResetShip();
        GameView.status=GameView.PROCESS;
    }

    public boolean TouchEvent(int x, int y){
        if(rectYes.contains(x, y)) btnWhich=BTN_YES;
        if(rectNo.contains(x, y)) btnWhich=BTN_NO;
        if(btnWhich!=0) status=TOUCH;
        return true;
    }
}


