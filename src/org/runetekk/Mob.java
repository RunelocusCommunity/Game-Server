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
     * The queue of the last updates that this mob was affected by.
     */
    int[] lastUpdates;
    
    /**
     * The local x coordinate where everything is centered from.
     */
    int updatedLocalX;
    
    /**
     * The local y coordinate where everything is centered from.
     */
    int updatedLocalY;
    
    /**
     * Updates the movement of this mob.
     */
    void updateMovement() {
        boolean runToggled = false;
        if(this instanceof Client)
            runToggled = ((Client) this).isRunActive;
        if(updatedLocalX < 0 && updatedLocalY < 0) {
            walkingQueue[MAXIMUM_STEPS - 2] = 1;
            walkingQueue[MAXIMUM_STEPS - 1] = 0;
            walkingQueue[0] = 8;
        }
        int readPosition = walkingQueue[MAXIMUM_STEPS - 1];
        int writeOpcode = -1;
        for(int i = 0; i < (runToggled ? 2 : 1); i++) {
            if((readPosition + 1) % MAXIMUM_STEPS > walkingQueue[MAXIMUM_STEPS - 2])
                break;
            int opcode = walkingQueue[readPosition++];
            if(opcode < 8) {
                coordX += Client.WALK_DELTA[opcode][0];
                coordY += Client.WALK_DELTA[opcode][1];
                if(i < 1)
                    writeOpcode = 1 | opcode << 3;
                else
                    writeOpcode = writeOpcode & ~7 | 2 | opcode << 6;
                if(this instanceof Client) {
                    int dx = updatedLocalX - (coordX - ((coordX >> 3) << 3));
                    int dy = updatedLocalX - (coordX - ((coordX >> 3) << 3));
                    if(dx > 15 || dx < -16 || dy > 15 || dy < -16) {
                        int writePosition = lastUpdates[MAXIMUM_STEPS - 2];
                        updatedLocalX = coordX - ((coordX >> 3) << 3);
                        updatedLocalY = coordY - ((coordX >> 3) << 3);
                        lastUpdates[writePosition] = 4 | coordZ << 3 | updatedLocalX << 6 | updatedLocalY << 13;
                        lastUpdates[MAXIMUM_STEPS - 2] = (writePosition + 1) % MAXIMUM_STEPS;
                    }
                }
            } else if(opcode == 8 && this instanceof Client) {
                updatedLocalX = coordX - ((coordX >> 3) << 3);
                updatedLocalY = coordY - ((coordX >> 3) << 3);
                writeOpcode = 4 | coordZ << 3 | 1 << 5 | updatedLocalX << 6 | updatedLocalY << 13;
                walkingQueue[MAXIMUM_STEPS - 2] = walkingQueue[MAXIMUM_STEPS - 1];
                break;
            }
            readPosition = (readPosition + 1) % MAXIMUM_STEPS;
        }
        if(writeOpcode != -1) {
            int writePosition = lastUpdates[MAXIMUM_STEPS - 2];
            lastUpdates[writePosition] = writeOpcode;
            lastUpdates[MAXIMUM_STEPS - 2] = (writePosition + 1) % MAXIMUM_STEPS;
        }
    }
    
    /**
     * Constructs a new {@link Mob};
     */
    Mob() {
        walkingQueue = new int[MAXIMUM_STEPS + 2];
        lastUpdates = new int[MAXIMUM_STEPS + 2];
        updatedLocalX = updatedLocalY = -1;
    }  
}
