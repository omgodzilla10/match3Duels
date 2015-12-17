package com.mygdx.game;

import java.util.ArrayList;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Animation.PlayMode;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.MoveToAction;
import com.badlogic.gdx.utils.TimeUtils;
import com.badlogic.gdx.utils.viewport.StretchViewport;

public class Match3Duels_Game implements Screen {
    final Match3Duels_Main game;
    
    /** The number of rows on the board. */
    static final int BOARD_ROWS = 7;
    
    /** The max number of moves a player can make before they have to
     * take a potion. */
    static final int MAX_MOVES = 6;
    
    /** The number of potions a player starts with. */
    static final int MAX_POTS = 3;
    
    /** The padding (in pixels) between gem sprites. */
    static final int SPRITE_PADDING = 15;
    
    /** The speed that the fire spell animation moves along the y axis. */
    static final int FIRE_SPELL_SPEED = 500;
    
    /** The time, in seconds, between frames in an animation. */
    static final float ANIM_DURATION = 0.1f;
    
    /** The global animation speed of each gem when moved. */
    final static float GEM_MOVE_DURATION = 0.15f;
    
    /** The maximum number of gems allowed to be in use. */
    final int MAX_GEMS = 5;
    
    /** The width of the screen (at execution). */
    private int screenWidth;
    
    /** The height of the screen (at execution). */
    private int screenHeight;
    
    /** Whether the fireGem has been fired or not. */
    private static boolean fireAnimFired;
    
    /** The elapsed time since game execution. */
    private float elapsedTime;
    
    /** The amount of moves the player has made. */
    private static int movesMade;
    
    /** The number of potions the player currently has. */
    private static int potCount;
    
    /** The y coordinate where all spell effect animations stop. */
    private int animThreshold;
    
    /** The y coordinate where all spell effect animations begin. */
    private static int animStart; //MAKE THIS A CONSTANT WHEN UI IS IMPLEMENTED.
    
    /** Used to check for matches on the first frame. */
    private static boolean firstFrame;
    
    /** An array for each of the gem actors on the board. */
    private static GemActor[][] boardGemArray;
    
    /** The actor for the potion count UI element. */
    private static Actor potionCounter;
    
    /** The stage on which the board and UI elements will appear. */
    private static Stage gameStage;
    
    /** The stage's viewport. */
    private StretchViewport sViewport;
    
    /** Used to animate the gem being moved by the player. */
    private static MoveToAction gemMoveAction;
    
    /** Used to animate the gem being swapped with the moved gem. */
    private static MoveToAction gemSwapAction;
    
    /** The animation for the fire gem. */
    private static Animation fireAnim;
    
    /** The texture for the fire animation. */
    private Texture fireAnimTex;
    
    /** The TextureRegion for the fire animation. */
    private TextureRegion[] fireTexRegion;
    
    /** A Vector2 for the fire animation's coordinates. */
    private static Vector2 fireVector;
    
    /** The speed an animation will move along the X and Y axis. */
    private static Vector2 animMoveSpeed;
    
    /** A spritebatch, for elements not drawn using scene2D. */
    private SpriteBatch spriteBatch;
    
    private enum SpellType {
        FIRE,
        LIGHTNING,
        POISON,
        SHIELD,
        HEAL;
    }
    
