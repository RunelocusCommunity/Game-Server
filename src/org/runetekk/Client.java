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
     * The {@link Player} that this client represents.
     */
    Player player;
    
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
     * The output has been written to the client.
     */
    volatile boolean oWritten;
    
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
     * The last time that the buffers were written to.
     */
    long lastWriteTime;
    
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
        buffer.putWord128((client.player.coordX >> 3) + 6);
        buffer.putWord((client.player.coordY >> 3) + 6);
        client.oWritePosition += buffer.offset - position;
    }
    
    /**
     * Adds the players currently around the client.
     * @param client The client to add the players to the added list.
     */
    static void addPlayers(Client client) {
        Player player = null;
        if((player = client.player) == null)
            throw new RuntimeException();      
        BitBuffer buffer = client.addingBuffer;
        /* CAN MOVE THIS */
        buffer.reset();
        buffer.offset = 0;
        int cChunkX = (player.coordX >> 3) + 6;
        int cChunkY = (player.coordY >> 3) + 6;
        for(int chunkX = cChunkX - CHUNK_RANGE; chunkX <= cChunkX + CHUNK_RANGE; chunkX++) {
            for(int chunkY = cChunkY - CHUNK_RANGE; chunkY <= cChunkY + CHUNK_RANGE; chunkY++) {
                Region region = null;
                if(Main.regions != null && Main.regions[chunkX << 3] != null && (region = Main.regions[chunkX << 3][chunkY << 3]) != null) {
                    Chunk chunk = null;
                    if(region.chunks != null && region.chunks[((player.coordX - (chunkX << 3)) >> 3)] 
                                     != null && (chunk = region.chunks[((player.coordX - (chunkX << 3)) >> 3)]
                                     [((player.coordY - (chunkY << 3)) >> 3)]) != null) {
                        ListNode node = chunk.activeEntities; 
                        while((node = node.childNode) != null) { 
                            if(!(node instanceof Player) || node == player)
                                continue;  
                            int pos = ((Player) node).id;
                            if((client.playerIndex[pos >> 3] & (1 << (pos & 7))) != 0)
                                continue;
                            int dx = player.coordX - ((Player) node).coordX;
                            if(dx > 15 || dx < -16)
                                continue;
                            if(dx < 0)
                                dx += 32;
                            int dy = player.coordX - ((Player) node).coordX;
                            if(dy > 15 || dy < -16)
                                continue;
                            if(dy < 0)
                                dy += 32;
                            buffer.put(pos, 11);                       
                            /* 
                             * FIX ME 
                             * -APPEND UPDATES
                             */
                            buffer.put(0, 1);                          
                            /* 
                             * FIX ME 
                             * -CLEAR QUEUE FLAG
                             */
                            buffer.put(1, 1);
                            buffer.put(dy, 5);
                            buffer.put(dx, 5);
                            IntegerNode posNode = new IntegerNode(pos);
                            posNode.parentNode = client.addedPlayers.parentNode;
                            posNode.childNode = client.addedPlayers;
                            posNode.parentNode.childNode = posNode;
                            posNode.childNode.parentNode = posNode;                           
                            client.playerIndex[pos >> 3] |= 1 << (pos & 7);
                        }
                    }
                }
            } 
        }
        buffer.put(2047, 11);
    }
    
    static void writePlayers(Main main, Client client) {
        Player player = null;
        if((player = client.player) == null)
            throw new RuntimeException();      
        BitBuffer buffer = client.playerBuffer;
        /* CAN MOVE THIS */
        buffer.reset();
        buffer.offset = 8;
        int amountPlayers = 0;
        ListNode node = client.addedPlayers;
        while((node = node.childNode) != null) {
            if(!(node instanceof IntegerNode) || node == player)
                continue;  
            /* FIX ME */
            if(amountPlayers >= PLAYER_UPDATES)
                break;
            amountPlayers++;
            int pos = ((IntegerNode) node).value;
            Player aPlayer = null;
            if((aPlayer = main.clientArray[pos].player) != null) {
                int dx = player.coordX - ((Player) node).coordX;
                int dy = player.coordX - ((Player) node).coordX;
                if(dy > 15 || dy < -16 || dx > 15 || dx < -16)
                    aPlayer = null;
            }
            if(aPlayer != null) {
                /* MAJOR FIX ME */
                buffer.put(0, 1);
            } else {
                buffer.put(1, 1);
                buffer.put(3, 2);
                node.removeFromList();
                client.playerIndex[pos >> 3] &= ~(1 << (pos & 7));
            }
        }
        int oldOffset = buffer.offset;
        buffer.offset = 0;
        buffer.put(amountPlayers, 8);
        buffer.offset = oldOffset;
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
        player.removeFromList();
        player = null;
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
        player = new Player(localId.value);
        rights = 2;
    } 
}