// @ts-ignore
import * as planck from "planck-js"

import {EntityRegistry} from "../entity/entityRegistry";
import {Entity} from "../entity/entity";
import {GamePacket} from "../protocol";

import {Terrain} from "./terrain";
import {Text} from "./text";

import {Connector} from "../connector/connector";
import {Emitter} from "./emitter";
import {Popup} from "../popups/popup";
import {Player} from "../entity/player/player";
import {DamagePopup} from "../popups/damagePopup";
import {ComicPopup, ComicPopupConfig} from "../popups/comicPopup";
import {Popups} from "../popups/popups";

export class World {
    static TIME_STEP = 1 / 60;

    app: PIXI.Application;

    private terrain: Terrain.Terrain;

    entityRegistry = new EntityRegistry(this);

    physics = new planck.World({
        gravity: planck.Vec2(0, -10),
        allowSleep: false,
    });
    physicsAccumulator = 0;

    socket?: Connector;

    debugRender = false;
    debugGraphics = new PIXI.Graphics();

    emitters: Emitter[] = [];
    popups: Popup[] = [];

    syncEvents: (() => void)[] = [];

    private destroyedBodies: Body[] = [];

    constructor(app: PIXI.Application, terrain: Terrain.Terrain) {
        this.app = app;

        this.terrain = terrain;
    }

    get width() {
        return this.terrain.width;
    }

    get height() {
        return this.terrain.height;
    }

    get entities() {
        return this.entityRegistry.entities;
    }

    createText(data: Terrain.Text) {
        return new Text(this, data);
    }

    createDamagePopup(data: Terrain.Text): DamagePopup {
        const popup = new DamagePopup(this, data);
        this.popups.push(popup);
        return popup;
    }

    createComicPopup(data: ComicPopupConfig): ComicPopup {
        const popup = new ComicPopup(this, data);
        this.popups.push(popup);
        return popup;
    }

    createPlatform(platform: Terrain.Platform) {
        const texture = PIXI.loader.resources[platform.texture].texture;

        const sprite = new PIXI.extras.TilingSprite(texture, platform.width * texture.width, platform.height * texture.height);
        this.app.stage.addChild(sprite);

        sprite.scale.x = 1 / texture.width;
        sprite.scale.y = 1 / texture.height;

        sprite.x = platform.x;
        sprite.y = this.height - platform.y - platform.height;

        const body = this.physics.createBody();
        body.createFixture({
            shape: planck.Box(platform.width / 2, platform.height / 2, planck.Vec2(platform.width / 2, platform.height / 2), 0),
            userData: "ground",
        });
        body.setPosition(planck.Vec2(platform.x, platform.y));

        // TODO spawn physic body

        return platform;
    }

    createEmitter(data: Terrain.Emitter) {
        const emitter = new Emitter(this, data);
        this.emitters.push(emitter);
        return emitter;
    }

    createDecoration(data: Terrain.Decoration) {
        const texture = PIXI.loader.resources[data.texture].texture;

        const decoration = new PIXI.Sprite(texture);
        this.app.stage.addChild(decoration);

        decoration.scale.x = data.width / texture.width;
        decoration.scale.y = data.height / texture.height;

        decoration.x = data.x;
        decoration.y = this.height - data.y - data.height;

        return decoration;
    }

    setup() {
        this.app.stage.removeChildren();

        this.physics.on("begin-contact", this.onContactBegin.bind(this));
        this.physics.on("end-contact", this.onContactEnd.bind(this));

        this.entityRegistry.onEnable();

        this.resize();

        if (this.terrain.platforms) {
            for (const platform of this.terrain.platforms) {
                this.createPlatform(platform)
            }
        }

        if (this.terrain.texts) {
            for (const text of this.terrain.texts) {
                this.createText(text);
            }
        }

        if (this.terrain.emitters) {
            for (const emitter of this.terrain.emitters) {
                this.createEmitter(emitter);
            }
        }

        if (this.terrain.decorations) {
            for (const decoration of this.terrain.decorations) {
                this.createDecoration(decoration);
            }
        }

        if (this.debugRender) {
            this.app.stage.addChild(this.debugGraphics);
        }

        if (this.socket) {
            this.socket!.subscribe("message", this.onPacket, this, false)
        }
    }

    resize() {
        const stage = this.app.stage;
        stage.scale.x = stage.scale.y = window.innerWidth / this.width;
        stage.y = window.innerHeight - this.height * stage.scale.y;
    }

    update(delta: number) {
        if (this.syncEvents) {
            let syncEvents = this.syncEvents;
            this.syncEvents = [];
            for (const e of syncEvents) {
                e();
            }
        }

        this.doPhysicsStep(delta);

        this.entityRegistry.onUpdate(delta);

        if (this.debugRender) {
            this.updateDebugRender();
        }

        this.entityRegistry.onUpdate(delta);

        for (const emitter of this.emitters) {
            emitter.update(delta);
        }

        for (let i = this.popups.length - 1; i >= 0; i--) {
            if (this.popups[i].update(delta)) {
                this.popups.splice(i, 1);
            }
        }
    }

