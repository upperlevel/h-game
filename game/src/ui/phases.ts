import {ConnectingPhase, NoConnectionPhase} from "./connection";
import {LoginPhase} from "./login";
import {LobbyPhase} from "./lobby";
import {GamePhase} from "./game";


export namespace Phases {
    export const CONNECTING     = new ConnectingPhase();
    export const NO_CONNECTION  = new NoConnectionPhase();
    export const LOGIN          = new LoginPhase();
    export const LOBBY          = new LobbyPhase();
    export const GAME           = new GamePhase();
}
