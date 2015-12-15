package com.mygdx.game;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;

public class GemTouchListener extends InputListener {
    private float originX;
    private float originY;
    
    private Actor actor;
    
    public GemTouchListener(Actor actor) {
        this.actor = actor;
        
        originX = 0;
        originY = 0;
    }
    
    @Override
    public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
        System.out.println("touch down!"); //Debug
        
        originX = x;
        originY = y;
        return true;
    }
    
    @Override
    public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
        System.out.println("touch up!"); //Debug
        
        float offsetX;
        float offsetY;
        
        offsetX = x - originX;
        offsetY = y - originY;
    }
}
