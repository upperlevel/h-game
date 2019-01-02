const DEFAULT_PORT = 23432;

interface ThrowableEntitySpawnMeta {
    type: "throwable";
    throwerEntityId: number;
}

interface PlayerEntitySpawnMeta {
    type: "player";
    name: string;
}

// TODO: the spawn meta should be easily defined by the entityType
type EntitySpawnMeta = ThrowableEntitySpawnMeta | PlayerEntitySpawnMeta;

interface EntitySpawnPacket {
    type: "entity_spawn";
    entityType: string;
    entityId: number;
    x: number;
    y: number;
    isFacingLeft: boolean;
    meta: EntitySpawnMeta | null;
}

interface EntityImpulsePacket {
    type: "entity_impulse";
    entityId: number;
    powerX: number;
    powerY: number;
    pointX: number;
    pointY: number;
}

interface EntityResetPacket {
    type: "entity_reset";
    entityId: number;
    // Additional info
    [key: string]: any;
}

interface BehaviourChangePacket {
    type: "behaviour_change";
    actorId: number;
    layerIndex: number;
    behaviour: string | null;
}

interface PlayerJumpPacket {
    type: "player_jump";
    entityId: number;
}

type GamePacket = EntitySpawnPacket | EntityImpulsePacket | EntityResetPacket | BehaviourChangePacket | PlayerJumpPacket;


