package ahnteve.mirim.bowling;

import android.util.Log;

import java.util.ArrayList;

public class Path {
    private ArrayList<SinglePath> mPath=new ArrayList<SinglePath>();

    public Path(String str){
        String tmp[]=str.split("\n");

        for(int i=0; i<tmp.length; i++){
            // 주석문이거나 공백이면 무시
            if(tmp[i].indexOf("//")>=0 || tmp[i].trim().equals(""))
                continue;
            mPath.add(new SinglePath(tmp[i]));
        }

        Log.v("Path", "Make Path Success");
    }

    public SinglePath GetPath(int index){
        return mPath.get(index);
    }
}

class SinglePath {
    public int startX;  // 시작 좌표
    public int startY;
    public int dir[];   // 경로 방향
    public int len[];   // 경로 거리

    public SinglePath(String str){
        String tmp[]=str.split(":");    // tmp[1]:시작 위치, tmp[2]:path

        int n=tmp[1].indexOf(',');
        startX=Integer.parseInt(tmp[1].substring(0, n).trim());
        startY=Integer.parseInt(tmp[1].substring(n+1).trim());

        String stmp[]=tmp[2].split(",");    // 경로를 ','을 기준으로 나누어 배열에 저장
        n=stmp.length;                      // 경로의 개수

        dir=new int[n];                     // 변수 선언부에서 만들어 둔 배열의 크기를 저장
        len=new int[n];

        int p;
        for(int i=0; i<n; i++){
            p=stmp[i].indexOf('-');
            dir[i]=Integer.parseInt(stmp[i].substring(0, p).trim());
            len[i]=Integer.parseInt(stmp[i].substring(p+1).trim());
        }

    }
}
