import * as ws from 'ws';
import * as express from "express";

export class GameRelay {
  connectionHandler(ws: ws, req: express.Request): void {
    ws.on("message", msg => {
      ws.send(msg)
    });
    console.log(req)
  }
}
