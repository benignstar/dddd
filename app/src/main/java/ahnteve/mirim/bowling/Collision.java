package ahnteve.mirim.bowling;

import java.util.Random;

public class Collision {
    Random rnd=new Random();

    public void CheckCollision(){
        Check_1();      // 아군 미사일과 적군의 충돌
        Check_2();
        Check_3();
        Check_4();
        if(GameView.isBoss){    // Boss Stage
            Check_5();
            Check_6();
        }
    }

    // 아군 미사일과 적 충돌돌
   private void Check_1() {
        int x, y, x1, y1, w, h;
        int r=rnd.nextInt(100)-93; // 0~6 : 보너스 나올 확률

        NEXT:
        for(int p = GameView.mGun.size()-1; p>=0; p--){
            x=GameView.mGun.get(p).x;   // 미사일 좌표
            y=GameView.mGun.get(p).y;

            for(int i=0; i<6; i++){
                for(int j=0; j<8; j++){
                    if(GameView.mEnemy[i][j].isDead) continue;

                    x1=GameView.mEnemy[i][j].x;         // 적군의 좌표
                    y1=GameView.mEnemy[i][j].y;
                    w=GameView.mEnemy[i][j].w;          // 충돌을 조사할 범위
                    h=GameView.mEnemy[i][j].h;

                    if(Math.abs(x-x1)>w || Math.abs(y-y1)>h) continue; // 충돌 없음

                    if(GameView.isPower)
                        GameView.mEnemy[i][j].shield-=4;    // 강화 미사일은 보호막 4 감소
                    else
                        GameView.mEnemy[i][j].shield--;

                    if(GameView.mEnemy[i][j].shield>0){ // 보호막이 있으면
                        // 작은 폭발은 미사일 위치에
                        GameView.mExp.add(new Explosion(x, y, Explosion.SMALL));
                        GameView.score+=(6-i)*100;
                    } else {
                        GameView.mEnemy[i][j].isDead=true;
                        GameView.mMap.enemyCnt--;
                        // 큰 폭발은 적군의 중심부에
                        GameView.mExp.add(new Explosion(x1, y1, Explosion.BIG));
                        GameView.score+=(6-i)*200;

                        if(r>0) // 보너스가 있으면 보너스 생성
                            GameView.mBonus.add(new Bonus(x1, y1, r));
                    }
                    GameView.mGun.remove(p);
                    continue NEXT;  // 다음 미사일 조사
                } // for j
            } // for i
        } // for p
    }

    // 적군 미사일과 아군기 충돌
    private void Check_2() {
        if(GameView.mShip.undead || GameView.mShip.isDead) return;

        int x, y, x1, y1, w, h;
        x=GameView.mShip.x;
        y=GameView.mShip.y;
        w=GameView.mShip.w;
        h=GameView.mShip.h;

        for(int i=GameView.mMissile.size()-1; i>=0; i--){
            x1=GameView.mMissile.get(i).x;
            y1=GameView.mMissile.get(i).y;

            if(Math.abs(x1-x)>w || Math.abs(y1-y)>h) // 충돌 없음
                continue;
            GameView.mMissile.remove(i);
            GameView.mShip.shield--;
            if(GameView.mShip.shield>=0) // 보호막이 남아 있으면
                GameView.mExp.add(new Explosion(x1, y1, Explosion.SMALL));
            else {
                GameView.mExp.add(new Explosion(x, y, Explosion.MYSHIP));
                GameView.shipCnt--;
            }
            break;
        }
    }

    // 아군기와 적기와의 충돌
    private void Check_3() {
        if(GameView.mShip.isDead) return;

        int x, y, x1, y1, w, h;

        x=GameView.mShip.x;
        y=GameView.mShip.y;
        w=GameView.mShip.w;
        h=GameView.mShip.h;

        for(int i=0; i<6; i++){
            for(int j=0; j<8; j++){
                if(GameView.mEnemy[i][j].isDead) continue;

                x1=GameView.mEnemy[i][j].x;
                y1=GameView.mEnemy[i][j].y;

                if (Math.abs(x1-x)>w || Math.abs(y1-y)>h) // 충돌 없음
                    continue;

                GameView.mEnemy[i][j].isDead=true;
                GameView.mMap.enemyCnt--;
                GameView.score+=(6-i)*200;
                if(GameView.mShip.undead)
                    GameView.mExp.add(new Explosion(x1, y1, Explosion.BIG));
                else {
                    GameView.mShip.isDead=true;
                    GameView.shipCnt--;
                    GameView.mExp.add(new Explosion(x, y, Explosion.MYSHIP));
                }
                return;
            } // for j
        } // for i
    }

    // 아군과 보너스의 충돌
    private void Check_4() {
        int x, y, x1, y1, w, h, bonus=0;

        x=GameView.mShip.x;
        y=GameView.mShip.y;
        w=GameView.mShip.w;
        h=GameView.mShip.h;

        for(int i=GameView.mBonus.size()-1; i>=0; i--){
            x1=GameView.mBonus.get(i).x;
            y1=GameView.mBonus.get(i).y;
            if(Math.abs(x-x1)>w*2 || Math.abs(y-y1)>h*2) // 충돌 없음
                continue;

            bonus=GameView.mBonus.get(i).kind;
            GameView.mBonus.remove(i);

            switch (bonus){
                case 1:
                    GameView.isDouble=true;
                    break;
                case 2:
                    GameView.isPower=true;
                    break;
                case 3 :
                    if(GameView.gunDelay>6)
                        GameView.gunDelay-=2;
                    break;
                case 4 :
                    GameView.mShip.shield=6;
                    break;
                case 5 :
                    GameView.mShip.undeadCnt=100;
                    GameView.mShip.undead=true;
                    break;
                case 6 :
                    if(GameView.shipCnt<4)
                        GameView.shipCnt++;
            }
        }
    }