    /** The constructor. 
     * 
     * @param game - Passed in when the game is launched 
     *               from the main menu.
     */
    public Match3Duels_Game(final Match3Duels_Main game) {
        this.game = game;
        
        movesMade = 0;
        elapsedTime = 0;
        firstFrame = true;
        potCount = MAX_POTS;
        
        fireAnimFired = false;
        
        screenWidth = Gdx.graphics.getWidth();
        screenHeight = Gdx.graphics.getHeight();
        
        sViewport = new StretchViewport(screenWidth, screenHeight);
        gameStage = new Stage(sViewport);
        
        Gdx.input.setInputProcessor(gameStage);
        
        gemMoveAction = new MoveToAction();
        gemSwapAction = new MoveToAction();
        
        //Initialize the board arrays with random gems
        boardGemArray = new GemActor[BOARD_ROWS][BOARD_ROWS];
        
        for(int row = 0; row < BOARD_ROWS; row++) {
            for(int col = 0; col < BOARD_ROWS; col++) {
                int idx = MathUtils.random(0, 4);
                
                switch(idx) {
                    case 0: boardGemArray[row][col] = new FireGemActor_01();
                            break;
                    case 1: boardGemArray[row][col] = new LightningGemActor_01();
                            break;
                    case 2: boardGemArray[row][col] = new PoisonGemActor_01();
                            break;
                    case 3: boardGemArray[row][col] = new ShieldGemActor_01();
                            break;
                    case 4: boardGemArray[row][col] = new HealGemActor_01();
                            break;
                }
                //Set position of each gem and center.
                boardGemArray[row][col].setPosition((row * (screenWidth / BOARD_ROWS))
                        + (screenWidth / BOARD_ROWS / 6), (col * (screenWidth / BOARD_ROWS)) 
                        + (screenWidth / BOARD_ROWS / 6) + 50);
                
                gameStage.addActor(boardGemArray[row][col]);
            }
        }
        
        potionCounter = new PotionCounter();
        gameStage.addActor(potionCounter);
        
        fireAnimTex = new Texture(Gdx.files.internal("gem_fire_anim.png"));
        fireTexRegion = new TextureRegion[6];
        TextureRegion[][] fireTexRegionTemp = new TextureRegion[1][6];

        fireTexRegionTemp = TextureRegion.split(fireAnimTex, 64, 64);
        for(int i = 0; i < fireTexRegion.length; i++) {
            fireTexRegion[i] = fireTexRegionTemp[0][i];
        }
        
        animThreshold = 3 * (screenHeight / 4);
        animStart = screenHeight / 2;
        
        fireAnim = new Animation(ANIM_DURATION, fireTexRegion);
        fireAnim.setPlayMode(PlayMode.LOOP);
        
        fireAnimFired = false;
        fireVector = new Vector2();
        animMoveSpeed = new Vector2();
        spriteBatch = new SpriteBatch();
    }
    
    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(1, 1, 1, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        
        elapsedTime += delta;
        
        //Initial check for matches
        if(firstFrame) {
            checkMatches();
            firstFrame = false;
        }
        
        if(Gdx.input.isKeyJustPressed(Keys.SPACE)) {
            if(potCount > 0) {
                movesMade = 0;
                potCount--;
                debugResetAllGems();
                checkMatches();
                ((PotionCounter) potionCounter).setNewSprite(potCount);
            }
        }
        
        if(Gdx.input.isKeyJustPressed(Keys.CONTROL_LEFT)) {
            movesMade = 0;
            debugResetAllGems();
        }
        
        if(Gdx.input.isKeyJustPressed(Keys.SHIFT_LEFT))
            startAnimation(SpellType.FIRE);
        
        gameStage.act(delta);
        gameStage.draw();
        
        spriteBatch.begin();
        
        if(fireAnimFired) {
            fireVector.x += animMoveSpeed.x * delta;
            fireVector.y += animMoveSpeed.y * delta;
            
            spriteBatch.draw(fireAnim.getKeyFrame(elapsedTime, true), fireVector.x, fireVector.y);
            
            if(fireVector.y > animThreshold) {
                fireAnimFired = false;
            }
        }
        
        spriteBatch.end();
    }
    
    private static void startAnimation(SpellType spellType) {
        switch(spellType) {
        case FIRE: fireAnimFired = true;
            fireVector.x = (float) (Math.random() * Gdx.graphics.getWidth());
            fireVector.y = animStart;
            
            //Ensure that the animation doesn't go off screen.
            if(fireVector.x < Gdx.graphics.getWidth() / 2)
                animMoveSpeed.x = (float) (Math.random() * 100);
            else animMoveSpeed.x = (float) -(Math.random() * 100);
            
            animMoveSpeed.y = FIRE_SPELL_SPEED;
            break;
        }
    }
    
    public static void moveGem(int dir, int signature) {
        if(movesMade < MAX_MOVES) {
            switch (dir) {
            case GemTouchListener.DIR_RIGHT: 
                moveGemRight(signature);
                checkMatches();
                movesMade++;
                break;
                
            case GemTouchListener.DIR_UP:
                moveGemUp(signature);
                checkMatches();
                movesMade++;
                break;
                
            case GemTouchListener.DIR_LEFT:
                moveGemLeft(signature);
                checkMatches();
                movesMade++;
                break;
                
            case GemTouchListener.DIR_DOWN:
                moveGemDown(signature);
                checkMatches();
                movesMade++;
                break;
            }
        }
    }
    
