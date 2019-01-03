import {Phase, Phases} from "./phase";
import {LoginPacket} from "@common/matchmaking/protocol";

import {hgame} from "../index";

export class LoginPhase extends Phase {
    name ="login";

    overlay: HTMLDivElement;
    feedback: HTMLDivElement;

    constructor() {
        super();

        this.overlay = document.getElementById("login-overlay") as HTMLDivElement;
        this.feedback = document.getElementById("login-feedback") as HTMLDivElement;

        const button = document.getElementById("login-button") as HTMLButtonElement;
        button.onclick = () => {
            const username = document.getElementById("login-username") as HTMLInputElement;
            const packet = {
                type: "login",
                name: username.value
            } as LoginPacket;

            hgame.socket!.send(JSON.stringify(packet));
            console.log("Login packet sent: " + JSON.stringify(packet));
        }
    }

    onShow() {
        this.overlay.style.display = "block";
    }

    onMessage(packet: any) {
        if (packet.error) {
            this.feedback.style.color = "red";
            this.feedback.innerText = packet.error;
        } else {
            this.feedback.style.color = "green";
            this.feedback.innerText = "Username accepted";

            hgame.phaseManager.show(Phases.LOBBY)
        }
    }

    onDismiss() {
        this.overlay.style.display = "none";
    }
}
