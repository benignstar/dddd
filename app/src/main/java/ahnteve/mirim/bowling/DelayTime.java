package ahnteve.mirim.bowling;

import android.util.Log;

public class DelayTime {
    private int Delay[][]=new int[6][8];

    public DelayTime(String str){
        String tmp[]=str.split("\n");
        String s;

        for(int i=1; i<tmp.length; i++){
            for(int j=0; j<8; j++){
                s=tmp[i].substring(j*4, (j+1)*4).trim(); // 각 행의 문자열을 네 문자씩 잘라 좌우의 공백을 제거
                if(s.equals("---"))
                    Delay[i-1][j]=-1;
                else
                    Delay[i-1][j]=Integer.parseInt(s);
            } // j
        } // i

        Log.v("Delay Success", "success");
    }

    public int GetDelay(int kind, int num){
        return Delay[kind][num];
    }
}
