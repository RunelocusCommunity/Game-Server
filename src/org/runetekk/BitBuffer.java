package org.runetekk;

/**
 * BitBuffer.java
 * @version 1.0.0
 * @author RuneTekk Development (SiniSoul)
 */
public final class BitBuffer {
    
    /**
     * The mask array.
     */
    private final static int[] MASKS;
    
    /**
     * The payload byte array.
     */
    byte[] payload;
    
    /**
     * The amount of bits in this bit buffer.
     */
    int size;
    
    /**
     * The current offset in the payload array.
     */
    int offset;
    
    /**
     * Puts a value into the payload array.
     * @param value The value to put into the payload array.
     * @param amountBits The amount of bits to write the value as.
     */
    void put(int value, int amountBits) {
        int byteOffset = offset >> 3;
        int bitOffset = 8 - (offset & 7);
        offset += amountBits;
        for (; amountBits > bitOffset; bitOffset = 8) {
            payload[byteOffset] &= ~MASKS[bitOffset];
            payload[byteOffset] |= value >> (amountBits - bitOffset) & MASKS[bitOffset];            
            amountBits -= bitOffset;
            byteOffset++;
        }
        if (amountBits == bitOffset) {
            payload[byteOffset] &= ~MASKS[bitOffset];
            payload[byteOffset] |= value & MASKS[bitOffset];
        } else {
            payload[byteOffset] &= ~MASKS[bitOffset] << (bitOffset - amountBits);
            payload[byteOffset] |= (value & MASKS[bitOffset]) << (bitOffset - amountBits);            
        }
    }
    
    /**
     * Constructs a new {@link BitBuffer};
     * @param size The size in bits of the buffer.
     */
    BitBuffer(int size) {
        payload = new byte[(size + 1) << 3];
        this.size = size;
    }
    
    static {
        MASKS = new int[32];
        for (int i = 0; i < 32; i++)
            MASKS[i] = (1 << i) - 1;
    } 
}
