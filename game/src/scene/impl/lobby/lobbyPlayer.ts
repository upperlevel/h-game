import {Player} from "../../../entity/player";
import {World} from "../../../world/world";
// @ts-ignore
import {EntityType} from "../../../entity/entityType";
import {Text} from "../../../world/text";


export class LobbyPlayer extends Player {
    private _ready = false;

    nameTag: Text;
    readyTag: Text;

    constructor(world: World, active: boolean, type: EntityType) {
        super(world, Player.createBody(world), active, type);

        this.nameTag = world.createText({
            text: "",
            x: 0,
            y: 0,
            centered: true,
            height: 0.25,
            style: {
                fontFamily: "pixeled",
                fill: 0xffffff
            }
        });

        this.readyTag = world.createText({
            text: "",
            x: 0,
            y: 0,
            centered: true,
            height: 0.18,
            style: {
                fontFamily: "pixeled",
            }
        });

        this.ready = false;
    }

    get ready(): boolean {
        return this._ready;
    }

    set ready(ready: boolean) {
        this._ready = ready;

        if (ready) {
            this.readyTag.text = "I'm ready!";
            this.readyTag.style = {fill: 0x00ff00};
        } else {
            this.readyTag.text = "Not ready";
            this.readyTag.style = {fill: 0xff0000};
        }

    }

    onUpdate(delta: number) {
        super.onUpdate(delta);

        this.nameTag.x = this.x;
        this.nameTag.y = this.y + this.height + this.readyTag.height;

        this.readyTag.x = this.x;
        this.readyTag.y = this.y + this.height;

        this.nameTag.text = this.name;
    }
}
