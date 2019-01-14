import {OverlayScene} from "../../overlayScene";
import {LobbyScene} from "./lobbyScene";

export class RequestsOverlay extends OverlayScene {
    private lobby: LobbyScene;

    constructor(lobby: LobbyScene) {
        super("requests-overlay");

        this.lobby = lobby;
    }

    addRequest(player: string) {
        console.log(`Creating request mailbox for: ${player}`);

        const request = document.createElement("div");

        const title = document.createElement("h6");
        title.innerText = "Invite request from " + player;
        request.appendChild(title);

        const buttonsBar = document.createElement("div");

        const accept = document.createElement("button") as HTMLButtonElement;
        accept.innerText = "Accept";
        accept.className = "green-button";
        accept.onclick = () => {
            this.lobby.acceptInvite(player);
            this.container.removeChild(request);
        };
        buttonsBar.appendChild(accept);

        const decline = document.createElement("button") as HTMLButtonElement;
        decline.innerText = "Decline";
        decline.className = "red-button";
        decline.onclick = () => {
            this.container.removeChild(request);
        };
        buttonsBar.appendChild(decline);

        request.appendChild(buttonsBar);

        this.container.appendChild(request);
    }

    onMessage(packet: any) {
        if (packet.type == "invite" && packet.kind == "INVITE_RECEIVED") {
            this.addRequest(packet.player);
        }
    }

    onShow() {
        this.lobby.game.matchmakingConnector.subscribe("message",  this.onMessage, this);
    }

    onHide() {
        this.lobby.game.matchmakingConnector.unsubscribe("message", this.onMessage.bind(this), this);
    }
}
