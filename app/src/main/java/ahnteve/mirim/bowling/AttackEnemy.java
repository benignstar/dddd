package ahnteve.mirim.bowling;

import java.util.Random;

public class AttackEnemy {
    public int loop = 0;
    private Random rnd=new Random();
    private int r1, r2;

    public void ResetAttack() {
        loop=0;
    }

    // 공격 명령
    public void Attack(){
        if(GameView.mMap.enemyCnt<=10){ // 적이 10마리 이하이면
            AttackAll(); // 일제 총공격
            return;
        }

        loop++;
        int n=loop-(GameView.mMap.attackTime+120);
        // 마지막 캐릭터의 딜레이에 120을 더한 것이 공격 시작 시간
        if(n<0) return; // 모든 캐릭터가 입장할 때까지 대기

        switch (n%600){
            case 0:
                r1=rnd.nextInt(10)+1;
                AttackPath(3, 1, r1);
                AttackPath(3, 3, r1);
                AttackPath(2, 1, r1);
                break;
            case 50:
                r1=rnd.nextInt(10)+1;
                AttackPath(5, 4, r1);
                AttackPath(5, 2, r1);
                AttackPath(4, 0, r1);
                break;
            case 100:
                r1=rnd.nextInt(10)+1;
                AttackPath(3, 0, r1);
                AttackPath(3, 2, r1);
                AttackPath(2, 4, r1);
                break;
            case 150:
                r1=rnd.nextInt(10)+1;
                AttackPath(0, 2, r1);
                AttackPath(1, 3, r1);
                AttackPath(1, 4, r1);
                break;
            case 200:
                r1=rnd.nextInt(10)+1;
                AttackPath(5, 3, r1);
                AttackPath(5, 5, r1);
                AttackPath(4, 6, r1);
                break;
            case 250:
                r1=rnd.nextInt(10)+1;
                AttackPath(3, 6, r1);
                AttackPath(3, 4, r1);
                AttackPath(2, 2, r1);
                break;
            case 300:
                r1=rnd.nextInt(10)+1;
                r2=rnd.nextInt(10)+1;
                AttackPath(2, 7, r1);
                AttackPath(2, 5, r1);
                AttackPath(0, 5, r2);
                AttackPath(1, 1, r2);
                break;
            case 350:
                r1=rnd.nextInt(10)+1;
                r2=rnd.nextInt(10)+1;
                AttackPath(4, 6, r1);
                AttackPath(4, 5, r1);
                AttackPath(3, 5, r1);
                AttackPath(3, 7, r2);
                AttackPath(4, 4, r2);
                break;
            case 400:
                r1=rnd.nextInt(10)+1;
                r2=rnd.nextInt(10)+1;
                AttackPath(5, 6, r1);
                AttackPath(5, 1, r1);
                AttackPath(2, 6, r2);
                AttackPath(2, 3, r2);
                break;
            case 450:
                r1=rnd.nextInt(10)+1;
                r2=rnd.nextInt(10)+1;
                AttackPath(1, 2, r1);
                AttackPath(1, 6, r1);
                AttackPath(2, 0, r2);
                AttackPath(4, 3, r2);
                break;
            case 500:
                r1=rnd.nextInt(10)+1;
                r2=rnd.nextInt(10)+1;
                AttackPath(4, 2, r1);
                AttackPath(4, 1, r1);
                AttackPath(1, 5, r2);
                AttackPath(5, 7, r2);
                break;
        }
    }

    private void AttackPath(int kind, int num, int aKind){
        // 위에서 지시한 공격 루트로 이동 - Sprite Attack 참조
        GameView.mEnemy[kind][num].BeginAttack(aKind);
    }

    private void AttackAll(){
        for (int i=0; i<6; i++){
            for(int j=0; j<8; j++){
                if(GameView.mEnemy[i][j].status==Sprite.SYNC)
                    AttackPath(i, j, rnd.nextInt(10)+1);
            }
        }
    }
}
