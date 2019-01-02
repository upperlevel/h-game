import {Phase} from "./phase";

export class LoginPhase implements Phase {
    overlay: HTMLDivElement;

    show() {
        this.overlay = document.getElementById("login-overlay") as HTMLDivElement;
        this.overlay.style.display = "block";

        const button = document.getElementById("login-button") as HTMLButtonElement;
        button.onclick = function () {
            const username = document.getElementById("login-username") as HTMLInputElement;
            console.log("You chose the username: " + username)
        }
    }

    dismiss() {
        this.overlay.style.display = "none";
    }
}
