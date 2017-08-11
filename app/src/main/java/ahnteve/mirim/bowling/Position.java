package ahnteve.mirim.bowling;

import android.util.Log;

public class Position {
    private int posX[][]=new int[6][8]; // 캐릭터의 위치를 저장할 변수 선언(여기에서 사용한 좌표는 모두 View의 절대 좌표이다)
    private int posY[][]=new int[6][8];
    private int enemy[][]=new int[6][8];

    public Position(String str){
        String tmp[]=str.split("\n");
        String s;
        char ch;

        // 적군 캐릭터 번호 구하기
        for(int i=1; i<tmp.length; i++){
            s=tmp[i];
            for(int j=0; j<8; j++){
                ch=s.charAt(j);
                if(ch=='-')
                    enemy[i-1][j]=-1;
                else if(ch<='9')
                    enemy[i-1][j]=ch-48;
                else enemy[i-1][j]=ch-87;
            } // j
        } // i

        int top=100;    // 점수 등을 표시할 수 있도록 View의 맨 위에서 100의 거리를 둔다
        int left=72;    // 좌측 여백을 72로 설정한다
        int wid=48;     // 캐릭터의 상하좌우 간격은 48로 설정한다.
        int x;

        // 적군의 View 좌표 구하기
        for(int i=0; i<6; i++){
            if(i<=1){
                for(int j=0; j<8; j++){
                    posX[i][j]=j*wid+left;
                    posY[i][j]=i*wid+top;
                }
            }
            else {
                for(int j=0; j<8; j++){
                    if(j%2 == 0)
                        x=3-j/2;
                    else
                        x=j/2+4;
                    posX[i][j]=x*wid+left;
                    posY[i][j]=i*wid+top;
                }
            } // if
        } // for i

        Log.v("Position 5, 0", ""+posX[5][0]+" "+posX[5][0]);
    }

    public int GetEnemyNum(int kind, int num){
        return enemy[kind][num];
    }

    // Position - X
    public int GetPosX(int kind, int num){
        return posX[kind][num];
    }

    // Position - Y
    public int GetPosY(int kind, int num){
        return posY[kind][num];
    }
}
