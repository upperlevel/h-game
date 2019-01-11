import {SceneWrapper} from "../sceneWrapper";

import {LoginOverlay} from "./loginOverlay";

export class LoginScene extends SceneWrapper {
    overlay: LoginOverlay;

    constructor() {
        super({key: "login"});

        this.overlay = new LoginOverlay(this);
    }

    login(username: string) {
        this.game.playerName = username;
        this.game.matchmakingConnector.send({
            type: "login",
            name: username
        });
    }

    onCreate() {
        this.game.matchmakingConnector.subscribe("message", this.overlay.onResult, this.overlay);
        this.overlay.show();
    }

    onShutdown() {
        this.game.matchmakingConnector.unsubscribe("message", this.overlay.onResult, this.overlay);
        this.overlay.hide();
    }
}
