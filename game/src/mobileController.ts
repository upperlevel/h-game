import Container = PIXI.Container;
import Sprite = PIXI.Sprite;
import InteractionEvent = PIXI.interaction.InteractionEvent;
import {Key} from "./input/key";
import {ATTACK, JUMP, LEFT, RIGHT, SPECIAL_ATTACK} from "./input/actions";

export class MobileController {
    static SHEET_PATH = "assets/mobile_control/controls.json";
    stage = new Container();

    leftBtn: Sprite;
    rightBtn: Sprite;
    jumpBtn: Sprite;
    attackBtn: Sprite;
    specialAttackBtn: Sprite;


    constructor(parent: Container) {
        let sheet = PIXI.loader.resources[MobileController.SHEET_PATH].spritesheet!;
        this.leftBtn = new Sprite(sheet.textures["left"]);
        this.rightBtn = new Sprite(sheet.textures["right"]);
        this.jumpBtn = new Sprite(sheet.textures["jump"]);
        this.attackBtn = new Sprite(sheet.textures["attack"]);
        this.specialAttackBtn = new Sprite(sheet.textures["special_attack"]);

        this.stage = new Container();

        for (let btn of [this.leftBtn, this.rightBtn, this.jumpBtn, this.attackBtn, this.specialAttackBtn]) {
            btn.interactive = true;
            btn.buttonMode = true;
            btn.scale.set(13, 13);
            this.stage.addChild(btn);
        }

        this.onResize();

        parent.addChild(this.stage);
    }

    onResize() {
        const screenMargin = 5;
        const margin = 5;
        const height = window.innerHeight;
        const width = window.innerWidth;

        this.leftBtn.position.set(screenMargin, height - screenMargin - this.leftBtn.height);
        this.bindKey(this.leftBtn, LEFT);
        this.rightBtn.position.set(this.leftBtn.x + this.leftBtn.width + margin, this.leftBtn.y);
        this.bindKey(this.rightBtn, RIGHT);

        this.jumpBtn.position.set(width - screenMargin - this.jumpBtn.width, height - screenMargin - this.jumpBtn.height);
        this.bindKey(this.jumpBtn, JUMP);
        this.attackBtn.position.set(this.jumpBtn.x - margin - this.attackBtn.width, this.jumpBtn.y);
        this.bindKey(this.attackBtn, ATTACK);
        this.specialAttackBtn.position.set(this.jumpBtn.x, this.jumpBtn.y - margin - this.specialAttackBtn.height);
        this.bindKey(this.specialAttackBtn, SPECIAL_ATTACK);
    }

    bindKey(sprite: Sprite, key: Key) {
        let pressers = 0;
        // Where the hell is mouseover???

        sprite.on("pointermove", (e: InteractionEvent) => {
            const touchMask = 1 << e.data.identifier;
            if ((pressers & touchMask) == 0) {
                if (sprite.containsPoint(e.data.global)) {
                    pressers |= touchMask;
                    key.forceDown();
                }
            } else {
                if (!sprite.containsPoint(e.data.global)) {
                    pressers &= ~touchMask;
                    key.forceUp();
                }
            }
        });
        sprite.on("pointerdown", (e: InteractionEvent) => {
            pressers |= 1 << e.data.identifier;
            key.forceDown();
        });
        sprite.on("pointerup", (e: InteractionEvent) => {
            pressers &= ~(1 << e.data.identifier);
            key.forceUp();
        });
    }


    static isEnabled(): boolean {
        return PIXI.utils.isMobile.any;
    }

    static getAssets(): string[] {
        if (!this.isEnabled()) return [];
        return  [this.SHEET_PATH];
    }
}


