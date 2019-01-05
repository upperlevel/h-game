import {SceneWrapper} from "./sceneWrapper";

import {Overlay} from "./overlay";

import {hgame} from "../index";
import {OperationResultPacket} from "@common/matchmaking/protocol";

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

            hgame.events.once("message", (packet: OperationResultPacket) => {
                if (packet.error) {
                    this.feedback.style.color = "red";
                    this.feedback.innerText = packet.error;
                } else {
                    this.feedback.style.color = "green";
                    this.feedback.innerText = "Username accepted";

                    this.scene.changeScene("lobby");
                }
            });

            hgame.socket!.send(JSON.stringify(packet));
            console.log("Login packet sent: " + JSON.stringify(packet));
        };

        this.feedback = document.getElementById("login-feedback") as HTMLDivElement;
    }
}

export class LoginScene extends SceneWrapper {
    overlay: LoginOverlay;

    constructor() {
        super("login");

        this.overlay = new LoginOverlay(this);
    }

    onPreload() {
    }

    onCreate() {
        this.overlay.show();
    }

    onUpdate() {
    }

    onShutdown() {
        this.overlay.hide();
    }
}
