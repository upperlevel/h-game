import {Overlay} from "../overlay";
import {InviteOverlay} from "./inviteOverlay";

import {hgame} from "../../index";

export class LobbyOverlay extends Overlay {
    inviteOverlay: InviteOverlay;

    readyButton: HTMLButtonElement;
    inviteButton: HTMLButtonElement;

    constructor() {
        super("lobby-overlay");

        this.inviteOverlay = new InviteOverlay();

        this.readyButton = document.getElementById("lobby-ready-button") as HTMLButtonElement;
        this.readyButton.onclick = () => {
            const notReady = this.readyButton.className == "red-button";

            if (notReady) {
                this.readyButton.className = "green-button";
            } else {
                this.readyButton.className = "red-button";
            }

            hgame.socket!.send(JSON.stringify({
                type: "lobby_update",
                character: "santy",
                ready: notReady
            }));
        };

        this.inviteButton = document.getElementById("lobby-invite-button") as HTMLButtonElement;
        this.inviteButton.onclick = () => {
            this.inviteOverlay.show();
        };
    }

    onHide() {
        this.inviteOverlay.hide();
    }
}
