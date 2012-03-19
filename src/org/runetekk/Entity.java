package org.runetekk;

/**
 * Entity.java
 * @version 1.0.0
 * @author RuneTekk Development (SiniSoul)
 */
public class Entity extends ListNode {
    
    /**
     * The default X coordinate.
     */
    private static final int DEFAULT_X = 2810;
    
    /**
     * The default Y coordinate.
     */
    private static final int DEFAULT_Y = 3467;
    
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
     * The updated region hash.
     */
    int rUpdatedHash;
    
    /**
     * The updated chunk hash.
     */
    int cUpdatedHash;
    
    /**
     * Updates the region that this entity is in.
     */
    void updateRegion() {      
        int cHash = (((coordX >> 3) - ((coordX >> 6) << 3)) << 8) | ((coordY >> 3) - ((coordY >> 6) << 3));
        int rHash = ((coordX >> 6) << 8) | ((coordY) >> 6);
        boolean updateRegion = cHash != cUpdatedHash;
        if(updateRegion || cHash != cUpdatedHash) {
            removeFromList();  
            Region region = null;       
            Chunk chunk = null;
            if(Main.regions != null && Main.regions[coordX >> 6] != null && (region = Main.regions[coordX >> 6][coordY >> 6]) != null) {
                if(region.chunks != null && region.chunks[(coordX >> 3) - ((coordX >> 6) << 3)] != null &&
                                   (chunk = region.chunks[(coordX >> 3) - ((coordX >> 6) << 3)]
                                                     [(coordY >> 3) - ((coordY >> 6) << 3)]) != null) {
                    ListNode list = null;
                    if(updateRegion && this instanceof Client) {
                        list = chunk.activePlayers;
                        if(!((Client) this).isLowMemory && region.songId != -1) {
                            int songId = region.songId;
                            Client client = (Client) this;
                            Client.sendMusic(client, songId);
                            if(Main.musicNames.length - 1 >= songId && Main.musicNames[songId] != null) {
                                if((client.activeMusic[songId >> 3] & (1 << (songId & 7))) == 0) {
                                    Client.sendMessage(client, "@red@You have unlocked the music track: " + Main.musicNames[region.songId] + ".");
                                    client.activeMusic[songId >> 3] |= 1 << (songId & 7);
                                }
                            }
                        }
                    } else if(this instanceof GroundItem) {
                        list = chunk.activeItems;
                    }
                    parentNode = list.parentNode;
                    childNode = list;
                    parentNode.childNode = this;
                    childNode.parentNode = this;   
                    rUpdatedHash = rHash;
                    cUpdatedHash = cHash;
                    return;
                }
            }
            throw new RuntimeException("eek");
        }   
    }
    
    /**
     * Constructs a new {@link Entity};
     */
    Entity() {
        coordX = DEFAULT_X;
        coordY = DEFAULT_Y;
        rUpdatedHash = -1;
        cUpdatedHash = -1;
    }
}