    updateDebugRender() {
        const debug = this.debugGraphics;
        debug.clear();

        for (let body = this.physics.getBodyList(); body; body = body.getNext()) {
            const pos = body.getPosition();
            for (let fixture = body.getFixtureList(); fixture; fixture = fixture.getNext()) {
                let type = fixture.getType();
                let shape: planck.Shape = fixture.getShape();

                if (type == "polygon") {
                    const s = shape as planck.PolygonShape;
                    const verts: Array<planck.Vec2> = s.m_vertices;

                    debug.lineStyle(1 / 48, 0x0000ff);
                    let points = verts.map((v: any) => new PIXI.Point(v.x + pos.x, this.height - (v.y + pos.y)));
                    points.push(points[0]);
                    //console.log(points);
                    debug.drawPolygon(points);
                } else console.warn(type);
            }
        }

        debug.lineStyle(1 / 48, 0xffff00);
        for (const child of this.app.stage.children as PIXI.Sprite[]) {
            if (child.x && child.y && child.width && child.height && child.anchor) {

                const x = child.x - child.anchor.x * child.width;
                const y = child.y - child.anchor.y * child.height;

                debug.drawPolygon([
                    new PIXI.Point(x, y),
                    new PIXI.Point(x + child.width, y),
                    new PIXI.Point(x + child.width, y + child.height),
                    new PIXI.Point(x, y + child.height),
                    new PIXI.Point(x, y),
                ]);
            }
        }
    }

    doPhysicsStep(deltaTime: number) {
        const TIME_STEP = World.TIME_STEP;
        const VELOCITY_ITERATIONS = 6;
        const POSITION_ITERATIONS = 2;
        // fixed time step

        // TODO: we should find a way to avoid spiral of death without limiting the time frame (yeah, networking)
        // max frame time to avoid spiral of death (on slow devices) DISABLED
        // val frameTime = Math.min(deltaTime, 0.25f) // Remove comment to enable

        this.physicsAccumulator += deltaTime;
        while (this.physicsAccumulator >= TIME_STEP) {
            this.entityRegistry.onPrePhysicsStep(TIME_STEP);

            this.physics.step(TIME_STEP, VELOCITY_ITERATIONS, POSITION_ITERATIONS);
            this.physicsAccumulator -= TIME_STEP
        }

        for (const body of this.destroyedBodies) {
            this.physics.destroyBody(body);
        }
        this.destroyedBodies = [];
    }

    spawn(entity: Entity) {
        this.entityRegistry.spawn(entity);
    }

    despawn(entity: Entity) {
        this.entityRegistry.despawn(entity);
    }

    removeBody(body: planck.Body) {
        this.destroyedBodies.push(body);
    }

    sendPacket(packet: GamePacket) {
        if (this.socket == null) return;
        this.socket.send(packet);
    }

    callSync(cb: () => void) {
        this.syncEvents.push(cb);
    }

    onPacket(packet: GamePacket) {
        switch (packet.type) {
            case "entity_spawn":
                this.entityRegistry.onSpawn(packet);
                break;
            case "behaviour_change":
                this.entityRegistry.onBehaviourChange(packet);
                break;
            case "player_jump":
                let p = this.entityRegistry.getEntity(packet.entityId);
                if (p != null && 'jump' in p) {
                    // @ts-ignore
                    p.jump();
                }
                break;
            case "spawn_popup":
                this.callSync(() => (this.popups.push(Popups.fromPacket(this, packet))));
                break;
            case "entity_reset":
                this.entityRegistry.onResetPacket(packet);
                break;
            case "entity_impulse":
                this.entityRegistry.getEntity(packet.entityId)!.applyImpulse(packet.powerX, packet.powerY, packet.pointX, packet.pointY);
                break;
            default:
                // @ts-ignore
                console.error(`Unhandled packet type: ${packet.type}`, packet);
                break;
        }
    }

    private onContactBegin(contact: planck.Contact) {
        let dataA: any = contact.getFixtureA().getUserData();
        let dataB: any = contact.getFixtureB().getUserData();

        if (dataA && typeof dataA.onTouchBegin === "function") {
            dataA.onTouchBegin(contact.getFixtureB(), contact);
        }

        if (dataB && typeof dataB.onTouchBegin === "function") {
            dataB.onTouchBegin(contact.getFixtureA(), contact);
        }
    }

    private onContactEnd(contact: planck.Contact) {
        let dataA: any = contact.getFixtureA().getUserData();
        let dataB: any = contact.getFixtureB().getUserData();

        if (dataA && typeof dataA.onTouchEnd === "function") {
            dataA.onTouchEnd(contact.getFixtureB(), contact);
        }

        if (dataB && typeof dataB.onTouchEnd === "function") {
            dataB.onTouchEnd(contact.getFixtureA(), contact);
        }
    }

    destroy() {
        this.entityRegistry.onDisable();
        this.app.stage.removeChildren();
    }

    getSpawnLocation() {
        return {
            x: Math.random() * this.width,
            y: this.height / 2,
        };
    }
}
