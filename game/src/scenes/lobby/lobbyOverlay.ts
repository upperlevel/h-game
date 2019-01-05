import {Overlay} from "../overlay";
import {InviteOverlay} from "./inviteOverlay";

import {RequestsOverlay} from "./requestsOverlay";
import {LobbyScene} from "./lobbyScene";

export class LobbyOverlay extends Overlay {
    scene: LobbyScene;

    readyButton: HTMLButtonElement;
    inviteButton: HTMLButtonElement;

    inviteOverlay: InviteOverlay;
    requestsOverlay: RequestsOverlay;

    constructor(scene: LobbyScene) {
        super("lobby-overlay");

        this.scene = scene;

        this.readyButton = document.getElementById("lobby-ready-button") as HTMLButtonElement;
        this.readyButton.onclick = () => {
            const notReady = this.readyButton.className == "red-button";

            if (notReady) {
                this.readyButton.className = "green-button";
            } else {
                this.readyButton.className = "red-button";
            }

            this.scene.game.send({
                type: "lobby_update",
                character: "santy",
                ready: notReady
            });
        };

        this.inviteButton = document.getElementById("lobby-invite-button") as HTMLButtonElement;
        this.inviteButton.onclick = () => {
            this.inviteOverlay.show();
        };

        this.inviteOverlay = new InviteOverlay(scene);
        this.requestsOverlay = new RequestsOverlay(scene);
    }

    onShow() {
        this.requestsOverlay.show();
    }

    onHide() {
        this.requestsOverlay.hide();
        this.inviteOverlay.hide();
    }
}
