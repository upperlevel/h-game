import {Entity} from "./entity";
import {createPlayerBehaviour} from "../behaviour/behaviours";
import {BehaviourManager} from "../behaviour/behaviour";
import {EntityResetPacket, PlayerEntitySpawnMeta} from "../protocol";
import {World} from "../world";
import {EntityType} from "./entityType";

export abstract class Player extends Entity {
    static SPRITE_SIZE = 48;

    static WIDTH  = 2;
    static HEIGHT = 2;
    static STEP_HEIGHT = (2.0 / Player.SPRITE_SIZE) * Player.HEIGHT;

    static COLLISION_CATEGORY = 0x2;

    maxEnergy = 1.0;
    energy = 0.0;
    specialAttackEnergy = 0.8;
    energyGainPerMs = 0.05 / 1000;

    attackPower = 10;
    jumpForce = 20.0;

    name = "Ulisse";

    // override
    damageable = true;
    private behaviour: BehaviourManager;

    //private hudRenderer: HudRenderer;

    get friction(): number {
        return this.body.getFixtureList()!.getFriction()
    }

    set friction(v: number) {
        this.body.getFixtureList()!.setFriction(v);
    }


    protected constructor(world: World, body: planck.Body, active: boolean, type: EntityType) {
        super(world, body, active, type);
        this.behaviour = createPlayerBehaviour(this);
        //this.hudRenderer = new HudRenderer(scene, this.name, this.active ? "lime" : "red");

        let conf = scene.config!;
        let x = (sceneWidth / (conf.playerCount + 1)) * (conf.playerIndex + 1);
        let y = 800;
        this.body.setPosition(planck.Vec2(x, y));

        let sensorW = Player.WIDTH / 2;
        let sensorH = 0.1;
        this.addSensor(body.createFixture({
            shape: planck.Edge(
                planck.Vec2(-sensorW / 2, -sensorH / 2),
                planck.Vec2(sensorW / 2, sensorH / 2)
            ),
        }));
    }

    onUpdate(deltatime: number) {
        super.onUpdate(deltatime);

        this.behaviour.update();

        if (this.active && this.scene.actions.JUMP.isDown && this.isTouchingGround) {
            this.jump();
        }
        this.energy = Math.min(this.energy + this.energyGainPerMs * deltatime, this.maxEnergy);

        //this.hudRenderer.update(this.body, this.life / this.maxLife, this.energy / this.maxEnergy);
    }

    reloadName() {
        //this.hudRenderer.setName(this.name);
    }

    createSpawnMeta(): PlayerEntitySpawnMeta {
        return {
            type: "player",
            name: this.name,
        };
    }

    loadSpawnMeta(meta: PlayerEntitySpawnMeta) {
        if (meta.type != "player") {
            console.log("Error: invalid player meta type");
            return;
        }
        this.name = meta.name;
        this.reloadName();
    }

    jump() {
        this.body.applyLinearImpulse(planck.Vec2(0, -this.jumpForce), this.body.getWorldCenter(), true);
        if (this.active) {
            this.world.sendPacket({
                type: "player_jump",
                entityId: this.id,
            })
        }
    }

    respawn() {
        super.respawn();
        this.energy = 0;
    }

    giveCloseAttackDamage() {
        for (const entity of this.world.entityRegistry.entities.values()) {
            // @ts-ignore
            if (entity.damageable && this.scene.physics.world.collide(this.sprite, entity.sprite)) {
                const distance = this.x - entity.x;
                if (distance == 0 || distance < 0 != this.isFacingLeft) {
                    entity.damage(this.attackPower);
                }
            }
        }
    }

    idle() {
        this.type.animations["idle"](this.sprite, this.type.spritesheet!);
        this.sprite.play();
    }

    attack(onComplete: () => void) {
        this.type.animations["attack"](this.sprite, this.type.spritesheet!);
        this.sprite.play();
        this.sprite.onComplete = onComplete;
    }

    canSpecialAttack(): boolean {
        return this.energy >= this.specialAttackEnergy;
    }

    specialAttack(onComplete: () => void) {
        this.type.animations["specialAttack"](this.sprite, this.type.spritesheet!);
        this.sprite.play();
        this.sprite.onComplete = onComplete;
    }

    fillResetPacket(packet: EntityResetPacket) {
        super.fillResetPacket(packet);
        packet.energy = this.energy;
    }

    onReset(packet: EntityResetPacket) {
        super.onReset(packet);
        this.energy = packet.energy;
    }

    static createBody(world: World) {
        //let sprite = scene.physics.add.sprite(x, 800, "santy").setDisplaySize(Player.WIDTH, Player.HEIGHT);;
        let body = world.physics.createBody({
            type: "dynamic",
            fixedRotation: true,
        });

        const width = 2;
        const height = 2;

        body.createFixture({
            shape: planck.Edge(
                planck.Vec2(-width / 2, 0),
                planck.Vec2(width / 2, height),
            ),
            density: 1,
            // Collide with everything BUT players (or anything that is the same category as the player)
            filterCategoryBits: Player.COLLISION_CATEGORY,
            filterMaskBits: ~Player.COLLISION_CATEGORY,
        });
        //sprite.setFlipX(conf.playerIndex % 2 != 0);
        return body;
    }
}
