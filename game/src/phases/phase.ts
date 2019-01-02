import {LoginPhase} from "./login"
import {LobbyPhase} from "./lobby"
import {GamePhase} from "./game"

export namespace Phases {
    export const LOGIN: Phase = new LoginPhase();
    export const LOBBY: Phase = new LobbyPhase();
    export const GAME:  Phase = new GamePhase();
}

export interface Phase {
    show(): void;

    dismiss(): void;
}

export namespace PhaseManager {
    let current: Phase = null;

    export function show(phase: Phase) {
        if (current) {
            current.dismiss();
        }
        current = phase;
        if (current) {
            current.show();
        }
    }
}

