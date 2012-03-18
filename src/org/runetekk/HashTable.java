package org.runetekk;

/**
 * HashTable.java
 * @version 1.0.0
 * @author RuneTekk Development (SiniSoul)
 */
public final class HashTable  {
    
    /**
     * The nodes of this hash map.
     */
    ListNode[] nodes;
    
    /**
     * Registers a node on this {@link HashTable}.
     * @param listNode The {@link ListNode} to store.
     * @param currentKey The key that the node will be stored at.
     */
    public void put(ListNode listNode, long key) {
        ListNode bucket = nodes[(int) (key & (long) (nodes.length - 1))];
        listNode.parentNode = bucket;
        listNode.childNode = bucket.childNode;
        listNode.childNode.parentNode = listNode;
        listNode.parentNode.childNode = listNode;
        listNode.currentKey = key;
    }
    
    /**
     * Gets the {@link ListNode} at the specific hash key.
     * @param key The hash key of the node to get.
     * @return The node.
     */
    public ListNode get(long key) {
        ListNode bucket = nodes[(int) (key & (long) (nodes.length - 1))];
        for(ListNode node = bucket.parentNode; node != bucket; node = node.parentNode) {
            if(node.currentKey == key)
                return node;
        }
        return null;
    }
    
    /**
     * Constructs a new {@link HashTable};
     * @param size The size of the map.
     */
    HashTable(int size) {      
        nodes = new ListNode[size];
        for(int i = 0; i < size; i++) {
            ListNode node = nodes[i] = new ListNode();
            node.childNode = node;
            node.parentNode = node;
        }
    }  
}
