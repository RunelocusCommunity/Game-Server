package org.runetekk;

/**
 * Region.java
 * @version 1.0.0
 * @author RuneTekk Development (SiniSoul)
 */
public final class Region {
    
    /**
     * The {@link Region} chunks.
     */
    Chunk[][] chunks;
    
    /**
     * The song that will be played when a player enters this region.
     */
    int songId;
    
    /**
     * Constructs a new {@link Region};
     */
    Region() {
        songId = -1;
    }
}
