package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Touchable;

public class LightningGemActor_01 extends GemActor {
    private int spriteXY;
    private int signature;
    private Sprite gemSprite;
    
    private GemTouchListener listener;
    
    public LightningGemActor_01() {
        spriteXY = (Gdx.graphics.getWidth() / Match3Duels_Game.BOARD_ROWS) 
                - Match3Duels_Game.SPRITE_PADDING;
        signature = super.setSignature();
        gemSprite = new Sprite(new Texture(Gdx.files.internal("gem_lightning_01.png")));
        gemSprite.setSize(spriteXY, spriteXY);
        
        listener = new GemTouchListener(this, signature);
        
        setBounds(gemSprite.getX(), gemSprite.getY(), gemSprite.getWidth(), 
                gemSprite.getHeight());
        
        setTouchable(Touchable.enabled);
        addListener(listener);
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

    @Override
    public void setSignature(int newSignature) {
        signature = newSignature;
        listener.setSignature(signature);
    }
    
    @Override
    public int getSignature() {
        return signature;
    }
}
