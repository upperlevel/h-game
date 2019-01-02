import {Phase, Phases} from "../phases/phase";
import {HGame} from "../index";

export class ConnectingPhase implements Phase {
    overlay: HTMLDivElement;
    label: HTMLDivElement;

    labelTimer: number = -1;

    constructor() {
        this.overlay = document.getElementById("connecting-overlay") as HTMLDivElement;
        this.label = document.getElementById("connecting-label") as HTMLDivElement;
    }

    show() {
        this.overlay.style.display = "block";

        const socket = new WebSocket("ws://localhost:80");

        socket.addEventListener("open", () => {
            HGame.instance.phaseManager.show(Phases.LOGIN);
        });

        socket.addEventListener("close", () => {
            HGame.instance.phaseManager.show(Phases.NO_CONNECTION);
        });

        HGame.instance.socket = socket;

        let timer = 0;
        this.labelTimer = setInterval(() => {
            this.label.innerText = "Connecting" + "...".substring(2 - timer % 3);
            timer++;

        }, 500)
    }

    dismiss() {
        clearInterval(this.labelTimer);
        this.overlay.style.display = "none";
    }
}
