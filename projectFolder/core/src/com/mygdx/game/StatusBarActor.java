package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.scenes.scene2d.Actor;

public class StatusBarActor extends Actor{
    final float STATUS_DURATION = 0.5f;
    
    float timer;
    float tempTimer;
    float tempDuration;
    
    private Sprite sprite;
    
    private Texture disabledTex;
    private Texture fireTex;
    private Texture lightningTex;
    private Texture poisonTex;
    private Texture shieldTex;
    private Texture healTex;
    
    public StatusBarActor() {
        timer = 0;
        tempTimer = 0;
        tempDuration = 0;
        
        disabledTex = new Texture(Gdx.files.internal("statusBar_empty_01.png"));
        fireTex = new Texture(Gdx.files.internal("statusBar_fire_01.png"));
        lightningTex = new Texture(Gdx.files.internal("statusBar_lightning_01.png"));
        poisonTex = new Texture(Gdx.files.internal("statusBar_poison_01.png"));
        shieldTex = new Texture(Gdx.files.internal("statusBar_shield_01.png"));
        healTex = new Texture(Gdx.files.internal("statusBar_heal_01.png"));
        
        sprite = new Sprite(disabledTex);
        
        setBounds(sprite.getX(), sprite.getY(), sprite.getWidth(), sprite.getHeight());
    }
    
    public void setStatus(int gemType) {
        switch (gemType) {
        case 0: sprite.setTexture(fireTex); break;
        case 1: sprite.setTexture(lightningTex); break;
        case 2: sprite.setTexture(poisonTex); break;
        case 3: sprite.setTexture(shieldTex); break;
        case 4: sprite.setTexture(healTex); break;
        default: sprite.setTexture(disabledTex); break;
        }
    }
    
    public void disableStatus() {
        sprite.setTexture(disabledTex);
    }
    
    public void update(float delta) {
        if(sprite.getTexture() != disabledTex) {
            timer += delta;
            
            if(timer > STATUS_DURATION) {
                sprite.setTexture(disabledTex);
                timer = 0;
            }
        }
    }
    
    /**
     * Alternate update method for shield effects.
     * 
     * @param delta
     * @param shieldDuration
     */
    public void updateShield(float delta, float shieldDuration) {
        timer += delta;
        
        //If the shield is on but another effect is used (ie. heal).
        if(sprite.getTexture() != shieldTex) {
            tempTimer += delta;
            
            if(tempTimer > STATUS_DURATION) {
                tempTimer = 0;
                setStatus(ShieldGemActor_01.GEM_TYPE);
            }
        }
        
        if(timer > shieldDuration) {
            timer = 0;
            disableStatus();
        }
    }
    
    @Override
    protected void positionChanged() {
        sprite.setPosition(getX(), getY());
        super.positionChanged();
    }
    
    @Override
    protected void sizeChanged() {
        super.sizeChanged();
        sprite.setSize(getWidth(), getHeight());
    }
    
    @Override
    public void draw(Batch batch, float parentAlpha) {
        sprite.draw(batch);
    }
    
    @Override
    public void act(float delta) {
        super.act(delta);
    }
}
