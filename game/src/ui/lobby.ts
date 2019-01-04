import {Phase} from "./phase";

import {hgame} from "../index";

class InviteOverlay {
    inviteOverlay: HTMLDivElement;

    username: HTMLInputElement;
    sendButton: HTMLButtonElement;
    cancelButton: HTMLButtonElement;
    feedback: HTMLElement;

    shown: boolean = false;

    constructor() {
        this.inviteOverlay = document.getElementById("invite-overlay") as HTMLDivElement;

        this.feedback = document.getElementById("invite-feedback") as HTMLElement;

        this.username = document.getElementById("invited-player") as HTMLInputElement;
        this.username.onchange = () => {
            this.sendButton.disabled = this.username.value == "";
        };

        this.sendButton = document.getElementById("invite-send-button") as HTMLButtonElement;
        this.sendButton.onclick = () => {
            this.sendButton.disabled = true;

            hgame.socket!.send(JSON.stringify({
                type: "invite",
                kind: "INVITE_PLAYER",
                player: this.username.value
            }));
        };

        this.cancelButton = document.getElementById("invite-cancel-button") as HTMLButtonElement;
        this.cancelButton.onclick = () => {
            this.hide();
        }
    }

    reset() {
        this.username.value = "";
        this.sendButton.disabled = false;
        this.feedback.innerText = "";
    }

    show() {
        this.reset();
        this.inviteOverlay.style.display = "block";

        this.shown = true;
    }

    onMessage(packet: any) {
        if (packet.type == "result") {
            const error = packet.error;
            this.feedback.innerText = error ? error : "Invite sent";
            this.feedback.style.color = error ? "red" : "white";

            this.sendButton.disabled = false;
        }
    }

    hide() {
        this.shown = false;

        this.inviteOverlay.style.display = "none";
    }
}


export class LobbyPhase extends Phase {
    name = "lobby";

    overlay: HTMLDivElement;
    inviteOverlay: InviteOverlay;

    private initReadyButton() {
        const readyButton = document.getElementById("ready-btn") as HTMLButtonElement;
        readyButton.onclick = () => {
            const notReady = readyButton.className == "red-button";

            if (notReady) {
                readyButton.className = "green-button";
            } else {
                readyButton.className = "red-button";
            }

            hgame.socket!.send(JSON.stringify({
                type: "lobby_update",
                character: "santy",
                ready: notReady
            }));

            console.log(`Ready status: ${notReady}`);
        };
    }

    private initInviteButton() {
        const button = document.getElementById("invite-button") as HTMLButtonElement;
        button.onclick = () => {
            this.inviteOverlay.show();
        };
    }

    constructor() {
        super();

        this.inviteOverlay = new InviteOverlay();

        this.overlay = document.getElementById("lobby-overlay") as HTMLDivElement;
        this.initReadyButton();
        this.initInviteButton();
    }

    onShow() {
        this.overlay.style.display = "block";
    };

    onMessage(packet: any) {
        if (this.inviteOverlay.shown) {
            this.inviteOverlay.onMessage(packet);
        }
    }

    onDismiss() {
        this.inviteOverlay.hide();
        this.overlay.style.display = "none"
    }
}
