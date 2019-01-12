import {LobbyPlayer} from "../lobby/lobbyScene";

export interface GameSceneConfig {
    playerIndex: number,
    playerCount: number,
    playerName: string,
    player: LobbyPlayer,
}