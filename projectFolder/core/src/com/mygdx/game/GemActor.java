package com.mygdx.game;

import com.badlogic.gdx.scenes.scene2d.Actor;

public abstract class GemActor extends Actor{
    static int gemNum = -1;
    
    public static int setSignature() {
        gemNum++;
        return gemNum;
    }
    
    public abstract void setSignature(int newSignature);
}
