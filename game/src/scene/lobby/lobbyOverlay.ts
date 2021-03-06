import {OverlayScene} from "../overlayScene";
import {InviteOverlay} from "./inviteOverlay";

import {RequestsOverlay} from "./requestsOverlay";
import {LobbyScene} from "./lobbyScene";

export class LobbyOverlay extends OverlayScene {
    lobby: LobbyScene;

    readyButton: HTMLButtonElement;
    inviteButton: HTMLButtonElement;

    inviteOverlay: InviteOverlay;
    requestsOverlay: RequestsOverlay;

    constructor(lobby: LobbyScene) {
        super("lobby-overlay");

        this.lobby = lobby;

        this.readyButton = document.getElementById("lobby-ready-button") as HTMLButtonElement;
        this.readyButton.onclick = () => {
            const notReady = this.readyButton.className == "red-button";

            if (notReady) {
                this.readyButton.className = "green-button";
            } else {
                this.readyButton.className = "red-button";
            }

            // Note: undefined means "keep the old value"
            this.lobby.updatePlayerInfo(undefined, notReady);
        };

        this.inviteButton = document.getElementById("lobby-invite-button") as HTMLButtonElement;
        this.inviteButton.onclick = () => {
            this.inviteOverlay.enable();
        };

        this.inviteOverlay = new InviteOverlay(lobby);
        this.requestsOverlay = new RequestsOverlay(lobby);
    }

    onEnable() {
        this.requestsOverlay.enable();
    }

    onDisable() {
        this.requestsOverlay.disable();
        this.inviteOverlay.disable();
    }
}
