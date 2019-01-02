/**
 *
 * the "matchmaking" protocol sends in the first line the packet type and in the second line the packet json-serialized
 * every client request is followed by an OperationResultPacket stating the error (or null if there wasn't any).
 * The first packet should be a LoginPacket (and it receives the OperationResultPacket too)
 */

// -------- RESPONSES --------

export interface LoginPacket {
    type: "login";
    name: string;
}

export interface PlayerLobbyInfoChangePacket {
    type: "lobby_update";
    character: string;
    ready: boolean;
}

// TODO: lobby discovery

// -------- RESPONSES --------

/**
 * The token is needed by the new connection to start a relay
 */
export interface MatchBeginPacket {
    type: "match_begin";
    token: number;
    playerIndex: number;
}

export interface LobbyPlayerInfo {
    name: string;
    character: string | null;
    ready: boolean;
}

/**
 * Sends all the info about the current lobby
 * The admin is encoded as the index of the previous list
 */
export interface CurrentLobbyInfoPacket {
    type: "lobby_info";
    players: Array<LobbyPlayerInfo>;
    admin: number;
}

export interface OperationResultPacket {
    type: "result";
    error: string | null;
}

// -------- REQUEST/RESPONSES --------

export type InviteType =
    /**
     * Client -> Server
     * Invite player with that name (a response will be sent after this).
     */
    "INVITE_PLAYER" |
    /**
     * Server -> Client
     * You have received an invite from "player".
     */
    "INVITE_RECEIVED" |
    /**
     * Client -> Server
     * I (client) accept the invite from "player".
     * Note that there's no packet for invite rejection
     */
    "ACCEPT_INVITE";

export interface InvitePacket {
    type: "invite";
    kind: InviteType;
    player: string;
}

export type MatchmakingPacket =
    LoginPacket
    | PlayerLobbyInfoChangePacket
    | MatchBeginPacket
    | CurrentLobbyInfoPacket
    | OperationResultPacket;


