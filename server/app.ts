import * as createError from "http-errors";
import * as express from "express";
import * as enableWs from "express-ws";
import * as path from "path";
import * as logger from "morgan";

import * as indexRouter from "./routes";
const matchmaker = require("./routes/matchmaking");
const gameRelay = require("./routes/gamerelay");

const app = express();

enableWs(app);

// view engine setup

const rootDir = path.join(__dirname, "..");

app.use(logger('dev'));
app.use(express.json());
app.use(express.urlencoded({ extended: false }));
app.use(express.static(path.join(rootDir, 'public')));

app.use("/", indexRouter);
app.use("/api/matchmaking", matchmaker.connectionHandler);
app.use("/api/game", gameRelay.connectionHandler);

// catch 404 and forward to error handler
app.use(function(req, res, next) {
  next(createError(404));
});

const PORT = 8080;
app.listen(PORT, () => {
  console.log("Listening on port " + PORT)
});
