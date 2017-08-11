package ahnteve.mirim.bowling;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;

import java.util.Random;

public class Sprite {
    final static int ENTER = 1;     // 캐릭터 입장
    final static int BEGINPOS=2;    // 전투 대형으로 가기 위해 좌표 계산
    final static int POSITION=3;    // 전투 대형으로 이동 중
    final static int SYNC=4;        // 전투 대형에서 대기 중
    final static int ATTACK=5;      // 공격 중
    final static int BEGINBACK=6;   // View를 벗어나서 다시 입장할 준비(탈영)
    final static int BACKPOS=7;     // 다시 입장 중(탈영병 복귀)

    public int x, y;                // 캐릭터의 좌표
    public int w, h;                // 크기
    public boolean isDead;          // 사망
    public int shield;              // 보호막
    public int status;              // 상태(위의 1~7)

    public Bitmap imgSprite;        // 현재 방향의 이미지

    private SinglePath sPath;       // 캐릭터가 이동할 Path 1줄 (입장 및 공격 루트)
    private float sx, sy;           // 캐릭터 이동 속도
    private int sncX;               // 싱크 위치로부터 떨어져 있는 거리
    private Bitmap imgSpt[]=new Bitmap[16];
    private int sKind, sNum;        // 캐릭터의 종류와 번호
    private int pNum, col;          // Path 번호와 현재의 경로 배열의 위치
    private int delay, dir, len;    // 입장 시 지연시간, 현재의 방향, 남은 거리
    private int posX, posY;         // 이동해야 할 목적지(전투 대형) 좌표
    private int aKind;              // 공격 경로 번호
    private Random rnd=new Random();// 난수

    private int diff[]={7, 4, 2};   // EASY, MEDIUM, HARD
    private int df;                 // 난이도

    public void MakeSprite(int kind, int num){
        sKind=kind;
        sNum=num;

        if(GameView.mMap.GetSelection(kind, num)==-1){
            isDead=true;
            return;
        }

        int enemy=GameView.mMap.GetEnemyNum(kind, num);
        imgSpt[0]= BitmapFactory.decodeResource(GameView.mContext.getResources(), R.drawable.enemy00+enemy);

        int sw=imgSpt[0].getWidth();
        int sh=imgSpt[0].getHeight();
        w=sw/2;
        h=sh/2;

        // 16방향으로 회전한 이미지 만들기
        Canvas canvas=new Canvas();
        for(int i=1; i<16; i++){
            imgSpt[i]=Bitmap.createBitmap(sw, sh, Bitmap.Config.ARGB_8888); // 캐릭터 크기와 같은 빈 비트맵을 만든다
            canvas.setBitmap(imgSpt[i]);    // Canvas에 빈 비트맵을 올려놓는다
            canvas.rotate(22.5f, w, h);     // Canvas를 시계 방향으로 22.5도 (1/16) 회전한다
            canvas.drawBitmap(imgSpt[0], 0, 0, null);   // 회전한 Canvas에 원본 이미지를 출력한다
        }
        ResetSprite();
    }

    public void ResetSprite() {
        // MapTable에서 맵 읽기
        pNum=GameView.mMap.GetSelection(sKind, sNum);   // 자신의 Path번호
        delay=GameView.mMap.GetDelay(sKind, sNum);      // Delay 시간 읽기
        shield=GameView.mMap.GetShield(sKind, sNum);    // 보호막

        posX=GameView.mMap.GetPosX(sKind, sNum);
        posY=GameView.mMap.GetPosY(sKind, sNum);

        GetPath(pNum);
        status=ENTER;   // 초기값은 입장
        isDead=false;

        df=GameView.difficult;
    }

    // Path
    public void GetPath(int num){
        sPath= GameView.mMap.GetPath(num); // Path 읽기

        // Path의 시작 좌표
        if(sPath.startX != -99) // 캐릭터의 시작 좌표가 -99이면 공격 경로로 사용 : 공격 경로의 시작 위치는 캐릭터의 현재 위치
            x=sPath.startX;
        if(sPath.startY != -99)
            y=sPath.startY;
        col=0;
        GetDir(col);
    }

    public void GetDir(int col){
        dir=sPath.dir[col];
        len=sPath.len[col];

        sx= GameView.mMap.sx[dir];  // 이동 속도
        sy=GameView.mMap.sy[dir];
        imgSprite=imgSpt[dir];      // 현재 방향의 이미지
    }

    public void Move(){
        if(isDead && (sKind!=5 || sNum!=0)) return;
        switch (status){
            case ENTER :
                EnterSprite();
                break;
            case BEGINPOS :
                BeginPos();
                break;
            case POSITION :
                Position();
                break;
            case SYNC :
                MakeSync();
                break;
            case ATTACK :
                Attack();
                break;
            case BEGINBACK :
                BeginbackPos();
                break;
            case BACKPOS :
                BackPosition();
                break;
        }
    }

