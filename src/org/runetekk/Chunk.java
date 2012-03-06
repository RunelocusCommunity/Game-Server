package org.runetekk;

/**
 * Chunk.java
 * @version 1.0.0
 * @author RuneTekk Development (SiniSoul)
 */
public final class Chunk {
    
    /**
     * The list of active entities within this chunk.
     */
    ListNode activeEntities;
    
    /**
     * Constructs a new {@link Chunk};
     */
    Chunk() {
        activeEntities = new ListNode();
    }
}
