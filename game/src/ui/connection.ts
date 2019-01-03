import {Phase, Phases} from "./phase";
import {hgame} from "../index";

export class ConnectingPhase extends Phase {
    name = "connecting";

    overlay: HTMLDivElement;
    label: HTMLDivElement;

    labelTimer: number = -1;

    constructor() {
        super();

        this.overlay = document.getElementById("connecting-overlay") as HTMLDivElement;
        this.label = document.getElementById("connecting-label") as HTMLDivElement;
    }

    onShow() {
        this.overlay.style.display = "block";

        hgame.reconnect();

        let timer = 0;
        this.labelTimer = setInterval(() => {
            this.label.innerText = "Connecting" + "...".substring(2 - timer % 3);
            timer++;

        }, 500)
    }

    onMessage(packet: any) {
    }

    onDismiss() {
        clearInterval(this.labelTimer);
        this.overlay.style.display = "none";
    }
}

export class NoConnectionPhase extends Phase {
    name = "no_connection";

    overlay: HTMLDivElement;
    retryButton: HTMLButtonElement;

    constructor() {
        super();

        this.overlay = document.getElementById("no-connection-overlay") as HTMLDivElement;

        this.retryButton = document.getElementById("connection-retry-btn") as HTMLButtonElement;
        this.retryButton.onclick = () => {
            hgame.phaseManager.show(Phases.CONNECTING)
        }
    }

    onShow() {
        this.overlay.style.display = "block";
    }

    onMessage(packet: any) {
    }

    onDismiss() {
        this.overlay.style.display = "none";
    }
}
