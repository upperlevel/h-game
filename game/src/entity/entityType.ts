import {Entity} from "./entity";
import {GameScene} from "../scene/game/gameScene";

import AnimatedSprite = PIXI.extras.AnimatedSprite;
import Spritesheet = PIXI.Spritesheet;

export interface EntityType {
    readonly id: string;

    readonly spritesheetPath: string;
    spritesheet?: Spritesheet;

    readonly animations: { [key: string]: ((sprite: AnimatedSprite, spritesheet: Spritesheet) => void) };

    create(game: GameScene, active: boolean): Entity;
}
