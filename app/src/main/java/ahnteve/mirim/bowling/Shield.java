package ahnteve.mirim.bowling;

import android.util.Log;

public class Shield {
    private int shield[][]=new int[6][8];

    public Shield(String str){
        String tmp[]=str.split("\n");
        String s;

        for(int i=1; i<tmp.length; i++){
            s=tmp[i];
            for(int j=0; j<8; j++){
                switch (s.charAt(j)){
                    case '-':
                        shield[i-1][j]=-1;
                        break;
                    default:
                        shield[i-1][j]=s.charAt(j)-48;
                }
            } // j
        } // i

        Log.v("Shield", "Make Shield Success");
    }

    public int GetShield(int kind, int num){
        return shield[kind][num];
    }
}
