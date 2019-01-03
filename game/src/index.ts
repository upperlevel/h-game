import {PhaseManager, Phases} from "./ui/phase";
import {Graphics} from "./game/graphics";

class HGame {
    phaseManager: PhaseManager = new PhaseManager();
    graphics: Graphics = new Graphics();

    socket: WebSocket | undefined  = undefined;

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

    start() {
        this.phaseManager.show(Phases.CONNECTING);
    }

    dismiss() {
    }
}

export const hgame = new HGame();
window.addEventListener("load", () => hgame.start());
