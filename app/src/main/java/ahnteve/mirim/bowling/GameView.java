package ahnteve.mirim.bowling;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.os.Vibrator;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Display;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.WindowManager;

import java.util.ArrayList;

public class GameView extends SurfaceView implements SurfaceHolder.Callback {
    static GameThread mThread;          // GameTread
    SurfaceHolder mHolder;              // SurfaceHolder
    static Context mContext;            // Context : mContext를 외부의 Class에서 참조할 수 있도록 static으로 선언

    static int width, height;           // View 해상도 : 외부의 Class에서 참조할 수 있도록 static으로 선언

    static MapTable mMap;               // MapTable
    static AttackEnemy mAttack; // x
    static GunShip mShip;
    static Collision mCollision;
    static GameOver mGameOver;
    StageClear mClear; // x
    static EnemyBoss mBoss; // x

    static int stageNum; // x

    static Sprite mEnemy[][]=new Sprite[6][8]; // 적 이미지
    static Bitmap imgBack;                      // 배경 이미지
    private int by1, sy1;
    private int counter=0;
    static int sw[]=new int[6];                 // 적군의 폭과 높이
    static int sh[]=new int[6];

    static ArrayList<Missile> mMissile;
    static ArrayList<FireGun> mGun;
    static ArrayList<BossMissile> mBsMissile;
    static ArrayList<Explosion> mExp;
    static ArrayList<Bonus> mBonus;
    static ArrayList<Obstacle> mObstacle;

    // 게임 난이도 - 메인 메뉴용
    final static int EASY=0;
    final static int MEDIUM=1;
    final static int HARD=2;
    static int difficult=EASY;

    // Game 진행에 관한 flag 변수들
    static boolean isPower=false;       // 강화된 미사일
    static boolean isDouble=false;      // 미사일 두 개씩 발사
    static boolean isAutoFire=false;    // 미사일 자동 발사

    // 메인 메뉴용 변수들
    static boolean isMusic=true;
    static boolean isSound=true;
    static boolean isVibe=true;

    // 사운드 관련 변수들
    static SoundPool sdPool;
    static int sdFire, sdExp0, sdExp1, sdExp2, sdExp3;
    static Vibrator vibe;
    static MediaPlayer player;

    // 프로그램 상태에 관한 상수
    final static int PROCESS=1;
    final static int STAGE_CLEAR=2;
    final static int GAMEOVER=3;
    final static int ALL_CLEAR=4;

    // 전체 스테이지 수와 BOSS 출현 빈도 상수
    final static int MAX_STAGE=6;
    final static int BOSS_COUNT=3;

    static int status=PROCESS;

    static int shipCnt=99;

    static int gunDelay=15;

    Bitmap imgMinShip;
    static int score=0;

    static boolean isBoss=false;

    boolean DEBUG=true;

    public GameView(Context context, AttributeSet attrs) {
        super(context, attrs);
        SurfaceHolder holder=getHolder();
        holder.addCallback(this);

        mHolder=holder;             // holder와 Context 보존
        mContext=context;
        mThread = new GameThread(holder, context);

        InitGame();
        MakeStage();
        setFocusable(true);

    }

