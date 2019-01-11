import {Overlay} from "../overlay";
import {LobbyScene} from "./lobbyScene";

export class InviteOverlay extends Overlay {
    lobby: LobbyScene;

    username: HTMLInputElement;
    sendButton: HTMLButtonElement;
    cancelButton: HTMLButtonElement;
    feedback: HTMLElement;

    constructor(lobby: LobbyScene) {
        super("invite-overlay");

        this.lobby = lobby;

        this.feedback = document.getElementById("invite-feedback") as HTMLElement;

        this.username = document.getElementById("invited-player") as HTMLInputElement;
        this.username.onchange = () => {
            this.sendButton.disabled = this.username.value == "";
            this.feedback.innerText = "";
        };

        this.sendButton = document.getElementById("invite-send-button") as HTMLButtonElement;
        this.sendButton.onclick = () => {
            this.lobby.invite(this.username.value);
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

            this.sendButton.disabled = false;
        }
    }

    onShow() {
        this.reset();

        this.lobby.game.matchmakingConnector.subscribe("message", this.onMessage, this);
    }

    onHide() {
        this.lobby.game.matchmakingConnector.unsubscribe("message", this.onMessage, this);
    }
}
