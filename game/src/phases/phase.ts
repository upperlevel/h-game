import {ConnectingPhase} from "../connecting/connecting";
import {NoConnectionPhase} from "../connecting/no_connection";
import {LoginPhase} from "./login"
import {LobbyPhase} from "./lobby"
import {GamePhase} from "./game"

export namespace Phases {
    export const CONNECTING: Phase    = new ConnectingPhase();
    export const NO_CONNECTION: Phase = new NoConnectionPhase();

    export const LOGIN:      Phase = new LoginPhase();
    export const LOBBY:      Phase = new LobbyPhase();
    export const GAME:       Phase = new GamePhase();
}

export abstract class Phase {
    abstract show(): void;

    abstract dismiss(): void;
}

export class PhaseManager {
    private current: Phase | undefined = undefined;

    show(phase: Phase) {
        if (this.current) {
            this.current.dismiss();
        }
        this.current = phase;
        if (this.current) {
            this.current.show();
        }
    }
}

