package org.runetekk;

/**
 * ByteBuffer.java
 * @version 1.0.0
 * @author RuneTekk Development (SiniSoul)
 */
public final class ByteBuffer {
    
    /**
     * The mask array.
     */
    private final static int[] MASKS;
    
    /**
     * The byte array payload of this {@link ByteBuffer}.
     */
    byte[] payload;
    
    /**
     * The current offset in the payload.
     */
    int offset;
    
    /**
     * The current bit offset in the payload.
     */
    int bitOffset;
    
    /**
     * Puts a byte into the payload.
     * @param value The byte value.
     */
    void putByte(int value) {
        payload[offset++] = (byte) value;
    }
    
    /**
     * Gets an unsigned byte from this buffer casted to an integer.
     * @return The unsigned byte value.
     */
    int getUbyte() {
        return payload[offset++] & 0xFF;
    }
    
    /**
     * Gets an unsigned type b byte from this buffer casted to an integer.
     * @return The unsigned byte value.
     */
    int getUbyteB() {
        return (128 - payload[offset++]) & 0xFF;
    }
    
    /**
     * Gets an signed byte from this buffer.
     * @return The byte value.
     */
    byte getByte() {
        return payload[offset++];
    }
    
    /**
     * Puts a byte form a into the payload.
     * @param value The byte value.
     */
    void putByteA(int value) {
        payload[offset++] = (byte) (-value);
    }
    
    /**
     * Puts a word into the payload.
     * @param value The word value.
     */
    void putWord(int value) {
        payload[offset++] = (byte) (value >> 8);
        payload[offset++] = (byte) (value & 0xFF);
    }
    
    /**
     * Puts a little endian word into the payload.
     * @param value The word value.
     */
    void putWordLe(int value) {
        payload[offset++] = (byte) (value & 0xFF);
        payload[offset++] = (byte) (value >> 8);
    }
    
    /**
     * Puts a word plus 128 into the payload.
     * @param value The word value.
     */
    void putWord128(int value) {
        payload[offset++] = (byte) (value >> 8);
        payload[offset++] = (byte) (value + 128 & 0xFF);
    }
    
    /**
     * Gets an unsigned word from this buffer casted to an integer.
     * @return The unsigned word value.
     */
    int getUword() {
        return  ((payload[offset++] & 0xFF) << 8) | 
                 (payload[offset++] & 0xFF);
    }
    
    /**
     * Gets an little endian unsigned word casted to an integer. 
     * @return The unsigned word value.
     */
    int getUwordLe() {
        return  (payload[offset++] & 0xFF) | 
               ((payload[offset++] & 0xFF) << 8);
    }
    
    /**
     * Gets an little endian unsigned word translated by 128 casted to an integer. 
     * @return The unsigned word value.
     */
    int getUwordLe128() {
        return  (payload[offset++] - 128 & 0xFF) | 
               ((payload[offset++] & 0xFF) << 8);
    }
    
    /**
     * Puts a dword type a into this buffer.
     * @param value The dword value. 
     */
    void putDwordA(int value) {
        payload[offset++] = (byte) (value >> 8);
        payload[offset++] = (byte)  value;
        payload[offset++] = (byte) (value >> 24);
        payload[offset++] = (byte) (value >> 16);
    }
    
    /**
     * Gets an unsigned dword from this buffer casted to an integer.
     * @return The unsigned dword value.
     */
    int getDword() {
        return ((payload[offset++] & 0xFF) << 24) | 
               ((payload[offset++] & 0xFF) << 16) | 
               ((payload[offset++] & 0xFF) << 8) | 
                (payload[offset++] & 0xFF);
    }
    
    /**
     * Puts a qword into the payload.
     * @param value The qword value.
     */
    void putQword(long value) {
        payload[offset++] = (byte) (value >> 56);
        payload[offset++] = (byte) (value >> 48);
        payload[offset++] = (byte) (value >> 40);
        payload[offset++] = (byte) (value >> 32);
        payload[offset++] = (byte) (value >> 24);
        payload[offset++] = (byte) (value >> 16);
        payload[offset++] = (byte) (value >> 8);
        payload[offset++] = (byte) (value & 0xFF);
    }
    
    /**
     * Puts a string into the payload.
     * @param str The string to putBits into the payload.
     */
    void putString(String str) {
        System.arraycopy(str.getBytes(), 0, payload, offset, str.length());
        offset += str.length();
        putByte(10);
    }
    
    /**
     * Gets a string from the buffer. The ending character being the null
     * character for a C-style string.
     * @return The string.
     */
    String getString() {
        int position = offset;
        while(payload[offset++] != 10) ;
        return new String(payload, position, offset - position - 1);
    }
    
    /**
     * Initializes the bit offset.
     */
    void initializeBitOffset() {
        bitOffset = offset * 8;
    }
    
    /**
     * Sets the current offset of the byte buffer from the bit offset.
     */
    void resetBitOffset() {
        offset = (bitOffset + 7) / 8;
    }
    
    /**
     * Puts a value into the payload array.
     * @param value The value to putBits into the payload array.
     * @param amountBits The amount of bits to write the value as.
     */
    void putBits(int value, int amountBits) {
        int byteOffset = bitOffset >> 3;
        int off = 8 - (bitOffset & 7);
        bitOffset += amountBits;
        for (; amountBits > off; off = 8) {
            payload[byteOffset] &= ~MASKS[off];
            payload[byteOffset] |= value >> (amountBits - off) & MASKS[off];            
            amountBits -= off;
            byteOffset++;
        }
        if (amountBits == off) {
            payload[byteOffset] &= ~MASKS[off];
            payload[byteOffset] |= value & MASKS[off];
        } else {
            payload[byteOffset] &= ~MASKS[amountBits] << (off - amountBits);
            payload[byteOffset] |= (value & MASKS[amountBits]) << (off - amountBits);            
        }
    }
    
    /**
     * Constructs a new {@link ByteBuffer};
     * @param size The size of the payload array. 
     */
    ByteBuffer(int size) {
        payload = new byte[size];
    }
    
    /**
     * Constructs a new {@link ByteBuffer};
     * @param src The source byte array.
     */
    ByteBuffer(byte[] src) {
        this.payload = src;
    }   
          
    static {
        MASKS = new int[32];
        for (int i = 0; i < 32; i++)
            MASKS[i] = (1 << i) - 1;
    } 
}
