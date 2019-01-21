import {Player} from "../../../entity/player";
import {World} from "../../../world/world";

// @ts-ignore
import * as planck from "planck-js"
import {EntityType} from "../../../entity/entityType";
import {Text} from "../../../world/text";


export class LobbyPlayer extends Player {
    nametag: Text;

    constructor(world: World, active: boolean, type: EntityType) {
        super(world, Player.createBody(world), active, type);

        this.nametag = world.createText({
            text: "Hello world :)",

            x: this.x,
            y: this.y + this.height,

            centered: true,
            height: 0.5,

            style: {
                fontFamily: "pixeled",
                fontSize: 16,
                fill: 0xffffff
            }
        });
        this.sync();
    }

    private sync() {
        this.nametag.x = this.x;
        this.nametag.y = this.y + this.height;
    }

    onUpdate(delta: number) {
        super.onUpdate(delta);
        this.sync();
    }
}
