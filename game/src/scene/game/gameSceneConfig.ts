import {LobbyPlayer} from "../impl/lobby/lobbyScene";

export interface GameSceneConfig {
    playerIndex: number,
    playerCount: number,
    playerName: string,
    player: LobbyPlayer,
}