import {Entity, EntityType} from "./entity";
import {createPlayerBehaviour} from "../behaviour/behaviours";
import {BehaviourManager} from "../behaviour/behaviour";
import {GameScene} from "../scenes/game/gameScene";
import {EntitySpawnMeta, PlayerEntitySpawnMeta} from "../protocol";
import {HudRenderer} from "./hudRenderer";
import Sprite = Phaser.Physics.Arcade.Sprite;

export abstract class Player extends Entity {
    maxEnergy = 1.0;
    energy = 0.0;
    energyGainPerMs = 0.05 / 1000;

    attackPower = 0.1;
    jumpForce = 300;

    name = "Ulisse";

    // override
    damageable = true;
    private behaviour: BehaviourManager;

    private hudRenderer: HudRenderer;


    protected constructor(scene: GameScene, sprite: Sprite, active: boolean, type: EntityType) {
        super(scene, sprite, active, type);
        this.behaviour = createPlayerBehaviour(scene, this);
        this.hudRenderer = new HudRenderer(scene, this.name);

        scene.entityPhysicsGroup.add(this.sprite);
        this.sprite.setCollideWorldBounds(true);
    }

    update(deltatime: number) {
        super.update(deltatime);

        this.behaviour.update();

        if (this.active && this.scene.actions.JUMP.isDown && this.body.onFloor()) {
            this.jump();
        }
        this.energy = Math.min(this.energy + this.energyGainPerMs * deltatime, this.maxEnergy);

        this.hudRenderer.update(this.body, this.life, this.energy);
    }

    reloadName() {
        this.hudRenderer.setName(this.name);
    }

    createSpawnMeta(): EntitySpawnMeta | undefined {
        const meta: PlayerEntitySpawnMeta = {
            type: "player",
            name: this.name,
        };
        return meta;
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
        console.log("Jumping");
        this.body.setVelocityY(-this.jumpForce);
        if (this.active) {
            this.scene.sendPacket({
                type: "player_jump",
                entityId: this.id,
            })
        }
    }

    respawn() {
        super.respawn();
        this.energy = 0;
    }

    attack(callBack: any) {
        this.sprite.anims.play(this.type.animations["attack"]);
        this.sprite.once("animationcomplete", callBack);

        for (const entity of this.scene.entityRegistry.entities.values()) {
            // @ts-ignore
            if (this.scene.physics.world.collide(this.sprite, entity.sprite)) {
                const distance = this.x - entity.x;
                if (distance == 0 || distance < 0 != this.isFacingLeft) {

                    entity.damage(this.attackPower);
                }
            }
        }
    }

    specialAttack(callBack: any) {
        this.sprite.anims.play(this.type.animations["special_attack"]);
        this.sprite.once("animationcomplete", callBack);
    }

    idle() {
        this.sprite.anims.play(this.type.animations["idle"])
    }

    onAttacked(player: Player) {
        this.life -= player.attackPower;
        if (this.life < 0) {
            // TODO: die
        }
    }

    renderHud() {

    }
}
