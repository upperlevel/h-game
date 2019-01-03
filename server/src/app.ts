import * as createError from "http-errors";
import * as express from "express";
import * as enableWs from "express-ws";
import * as path from "path";
import * as logger from "morgan";

import {MatchMaker} from "./routes/matchmaking";
import {GameRelay} from "./routes/gamerelay";
import {PlayerRegistry} from "./player";
import {LobbyRegistry} from "./lobby";

const app = enableWs(express()).app;

const rootDir = path.join(__dirname, "../..");

const playerRegistry = new PlayerRegistry();
const lobbyRegistry = new LobbyRegistry();

const matchMaker = new MatchMaker(playerRegistry, lobbyRegistry);
const gameRelay = new GameRelay(playerRegistry);

app.use(logger('dev'));
app.use(express.json());
app.use(express.urlencoded({ extended: false }));

const gameDir = path.join(rootDir, "../../game/dist");
console.log("Game directory at: " + gameDir);
app.use("/", express.static(gameDir));

app.ws("/api/matchmaking", matchMaker.connectionHandler.bind(matchMaker));
app.ws("/api/game", gameRelay.connectionHandler.bind(gameRelay));
app.ws("/api/echo", (ws) => {
  ws.on("message", (mex) => { ws.send(mex); })
});

// catch 404 and forward to error handler
app.use(function(req, res, next) {
  next(createError(404));
});

// Error handling
app.use((err: Error, req: express.Request, res: express.Response) => {
  console.error(err.stack);
  res.status(500).send('Something broke!');
});

const PORT = 8080;
app.listen(PORT, () => {
  console.log("Listening on port " + PORT)
});
