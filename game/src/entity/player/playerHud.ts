import {Player} from "./player";

export interface PlayerHud {
    update(pl: Player): void;

    onDespawn(pl: Player): void;
}

