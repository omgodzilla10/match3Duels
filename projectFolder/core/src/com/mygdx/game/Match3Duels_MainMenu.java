package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;

public class Match3Duels_MainMenu implements Screen {
    final Match3Duels_Main game;
    
    OrthographicCamera camera;
    
    public Match3Duels_MainMenu(final Match3Duels_Main game) {
        this.game = game;
        
        camera = new OrthographicCamera();
        camera.setToOrtho(false, Gdx.graphics.getWidth(), 
                Gdx.graphics.getHeight());
    }

    @Override
    public void show() {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0.2f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        
        camera.update();
        game.batch.setProjectionMatrix(camera.combined);
        
        game.batch.begin();
        game.font.draw(game.batch, "Welcome to Match3Duels!", 100, 150);
        game.font.draw(game.batch, "Tap to begin", 100, 100);
        game.batch.end();
        
        if (Gdx.input.isTouched()) {
            game.setScreen(new Match3Duels_Game(game));
            dispose();
        }
    }

    @Override
    public void resize(int width, int height) {}
    @Override
    public void pause() {}
    @Override
    public void resume() {}
    @Override
    public void hide() {}
    @Override
    public void dispose() {}
}
