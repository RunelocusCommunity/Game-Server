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
     * The offset for each type of walk step in the amount of coordinates.
     */
    public final static int[][] WALK_DELTA;
    
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
     * The buffer has been written to the client.
     */
    volatile boolean hasWritten;
    
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
     * The list of added players around this client.
     */
    ListNode addedPlayers;
    
    /**
     * The list of active players around this client.
     */
    ListNode activePlayers;
    
    /**
     * The collection of flags for indexed players.
     */
    byte[] playerIndex;
    
    /**
     * The current amount of listed players.
     */
    int listedPlayers;
    
    /**
     * Force an appearance update if the client does not currently have
     * an active appearance update ready.
     */
    boolean[] appearanceUpdates;
    
    /**
     * The buffer that handles the player appearances and flags.
     */
    ByteBuffer flagBuffer;
    
    /**
     * The active flags variable.
     */
    int activeFlags;
    
    /**
     * The active head icons.
     */
    int headIcons;
    
    /**
     * The current appearance states for the client.
     */
    int[] appearanceStates;
    
    /**
     * The color ids for the appearance states.
     */
    int[] colorIds;
    
    /**
     * The current appearance animations.
     */
    int[] animationIds;
    
    /**
     * The combat level for this client.
     */
    int combatLevel;
    
    /**
     * The skill total for this client.
     */
    int skillTotal;
    
    /**
     * Run is currently toggled.
     */
    boolean isRunActive;
    
    /**
     * The currently pending commands separated by a new line character for
     * each command.
     */
    String commandStr;
    
    /**
     * The loaded music byte array.
     */
    byte[] activeMusic;
    
    /**
     * The chat effects hash.
     */
    int chatEffects;
    
    /**
     * The data for chat.
     */
    byte[] chatData;
    
    /**
     * The current farming patch states.
     */
    int[][] patchStates;
    
    /**
     * The timestamps for when the farming patches were updated last.
     */
    long[][] patchTimestamps;
    
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
    public static void sendMessage(Client client, String message) {
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
    public static void sendCurrentChunk(Client client) {
        ByteBuffer buffer = new ByteBuffer(client.outgoingBuffer);
        int position = client.oWritePosition;
        buffer.offset = position;
        buffer.putByte(73 + client.outgoingCipher.getNextValue());
        buffer.putWord128(client.coordX >> 3);
        buffer.putWord(client.coordY >> 3);
        client.oWritePosition += buffer.offset - position;
    }
    
    /**
     * Sets the tab interface id for a client.
     * @param client The client to send the set tab interface packet to.
     * @param tabId The tab id.
     * @param interId The interface id.
     */
    public static void sendTabInterface(Client client, int tabId, int interId) {
        ByteBuffer buffer = new ByteBuffer(client.outgoingBuffer);
        int position = client.oWritePosition;
        buffer.offset = position;
        buffer.putByte(71 + client.outgoingCipher.getNextValue());
        buffer.putWord(interId);
        buffer.putByteA(tabId);
        client.oWritePosition += buffer.offset - position;
    }
    
    /**
     * Sends a request for a song to be played by the client.
     * @param client The client to send the music to.
     * @param archiveId The archive id of the song to player.
     */
    public static void sendMusic(Client client, int archiveId) {
        ByteBuffer buffer = new ByteBuffer(client.outgoingBuffer);
        int position = client.oWritePosition;
        buffer.offset = position;
        buffer.putByte(74 + client.outgoingCipher.getNextValue());
        buffer.putWordLe(archiveId);
        client.oWritePosition += buffer.offset - position;
    }
    
    /**
     * Sends a configuration value to the client.
     * @param client The client to send the music to.
     * @param configId The id of the configuration.
     * @param value The value of the configuration.
     */
    public static void sendConfig(Client client, int configId, int value) {
        ByteBuffer buffer = new ByteBuffer(client.outgoingBuffer);
        int position = client.oWritePosition;
        buffer.offset = position;
        buffer.putByte((value < 256 ? 36 : 87) + client.outgoingCipher.getNextValue());
        buffer.putWordLe(configId);
        if(value < 256)
            buffer.putByte(value);
        else
            buffer.putDwordA(value);
        client.oWritePosition += buffer.offset - position;
    }
    
    /**
     * Sends the update for the player.
     * @param client The client to send the player update to.
     */
    public static void sendPlayerUpdate(Client client) {
        ByteBuffer buffer = new ByteBuffer(client.outgoingBuffer);
        int position = client.oWritePosition;
        buffer.offset = position;
        buffer.putByte(81 + client.outgoingCipher.getNextValue());
        buffer.putWord(0);
        buffer.initializeBitOffset();
        boolean localUpdate = client.activeFlags != 0;
        boolean localMovementUpdate = true;
        int amountData = client.lastUpdates[client.lastUpdates.length - 1] > client.lastUpdates[client.lastUpdates.length - 2] ? (MAXIMUM_POINTS - client.lastUpdates[client.lastUpdates.length - 2]) + client.lastUpdates[client.lastUpdates.length - 1] : client.lastUpdates[client.lastUpdates.length - 2] - client.lastUpdates[client.lastUpdates.length - 1];
        if(amountData < 0)
            throw new RuntimeException("eek");
        else if(amountData <= 0)
            localMovementUpdate = false;
        buffer.putBits(localUpdate || localMovementUpdate ? 1 : 0, 1);
        if(localUpdate || localMovementUpdate) {
            if(localMovementUpdate) {
                int updateHash = client.lastUpdates[client.lastUpdates[client.lastUpdates.length - 1]];
                buffer.putBits(updateHash & 3, 2);
                if((updateHash & 3) == 1) {
                    buffer.putBits(updateHash >> 2, 3); 
                    buffer.putBits(localUpdate ? 1 : 0, 1);
                } else if((updateHash & 3) == 2) {
                    buffer.putBits((updateHash >> 2) & 7, 3);
                    buffer.putBits(updateHash >> 5, 3);
                    buffer.putBits(localUpdate ? 1 : 0, 1);
                } else if((updateHash & 3) == 3) {
                    buffer.putBits((updateHash >> 2) & 3, 2);
                    buffer.putBits((updateHash >> 4) & 1, 1);
                    buffer.putBits(localUpdate ? 1 : 0, 1);
                    buffer.putBits((updateHash >> 12) & 127, 7);
                    buffer.putBits((updateHash >> 5) & 127, 7);
                }
            } else
                buffer.putBits(0, 2);
        }
        buffer.putBits(client.listedPlayers, 8);
        ListNode node = client.activePlayers;
        while((node = node.childNode) != null) {
            if(!(node instanceof IntegerNode))
                break;
            Client pClient = Main.clientArray[((IntegerNode) node).value];
            int dx = 0;
            int dy = 0;
            if(pClient != null) {
                dx = client.coordX - pClient.coordX;
                dy = client.coordY - pClient.coordY;
            }
            boolean remove = pClient == null || dx > 15 || dx < -15 || dy > 15 || dy < -15;
            if(remove) {
                client.listedPlayers--;
                client.playerIndex[((IntegerNode) node).value >> 3] &= ~(1 << (((IntegerNode) node).value & 7));
                buffer.putBits(1, 1);
                buffer.putBits(3, 2);
                node.removeFromList();
                continue;
            }
            boolean update = pClient.activeFlags != 0 || client.appearanceUpdates[((IntegerNode) node).value];
            boolean movementUpdate = true;
            amountData = pClient.lastUpdates[pClient.lastUpdates.length - 1] > pClient.lastUpdates[pClient.lastUpdates.length - 2] ? (MAXIMUM_POINTS - pClient.lastUpdates[pClient.lastUpdates.length - 2]) + pClient.lastUpdates[pClient.lastUpdates.length - 1] : pClient.lastUpdates[pClient.lastUpdates.length - 2] - pClient.lastUpdates[pClient.lastUpdates.length - 1];
            if(amountData < 0)
                throw new RuntimeException("eek");
            if(amountData <= 0 || (pClient.lastUpdates[pClient.lastUpdates[pClient.lastUpdates.length - 1]] & 3) == 3)
                movementUpdate = false;
            buffer.putBits(update || movementUpdate ? 1 : 0, 1);
            if(update || movementUpdate) {
                if(movementUpdate) {
                    int updateHash = pClient.lastUpdates[pClient.lastUpdates[pClient.lastUpdates.length - 1]];
                    buffer.putBits(updateHash & 3, 2);
                    if((updateHash & 3) == 1) {
                        buffer.putBits(updateHash >> 2, 3); 
                        buffer.putBits(localUpdate ? 1 : 0, 1);
                    } else if((updateHash & 3) == 2) {
                        buffer.putBits((updateHash >> 2) & 7, 3);
                        buffer.putBits(updateHash >> 5, 3);
                        buffer.putBits(localUpdate ? 1 : 0, 1);
                    }
                } else
                    buffer.putBits(0, 2);
            }
        }
        node = client.addedPlayers;
        while((node = node.childNode) != null) {
            if(!(node instanceof IntegerNode))
                break;
            Client pClient = Main.clientArray[((IntegerNode) node).value];
            node.removeFromList();
            if(pClient != null) {
                int dx = pClient.coordX - client.coordX;
                int dy = pClient.coordY - client.coordY;
                buffer.putBits(((IntegerNode) node).value, 11);
                buffer.putBits(1, 1);
                /* Unsure about what else this value could be used for */
                buffer.putBits(1, 1); 
                if(dx < 0)
                    dx += 32;
                if(dy < 0)
                    dy += 32;
                buffer.putBits(dy, 5);
                buffer.putBits(dx, 5);
                client.listedPlayers++;
                client.appearanceUpdates[((IntegerNode) node).value] = true;
                node.parentNode = client.activePlayers.parentNode;
                node.childNode = client.activePlayers;
                node.parentNode.childNode = node;
                node.childNode.parentNode = node;
            }
        }
        buffer.putBits(2047, 11);
        buffer.resetBitOffset();
        node = client.activePlayers;
        if(localUpdate) {
            int len = client.flagBuffer.offset;
            if(len < 1)
                buffer.putByte(0);
            else {
                System.arraycopy(client.flagBuffer.payload, 0, buffer.payload, buffer.offset, len);  
                buffer.offset += len;
            }
        }
        while((node = node.childNode) != null) {
            if(!(node instanceof IntegerNode))
                break;
            Client pClient = Main.clientArray[((IntegerNode) node).value];
            ByteBuffer flagBuffer = pClient.activeFlags != 0 ? pClient.flagBuffer : null;
            if(client.appearanceUpdates[((IntegerNode) node).value] && (pClient.activeFlags & 1 << 7) == 0) {
                flagBuffer = new ByteBuffer(122);
                Client.writeFlaggedUpdates(pClient, flagBuffer, pClient.activeFlags | 1 << 7);
                client.appearanceUpdates[((IntegerNode) node).value] = false;
            }
            if(flagBuffer != null) {
                int len = flagBuffer.offset;
                if(len < 1)
                    buffer.putByte(0);
                else {
                    System.arraycopy(flagBuffer.payload, 0, buffer.payload, buffer.offset, len);  
                    buffer.offset += len;
                }
            }
        }
        int oldOffset = buffer.offset;
        buffer.offset = position + 1;
        buffer.putWord(oldOffset - (position + 3));
        client.oWritePosition += oldOffset - position;
    }
    
    /**
     * Writes the flagged updates for a {@link Client} to the flag buffer.
     * @param client The client to write the updates for.
     */
    public static void writeFlaggedUpdates(Client client, ByteBuffer buffer, int activeFlags) {
        buffer.offset = 2;
        int mask = 0;
        for(int bitOff = 0; bitOff < 10; bitOff++) {
            if((activeFlags & 1 << (bitOff + 1)) != 0) {
                switch(bitOff) {
                    
                    /* Async Walk */
                    case 0:
                        break;
                     
                    /* Force Move */
                    case 1:
                        break;
                    
                    /* Animation */
                    case 2:
                        break;
                        
                    /* Force text */
                    case 3:
                        break;
                        
                    /* Chat text */
                    case 4:
                        {
                            mask |= 0x80;
                            buffer.putWordLe(client.chatEffects);
                            buffer.putByte(2 /* client.rights */);
                            int size = client.chatData.length;
                            buffer.putByteA(size);
                            System.arraycopy(client.chatData, 0, buffer.payload, buffer.offset, size);
                            buffer.offset += size;
                        }
                        break;
                     
                    /* Turn to NPC */
                    case 5:
                        break;
                    
                    /* Appearance */
                    case 6:
                        {
                            mask |= 0x10;
                            int oldOffset = buffer.offset;
                            buffer.putByte(0);
                            writeAppearance(client, buffer);
                            int size = buffer.offset - (oldOffset + 1);
                            buffer.offset = oldOffset;
                            buffer.putByteA(size);
                            buffer.offset += size;
                        }
                        break;
                        
                    /* Turn to Loc */
                    case 7:
                        break;
                       
                    /* Hit One */
                    case 8:
                        break;
                    
                    /* Hit Two */
                    case 9:
                        break;
                }
            }
        }
        int oldOffset = buffer.offset;
        buffer.offset = 0;
        buffer.putByte(mask | 0x40);
        buffer.putByte(mask >> 8);
        buffer.offset = oldOffset;
    }
    
    /**
     * Writes the appearance for a client.
     * @param client The client to write the appearance for.
     * @param buffer The buffer to write the appearance data to.
     */
    public static void writeAppearance(Client client, ByteBuffer buffer) {
        buffer.putByte(0);
        buffer.putByte(client.headIcons);
        for(int i = 0; i < 12; i++) {
            if(client.appearanceStates[i] != 0)
                buffer.putWord(client.appearanceStates[i]);
            else
                buffer.putByte(0);
        }
        for(int i = 0; i < 5; i++) {
            buffer.putByte(client.colorIds[i]);
        }
        for(int i = 0; i < 7; i++) {
            buffer.putWord(client.animationIds[i]);
        }
        buffer.putQword(encodeBase37(client.username));
        buffer.putByte(client.combatLevel);
        buffer.putWord(client.skillTotal);        
    }
    
    /**
     * Populates the added list for players that are around the client.
     * @param client The client to populate its list for.
     */
    public static void populatePlayers(Client client) {
        for(int chunkX = (client.coordX >> 3) - Client.CHUNK_RANGE; chunkX <= (client.coordX >> 3) + Client.CHUNK_RANGE; chunkX++) {
            for(int chunkY = (client.coordY >> 3) - Client.CHUNK_RANGE; chunkY <= (client.coordY >> 3) + Client.CHUNK_RANGE; chunkY++) {
                Region region = null;
                if(Main.regions != null && Main.regions[chunkX >> 3] != null && (region = Main.regions[chunkX >> 3][chunkY >> 3]) != null) {
                    Chunk chunk = null;
                    if(region.chunks != null && region.chunks[chunkX - ((chunkX >> 3) << 3)] != null && (chunk = region.chunks[chunkX - ((chunkX >> 3) << 3)][chunkY - ((chunkY >> 3) << 3)]) != null) {
                        ListNode node = chunk.activeEntities;
                        while((node = node.childNode) != null) {
                            if(!(node instanceof Entity) || client.listedPlayers > Client.PLAYER_UPDATES)
                                break;
                            if(!(node instanceof Client))
                                continue;
                            Client pClient = (Client) node;
                            int pos = pClient.localId.value;
                            int dCx = (pClient.coordX >> 3) - (client.coordX >> 3);
                            int dCy = (pClient.coordY >> 3) - (client.coordY >> 3);
                            if(pos == client.localId.value || (client.playerIndex[pos >> 3] & (1 << (pos & 7))) != 0 || 
                               dCx >= Client.CHUNK_RANGE  || dCy >= Client.CHUNK_RANGE || 
                               dCx <= -Client.CHUNK_RANGE || dCy <= -Client.CHUNK_RANGE)
                                continue;
                            ListNode idNode = new IntegerNode(pos);
                            idNode.parentNode = client.addedPlayers.parentNode;
                            idNode.childNode = client.addedPlayers;
                            idNode.parentNode.childNode = idNode;
                            idNode.childNode.parentNode = idNode;
                            client.playerIndex[pos >> 3] |= 1 << (pos & 7);
                        }
                    }
                }
            }
        }
    }
    
    /**
     * Encodes a string to be a base 37 long value.
     * @param s The string to encode.
     * @return The encoded value.
     */
    public static long encodeBase37(String s) {
        long l = 0L;
        for(int i = 0; i < s.length() && i < 12; i++) {
            char c = s.charAt(i);
            l *= 37L;
            if(c >= 'A' && c <= 'Z')
                l += (1 + c) - 65;
            else
            if(c >= 'a' && c <= 'z')
                l += (1 + c) - 97;
            else
            if(c >= '0' && c <= '9')
                l += (27 + c) - 48;
        }
        for(; l % 37L == 0L && l != 0L; l /= 37L);
        return l;
    }
    
    /**
     * Destroys this {@link Client}.
     */
    public void destroy() {
        removeFromList();
        try {
            inputStream.close();
            outputStream.close();
        } catch(IOException ioex) {}  
        incomingBuffer = null;
        outgoingBuffer = null;
        incomingCipher = null;
        outgoingCipher = null;
        activePlayers = null;
        addedPlayers = null;
        animationIds = null;
        appearanceUpdates = null;
        appearanceStates = null;
        colorIds = null;
        flagBuffer = null;
        commandStr = null;
        localId = null;
        username = null;
        password = null;
        activeMusic = null;
    }
    
    /**
     * Constructs a new {@link Client};
     * @param socket The socket to create the client from.
     */
    public Client(Socket socket) throws IOException {
        this.inputStream = socket.getInputStream();
        this.outputStream = socket.getOutputStream();
        timeoutStamp = -1L;
        isRunActive = true;
    } 
    
    static {
        WALK_DELTA = new int[8][2];
        WALK_DELTA[0][0] = -1;
        WALK_DELTA[0][1] =  1;
        WALK_DELTA[1][1] =  1;
        WALK_DELTA[2][0] =  1;
        WALK_DELTA[2][1] =  1;
        WALK_DELTA[3][0] = -1;
        WALK_DELTA[4][0] =  1;
        WALK_DELTA[5][0] = -1;
        WALK_DELTA[5][1] = -1;
        WALK_DELTA[6][1] = -1;
        WALK_DELTA[7][0] =  1;
        WALK_DELTA[7][1] = -1;
    }
}