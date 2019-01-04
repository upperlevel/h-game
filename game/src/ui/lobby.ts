import {Phase} from "./phase";

import {hgame} from "../index";


export class LobbyPhase extends Phase {
    name = "lobby";

    overlay: HTMLDivElement;

    constructor() {
        super();

        this.overlay = document.getElementById("lobby-overlay") as HTMLDivElement;

        const readyButton = document.getElementById("ready-btn") as HTMLButtonElement;
        readyButton.onclick = () => {
            const ready = readyButton.style.backgroundColor == "red";

            if (ready) {
                readyButton.style.backgroundColor = "lime";
            }

            hgame.socket!.send(JSON.stringify({
                type: "lobby_update",
                character: "santy",
                ready: ready
            }));
        };
    }

    onShow() {
        this.overlay.style.display = "block";
    };

    onMessage(packet: any) {
    }

    onDismiss() {
        this.overlay.style.display = "none"
    }
}
