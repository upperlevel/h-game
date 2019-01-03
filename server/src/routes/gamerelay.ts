import * as ws from 'ws';
import * as express from "express";
import {Player, PlayerRegistry} from "../player";

class ConnectionHandler {
  playerRegistry: PlayerRegistry;
  player?: Player;
  socket: ws;

  constructor(playerRegistry: PlayerRegistry, socket: ws) {
    this.playerRegistry = playerRegistry;
    this.socket = socket;
    socket.on("message", this.onMessage.bind(this));
    socket.on("close", this.onDisconnect.bind(this));
  }

  onMessage(msg: string) {
    if (this.player == null) {
      let player = this.playerRegistry.getByName(msg);
      if (player != null && player.relaySocket == null) {
        this.player = player;
        this.player.relaySocket = this.socket;

        this.socket.send("ok");
      } else {
        this.socket.send("error: invalid token")
      }
      return
    }

    if (this.player.lobby == null) return;

    let other: Player;
    for (other of this.player.lobby.players) {
      if (other != this.player && other.relaySocket != null) {
        other.relaySocket.send(msg)
      }
    }
  }

  onDisconnect() {
    if (this.player != null) {
      this.player.relaySocket = undefined
    }
  }
}

export class GameRelay {
  playerRegistry: PlayerRegistry;

  constructor(playerRegistry: PlayerRegistry) {
    this.playerRegistry = playerRegistry;
  }

  connectionHandler(ws: ws, req: express.Request): void {
    new ConnectionHandler(this.playerRegistry, ws);
  }
}
