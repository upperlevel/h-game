import {SceneWrapper} from "./sceneWrapper";

import {Overlay} from "./overlay";

import {hgame} from "../index";

class LoginOverlay extends Overlay {
    scene: LoginScene;

    submit: HTMLButtonElement;
    feedback: HTMLDivElement;

    constructor(scene: LoginScene) {
        super("login-overlay");

        this.scene = scene;

        this.submit = document.getElementById("login-button") as HTMLButtonElement;
        this.submit.onclick = () => {
            const username = document.getElementById("login-username") as HTMLInputElement;
            const packet = {
                type: "login",
                name: username.value
            };

            hgame.socket!.send(JSON.stringify(packet));
            console.log("Login packet sent: " + JSON.stringify(packet));
        };

        this.feedback = document.getElementById("login-feedback") as HTMLDivElement;
    }

    onResponse(error: string | null) {
        if (error) {
            this.feedback.style.color = "red";
            this.feedback.innerText = error;
        } else {
            this.feedback.style.color = "green";
            this.feedback.innerText = "Username accepted";

            this.scene.changeScene("lobby");
        }
    }
}

export class LoginScene extends SceneWrapper {
    overlay?: LoginOverlay;

    constructor() {
        super("login");
    }

    onPreload() {
        this.overlay = new LoginOverlay(this);
    }

    onCreate() {
        this.overlay!.show();
        hgame.setJsonChannel(packet => this.overlay!.onResponse(packet.error));
    }

    onUpdate() {
    }

    onShutdown() {
        hgame.dropJsonChannel();
        this.overlay!.hide();
    }
}
