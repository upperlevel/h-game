import * as ws from 'ws';
import * as express from "express";

class MatchMaker {
  connectionHandler(ws: ws, req: express.Request): void {
    ws.on("message", msg => {
      ws.send(msg)
    });
    console.log(req)
  }
}

export = new MatchMaker()
