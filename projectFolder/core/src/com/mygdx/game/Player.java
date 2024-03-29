package com.mygdx.game;

import com.badlogic.gdx.scenes.scene2d.Stage;

public class Player {
    final static float MAX_HEALTH = 100;
    
    float health;
    boolean isPoisoned;
    boolean isShielded;
    
    HealthBarActor healthBar;
    StatusBarActor statusBar;
    
    public Player(Stage gameStage) {
        health = MAX_HEALTH;
        isPoisoned = false;
        isShielded = false;
        
        healthBar = new HealthBarActor();
        statusBar = new StatusBarActor();
        
        gameStage.addActor(healthBar);
        gameStage.addActor(statusBar);
    }
    
    /**
     * Returns health
     * 
     * @return health
     */
    public float getHealth() {
        return health;
    }
    
    /**
     * Used to change the player's health. -20 will reduce the health by 20, 
     * +50 will increase health by 50, etc.
     * 
     * @param health
     */
    public void changeHealth(float healthChange) {
        health += healthChange;
    }
    
    public void setPoisoned(boolean b) {
        isPoisoned = b;
    }
    
    public boolean isPoisoned() {
        return isPoisoned;
    }
    
    public void setShielded(boolean b) {
        isShielded = b;
    }
    
    public boolean isShielded() {
        return isShielded;
    }
}
