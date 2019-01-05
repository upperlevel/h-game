import {Overlay} from "../overlay";

import {hgame} from "../../index";
import {OperationResultPacket} from "@common/matchmaking/protocol";

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
            this.feedback.innerText = "";
        };

        this.sendButton = document.getElementById("invite-send-button") as HTMLButtonElement;
        this.sendButton.onclick = () => {
            hgame.send({
                type: "invite",
                kind: "INVITE_PLAYER",
                player: this.username.value
            });

            this.sendButton.disabled = true;
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

    onMessage(packet: any) {
        if (packet.type == "result") {
            const error = packet.error;
            this.feedback.innerText = error ? error : "Invite sent";
            this.feedback.style.color = error ? "red" : "white";
        }

        this.sendButton.disabled = false;
    }

    onShow() {
        this.reset();
        this.shown = true;

        hgame.events.on("message", this.onMessage, this);
    }

    onHide() {
        hgame.events.removeListener("message", this.onMessage, this, false);

        this.shown = false;
    }
}
