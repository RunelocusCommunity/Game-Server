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
public final class Client extends Mob {
    
    /**
     * The size of the buffer for this client.
     */
    public final static int BUFFER_SIZE = 5000;
    
    /**
     * The amount of players that can be updated per player update packet.
     */
    public final static int PLAYER_UPDATES = 256;
    
    /**
     * The amount of chunks that can be viewed by the player on all sides.
     */
    public final static int CHUNK_RANGE = 2;
    
    /**
     * The inbound {@link IsaacCipher}.
     */
    IsaacCipher incomingCipher;
    
    /**
     * The incoming byte buffer array.
     */
    byte[] incomingBuffer;
    
    /**
     * The current incoming write position.
     */
    int iWritePosition;
    
    /**
     * The outbound {@link IsaacCipher}.
     */
    IsaacCipher outgoingCipher;
    
    /**
     * The current incoming read position.
     */
    int iReadPosition;
    
    /**
     * The outgoing byte array source.
     */
    byte[] outgoingBuffer;
    
    /**
     * The current outgoing write position.
     */
    volatile int oWritePosition;
    
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
     * The amount of rights, admin/moderator/player, that this client has.
     */
    int rights;
    
    /**
     * The client is currently using the low memory state.
     */
    boolean isLowMemory;
    
    /**
     * The client is currently reconnecting.
     */
    boolean isReconnecting;
    
    /**
     * The last time that the client was pinged by the server.
     */
    long lastRecievedPing;
    
    /**
     * The buffer that handles movement updates.
     */
    BitBuffer movementBuffer;
    
    /**
     * The buffer that handles player updates.
     */
    BitBuffer playerBuffer;
    
    /**
     * The buffer that handles adding players.
     */
    BitBuffer addingBuffer;
    
    /**
     * The buffer that handles the player appearances and flags.
     */
    ByteBuffer flagBuffer;
    
    /**
     * The list of players within coordinate range to update.
     */
    ListNode addedPlayers;
    
    /**
     * The added player index.
     */
    byte[] playerIndex;
   
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
     * Sends a message to a client.
     * @param client The client to write the message to.
     * @param message The message to write.
     */
    static void sendMessage(Client client, String message) {
        ByteBuffer buffer = new ByteBuffer(client.outgoingBuffer);
        int position = client.oWritePosition;
        buffer.offset = position;
        buffer.putByte(253 + client.outgoingCipher.getNextValue());
        buffer.putByte(message.length() + 1);
        buffer.putString(message);
        client.oWritePosition += buffer.offset - position;
    }
    
    /**
     * Sends the current chunk coordinates of where the client is.
     * @param client The client to write the packet to.
     */
    static void sendCurrentChunk(Client client) {
        ByteBuffer buffer = new ByteBuffer(client.outgoingBuffer);
        int position = client.oWritePosition;
        buffer.offset = position;
        buffer.putByte(73 + client.outgoingCipher.getNextValue());
        buffer.putWord128((client.coordX >> 3) + 6);
        buffer.putWord((client.coordY >> 3) + 6);
        client.oWritePosition += buffer.offset - position;
    }
    
    /**
     * Destroys this {@link Client}.
     */
    public void destroy() {
        try {
            inputStream.close();
            outputStream.close();
        } catch(IOException ioex) {}  
        incomingBuffer = null;
        outgoingBuffer = null;
        incomingCipher = null;
        outgoingCipher = null;
        movementBuffer = null;
        playerBuffer = null;
        localId = null;
        username = null;
        password = null;
    }
    
    /**
     * Constructs a new {@link Client};
     * @param socket The socket to create the client from.
     */
    public Client(Socket socket) throws IOException {
        this.inputStream = socket.getInputStream();
        this.outputStream = socket.getOutputStream();
        timeoutStamp = -1L;
        /* FIX THIS TIDBIT LATER */
        rights = 2;
    } 
}