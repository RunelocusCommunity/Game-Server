package org.runetekk;

/**
 * CustomChunk.java
 * @version 1.0.0
 * @author RuneTekk Development (SiniSoul)
 */
public final class CustomChunk extends Chunk {
    
    /**
     * The hashes for all the chunk planes.
     */
    int[] chunkHashes;
    
    /**
     * Constructs a new {@link CustomChunk};
     */
    CustomChunk() {
        chunkHashes = new int[4];
        chunkHashes[0] = 1 | (3200 >> 3) << 14 | (3200 >> 3) << 3;
    }  
}
