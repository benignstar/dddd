package ahnteve.mirim.bowling;

import java.io.IOException;
import java.io.InputStream;

public class MapTable {
    // 16 방향에 따른 이동 거리 삼각함수표
    public float sx[] = {0f, 0.39f, 0.75f, 0.93f, 1, 0.93f, 0.75f, 0.39f, 0f, -0.39f, -0.75f, -0.93f, -1f, -0.93f, -0.75f, -0.39f};
    public float sy[] = {-1f, -0.93f, -0.75f, -0.39f, 0f, 0.39f, 0.75f, 0.93f, 1f, 0.93f, 0.75f, 0.39f, 0f, -0.39f, -0.75f, -0.93f};

    private Path mPath;
    private Selection mSelect;
    private DelayTime mDelay;
    private Position mPos;
    private Shield mShield;

    public int enemyCnt;    // 현 스테이지에서 적군의 생존자 수
    public int attackTime;  // 공격 시작 시간

    public int syncCnt = 0; // 기준 위치로부터의 편자: 좌표 보정용
    public int dirLen = 52; // 이동할 거리
    public int dir = 4;     // 이동할 방향 : 3시 방향
    public int dirCnt = 0;  // 실제 이동한 거리 : 방향 전환 및 싱크 유지용

    public MapTable(){}

    public void ReadMap(int num){   // Read File
        num--;

        InputStream fi=GameView.mContext.getResources().openRawResource(R.raw.stage01+num);

        try{
            byte[] data=new byte[fi.available()]; // 파일의 크기를 배열의 크기로
            fi.read(data);
            fi.close();

            String s=new String(data, "EUC-KR");
            MakeMap(s);
        }catch (IOException e){}
    }

    public void MakeMap(String str){
        int n1=str.indexOf("selection");
        mPath=new Path(str.substring(0, n1));   // 찾은 문자열 직전까지를 잘라 Path Class에 전달하고 그 결과를 변수에 담는다

        int n2=str.indexOf("delay");
        mSelect=new Selection(str.substring(n1, n2));
        enemyCnt=mSelect.GetEnemyCount();   // 현재 스테이지에 등장하는 적군의 수 : 적군이 사망하면 이 값을 감소시켜 스테이지가 Clear되었는지 확인

        n1=str.indexOf("position");
        mDelay=new DelayTime(str.substring(n2, n1));
        attackTime=mDelay.GetDelay(0,5);    // 마지막 캐릭터의 Delay값 : 적군 전체가 전투 대형을 유지했는지 판단하는 데 사용

        n2=str.indexOf("shield");
        mPos=new Position(str.substring(n1, n2));

        mShield=new Shield(str.substring(n2));
    }

    public SinglePath GetPath(int num){
        return mPath.GetPath(num);
    }

    public int GetSelection(int kind, int num){
        return mSelect.GetSelection(kind, num);
    }

    public int GetDelay(int kind, int num){
        return mDelay.GetDelay(kind, num);
    }

    public int GetPosX(int kind, int num){
        return mPos.GetPosX(kind, num);
    }

    public int GetPosY(int kind, int num){
        return mPos.GetPosY(kind, num);
    }

    public int GetEnemyNum(int kind, int num){
        return mPos.GetEnemyNum(kind, num);
    }

    public int GetShield(int kind, int num){
        return mShield.GetShield(kind, num);
    }
}
