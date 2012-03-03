package org.runetekk;

/**
 * ByteBuffer.java
 * @version 1.0.0
 * @author RuneTekk Development (SiniSoul)
 */
public final class ByteBuffer {
    
    /**
     * The byte array payload of this {@link ByteBuffer}.
     */
    byte[] payload;
    
    /**
     * The current offset in the payload.
     */
    int offset;
    
    /**
     * Gets an unsigned byte from this buffer casted to an integer.
     * @return The unsigned byte value.
     */
    int getUbyte() {
        return payload[offset++] & 0xFF;
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
}
