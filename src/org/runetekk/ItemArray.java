package org.runetekk;

/**
 * ItemArray.java
 * @version 1.0.0
 * @author RuneTekk Development (SiniSoul)
 */
public final class ItemArray extends ListNode {
    
    /**
     * The items in this item array.
     */
    Item[] items;
    
    /**
     * Constructs a new {@link ItemArray};
     * @param items The item array.
     */
    ItemArray(Item[] items) {
        this.items = items;
    }
}
