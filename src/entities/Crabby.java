package entities;
import main.Game;

import static utilz.Constants.Directions.LEFT;
import static utilz.Constants.Directions.RIGHT;
import static utilz.Constants.EnemyConstants.*;
import static utilz.HelpMethods.*;

public class Crabby extends Enemy{
    public Crabby(float x, float y) {

        super(x, y, CRABBY_WIDTH, CRABBY_HEIGHT, CRABBY);
        initHitbox(x,y,(int)(22* Game.SCALE),(int)(19*Game.SCALE));
    }

    public void update(int[][] lvlData,Player player){
        updateMove(lvlData,player);
        updateAnimationTick();

    }

    private void updateMove(int[][] lvlData,Player player) {
        if (firstUpdate){
         firstUpdateCheck(lvlData);

        }
        if(inAir) {
           updateInAir(lvlData);
        }else{
            switch (enemyState){
                case IDLE :
                    newState(RUNNING);
                    break;

                case RUNNING:
                    if(canSeePlayer(lvlData,player))
                        turnTowardsPlayer(player);
                    if(isPlayerCloseAttack(player))
                    newState(ATTACK);
                    move(lvlData);
                    break;
            }

        }

    }
    public int flipX(){
        if(walkDir==RIGHT)
            return width;
        else
            return  0;
    }
    public int flipY(){
          if(walkDir==RIGHT)
              return -1;
          else{
              return 1;
          }
    }

}
