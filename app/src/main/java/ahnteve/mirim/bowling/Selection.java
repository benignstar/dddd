package ahnteve.mirim.bowling;

import android.util.Log;

public class Selection {
    private int pathNum[][]=new int[6][8];
    private int enemyCnt;

    public Selection(String str){
        String tmp[]=str.split("\n");   // 매개변수로 넘어온 문자열을 행 단위로 잘라 배열에 저장한다
        String s;
        enemyCnt=0;
        char ch;

        for(int i=1; i<tmp.length; i++) {// 배열의 첫 요소는 selection이므로 1번째 배열부터 처리한다
            s=tmp[i];
            for(int j=0; j<8; j++){
                ch=s.charAt(j);
                switch (ch){
                    case '-':
                        pathNum[i-1][j]=-1; // '-'이면 아무것도 없음
                        break;
                    default:
                        enemyCnt++;         // 장애물의 수
                        if(ch<='9')
                            pathNum[i-1][j]=ch-48;  // 0~9
                        else
                            pathNum[i-1][j]=ch-87;  // 'a'~'z'
                } // switch
            } // j
        } // i

        Log.v("Selection", "Make Selection success");
    }

    // Path 번호 구하기
    public int GetSelection(int kind, int num){
        return pathNum[kind][num];
    }

    public int GetEnemyCount(){ // 전체 적군 수 구하기
        return enemyCnt;
    }
}
