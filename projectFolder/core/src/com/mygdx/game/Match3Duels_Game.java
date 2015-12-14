package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;

public class Match3Duels_Game implements Screen {
    final Match3Duels_Main game;
    
    /** The number of rows on the board. */
    final int BOARD_ROWS = 6;
    
    /** The maximum number of gems allowed to be in use. */
    final int MAX_GEMS = 5;
    
    private int screenWidth;
    private int screenHeight;
    
    private OrthographicCamera camera;
    
    private Texture gemIcon_fire_01;
    private Texture gemIcon_lightning_01;
    private Texture gemIcon_poison_01;
    private Texture gemIcon_shield_01;
    private Texture gemIcon_heal_01;
    
    private Texture[] iconArray;
    private Texture[][] boardIconArray;
    
    private Rectangle gem_fire_01;
    private Rectangle gem_lightning_01;
    private Rectangle gem_poison_01;
    private Rectangle gem_shield_01;
    private Rectangle gem_heal_01;
    
    private Rectangle[] gemArray;
    private Rectangle[][] boardGemArray;
    
    
    public Match3Duels_Game(final Match3Duels_Main game) {
        this.game = game;
        
        screenWidth = Gdx.graphics.getWidth();
        screenHeight = Gdx.graphics.getHeight();
        
        camera = new OrthographicCamera();
        camera.setToOrtho(false, screenWidth, screenHeight);
        
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
        
        //Create each gem rectangle object.
        gem_fire_01 = new Rectangle();
        gem_fire_01.width = screenWidth / (BOARD_ROWS + 3);
        gem_fire_01.height = gem_fire_01.width;
        
        gem_lightning_01 = new Rectangle();
        gem_lightning_01.width = screenWidth / (BOARD_ROWS + 3);
        gem_lightning_01.height = gem_lightning_01.width;
        
        gem_poison_01 = new Rectangle();
        gem_poison_01.width = screenWidth / (BOARD_ROWS + 3);
        gem_poison_01.height = gem_poison_01.width;
        
        gem_shield_01 = new Rectangle();
        gem_shield_01.width = screenWidth / (BOARD_ROWS + 3);
        gem_shield_01.height = gem_shield_01.width;
        
        gem_heal_01 = new Rectangle();
        gem_heal_01.width = screenWidth / (BOARD_ROWS + 3);
        gem_heal_01.height = gem_heal_01.width;
        
        //Assign each gem to the gemArray
        gemArray = new Rectangle[MAX_GEMS];
        gemArray[0] = gem_fire_01;
        gemArray[1] = gem_lightning_01;
        gemArray[2] = gem_poison_01;
        gemArray[3] = gem_shield_01;
        gemArray[4] = gem_heal_01;
        
        //Initialize the board arrays with random gems
        boardGemArray = new Rectangle[BOARD_ROWS][BOARD_ROWS];
        boardIconArray = new Texture[BOARD_ROWS][BOARD_ROWS];
        
        for(int row = 0; row < BOARD_ROWS; row++) {
            for(int col = 0; col < BOARD_ROWS; col++) {
                int idx = MathUtils.random(0, 4);
                
                boardIconArray[row][col] = iconArray[idx];
                boardGemArray[row][col] = new Rectangle(gemArray[idx]);
                
                boardGemArray[row][col].x = (row * (screenWidth / BOARD_ROWS)) + ((screenWidth / BOARD_ROWS) / 4);
                boardGemArray[row][col].y = (col * (screenWidth / BOARD_ROWS)) + ((screenWidth / BOARD_ROWS) / 4);
            }
        }
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(153f / 255f, 204f / 255f, 255f / 255f, 1.0f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        
        camera.update();
        game.batch.setProjectionMatrix(camera.combined);
        
        game.batch.begin();
        
        //Render each gem in the board
        for(int row = 0; row < BOARD_ROWS; row++) {
            for(int col = 0; col < BOARD_ROWS; col++) {
                game.batch.draw(boardIconArray[row][col], boardGemArray[row][col].x, 
                        boardGemArray[row][col].y, boardGemArray[row][col].width, 
                        boardGemArray[row][col].height);
            }
        }
        
        game.batch.end();
    }

    @Override
    public void resize(int width, int height) { 
        camera.viewportHeight = height;
        camera.viewportWidth = width;
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
