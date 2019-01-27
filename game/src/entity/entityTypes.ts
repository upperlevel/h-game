import {EntityType} from "./entityType";
import {SantyType} from "./entities/santy";
import {PoisonType} from "./entities/poison";
// @ts-ignore
import * as planck from "planck-js";
import {MixterType} from "./entities/mixter";
import {MikrotikType} from "./entities/mikrotik";

export namespace EntityTypes {
    const entities = new Map<string, EntityType>();

    function register(type: EntityType) {
        entities.set(type.id, type);
    }

    export function onLoad() {
        for (const entity of entities.values()) {
            entity.onLoad();
        }
    }

    export function get(id: string): EntityType | undefined {
        return entities.get(id);
    }

    export function getAssets(): string[] {
        let assets: string[] = [];
        for (const entity of entities.values()) {
            assets = assets.concat(entity.assets);
        }
        return assets;
    }

    export const SANTY = new SantyType();
    export const MIXTER = new MixterType();
    export const POISON = new PoisonType();
    export const MIKROTIK = new MikrotikType();

    register(SANTY);
    register(MIXTER);
    register(POISON);
    register(MIKROTIK);
}
