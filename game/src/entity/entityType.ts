import {Entity} from "./entity";

import AnimatedSprite = PIXI.extras.AnimatedSprite;
import Spritesheet = PIXI.Spritesheet;
import {World} from "../world";

export interface EntityType {
    readonly id: string;

    readonly spritesheetPath: string;
    spritesheet?: Spritesheet;

    readonly animations: { [key: string]: ((sprite: AnimatedSprite, spritesheet: Spritesheet) => void) };

    create(game: World, active: boolean): Entity;
}
