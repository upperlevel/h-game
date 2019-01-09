/**
 * --- Initialization ---
 * This protocol is used when everyone in the lobby is ready.
 * When that happens the server sends a MatchBeginPacket in the matchmaking connession.
 * After this signal the client should start another connession in the game entrypoint (/api/game).
 * The first thing to do is to send a text packet with the client token (that can be found in the MatchBeginPacket).
 * The token is used to identify the player, think of it as a session cookie, in the default implementation it
 * is simply the player name but in a more secure implementation it could be a hash-secure random string (to prevent
 * other connections to enter instead of the real player).
 * The server should respond with a single TextFrame "ok" if the token was found and paired to the current connection
 * any other message are errors regarding the token pairing process.
 * After the "ok" packet the client should wait for the "start" packet (always remember that we are working with pure
 * websocket text frames, not json).
 * The "start" message signals that every client in the lobby has been connected and so it's safe to start sending packets.
 * After the "start" text message any packet received from the connection is relayed to the other players in the lobby,
 * so it begins the game json protocol:
 *
 * --- Game ---
 * Every text frame includes only one packet encoded in json,
 * the packet type is defined by the "type" field (search "discriminant pattern" to find out more).
 * This protocol does not require the server to answer with a response packet like the matchmaking protocol.
 * Every input-induced movement is sent with the BehaviourChangePacket because it induces a change in the behaviour
 * system (read it's description for a more in-depth explanation).
 * The only input-related packet found here is the PlayerJumpPacket (it still hasn't been included in the behaviour
 * system).
 * The spawn system is worth describing, in order to assign unique indexes in a decentralized way
 * every client has an unique clientIndex (assigned by the server in the MatchBeginPacket).
 * The spawn index then is found with the following expression:
 * spawnIndex = localSpawnIndex++ * playerCount + playerIndex
 * in this way every client uses multiples of the playerCount number offsetted by the unique playerIndex number.
 *
 * The eventual position errors generated by network latency are solved using the EntityResetPacket.
 */

export interface ThrowableEntitySpawnMeta {
    type: "throwable";
    throwerEntityId: number;
}

export interface PlayerEntitySpawnMeta {
    type: "player";
    name: string;
}

// TODO: the spawn meta should be easily defined by the entityType
export type EntitySpawnMeta = ThrowableEntitySpawnMeta | PlayerEntitySpawnMeta;

export interface EntitySpawnPacket {
    type: "entity_spawn";
    entityType: string;
    entityId: number;
    x: number;
    y: number;
    isFacingLeft: boolean;
    meta?: EntitySpawnMeta;
}

export interface EntityImpulsePacket {
    type: "entity_impulse";
    entityId: number;
    powerX: number;
    powerY: number;
    pointX: number;
    pointY: number;
}

export interface EntityResetPacket {
    type: "entity_reset";
    entityId: number;

    // Additional info
    [key: string]: any;
}

export interface BehaviourChangePacket {
    type: "behaviour_change";
    actorId: number;
    layerIndex: number;
    behaviour: string;
}

export interface PlayerJumpPacket {
    type: "player_jump";
    entityId: number;
}

export type GamePacket =
    EntitySpawnPacket
    | EntityImpulsePacket
    | EntityResetPacket
    | BehaviourChangePacket
    | PlayerJumpPacket;


