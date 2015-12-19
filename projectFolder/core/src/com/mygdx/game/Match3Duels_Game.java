package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.MoveToAction;
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
    static final int SPRITE_PADDING = 10;
    
    /** The padding (in pixels) beneath the match-3 board. */
    final int BOTTOM_PADDING = Gdx.graphics.getHeight() / 9;
    
    /** The speed that the fire spell animation moves along the y axis. */
    static final int FIRE_SPELL_SPEED = 400;
    
    /** The time, in seconds, between frames in an animation. */
    static final float ANIM_DURATION = 0.1f;
    
    /** The global animation speed of each gem when moved. */
    final static float GEM_MOVE_DURATION = 0.15f;
    
    /** The length of the shield effect. */
    final static float SHIELD_DURATION = (float) 1.5;
    
    /** The rate at which a spell effect fades out. Higher = faster. */
    final static float EFFECT_FADE_RATE = 0.01f;
    
    /** The maximum number of gems allowed to be in use. */
    final int MAX_GEMS = 5;
    
    /** The width of the screen (at execution). */
    private int screenWidth;
    
    /** The height of the screen (at execution). */
    private int screenHeight;
    
    /** Whether the fireGem has been fired or not. */
    private static boolean fireAnimFired;
    
    /** Whether the lightningGem has been fired or not. */
    private static boolean lightningAnimFired;
    
    /** Whether the poisonGem has been fired or not. */
    private static boolean poisonAnimFired;
    
    /** Whether the shieldGem has been fired or not. */
    private static boolean shieldAnimFired;
    
    /** Whether the healGem has been fired or not. */
    private static boolean healAnimFired;
    
    /** The amount of moves the player has made. */
    private static int movesMade;
    
    /** The number of potions the player currently has. */
    private static int potCount;
    
    /** The R color of the background. */
    private static float rCol;
    
    /** The G color of the background. */
    private static float gCol;
    
    /** The B color of the background. */
    private static float bCol;
    
    /** The elapsed time since game execution. */
    private static float elapsedTime;
    
    /** A timer for the shield effect. */
    private static float shieldTimer;
    
    /** An array for each of the gem actors on the board. */
    private static GemActor[][] boardGemArray;
    
    /** The actor for the potion count UI element. */
    private static Actor potionCounter;
    
    /** The actor for the player's health bar. */
    private static Actor playerHealth;
    
    /** The actor for the enemy's health bar. */
    private static Actor enemyHealth;
    
    /** The stage on which the board and UI elements will appear. */
    private static Stage gameStage;
    
    /** The stage's viewport. */
    private StretchViewport sViewport;
    
    /** Used to animate the gem being moved by the player. */
    private static MoveToAction gemMoveAction;
    
    /** Used to animate the gem being swapped with the moved gem. */
    private static MoveToAction gemSwapAction;
    
    /** A fireball sound effect. */
    private static Sound fireGemSound;
    
    /** A "zap" sound effect. */
    private static Sound lightningGemSound;
    
    /** The poison sound effect. */
    private static Sound poisonGemSound;
    
    /** The shield sound effect. */
    private static Sound shieldGemSound;
    
    /** The heal sound effect. */
    private static Sound healGemSound;
    
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
        potCount = MAX_POTS;
        
        rCol = 1f;
        gCol = 1f;
        bCol = 1f;
        
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
                boardGemArray[row][col] = newGem();
                
                //Set position of each gem and center.
                boardGemArray[row][col].setPosition((row * (screenWidth / BOARD_ROWS))
                        + (screenWidth / BOARD_ROWS / 6), (col * (screenWidth / BOARD_ROWS)) 
                        + (screenWidth / BOARD_ROWS / 6) + BOTTOM_PADDING);
                
                gameStage.addActor(boardGemArray[row][col]);
            }
        }
        
        //Initialize all extra UI elements and draw them to the screen.
        potionCounter = new PotionCounter();
        playerHealth = new HealthBarActor();
        enemyHealth = new HealthBarActor();
        
        playerHealth.setPosition(0, 0);
        
        gameStage.addActor(potionCounter);
        gameStage.addActor(playerHealth);
        gameStage.addActor(enemyHealth);
        
        //Initialize all sound effects.
        lightningGemSound = Gdx.audio.newSound(Gdx.files.internal("zap_01.wav"));
        fireGemSound = Gdx.audio.newSound(Gdx.files.internal("fire_01.wav"));
        poisonGemSound = Gdx.audio.newSound(Gdx.files.internal("poison_01.wav"));
        shieldGemSound = Gdx.audio.newSound(Gdx.files.internal("shield_01.wav"));
        healGemSound = Gdx.audio.newSound(Gdx.files.internal("heal_01.wav"));
        
        //Initial check for matches at game execution.
        checkMatches();
    }
    
    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(rCol, gCol, bCol, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        
        elapsedTime += delta;
        
        if(Gdx.input.isKeyJustPressed(Keys.SPACE)) {
            if(potCount > 0) {
                movesMade = 0;
                potCount--;
                fillEmptySlots();
                checkMatches();
                ((PotionCounter) potionCounter).setNewSprite(potCount);
            }
        }
        
        if(Gdx.input.isKeyJustPressed(Keys.CONTROL_LEFT)) {
            movesMade = 0;
            fillEmptySlots();
        }
        
        gameStage.act(delta);
        gameStage.draw();
        
        fireAnimations();
    }
    
    private static void fireAnimations() {
        if(fireAnimFired) {
            if(gCol < 1 || bCol < 1) {
                if(elapsedTime > 0.01f) {
                    elapsedTime = 0;
                    gCol += EFFECT_FADE_RATE;
                    bCol += EFFECT_FADE_RATE;
                }
            } else {
                elapsedTime = 0;
                gCol = 1;
                bCol = 1;
                fireAnimFired = false;
            }
        }
        
        if(lightningAnimFired) {
            if(rCol < 1 || gCol < 1) {
                if(elapsedTime > 0.01f) {
                    elapsedTime = 0;
                    rCol += EFFECT_FADE_RATE;
                    gCol += EFFECT_FADE_RATE;
                }
            } else {
                elapsedTime = 0;
                rCol = 1;
                gCol = 1;
                lightningAnimFired = false;
            }
        }
        
        if(poisonAnimFired) {
            if(rCol < 1 || gCol < 1 || bCol < 1) {
                if(elapsedTime > 0.01f) {
                    elapsedTime = 0;
                    rCol += EFFECT_FADE_RATE;
                    gCol += EFFECT_FADE_RATE;
                    bCol += EFFECT_FADE_RATE;
                }
            } else {
                elapsedTime = 0;
                rCol = 1;
                gCol = 1;
                bCol = 1;
                poisonAnimFired = false;
            }
        }
        
        if(shieldAnimFired) {
            if(shieldTimer > SHIELD_DURATION) {
                if(bCol < 1) {
                    if(elapsedTime > 0.01f) {
                        elapsedTime = 0;
                        bCol += EFFECT_FADE_RATE;
                    }
                } else {
                    elapsedTime = 0;
                    bCol = 1;
                    shieldAnimFired = false;
                }
            } else
                shieldTimer += Gdx.graphics.getDeltaTime();
        }
        
        if(healAnimFired) {
            if(rCol < 1 || bCol < 1) {
                if(elapsedTime > 0.01f) {
                    elapsedTime = 0;
                    rCol += EFFECT_FADE_RATE;
                    bCol += EFFECT_FADE_RATE;
                }
            } else {
                elapsedTime = 0;
                rCol = 1;
                bCol = 1;
                healAnimFired = false;
            }
        }
    }
    
    private static void startAnimation(SpellType spellType) {
        rCol = 1;
        gCol = 1;
        bCol = 1;
        
        fireAnimFired = false;
        lightningAnimFired = false;
        poisonAnimFired = false;
        healAnimFired = false;
        
        switch(spellType) {
        case FIRE: 
            fireAnimFired = true;
            gCol = 0.7f;
            bCol = 0.7f;
            
            fireGemSound.play();
            break;
            
        case LIGHTNING: 
            lightningAnimFired = true;
            rCol = 0.5f;
            gCol = 0.8f;
            elapsedTime = 0;
            
            lightningGemSound.play();
            break;
            
        case POISON:
            poisonAnimFired = true;
            rCol = 0.8f;
            gCol = 0.7f;
            bCol = 0.8f;
            elapsedTime = 0;
            
            poisonGemSound.play();
            break;
            
        case SHIELD:
            shieldAnimFired = true;
            shieldTimer = 0;
            bCol = 0.7f;
            elapsedTime = 0;
            
            shieldGemSound.play();
            break;
            
        case HEAL:
            healAnimFired = true;
            rCol = 0.7f;
            bCol = 0.7f;
            elapsedTime = 0;
            
            healGemSound.play();
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
                    hideMatchHorizontal(col, row, matchLevel);
                }
            }
        }
    }
    
    private static void checkMatchesVertical() {
        int matchLevel;
        
        for(int col = 0; col < BOARD_ROWS; col++) {
            for(int row = 0; row < BOARD_ROWS; row++) {
                if(!boardGemArray[col][row].isInvisible()) {
                    matchLevel = 1;
                    
                    while(row < BOARD_ROWS - matchLevel
                            && boardGemArray[col][row + matchLevel].getType()
                            == boardGemArray[col][row].getType()
                            && !boardGemArray[col][row + matchLevel].isInvisible()) {
                        matchLevel++;
                    }
                    
                    if(matchLevel >= 3) {
                        hideMatchVertical(col, row, matchLevel);
                    }
                }
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
                break;
            case 1: startAnimation(SpellType.LIGHTNING);
                break;
            case 2: startAnimation(SpellType.POISON);
                break;
            case 3: startAnimation(SpellType.SHIELD);
                break;
            case 4: startAnimation(SpellType.HEAL);
                break;
        }
    }
    
    private static void hideMatchVertical(int col, int row, int matchLevel) {
        //For each gem in the match
        for(int i = 0; i < matchLevel; i++) {
            boardGemArray[col][row + i].setInvisible(true);
        }
        
        switch(boardGemArray[col][row].getType()) {
            case 0: startAnimation(SpellType.FIRE);
                break;
            case 1: startAnimation(SpellType.LIGHTNING);
                break;
            case 2: startAnimation(SpellType.POISON);
                break;
            case 3: startAnimation(SpellType.SHIELD);
                break;
            case 4: startAnimation(SpellType.HEAL);
                break;
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
