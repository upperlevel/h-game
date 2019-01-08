import {Connector} from "./connector";
import {HGame} from "../index";

export class GameConnector extends Connector {
    token: string;

    constructor(token: string) {
        super("ws://localhost:8080/api/game");
        this.token = token;
    }

    protected onHandshakeAccept(message: string) {
        if (message.startsWith("error")) {
            this.disconnect();
            return;
        }

        switch (message) {
            case "ok":
                console.log("Token accepted.");
                break;
            case "ready":
                this.events.emit("connect");
                this.events.removeListener("message", this.onHandshakeAccept, this, false);
                break;
        }
    }

    onConnect() {
        if (this.token == null) {
            throw "Player name wasn't set! Have you login?";
        }

        this.events.on("message", this.onHandshakeAccept, this);

        this.send(this.token);
    }
}
