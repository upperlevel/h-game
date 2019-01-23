import {Player} from "../../../entity/player";
import {World} from "../../../world/world";
// @ts-ignore
import {EntityType} from "../../../entity/entityType";
import {Text} from "../../../world/text";


export class LobbyPlayer extends Player {
    private _ready = false;
    private _me = false;
    private _leader = false;

    youTag: Text;
    leaderTag: Text;
    nameTag: Text;
    readyTag: Text;

    constructor(world: World, active: boolean, type: EntityType) {
        super(world, Player.createBody(world), active, type);

        this.youTag = world.createText({
            text: "",
            x: 0,
            y: 0,
            isCentered: true,
            height: 0.3,
            style: {
                fontFamily: "pixeled",
                fill: 0x0000ff,
            }
        });

        this.leaderTag = world.createText({
            text: "",
            x: 0,
            y: 0,
            isCentered: true,
            height: 0.125,
            style: {
                fontFamily: "pixeled",
                fill: 0xffff00,
            }
        });

        this.nameTag = world.createText({
            text: "",
            x: 0,
            y: 0,
            isCentered: true,
            height: 0.3,
            style: {
                fontFamily: "pixeled",
                fill: 0xffffff,
            }
        });

        this.readyTag = world.createText({
            text: "",
            x: 0,
            y: 0,
            isCentered: true,
            height: 0.125,
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

    set me(me: boolean) {
        this._me = me;
        this.youTag.text = me ? "YOU" : "";
    }

    set leader(leader: boolean) {
        this._leader = leader;
        this.leaderTag.text = leader ? "(Leader)" : "";
    }

    onUpdate(delta: number) {
        super.onUpdate(delta);

        this.readyTag.x = this.x;
        this.readyTag.y = this.y + this.height;

        this.nameTag.text = this.name; // TODO change text only when name changes
        this.nameTag.x = this.x;
        this.nameTag.y = this.readyTag.y + this.readyTag.height;

        this.leaderTag.x = this.x;
        this.leaderTag.y = this.nameTag.y + this.nameTag.height;

        this.youTag.x = this.x;
        this.youTag.y = this.leaderTag.y + this.leaderTag.height;
    }

    remove() {
        super.remove();

        this.youTag.remove();
        this.leaderTag.remove();
        this.nameTag.remove();
        this.readyTag.remove();
    }
}
