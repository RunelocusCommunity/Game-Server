package org.runetekk;

/**
 * Mob.java
 * @version 1.0.0
 * @author RuneTekk Development (SiniSoul)
 */
public class Mob extends Entity {
    
    /**
     * The maximum amount of steps in a walking queue.
     */
    public final static int MAXIMUM_STEPS = 25;
    
    /**
     * The walking queue in the form that the client reads
     * to move the player. The read position is positioned 
     * near the end of the queue along with the write 
     * position.
     */
    int[] walkingQueue;
    
    /**
     * Puts the walk opcode into the walking queue.
     * @param opcode The opcode.
     */
    void putWalkOpcode(int opcode) {
        walkingQueue[walkingQueue[walkingQueue.length - 1]++] = opcode;
    }
    
    /**
     * Constructs a new {@link Mob};
     */
    Mob() {
        walkingQueue = new int[MAXIMUM_STEPS + 2];
    }  
}
