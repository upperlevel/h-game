import {Phase, Phases} from "./phase";
import {HGame} from "../index";

export class LoginPhase implements Phase {
    overlay: HTMLDivElement;

    constructor() {
        this.overlay = document.getElementById("login-overlay") as HTMLDivElement;

        const button = document.getElementById("login-button") as HTMLButtonElement;
        button.onclick = () => {
            const username = document.getElementById("login-username") as HTMLInputElement;
            HGame.instance.socket!.send(JSON.stringify({
                type: "login",
                name: username.value
            }))
        }
    }

    show() {
        this.overlay.style.display = "block";

        HGame.instance.socket!.addEventListener("message", (event) => {
            if (event.data == "ok") {
                HGame.instance.phaseManager.show(Phases.LOBBY)
            }
        })
    }

    dismiss() {
        this.overlay.style.display = "none";
    }
}
