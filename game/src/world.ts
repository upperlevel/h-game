import {EntityRegistry} from "./entity/entityRegistry";
import {Entity} from "./entity/entity";
import {GamePacket} from "./protocol";

export class World {
    entityRegistry = new EntityRegistry(this);

    physics = new planck.World({
        gravity: planck.Vec2(0, -10)
    });
    physicsAccumulator = 0;

    socket?: WebSocket;

    onEnable() {
        this.physics.on("begin-contact", this.onContactBegin.apply(this))
        this.physics.on("end-contact", this.onContactEnd.apply(this))
    }

    onDisable() {
    }

    onUpdate(deltaTime: number) {
        this.doPhysicsStep(deltaTime);

        this.entityRegistry.onUpdate(deltaTime);
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

        if ("onTouchBegin" in dataA) {
            dataA.onTouchBegin(contact.getFixtureB(), contact);
        }

        if ("onTouchBegin" in dataB) {
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

}
