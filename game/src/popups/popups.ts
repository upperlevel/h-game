import {DamagePopup} from "./damagePopup";
import {ComicPopup} from "./comicPopup";
import {PopupSpawnPacket} from "../protocol";
import {Popup} from "./popup";
import {World} from "../world/world";

export namespace Popups {
    const TYPES = {
      "damage": DamagePopup,
      "comic": ComicPopup,
    };

    export function fromPacket(world: World, packet: PopupSpawnPacket): Popup {
        // @ts-ignore
        const type = TYPES[packet.popupType];
        if (type == null) throw "Cannot find type: " + packet.type;
        return type.fromPacket(world, packet);
    }
}