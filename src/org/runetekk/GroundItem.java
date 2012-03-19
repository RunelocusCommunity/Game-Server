package org.runetekk;

/**
 * GroundItem.java
 * @version 1.0.0
 * @author RuneTekk Development (SiniSoul)
 */
public final class GroundItem extends Entity {
    
    /**
     * The local id of the ground item in the ground items array.
     */
    int localId;
    
    /**
     * The local id of the client who spawned this item.
     */
    int clientId;
    
    /**
     * The item id of this ground item.
     */
    int id;
    
    /**
     * The amount of the item on the ground.
     */
    int amount;
    
    /**
     * The node that references to the player who spawned this item so that
     * when they log out the items that they created can be removed.
     */
    IntegerNode referenceNode;
    
    /**
     * The time that this item will appear to people who aren't the owner of this item.
     */
    long appearTime;
    
    /**
     * The time that this item will be destroyed.
     */
    long destroyTime;
    
    /**
     * The current update number.
     */
    int currentUpdate;
    
    /**
     * Flag to remove the item from the client.
     */
    boolean remove;
    
    /**
     * Destroys this {@link GroundItem}.
     */
    public void destroy() {
        if(referenceNode != null)
            referenceNode.removeFromList();
        removeFromList();
        referenceNode = null;
        remove = true;
    }
    
    /**
     * Constructs a new {@link GroundItem};
     */
    GroundItem() {
        
    }
}
