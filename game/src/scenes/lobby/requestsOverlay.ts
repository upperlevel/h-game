import {Overlay} from "../overlay";
import {LobbyScene} from "./lobbyScene";

export class RequestsOverlay extends Overlay {
    private scene: LobbyScene;

    constructor(scene: LobbyScene) {
        super("requests-overlay");

        this.scene = scene;
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
            this.scene.game.send({
                type: "invite",
                kind: "ACCEPT_INVITE",
                player: player
            });
            console.log(`You accepted the invite of: ${player}`)
        };
        buttonsBar.appendChild(accept);

        const decline = document.createElement("button") as HTMLButtonElement;
        decline.innerText = "Decline";
        decline.className = "red-button";
        decline.onclick = () => {
            this.container.removeChild(request);
            console.log(`You declined the invite of: ${player}`);
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
        this.scene.game.events.on("message",  this.onMessage, this);
    }

    onHide() {
        this.scene.game.events.removeListener("message", this.onMessage.bind(this), this, false);
    }
}