    public void EnterSprite() {
        if(--delay >= 0) return;    // delay time이 끝나지 않았으면 대기

        x+=(int)(sx*8);             // 현재의 방향으로 최대 8픽셀 이동
        y+=(int)(sy*8);

        // 캐릭터 입장 시 아군을 공격할 방향 결정
        int dr=rnd.nextInt(5)+6; // 6~10 : 발사 방향
        if(len%15 ==0)
            ShootMissile(dr);
        len--;
        if(len>=0) return;          // 이동할 거리가 남았는지 검사

        col++;                      // 다음 방향 조사 준비
        if(col<sPath.dir.length)
            GetDir(col);
        else status=BEGINPOS;
    }

    // 전투 대형으로 이동 준비
    public void BeginPos() {
        // 이동 방향 결정
        if(x<posX+GameView.mMap.syncCnt){
            dir=2;      // 목적지보다 왼쪽에 있는 경우에는 북동쪽으로 이동
        }
        else dir=14;    // 오른쪽에 있는 경우는 북서쪽으로 이동
        if(y<posY)
            dir=(dir==2)? 6:10;

        sx=GameView.mMap.sx[dir];   // 이동 방향에 따른 속도 계산
        sy=GameView.mMap.sy[dir];
        imgSprite=imgSpt[dir];       // 현재 방향의 이미지
        status=POSITION;            // 전투 대형으로 이동 준비 끝
    }

    public void Position() {
        x+=(int) (sx*8);
        y+=(int) (sy*8);

        if(x<posX+GameView.mMap.syncCnt)
            dir=2;
        else dir=14;

        if(y<posY)
            dir=(dir==2)?6:10;

        // 수평 좌표 비교
        if(Math.abs(y-posY) <=4){   // 수평 위치 도착
            y=posY;
            if(x<posX+GameView.mMap.syncCnt)
                dir=4;      // 3시 방향으로 설정
            else dir=12;    // 9시 방향으로 설정

            if(Math.abs(x-(posX+GameView.mMap.syncCnt))<=4){
                x=posX+GameView.mMap.syncCnt;
                dir=0;          // 12시 방향으로 설정
            }

            if(y==posY && x==posX+GameView.mMap.syncCnt){
                imgSprite=imgSpt[0];
                sx=1;
                status=SYNC;
                return;
            }

            sx=GameView.mMap.sx[dir];
            sy=GameView.mMap.sy[dir];
            imgSprite=imgSpt[dir];

        }
    }

    public void MakeSync() {
        sncX=(int)GameView.mMap.sx[GameView.mMap.dir];  // 좌우 이동 방향 계산
        x+=sncX; // 좌 또는 우로 이동

        if(sKind==5 && sNum == 0){      // 대형에 가장 먼저 진입한 5레벨 0번 캐릭터가 싱크를 설정한다
            GameView.mMap.syncCnt+=sncX;// 최초 도착자가 좌우로 이동한 거리
            GameView.mMap.dirCnt++;     // 현재 방향으로 이동한 거리
            if(GameView.mMap.dirCnt >= GameView.mMap.dirLen){
                GameView.mMap.dirCnt=0;
                GameView.mMap.dirLen=104;   // 반대 방향으로 이동할 거리 : 최초 방향으로 52 이동했으므로 그의 2배수를 적용해 균형을 맞춘다
                GameView.mMap.dir=16-GameView.mMap.dir; // 이동 방향 반전
            }
        }
    }

    // 공격 루트 수령
    public void BeginAttack(int aKind){
        if(isDead || (sKind==5 && sNum==0)) return; // 사망자와 싱크 기준은 공격에 참여하지 않는다
        this.aKind=aKind;
        GetPath(aKind+10);  // 공격 경로 번호를 설정한다
        status=ATTACK;      // 현재 공격 상태임을 Move메서드에 알린다
    }

    public void Attack(){
        x+=(int) (sx*8);
        y+=(int) (sy*8);

        if(y<-100 || y>GameView.height+100 || x<-100 || x>GameView.width+100){
            status=BEGINBACK;
            return;
        }
        len--;
        if(len>=0) return;

        col++;
        if(col < sPath.dir.length){
            GetDir(col);
            if(dir>=6 && dir<=10)
                ShootMissile(dir);
        } else status=BEGINPOS;     // 공격을 끝내고 전투 대형으로 복귀
    }

    public void BeginbackPos(){
        y=-32;  // 복귀 시작점
        x=posX+GameView.mMap.syncCnt;   // 자신의 전투 대형 위치 계산

        imgSprite=imgSpt[0];
        status=BACKPOS;
    }

    public void BackPosition(){
        // 전투 대형이 좌우 어느 쪽으로 이동 중인가 계산
        sncX=(int) GameView.mMap.sx[GameView.mMap.dir];
        y+=2;   // 입장 속도는 2
        x+=sncX;// 전투 대형의 이동 방향과 맞추어 좌우로 이동하며 입장

        // 전투대형 복귀 후 마지막 공격 루트로 다시 공격 시작
        if(Math.abs(y-posY)<=4){
            GetPath(aKind+10);
            status=ATTACK;
        }
    }

    private void ShootMissile(int dir){
        if(rnd.nextInt(10)>=diff[df])
            GameView.mMissile.add(new Missile(x, y, dir));
    }

}
