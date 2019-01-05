import {Overlay} from "../overlay";

import {hgame} from "../../index";

export class InviteOverlay extends Overlay {
    username: HTMLInputElement;
    sendButton: HTMLButtonElement;
    cancelButton: HTMLButtonElement;
    feedback: HTMLElement;

    shown: boolean = false;

    constructor() {
        super("invite-overlay");

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

    onShow() {
        this.reset();
        this.shown = true;
    }

    // TODO
    onMessage(packet: any) {
        if (packet.type == "result") {
            const error = packet.error;
            this.feedback.innerText = error ? error : "Invite sent";
            this.feedback.style.color = error ? "red" : "white";

            this.sendButton.disabled = false;
        }
    }

    onHide() {
        this.shown = false;
    }
}
