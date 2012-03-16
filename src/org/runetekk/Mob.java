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
     * The step queue where points of where the player will need to move towards.
     * These interpolation between one of the points and the current coordinates
     * should only be 1, -1, 0, undef. Variables cannot just be placed into
     * the queue and the queue will be dropped if the conditions prove to be
     * false.
     */
    int[] stepQueue;
    
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
     * Updates the step queue of this mob.
     */
    void updateSteps() {
        if(stepQueue[stepQueue.length - 2] > 0) {
            boolean runToggled = false;
            if(this instanceof Client)
                runToggled = ((Client) this).isRunActive;
            int x = coordX;
            int y = coordY;
            int i = 0;
            for(; i < (runToggled ? 2 : 1);) {
                if(stepQueue[stepQueue.length - 1] >= stepQueue[stepQueue.length - 2])
                    return;
                int position = stepQueue[stepQueue.length - 1];
                int dx = ((stepQueue[position] >> 15) & 0x7FFF) - x;
                int dy = (stepQueue[position] & 0x7FFF) - y;
                if(dx == 0 && dy == 0) {
                    stepQueue[stepQueue.length - 1] = (position + 1) % Mob.MAXIMUM_STEPS; 
                    continue;
                }
                int writePosition = walkingQueue[walkingQueue.length - 2];
                int opcode = -1;
                if(dx == 0)
                    if(dy > 0)
                       opcode = 1;
                    else
                       opcode = 6;
                else if(dy == 0)
                    if(dx > 0)
                        opcode = 4;
                    else
                        opcode = 3;
                else if(dx < 0)
                    if(dy < 0)
                        opcode = 5;
                    else
                        opcode = 0;
                else if(dy < 0)
                    opcode = 7;
                else
                    opcode = 2;
                walkingQueue[writePosition] = opcode;
                walkingQueue[walkingQueue.length - 2] = (writePosition + 1) % Mob.MAXIMUM_STEPS;
                x += Client.WALK_DELTA[opcode][0];
                y += Client.WALK_DELTA[opcode][1];
                i++;
            }
        }
    }
    
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
        boolean updateRegion = false;
        int readPosition = walkingQueue[walkingQueue.length - 1];
        int writeOpcode = -1;
        int amountData = readPosition > walkingQueue[walkingQueue.length - 2] ? (MAXIMUM_STEPS - readPosition) + walkingQueue[walkingQueue.length - 2] : walkingQueue[walkingQueue.length - 2] - readPosition;
        for(int i = 0; i < (runToggled ? 2 : 1); i++) {
            if(amountData-- < 1)
                break;
            int opcode = walkingQueue[readPosition];
            readPosition = walkingQueue[walkingQueue.length - 1] = (readPosition + 1) % MAXIMUM_STEPS;
            if(opcode < 8) {
                if(i < 1)
                    writeOpcode = 1 | opcode << 2;
                else
                    writeOpcode = writeOpcode & ~3 | 2 | opcode << 5;
                coordX += Client.WALK_DELTA[opcode][0];
                coordY += Client.WALK_DELTA[opcode][1];
                if(this instanceof Client) {
                    int dx = updatedChunkX - ((coordX >> 3) - 6);
                    int dy = updatedChunkY - ((coordY >> 3) - 6);
                    if(dx >= 4 || dx <= -4 || dy >= 4 || dy <= -4) {                      
                        updateRegion = true;
                        break;
                    }                  
                }
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
        if(updateRegion) {
            int writePosition = lastUpdates[lastUpdates.length - 2];
            updatedChunkX = (coordX >> 3) - 6;
            updatedChunkY = (coordY >> 3) - 6;
            lastUpdates[writePosition] = 3 | coordZ << 2 | (coordX - (updatedChunkX << 3)) << 5 | (coordY - (updatedChunkY << 3)) << 12;
            lastUpdates[lastUpdates.length - 2] = (writePosition + 1) % MAXIMUM_STEPS;
            Client.sendCurrentChunk((Client) this);
        }
    }
    
    /**
     * Constructs a new {@link Mob};
     */
    Mob() {
        walkingQueue = new int[MAXIMUM_STEPS + 2];
        lastUpdates = new int[MAXIMUM_STEPS + 2];
        stepQueue = new int[MAXIMUM_STEPS + 2];
        updatedChunkX = updatedChunkY = -1;
    }  
}
