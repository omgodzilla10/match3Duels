package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Touchable;

public class HealGemActor_01 extends GemActor {
    static final int GEM_TYPE = 4;
    
    private int spriteXY;
    private int signature;
    private boolean matched;
    private Sprite gemSprite;
    
    private GemTouchListener listener;
    
    public HealGemActor_01() {
        spriteXY = (Gdx.graphics.getWidth() / Match3Duels_Game.BOARD_ROWS) 
                - Match3Duels_Game.SPRITE_PADDING;
        signature = super.setSignature();
        matched = false;
        gemSprite = new Sprite(new Texture(Gdx.files.internal("gem_heal_01.png")));
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
    
    @Override
    public int getType() {
        return GEM_TYPE;
    }
    
    @Override
    public boolean isMatched(){
        return matched;
    }
    
    @Override
    public void toggleMatched() {
        if(matched)
            matched = false;
        else if(!matched)
            matched = true;
    }
    
    @Override
    public void setMatched(boolean b){
        matched = b;
    }
}
