import {EntityType} from "./entityType";
import {SantyType} from "./entities/santy";

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
        const assets = [];
        for (const entity of entities.values()) {
            assets.push(entity.asset);
        }
        return assets;
    }

    export const SANTY = new SantyType();

    register(SANTY);
}
