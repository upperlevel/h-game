// @ts-ignore
import * as planck from "planck-js"

import {EntityRegistry} from "./entity/entityRegistry";
import {Entity} from "./entity/entity";
import {GamePacket} from "./protocol";
import {Player} from "./entity/player";

export class World {
    app: PIXI.Application;

    width = 32;
    height = 18;

    entityRegistry = new EntityRegistry(this);

    physics = new planck.World({
        gravity: planck.Vec2(0, -10)
    });
    physicsAccumulator = 0;

    socket?: WebSocket;

    debugRender = true;
    debugGraphics = new PIXI.Graphics();

    constructor(app: PIXI.Application) {
        this.app = app;
    }

    createPlatform(x: number, y: number, width: number, height: number, texture: PIXI.Texture) {
        const platform = new PIXI.extras.TilingSprite(texture, width * texture.width, height * texture.height);
        platform.scale.x = 1 / texture.width;
        platform.scale.y = 1 / texture.height;

        platform.x = x;
        platform.y = this.height - y - height;

        const body = this.physics.createBody();
        body.createFixture(
            planck.Box(width / 2, height / 2, planck.Vec2(0, 0), 0)
        );
        body.setPosition(planck.Vec2(x, y));

        this.app.stage.addChild(platform);

        // TODO spawn physic body

        return platform;
    }

    setup() {
        this.physics.on("begin-contact", this.onContactBegin.bind(this));
        this.physics.on("end-contact", this.onContactEnd.bind(this));

        this.resize();

        const terrain = PIXI.loader.resources["assets/game/urban_terrain.png"].texture;
        this.createPlatform(0, 0, this.width, 1, terrain);
        //this.createPlatform(0, 3, 9, 1, terrain);
        this.createPlatform(this.width - 5, 12, 5, 1, terrain);


        if (this.debugRender) {
            this.app.stage.addChild(this.debugGraphics)
        }
    }

    resize() {
        const stage = this.app.stage;
        stage.scale.x = stage.scale.y = window.innerWidth / this.width;
        stage.y = window.innerHeight - this.height * stage.scale.y;
    }

    update(delta: number) {
        this.doPhysicsStep(delta);

        if (this.debugRender) {
            this.updateDebugRender();
        }

        this.entityRegistry.onUpdate(delta);
    }

    updateDebugRender() {
        const g = this.debugGraphics;
        g.clear();
        g.beginFill(0xffffff);
        for (let body = this.physics.getBodyList(); body; body = body.getNext()) {
            const pos = body.getPosition();
            for (let fixture = body.getFixtureList(); fixture; fixture = fixture.getNext()) {
                let type = fixture.getType();
                let shape: planck.Shape = fixture.getShape();

                if (type == "polygon") {
                    const s = shape as planck.PolygonShape;
                    const verts = s.m_vertices;

                    g.beginFill(0xffffff);
                    let points = verts.map((v) => new PIXI.Point(v.x + pos.x, this.height - (v.y + pos.y)));
                    //console.log(points);
                    g.drawPolygon(points);
                    g.endFill();
                } else console.warn(type);
            }
        }
    }

    doPhysicsStep(deltaTime: number) {
        const TIME_STEP = 1 / 60;
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

        //TODO: entityRegistry.clearDestroyedBodies()
    }

    spawn(entity: Entity) {
        this.entityRegistry.spawn(entity);
    }

    despawn(entity: Entity) {
        this.entityRegistry.despawn(entity);
    }

    sendPacket(packet: GamePacket) {
        if (this.socket == null) return;
        this.socket.send(JSON.stringify(packet));
    }


    private onContactBegin(contact: planck.Contact) {
        let dataA: any = contact.getFixtureA().getUserData();
        let dataB: any = contact.getFixtureB().getUserData();

        if (dataA != null && "onTouchBegin" in dataA) {
            dataA.onTouchBegin(contact.getFixtureB(), contact);
        }

        if (dataB != null && "onTouchBegin" in dataB) {
            dataB.onTouchBegin(contact.getFixtureA(), contact);
        }
    }

    private onContactEnd(contact: planck.Contact) {
        let dataA: any = contact.getFixtureA().getUserData();
        let dataB: any = contact.getFixtureB().getUserData();

        if ("onTouchEnd" in dataA) {
            dataA.onTouchBegin(contact.getFixtureB(), contact);
        }

        if ("onTouchEnd" in dataB) {
            dataB.onTouchBegin(contact.getFixtureA(), contact);
        }
    }

    destroy() {
    }
}