    // Boss 미사일과 아군과의 충돌
    private void Check_5() {
        if(GameView.mShip.undead) return;

        int x, y, x1, y1;
        int w, h;

        x=GameView.mShip.x;
        y=GameView.mShip.y;
        w=GameView.mShip.w;
        h=GameView.mShip.h;

        for(int i=GameView.mBsMissile.size()-1; i>=0; i--){
            x1=GameView.mBsMissile.get(i).x;
            y1=GameView.mBsMissile.get(i).y;

            if(Math.abs(x1-x)<=w && Math.abs(y1-y)<=h){
                GameView.mBsMissile.remove(i);
                GameView.mShip.isDead=true;
                GameView.mExp.add(new Explosion(x, y, Explosion.MYSHIP));
                GameView.shipCnt--;
            }
        }
    }

    // 아군 미사일과 Boss와 충돌
    private void Check_6() {
        int x1, x2, x3, y1, w, h;
        int x, y, damage=1;
        if(GameView.isPower) damage=4;

        w=GameView.mBoss.w/2;
        h=GameView.mBoss.h;
        x1=GameView.mBoss.x;
        x2=x1-w;
        x3=x1+w;
        y1=GameView.mBoss.y;

        for(int i=GameView.mGun.size()-1; i>=0; i--){
            x=GameView.mGun.get(i).x;
            y=GameView.mGun.get(i).y;

            // Boss Center
            if(Math.abs(x-x1)<w && Math.abs(y-y1)<h){
                GameView.mBoss.shield[EnemyBoss.CENTER]-=damage;
                GameView.mGun.remove(i);

                if(GameView.mBoss.shield[EnemyBoss.CENTER]>=0){
                    GameView.mExp.add(new Explosion(x,y,Explosion.SMALL));
                    GameView.score+=50;
                    continue;
                }
                ClearAllEnemies();
                return;
            } // if

            // 보스의 왼쪽
            if(Math.abs(x-x2)<w && Math.abs(y-y1)<h && GameView.mBoss.shield[EnemyBoss.LEFT]>0){
                GameView.mBoss.shield[1]-=damage;
                GameView.mGun.remove(i);

                if(GameView.mBoss.shield[EnemyBoss.LEFT]>0) {
                    GameView.mExp.add(new Explosion(x, y, Explosion.SMALL));
                    GameView.score += 50;
                    continue;
                }

                GameView.mExp.add(new Explosion(x2, y1, Explosion.BIG));
                GameView.score+=1000;
                GameView.mGun.remove(i);

                if(GameView.mBoss.shield[EnemyBoss.RIGHT]>0){
                    GameView.mBoss.imgBoss=GameView.mBoss.imgSpt[EnemyBoss.RIGHT];
                } else GameView.mBoss.imgBoss=GameView.mBoss.imgSpt[EnemyBoss.CENTER];
                continue;
            }

            if(Math.abs(x-x3)<w && Math.abs(y-y1)<h && GameView.mBoss.shield[EnemyBoss.RIGHT]>0){
                GameView.mBoss.shield[EnemyBoss.RIGHT]-=damage;
                GameView.mGun.remove(i);

                if(GameView.mBoss.shield[EnemyBoss.RIGHT]>0){
                    GameView.mExp.add(new Explosion(x,y,Explosion.SMALL));
                    GameView.score+=50;
                    continue;
                }

                GameView.mExp.add(new Explosion(x3, y1, Explosion.BIG));
                GameView.score+=1000;
                GameView.mGun.remove(i);

                if(GameView.mBoss.shield[EnemyBoss.LEFT]>0){
                    GameView.mBoss.imgBoss=GameView.mBoss.imgSpt[EnemyBoss.LEFT];
                } else GameView.mBoss.imgBoss=GameView.mBoss.imgSpt[EnemyBoss.CENTER];

            }
        }
    }

    private void ClearAllEnemies(){
        int x1, x2, x3, y1, w;

        w=GameView.mBoss.w/2;
        x1=GameView.mBoss.x;
        x2=x1-w;
        x3=x1+w;
        y1=GameView.mBoss.y;

        GameView.mExp.add(new Explosion(x1, y1, Explosion.BOSS));
        GameView.score+=5000;

        if(GameView.mBoss.shield[EnemyBoss.LEFT]>0){
            GameView.mBoss.shield[EnemyBoss.LEFT]=0;
            GameView.mExp.add(new Explosion(x2, y1, Explosion.BOSS));
        }

        if(GameView.mBoss.shield[EnemyBoss.RIGHT]>0){
            GameView.mBoss.shield[EnemyBoss.RIGHT]=0;
            GameView.mExp.add(new Explosion(x3, y1, Explosion.BOSS));
        }

        for(BossMissile tmp : GameView.mBsMissile){
            GameView.mExp.add(new Explosion(tmp.x, tmp.y, Explosion.BIG));
        }
        GameView.mBsMissile.clear();

        for(int i=0; i<6; i++){
            for(int j=0; j<8; j++){
                if(GameView.mEnemy[i][j].shield>0){
                    x1=GameView.mEnemy[i][j].x;
                    y1=GameView.mEnemy[i][j].y;
                    GameView.mExp.add(new Explosion(x1, y1, Explosion.BIG));
                    GameView.mEnemy[i][j].shield=0;
                    GameView.mMap.enemyCnt--;
                }
            }
        }
    }
}
