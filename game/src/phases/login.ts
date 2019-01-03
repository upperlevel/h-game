import {Phase, Phases} from "./phase";
import {HGame} from "../index";
import {LoginPacket, OperationResultPacket} from "@common/matchmaking/protocol";

export class LoginPhase implements Phase {
    overlay: HTMLDivElement;
    feedback: HTMLDivElement;

    constructor() {
        this.overlay = document.getElementById("login-overlay") as HTMLDivElement;
        this.feedback = document.getElementById("login-feedback") as HTMLDivElement;

        const button = document.getElementById("login-button") as HTMLButtonElement;
        button.onclick = () => {
            const username = document.getElementById("login-username") as HTMLInputElement;
            const packet = {
                type: "login",
                name: username.value
            } as LoginPacket;

            HGame.instance.socket!.send(JSON.stringify(packet));
        }
    }

    show() {
        this.overlay.style.display = "block";

        HGame.instance.socket!.addEventListener("message", (event) => {
            const packet = JSON.parse(event.data) as OperationResultPacket;

            if (packet.error) {
                this.feedback.style.color = "red";
                this.feedback.innerText = packet.error;
            } else {
                this.feedback.style.color = "green";
                this.feedback.innerText = "Username accepted";

                HGame.instance.phaseManager.show(Phases.LOBBY)
            }
        })
    }

    dismiss() {
        this.overlay.style.display = "none";
    }
}
