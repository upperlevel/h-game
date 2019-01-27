import {Player} from "../../entity/player/player";
import {Text} from "../../world/text";
import {PlayerHud} from "../../entity/player/playerHud";


export class LobbyPlayerHud implements PlayerHud {
    private _ready = false;
    private _me = false;
    private _leader = false;

    youTag: Text;
    leaderTag: Text;
    nameTag: Text;
    readyTag: Text;

    constructor(player: Player) {
        const world = player.world;

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

    update(player: Player) {

        this.readyTag.x = player.x;
        this.readyTag.y = player.y + player.height;

        this.nameTag.text = player.name; // TODO change text only when name changes
        this.nameTag.x = player.x;
        this.nameTag.y = this.readyTag.y + this.readyTag.height;

        this.leaderTag.x = player.x;
        this.leaderTag.y = this.nameTag.y + this.nameTag.height;

        this.youTag.x = player.x;
        this.youTag.y = this.leaderTag.y + this.leaderTag.height;
    }

    onDespawn() {
        this.youTag.remove();
        this.leaderTag.remove();
        this.nameTag.remove();
        this.readyTag.remove();
    }
}
