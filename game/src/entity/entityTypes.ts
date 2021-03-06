import {EntityType} from "./entityType";
import {SantyType} from "./entities/santy";
import {PoisonType} from "./entities/poison";
// @ts-ignore
import * as planck from "planck-js";
import {MestoliType} from "./entities/mestoli";
import {MikrotikType} from "./entities/mikrotik";
import {JavaType} from "./entities/java";
import {BusType} from "./entities/bus";

export namespace EntityTypes {
    const entities = new Map<string, EntityType>();

    function register(type: EntityType) {
        entities.set(type.id, type);
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
    export const MESTOLI = new MestoliType();
    export const POISON = new PoisonType();
    export const MIKROTIK = new MikrotikType();
    export const JAVA = new JavaType();
    export const BUS = new BusType();


    export const defaultCharacter = SANTY;
    export const playableCharacters: EntityType[] = [SANTY, MESTOLI, JAVA];

    register(SANTY);
    register(MESTOLI);
    register(POISON);
    register(MIKROTIK);
    register(JAVA);
    register(BUS);
}
