package org.runetekk;

/**
 * Player.java
 * @version 1.0.0
 * @author RuneTekk Development (SiniSoul)
 */
public final class Player extends Mob {
    
    /**
     * The {@link Client} id that this player represents.
     */
    int id;
    
    /**
     * Constructs a new {@link Player};
     * @param id The id of the player.
     */
    Player(int id) {
        this.id = id;
    }  
}
