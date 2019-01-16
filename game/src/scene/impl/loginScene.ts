import {OverlayScene} from "../overlayScene";

import {HGame} from "../../index";
import {LobbyScene} from "./lobby/lobbyScene";

export class LoginScene extends OverlayScene {
    game: HGame;

    usernameInput: HTMLInputElement;
    submitButton: HTMLButtonElement;
    feedbackLabel: HTMLDivElement;

    constructor(game: HGame) {
        super("login-overlay");

        this.game = game;

        this.usernameInput = document.getElementById("login-username") as HTMLInputElement;

        this.submitButton = document.getElementById("login-button") as HTMLButtonElement;
        this.submitButton.onclick = this.onLogin.bind(this);

        this.feedbackLabel = document.getElementById("login-feedback") as HTMLDivElement;
    }

    onLogin() {
        const name = this.usernameInput.value;
        this.game.playerName = name;
        this.game.matchmakingConnector.send({
            type: "login",
            name: name
        });
    }

    onResult(packet: any) {
        if (packet.type == "result") {
            if (packet.error) {
                this.feedbackLabel.style.color = "red";
                this.feedbackLabel.innerText = packet.error;
            } else {
                this.feedbackLabel.style.color = "green";
                this.feedbackLabel.innerText = "Username accepted";

                this.game.sceneManager.setScene(new LobbyScene(this.game));
            }
        }
    }

    onEnable() {
        this.game.matchmakingConnector.subscribe("message", this.onResult, this);
    }

    onDisable() {
        this.game.matchmakingConnector.unsubscribe("message", this.onResult, this);
    }
}
