import {Phases} from "./ui/phases";
import {PhaseManager} from "./ui/phase";
import {Graphics} from "./game/graphics";

class HGame {
    phaseManager: PhaseManager = new PhaseManager();
    graphics?: Graphics = undefined;

    socket?: WebSocket;

    reconnect() {
        if (this.socket) {
            this.socket.onclose = null;
            this.socket!.close();
        }

        this.socket = new WebSocket("ws://localhost:8080/api/matchmaking");
        this.socket.onopen = () => {
            this.phaseManager.show(Phases.LOGIN);
        };
        this.socket.onclose = () => {
            this.phaseManager.show(Phases.NO_CONNECTION);
        };
    }

    onLoad() {
        this.graphics = new Graphics();
        this.phaseManager.show(Phases.CONNECTING);
    }

    onDismiss() {
    }
}

export const hgame = new HGame();
window.addEventListener("load", () => hgame.onLoad());