    private static void moveGemRight(int signature) {
        int col = findCol(signature);
        int row = signature - (col * BOARD_ROWS);
        
        System.out.println("Col: " + col);
        System.out.println("Row: " + row);
        
        GemActor actor1;
        GemActor actor2;
        
        if(col != BOARD_ROWS - 1) {
            int colSwap;
            int rowSwap;
            
            colSwap = col + 1;
            rowSwap = row;
            
            actor1 = (GemActor) boardGemArray[col][row];
            actor2 = (GemActor) boardGemArray[colSwap][rowSwap];
            swapGems(actor1, actor2);
            
            //Set new signatures for each 
            int tempSig = actor1.getSignature();
            actor1.setSignature(actor2.getSignature());
            actor2.setSignature(tempSig);
            
            boardGemArray[colSwap][rowSwap] = actor1;
            boardGemArray[col][row] = actor2;
        }
    }
    
    private static void moveGemUp(int signature) {
        int col = findCol(signature);
        int row = signature - (col * BOARD_ROWS);
        
        GemActor actor1;
        GemActor actor2;
        
        if(row != BOARD_ROWS - 1) {
            int colSwap;
            int rowSwap;
            
            colSwap = col;
            rowSwap = row + 1;
            
            actor1 = (GemActor) boardGemArray[col][row];
            actor2 = (GemActor) boardGemArray[colSwap][rowSwap];
            swapGems(actor1, actor2);
            
            //Set new signatures for each 
            int tempSig = actor1.getSignature();
            actor1.setSignature(actor2.getSignature());
            actor2.setSignature(tempSig);
            
            boardGemArray[colSwap][rowSwap] = actor1;
            boardGemArray[col][row] = actor2;
        }
    }
    
    private static void moveGemLeft(int signature) {
        int col = findCol(signature);
        int row = signature - (col * BOARD_ROWS);
        
        GemActor actor1;
        GemActor actor2;
        
        if(col != 0) {
            int colSwap;
            int rowSwap;
            
            colSwap = col - 1;
            rowSwap = row;
            
            actor1 = (GemActor) boardGemArray[col][row];
            actor2 = (GemActor) boardGemArray[colSwap][rowSwap];
            swapGems(actor1, actor2);
            
            //Set new signatures for each 
            int tempSig = actor1.getSignature();
            actor1.setSignature(actor2.getSignature());
            actor2.setSignature(tempSig);
            
            boardGemArray[colSwap][rowSwap] = actor1;
            boardGemArray[col][row] = actor2;
            
            boardGemArray[col][row].setColor(Color.BLACK);
        }
    }
    
    private static void moveGemDown(int signature) {
        int col = findCol(signature);
        int row = signature - (col * BOARD_ROWS);
        
        GemActor actor1;
        GemActor actor2;
        
        if(row != 0) {
            int colSwap;
            int rowSwap;
            
            colSwap = col;
            rowSwap = row - 1;
            
            actor1 = (GemActor) boardGemArray[col][row];
            actor2 = (GemActor) boardGemArray[colSwap][rowSwap];
            swapGems(actor1, actor2);
            
            //Set new signatures for each 
            int tempSig = actor1.getSignature();
            actor1.setSignature(actor2.getSignature());
            actor2.setSignature(tempSig);
            
            boardGemArray[colSwap][rowSwap] = actor1;
            boardGemArray[col][row] = actor2;
        }
    }
    
    private static void swapGems(GemActor actor1, GemActor actor2) {
        gemMoveAction.setPosition(actor2.getX(), actor2.getY());
        gemMoveAction.setDuration(GEM_MOVE_DURATION);
        
        gemSwapAction.setPosition(actor1.getX(), actor1.getY());
        gemSwapAction.setDuration(GEM_MOVE_DURATION);
        
        gemMoveAction.reset();
        gemSwapAction.reset();
        
        actor1.addAction(gemMoveAction);
        actor2.addAction(gemSwapAction);
    }
    
    private static int findCol(int signature) {
        int col = 0;
        
        if(signature < BOARD_ROWS)
            col = 0;
        else if(signature < (2 * BOARD_ROWS))
            col = 1;
        else if(signature < (3 * BOARD_ROWS))
            col = 2;
        else if(signature < (4 * BOARD_ROWS))
            col = 3;
        else if(signature < (5 * BOARD_ROWS))
            col = 4;
        else if(signature < (6 * BOARD_ROWS))
            col = 5;
        else if(signature < (7 * BOARD_ROWS))
            col = 6;
        
        return col;
    }
    
    private static void checkMatches() {
        checkMatchesHorizontal();
        checkMatchesVertical();
    }
    
