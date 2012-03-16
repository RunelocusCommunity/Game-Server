package org.runetekk;

/**
 * StringNode.java
 * @version 1.0.0
 * @author RuneTekk Development (SiniSoul)
 */
public class StringNode extends ListNode {
    
    /**
     * The value of this node.
     */
    String value;
    
    /**
     * Constructs a new {@link StringNode};
     * @param value The value of the node.
     */
    StringNode(String value) {
        this.value = value;
    }
}
