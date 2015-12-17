package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.scenes.scene2d.Actor;

public class PotionCounter extends Actor {
    private Sprite sprite = new Sprite(new Texture(Gdx.files.internal("pot_count_03.png")));
    
    public PotionCounter() {
        setBounds(sprite.getX(), sprite.getY(), sprite.getWidth(), sprite.getHeight());
        
        System.out.println(sprite.getWidth() + "  " + sprite.getHeight());
        sprite.setSize(Gdx.graphics.getWidth() / 6, Gdx.graphics.getWidth() / 6 / 2);
        setPosition(0, Gdx.graphics.getHeight() - sprite.getHeight());
        
    }
    
    public void setNewSprite(int potCount) {
        sprite.setTexture(new Texture(Gdx.files.internal("pot_count_0"
                + potCount + ".png")));
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
}
