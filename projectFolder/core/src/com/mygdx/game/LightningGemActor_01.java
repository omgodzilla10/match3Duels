package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Touchable;

public class LightningGemActor_01 extends Actor {
    private int spriteXY;
    private Sprite gemSprite;
    
    public LightningGemActor_01() {
        spriteXY = (Gdx.graphics.getWidth() / Match3Duels_Game.BOARD_ROWS) 
                - Match3Duels_Game.SPRITE_PADDING;
        gemSprite = new Sprite(new Texture(Gdx.files.internal("gem_lightning_01.png")));
        
        setTouchable(Touchable.enabled);
        setBounds(gemSprite.getX(), gemSprite.getY(), gemSprite.getWidth(), 
                gemSprite.getHeight());
        gemSprite.setSize(spriteXY, spriteXY);
    }
    
    @Override
    public void draw(Batch batch, float parentAlpha) {
        gemSprite.draw(batch);
    }
    
    @Override
    public void act(float delta) {
        super.act(delta);
    }
    
    @Override
    protected void positionChanged() {
        gemSprite.setPosition(getX(), getY());
        super.positionChanged();
    }
}
