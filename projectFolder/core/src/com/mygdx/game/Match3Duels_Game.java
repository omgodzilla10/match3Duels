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
    
    /** The AI's difficulty level. */
    static final int AI_DIFF = 1;
    
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
    final int BOTTOM_PADDING = Gdx.graphics.getHeight() / 14;
    
    /** The speed that the fire spell animation moves along the y axis. */
    static final int FIRE_SPELL_SPEED = 400;
    
    /** The default max health for the player and enemy. */
    static final int MAX_HEALTH = 100;
    
    /** The number of spell casts per firing of a recurring gem. */
    static final int SPELLS_PER_RECURRING = 2;
    
    /** The time, in seconds, between frames in an animation. */
    static final float ANIM_DURATION = 0.1f;
    
    /** The global animation speed of each gem when moved. */
    final static float GEM_MOVE_DURATION = 0.15f;
    
    /** The rate at which a spell effect fades out. Higher = faster. */
    final static float EFFECT_FADE_RATE = 0.005f;
    
    /** The default duration of a persisting effect. */
    final static float RECURRING_DURATION = 2f;
    
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
    
    /** Whether a persisting effect is on or not. */
    private static boolean recurringEffect;
    
    /** Whether a shield is deployed or not. */
    private static boolean shieldEffect;
    
    /** Whether the player has a poisoned status or not. */
    private static boolean playerPoisoned;
    
    /** Whether the enemy has a poisoned status or not. */
    private static boolean enemyPoisoned;
    
    /** Whether or not a match has been found after the board was cleared. */
    private static boolean firstMatch;
    
    /** The amount of moves the player has made. */
    private static int movesMade;
    
    /** The number of potions the player currently has. */
    private static int potCount;
    
    /** The player's health. */
    private static int playerHealth;
    
    /** The enemy's health. */
    private static int enemyHealth;
    
    /** Used to track how many times a recurring spell has been fired in its lifetime. */
    private static int recurringFired;
    
    /** The length of the poison effect, measured in number of attacks. */
    private static int poisonDuration;
    
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
    
    /** The length of the shield effect. */
    private static float shieldDuration;
    
    /** A timer for the poison effect. */
    private static float poisonTimer;
    
    /** The damage done by each poison "flash". */
    private static float poisonDamage;
    
    /** An array for each of the gem actors on the board. */
    private static GemActor[][] boardGemArray;
    
    /** The actor for the potion count UI element. */
    private static Actor potionCounter;
    
    /** The player character class. */
    private static Player playerChar;
    
    /** The enemy character class. */
    private static Player enemyChar;
    
    /** The stage on which the board and UI elements will appear. */
    private static Stage gameStage;
    
    /** The stage's viewport. */
    private StretchViewport sViewport;
    
    /** Used to animate the gem being moved by the player. */
    private static MoveToAction gemMoveAction;
    
    /** Used to animate the gem being swapped with the moved gem. */
    private static MoveToAction gemSwapAction;
    
    /** The enemy AI. */
    EnemyAI enemyAI;
    
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
        shieldTimer = 0;
        poisonTimer = 0;
        poisonDuration = 0;
        recurringFired = 0;
        potCount = MAX_POTS;
        
        recurringEffect = false;
        shieldEffect = false;
        playerPoisoned = false;
        enemyPoisoned = false;
        
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
        
        int tempType;
        int sig = 0;
        
        for(int row = 0; row < BOARD_ROWS; row++) {
            for(int col = 0; col < BOARD_ROWS; col++) {
                boardGemArray[row][col] = newGem();
                
                if(row > 1 && col > 1) {
                    tempType = boardGemArray[row][col].getType();
                    
                    //Randomize the gem until it's not matching the gems adjacent to it.
                    while((boardGemArray[row][col - 1].getType() == tempType 
                            && boardGemArray[row][col - 2].getType() == tempType) 
                            || (boardGemArray[row - 1][col].getType() == tempType
                            && boardGemArray[row - 2][col].getType() == tempType)
                            && boardGemArray[row][col - 1].getType() == boardGemArray[row][col - 2].getType()
                            && boardGemArray[row - 1][col].getType() == boardGemArray[row - 2][col].getType()) {
                        
                        //Create a new random gem.
                        boardGemArray[row][col] = newGem();
                        tempType = boardGemArray[row][col].getType();
                    }
                }
                
                //Set position of each gem and center.
                boardGemArray[row][col].setPosition((row * (screenWidth / BOARD_ROWS))
                        + (screenWidth / BOARD_ROWS / 6), (col * (screenWidth / BOARD_ROWS)) 
                        + (screenWidth / BOARD_ROWS / 6) + BOTTOM_PADDING);
                
                boardGemArray[row][col].setSignature(sig);
                sig++;
                
                gameStage.addActor(boardGemArray[row][col]);
            }
        }
        
        playerChar = new Player(gameStage);
        enemyChar = new Player(gameStage);
        
        playerChar.healthBar.setPosition(0, 0);
        /*
        playerChar.statusBar = new StatusBarActor();
        enemyChar.statusBar = new StatusBarActor();*/
        
        playerChar.statusBar.setPosition(SPRITE_PADDING, boardGemArray[0][BOARD_ROWS - 1].getY()
                + boardGemArray[0][BOARD_ROWS - 1].getHeight() + SPRITE_PADDING);
        enemyChar.statusBar.setPosition(SPRITE_PADDING, enemyChar.healthBar.getY()
                - enemyChar.healthBar.getHeight() - SPRITE_PADDING);
        
        playerChar.statusBar.setSize(screenWidth - (2 * SPRITE_PADDING), playerChar.statusBar.getHeight());
        enemyChar.statusBar.setSize(screenWidth - (2 * SPRITE_PADDING), enemyChar.statusBar.getHeight());
        
        //Initial check for matches at game execution.
        do {
            firstMatch = false;
            fillEmptySlots();
            checkMatches(true);
        } while (firstMatch);
        
        enemyAI = new EnemyAI(AI_DIFF, this);
    }
    
    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(rCol, gCol, bCol, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        
        elapsedTime += delta;
        
        if(Gdx.input.isKeyJustPressed(Keys.SPACE)) {
            usePotion();
        }
        
        //For debug.
        if(Gdx.input.isKeyJustPressed(Keys.CONTROL_LEFT)) {
            movesMade = 0;
            fillEmptySlots();
            checkMatches(false);
        }
        
        if(recurringEffect) {
            if(playerPoisoned)
                recurringEffect(delta, false);
            else if (enemyPoisoned)
                recurringEffect(delta, true);
        }
        
        gameStage.act(delta);
        gameStage.draw();
        
        fireAnimations();
        
        if(shieldEffect)
            ((StatusBarActor)playerChar.statusBar).updateShield(delta, shieldDuration);
        else ((StatusBarActor)playerChar.statusBar).update(delta);
        
        //Temp
        ((StatusBarActor)enemyChar.statusBar).update(delta);
        
        //Simulate the enemy ai.
        enemyAI.simulate(delta);
    }
    
    private static void usePotion() {
        if(potCount > 0) {
            //Reset moves made and lower potion count.
            movesMade = 0;
            
            //Cancel recurring and persisting effects.
            recurringEffect = false;
            //persistingEffect = false;
            
            enemyPoisoned = false;
            poisonDuration = 0;
            poisonDamage = 0;
            poisonTimer = 0;
            recurringFired = 0;
            fillEmptySlots();
            checkMatches(false);
            
            //((PotionCounter) potionCounter).setNewSprite(potCount);
        }
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
            if(shieldTimer > shieldDuration) {
                shieldEffect = false;
                
                if(bCol < 1) {
                    if(elapsedTime > 0.01f) {
                        elapsedTime = 0;
                        bCol += EFFECT_FADE_RATE;
                    }
                } else {
                    elapsedTime = 0;
                    System.out.println("Shield duration: " + shieldDuration);
                    shieldDuration = 0;
                    shieldTimer = 0;
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
    
    /*
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
            gCol = 0.6f;
            bCol = 0.6f;
            
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
            bCol = 0.6f;
            elapsedTime = 0;
            
            shieldGemSound.play();
            break;
            
        case HEAL:
            healAnimFired = true;
            rCol = 0.6f;
            bCol = 0.6f;
            elapsedTime = 0;
            
            healGemSound.play();
            break;
        }
    }*/
    
    
    protected static void moveGem(int dir, int signature) {
        if(movesMade < MAX_MOVES) {
            switch (dir) {
            case GemTouchListener.DIR_RIGHT: 
                moveGemRight(signature);
                checkMatches(false);
                movesMade++;
                break;
                
            case GemTouchListener.DIR_UP:
                moveGemUp(signature);
                checkMatches(false);
                movesMade++;
                break;
                
            case GemTouchListener.DIR_LEFT:
                moveGemLeft(signature);
                checkMatches(false);
                movesMade++;
                break;
                
            case GemTouchListener.DIR_DOWN:
                moveGemDown(signature);
                checkMatches(false);
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
        } else movesMade--;
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
        } else movesMade--;
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
        } else movesMade--;
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
        } else movesMade--;
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
    
    /**
     * Checks for vertical and horizontal matches across the match-3 board.
     * 
     * @param newBoard - If the board has been reset (prevents initial matches.)
     */
    private static void checkMatches(boolean newBoard) {
        checkMatchesHorizontal(newBoard);
        checkMatchesVertical(newBoard);
    }
    
    private static void checkMatchesHorizontal(boolean newBoard) {
        int matchLevel;
        
        for(int col = 0; col < BOARD_ROWS; col++) {
            
            for(int row = 0; row < BOARD_ROWS; row++) {
                if(!boardGemArray[col][row].isInvisible()) {
                    matchLevel = 1;
                    
                    while(col < BOARD_ROWS - matchLevel
                            && boardGemArray[col + matchLevel][row].getType()
                            == boardGemArray[col][row].getType()
                            && !boardGemArray[col + matchLevel][row].isInvisible()) {
                        matchLevel++;
                    }
                    
                    if(matchLevel >= 3) {
                        hideMatchHorizontal(col, row, matchLevel, newBoard);
                    }
                }
            }
        }
    }
    
    private static void checkMatchesVertical(boolean newBoard) {
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
                        hideMatchVertical(col, row, matchLevel, newBoard);
                    }
                }
            }
        }
    }
    
    private static void hideMatchHorizontal(int col, int row, int matchLevel, boolean newBoard) {
        //For each gem in the match
        for(int i = 0; i < matchLevel; i++) {
            boardGemArray[col + i][row].setInvisible(true);
        }
        
        if(!newBoard)
            fireSpell(boardGemArray[col][row], matchLevel, true);
        else firstMatch = true;
    }
    
    private static void hideMatchVertical(int col, int row, int matchLevel, boolean newBoard) {
        //For each gem in the match
        for(int i = 0; i < matchLevel; i++) {
            boardGemArray[col][row + i].setInvisible(true);
        }
        
        if(!newBoard)
            fireSpell(boardGemArray[col][row], matchLevel, true);
        else firstMatch = true;
    }
    
    /**
     * Called whenever a spell is fired from the board.
     * 
     * @param gem - The gem that was matched.
     * @param matchLevel - The number of gems in the match.
     * @param player - Whether the spell was cast by the player or not.
     */
    public static void fireSpell(GemActor gem, int matchLevel, boolean player) {
        int type = gem.getType();
        
        switch (type) {
        case 0: modifyHealth(gem.fireSpell(matchLevel), player);
            break;
        case 1: modifyHealth(gem.fireSpell(matchLevel), player);
            break;
        case 2: recurringSpell(gem.fireSpell(matchLevel), player);
            break;
        case 3: persistingSpell(gem.firePersistSpell(matchLevel), player);
            break;
        case 4: modifyHealth(gem.fireSpell(matchLevel), player);
            break;
        }
        
        if(gem.getType() != 2) {
            gem.playSound();
            
            if(player) {
                if(gem.getType() == 3 || gem.getType() == 4) {
                    ((StatusBarActor)playerChar.statusBar).setStatus(gem.getType());
                } else {
                    ((StatusBarActor)enemyChar.statusBar).setStatus(gem.getType());
                }
            } else {
                if(gem.getType() == 3 || gem.getType() == 4) {
                    ((StatusBarActor)enemyChar.statusBar).setStatus(gem.getType());
                } else {
                    ((StatusBarActor)playerChar.statusBar).setStatus(gem.getType());
                }
            }
        }
    }
    
    /**
     * Called whenever a spell deals damage or heals.
     * 
     * @param health - The health that is removed or added to a healthbar.
     * @param player - Whether the attack was cast by the player or not.
     */
    private static void modifyHealth(int health, boolean player) {
        if(player) {
            if(health < 0) {
                health *= -1;
                enemyHealth -= health;
                enemyChar.healthBar.setX(enemyChar.healthBar.getX() 
                        - (enemyChar.healthBar.getWidth() * health / 100));
            } else {
                playerHealth += health;
                if(playerHealth > MAX_HEALTH) {
                    playerHealth = MAX_HEALTH;
                } else {
                    playerChar.healthBar.setX(playerChar.healthBar.getX() 
                            + (playerChar.healthBar.getWidth() * health / 100));
                }
            }
        } else {
            if(health < 0) {
                health *= -1;
                playerHealth -= health;
                playerChar.healthBar.setX(playerChar.healthBar.getX() 
                        - (playerChar.healthBar.getWidth() * health / 100));
            } else {
                enemyHealth += health;
                if(enemyHealth > MAX_HEALTH) {
                    enemyHealth = MAX_HEALTH;
                } else {
                    enemyChar.healthBar.setX(enemyChar.healthBar.getX() 
                            + (enemyChar.healthBar.getWidth() * health / 100));
                }
            }
        }
    }
    
    private static void persistingSpell(float effect, boolean player) {
        shieldDuration += effect;
        shieldAnimFired = true;
        shieldEffect = true;
    }
    
    /** Called once when a recurring spell is cast. */
    private static void recurringSpell(int health, boolean player) {
        poisonDamage += health;
        poisonDuration += SPELLS_PER_RECURRING;
        recurringEffect = true;
        
        if(player)
            enemyPoisoned = true;
        else playerPoisoned = true;
    }
    
    /** Called each frame while a recurring effect lasts.
     * 
     * @param delta - The time between frames
     * @param player - Whether or not the spell was cast by a player.
     */
    private static void recurringEffect(float delta, boolean player) {
        poisonTimer += delta;
        
        //Cast poison
        if(poisonTimer >= RECURRING_DURATION / SPELLS_PER_RECURRING) {
            new PoisonGemActor_01().playSound();
            modifyHealth((int)poisonDamage, player);
            
            poisonTimer = 0;
            recurringFired++;
            
            if(player)
                ((StatusBarActor)enemyChar.statusBar).setStatus(PoisonGemActor_01.GEM_TYPE);
            else ((StatusBarActor)playerChar.statusBar).setStatus(PoisonGemActor_01.GEM_TYPE);
        }
        
        //End of spell.
        if(recurringFired >= poisonDuration) {
            poisonDuration = 0;
            poisonDamage = 0;
            recurringFired = 0;
            recurringEffect = false;
            System.out.println("End poison!");
            
            if(player)
                enemyPoisoned = false;
            else playerPoisoned = false;
        }
    }
    
    private static void fillEmptySlots() {
        int xPos;
        int yPos;
        int sig;
        int tempType;
        
        for(int col = 0; col < BOARD_ROWS; col++) {
            for(int row = 0; row < BOARD_ROWS; row++) {
                if(boardGemArray[col][row].isInvisible()) {
                    xPos = (int) boardGemArray[col][row].getX();
                    yPos = (int) boardGemArray[col][row].getY();
                    sig = boardGemArray[col][row].getSignature();
                    
                    boardGemArray[col][row].remove();
                    boardGemArray[col][row] = newGem();
                    /*
                    if(row > 1 && col > 1) {
                        tempType = boardGemArray[row][col].getType();
                        
                        //Randomize the gem until it's not matching the gems adjacent to it.
                        while((boardGemArray[row][col - 1].getType() == tempType 
                                && boardGemArray[row][col - 2].getType() == tempType) 
                                || (boardGemArray[row - 1][col].getType() == tempType
                                && boardGemArray[row - 2][col].getType() == tempType)
                                && boardGemArray[row][col - 1].getType() == boardGemArray[row][col - 2].getType()
                                && boardGemArray[row - 1][col].getType() == boardGemArray[row - 2][col].getType()) {
                            
                            //Create a new random gem.
                            boardGemArray[row][col] = newGem();
                            tempType = boardGemArray[row][col].getType();
                        }
                    }*/
                    
                    
                    boardGemArray[col][row].setPosition(xPos, yPos);
                    boardGemArray[col][row].setSignature(sig);
                    
                    gameStage.addActor(boardGemArray[col][row]);
                }
            }
        }
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
        
        checkMatches(true);
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
