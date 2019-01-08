import {Overlay} from "../overlay";
import {LoginScene} from "./loginScene";

export class LoginOverlay extends Overlay {
    scene: LoginScene;

    submit: HTMLButtonElement;
    feedback: HTMLDivElement;

    constructor(scene: LoginScene) {
        super("login-overlay");

        this.scene = scene;

        this.submit = document.getElementById("login-button") as HTMLButtonElement;
        this.submit.onclick = () => {
            const username = document.getElementById("login-username") as HTMLInputElement;
            this.scene.login(username.value);
        };

        this.feedback = document.getElementById("login-feedback") as HTMLDivElement;
    }

    onResult(packet: any) {
        if (packet.type == "result") {
            if (packet.error) {
                this.feedback.style.color = "red";
                this.feedback.innerText = packet.error;
            } else {
                this.feedback.style.color = "green";
                this.feedback.innerText = "Username accepted";

                this.scene.scene.start("lobby");
            }
        }
    }

    onShow() {
    }

    onHide() {
    }
}
