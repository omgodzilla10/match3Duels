package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.viewport.StretchViewport;

public class Match3Duels_Game implements Screen {
    final Match3Duels_Main game;
    
    /** The number of rows on the board. */
    static final int BOARD_ROWS = 6;
    
    /** The padding (in pixels) between gem sprites. */
    static final int SPRITE_PADDING = 50;
    
    /** The maximum number of gems allowed to be in use. */
    final int MAX_GEMS = 5;
    
    private int screenWidth;
    private int screenHeight;
    
    /*
    private Texture gemIcon_fire_01;
    private Texture gemIcon_lightning_01;
    private Texture gemIcon_poison_01;
    private Texture gemIcon_shield_01;
    private Texture gemIcon_heal_01;
    
    private Texture[] iconArray;
    private Texture[][] boardIconArray;
    
    private Actor gem_fire_01;
    private Actor gem_lightning_01;
    private Actor gem_poison_01;
    private Actor gem_shield_01;
    private Actor gem_heal_01;*/
    
    private Actor[] gemArray;
    private Actor[][] boardGemArray;
    
    private Stage gameStage;
    
    private StretchViewport sViewport;
    
    public Match3Duels_Game(final Match3Duels_Main game) {
        this.game = game;
        
        screenWidth = Gdx.graphics.getWidth();
        screenHeight = Gdx.graphics.getHeight();
        
        sViewport = new StretchViewport(screenWidth, screenHeight);
        gameStage = new Stage(sViewport);
        
        /*
        //Create each gemIcon texture object.
        gemIcon_fire_01 = new Texture(Gdx.files.internal("gem_fire_01.png"));
        gemIcon_lightning_01 = new Texture(Gdx.files.internal("gem_lightning_01.png"));
        gemIcon_poison_01 = new Texture(Gdx.files.internal("gem_poison_01.png"));
        gemIcon_shield_01 = new Texture(Gdx.files.internal("gem_shield_01.png"));
        gemIcon_heal_01 = new Texture(Gdx.files.internal("gem_heal_01.png"));
        
        //Assign each gem icon to the gemIcon array.
        iconArray = new Texture[MAX_GEMS];
        iconArray[0] = gemIcon_fire_01;
        iconArray[1] = gemIcon_lightning_01;
        iconArray[2] = gemIcon_poison_01;
        iconArray[3] = gemIcon_shield_01;
        iconArray[4] = gemIcon_heal_01;
        
        //Create each gem actor.
        gem_fire_01 = new FireGemActor_01();
        gem_lightning_01 = new Actor();
        gem_poison_01 = new Actor();
        gem_shield_01 = new Actor();
        gem_heal_01 = new Actor();
        
        //Assign each gem to the gemArray
        gemArray = new Actor[MAX_GEMS];
        gemArray[0] = gem_fire_01;
        gemArray[1] = gem_lightning_01;
        gemArray[2] = gem_poison_01;
        gemArray[3] = gem_shield_01;
        gemArray[4] = gem_heal_01;*/
        
        //Initialize the board arrays with random gems
        boardGemArray = new Actor[BOARD_ROWS][BOARD_ROWS];
        
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
                        + (screenWidth / BOARD_ROWS / 4), (col * (screenWidth / BOARD_ROWS)) 
                        + (screenWidth / BOARD_ROWS / 4));
                
                /*boardGemArray[row][col].setPosition((row * (screenWidth / BOARD_ROWS)), 
                        (col * (screenWidth / BOARD_ROWS)));*/
                
                gameStage.addActor(boardGemArray[row][col]);
            }
        }
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(1, 1, 1, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        
        /*
        game.batch.begin();
        
        //Render each gem in the board
        for(int row = 0; row < BOARD_ROWS; row++) {
            for(int col = 0; col < BOARD_ROWS; col++) {
                game.batch.draw(boardIconArray[row][col], boardGemArray[row][col].x, 
                        boardGemArray[row][col].y, boardGemArray[row][col].width, 
                        boardGemArray[row][col].height);
            }
        }
        
        game.batch.end();*/
        
        gameStage.act(delta);
        gameStage.draw();
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
