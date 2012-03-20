package org.runetekk;

/**
 * Chunk.java
 * @version 1.0.0
 * @author RuneTekk Development (SiniSoul)
 */
public class Chunk {
    
    /**
     * The list of active players within this chunk.
     */
    ListNode activePlayers;
    
    /**
     * The list of active items within this chunk.
     */
    ListNode activeItems;
    
    /**
     * Constructs a new {@link Chunk};
     */
    Chunk() {
        activePlayers = new ListNode();
        activePlayers.parentNode = activePlayers;
        activePlayers.childNode = activePlayers;
        activeItems = new ListNode();
        activeItems.parentNode = activeItems;
        activeItems.childNode = activeItems;
    }
}
