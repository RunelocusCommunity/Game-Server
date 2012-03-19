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
     * The attack tab id in the interface toolbar.
     */
    public final static int ATTACK_TAB = 0;
    
    /**
     * The level and experience tab id in the interface toolbar.
     */
    public final static int LEVELS_TAB = 1;
    
    /**
     * The quest tab id in the interface toolbar.
     */
    public final static int QUEST_TAB = 2;
    
    /**
     * The inventory tab id in the interface toolbar.
     */
    public final static int INVENTORY_TAB = 3;
    
    /**
     * The equipment tab id in the interface toolbar.
     */
    public final static int EQUIPMENT_TAB = 4;
    
    /**
     * The prayer tab id in the interface toolbar.
     */
    public final static int PRAYER_TAB = 5;
    
    /**
     * The magic tab id in the interface toolbar.
     */
    public final static int MAGIC_TAB = 6;
    
    /**
     * The ignore tab id in the interface toolbar.
     */
    public final static int IGNORE_TAB = 7;
    
    /**
     * The friend tab id in the interface toolbar.
     */
    public final static int FRIEND_TAB = 8;
    
    /**
     * The logout tab id in the interface toolbar.
     */
    public final static int LOGOUT_TAB = 10;
    
    /**
     * The setting tab id in the interface toolbar.
     */
    public final static int SETTING_TAB = 11;
        
    /**
     * The energy tab id in the interface toolbar.
     */
    public final static int ENERGY_TAB = 12;
    
    /**
     * The music tab id in the interface toolbar.
     */
    public final static int MUSIC_TAB = 13;
    
    /**
     * The size of the inventory.
     */
    public final static int INVENTORY_SIZE = 28;   
    
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
     * The generated server session currentKey for this {@link Client}.
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
     * The item arrays for the active widgets of this client.
     */
    HashTable widgetItems;
    
    /**
     * The list of items spawned by this client.
     */
    ListNode spawnedItems;
        
    /**
     * The list of items active in the client.
     */
    ListNode activeItems;
    
    /**
     * The index of added items on the clients map.
     */
    byte[] itemIndex;
    
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
     * Sends an update to a widget to populate the items.
     * @param client The client to write the message to.
     * @param widgetId The widget id.
     * @param items The items.
     */
    public static void sendWidgetItems(Client client, int widgetId, Item[] items) {
        ByteBuffer buffer = new ByteBuffer(client.outgoingBuffer);
        int position = client.oWritePosition;
        buffer.offset = position;
        buffer.putByte(53 + client.outgoingCipher.getNextValue());
        buffer.putWord(0);
        buffer.putWord(widgetId);
        buffer.putWord(items.length);
        for(Item item : items) {
            int id = (item == null ? -1 : item.id) + 1;
            int amount = item == null ? 0 : item.amount;
            if(amount >= 255) {
                buffer.putByte(255);
                buffer.putDwordB(amount);
            } else
                buffer.putByte(amount);
            buffer.putWordLe128(id);
        }
        int oldOffset = buffer.offset;
        buffer.offset = position + 1;
        buffer.putWord(oldOffset - (position + 3));
        client.oWritePosition += oldOffset - position;
    }
    
    /**
     * Sends an update to a widget with items.
     * @param client The client to write the message to.
     * @param widgetId The widget id.
     * @param items The items to send.
     * @param updateSlots The slots to update.
     */
    public static void sendUpdateWidgetItems(Client client, int widgetId, Item[] items, int[] updateSlots) {
        ByteBuffer buffer = new ByteBuffer(client.outgoingBuffer);
        int position = client.oWritePosition;
        buffer.offset = position;
        buffer.putByte(34 + client.outgoingCipher.getNextValue());
        buffer.putWord(0);
        buffer.putWord(widgetId);
        for(int i = 0; i < updateSlots.length; i++) {
            Item item = items[updateSlots[i]];
            int id = (item == null ? -1 : item.id) + 1;
            int amount = item == null ? 0 : item.amount;
            buffer.putSmartB(updateSlots[i]);
            buffer.putWord(id);
            if(amount >= 255) {
                buffer.putByte(255);
                buffer.putDword(amount);
            } else
                buffer.putByte(amount);
        }
        int oldOffset = buffer.offset;
        buffer.offset = position + 1;
        buffer.putWord(oldOffset - (position + 3));
        client.oWritePosition += oldOffset - position;
    }
    
    /**
     * Sends an update to a widget to clear the items
     * @param client The client to write the message to.
     * @param widgetId The widget id.
     */
    public static void sendClearWidgetItems(Client client, int widgetId) {
        ByteBuffer buffer = new ByteBuffer(client.outgoingBuffer);
        int position = client.oWritePosition;
        buffer.offset = position;
        buffer.putByte(72 + client.outgoingCipher.getNextValue());
        buffer.putWord(widgetId);
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
        buffer.putByte128(tabId);
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
     * Sends the local server client info to the client.
     * @param client The client to send the info to.
     * @param isMember The client is a member.
     */
    public static void sendInfo(Client client, boolean isMember) {
        ByteBuffer buffer = new ByteBuffer(client.outgoingBuffer);
        int position = client.oWritePosition;
        buffer.offset = position;
        buffer.putByte(249 + client.outgoingCipher.getNextValue());
        buffer.putByte128(isMember ? 0 : 1);
        buffer.putWordLe128(client.localId.value);
        client.oWritePosition += buffer.offset - position;
    }
    
    /**
     * Sends the map coordinates for spawning packets.
     * @param client The client to send the coordinates to.
     * @param coordX The map coordinate x.
     * @param coordY The map coordinate y.
     */
    public static void sendMapCoords(Client client, int coordX, int coordY) {
        ByteBuffer buffer = new ByteBuffer(client.outgoingBuffer);
        int position = client.oWritePosition;
        buffer.offset = position;
        buffer.putByte(85 + client.outgoingCipher.getNextValue());
        buffer.putByteA(coordY);
        buffer.putByteA(coordX);
        client.oWritePosition += buffer.offset - position;
    }
    
    /**
     * Sends a new ground item to the client.
     * @param client The client to send the ground item to.
     * @param groundItem The ground item to send.
     * @param spoof Option to spoof the packet with the next isaac value.
     */
    public static void sendGroundItem(Client client, GroundItem groundItem, boolean spoof) {
        ByteBuffer buffer = new ByteBuffer(client.outgoingBuffer);
        int position = client.oWritePosition;
        buffer.offset = position;
        buffer.putByte(215 + (spoof ? client.outgoingCipher.getNextValue() : 0));
        buffer.putWord128(groundItem.id);
        buffer.putByteB((groundItem.coordX - ((groundItem.coordX >> 3) << 3)) << 4 | 
                         groundItem.coordY - ((groundItem.coordY >> 3) << 3));
        buffer.putWord128(-1);
        buffer.putWord(groundItem.amount);
        client.oWritePosition += buffer.offset - position;
    }
    
    /**
     * Sends for a ground item to be removed.
     * @param client The client to remove the ground item from.
     * @param groundItem The ground item to remove.
     * @param spoof Option to spoof the packet with the next isaac value.
     */
    public static void sendRemoveGroundItem(Client client, GroundItem groundItem, boolean spoof) {
        ByteBuffer buffer = new ByteBuffer(client.outgoingBuffer);
        int position = client.oWritePosition;
        buffer.offset = position;
        buffer.putByte(156 + (spoof ? client.outgoingCipher.getNextValue() : 0));
        buffer.putByte128((groundItem.coordX - ((groundItem.coordX >> 3) << 3)) << 4 | 
                           groundItem.coordY - ((groundItem.coordY >> 3) << 3));
        buffer.putWord(groundItem.id);
        client.oWritePosition += buffer.offset - position;
    }
    
    /**
     * Sends the coordinates of the map chunk to reset.
     * @param client The client to send the coordinates to.
     * @param coordX The map coordinate x.
     * @param coordY The map coordinate y.
     */
    public static void resetMapChunk(Client client, int coordX, int coordY) {
        ByteBuffer buffer = new ByteBuffer(client.outgoingBuffer);
        int position = client.oWritePosition;
        buffer.offset = position;
        buffer.putByte(64 + client.outgoingCipher.getNextValue());
        buffer.putByteA(coordX);
        buffer.putByteB(coordY);
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
        for(int chunkX = (client.coordX >> 3) - 2; chunkX <= (client.coordX >> 3) + 2; chunkX++) {
            for(int chunkY = (client.coordY >> 3) - 2; chunkY <= (client.coordY >> 3) + 2; chunkY++) {
                Region region = null;
                if(Main.regions != null && Main.regions[chunkX >> 3] != null && (region = Main.regions[chunkX >> 3][chunkY >> 3]) != null) {
                    Chunk chunk = null;
                    if(region.chunks != null && region.chunks[chunkX - ((chunkX >> 3) << 3)] != null && (chunk = region.chunks[chunkX - ((chunkX >> 3) << 3)][chunkY - ((chunkY >> 3) << 3)]) != null) {
                        ListNode node = chunk.activePlayers;
                        while((node = node.childNode) != null) {
                            if(!(node instanceof Entity) || client.listedPlayers > Client.PLAYER_UPDATES)
                                break;
                            if(!(node instanceof Client))
                                continue;
                            Client pClient = (Client) node;
                            int position = pClient.localId.value;
                            int dCx = (pClient.coordX >> 3) - (client.coordX >> 3);
                            int dCy = (pClient.coordY >> 3) - (client.coordY >> 3);
                            if(position == client.localId.value || (client.playerIndex[position >> 3] & (1 << (position & 7))) != 0 || 
                               dCx >= 2  || dCy >= 2 || 
                               dCx <= -2 || dCy <= -2)
                                continue;
                            ListNode idNode = new IntegerNode(position);
                            idNode.parentNode = client.addedPlayers.parentNode;
                            idNode.childNode = client.addedPlayers;
                            idNode.parentNode.childNode = idNode;
                            idNode.childNode.parentNode = idNode;
                            client.playerIndex[position >> 3] |= 1 << (position & 7);
                        }
                    }
                }
            }
        }
    }
    
    /**
     * Populates the added list for item that are around the client.
     * @param client The client to populate its list for.
     */
    public static void populateItems(Client client) {
        if(client.updatedChunkX < 0 && client.updatedChunkY < 0)
            return;
        for(int chunkX = client.updatedChunkX; chunkX <= client.updatedChunkX + 12; chunkX++) {
            for(int chunkY = client.updatedChunkY; chunkY <= client.updatedChunkY + 12; chunkY++) {
                boolean sendCoords = true;
                Region region = null;
                if(Main.regions != null && Main.regions[chunkX >> 3] != null && (region = Main.regions[chunkX >> 3][chunkY >> 3]) != null) {
                    Chunk chunk = null;
                    if(region.chunks != null && region.chunks[chunkX - ((chunkX >> 3) << 3)] != null && (chunk = region.chunks[chunkX - ((chunkX >> 3) << 3)][chunkY - ((chunkY >> 3) << 3)]) != null) {
                        ListNode node = chunk.activeItems;
                        while((node = node.childNode) != null) {
                            if(!(node instanceof Entity))
                                break;
                            if(!(node instanceof GroundItem))
                                continue;
                            GroundItem groundItem = (GroundItem) node;
                            if(groundItem.destroyTime < System.currentTimeMillis()) {
                                groundItem.destroy();
                                continue;
                            }
                            int position = groundItem.localId;
                            int dCx = (groundItem.coordX >> 3) - (client.coordX >> 3);
                            int dCy = (groundItem.coordY >> 3) - (client.coordY >> 3);
                            if(groundItem.appearTime > System.currentTimeMillis() && groundItem.clientId != client.localId.value || (client.itemIndex[position >> 3] & (1 << (position & 7))) != 0 || 
                               dCx >= 6  || dCy >= 6 || dCx <= -6 || dCy <= -6 || groundItem.coordZ != client.coordZ)
                                continue;
                            if(sendCoords) {
                                System.out.println("Mx: " + ((chunkX - client.updatedChunkX) << 3) + "My: " + ((chunkY - client.updatedChunkY) << 3));
                                sendMapCoords(client, (chunkX - client.updatedChunkX) << 3, (chunkY - client.updatedChunkY) << 3);
                                sendCoords = false;
                            }
                            sendGroundItem(client, groundItem, true);
                            ListNode idNode = new IntegerNode(groundItem.currentUpdate << 16 | position);
                            idNode.parentNode = client.activeItems.parentNode;
                            idNode.childNode = client.activeItems;
                            idNode.parentNode.childNode = idNode;
                            idNode.childNode.parentNode = idNode;
                            client.itemIndex[position >> 3] |= 1 << (position & 7);
                        }
                    }
                }
            }
        }
    }
    
    /**
     * Processes the items currently in the clients active item list.
     * @param client The client to process the items for.
     */
    public static void processItems(Client client) {
        ListNode node = client.activeItems;
        while((node = node.childNode) != null) {
            if(!(node instanceof IntegerNode))
                break;
            GroundItem groundItem = Main.groundItems[((IntegerNode) node).value & 0xFFFF];
            int dCx = 0;
            int dCy = 0;
            if(groundItem != null) {
                dCx = ((groundItem.coordX >> 3) - client.updatedChunkX) << 3;
                dCy = ((groundItem.coordY >> 3) - client.updatedChunkY) << 3;
            }
            boolean remove = groundItem == null || groundItem.coordZ != client.coordZ ||  groundItem.remove || dCx > 104  || dCy > 104 || dCx < 0 || dCy < 0;
            if(remove) {
                node.removeFromList();
                if(groundItem != null && groundItem.remove) {
                    Client.sendMapCoords(client, dCx, dCy);
                    Client.sendRemoveGroundItem(client, groundItem, true);
                }
                client.itemIndex[(((IntegerNode) node).value & 0xFFFF) >> 3] &= ~(1 << (((IntegerNode) node).value & 7));
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