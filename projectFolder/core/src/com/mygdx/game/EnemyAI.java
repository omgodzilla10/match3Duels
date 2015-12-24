package com.mygdx.game;

import com.badlogic.gdx.math.MathUtils;

public class EnemyAI {
    /** The max number of gems available to the AI. */
    final int MAX_GEMS = 5;
    
    /** The crit-chance (chance of a match being 4+) for an easy AI. */
    final float EASY_CRIT = 0.25f;
    
    /** The crit-chance (chance of a match being 4+) for a medium AI. */
    final float MEDIUM_CRIT = 0.4f;
    
    /** The crit-chance (change of a match being 4+) for a hard AI. */
    final float HARD_CRIT = 0.6f;
    
    /** The chance of a crit being a 5-match. */
    final float DOUBLE_CRIT = 0.25f;
    
    /** The level of difficulty of the AI. Has three values:
     * 1: Easy
     * 2: Medium
     * 3: Hard
     */
    private int difficulty;
    
    /** The match level of a particular random spell cast. */
    private int matchLevel;
    
    /** The time between spell casts, depends on difficulty level. */
    private float castTime;
    
    /** A timer that keeps track of how much time has passed since the last
     * spell cast. */
    private float spellTimer;
    
    /** Max cast time for easy mode. */
    private float EASY_MAX = 2f;
    
    /** Min cast time for easy mode. */
    private float EASY_MIN = 1f;
    
    /** Max cast time for medium mode. */
    private float MEDIUM_MAX = 1.5f;
    
    /** Min cast time for medium mode. */
    private float MEDIUM_MIN = 1f;
    
    /** Max cast time for hard mode. */
    private float HARD_MAX = 1.5f;
    
    /** Min cast time for hard mode. */
    private float HARD_MIN = 0.5f;
    
    /** The gem that the AI is currently firing. */
    private GemActor currentGem;
    
    /** The gems available to the AI. */
    private GemActor fireGem;
    private GemActor lightningGem;
    private GemActor poisonGem;
    private GemActor shieldGem;
    private GemActor healGem;
    
    /** The actual game class. */
    Match3Duels_Game game;
    
    public EnemyAI(int diffLevel, Match3Duels_Game game) {
        difficulty = diffLevel;
        this.game = game;
        
        matchLevel = 0;
        castTime = 0;
        spellTimer = 0;
        
        fireGem = new FireGemActor_01();
        lightningGem = new LightningGemActor_01();
        poisonGem = new PoisonGemActor_01();
        shieldGem = new ShieldGemActor_01();
        healGem = new HealGemActor_01();
    }
    
    public void simulate(float delta) {
        if(castTime == 0) {
            randomizeSpell(difficulty);
        }
        
        spellTimer += delta;
        
        if(spellTimer > castTime) {
            castTime = 0;
            spellTimer = 0;
            game.fireSpell(currentGem, matchLevel, false);
        }
    }
    
    private void randomizeSpell(int difficulty) {
        switch(difficulty) {
        case 1: 
            castTime = MathUtils.random(EASY_MIN, EASY_MAX);
            randomizeGem(difficulty);
            break;
        case 2:
            castTime = MathUtils.random(MEDIUM_MIN, MEDIUM_MAX);
            randomizeGem(difficulty);
            break;
        case 3:
            castTime = MathUtils.random(HARD_MIN, HARD_MAX);
            randomizeGem(difficulty);
            break;
        }
    }
    
    private void randomizeGem(int difficulty) {
        int gemNumber = MathUtils.random(1, MAX_GEMS);
        
        /* A random number between 0 and 1, used to determine
        the match-level for a spell cast. */
        float matchLevelFactor = (float) Math.random();
        
        //Randomize which gem is cast.
        switch(gemNumber) {
        case 1:
            currentGem = fireGem;
            break;
        case 2:
            currentGem = lightningGem;
            break;
        case 3:
            currentGem = poisonGem;
            break;
        case 4: 
            currentGem = shieldGem;
            break;
        case 5: 
            currentGem = healGem;
            break;
        }
        
        //Randomize the matchLevel.
        switch(difficulty) {
        case 1: 
            if(matchLevelFactor < EASY_CRIT) {
                if(matchLevelFactor < DOUBLE_CRIT * EASY_CRIT) {
                    matchLevel = 5;
                } else matchLevel = 4;
            } else matchLevel = 3;
            break;
        case 2:
            if(matchLevelFactor < MEDIUM_CRIT) {
                if(matchLevelFactor < DOUBLE_CRIT * MEDIUM_CRIT) {
                    matchLevel = 5;
                } else matchLevel = 4;
            } else matchLevel = 3;
            break;
        case 3:
            if(matchLevelFactor < HARD_CRIT) {
                if(matchLevelFactor < DOUBLE_CRIT * HARD_CRIT) {
                    matchLevel = 5;
                } else matchLevel = 4;
            } else matchLevel = 3;
            break;
        }
    }
}