    private static void checkMatchesHorizontal() {
        int matchLevel;
        
        for(int col = 0; col < BOARD_ROWS; col++) {
            
            for(int row = 0; row < BOARD_ROWS; row++) {
                
                matchLevel = 1;
                while(col != BOARD_ROWS - matchLevel && !boardGemArray[col][row].isMatched()
                        && boardGemArray[col + (matchLevel - 1)][row].getType() 
                        == boardGemArray[col + matchLevel][row].getType()
                        && !boardGemArray[col + (matchLevel - 1)][row].isInvisible()
                        && !boardGemArray[col + matchLevel][row].isInvisible()) {
                    
                    matchLevel++;
                    boardGemArray[col + (matchLevel - 1)][row].toggleMatched();
                }
                
                if(matchLevel >= 3) {
                    System.out.println("Horizontal: " + matchLevel);
                    hideMatchHorizontal(col, row, matchLevel);
                    matchLevel = 0;
                }
            }
        }
    }
    
    private static void checkMatchesVertical() {
        int matchLevel;
        
        for(int col = 0; col < BOARD_ROWS; col++) {
            for(int row = 0; row < BOARD_ROWS; row++) {
                matchLevel = 1;
                while(row != BOARD_ROWS - matchLevel && !boardGemArray[col][row].isMatched()
                        && boardGemArray[col][row + (matchLevel - 1)].getType() 
                        == boardGemArray[col][row + matchLevel].getType()
                        && !boardGemArray[col][row + (matchLevel - 1)].isInvisible()
                        && !boardGemArray[col][row + matchLevel].isInvisible()) {
                            
                    matchLevel++;
                    boardGemArray[col][row + (matchLevel - 1)].toggleMatched();
                }
                
                if(matchLevel >= 3) {
                    System.out.println("Vertical: " + matchLevel);
                    hideMatchVertical(col, row, matchLevel);
                    matchLevel = 0;
                }
                
                if(boardGemArray[col][row].isMatched())
                    boardGemArray[col][row].toggleMatched();
            }
        }
    }
    
    private static void hideMatchHorizontal(int col, int row, int matchLevel) {
        //For each gem in the match
        for(int i = 0; i < matchLevel; i++) {
            boardGemArray[col + i][row].setInvisible(true);
        }
        
        switch(boardGemArray[col][row].getType()) {
            case 0: startAnimation(SpellType.FIRE);
        }
    }
    
    private static void hideMatchVertical(int col, int row, int matchLevel) {
        //For each gem in the match
        for(int i = 0; i < matchLevel; i++) {
            boardGemArray[col][row + i].setInvisible(true);
        }
        
        switch(boardGemArray[col][row].getType()) {
            case 0: startAnimation(SpellType.FIRE);
        }
    }
    
    private static void fillEmptySlots() {
        int xPos;
        int yPos;
        int sig;
        
        for(int col = 0; col < BOARD_ROWS; col++) {
            for(int row = 0; row < BOARD_ROWS; row++) {
                if(boardGemArray[col][row].isInvisible()) {
                    xPos = (int) boardGemArray[col][row].getX();
                    yPos = (int) boardGemArray[col][row].getY();
                    sig = boardGemArray[col][row].getSignature();
                    
                    boardGemArray[col][row].remove();
                    boardGemArray[col][row] = newGem();
                    boardGemArray[col][row].setPosition(xPos, yPos);
                    boardGemArray[col][row].setSignature(sig);
                    
                    gameStage.addActor(boardGemArray[col][row]);
                }
            }
        }
        
        checkMatches();
    }
    
    private static void debugResetAllGems() {
        int xPos;
        int yPos;
        int sig;
        
        for(int col = 0; col < BOARD_ROWS; col++) {
            for(int row = 0; row < BOARD_ROWS; row++) {
                xPos = (int) boardGemArray[col][row].getX();
                yPos = (int) boardGemArray[col][row].getY();
                sig = boardGemArray[col][row].getSignature();
                
                boardGemArray[col][row].remove();
                boardGemArray[col][row] = newGem();
                boardGemArray[col][row].setPosition(xPos, yPos);
                boardGemArray[col][row].setSignature(sig);
                
                gameStage.addActor(boardGemArray[col][row]);
                
            }
        }
        
        checkMatches();
    }
    
    private static GemActor newGem() {
        int idx = MathUtils.random(0, 4);
        
        switch(idx) {
            case 0: return new FireGemActor_01();
            case 1: return new LightningGemActor_01();
            case 2: return new PoisonGemActor_01();
            case 3: return new ShieldGemActor_01();
            case 4: return new HealGemActor_01();
            default: return new FireGemActor_01();
        }
    }

    @Override
    public void resize(int width, int height) { 
        gameStage.getViewport().update(width, height);
    }
    @Override
    public void pause() {}
    @Override
    public void resume() {}
    @Override
    public void show() {}
    @Override
    public void hide() {}
    @Override
    public void dispose() {}
}
