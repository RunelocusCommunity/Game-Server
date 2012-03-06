package org.runetekk;

/**
 * ListNode.java
 * @version 1.0.0
 * @author RuneTekk Development (SiniSoul)
 */
public class ListNode {
    
    /**
     * The parent {@link ListNode}.
     */
    ListNode parentNode;
    
    /**
     * The child {@link ListNode}.
     */
    ListNode childNode;
    
    /**
     * Removes the node from the current queue list it is in.
     */
    public final void removeFromList() {
        if(parentNode != null) {
             childNode.parentNode = parentNode;
             parentNode.childNode = childNode;
             childNode = null;
             parentNode = null;
         }
    }    
}
