package gameState;

import entities.Enemy;
import entities.EnemyManager;
import entities.Player;
import levels.LevelManager;
import main.Game;
import ui.GameOverOverLay;
import ui.PauseOverlay;
import utilz.LoadSave;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.util.Random;

import static utilz.Constants.Environment.*;

public class Playing extends State implements Statemethods {
    private Player player;
    private LevelManager levelManager;
    private EnemyManager enemyManager;
    private PauseOverlay pauseOverlay;
    private boolean paused = true;
    private GameOverOverLay gameOverOverLay;

    private  int xLvlOffset;
    private  int leftBorder = (int)(0.2*Game.SCALE);
    private  int rightBorder = (int)(0.8*Game.GAME_WIDTH);
    private  int lvlTilesWide = LoadSave.GetLevelData()[0].length;
    private  int maxTilesOffset = lvlTilesWide-Game.TILES_IN_WIDTH;
    private int maxLvlOffsetX = maxTilesOffset *Game.TILES_SIZE;

    private BufferedImage getBackGroundImg,bigCloud,smallCloud,backGroundImg;
    private  int[] smallCloudsPos;
    private Random rnd = new Random();

    private boolean gameOver;

    public Playing(Game game) {
        super(game);
        initClasses();

        backGroundImg = LoadSave.GetSpriteAtlas(LoadSave.PLAYING_BACKGROUND_IMG);
        bigCloud = LoadSave.GetSpriteAtlas(LoadSave.BIG_CLOUDS);
        smallCloud = LoadSave.GetSpriteAtlas(LoadSave.SMALL_CLOUDS);
        smallCloudsPos = new int[8];
        for(int i=0;i<smallCloudsPos.length;i++)
            smallCloudsPos[i]=(int)((90*Game.SCALE)+rnd.nextInt((int)(100*Game.SCALE)));
    }

    private void initClasses() {
        levelManager = new LevelManager(game);
        enemyManager = new EnemyManager(this);
        player = new Player(200,200,(int)(64*Game.SCALE),(int)(40*Game.SCALE),this);
        player.loadLvlData(levelManager.getCurrentLevel().getLvlData());
        pauseOverlay = new PauseOverlay(this);
        gameOverOverLay=new GameOverOverLay(this);
    }
    public Player getPlayer(){
        return player;
    }

    public void windowFocusLost() {
        player.resetDirBooleans();
    }

    @Override
    public void update() {
        if(!paused&&!gameOver) {
            levelManager.update();
            player.update();
            enemyManager.update(levelManager.getCurrentLevel().getLvlData(),player);
            checkCloseToBorder();
        }
        else{
            pauseOverlay.update();
        }

    }

    private  void checkCloseToBorder(){
        int playerX = (int)player.getHitBox().x;
        int diff = playerX - xLvlOffset;

        if(diff>rightBorder)
            xLvlOffset+=diff-rightBorder;
        else if(diff<leftBorder)
            xLvlOffset += diff - leftBorder;

        if(xLvlOffset>maxLvlOffsetX)
            xLvlOffset = maxLvlOffsetX;
        else if(xLvlOffset<0)
            xLvlOffset = 0;

    }

    @Override
    public void test() {

    }

    @Override
    public void draw(Graphics g) {
        g.drawImage(backGroundImg,0,0,Game.GAME_WIDTH,Game.GAME_HEIGHT,null);
             drawClouds(g);
        levelManager.draw(g,xLvlOffset);
        player.render(g,xLvlOffset);
        enemyManager.draw(g,xLvlOffset);

        if(paused) {
            g.setColor(new Color(0, 0, 0, 150));
            g.fillRect(0, 0, Game.GAME_WIDTH, Game.GAME_HEIGHT);
            pauseOverlay.draw(g);
        }else if(gameOver)
            gameOverOverLay.draw(g);
    }

    private void drawClouds(Graphics g) {
        for(int i=0;i<3;i++)
        g.drawImage(bigCloud,0+i*BIG_CLOUD_WIDTH-(int)(xLvlOffset*0.3),(int)(204*Game.SCALE),BIG_CLOUD_WIDTH,BIG_CLOUD_HEIGHT,null);
           for(int i=0;i<smallCloudsPos.length;i++)
        g.drawImage(smallCloud,SMALL_CLOUD_WIDTH*4*i-(int)(xLvlOffset*0.7),smallCloudsPos[i],SMALL_CLOUD_WIDTH,SMALL_CLOUD_HEIGHT,null);
    }

    public void resetAll(){
        //reset playing,enemy,lvl etc
        gameOver = false;
        paused = false;
        player.resetAll();
        enemyManager.resetAllEnemies();


    }
    public void setGameOver(boolean gameOver){
        this.gameOver=gameOver;
    }

     public void checkEnemyHit(Rectangle2D.Float attackBox){
        enemyManager.checkEnemyHit(attackBox);
     }
    @Override
    public void mouseClicked(MouseEvent e) {
        if(!gameOver)
        if(e.getButton()==MouseEvent.BUTTON1)
            player.setAttacking(true);

    }

    @Override
    public void mousePressed(MouseEvent e) {
        if(!gameOver)
        if(paused)
            pauseOverlay.mousePressed(e);

    }

    @Override
    public void mouseReleased(MouseEvent e) {
        if(!gameOver)
        if(paused)
            pauseOverlay.mouseReleased(e);
    }

    @Override
    public void mouseMoved(MouseEvent e) {
        if(!gameOver)
        if(paused)
            pauseOverlay.mouseMoved(e);
    }


    public void unpauseGame(){
        paused = false;

    }
    public void mouseDragged(MouseEvent e){
        if(!gameOver)
            if(paused)
                pauseOverlay.mouseDragged(e);
    }

    @Override
    public void keyPressed(KeyEvent e) {
        if(gameOver)
            gameOverOverLay.keyPressed(e);
        switch(e.getKeyCode()){

            case KeyEvent.VK_A:
                player.setLeft(true);
                break;
            case KeyEvent.VK_D:
                player.setRight(true);
                break;
            case KeyEvent.VK_SPACE:
                player.setJump(true);
                break;
            case KeyEvent.VK_BACK_SPACE:
                Gamestate.state=Gamestate.MENU;
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        if(!gameOver)
        switch(e.getKeyCode()){

            case KeyEvent.VK_A:
               player.setLeft(false);
                break;
            case KeyEvent.VK_D:
                player.setRight(false);
                break;
            case KeyEvent.VK_SPACE:
                player.setJump(false);
                break;
        }

    }
}
