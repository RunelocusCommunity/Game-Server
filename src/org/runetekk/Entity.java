package org.runetekk;

/**
 * Entity.java
 * @version 1.0.0
 * @author RuneTekk Development (SiniSoul)
 */
public class Entity extends ListNode {
    
    /**
     * The x coordinate of where this entity is located.
     */
    int coordX;
    
    /**
     * The y coordinate of where this entity is located.
     */
    int coordY;
    
    /**
     * The z coordinate of where this entity is located.
     */
    int coordZ;
    
    /**
     * Constructs a new {@link Entity};
     */
    Entity() {
        coordX = coordY = 3200;
    }
}
