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
    int updatedChunkX;
    
    /**
     * The local y coordinate where everything is centered from.
     */
    int updatedChunkY;
    
    /**
     * Updates the movement of this mob.
     */
    void updateMovement() {
        boolean runToggled = false;
        if(this instanceof Client)
            runToggled = ((Client) this).isRunActive;
        if(updatedChunkX < 0 && updatedChunkY < 0) {
            walkingQueue[walkingQueue.length - 2] = 1;
            walkingQueue[walkingQueue.length - 1] = 0;
            walkingQueue[0] = 8;
        }
        int readPosition = walkingQueue[walkingQueue.length - 1];
        int writeOpcode = -1;
        for(int i = 0; i < (runToggled ? 2 : 1); i++) {
            if((readPosition + 1) % MAXIMUM_STEPS > walkingQueue[walkingQueue.length - 2])
                break;
            int opcode = walkingQueue[readPosition];
            System.out.println("OPCODE " + opcode);
            if(opcode < 8) {
                coordX += Client.WALK_DELTA[opcode][0];
                coordY += Client.WALK_DELTA[opcode][1];
                if(i < 1)
                    writeOpcode = 1 | opcode << 2;
                else
                    writeOpcode = writeOpcode & ~3 | 2 | opcode << 5;
                if(this instanceof Client) {
                    int dx = updatedChunkX - ((coordX >> 3) - 6);
                    int dy = updatedChunkY - ((coordY >> 3) - 6);
                    System.out.println("dx: " + dx + ", dy: " + dy);
                    if(dx > 4 || dx < -4 || dy > 4 || dy < -4) {
                        int writePosition = lastUpdates[lastUpdates.length - 2];
                        updatedChunkX = (coordX >> 3) - 6;
                        updatedChunkY = (coordY >> 3) - 6;
                        lastUpdates[writePosition] = 3 | coordZ << 2 | (coordX - (updatedChunkX << 3)) << 5 | (coordY - (updatedChunkY << 3)) << 12;
                        lastUpdates[lastUpdates.length - 2] = (writePosition + 1) % MAXIMUM_STEPS;
                        Client.sendCurrentChunk((Client) this);
                    }
                }
                readPosition = walkingQueue[walkingQueue.length - 1] = (readPosition + 1) % MAXIMUM_STEPS;
            } else if(opcode == 8 && this instanceof Client) {
                updatedChunkX = (coordX >> 3) - 6;
                updatedChunkY = (coordY >> 3) - 6;
                writeOpcode = 3 | coordZ << 2 | 1 << 4 | (coordX - (updatedChunkX << 3)) << 5 | (coordY - (updatedChunkY << 3)) << 12;
                walkingQueue[walkingQueue.length - 2] = walkingQueue[walkingQueue.length - 1];
                Client.sendCurrentChunk((Client) this);
                break;
            }
        }
        if(writeOpcode != -1) {
            int writePosition = lastUpdates[lastUpdates.length - 2];
            lastUpdates[writePosition] = writeOpcode;
            lastUpdates[lastUpdates.length - 2] = (writePosition + 1) % MAXIMUM_STEPS;
        }
    }
    
    /**
     * Constructs a new {@link Mob};
     */
    Mob() {
        walkingQueue = new int[MAXIMUM_STEPS + 2];
        lastUpdates = new int[MAXIMUM_STEPS + 2];
        updatedChunkX = updatedChunkY = -1;
    }  
}
