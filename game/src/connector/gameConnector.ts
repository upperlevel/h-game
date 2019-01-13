import {Connector} from "./connector";

/**
 * The GameConnector is used during the game phase.
 *
 * It receives message from the game relay.
 * During the handshake, messages are bare strings, after packets are JSON.
 */
export class GameConnector extends Connector {
    token: string;
    handshakeDone: boolean = false;

    constructor(token: string) {
        super("/api/game");
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
                this.handshakeDone = true;

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

    deserialize(message: string): any {
        if (this.handshakeDone) {
            try {
                return JSON.parse(message);
            } catch (e) {
                throw `Error parsing json: '${message}'`;
            }
        } else {
            return message;
        }
    }

    serialize(message: any): string {
        if (this.handshakeDone) {
            return JSON.stringify(message);
        } else {
            return message;
        }
    }
}
