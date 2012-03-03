package org.runetekk;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

/**
 * Client.java
 * @version 1.0.0
 * @author RuneTekk Development (SiniSoul)
 */
public final class Client {
    
    /**
     * The size of the buffer for this client.
     */
    public final static int BUFFER_SIZE = 5000;
    
    /**
     * The incoming byte buffer array.
     */
    byte[] incomingBuffer;
    
    /**
     * The current incoming write position.
     */
    int iWritePosition;
    
    /**
     * The current incoming read position.
     */
    int iReadPosition;
    
    /**
     * The local id of this client.
     */
    IntegerNode localId;
    
    /**
     * The generated server session key for this {@link Client}.
     */
    long sessionKey;
    
    /**
     * The name hash of this client.
     */
    int nameHash;
    
    /**
     * The username for the player of this client.
     */
    String username;
    
    /**
     * The password for the player of this client.
     */
    String password;
    
    /**
     * The unique id of this client.
     */
    int uid;
    
    /**
     * The current state of this client.
     */
    int state;
    
    /**
     * The client is currently using the low memory state.
     */
    boolean isLowMemory;
   
    /**
     * The time that the client will timeoutStamp.
     */
    long timeoutStamp;
    
    /**
     * The {@link InputStream} of this client.
     */
    InputStream inputStream;
    
    /**
     * The {@link OutputStream} of this client.
     */
    OutputStream outputStream;
    
    /**
     * Destroys this {@link Client}.
     */
    public void destroy() {
        try {
            inputStream.close();
            outputStream.close();
        } catch(IOException ioex) {}   
        incomingBuffer = null;
        localId = null;
    }
    
    /**
     * Constructs a new {@link Client};
     * @param socket The socket to create the client from.
     */
    public Client(Socket socket) throws IOException {
        this.inputStream = socket.getInputStream();
        this.outputStream = socket.getOutputStream();
        timeoutStamp = -1L;
    } 
}