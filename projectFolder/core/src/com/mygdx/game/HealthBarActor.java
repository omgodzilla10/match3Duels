package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.scenes.scene2d.Actor;

public class HealthBarActor extends Actor {
    private Sprite sprite = new Sprite(new Texture(Gdx.files.internal("healthBar_01.png")));
    float width;
    
    public HealthBarActor() {
        width = Gdx.graphics.getWidth();
        
        setBounds(sprite.getX(), sprite.getY(), sprite.getWidth(), sprite.getHeight());
        sprite.setSize(width, width / 10);
        setPosition(0, Gdx.graphics.getHeight() - sprite.getHeight());
        
    }
    
    @Override
    public void draw(Batch batch, float parentAlpha) {
        sprite.draw(batch);
    }
    
    @Override
    protected void positionChanged() {
        sprite.setPosition(getX(), getY());
        super.positionChanged();
    }
    
    @Override
    protected void sizeChanged() {
        sprite.setSize(getWidth(), getHeight());
        super.sizeChanged();
    }
}