    public void InitGame(){
        Display display=((WindowManager)mContext.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
        Point point=new Point();
        display.getSize(point);

        width=point.x;
        height=point.y;     // 해상도 구하기

        mMap=new MapTable();
        mAttack=new AttackEnemy();
        mMissile=new ArrayList<Missile>();
        mGun=new ArrayList<FireGun>();
        mBsMissile=new ArrayList<BossMissile>();
        mExp=new ArrayList<Explosion>();
        mBonus=new ArrayList<Bonus>();
        mCollision=new Collision();
        mObstacle=new ArrayList<Obstacle>();

        mClear=new StageClear();
        mGameOver=new GameOver();
        mBoss=new EnemyBoss();

        mShip=new GunShip(width/2, height-60);
        shipCnt=3;
        stageNum=1;
        status=PROCESS;

        for(int i=0; i<6; i++){
            for(int j=0; j<8; j++)
                mEnemy[i][j]=new Sprite();
        }

        by1=height/2;
        sy1=-(width/50);

        difficult = ((GlobalVars) mContext.getApplicationContext()).getDifficult();
        isMusic = ((GlobalVars) mContext.getApplicationContext()).getIsMusic();
        isSound = ((GlobalVars) mContext.getApplicationContext()).getIsSound();
        isVibe = ((GlobalVars) mContext.getApplicationContext()).getIsVibe();

        sdPool=new SoundPool(10, AudioManager.STREAM_MUSIC, 0);
        sdFire=sdPool.load(mContext, R.raw.fire, 1);
        sdExp0=sdPool.load(mContext, R.raw.exp0, 2);
        sdExp1=sdPool.load(mContext, R.raw.exp1, 3);
        sdExp2=sdPool.load(mContext, R.raw.exp2, 4);
        sdExp3=sdPool.load(mContext, R.raw.exp3, 5);

        vibe=(Vibrator)mContext.getSystemService(Context.VIBRATOR_SERVICE);

        player=MediaPlayer.create(mContext, R.raw.green);
        player.setVolume(0.7f, 0.7f);
        player.setLooping(true);

        if(isMusic) player.start();

        imgMinShip=BitmapFactory.decodeResource(mContext.getResources(), R.drawable.miniship);
    }

    public static void MakeStage(){
        mMap.ReadMap(stageNum);
        imgBack= BitmapFactory.decodeResource(mContext.getResources(), R.drawable.space0);
        imgBack=Bitmap.createScaledBitmap(imgBack, width, height, true);

        for(int i=0; i<6; i++){
            for(int j=0; j<8; j++){
                mEnemy[i][j].MakeSprite(i, j);
            }
            sw[i]=mEnemy[i][2].w;
            sh[i]=mEnemy[i][2].h;
        }

        mShip.y=height-36;
        mAttack.ResetAttack();
    }

    public static void MakeBossStage(){
        for(int i=2; i<=4; i++){
            for(int j=0; j<8; j++){
                mEnemy[i][j].ResetSprite();
            }
        }
        mMap.enemyCnt=24;
        mBoss.InitBoss();
        isBoss=true;
        status=PROCESS;
        mShip.y=height-36;
        mAttack.ResetAttack();
    }

    public static void GameOver(){
        StopGame();
        mContext.startActivity(new Intent(mContext, StartGame.class));
        ((Activity)mContext).finish();
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        // SurfaceView가 생성될 때 실행되는 부분
        try{
            mThread.start();
        }catch (Exception e){
            RestartGame();
            if(isMusic) player.start();
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        // SurfaceView가 변경될 때 실행되는 부분
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        // SurfaceView가 해제될 때 실행되는 부분
        StopGame();
        player.stop();
    }

    public static void StopGame(){
        // Thread 완전 정지
        mThread.StopThread();
    }

    public void PauseGame(){
        // Thread 일시 정지
        mThread.PauseNResume(true);
    }

    public void ResumeGame(){
        // Thread 재가동
        mThread.PauseNResume(false);
    }

    public void RestartGame(){
        // 게임 초기화
        mThread.StopThread();   // Thread 중지
        // 현재의 Thread를 비우고 다시 생성
        mThread=null;
        mThread=new GameThread(mHolder, mContext);
        mThread.start();
    }


    // GameThread Class
    class GameThread extends Thread {
        boolean canRun=true;        // Thread 제어용 변수
        boolean isWait=false;       // Thread 일시정지 여부
        int loop;
        Paint paint=new Paint();

        public GameThread(SurfaceHolder holder, Context context){
            paint.setColor(Color.WHITE);
            paint.setAntiAlias(true);
            paint.setTextSize(20);
            paint.setTypeface(Typeface.create("", Typeface.BOLD));

        }

        public void CheckCollision(){
            mCollision.CheckCollision();
        }

        public void AttackSprite() {
            mAttack.Attack();
        }

        public void MoveAll(){
            loop++;

            if(isBoss){
                mBoss.Move();
                for (int i=mBsMissile.size()-1; i>=0; i--)
                    if(mBsMissile.get(i).Move())
                        mBsMissile.remove(i);
            }
            for(int i=5; i>=0; i--){
                for(int j=0; j<8; j++)
                    mEnemy[i][j].Move();
            }

            for(int i=mMissile.size()-1; i>=0; i--){
                if(mMissile.get(i).Move())
                    mMissile.remove(i);
            }

            for(int i=mGun.size()-1; i>=0; i--)
                if(mGun.get(i).Move())
                    mGun.remove(i);

            for(int i=mBonus.size()-1; i>=0; i--)
                if(mBonus.get(i).Move())
                    mBonus.remove(i);

            for(int i=mExp.size()-1; i>=0; i--)
                if(mExp.get(i).Explode())
                    mExp.remove(i);

            if(!mShip.isDead)
                mShip.Move();
        }

        public void DrawAll(Canvas canvas){
            Rect src=new Rect();
            Rect dst=new Rect();
            dst.set(0,0, width, height);

            ScrollImage();
            src.set(0, by1, width, by1+height/2);
            canvas.drawBitmap(imgBack, src, dst, null);

            // 적기
            for(int i=5; i>=0; i--){
                for(int j=0; j<8; j++){
                    if(mEnemy[i][j].isDead) continue;
                    canvas.drawBitmap(mEnemy[i][j].imgSprite, mEnemy[i][j].x - sw[i], mEnemy[i][j].y-sh[i], null);
                }
            }

            if (isBoss){
                for(BossMissile tmp : mBsMissile)
                    canvas.drawBitmap(tmp.imgMissile, tmp.x-tmp.w, tmp.y-tmp.h, null);
                canvas.drawBitmap(mBoss.imgBoss, mBoss.x-mBoss.w, mBoss.y-mBoss.h, null);
            }
            for(Missile tmp : mMissile)
                canvas.drawBitmap(tmp.imgMissile, tmp.x-1, tmp.y-1, null);

            for(FireGun tmp : mGun)
                canvas.drawBitmap(tmp.imgGun, tmp.x- tmp.w, tmp.y-tmp.h, null);

            for(Bonus tmp : mBonus)
                canvas.drawBitmap(tmp.imgBonus, tmp.x-tmp.w, tmp.y-tmp.h, null);

            if(!mShip.isDead)
                canvas.drawBitmap(mShip.imgShip, mShip.x-mShip.w, mShip.y-mShip.h, null);

            for(Explosion tmp : mExp)
                canvas.drawBitmap(tmp.imgExp, tmp.x-tmp.w, tmp.y-tmp.h, null);

            DrawScore(canvas);
        }

        private void ScrollImage() {

            counter++;
/*
            x2+=sx2;
            y2+=sy2;
            if(x2<0) x2=cx;
            if(y2<0) y2=cy;*/

            if(counter%2==0){

                by1+=sy1;

                if(by1<0) by1=height/2;
            }
        }

        public void run(){
            Canvas canvas=null;
            while(canRun){
                canvas=mHolder.lockCanvas();
                try {
                    synchronized (mHolder){
                        switch (status){
                            case PROCESS :
                                if(isAutoFire) FireGunship();
                                AttackSprite();
                                CheckCollision();
                                MoveAll();
                                DrawAll(canvas);
                                break;
                            case STAGE_CLEAR :
                                mClear.SetClear(canvas);
                                break;
                            case ALL_CLEAR :
                            case GAMEOVER :
                                mGameOver.SetOver(canvas);
                        }

                    }
                } finally {
                    if(canvas!=null)
                        mHolder.unlockCanvasAndPost(canvas);
                }

                // Thread 일시 정지
                synchronized (this){
                    if (isWait)     // isWait가 true이면
                        try {
                            wait(); // Thread 대기
                        } catch (Exception e){ }
                }   // sync
            } // while
        } // run


        public void StopThread(){ // Thread 완전 정지
            canRun=false;
            synchronized (this){
                this.notify();
            }
        }

        public void PauseNResume(boolean wait){
            isWait=wait;
            synchronized (this){
                this.notify();
            }
        }

        public void FireGunship() {
            if(loop<gunDelay || mShip.isDead) return;
            if(isDouble){
                mGun.add(new FireGun(mShip.x-18, mShip.y));
                mGun.add(new FireGun(mShip.x+18, mShip.y));
            } else
                mGun.add(new FireGun(mShip.x, mShip.y));

            if(!isAutoFire) mShip.dir=0;
            loop=0;

            if(GameView.isSound)
                GameView.sdPool.play(GameView.sdFire, 1, 1, 9, 0, 1);
        }

        public void DrawScore(Canvas canvas){
            int x, x1, x2, y=30;
            x1=134;                     // hp 위치
            x2=x1+mShip.shield*8+4;     // undead 위치
            x=mShip.undeadCnt/2;

            for(int i=0; i<shipCnt; i++)
                canvas.drawBitmap(imgMinShip, i*20+10, y-15, null);

            canvas.drawText("HP",100, y, paint);
            paint.setColor(0xFF00A0F0);
            for(int i=0; i<mShip.shield; i++)
                canvas.drawRect(i*8+x1, y-10, i*8+x1+6, y-4, paint);

            paint.setColor(Color.RED);
            canvas.drawRect(x2, y-10, x2+x, y-4, paint);

            paint.setColor(Color.WHITE);
            canvas.drawText("Score "+score, 200, y, paint);
            canvas.drawText("Stage"+stageNum, 400, y, paint);
        }

    } // GameThread

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if(event.getPointerCount()>1){
            synchronized (mHolder) {
                int x = (int) event.getX(1);
                int y = (int) event.getY(1);

                if (status == GAMEOVER || status == ALL_CLEAR) {
                    return mGameOver.TouchEvent(x, y);
                }

                if(!mShip.isDead){
                    mShip.dir=0;
                    if(x>0 && x<width/2+1)
                        mShip.dir=1;
                    if(x>width/2 && x<=width)
                        mShip.dir=2;
                }
            }

        } else {
            synchronized (mHolder){
                int x = (int) event.getX();
                int y = (int) event.getY();

                if (status == GAMEOVER || status == ALL_CLEAR) {
                    return mGameOver.TouchEvent(x, y);
                }

                if(!mShip.isDead){
                    mShip.dir=0;
                    if(x>0 && x<width/2+1)
                        mShip.dir=1;
                    if(x>width/2 && x<=width)
                        mShip.dir=2;
                }
            }

        }
        if(event.getAction()==MotionEvent.ACTION_UP){
            synchronized (mHolder){
                mShip.dir=0;
            }
        }
        return true;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(mShip.isDead) return false;

        synchronized (mHolder){
            switch (keyCode){
                case KeyEvent.KEYCODE_DPAD_LEFT :
                    mShip.dir=1;
                    break;
                case KeyEvent.KEYCODE_DPAD_RIGHT :
                    mShip.dir=2;
                    break;
                case KeyEvent.KEYCODE_DPAD_UP :
                    mThread.FireGunship();
                    break;
                default:
                    mShip.dir=0;
            }
        }
        return false;
    }
} // GameView
