package com.mygdx.game;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.actions.MoveToAction;

public class GemTouchListener extends InputListener {
    final float MAX_ANG_QUAD_01 = 45;
    final float MIN_ANG_QUAD_01 = 315;
    final float MAX_ANG_QUAD_02 = 135;
    final float MIN_ANG_QUAD_02 = 46;
    final float MAX_ANG_QUAD_03 = 225;
    final float MIN_ANG_QUAD_03 = 136;
    final float MAX_ANG_QUAD_04 = 314;
    final float MIN_ANG_QUAD_04 = 226;
    
    final int MIN_OFFSET = 20;
    static final int DIR_RIGHT = 0;
    static final int DIR_UP = 1;
    static final int DIR_LEFT = 2;
    static final int DIR_DOWN = 3;
    
    private int signature;
    
    private float originX;
    private float originY;
    
    private Actor actor;
    
    public GemTouchListener(Actor actor, int signature) {
        this.actor = actor;
        this.signature = signature;
        
        originX = 0;
        originY = 0;
    }
    
    @Override
    public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
        System.out.println("touch down!"); //Debug
        System.out.println(signature);
        
        originX = x;
        originY = y;
        return true;
    }
    
    @Override
    public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
        System.out.println("touch up!"); //Debug
        
        float offsetX;
        float offsetY;
        float dragAngle;
        
        //Find how much the gem was dragged by.
        offsetX = x - originX;
        offsetY = y - originY;
        
        if((offsetX > MIN_OFFSET || offsetY > MIN_OFFSET)
                || (offsetX < -MIN_OFFSET || offsetY < -MIN_OFFSET)) {
            //Find which direction the gem was dragged.
            dragAngle = (float) Math.atan2(offsetY, offsetX);
            dragAngle = (float) Math.toDegrees(dragAngle);
            if(dragAngle < 0)
                dragAngle += Math.toDegrees(2 * Math.PI);
            
            //Move right.
            if(dragAngle <= MAX_ANG_QUAD_01 || dragAngle >= MIN_ANG_QUAD_01) {
                System.out.println("Move right!"); //Debug
                Match3Duels_Game.moveGem(DIR_RIGHT, signature);
            }
            //Move up.
            else if(dragAngle <= MAX_ANG_QUAD_02 && dragAngle >= MIN_ANG_QUAD_02) {
                System.out.println("Move up!");
                Match3Duels_Game.moveGem(DIR_UP, signature);
            }
            //Move left.
            else if(dragAngle <= MAX_ANG_QUAD_03 && dragAngle >= MIN_ANG_QUAD_03) {
                System.out.println("Move left!");
                Match3Duels_Game.moveGem(DIR_LEFT, signature);
            }
            //Move down.
            else if(dragAngle <= MAX_ANG_QUAD_04 && dragAngle >= MIN_ANG_QUAD_04) {
                System.out.println("Move down!");
                Match3Duels_Game.moveGem(DIR_DOWN, signature);
            }
        }
    }
    
    public void setSignature(int signature) {
        this.signature = signature;
    }
}
